package com.zone24x7.faume.webapp.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.zone24x7.faume.webapp.exception.FaceDataVerificationException;
import com.zone24x7.faume.webapp.pojo.FaceData;
import com.zone24x7.faume.webapp.pojo.VerificationFaceData;
import com.zone24x7.faume.webapp.util.AppConfigStringConstants;
import com.zone24x7.faume.webapp.util.JsonPojoConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.Base64;
import java.util.LinkedList;
import java.util.List;

/**
 * Implementation of the face data verification service to happen via ML backend.
 */
@Component
public class FaceDataVerificationServiceViaMLBackend implements FaceDataVerificationService {
    @Autowired
    private RestTemplateBuilder restTemplateBuilder;

    @Value(AppConfigStringConstants.CONFIG_FACE_DATA_VERIFICATION_URL)
    private String mlBackendUrl;

    /**
     * Method to send face data for verification.
     *
     * @param faceData the face data
     * @param correlationId the correlation id
     * @return the response from the ML backend
     * @throws FaceDataVerificationException if an error occurs when sending face data
     */
    @Override
    public String sendFaceDataForVerification(FaceData faceData, String correlationId) throws FaceDataVerificationException {
        RestTemplate restTemplate = restTemplateBuilder
                .setConnectTimeout(Duration.ofMillis(20000))
                .setReadTimeout(Duration.ofMillis(20000))
                .build();

        List<byte[]> faceDataByteList = faceData.getData();

        List<String> base64DataToSend = new LinkedList<>();

        for (byte[] data : faceDataByteList) {
            base64DataToSend.add(Base64.getEncoder().encodeToString(data));
        }

        VerificationFaceData verificationFaceData = new VerificationFaceData(faceData.getAccountId(), base64DataToSend, faceData.getRoi());

        JsonNode verificationFaceDataAsJson = JsonPojoConverter.toJson(verificationFaceData);

        try {
            return restTemplate.postForObject(mlBackendUrl, verificationFaceDataAsJson, String.class, faceData.getAccountId(), faceData.getProfileCount(), faceData.getPatternId());
        } catch (RestClientException e) {
            throw new FaceDataVerificationException("Rest client error occurred when trying to send face data to ML backend.", e);
        } catch (Exception e) {
            throw new FaceDataVerificationException("Unknown error occurred when trying to send face data to ML backend.", e);
        }
    }
}

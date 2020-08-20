package com.zone24x7.faume.webapp.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.zone24x7.faume.webapp.exception.FaceDataVerificationException;
import com.zone24x7.faume.webapp.pojo.*;
import com.zone24x7.faume.webapp.util.AppConfigStringConstants;
import com.zone24x7.faume.webapp.util.JsonPojoConverter;
import com.zone24x7.faume.webapp.util.StringConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.time.Duration;
import java.util.Base64;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Implementation of the face data verification service.
 */
@Component
public class FaceAuthMeDataVerificationService implements FaceDataVerificationService {
    @Autowired
    private RestTemplateBuilder restTemplateBuilder;

    @Autowired
    @Qualifier("webClientBuilder")
    private WebClient.Builder webClientBuilder;

    @Value(AppConfigStringConstants.CONFIG_FACE_DATA_VERIFICATION_URL)
    private String mlBackendUrl;

    @Value(AppConfigStringConstants.CONFIG_INTEGRATION_APP_URL)
    private String integrationAppUrl;

    @Value(AppConfigStringConstants.CONFIG_INTEGRATION_APP_REQUEST_INFO_URL)
    private String integrationAppRequestInfoUrl;

    @Value(AppConfigStringConstants.CONFIG_INTEGRATION_APP_REQUEST_VERIFICATION_URL)
    private String integrationAppRequestVerificationUrl;

    @Value(AppConfigStringConstants.CONFIG_INTEGRATION_APP_DEVICE_BROWSER_INFO_URL)
    private String integrationAppDeviceBrowserInfoUrl;

    @Value(AppConfigStringConstants.CONFIG_INTEGRATION_APP_FACE_MATCH_RESULT_URL)
    private String integrationAppFaceMatchResultUrl;

    @Value(AppConfigStringConstants.CONFIG_REST_TEMPLATE_CONN_TIMEOUT_IN_MILLIS)
    private int restTemplateConnectionTimeoutInMillis;

    @Value(AppConfigStringConstants.CONFIG_REST_TEMPLATE_READ_TIMEOUT_IN_MILLIS)
    private long restTemplateReadTimeoutInMillis;

    @Value(AppConfigStringConstants.INTEGRATION_APP_API_KEYS)
    private String integrationAppApiKey;

    /**
     * Method to send face data for verification.
     *
     * @param faceData      the face data
     * @param correlationId the correlation id
     * @return the response from the ML backend
     * @throws FaceDataVerificationException if an error occurs when sending face data
     */
    @Override
    public String sendFaceDataForVerification(FaceData faceData, String correlationId) throws FaceDataVerificationException {
        //TODO: Change to Web client
        RestTemplate restTemplate = restTemplateBuilder
                .setConnectTimeout(Duration.ofMillis(restTemplateConnectionTimeoutInMillis))
                .setReadTimeout(Duration.ofMillis(restTemplateReadTimeoutInMillis))
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

    /**
     * Method to send face match result.
     *
     * @param requestId     the request id
     * @param response      the response
     * @param correlationId the correlation id
     * @return true if success and false if not
     * @throws FaceDataVerificationException if an error occurs when sending face data to integration app
     */
    @Override
    public boolean sendFaceMatchResult(String requestId, String response, String correlationId) throws FaceDataVerificationException {
        MLConfidenceResultContainer mlConfidenceResultContainer;

        try {
            mlConfidenceResultContainer = JsonPojoConverter.toPojo(response, MLConfidenceResultContainer.class);
        } catch (IOException e) {
            throw new FaceDataVerificationException("Error occurred when trying process ML face data result. Request Id: {}" + requestId, e);
        }

        FaceMatchResult faceMatchResult = new FaceMatchResult();
        faceMatchResult.setFaceMatch(mlConfidenceResultContainer.getConfidence().getFaceMatch());
        faceMatchResult.setLiveness(mlConfidenceResultContainer.getConfidence().getLiveness());

        WebClient webClient = webClientBuilder.baseUrl(integrationAppUrl).build();

        try {
            StatusResponse statusResponse = webClient.post()
                    .uri(uriBuilder -> uriBuilder.path(integrationAppFaceMatchResultUrl).queryParam("request_id", "{requestId}").build(requestId))
                    .body(Mono.just(faceMatchResult), FaceMatchResult.class)
                    .header(StringConstants.X_API_KEY_HEADER, integrationAppApiKey)
                    .retrieve()
                    .bodyToMono(StatusResponse.class)
                    .block();

            if (statusResponse == null) {
                return false;
            }

            return (StringConstants.CONTROLLER_RESPONSE_SUCCESS.equals(statusResponse.getStatus()));
        } catch (Exception e) {
            throw new FaceDataVerificationException("Error occurred when trying to send face match result to integration app. Request Id: " + requestId, e);
        }
    }

    /**
     * Method to send verification id to integration app for retrieving request id.
     *
     * @param verificationId the verification id.
     * @return RequestIdResponse
     */
    public RequestIdResponse getRequestIdFromVerificationId(String verificationId) throws FaceDataVerificationException {
        WebClient webClient = webClientBuilder.baseUrl(integrationAppUrl).build();

        try {
            return webClient
                    .get()
                    .uri(uriBuilder -> uriBuilder.path(integrationAppRequestInfoUrl).queryParam("verification_id", "{verificationId}").build(verificationId))
                    .header(StringConstants.X_API_KEY_HEADER, integrationAppApiKey)
                    .retrieve()
                    .bodyToMono(RequestIdResponse.class)
                    .block();
        } catch (Exception e) {
            throw new FaceDataVerificationException("Error occurred when trying to retrieve request id for given verification id : " + verificationId, e);
        }
    }

    /**
     * Method to send requestId to integration app and get verified
     *
     * @param requestId request id to be verified
     * @return verification status: VALID/INVALID
     */
    public boolean isRequestValid(String requestId) throws FaceDataVerificationException {
        WebClient webClient = webClientBuilder.baseUrl(integrationAppUrl).build();

        try {
            RequestIdResponse requestIdResponse = webClient
                    .get()
                    .uri(uriBuilder -> uriBuilder.path(integrationAppRequestVerificationUrl).queryParam("request_id", "{requestId}").build(requestId))
                    .header(StringConstants.X_API_KEY_HEADER, integrationAppApiKey)
                    .retrieve()
                    .bodyToMono(RequestIdResponse.class)
                    .block();

            return requestIdResponse != null && RequestIdStatus.VALID == requestIdResponse.getStatus();
        } catch (Exception e) {
            throw new FaceDataVerificationException("Error occurred when trying to retrieve status of the request id : " + requestId, e);
        }
    }

    /**
     * Method to send requestId to integration app and get verified
     *
     * @param requestId         request id to be verified
     * @param deviceBrowserInfo the device browser information
     * @return verification status: VALID/INVALID
     */
    public JsonNode sendDeviceBrowserInfo(String requestId, DeviceBrowserInfo deviceBrowserInfo) throws FaceDataVerificationException {
        WebClient webClient = webClientBuilder.baseUrl(integrationAppUrl).build();

        try {
            return webClient
                    .post()
                    .uri(uriBuilder -> uriBuilder.path(integrationAppDeviceBrowserInfoUrl).queryParam("request_id", "{requestId}").build(requestId))
                    .body(Mono.just(deviceBrowserInfo), DeviceBrowserInfo.class)
                    .header(StringConstants.X_API_KEY_HEADER, integrationAppApiKey)
                    .retrieve()
                    .bodyToMono(JsonNode.class)
                    .block();
        } catch (Exception e) {
            throw new FaceDataVerificationException("Error occurred when trying to retrieve status of the request id : " + requestId, e);
        }
    }
}

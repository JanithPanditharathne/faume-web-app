package com.zone24x7.faume.webapp.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.zone24x7.faume.webapp.exception.FaceDataVerificationException;
import com.zone24x7.faume.webapp.pojo.*;
import com.zone24x7.faume.webapp.processors.ChunkProcessor;
import com.zone24x7.faume.webapp.service.FaceDataVerificationService;
import com.zone24x7.faume.webapp.service.FilesStorageService;
import com.zone24x7.faume.webapp.util.AppConfigStringConstants;
import com.zone24x7.faume.webapp.util.JsonPojoConverter;
import com.zone24x7.faume.webapp.util.StringConstants;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Controller class for posting face data
 */
@RestController
public class FaceDataController {
    private static final Logger LOGGER = LoggerFactory.getLogger(FaceDataController.class);

    @Autowired
    private FilesStorageService storageService;

    @Autowired
    private ChunkProcessor chunkProcessor;

    @Autowired
    private FaceDataVerificationService faceDataVerificationService;

    @Value(AppConfigStringConstants.CONFIG_ACCOUNT_ID)
    private String accountId;

    @Value(AppConfigStringConstants.CONFIG_PROFILE_COUNT)
    private int profileCount;

    @Value(AppConfigStringConstants.CONFIG_PATTERN_ID)
    private int patternId;

    @Value(AppConfigStringConstants.CONFIG_FACE_DATA_SAVE_TO_FILE)
    private boolean saveFaceDataToFile;

    private static final String REQUEST_MALFORMED_ERROR_MESSAGE = "Request is malformed";
    private static final String REQUEST_EXPIRED_ERROR_MESSAGE = "Request is expired";

    /**
     * Method to post face data from the length based approach
     *
     * @param requestId the requestId
     * @param bytes     face data as a byte array
     * @param metaInfo  the meta info header
     * @return 200 OK if success, 400 if request is malformed, 403 if request is expired.
     */
    @CrossOrigin(origins = AppConfigStringConstants.CONFIG_CORS_ALLOWED_URLS)
    @PostMapping(path = "/v1/length-based/verification/web/{requestId}")
    public ResponseEntity<Object> postLengthBasedData(@PathVariable String requestId,
                                                      @RequestBody byte[] bytes,
                                                      @RequestHeader("x-meta-info") String metaInfo) {

        String correlationId = MDC.get(StringConstants.CORRELATION_ID);
        LOGGER.info("[CorrelationId: {}] Received Face Data RequestId: {}", correlationId, requestId);
        RequestMetaInfo requestMetaInfo;

        if (StringUtils.isEmpty(metaInfo)) {
            LOGGER.error("[CorrelationId: {}] x-meta-info header not found", correlationId);
            return new ResponseEntity<>(REQUEST_MALFORMED_ERROR_MESSAGE, HttpStatus.BAD_REQUEST);
        }

        try {
            requestMetaInfo = JsonPojoConverter.toPojo(metaInfo, RequestMetaInfo.class);
        } catch (IOException ioe) {
            LOGGER.error("[CorrelationId: {}] Error occurred when converting meta-info header information to POJO", correlationId, ioe);
            return new ResponseEntity<>(REQUEST_MALFORMED_ERROR_MESSAGE, HttpStatus.BAD_REQUEST);
        }

        requestMetaInfo.setRequestId(requestId);

        try {
            if (!faceDataVerificationService.isRequestValid(requestId)) {
                return new ResponseEntity<>(REQUEST_EXPIRED_ERROR_MESSAGE, HttpStatus.FORBIDDEN);
            }
        } catch (FaceDataVerificationException e) {
            LOGGER.error("[CorrelationId: {}] Error occurred when trying to check the status of the request id.", correlationId, e);
            return new ResponseEntity<>("Error occurred when trying to check the status of the request id.", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        String lengths = requestMetaInfo.getLengths();
        String[] splits = lengths.split("\\s*,\\s*");
        List<Integer> lengthsAsInts = new LinkedList<>();

        String faceDataResult = null;

        try {
            for (String length : splits) {
                lengthsAsInts.add(Integer.valueOf(length));
            }

            faceDataResult = sendFaceDataAndGetResult(lengthsAsInts, bytes, requestMetaInfo, correlationId);
        } catch (NumberFormatException e) {
            LOGGER.error("[CorrelationId: {}] Exception occurred while de-serializing header: {}", correlationId, metaInfo, e);
            return new ResponseEntity<>(REQUEST_MALFORMED_ERROR_MESSAGE, HttpStatus.BAD_REQUEST);
        }

        if (faceDataResult == null) {
            return new ResponseEntity<>(new StatusResponse(StringConstants.CONTROLLER_RESPONSE_FAILED), HttpStatus.OK);
        }

        boolean isSuccess;

        try {
            isSuccess = faceDataVerificationService.sendFaceMatchResult(requestId, faceDataResult, correlationId);
        } catch (FaceDataVerificationException e) {
            LOGGER.error("[CorrelationId: {}] Error occurred when trying to send face match result to integration app.", correlationId, e);
            return new ResponseEntity<>("Error occurred when trying to send face match result.", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if (!isSuccess) {
            return new ResponseEntity<>(new StatusResponse(StringConstants.CONTROLLER_RESPONSE_FAILED), HttpStatus.OK);
        }

        LOGGER.info("[CorrelationId: {}] Byte array length: {}, meta-info: {}", correlationId, bytes.length, metaInfo);
        return new ResponseEntity<>(new StatusResponse(StringConstants.CONTROLLER_RESPONSE_SUCCESS), HttpStatus.OK);
    }

    /**
     * Controller method to post data based on chunks.
     *
     * @param requestId the request id
     * @param data      the data
     * @param metaInfo  the meta information
     * @return 200 OK if success, 400 if the request is malformed, 403 if the request has expired
     */
    @CrossOrigin(origins = AppConfigStringConstants.CONFIG_CORS_ALLOWED_URLS)
    @PostMapping(path = "/v1/chunk-based/verification/web/{requestId}")
    public ResponseEntity<Object> postChunkBasedData(@PathVariable String requestId,
                                                     @RequestBody byte[] data,
                                                     @RequestHeader("x-meta-info") String metaInfo) {

        String correlationId = MDC.get(StringConstants.CORRELATION_ID);
        LOGGER.info("Received Face Data RequestId: {},  correlationId: {}", requestId, correlationId);
        ChunkRequestMetaInfo chunkRequestMetaInfo;

        try {
            chunkRequestMetaInfo = JsonPojoConverter.toPojo(metaInfo, ChunkRequestMetaInfo.class);
        } catch (IOException e) {
            return new ResponseEntity<>(REQUEST_MALFORMED_ERROR_MESSAGE, HttpStatus.BAD_REQUEST);
        }

        if (!requestId.equals(chunkRequestMetaInfo.getRequestId())) {
            return new ResponseEntity<>(REQUEST_MALFORMED_ERROR_MESSAGE, HttpStatus.BAD_REQUEST);
        }

        try {
            if (!faceDataVerificationService.isRequestValid(requestId)) {
                return new ResponseEntity<>(REQUEST_EXPIRED_ERROR_MESSAGE, HttpStatus.FORBIDDEN);
            }
        } catch (FaceDataVerificationException e) {
            LOGGER.error("[CorrelationId: {}] Error occurred when trying to check the status of the request id.", correlationId, e);
            return new ResponseEntity<>("Error occurred when trying to check the status of the request id.", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        //TODO: Unblock servlet thread.
        chunkProcessor.storeDataChunk(chunkRequestMetaInfo, data, correlationId);

        return new ResponseEntity<>(new StatusResponse(StringConstants.CONTROLLER_RESPONSE_SUCCESS), HttpStatus.OK);
    }

    /**
     * Method to post face data from the multi-part approach
     *
     * @param files     files to be uploaded
     * @param requestId the requestId
     * @param roi       the region of interest
     * @return 200 OK if success, 400 if request is malformed, 403 if request is expired.
     */
    @CrossOrigin(origins = AppConfigStringConstants.CONFIG_CORS_ALLOWED_URLS)
    @PostMapping("/v1/multi-part/verification/web/{requestId}")
    public ResponseEntity<Object> postMultiPartBasedData(@RequestParam("files") MultipartFile[] files, @PathVariable("requestId") String requestId, @RequestParam("roi") String roi) {
        String correlationId = MDC.get(StringConstants.CORRELATION_ID);
        LOGGER.info("[CorrelationId: {}] Received Face Data RequestId: {}, files: {}, roi: {}", correlationId, requestId, files.length, roi);

        try {
            if (!faceDataVerificationService.isRequestValid(requestId)) {
                return new ResponseEntity<>(REQUEST_EXPIRED_ERROR_MESSAGE, HttpStatus.FORBIDDEN);
            }
        } catch (FaceDataVerificationException e) {
            LOGGER.error("[CorrelationId: {}] Error occurred when trying to check the status of the request id.", correlationId, e);
            return new ResponseEntity<>("Error occurred when trying to check the status of the request id.", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        //TODO: Remove. Saving for tests
        Arrays.asList(files).forEach(file -> {
            try {
                storageService.save(file);
            } catch (IOException e) {
                LOGGER.error("[CorrelationId: {}] Error occurred when trying to save file: {}", MDC.get("correlationId"), file.getName());
            }
        });

        return new ResponseEntity<>(new StatusResponse(StringConstants.CONTROLLER_RESPONSE_SUCCESS), HttpStatus.OK);
    }

    /**
     * Method to get the request id associate with a given verification id.
     *
     * @param verificationId the verification id.
     * @return status: VALID if the given verification id is valid, INVALID if the given verification id is invalid.
     * request_id: the request id associated with the given verification id.
     */
    @CrossOrigin(origins = AppConfigStringConstants.CONFIG_CORS_ALLOWED_URLS)
    @GetMapping("/v1/request-info")
    public ResponseEntity<Object> getRequestId(@RequestParam(value = "verification_id", defaultValue = "") String verificationId) {
        String correlationId = MDC.get(StringConstants.CORRELATION_ID);

        if (StringUtils.isEmpty(verificationId)) {
            return new ResponseEntity<>("Verification Id is mandatory", HttpStatus.BAD_REQUEST);
        }

        try {
            RequestIdResponse requestIdFromVerificationId = faceDataVerificationService.getRequestIdFromVerificationId(verificationId);
            LOGGER.info("[CorrelationId: {}] received the request id: {} from verification id : {}", correlationId, requestIdFromVerificationId.getRequestId(), verificationId);
            return new ResponseEntity<>(requestIdFromVerificationId, HttpStatus.OK);
        } catch (FaceDataVerificationException e) {
            LOGGER.error("[CorrelationId: {}] Error occurred when trying to get the request id from verification id.", correlationId, e);
            return new ResponseEntity<>("Error occurred when trying to get the request id from verification id.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Controller method to store device binding information.
     *
     * @param requestId the request id
     * @param deviceBrowserInfo the face match result
     * @return 200 OK status if success, 400 if bad request and 403 if forbidden
     */
    @CrossOrigin(origins = AppConfigStringConstants.CONFIG_CORS_ALLOWED_URLS)
    @PostMapping(path = "/v1/web/device-browser-info")
    public ResponseEntity<Object> sendFaceMatchResults(@RequestParam(value = "request_id") String requestId,
                                                       @RequestBody DeviceBrowserInfo deviceBrowserInfo) {

        String correlationId = MDC.get(StringConstants.CORRELATION_ID);
        LOGGER.info("[CorrelationId: {}] Received device browser data. Request Id : {}, DeviceBrowserInfo: {}", correlationId, requestId, deviceBrowserInfo);

        try {
            JsonNode response = faceDataVerificationService.sendDeviceBrowserInfo(requestId, deviceBrowserInfo);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (FaceDataVerificationException e) {
            LOGGER.error("[CorrelationId: {}] Error occurred when trying to send device browser information", correlationId, e);
            return new ResponseEntity<>("Error occurred when trying to send device browser information.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Method to send face data.
     *
     * (2000); // 0 - 1999
     * (5000); 7000// 2000 - 6999
     * (3000); 10000// 7000 - 9999
     *
     * @param images          images array
     * @param requestMetaInfo the request meta information
     * @param correlationId   the correlation id
     * @return the face data result, null will be returned if any error occurs
     */
    private String sendFaceDataAndGetResult(List<Integer> lengths, byte[] images, RequestMetaInfo requestMetaInfo, String correlationId) {
        int i = 0;
        List<byte[]> data = new LinkedList<>();

        for (int length : lengths) {
            byte[] bytes = Arrays.copyOfRange(images, i, length + i);
            data.add(bytes);

            i += length;

            //TODO: Remove. Saving for tests
            if (saveFaceDataToFile) {
                try {
                    storageService.saveImage(Paths.get("frame" + length + ".png"), bytes, "png");
                } catch (IOException e) {
                    LOGGER.error("[CorrelationId: {}] Error occurred when trying to save file : {}", correlationId, "frame" + length + ".png");
                }
            }
        }

        FaceData faceData = new FaceData(requestMetaInfo.getRequestId(), accountId, profileCount, patternId, requestMetaInfo.getRoi(), data);

        try {
            String result = faceDataVerificationService.sendFaceDataForVerification(faceData, correlationId);
            LOGGER.info("[CorrelationId: {}] Response from face verification : {}", correlationId, result);
            return result;
        } catch (FaceDataVerificationException e) {
            LOGGER.error("[CorrelationId: {}] Error occurred when trying to verify face data", correlationId, e);
        }

        return null;
    }
}

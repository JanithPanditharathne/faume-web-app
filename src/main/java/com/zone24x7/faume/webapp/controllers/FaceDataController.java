package com.zone24x7.faume.webapp.controllers;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.zone24x7.faume.webapp.exception.FaceDataVerificationException;
import com.zone24x7.faume.webapp.pojo.ChunkRequestMetaInfo;
import com.zone24x7.faume.webapp.pojo.FaceData;
import com.zone24x7.faume.webapp.pojo.OutputFrame;
import com.zone24x7.faume.webapp.pojo.RequestMetaInfo;
import com.zone24x7.faume.webapp.processors.ChunkProcessor;
import com.zone24x7.faume.webapp.service.FaceDataVerificationService;
import com.zone24x7.faume.webapp.service.FilesStorageService;
import com.zone24x7.faume.webapp.util.JsonPojoConverter;
import com.zone24x7.faume.webapp.util.AppConfigStringConstants;
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

    /**
     * Method to post face data from the length based approach
     *
     * @param requestId the requestId
     * @param bytes     face data as a byte array
     * @param metaInfo  the meta info header
     * @return 200 OK if success, 400 if request is malformed, 403 if request is expired.
     */
    //TODO: Add Proper CORS
    @CrossOrigin(origins = "*")
    @PostMapping(path = "/v1/length-based/verification/web/{requestId}")
    public ResponseEntity<Object> postLengthBasedData(@PathVariable String requestId,
                                                      @RequestBody byte[] bytes,
                                                      @RequestHeader("x-meta-info") String metaInfo) {

        String correlationId = MDC.get("correlationId");
        LOGGER.info("[CorrelationId: {}] Received Face Data RequestId: {}", correlationId, requestId);
        RequestMetaInfo requestMetaInfo;

        try {
            requestMetaInfo = JsonPojoConverter.toPojo(metaInfo, RequestMetaInfo.class);
        } catch (IOException ioe) {
            LOGGER.error("[CorrelationId: {}] Error occurred when converting meta-info header information to POJO", correlationId, ioe);
            return new ResponseEntity<>("Request is malformed", HttpStatus.BAD_REQUEST);
        }

        String lengths = requestMetaInfo.getLengths();
        String[] splits = lengths.split("\\s*,\\s*");
        List<Integer> lengthsAsInts = new LinkedList<>();

        try {
            for (String length : splits) {
                lengthsAsInts.add(Integer.valueOf(length));
            }

            getOutputFrames(lengthsAsInts, bytes, requestMetaInfo, correlationId);
        } catch (NumberFormatException e) {
            LOGGER.error("[CorrelationId: {}] Exception occurred while de-serializing header: {}", correlationId, metaInfo, e);
            return new ResponseEntity<>("Request is malformed", HttpStatus.BAD_REQUEST);
        }

        LOGGER.info("[CorrelationId: {}] Byte array length: {}, meta-info: {}", correlationId, bytes.length, metaInfo);
        ObjectNode jsonNode = JsonNodeFactory.instance.objectNode();
        jsonNode.put("status", "success");
        return new ResponseEntity<>(jsonNode, HttpStatus.OK);
    }

    /**
     * Controller method to post data based on chunks.
     *
     * @param requestId the request id
     * @param data      the data
     * @param metaInfo  the meta information
     * @return 200 OK if success, 400 if the request is malformed, 403 if the request has expired
     */
    //TODO: Add Proper CORS
    @CrossOrigin(origins = "*")
    @PostMapping(path = "/v1/chunk-based/verification/web/{requestId}")
    public ResponseEntity<Object> postChunkBasedData(@PathVariable String requestId,
                                                     @RequestBody byte[] data,
                                                     @RequestHeader("x-meta-info") String metaInfo) {

        String correlationId = MDC.get("correlationId");
        LOGGER.info("Received Face Data RequestId: {},  correlationId: {}", requestId, correlationId);
        ChunkRequestMetaInfo chunkRequestMetaInfo;

        try {
            chunkRequestMetaInfo = JsonPojoConverter.toPojo(metaInfo, ChunkRequestMetaInfo.class);
        } catch (IOException e) {
            return new ResponseEntity<>("Request is malformed.", HttpStatus.BAD_REQUEST);
        }

        if (!requestId.equals(chunkRequestMetaInfo.getRequestId())) {
            return new ResponseEntity<>("Request is malformed.", HttpStatus.BAD_REQUEST);
        }

        //TODO: Validate request validity and send 403 forbidden if invalid.

        //TODO: Unblock servlet thread.
        chunkProcessor.storeDataChunk(chunkRequestMetaInfo, data, correlationId);

        ObjectNode jsonNode = JsonNodeFactory.instance.objectNode();
        jsonNode.put("status", "success");
        return new ResponseEntity<>(jsonNode, HttpStatus.OK);
    }

    /**
     * Method to post face data from the multi-part approach
     *
     * @param files     files to be uploaded
     * @param requestId the requestId
     * @return 200 OK if success, 400 if request is malformed, 403 if request is expired.
     */
    //TODO: Add Proper CORS
    @CrossOrigin(origins = "*")
    @PostMapping("/v1/multi-part/verification/web/{requestId}")
    public ResponseEntity<Object> postMultiPartBasedData(@RequestParam("files") MultipartFile[] files, @PathVariable("requestId") String requestId) {
        LOGGER.info("[CorrelationId: {}] Received Face Data RequestId: {}, files: {}", MDC.get("correlationId"), requestId, files.length);

        //TODO: Remove. Saving for tests
        Arrays.asList(files).forEach(file -> {
            try {
                storageService.save(file);
            } catch (IOException e) {
                LOGGER.error("[CorrelationId: {}] Error occurred when trying to save file: {}", MDC.get("correlationId"), file.getName());
            }
        });

        ObjectNode jsonNode = JsonNodeFactory.instance.objectNode();
        jsonNode.put("status", "success");
        return new ResponseEntity<>(jsonNode, HttpStatus.OK);
    }

    /**
     * (2000); // 0 - 1999
     * (5000); 7000// 2000 - 6999
     * (3000); 10000// 7000 - 9999
     *
     * @param images          images array
     * @param requestMetaInfo the request meta information
     * @param correlationId   the correlation id
     * @return list of output frames
     */
    private List<OutputFrame> getOutputFrames(List<Integer> lengths, byte[] images, RequestMetaInfo requestMetaInfo, String correlationId) {
        List<OutputFrame> outputFrames = new LinkedList<>();
        int i = 0;
        int count = 0;

        List<byte[]> data = new LinkedList<>();

        for (int length : lengths) {
            byte[] bytes = Arrays.copyOfRange(images, i, length + i);
            data.add(bytes);

            i += length;
            outputFrames.add(new OutputFrame(count++, bytes));

            //TODO: Remove. Saving for tests

            if (saveFaceDataToFile) {
                try {
                    storageService.saveImage(Paths.get("frame" + length + ".png"), bytes, "png");
                } catch (IOException e) {
                    LOGGER.error("[CorrelationId: {}] Error occurred when trying to save file{}", correlationId, "frame" + length + ".png");
                }
            }
        }

        //TODO: Add roi
        FaceData faceData = new FaceData(requestMetaInfo.getRequestId(), accountId, profileCount, patternId, "roi", data);

        try {
            String result = faceDataVerificationService.sendFaceDataForVerification(faceData, correlationId);
            LOGGER.info("[CorrelationId: {}] RESPONSE FROM ML BACKEND : {}", correlationId, result);
        } catch (FaceDataVerificationException e) {
            LOGGER.error("Error occurred when trying to verify face data", e);
        }

        return outputFrames;
    }
}

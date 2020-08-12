package com.zone24x7.faume.webapp.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.zone24x7.faume.webapp.pojo.ChunkRequestMetaInfo;
import com.zone24x7.faume.webapp.pojo.OutputFrame;
import com.zone24x7.faume.webapp.pojo.RequestMetaInfo;
import com.zone24x7.faume.webapp.service.FilesStorageService;
import com.zone24x7.faume.webapp.util.JsonPojoConverter;
import com.zone24x7.faume.webapp.processors.ChunkProcessor;
import com.zone24x7.faume.webapp.util.JsonPojoConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
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

    /**
     * Method to post face data from the length based approach
     *
     * @param requestId the requestId
     * @param bytes     face data as a byte array
     * @param metaInfo  the meta info header
     * @return 200 OK if success, 400 if request is malformed, 403 if request is expired.
     */
    @PostMapping(path = "/v1/length-based/verification/web/{requestId}")
    public ResponseEntity<Object> postLengthBasedData(@PathVariable String requestId,
                                                      @RequestBody byte[] bytes,
                                                      @RequestHeader("x-meta-info") String metaInfo) {

        LOGGER.info("Received Face Data RequestId: {},  correlationId: {}", requestId, MDC.get("correlationId"));
        RequestMetaInfo requestMetaInfo;

        try {
            requestMetaInfo = JsonPojoConverter.toPojo(metaInfo, RequestMetaInfo.class);
        } catch (IOException ioe) {
            LOGGER.error("Error occurred when converting meta-info header information to POJO", ioe);
            return new ResponseEntity<>("Request is malformed", HttpStatus.BAD_REQUEST);
        }

        String lengths = requestMetaInfo.getLengths();
        String[] splits = lengths.split("\\s*,\\s*");
        List<Integer> lengthsAsInts = new LinkedList<>();

        try {
            for (String length : splits) {
                lengthsAsInts.add(Integer.valueOf(length));
            }

            getOutputFrames(lengthsAsInts, bytes);
        } catch (NumberFormatException e) {
            LOGGER.error("Exception occurred while de-serializing header: {}", metaInfo, e);
            return new ResponseEntity<>("Request is malformed", HttpStatus.BAD_REQUEST);
        }

        LOGGER.info("Byte array length: {}, meta-info", bytes.length, metaInfo);
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
     * (2000); // 0 - 1999
     * (5000); 7000// 2000 - 6999
     * (3000); 10000// 7000 - 9999
     *
     * @param images images array
     * @return list of output frames
     */
    private List<OutputFrame> getOutputFrames(List<Integer> lengths, byte[] images) {
        List<OutputFrame> outputFrames = new LinkedList<>();
        int i = 0;
        int count = 0;

        for (int length : lengths) {
            byte[] bytes = Arrays.copyOfRange(images, i, length + i);
            i += length;
            outputFrames.add(new OutputFrame(count++, bytes));

            //TODO: Remove. Saving for tests
            try {
                storageService.save(Paths.get("frame" + length + ".png"), bytes);
            } catch (IOException e) {
                LOGGER.error("Error occurred when trying to save file{}", "frame" + length + ".png");
            }
        }

        return outputFrames;
    }

    /**
     * Method to post face data from the multi-part approach
     *
     * @param files     files to be uploaded
     * @param requestId the requestId
     * @return 200 OK if success, 400 if request is malformed, 403 if request is expired.
     */
    @PostMapping("/v1/multi-part/verification/web/{requestId}")
    public ResponseEntity<Object> postMultiPartBasedData(@RequestParam("files") MultipartFile[] files, @PathVariable("requestId") String requestId) {
        LOGGER.info("Received Face Data RequestId: {},  correlationId: {}, files: {}", requestId, MDC.get("correlationId"), files.length);

        //TODO: Remove. Saving for tests
        Arrays.asList(files).forEach(file -> {
            try {
                storageService.save(file);
            } catch (IOException e) {
                LOGGER.error("Error occurred when trying to save file: {}", file.getName());
            }
        });

        ObjectNode jsonNode = JsonNodeFactory.instance.objectNode();
        jsonNode.put("status", "success");
        return new ResponseEntity<>(jsonNode, HttpStatus.OK);
    }
}

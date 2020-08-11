package com.zone24x7.faume.webapp.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zone24x7.faume.webapp.pojo.OutputFrame;
import com.zone24x7.faume.webapp.pojo.RequestMetaInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

@RestController
public class FaceDataController {
    private static final Logger LOGGER = LoggerFactory.getLogger(FaceDataController.class);

    @PostMapping(path = "/v1/length-based/verification/{requestId}")
    public ResponseEntity<String> postLengthBasedData(@PathVariable String requestId,
                                                      @RequestBody byte[] bytes,
                                                      @RequestHeader("x-meta-info") String metaInfo) {
        LOGGER.info("Received Face Data RequestId: {},  correlationID{}", requestId, MDC.get("correlationId"));

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            RequestMetaInfo requestMetaInfo = objectMapper.readValue(metaInfo, RequestMetaInfo.class);
            String lengths = requestMetaInfo.getLengths();
            String[] splits = lengths.split(",");

            List<Integer> lengthsAsInts = new LinkedList<>();
            for (String oneString : splits) {
                //TODO: handle exception
                lengthsAsInts.add(Integer.valueOf(oneString));
            }

            getOutputFrames(lengthsAsInts, bytes);
        } catch (JsonProcessingException e) {
            LOGGER.error("Exception occurred while de-serializing header: {}", metaInfo, e);
            return new ResponseEntity<String>("Error in header", HttpStatus.BAD_REQUEST);
        }

        LOGGER.info("Byte array length: {}, metaonfo", bytes.length, metaInfo);
        return new ResponseEntity<String>("POST Response", HttpStatus.OK);
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
                Files.write(Paths.get("frame" + length + ".png"), bytes);
            } catch (IOException e) {
                LOGGER.error("Exception saving", e);
            }
        }
        return outputFrames;
    }
}

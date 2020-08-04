package com.zone24x7.faume.webapp.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FaceDataController {
    private static final Logger LOGGER = LoggerFactory.getLogger(FaceDataController.class);

    @PostMapping(path = "/v1/verification/{requestId}")
    public ResponseEntity<String> getRecommendation(@PathVariable String requestId){

        LOGGER.info("Received Face Data. RequestId: {}, {}", requestId, MDC.get("correlationId"));
        return new ResponseEntity<String>("POST Response", HttpStatus.OK);
    }
}

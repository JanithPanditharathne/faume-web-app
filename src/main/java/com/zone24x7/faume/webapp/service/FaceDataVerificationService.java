package com.zone24x7.faume.webapp.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.zone24x7.faume.webapp.exception.FaceDataVerificationException;
import com.zone24x7.faume.webapp.pojo.DeviceBrowserInfo;
import com.zone24x7.faume.webapp.pojo.FaceData;
import com.zone24x7.faume.webapp.pojo.RequestIdResponse;
import com.zone24x7.faume.webapp.pojo.StatusResponse;

/**
 * Interface for face data verification service.
 */
public interface FaceDataVerificationService {

    /**
     * Method to send face data for verification.
     *
     * @param faceData the face data
     * @param correlationId the correlation id
     * @return the response from the ML backend
     * @throws FaceDataVerificationException if an error occurs when sending face data
     */
    String sendFaceDataForVerification(FaceData faceData, String correlationId) throws FaceDataVerificationException;

    /**
     * Method to send face match result.
     *
     * @param requestId the request id
     * @param response the response
     * @param correlationId the correlation id
     * @return true if success and false if not
     * @throws FaceDataVerificationException if an error occurs when sending face data to integration app
     */
    boolean sendFaceMatchResult(String requestId, String response, String correlationId) throws FaceDataVerificationException;

    /**
     * Method to send verification id to integration app for retrieving request id.
     *
     * @param verificationId the verification id.
     * @return RequestIdResponse
     */
    RequestIdResponse getRequestIdFromVerificationId(String verificationId) throws FaceDataVerificationException;

    /**
     * Method to send requestId to integration app and get verified
     *
     * @param requestId request id to be verified
     * @return verification status: VALID/INVALID
     */
    boolean isRequestValid(String requestId) throws FaceDataVerificationException;

    /**
     * Method to send requestId to integration app and get verified
     *
     * @param requestId         request id to be verified
     * @param deviceBrowserInfo the device browser information
     * @return verification status: VALID/INVALID
     */
    JsonNode sendDeviceBrowserInfo(String requestId, DeviceBrowserInfo deviceBrowserInfo) throws FaceDataVerificationException;
}

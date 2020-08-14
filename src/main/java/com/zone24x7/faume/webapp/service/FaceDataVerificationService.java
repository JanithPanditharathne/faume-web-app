package com.zone24x7.faume.webapp.service;

import com.zone24x7.faume.webapp.exception.FaceDataVerificationException;
import com.zone24x7.faume.webapp.pojo.FaceData;

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
}

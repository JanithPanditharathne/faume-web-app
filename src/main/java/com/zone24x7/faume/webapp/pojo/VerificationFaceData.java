package com.zone24x7.faume.webapp.pojo;

import java.util.List;

/**
 * Class to represent VerificationFaceData.
 */
public class VerificationFaceData {
    private String userId;
    private List<String> frames;
    private String roi;

    /**
     * Constructor to instantiate VerificationFaceData
     * @param userId the user id
     * @param frames the frames
     * @param roi the roi
     */
    public VerificationFaceData(String userId, List<String> frames, String roi) {
        this.userId = userId;
        this.frames = frames;
        this.roi = roi;
    }

    /**
     * Method to get the user id.
     *
     * @return the user id
     */
    public String getUserId() {
        return userId;
    }

    /**
     * Method to get the frames.
     *
     * @return the frames
     */
    public List<String> getFrames() {
        return frames;
    }

    /**
     * Method to get the ROI.
     *
     * @return the ROI
     */
    public String getRoi() {
        return roi;
    }
}

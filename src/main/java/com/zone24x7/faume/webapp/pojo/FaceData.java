package com.zone24x7.faume.webapp.pojo;

import java.util.List;

/**
 * Class to represent face data.
 */
public class FaceData {
    private String requestId;
    private String accountId;
    private int profileCount;
    private int patternId;
    private int roi;
    private List<byte[]> data;

    /**
     * Constructor to instantiate FaceData.
     *
     * @param requestId the request id
     * @param accountId the account id
     * @param profileCount the profile count
     * @param patternId the pattern id
     * @param roi the roi
     * @param data the data to be sent
     */
    public FaceData(String requestId, String accountId, int profileCount, int patternId, int roi, List<byte[]> data) {
        this.requestId = requestId;
        this.accountId = accountId;
        this.profileCount = profileCount;
        this.patternId = patternId;
        this.roi = roi;
        this.data = data;
    }

    /**
     * Method to get the request id.
     *
     * @return the request id
     */
    public String getRequestId() {
        return requestId;
    }

    /**
     * Method to get the account id.
     *
     * @return the account id
     */
    public String getAccountId() {
        return accountId;
    }

    /**
     * Method to get the profile count.
     *
     * @return the profile count
     */
    public int getProfileCount() {
        return profileCount;
    }

    /**
     * Method to get the pattern id.
     *
     * @return the pattern id
     */
    public int getPatternId() {
        return patternId;
    }

    /**
     * Method to get the data.
     *
     * @return the data
     */
    public List<byte[]> getData() {
        return data;
    }

    /**
     * Method to get the ROI.
     *
     * @return the ROI
     */
    public int getRoi() {
        return roi;
    }
}

package com.zone24x7.faume.webapp.pojo;

/**
 * POJO class for Request meta-info
 */
public class RequestMetaInfo {
    private String lengths;
    private int roi;

    /**
     * Method to get the length of the request meta-info
     *
     * @return length of the meta-info
     */
    public String getLengths() {
        return lengths;
    }

    /**
     * Method to set the length of the request meta-info
     *
     * @param lengths length of the meta-info
     */
    public void setLengths(String lengths) {
        this.lengths = lengths;
    }

    /**
     * Method to get the region of interest
     *
     * @return region of interest
     */
    public int getRoi() {
        return roi;
    }

    /**
     * Method to set the region of interest
     *
     * @param roi region of interest to be set
     */
    public void setRoi(int roi) {
        this.roi = roi;
    }
}

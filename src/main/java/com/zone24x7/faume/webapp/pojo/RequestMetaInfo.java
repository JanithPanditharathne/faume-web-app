package com.zone24x7.faume.webapp.pojo;

/**
 * POJO class for Request meta-info
 */
public class RequestMetaInfo {
    private String lengths;
    private String requestId;

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
     * Method to get the request id
     *
     * @return request id
     */
    public String getRequestId() {
        return requestId;
    }

    /**
     * Method to set the request id
     *
     * @param requestId request id to be set
     */
    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }
}

package com.zone24x7.faume.webapp.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * POJO class for Response of get request_id from integration-app
 */
public class RequestIdResponse {
    @JsonProperty("status")
    private Validity status;
    @JsonProperty("request_id")
    private String requestId;

    /**
     * Constructor to initiate RequestIdResponse
     *
     * @param status the status of the get request_id from verification id request
     * @param requestId the request id
     */
    public RequestIdResponse(Validity status, String requestId) {
        this.status = status;
        this.requestId = requestId;
    }

    /**
     * Method to get the status
     *
     * @return the status
     */
    public Validity getStatus() {
        return status;
    }

    /**
     * Method to set the status
     *
     * @param status the status
     */
    public void setStatus(Validity status) {
        this.status = status;
    }

    /**
     * Method to get request id
     *
     * @return the request id
     */
    public String getRequestId() {
        return requestId;
    }

    /**
     * Method to set the request id
     *
     * @param requestId the request id
     */
    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }
}

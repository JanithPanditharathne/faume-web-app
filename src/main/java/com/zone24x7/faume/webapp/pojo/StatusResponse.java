package com.zone24x7.faume.webapp.pojo;

/**
 * Class to represent the status response.
 */
public class StatusResponse {
    private String status;

    /**
     * Constructor to instantiate StatusResponse.
     *
     * @param status the status
     */
    public StatusResponse(String status) {
        this.status = status;
    }

    /**
     * Default constructor
     */
    public StatusResponse() {
        // Default constructor
    }

    /**
     * Get status.
     *
     * @return Status
     */
    public String getStatus() {
        return status;
    }

    /**
     * Method to set the status.
     *
     * @param status the status
     */
    public void setStatus(String status) {
        this.status = status;
    }
}

package com.zone24x7.faume.webapp.pojo;

/**
 * Class for response message
 */
public class ResponseMessage {
    private String message;

    /**
     * Constructor to create a ResponseMessage
     *
     * @param message the message which needs to be included in the response
     */
    public ResponseMessage(String message) {
        this.message = message;
    }

    /**
     * Method to get the message
     *
     * @return the message in the response
     */
    public String getMessage() {
        return message;
    }

    /**
     * Method to set the message to response
     *
     * @param message the message to be set to the response
     */
    public void setMessage(String message) {
        this.message = message;
    }

}

package com.zone24x7.faume.webapp.model;

public class FileUploadTaskMetaData {
    private String message;

    public FileUploadTaskMetaData() {
    }

    public FileUploadTaskMetaData(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

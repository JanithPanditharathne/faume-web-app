package com.zone24x7.faume.webapp.enumeration;

/**
 * Enum constants for validity status
 */
public enum RequestIdStatus {
    VALID("VALID"),
    INVALID("INVALID");

    private String value;

    /**
     * Constructor to instantiate an enum constant
     *
     * @param value validity status value of the enum constant
     */
    RequestIdStatus(String value) {
        this.value = value;
    }
}

package com.zone24x7.faume.webapp.pojo;

/**
 * Enum constants for validity status
 */
public enum Validity {
    VALID("VALID"),INVALID("INVALID");

    private String value;

    /**
     * Constructor to instantiate an enum constant
     *
     * @param value validity status value of the enum constant
     */
    Validity(String value){
        this.value=value;
    }

}

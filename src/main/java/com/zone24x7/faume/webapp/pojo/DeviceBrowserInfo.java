package com.zone24x7.faume.webapp.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Class to represent device browser information.
 */
public class DeviceBrowserInfo {
    private String deviceToken;
    private String browserFingerprint;

    /**
     * Method to get the device token.
     *
     * @return the device token
     */
    @JsonProperty("device_token")
    public String getDeviceToken() {
        return deviceToken;
    }

    /**
     * Method to set the device token.
     *
     * @param deviceToken the device token
     */
    @JsonProperty("device_token")
    public void setDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
    }

    /**
     * Method to get the browser fingerprint.
     *
     * @return the browser fingerprint
     */
    @JsonProperty("browser_fingerprint")
    public String getBrowserFingerprint() {
        return browserFingerprint;
    }

    /**
     * Method to set the browser fingerprint.
     *
     * @param browserFingerprint the browser fingerprint
     */
    @JsonProperty("browser_fingerprint")
    public void setBrowserFingerprint(String browserFingerprint) {
        this.browserFingerprint = browserFingerprint;
    }
}


package com.zone24x7.faume.webapp.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * POJO class for Response of get request_id from integration-app
 */
public class RequestIdValidityResponse {
    @JsonProperty("status")
    private RequestIdStatus status;

    @JsonProperty("account_id")
    private String accountId;

    @JsonProperty("profile_count")
    private int profileCount;

    /**
     * Method to get the status
     *
     * @return the status
     */
    @JsonProperty("status")
    public RequestIdStatus getStatus() {
        return status;
    }

    /**
     * Method to set the status
     *
     * @param status the status
     */
    @JsonProperty("status")
    public void setStatus(RequestIdStatus status) {
        this.status = status;
    }

    /**
     * Method to get the account id
     *
     * @return the account id
     */
    @JsonProperty("account_id")
    public String getAccountId() {
        return accountId;
    }

    /**
     * Method to set the status
     *
     * @param accountId the account id
     */
    @JsonProperty("account_id")
    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    /**
     * Method to get the profile count
     *
     * @return the profile count
     */
    @JsonProperty("profile_count")
    public int getProfileCount() {
        return profileCount;
    }

    /**
     * Method to set the profile count
     *
     * @param profileCount the profile count
     */
    @JsonProperty("profile_count")
    public void setProfileCount(int profileCount) {
        this.profileCount = profileCount;
    }
}

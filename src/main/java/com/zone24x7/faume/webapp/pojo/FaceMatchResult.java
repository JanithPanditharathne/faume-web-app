package com.zone24x7.faume.webapp.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Class to represent the face match result.
 */
public class FaceMatchResult {
    private double liveness;
    private double faceMatch;

    /**
     * Method to get the liveness.
     *
     * @return liveness score
     */
    @JsonProperty("liveness")
    public double getLiveness() {
        return liveness;
    }

    /**
     * Method to set the liveness score.
     *
     * @param liveness the liveness score to set
     */
    @JsonProperty("liveness")
    public void setLiveness(double liveness) {
        this.liveness = liveness;
    }

    /**
     * Method to get the face match score.
     *
     * @return face match score
     */
    @JsonProperty("face_match")
    public double getFaceMatch() {
        return faceMatch;
    }

    /**
     * Method to set the face match score.
     *
     * @param faceMatch face match score to set
     */
    @JsonProperty("face_match")
    public void setFaceMatch(double faceMatch) {
        this.faceMatch = faceMatch;
    }

    /**
     * Overridden toString method.
     *
     * @return string representation of the object
     */
    @Override
    public String toString() {
        return "FaceMatchResult{" +
                "liveness=" + liveness +
                ", faceMatch=" + faceMatch +
                '}';
    }
}

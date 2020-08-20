package com.zone24x7.faume.webapp.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Class to represent ML confidence result container.
 */
public class MLConfidenceResultContainer {
    private ConfidenceResult confidence;

    /**
     * Method to get the confidence result.
     *
     * @return the confidence result
     */
    @JsonProperty("Confidence")
    public ConfidenceResult getConfidence() {
        return confidence;
    }

    /**
     * Method to set the confidence result.
     *
     * @param confidence the confidence result
     */
    @JsonProperty("Confidence")
    public void setConfidence(ConfidenceResult confidence) {
        this.confidence = confidence;
    }

    /**
     * Class to represent the confidence result.
     */
    public class ConfidenceResult {
        private double faceMatch;
        private double liveness;

        /**
         * Method to get the face match.
         *
         * @return the face match
         */
        @JsonProperty("Face_match")
        public double getFaceMatch() {
            return faceMatch;
        }

        /**
         * Method to set the face match
         *
         * @param faceMatch the face match
         */
        @JsonProperty("Face_match")
        public void setFaceMatch(double faceMatch) {
            this.faceMatch = faceMatch;
        }

        /**
         * Method to get the liveness.
         *
         * @return the liveness
         */
        @JsonProperty("liveliness")
        public double getLiveness() {
            return liveness;
        }

        /**
         * Method to set the liveness.
         *
         * @param liveness the liveness
         */
        @JsonProperty("liveliness")
        public void setLiveness(double liveness) {
            this.liveness = liveness;
        }
    }
}

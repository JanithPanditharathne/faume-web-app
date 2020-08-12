package com.zone24x7.faume.webapp.pojo;

/**
 * POJO class for OutputFrame
 */
public class OutputFrame {
    private int frameId;
    private byte[] data;

    /**
     * Constructor to initiate an OutputFrame
     *
     * @param frameId the frame id
     * @param data the data
     */
    public OutputFrame(int frameId, byte[] data) {
        this.frameId = frameId;
        this.data = data;
    }

    /**
     * Method to get the frame id
     *
     * @return the frame id
     */
    public int getFrameId() {
        return frameId;
    }

    /**
     * Method to set the frame id
     *
     * @param frameId the frame id to be set
     */
    public void setFrameId(int frameId) {
        this.frameId = frameId;
    }

    /**
     * Method to get date of the frame
     *
     * @return data of the frame
     */
    public byte[] getData() {
        return data;
    }

    /**
     * Method to set data to the frame
     *
     * @param data frame data to be set
     */
    public void setData(byte[] data) {
        this.data = data;
    }
}

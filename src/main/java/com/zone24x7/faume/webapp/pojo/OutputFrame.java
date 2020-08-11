package com.zone24x7.faume.webapp.pojo;

public class OutputFrame {
    private int frameId;
    private byte[] data;

    public OutputFrame(int frameId, byte[] data) {
        this.frameId = frameId;
        this.data = data;
    }

    public int getFrameId() {
        return frameId;
    }

    public void setFrameId(int frameId) {
        this.frameId = frameId;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }
}

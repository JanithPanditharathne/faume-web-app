package com.zone24x7.faume.webapp.pojo;

/**
 * Class to represent chunk request meta information.
 */
public class ChunkRequestMetaInfo {
    private String requestId;
    private int totalChunkCount;
    private int chunkId;

    /**
     * Method to get the request id.
     *
     * @return the request id
     */
    public String getRequestId() {
        return requestId;
    }

    /**
     * Method to set the request id.
     *
     * @param requestId the request id
     */
    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    /**
     * Method to get the total chunk count.
     *
     * @return the total chunk count
     */
    public int getTotalChunkCount() {
        return totalChunkCount;
    }

    /**
     * Method to set the total chunk count.
     *
     * @param totalChunkCount the total chunk count
     */
    public void setTotalChunkCount(int totalChunkCount) {
        this.totalChunkCount = totalChunkCount;
    }

    /**
     * Method to get the chunk id.
     *
     * @return the chunk id
     */
    public int getChunkId() {
        return chunkId;
    }

    /**
     * Method to set the chunk id.
     *
     * @param chunkId the chunk id
     */
    public void setChunkId(int chunkId) {
        this.chunkId = chunkId;
    }
}

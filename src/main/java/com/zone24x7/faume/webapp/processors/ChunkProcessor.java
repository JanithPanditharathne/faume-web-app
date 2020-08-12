package com.zone24x7.faume.webapp.processors;

import com.zone24x7.faume.webapp.pojo.ChunkRequestMetaInfo;

/**
 * Interface for the chunk processor.
 */
public interface ChunkProcessor {
    /**
     * Method to store data chunk.
     *
     * @param chunkRequestMetaInfo the chunk request meta information.
     * @param data                 the data
     * @param correlationId        the correlation id
     */
    void storeDataChunk(ChunkRequestMetaInfo chunkRequestMetaInfo, byte[] data, String correlationId);
}

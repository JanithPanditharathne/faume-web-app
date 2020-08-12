package com.zone24x7.faume.webapp.processors;

import com.zone24x7.faume.webapp.pojo.ChunkRequestMetaInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.nio.ByteBuffer;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Implementation of chunk processor for face data.
 */
@Component
public class FaceDataChunkProcessor implements ChunkProcessor {
    private Map<String, Integer> requestChunkCountMapping = new ConcurrentHashMap<>();
    private Map<String, AtomicInteger> requestChunkTotalSizeMapping = new ConcurrentHashMap<>();
    private Map<String, Map<Integer, byte[]>> requestChunkMapping = new ConcurrentHashMap<>();

    private static final Logger LOGGER = LoggerFactory.getLogger(FaceDataChunkProcessor.class);

    /**
     * Method to store data chunk.
     *
     * @param chunkRequestMetaInfo the chunk request meta information.
     * @param data                 the data
     * @param correlationId        the correlation id
     */
    @Override
    public void storeDataChunk(ChunkRequestMetaInfo chunkRequestMetaInfo, byte[] data, String correlationId) {
        //TODO: Add validations

        String requestId = chunkRequestMetaInfo.getRequestId();
        int chunkId = chunkRequestMetaInfo.getChunkId();
        int totalChunkCount = chunkRequestMetaInfo.getTotalChunkCount();

        requestChunkCountMapping.putIfAbsent(requestId, totalChunkCount);
        requestChunkTotalSizeMapping.putIfAbsent(requestId, new AtomicInteger(0));
        requestChunkMapping.putIfAbsent(requestId, new ConcurrentSkipListMap<>());

        requestChunkTotalSizeMapping.get(requestId).addAndGet(data.length);
        requestChunkMapping.get(requestId).putIfAbsent(chunkId, data);

        LOGGER.info("[CorrelationId: {}] Chunk received requestId: {}, Total Chunks: {}, Received Chunk Id: {}", correlationId, requestId, totalChunkCount, chunkId);

        if (requestChunkCountMapping.get(requestId) == requestChunkMapping.get(requestId).size()) {
            LOGGER.info("[CorrelationId: {}] All Chunks received for requestId : {}", correlationId, requestId);

            ByteBuffer buffer = ByteBuffer.allocate(requestChunkTotalSizeMapping.get(requestId).get());

            for (Map.Entry<Integer, byte[]> entry : requestChunkMapping.get(requestId).entrySet()) {
                buffer.put(entry.getValue());
            }

            //TODO: Send the buffer to the ML backend

            // Clear the maps after the usage.
            requestChunkMapping.remove(requestId);
            requestChunkCountMapping.remove(requestId);
            requestChunkTotalSizeMapping.remove(requestId);
        }
    }
}

package com.zone24x7.faume.webapp.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Interface for file storage service
 */
public interface FilesStorageService {
    /**
     * Method to save the multipart file.
     *
     * @param file multipart file which needs to be saved
     * @param path the path to save
     */
    void save(MultipartFile file, Path path) throws IOException;

    /**
     * Method to save file when given as a byte array
     *
     * @param path        path to save
     * @param fileAsBytes the file content as byte
     * @param type        the image type
     */
    void saveImage(Path path, byte[] fileAsBytes, String type) throws IOException;
}
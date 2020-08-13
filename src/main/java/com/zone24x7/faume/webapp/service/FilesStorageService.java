package com.zone24x7.faume.webapp.service;

import java.io.IOException;
import java.nio.file.Path;
import java.util.stream.Stream;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

/**
 * Interface for file storage service
 */
public interface FilesStorageService {

    /**
     * Method to initialize file storage directory
     */
    public void init() throws IOException;

    /**
     *
     * @param file multipart file which needs to be saved
     */
    public void save(MultipartFile file) throws IOException;

    /**
     * Method to save file when given as a byte array
     *
     * @param path Path to save
     * @param file the file content as byte
     */
    public void save(Path path, byte[] file) throws IOException;

    /**
     * Method to save file when given as a byte array
     *
     * @param path Path to save
     * @param file the file content as byte
     * @param type the image type
     */
    public void saveImage(Path path, byte[] file, String type) throws IOException;

    /**
     * delete all the resources stored
     */
    public void deleteAll();
}
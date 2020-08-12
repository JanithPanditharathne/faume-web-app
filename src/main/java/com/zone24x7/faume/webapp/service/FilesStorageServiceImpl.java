package com.zone24x7.faume.webapp.service;

import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Implementation of the FileStorageService interface
 */
@Service
public class FilesStorageServiceImpl implements FilesStorageService {
    private final Path root = Paths.get("uploads");

    /**
     * Method to initialize file storage directory
     */
    @Override
    public void init() throws IOException {
        Files.createDirectory(root);
    }

    /**
     * @param file multipart file which needs to be saved
     */
    @Override
    public void save(MultipartFile file) throws IOException {
        Files.copy(file.getInputStream(), this.root.resolve(file.getName()));
    }

    /**
     * Method to save file when given as a byte array
     *
     * @param path Path to save
     * @param file the file content as byte
     */
    @Override
    public void save(Path path, byte[] file) throws IOException {
        Files.write(Paths.get("frame" + file.length + ".png"), file);
    }

    /**
     * delete all the resources stored
     */
    @Override
    public void deleteAll() {
        FileSystemUtils.deleteRecursively(root.toFile());
    }
}

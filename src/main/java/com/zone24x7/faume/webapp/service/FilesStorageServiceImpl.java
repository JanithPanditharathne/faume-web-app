package com.zone24x7.faume.webapp.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Implementation of the FileStorageService interface
 */
@Service
public class FilesStorageServiceImpl implements FilesStorageService {
    /**
     * Method to save the multipart file.
     *
     * @param file multipart file which needs to be saved
     * @param path the path to save
     */
    @Override
    public void save(MultipartFile file, Path path) throws IOException {
        Files.copy(file.getInputStream(), path);
    }

    /**
     * Method to save file when given as a byte array
     *
     * @param path        Path to save
     * @param fileAsBytes the file content as byte
     * @param type        the image type
     */
    @Override
    public void saveImage(Path path, byte[] fileAsBytes, String type) throws IOException {
        BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(fileAsBytes));
        ImageIO.write(bufferedImage, type, path.toFile());
    }
}

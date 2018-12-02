package com.trlogic.filestorage.files.storage;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * Files storage. Store files in in a directory configured as Spring parameter: file-storage.path
 */
@Service
public class FileStorage {

    private static final Logger logger = LoggerFactory.getLogger(FileStorage.class);

    private String basePath;

    public FileStorage(@Value("${file-storage.path}") String basePath) {
        Assert.notNull(basePath, "base path must not be null");
        this.basePath = basePath;
    }

    /**
     * Store file
     *
     * @param file
     *          file stream
     * @param fileName
     *          name of the file
     * @return path of the stored file
     */
    public Path store(InputStream file, String fileName) {
        Assert.notNull(file, "file must not be null");
        Assert.isTrue(!StringUtils.isEmpty(fileName), "file name must not be empty");
        try {
            Path targetPath = targetPath(fileName);
            Files.copy(file, targetPath, REPLACE_EXISTING);
            logger.info("File was stored: {}", targetPath);
            return targetPath;
        } catch (IOException ex) {
            close(file);
            throw new RuntimeException("Failed to storeImage file: " + fileName, ex);
        }
    }

    /**
     * Store image
     *
     * @param image
     *          image
     * @param fileName
     *          name of the file
     * @param format
     *          image format
     * @return path of the stored file
     */
    public Path storeImage(BufferedImage image, String fileName, String format) {
        Assert.notNull(image, "image must not be null");
        Assert.isTrue(!StringUtils.isEmpty(fileName), "file name must not be empty");
        try {
            Path targetPath = targetPath(fileName);
            OutputStream output = Files.newOutputStream(targetPath);
            ImageIO.write(image, format, output);
            output.close();
            logger.info("Image was stored: {}", targetPath);
            return targetPath;
        } catch (IOException ex) {
            throw new RuntimeException("Failed to store image file: " + fileName, ex);
        }
    }

    /**
     * Read file from file storage
     *
     * @param path
     *          file path
     * @return InputStream of the file
     */
    public InputStream readFile(Path path) {
        Assert.notNull(path, "path must not be null");
        try {
            return Files.newInputStream(path);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read file: " + path, e);
        }
    }

    private Path targetPath(String fileName) {
        return Paths.get(basePath, fileName);
    }

    private void close(InputStream file) {
        try {
            file.close();
        } catch (IOException e) {
            logger.error("Failed to close file stream", e);
        }
    }


}

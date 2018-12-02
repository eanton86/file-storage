package com.trlogic.filestorage.files.image;

import static com.trlogic.filestorage.files.FileNameUtil.newFileName;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

import javax.imageio.ImageIO;

import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.trlogic.filestorage.files.FileProcessor;
import com.trlogic.filestorage.files.command.InputStreamFile;
import com.trlogic.filestorage.files.storage.FileStorage;

/**
 * Processes files represented in InputStream
 */
@Service
public class ImageProcessor implements FileProcessor<InputStreamFile> {

    private static final Logger logger = LoggerFactory.getLogger(ImageProcessor.class);

    private final FileStorage fileStorage;

    private final ThumbnailGenerator thumbnailGenerator;

    @Autowired
    public ImageProcessor(FileStorage fileStorage, ThumbnailGenerator thumbnailGenerator) {
        this.fileStorage = fileStorage;
        this.thumbnailGenerator = thumbnailGenerator;
    }

    @Override
    public boolean process(InputStreamFile image) {
        Assert.notNull(image, "image must not be null");
        InputStream inputStream = new BufferedInputStream(image.getInputStream());
        mark(inputStream);
        checkFileIsImage(image, inputStream);
        reset(inputStream);
        Path path = fileStorage.store(inputStream, newFileName(image.getFileName()));
        IOUtils.closeQuietly(inputStream);
        generateThumbnail(path);
        return true;
    }

    private void mark(InputStream inputStream) {
        inputStream.mark(Integer.MAX_VALUE);
    }

    private void checkFileIsImage(InputStreamFile image, InputStream inputStream) {
        BufferedImage bufferedImage = asBufferedImage(inputStream);
        if (bufferedImage == null) {
            throw new IllegalStateException("Input file is not an image: " + image.getFileName());
        }
    }

    private void generateThumbnail(Path originalFilePath) {
        try {
            thumbnailGenerator.generateThumbnail(originalFilePath);
        } catch (Exception ex) {
            logger.error("Failed to generate thumbnail for image: " + originalFilePath, ex);
        }

    }

    private BufferedImage asBufferedImage(InputStream inputStream) {
        try {
            return ImageIO.read(inputStream);
        } catch (IOException e) {
            throw new IllegalStateException("Error reading image stream", e);
        }
    }

    private void reset(InputStream inputStream) {
        try {
            inputStream.reset();
        } catch (IOException e) {
            throw new IllegalStateException("Unable to reset input stream for reading twice", e);
        }
    }

}

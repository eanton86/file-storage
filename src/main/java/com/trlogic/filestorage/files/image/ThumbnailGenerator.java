package com.trlogic.filestorage.files.image;

import static com.trlogic.filestorage.files.FileNameUtil.thumbnailFileNameFor;
import static java.awt.image.BufferedImage.TYPE_INT_ARGB;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;

import javax.imageio.ImageIO;

import org.imgscalr.Scalr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.trlogic.filestorage.files.storage.FileStorage;

/**
 * Generates thumbnail for the image
 */
@Service
public class ThumbnailGenerator {

    private final FileStorage fileStorage;

    @Autowired
    public ThumbnailGenerator(FileStorage fileStorage) {
        this.fileStorage = fileStorage;
    }

    public void generateThumbnail(Path originalPath) {
        Assert.notNull(originalPath, "original image path must not be null");
        BufferedImage original = readOriginal(originalPath);
        BufferedImage thumbnail = Scalr.resize(original, 100);
        fileStorage.storeImage(thumbnail, thumbnailFileNameFor(originalPath), getThumbnailFormat(original));
    }

    private String getThumbnailFormat(BufferedImage original) {
        // for image with transparency use png for thumbnails
        return original.getColorModel().hasAlpha() ? "png" : "jpg";
    }

    private BufferedImage readOriginal(Path originalPath) {
        try {
            return ImageIO.read(fileStorage.readFile(originalPath));
        } catch (IOException e) {
            throw new RuntimeException("Failed to read original image by path: " + originalPath, e);
        }
    }

}

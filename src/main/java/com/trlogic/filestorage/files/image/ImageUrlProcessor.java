package com.trlogic.filestorage.files.image;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.regex.Pattern;

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
 * Processes images referenced by URLs
 */
@Service
public class ImageUrlProcessor implements FileProcessor<String> {

    private final static Logger logger = LoggerFactory.getLogger(ImageUrlProcessor.class);

    private final ImageProcessor imageProcessor;

    @Autowired
    public ImageUrlProcessor(ImageProcessor imageProcessor) {
        this.imageProcessor = imageProcessor;
    }

    @Override
    public boolean process(String url) {
        Assert.notNull(url, "url must not be null");
        try(InputStream inputStream = new URL(url).openStream()) {
            imageProcessor.process(new InputStreamFile(fileNameFrom(url), inputStream));
            IOUtils.closeQuietly(inputStream);
        } catch (IOException e) {
            logger.info("Failed to get file by URL: " + url);
            return false;
        }
        return true;
    }

    private String fileNameFrom(String url) {
        String lastPartOfUrl = url.substring(url.lastIndexOf('/') + 1);
        if (Pattern.matches("[\\w\\.\\+-]+", lastPartOfUrl)) {
            return lastPartOfUrl;
        }
        return "";
    }
}

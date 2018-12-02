package com.trlogic.filestorage.files.command;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.springframework.util.Assert;
import org.springframework.util.Base64Utils;

/**
 * File representation, contains file name and content
 */
public class InputStreamFile {

    private String fileName;

    private InputStream inputStream;

    public InputStreamFile(String fileName, InputStream inputStream) {
        Assert.notNull(fileName, "file name must not be null");
        Assert.notNull(inputStream, "file stream must not be null");
        this.fileName = fileName;
        this.inputStream = inputStream;
    }

    public InputStreamFile(String fileName, String fileBase64) {
        Assert.notNull(fileName, "file name must not be null");
        Assert.notNull(fileBase64, "file content must not be null");
        this.fileName = fileName;
        this.inputStream = toInputStream(fileBase64);
    }

    public String getFileName() {
        return fileName;
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    private InputStream toInputStream(String base64) {
        byte[] bytes = Base64Utils.decodeFromString(base64);
        return new ByteArrayInputStream(bytes);
    }
}

package com.trlogic.filestorage.rest;

/**
 * Base64 encoded file descriptor
 */
public class Base64EncodedFileDescriptor {

    private String fileName;

    private String contentBase64;

    public Base64EncodedFileDescriptor(String fileName, String contentBase64) {
        this.fileName = fileName;
        this.contentBase64 = contentBase64;
    }

    public Base64EncodedFileDescriptor() {
        // json
    }

    public String getFileName() {
        return fileName;
    }

    public String getContentBase64() {
        return contentBase64;
    }
}

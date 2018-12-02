package com.trlogic.filestorage.rest;

import static java.util.Collections.emptyList;

import java.util.Collections;
import java.util.List;

/**
 * Descriptor of input data: files, images, image urls
 */
public class FilesDescriptor {

    private List<Base64EncodedFileDescriptor> files;

    private List<Base64EncodedFileDescriptor> images;

    private List<String> imageUrls;

    public FilesDescriptor(List<Base64EncodedFileDescriptor> files, List<Base64EncodedFileDescriptor> images, List<String> imageUrls) {
        this.files = files;
        this.images = images;
        this.imageUrls = imageUrls;
    }

    public FilesDescriptor() {
        // json
    }

    public List<Base64EncodedFileDescriptor> getFiles() {
        return files != null ? files : emptyList();
    }

    public List<Base64EncodedFileDescriptor> getImages() {
        return images != null ? images : emptyList();
    }

    public List<String> getImageUrls() {
        return imageUrls != null ? imageUrls : emptyList();
    }
}

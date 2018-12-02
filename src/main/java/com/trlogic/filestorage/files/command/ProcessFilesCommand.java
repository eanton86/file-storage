package com.trlogic.filestorage.files.command;

import java.util.List;

import org.springframework.util.Assert;

/**
 * Upload and process files command
 */
public class ProcessFilesCommand {

    private List<InputStreamFile> files;

    private List<InputStreamFile> images;

    private List<String> imageUrls;

    public ProcessFilesCommand(List<InputStreamFile> files, List<InputStreamFile> images, List<String> imageUrls) {
        Assert.notNull(files, "file list must not be null");
        Assert.notNull(images, "image list must not be null");
        Assert.notNull(imageUrls, "URL list must not be null");
        this.files = files;
        this.images = images;
        this.imageUrls = imageUrls;
    }

    public List<InputStreamFile> getFiles() {
        return files;
    }

    public List<InputStreamFile> getImages() {
        return images;
    }

    public List<String> getImageUrls() {
        return imageUrls;
    }
}

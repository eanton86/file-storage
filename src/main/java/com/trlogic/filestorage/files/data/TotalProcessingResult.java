package com.trlogic.filestorage.files.data;

public class TotalProcessingResult {

    private FilesProcessingResult filesProcessingResult;
    private FilesProcessingResult imagesProcessingResult;
    private FilesProcessingResult imageUrlsProcessingResult;

    public TotalProcessingResult(FilesProcessingResult filesProcessingResult,
                                 FilesProcessingResult imagesProcessingResult,
                                 FilesProcessingResult imageUrlsProcessingResult) {
        this.filesProcessingResult = filesProcessingResult;
        this.imagesProcessingResult = imagesProcessingResult;
        this.imageUrlsProcessingResult = imageUrlsProcessingResult;
    }

    public TotalProcessingResult() {
        // json
    }

    public FilesProcessingResult getFilesProcessingResult() {
        return filesProcessingResult;
    }

    public FilesProcessingResult getImagesProcessingResult() {
        return imagesProcessingResult;
    }

    public FilesProcessingResult getImageUrlsProcessingResult() {
        return imageUrlsProcessingResult;
    }
}

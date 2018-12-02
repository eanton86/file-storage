package com.trlogic.filestorage.rest;

import com.trlogic.filestorage.files.data.TotalProcessingResult;

/**
 * Upload operation response
 */
public class UploadResponse {

    private String status;

    private String message;

    public UploadResponse(TotalProcessingResult result) {
        defineMessage(result);
        if (message.isEmpty()) {
            status = "SUCCESS";
        } else {
            status = "PARTIAL_FAIL";
            message = "Failed to process: " + message;
        }
    }

    private void defineMessage(TotalProcessingResult result) {
        message = "";
        if (!result.getFilesProcessingResult().isAllSucessful()) {
            message = message + result.getFilesProcessingResult().getFailedFiles() + " files; ";
        }
        if (!result.getImagesProcessingResult().isAllSucessful()) {
            message = message + result.getImagesProcessingResult().getFailedFiles() + " images; ";
        }
        if (!result.getImageUrlsProcessingResult().isAllSucessful()) {
            message = message + result.getImageUrlsProcessingResult().getFailedFiles() + " image URLs; ";
        }
    }

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}

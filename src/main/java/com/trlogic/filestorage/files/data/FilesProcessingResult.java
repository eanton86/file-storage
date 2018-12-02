package com.trlogic.filestorage.files.data;

/**
 * Result of processing files. Shows which items were failed
 */
public class FilesProcessingResult {

    private int failedFiles;

    public FilesProcessingResult(int failedFiles) {
        this.failedFiles = failedFiles;
    }

    public FilesProcessingResult() {
        // json
    }

    public int getFailedFiles() {
        return failedFiles;
    }

    public boolean isAllSucessful() {
        return failedFiles == 0;
    }
}

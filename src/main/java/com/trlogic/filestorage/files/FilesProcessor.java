package com.trlogic.filestorage.files;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.trlogic.filestorage.files.binary.BinaryFileProcessor;
import com.trlogic.filestorage.files.command.InputStreamFile;
import com.trlogic.filestorage.files.command.ProcessFilesCommand;
import com.trlogic.filestorage.files.data.FilesProcessingResult;
import com.trlogic.filestorage.files.data.TotalProcessingResult;
import com.trlogic.filestorage.files.image.ImageProcessor;
import com.trlogic.filestorage.files.image.ImageUrlProcessor;

/**
 * Process files encoded in Base64 format
 */
@Service
public class FilesProcessor {

    private static final Logger logger = LoggerFactory.getLogger(FilesProcessor.class);

    private final BinaryFileProcessor binaryFileProcessor;
    private final ImageProcessor imageProcessor;
    private final ImageUrlProcessor imageUrlProcessor;

    @Autowired
    public FilesProcessor(BinaryFileProcessor binaryFileProcessor, ImageProcessor imageProcessor, ImageUrlProcessor imageUrlProcessor) {
        this.binaryFileProcessor = binaryFileProcessor;
        this.imageProcessor = imageProcessor;
        this.imageUrlProcessor = imageUrlProcessor;
    }

    /**
     * Process files, images and images URLs. Input files and images represented as InputStream
     *
     * @return processing result
     */
    public TotalProcessingResult process(ProcessFilesCommand command) {
        Assert.notNull(command, "command must not be null");
        FilesProcessingResult filesProcessingResult = process(command.getFiles(), binaryFileProcessor);
        FilesProcessingResult imagesProcessingResult = process(command.getImages(), imageProcessor);
        FilesProcessingResult imageUrlsProcessingResult = process(command.getImageUrls(), imageUrlProcessor);
        return new TotalProcessingResult(filesProcessingResult, imagesProcessingResult, imageUrlsProcessingResult);
    }

    private <T> FilesProcessingResult process(List<T> files, FileProcessor<T> fileProcessor) {
        Assert.notNull(files, "file list must not be null");
        AtomicInteger failedFiles = new AtomicInteger(0);
        files.forEach(file -> {
            if (!processFileInternal(file, fileProcessor)) {
                failedFiles.incrementAndGet();
            }
        });
        return new FilesProcessingResult(failedFiles.intValue());
    }

    private <T> boolean processFileInternal(T file, FileProcessor<T> fileProcessor) {
        try {
            return fileProcessor.process(file);
        } catch (Exception ex) {
            logger.info("Failed to process file", ex);
            return false;
        }
    }
}

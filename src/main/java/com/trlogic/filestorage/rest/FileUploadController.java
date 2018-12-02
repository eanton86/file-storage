package com.trlogic.filestorage.rest;

import static org.springframework.http.MediaType.*;
import static org.springframework.http.ResponseEntity.ok;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.trlogic.filestorage.files.FilesProcessor;
import com.trlogic.filestorage.files.command.ProcessFilesCommand;
import com.trlogic.filestorage.files.data.TotalProcessingResult;
import com.trlogic.filestorage.files.command.InputStreamFile;

@RestController
public class FileUploadController {

    private final FilesProcessor filesProcessor;

    @Autowired
    public FileUploadController(FilesProcessor filesProcessor) {
        this.filesProcessor = filesProcessor;
    }

    @PostMapping(value = "upload-files", consumes = MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadFilesInMultipart(@RequestParam(value = "files", required = false) MultipartFile[] files,
                                                    @RequestParam(value = "images", required = false) MultipartFile[] images,
                                                    @RequestParam(value = "imageUrls", required = false) String[] imageUrls) {
        ProcessFilesCommand command =
                new ProcessFilesCommand(toInputStreamFiles(files), toInputStreamFiles(images), imageUrlList(imageUrls));
        TotalProcessingResult totalProcessingResult = filesProcessor.process(command);
        return ok(totalProcessingResult);
    }

    @PostMapping(value = "upload-files", consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<?> uploadFilesInJson(@RequestBody FilesDescriptor filesDescriptor) {
        List<String> imageUrls = filesDescriptor.getImageUrls() != null ? filesDescriptor.getImageUrls() : Collections.emptyList();
        ProcessFilesCommand command =
                new ProcessFilesCommand(toInputStreamFiles(filesDescriptor.getFiles()), toInputStreamFiles(filesDescriptor.getImages()),
                        imageUrls);
        TotalProcessingResult totalProcessingResult = filesProcessor.process(command);
        return ok(totalProcessingResult);
    }

    private List<InputStreamFile> toInputStreamFiles(List<Base64EncodedFileDescriptor> files) {
        if (files == null) {
            return Collections.emptyList();
        }
        return files.stream().map(file -> new InputStreamFile(file.getFileName(), file.getContentBase64())).collect(Collectors.toList());
    }

    private List<String> imageUrlList(String[] imageUrls) {
        return imageUrls != null ? Arrays.asList(imageUrls) : Collections.emptyList();
    }

    private List<InputStreamFile> toInputStreamFiles(MultipartFile[] files) {
        return Arrays.stream(files).map(file -> {
            try {
                return new InputStreamFile(file.getOriginalFilename(), file.getInputStream());
            } catch (IOException ex) {
                throw new IllegalArgumentException("Failed to read multipart files");
            }
        }).collect(Collectors.toList());
    }

}

package com.trlogic.filestorage.files;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.util.Lists.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;

import org.assertj.core.util.Lists;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.trlogic.filestorage.files.binary.BinaryFileProcessor;
import com.trlogic.filestorage.files.command.InputStreamFile;
import com.trlogic.filestorage.files.command.ProcessFilesCommand;
import com.trlogic.filestorage.files.data.TotalProcessingResult;
import com.trlogic.filestorage.files.image.ImageProcessor;
import com.trlogic.filestorage.files.image.ImageUrlProcessor;

@RunWith(MockitoJUnitRunner.class)
public class FilesProcessorTest {

    @Mock
    private BinaryFileProcessor binaryFileProcessor;

    @Mock
    private ImageProcessor imageProcessor;

    @Mock
    private ImageUrlProcessor imageUrlProcessor;

    @InjectMocks
    private FilesProcessor filesProcessor;

    @Test
    public void process() {
        when(binaryFileProcessor.process(any())).thenReturn(true);
        when(imageProcessor.process(any())).thenReturn(true);
        when(imageUrlProcessor.process(any())).thenReturn(true);
        InputStream inputStream = new ByteArrayInputStream(new byte[] {});

        ProcessFilesCommand command =
                command(newArrayList(new InputStreamFile("file", inputStream)), newArrayList(new InputStreamFile("image", inputStream)),
                        newArrayList("imageUrl"));
        TotalProcessingResult result = filesProcessor.process(command);

        assertThat(result.getFilesProcessingResult().getFailedFiles()).isEqualTo(0);
        assertThat(result.getImagesProcessingResult().getFailedFiles()).isEqualTo(0);
        assertThat(result.getImageUrlsProcessingResult().getFailedFiles()).isEqualTo(0);
    }

    @Test
    public void processPartialFailed() {
        InputStream inputStream = new ByteArrayInputStream(new byte[] {});
        InputStreamFile valid = new InputStreamFile("file", inputStream);
        InputStreamFile invalid = new InputStreamFile("invalid", inputStream);
        when(binaryFileProcessor.process(valid)).thenReturn(true);
        when(binaryFileProcessor.process(invalid)).thenReturn(false);
        when(imageProcessor.process(valid)).thenReturn(true);
        when(imageProcessor.process(invalid)).thenReturn(false);
        when(imageUrlProcessor.process("valid")).thenReturn(true);
        when(imageUrlProcessor.process("invalid")).thenReturn(false);

        ProcessFilesCommand command = command(newArrayList(invalid, valid), newArrayList(valid, invalid), newArrayList("invalid", "valid"));
        TotalProcessingResult result = filesProcessor.process(command);

        assertThat(result.getFilesProcessingResult().getFailedFiles()).isEqualTo(1);
        assertThat(result.getImagesProcessingResult().getFailedFiles()).isEqualTo(1);
        assertThat(result.getImageUrlsProcessingResult().getFailedFiles()).isEqualTo(1);
    }

    private ProcessFilesCommand command(List<InputStreamFile> files, List<InputStreamFile> images, List<String> imageUrls) {
        return new ProcessFilesCommand(files, images, imageUrls);
    }

}
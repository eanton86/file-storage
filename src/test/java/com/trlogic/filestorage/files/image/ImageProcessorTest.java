package com.trlogic.filestorage.files.image;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.imageio.stream.ImageInputStream;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.trlogic.filestorage.files.command.InputStreamFile;
import com.trlogic.filestorage.files.storage.FileStorage;

@RunWith(MockitoJUnitRunner.class)
public class ImageProcessorTest {

    @Mock
    private FileStorage fileStorage;

    @Mock
    private ThumbnailGenerator thumbnailGenerator;

    @InjectMocks
    private ImageProcessor imageProcessor;

    @Test
    public void process() {
        String fileName = "some-image.jpg";
        InputStream inputStream = ClassLoader.class.getResourceAsStream("/cat1.jpg");
        InputStreamFile inputStreamFile = new InputStreamFile(fileName, inputStream);
        Path path = Paths.get(fileName);
        when(fileStorage.store(any(), endsWith(fileName))).thenReturn(path);


        imageProcessor.process(inputStreamFile);

        verify(thumbnailGenerator).generateThumbnail(path);
    }
}
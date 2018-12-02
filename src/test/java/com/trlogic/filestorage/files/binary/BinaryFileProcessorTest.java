package com.trlogic.filestorage.files.binary;

import static org.mockito.Mockito.*;

import java.io.InputStream;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.trlogic.filestorage.files.FileProcessor;
import com.trlogic.filestorage.files.command.InputStreamFile;
import com.trlogic.filestorage.files.storage.FileStorage;

@RunWith(MockitoJUnitRunner.class)
public class BinaryFileProcessorTest {

    @Mock
    private FileStorage fileStorage;

    @InjectMocks
    private BinaryFileProcessor fileProcessor;

    @Test
    public void process() {
        String fileName = "some-image.jpg";
        InputStream inputStream = ClassLoader.class.getResourceAsStream("/cat1.jpg");
        InputStreamFile inputStreamFile = new InputStreamFile(fileName, inputStream);

        fileProcessor.process(inputStreamFile);

        verify(fileStorage).store(same(inputStream), endsWith(fileName));
    }

}
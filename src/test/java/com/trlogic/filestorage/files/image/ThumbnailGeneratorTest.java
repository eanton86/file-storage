package com.trlogic.filestorage.files.image;

import static org.mockito.Mockito.*;

import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.trlogic.filestorage.files.storage.FileStorage;

@RunWith(MockitoJUnitRunner.class)
public class ThumbnailGeneratorTest {

    @Mock
    private FileStorage fileStorage;

    @InjectMocks
    private ThumbnailGenerator thumbnailGenerator;

    @Test
    public void testGenerateThumbnail() {
        Path originalPath = Paths.get("/tmp/some-image.jpg");
        InputStream inputStream = ClassLoader.class.getResourceAsStream("/cat1.jpg");
        when(fileStorage.readFile(originalPath)).thenReturn(inputStream);

        thumbnailGenerator.generateThumbnail(originalPath);

        verify(fileStorage).storeImage(notNull(), eq("thumbnail__some-image.jpg"), eq("jpg"));
    }

}
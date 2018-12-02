package com.trlogic.filestorage.files.image;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

import com.trlogic.filestorage.files.command.InputStreamFile;

@RunWith(MockitoJUnitRunner.class)
public class ImageUrlProcessorTest {

    @Mock
    private ImageProcessor imageProcessor;

    @InjectMocks
    private ImageUrlProcessor imageUrlProcessor;

    @Captor
    private ArgumentCaptor<InputStreamFile> inputStreamFileArgumentCaptor;

    @Before
    public void setUp() {
        System.setProperty("java.protocol.handler.pkgs", "com.trlogic.protocols");
    }

    @Test
    public void testProcessUrl() {
        imageUrlProcessor.process("classpath:/cat1.jpg");

        verify(imageProcessor).process(inputStreamFileArgumentCaptor.capture());
        InputStreamFile inputStreamFile = inputStreamFileArgumentCaptor.getValue();
        assertThat(inputStreamFile).isNotNull();
        assertThat(inputStreamFile.getFileName()).isEqualTo("cat1.jpg");
        assertThat(inputStreamFile.getInputStream()).isNotNull();
    }

}

package com.trlogic.filestorage.rest;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.internal.util.io.IOUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.util.StreamUtils;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.util.Lists.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Collection;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.trlogic.filestorage.files.data.TotalProcessingResult;
import com.trlogic.filestorage.files.storage.FileStorage;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class FileUploadControllerIT {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private FileStorage fileStorage;

    @Captor
    private ArgumentCaptor<InputStream> inputStreamArgumentCaptor;

    @Before
    public void setUp() throws URISyntaxException, IOException {
        URL imageResource = ClassLoader.getSystemResource("cat1.jpg");
        Path path = Paths.get(imageResource.toURI());
        when(fileStorage.store(any(), any())).thenReturn(path);
        when(fileStorage.storeImage(any(), any(), any())).thenReturn(path);
        when(fileStorage.readFile(any())).thenReturn(ClassLoader.class.getResourceAsStream("/cat1.jpg"));
        System.setProperty("java.protocol.handler.pkgs", "com.trlogic.protocols");
    }

    @Test
    public void testUploadBinFile() throws Exception {
        String testFile = "test file";
        Base64EncodedFileDescriptor encodedFile = new Base64EncodedFileDescriptor("some-file.bin", encode(testFile.getBytes()));
        FilesDescriptor filesDescriptor = new FilesDescriptor(newArrayList(encodedFile), emptyList(), emptyList());
        String body = toJson(filesDescriptor);

        MvcResult mvcResult = mvc.perform(post("/upload-files").contentType(MediaType.APPLICATION_JSON).content(body))
                                 .andExpect(status().isOk())
                                 .andReturn();

        assertResultSuccessful(mvcResult);

        verify(this.fileStorage, times(1)).store(inputStreamArgumentCaptor.capture(), endsWith("some-file.bin"));
        InputStream inputStream = inputStreamArgumentCaptor.getValue();
        Collection<String> strings = IOUtil.readLines(inputStream);
        assertThat(strings).containsOnly(testFile);
    }

    @Test
    public void testUploadImage() throws Exception {
        InputStream inputStream = ClassLoader.class.getResourceAsStream("/cat1.jpg");
        byte[] bytes = StreamUtils.copyToByteArray(inputStream);
        Base64EncodedFileDescriptor encodedFile = new Base64EncodedFileDescriptor("cat1.jpg", encode(bytes));
        FilesDescriptor filesDescriptor = new FilesDescriptor(emptyList(), newArrayList(encodedFile), emptyList());
        String body = toJson(filesDescriptor);

        MvcResult mvcResult = mvc.perform(post("/upload-files").contentType(MediaType.APPLICATION_JSON).content(body))
                                 .andExpect(status().isOk())
                                 .andReturn();

        assertResultSuccessful(mvcResult);

        verify(this.fileStorage, times(1)).store(notNull(), endsWith("cat1.jpg"));
        verify(this.fileStorage, times(1)).storeImage(notNull(), eq("thumbnail__cat1.jpg"), eq("jpg"));
        inputStream.close();
    }



    @Test
    public void testUploadImageUrl() throws Exception {
        InputStream inputStream = ClassLoader.class.getResourceAsStream("/cat1.jpg");
        FilesDescriptor filesDescriptor = new FilesDescriptor(emptyList(), emptyList(), newArrayList("classpath:/cat1.jpg"));
        String body = toJson(filesDescriptor);

        MvcResult mvcResult = mvc.perform(post("/upload-files").contentType(MediaType.APPLICATION_JSON).content(body))
                                 .andExpect(status().isOk())
                                 .andReturn();

        assertResultSuccessful(mvcResult);

        verify(this.fileStorage, times(1)).store(notNull(), endsWith("cat1.jpg"));
        verify(this.fileStorage, times(1)).storeImage(notNull(), eq("thumbnail__cat1.jpg"), eq("jpg"));
        inputStream.close();
    }

    @Test
    public void testWrongResources() throws Exception {
        InputStream inputStream = ClassLoader.class.getResourceAsStream("/not-image.txt");
        byte[] bytes = StreamUtils.copyToByteArray(inputStream);
        List<Base64EncodedFileDescriptor> files =
                newArrayList(new Base64EncodedFileDescriptor("filename", "1234"), new Base64EncodedFileDescriptor("filename", "4321"));
        List<Base64EncodedFileDescriptor> images = newArrayList(new Base64EncodedFileDescriptor("not-image", encode(bytes)));
        List<String> imageUrls = newArrayList("classpath:/not-image.jpg", "classpath:/not-exist.jpg");
        FilesDescriptor filesDescriptor = new FilesDescriptor(files, images, imageUrls);
        String body = toJson(filesDescriptor);
        when(fileStorage.store(any(), endsWith("filename"))).thenThrow(new RuntimeException("Error"));

        MvcResult mvcResult = mvc.perform(post("/upload-files").contentType(MediaType.APPLICATION_JSON).content(body))
                                 .andExpect(status().isOk())
                                 .andReturn();

        String contentAsString = mvcResult.getResponse().getContentAsString();
        TotalProcessingResult result = fromJson(contentAsString, TotalProcessingResult.class);
        assertThat(result.getFilesProcessingResult().getFailedFiles()).isEqualTo(2);
        assertThat(result.getImagesProcessingResult().getFailedFiles()).isEqualTo(1);
        assertThat(result.getImageUrlsProcessingResult().getFailedFiles()).isEqualTo(2);

        inputStream.close();
    }

    private void assertResultSuccessful(MvcResult mvcResult) throws IOException {
        String contentAsString = mvcResult.getResponse().getContentAsString();
        TotalProcessingResult result = fromJson(contentAsString, TotalProcessingResult.class);
        assertThat(result.getFilesProcessingResult().getFailedFiles()).isEqualTo(0);
        assertThat(result.getImagesProcessingResult().getFailedFiles()).isEqualTo(0);
        assertThat(result.getImageUrlsProcessingResult().getFailedFiles()).isEqualTo(0);
    }

    private String toJson(Object obj) throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(obj);
    }

    private <T> T fromJson(String json, Class<T> clazz) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return objectMapper.readValue(json, clazz);
    }

    private String encode(byte[] bytes) {
        return Base64.getEncoder().encodeToString(bytes);
    }

    private DigestInputStream wrapWithDigestInputStream(InputStream inputStream) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        return new DigestInputStream(inputStream, md);
    }

}

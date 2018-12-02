package com.trlogic.filestorage.files.storage;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertTrue;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.imageio.ImageIO;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.springframework.util.StreamUtils;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class FileStorageTest {

    public static final String OUT_DIR = "./out";

    private FileStorage fileStorage = new FileStorage(OUT_DIR);

    @Test
    public void store() throws NoSuchAlgorithmException, IOException {
        String fileName = "cat1.jpg";
        InputStream inputStream = ClassLoader.class.getResourceAsStream("/" + fileName);
        DigestInputStream digestInputStream = wrapWithDigestInputStream(inputStream);
        clear();

        Path path = fileStorage.store(digestInputStream, fileName);

        assertThat(path).isNotNull();
        InputStream inputStreamResult = fileStorage.readFile(path);
        DigestInputStream digestInputStreamResult = wrapWithDigestInputStream(inputStreamResult);
        StreamUtils.copyToByteArray(digestInputStreamResult);
        assertThat(digestInputStreamResult.getMessageDigest().digest()).isEqualTo(digestInputStream.getMessageDigest().digest());
        digestInputStream.close();
        digestInputStreamResult.close();
    }

    @Test
    public void storeImage() throws IOException {
        clear();
        String fileName = "cat1.jpg";
        InputStream inputStream = ClassLoader.class.getResourceAsStream("/" + fileName);
        BufferedImage bufferedImage = ImageIO.read(inputStream);

        Path path = fileStorage.storeImage(bufferedImage, fileName, "jpg");

        assertThat(path).isNotNull();
        assertTrue(Files.exists(path));
        inputStream.close();
    }

    private void clear() throws IOException {
        Files.list(Paths.get(OUT_DIR))
             .filter(s -> s.toString().endsWith(".jpg"))
             .forEach(path -> {
                 try {
                     Files.deleteIfExists(path);
                 } catch (IOException e) {
                     throw new RuntimeException(e);
                 }
             });
    }

    private DigestInputStream wrapWithDigestInputStream(InputStream inputStream) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        return new DigestInputStream(inputStream, md);
    }

}
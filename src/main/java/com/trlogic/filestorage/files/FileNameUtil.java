package com.trlogic.filestorage.files;

import java.nio.file.Path;
import java.util.UUID;

import org.springframework.util.Assert;

public final class FileNameUtil {

    public static final String SEPARATOR = "__";

    public static String newFileName(String fileNameSuffix) {
        Assert.notNull(fileNameSuffix, "file name suffix must not be null");
        return UUID.randomUUID().toString() + SEPARATOR + fileNameSuffix;
    }

    public static String thumbnailFileNameFor(Path file) {
        Assert.notNull(file, "file must not be null");
        return "thumbnail" + SEPARATOR + file.getFileName();
    }

    private FileNameUtil() {
        // nop
    }
}

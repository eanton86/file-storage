package com.trlogic.filestorage.files.binary;

import static com.trlogic.filestorage.files.FileNameUtil.newFileName;

import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.trlogic.filestorage.files.FileProcessor;
import com.trlogic.filestorage.files.command.InputStreamFile;
import com.trlogic.filestorage.files.storage.FileStorage;

/**
 * Processes files represented in InputStream
 */
@Service
public class BinaryFileProcessor implements FileProcessor<InputStreamFile> {

    private final FileStorage fileStorage;

    @Autowired
    public BinaryFileProcessor(FileStorage fileStorage) {
        this.fileStorage = fileStorage;
    }

    @Override
    public boolean process(InputStreamFile file) {
        Assert.notNull(file, "file must not be null");
        try {
            fileStorage.store(file.getInputStream(), newFileName(file.getFileName()));
        } finally {
            IOUtils.closeQuietly(file.getInputStream());
        }
        return true;
    }
}

package com.trlogic.filestorage.files;

import java.util.List;

public interface FileProcessor<T> {

    boolean process(T file);

}

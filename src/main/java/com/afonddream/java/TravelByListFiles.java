package com.afonddream.java;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

public class TravelByListFiles extends DirectoryIterator {
    public TravelByListFiles(Path dir, Path savePath) {
        super(dir, savePath);
    }

    @Override
    void traversal() throws IOException {
        this.listFiles(getDir().toFile());
    }

    @Override
    TraversalType getTraversalType() {
        return TraversalType.JAVA_LIST_FILES;
    }

    private void listFiles(File file) {
        if (file.isFile()) {
            handleResult(file.toPath());
            return;
        }

        if (file.isDirectory()) {
            File[] fs = file.listFiles();
            if (fs == null) {
                return;
            }
            for (File f : fs) {
                listFiles(f);
            }
        }
    }
}

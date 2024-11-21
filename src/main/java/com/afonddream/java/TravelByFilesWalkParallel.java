package com.afonddream.java;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

public class TravelByFilesWalkParallel extends DirectoryIterator {
    public TravelByFilesWalkParallel(Path dir, Path savePath) {
        super(dir, savePath);
    }

    @Override
    void traversal() throws IOException {
        try (Stream<Path> stream = Files.walk(getDir()).parallel()) {
            stream.filter(Files::isRegularFile)
                .forEach(this::handleResult);
        }
    }

    @Override
    TraversalType getTraversalType() {
        return TraversalType.JAVA_FILES_PARALLEL;
    }
}
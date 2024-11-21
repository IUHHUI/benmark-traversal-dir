package com.afonddream.java;

import software.amazon.awssdk.crt.CRT;
import software.amazon.awssdk.crt.io.DirectoryTraversal;
import software.amazon.awssdk.crt.io.DirectoryTraversalHandler;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;


public class TravelBytAwsCrt extends DirectoryIterator {

    public TravelBytAwsCrt(Path dir, Path savePath) {
        super(dir, savePath);
        //invoke, for load crt library.
        CRT.getArchIdentifier();
    }

    @Override
    void traversal() throws IOException {
        DirectoryTraversalHandler handler = directoryEntry -> {
            if (directoryEntry.isFile()) {
                handleResult(Paths.get(directoryEntry.getPath()));
            }
            return true;
        };
        DirectoryTraversal.traverse(getDir().toString(), true, handler);
    }

    @Override
    TraversalType getTraversalType() {
        return TraversalType.AWS_CRT;
    }
}

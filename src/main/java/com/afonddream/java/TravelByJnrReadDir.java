package com.afonddream.java;

import com.afonddream.java.jnr.CLibrary;
import com.afonddream.java.jnr.Dirent;
import jnr.ffi.LibraryLoader;
import jnr.ffi.Pointer;
import jnr.ffi.Runtime;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;


public class TravelByJnrReadDir extends DirectoryIterator {

    private static final CLibrary CLIB;

    static {
        CLIB = LibraryLoader.create(CLibrary.class).load("c");
    }

    public TravelByJnrReadDir(Path dir, Path savePath) {
        super(dir, savePath);
    }

    @Override
    void traversal() throws IOException {
        travelDir(getDir().toString());
    }

    @Override
    TraversalType getTraversalType() {
        return TraversalType.JNR_READ_DIR;
    }

    void travelDir(String path) {
        Runtime runtime = Runtime.getRuntime(CLIB);
        Pointer dir = CLIB.opendir(path);
        if (dir == null) {
            System.err.println("Failed to open directory: " + path);
            return;
        }

        try {
            Dirent dirent = new Dirent(runtime);
            Pointer ptr;
            while ((ptr = CLIB.readdir(dir)) != null) {
                dirent.useMemory(ptr);
                String name = dirent.getDName();
                if (dirent.isRegularFile()) {
                    handleResult(Paths.get(path, name));
                } else if (dirent.isDirectory()) {
                    if (name.equals(".") || name.equals("..")) {
                        continue;
                    }
                    travelDir(Paths.get(path, name).toString());
                }
            }
        } finally {
            int result = CLIB.closedir(dir);
            if (result != 0) {
                System.err.println("Failed to close directory: " + path);
            }
        }
    }

}

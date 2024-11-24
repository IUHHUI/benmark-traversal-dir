package com.afonddream.java;

import software.amazon.awssdk.crt.CRT;
import com.afonddream.java.jnr.CTraversalLibrary;
import jnr.ffi.LibraryLoader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public abstract class TravelByJnrTraversalSo extends DirectoryIterator {
    private static final String SO_LIBRARY_NAME = "traversal";
    protected static final CTraversalLibrary C_TRAVERSAL_LIB;

    static {
        try {
            Path tmpPath = Files.createTempDirectory("TravelByJnr_" + System.currentTimeMillis());
            tmpPath.toFile().deleteOnExit();
            String SO_FIlE_NAME;
            if (CRT.getOSIdentifier().startsWith("linux")) {
                SO_FIlE_NAME = "lib" + SO_LIBRARY_NAME + ".so";
            } else {
                SO_FIlE_NAME = "lib" + SO_LIBRARY_NAME + ".dylib";
            }
            try (InputStream in = ClassLoader.getSystemResourceAsStream(SO_FIlE_NAME);) {
                assert in != null;
                Path soPath = tmpPath.resolve(SO_FIlE_NAME);
                Files.copy(in, soPath, StandardCopyOption.REPLACE_EXISTING);
                soPath.toFile().deleteOnExit();
            }
            System.setProperty("jnr.ffi.library.path", tmpPath.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        C_TRAVERSAL_LIB = LibraryLoader.create(CTraversalLibrary.class).load(SO_LIBRARY_NAME);
    }

    public TravelByJnrTraversalSo(Path dir, Path savePath) {
        super(dir, savePath);
    }
}

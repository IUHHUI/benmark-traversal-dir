package com.afonddream.java;

import java.io.IOException;
import java.nio.file.Path;


public class TravelByJnrTraversalSoSingle extends TravelByJnrTraversalSo {
    public TravelByJnrTraversalSoSingle(Path dir, Path savePath) {
        super(dir, savePath);
    }

    @Override
    void traversal() throws IOException {
        travelDir(getDir().toString());
    }

    @Override
    TraversalType getTraversalType() {
        return TraversalType.JNR_TRAVERSAL_SO_SINGLE;
    }

    void travelDir(String path) {
        C_TRAVERSAL_LIB.traversal_single_threaded(path, getSavePath().toString(), "");
    }
}

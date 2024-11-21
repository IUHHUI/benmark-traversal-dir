package com.afonddream.java;

import java.io.IOException;
import java.nio.file.Path;


public class TravelByJnrTraversalSoParallel extends TravelByJnrTraversalSo {

    public TravelByJnrTraversalSoParallel(Path dir, Path savePath) {
        super(dir, savePath);
    }

    @Override
    void traversal() throws IOException {
        travelDir(getDir().toString());
    }

    @Override
    TraversalType getTraversalType() {
        return TraversalType.JNR_TRAVERSAL_SO_PARALLEL;
    }

    void travelDir(String path) {
        C_TRAVERSAL_LIB.traversal_multi_threaded(path, getSavePath().toString(), "");
    }
}

package com.afonddream.java;

import com.afonddream.java.jnr.CLibrary;
import com.afonddream.java.jnr.Dirent;
import jnr.ffi.LibraryLoader;
import jnr.ffi.Pointer;
import jnr.ffi.Runtime;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;


public class TravelByJnrReadDirForkJoin extends DirectoryIterator {

    private static final CLibrary CLIB;

    static {
        CLIB = LibraryLoader.create(CLibrary.class).load("c");
    }

    private final ForkJoinPool pool = new ForkJoinPool(6);

    public TravelByJnrReadDirForkJoin(Path dir, Path savePath) {
        super(dir, savePath);
    }

    @Override
    void traversal() throws IOException {
        TravelTask t = new TravelTask(getDir());
        pool.invoke(t);
        t.join();
        pool.shutdown();
    }

    @Override
    TraversalType getTraversalType() {
        return TraversalType.JNR_READ_DIR_FORK_JOIN;
    }

    private class TravelTask extends RecursiveAction {
        private final Path target;

        public TravelTask(Path target) {
            this.target = target;
        }

        @Override
        protected void compute() {
            this.travelDir(target.toString());
        }

        private void travelDir(String path) {
            Runtime runtime = Runtime.getRuntime(CLIB);
            Pointer dir = CLIB.opendir(path);
            if (dir == null) {
                System.err.println("Failed to open directory: " + path);
                return;
            }

            List<TravelTask> tasks = new ArrayList<>();
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
                        TravelTask t = new TravelTask(Paths.get(path, name));
                        t.fork();
                        tasks.add(t);
                    }
                }
            } finally {
                int result = CLIB.closedir(dir);
                if (result != 0) {
                    System.err.println("Failed to close directory: " + path);
                }
            }

            for (var tk : tasks) {
                tk.join();
            }
        }
    }

}

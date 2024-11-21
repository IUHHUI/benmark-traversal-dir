package com.afonddream.java;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public abstract class DirectoryIterator {

    private enum State {
        RUNNING,
        SUCCESS,
        FAILED
    }

    private final BlockingQueue<String> queue = new ArrayBlockingQueue<>(1024);
    private volatile State state = State.RUNNING;
    private final Path dir;
    private final Path savePath;
    private final String iterateEndFlag = "EOF";
    private long start = 0L;

    public DirectoryIterator(Path dir, Path savePath) {
        this.dir = dir;
        this.savePath = savePath;
    }

    public Path getDir() {
        return dir;
    }

    public Path getSavePath() {
        return savePath;
    }

    public void doIterate() throws IOException, InterruptedException {
        Thread t = new Thread(this::consume);
        t.start();
        this.directoryIterator();
        t.join();
    }

    private void consume() {
        try (var raf = new RandomAccessFile(savePath.toFile(), "rw");
             var ch = raf.getChannel()) {
            while (this.state == State.RUNNING) {
                List<String> list = new ArrayList<>();
                int size = queue.drainTo(list);
                for (int i = 0; i < size; i++) {
                    var path = list.get(i);
                    if (path.equals(iterateEndFlag)) {
                        this.state = State.SUCCESS;
                        break;
                    }
                    ch.write(
                        ByteBuffer.wrap(
                            (path + System.lineSeparator()).getBytes(StandardCharsets.UTF_8)
                        )
                    );
                }

                if (this.state == State.RUNNING) {
                    var path = queue.take();
                    if (path.equals(iterateEndFlag)) {
                        this.state = State.SUCCESS;
                        break;
                    }
                    ch.write(
                        ByteBuffer.wrap(
                            (path + System.lineSeparator()).getBytes(StandardCharsets.UTF_8)
                        )
                    );
                }
            }
        } catch (IOException e) {
            this.state = State.FAILED;
            return;
        } catch (InterruptedException e) {
            this.state = State.FAILED;
            Thread.currentThread().interrupt();
        }

        long end = System.currentTimeMillis();
        System.out.println(this + " Time elapsed: " + (end - this.start) + "ms. result : " + this.state);
    }

    private void directoryIterator() throws IOException {
        this.start = System.currentTimeMillis();
        this.traversal();
        try {
            this.queue.put(iterateEndFlag);
        } catch (InterruptedException e) {
            this.state = State.FAILED;
            Thread.currentThread().interrupt();
        }
    }

    protected void handleResult(Path p) {
        if (this.state == State.FAILED) {
            return;
        }
        try {
            queue.put(p.toString());
        } catch (InterruptedException e) {
            this.state = State.FAILED;
            Thread.currentThread().interrupt();
        }
    }

    abstract void traversal() throws IOException;

    abstract TraversalType getTraversalType();

    @Override
    public String toString() {
        return getTraversalType().name();
    }
}

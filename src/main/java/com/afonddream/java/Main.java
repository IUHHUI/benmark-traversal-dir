package com.afonddream.java;

import software.amazon.awssdk.crt.CRT;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Locale;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {
        if (args.length != 3
            || "help".equalsIgnoreCase(args[0])
            || "-help".equalsIgnoreCase(args[0])
            || "--help".equalsIgnoreCase(args[0])
            || "-h".equalsIgnoreCase(args[0])
            || "--h".equalsIgnoreCase(args[0])) {
            printUsage();
            return;
        }
        int type = Integer.parseInt(args[0]);
        TraversalType traversalType = from(type);
        if (traversalType.name().toLowerCase(Locale.ROOT).startsWith("jnr")) {
            if (!CRT.getOSIdentifier().equalsIgnoreCase("linux")) {
                System.err.println(traversalType + " only support on linux.");
                return;
            }
        }

        String dir = args[1];
        String savePath = args[2];
        DirectoryIterator fw;
        switch (traversalType) {
            case JAVA_FILES_WALk -> fw = new TravelByFilesWalk(Paths.get(dir), Paths.get(savePath));
            case JAVA_FILES_PARALLEL -> fw = new TravelByFilesWalkParallel(Paths.get(dir), Paths.get(savePath));
            case AWS_CRT -> fw = new TravelBytAwsCrt(Paths.get(dir), Paths.get(savePath));
            case JNR_READ_DIR -> fw = new TravelByJnrReadDir(Paths.get(dir), Paths.get(savePath));
            case JNR_READ_DIR_FORK_JOIN -> fw = new TravelByJnrReadDirForkJoin(Paths.get(dir), Paths.get(savePath));
            case JNR_TRAVERSAL_SO_SINGLE -> fw = new TravelByJnrTraversalSoSingle(Paths.get(dir), Paths.get(savePath));
            case JNR_TRAVERSAL_SO_PARALLEL ->
                fw = new TravelByJnrTraversalSoParallel(Paths.get(dir), Paths.get(savePath));
            default -> fw = new TravelByListFiles(Paths.get(dir), Paths.get(savePath));
        }
        fw.doIterate();
    }

    private static void printUsage() {
        System.out.println("Usage: java -jar <jarfile> type <targetDir> <outputPath>");
        System.out.println("\ttype:");
        var types = TraversalType.values();
        for (var t : types) {
            System.out.println("\t\t" + t.ordinal() + "\t" + t.name());
        }
    }

    private static TraversalType from(int ordinal) {
        var types = TraversalType.values();
        if (ordinal >= types.length || ordinal < 0) {
            return TraversalType.JAVA_LIST_FILES;
        }
        return types[ordinal];
    }
}
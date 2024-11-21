## target

Test recursively traversing folders in different ways.

## need

* java >= 17
* make
* maven >= 3.6

## package

``` shell
mvn clean package
```

## run

``` shell
java -jar target/benmark-traversal-dir-1.0-SNAPSHOT-jar-with-dependencies.jar

Usage: java -jar <jarfile> type <dir> <savePath>
        type:
                0       JAVA_LIST_FILES
                1       JAVA_FILES_WALk
                2       JAVA_FILES_PARALLEL
                3       AWS_CRT
                4       JNR_READ_DIR
                5       JNR_READ_DIR_FORK_JOIN
```

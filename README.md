## 目标

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
                6       JNR_TRAVERSAL_SO_SINGLE
                7       JNR_TRAVERSAL_SO_PARALLEL
```

## example
``` shell
 #简单的测试脚本
cat bench.sh

for i in $(seq 0 7) ; do
        echo 3 > /proc/sys/vm/drop_caches
        sync
        sleep 2
        rm -f  "/meta/data${i}";
        java -jar /tmp/benmark-traversal-dir-1.0-SNAPSHOT-jar-with-dependencies.jar "${i}" /data/ "/meta/data${i}";
done

 #测试环境数据量
df -h /data

Filesystem                 Size  Used Avail Use% Mounted on
/dev/mapper/dbf_vg-dbf_lv   22T   20T  2.3T  90% /data


 #执行测试脚本
sh bench.sh

JAVA_LIST_FILES Time elapsed: 1217766ms. result : SUCCESS
JAVA_FILES_WALk Time elapsed: 1233453ms. result : SUCCESS
JAVA_FILES_PARALLEL Time elapsed: 1165995ms. result : SUCCESS
AWS_CRT Time elapsed: 1403918ms. result : SUCCESS
JNR_READ_DIR Time elapsed: 100373ms. result : SUCCESS
JNR_READ_DIR_FORK_JOIN Time elapsed: 36833ms. result : SUCCESS
JNR_TRAVERSAL_SO_SINGLE Time elapsed: 113996ms. result : SUCCESS
JNR_TRAVERSAL_SO_PARALLEL Time elapsed: 38974ms. result : SUCCESS
```

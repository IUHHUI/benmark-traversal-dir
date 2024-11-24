#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <pthread.h>
#include <dirent.h>
#include <stdatomic.h>
#include "single_threaded_traversal.h"

static char *QUEUE_END_FLAG = "EOF";

void producer_travel_dir0(TravelArg *task, char *dirPath, DIR *dir)
{
    struct dirent *entry;
    while ((entry = readdir(dir)) != NULL)
    {
        if (is_travel_fail(task))
        {
            return;
        }

        if (entry->d_type == DT_REG)
        {
            int len = strlen(dirPath) + strlen(entry->d_name) + 3;
            char *filename = malloc(len);
            snprintf(filename, len, "%s/%s", dirPath, entry->d_name);
            queue_enqueue(task->queue, filename);
            continue;
        }
        else if (entry->d_type == DT_DIR)
        {
            if (strcmp(entry->d_name, ".") != 0 && strcmp(entry->d_name, "..") != 0)
            {
                producer_travel_dir(dirPath, entry->d_name, task);
            }
        }
    }
}

void producer_travel_dir(char *parent, char *dirName, TravelArg *task)
{
    char *dirPath;
    if (strcmp(dirName, "/") == 0)
    {
        dirPath = strdup(parent);
    }
    else
    {
        int len = strlen(parent) + strlen(dirName) + 2;
        dirPath = malloc(len);
        snprintf(dirPath, len, "%s/%s", parent, dirName);
    }

    DIR *dir = opendir(dirPath);
    if (dir == NULL)
    {
        perror("Failed to open directory");
        perror(dirPath);

        set_travel_fail(task);
        free(dirPath);
        return;
    }

    producer_travel_dir0(task, dirPath, dir);

    closedir(dir);
    free(dirPath);
}

// 生产者线程函数
static void *producer(void *arg)
{
    TravelArg *t = (TravelArg *)arg;
    producer_travel_dir(t->path, "/", t);

    queue_enqueue(t->queue, strdup(QUEUE_END_FLAG));
    return NULL;
}

// 消费者线程函数
static void *consumer(void *arg)
{
    TravelArg *t = (TravelArg *)arg;
    FILE *output = fopen(t->output, "a+");
    if (output == NULL)
    {
        set_travel_fail(t);
        perror("Failed to open output file");
        return NULL;
    }
    while (1)
    {
        char *filename = queue_dequeue(t->queue);
        if (strcmp(filename, QUEUE_END_FLAG) == 0)
        {
            free(filename);
            break;
        }
        else if (is_endwith(filename, t->suffix_matcher))
        {
            fprintf(output, "%s\n", filename);
            free(filename);
        }

        if (is_travel_fail(t))
        {
            return NULL;
        }
    }
    fclose(output);
    return NULL;
}

int traversal_single_threaded(char *path, char *output, char *suffix_matcher)
{
    atomic_bool state;
    atomic_init(&state, true);

    Queue queue;
    queue_init(&queue);

    TravelArg travel_arg;
    (&travel_arg)->path = path;
    (&travel_arg)->output = output;
    (&travel_arg)->suffix_matcher = suffix_matcher;
    (&travel_arg)->queue = &queue;
    (&travel_arg)->state = &state;

    pthread_t producer_thread;
    pthread_create(&producer_thread, NULL, producer, &travel_arg);

    pthread_t consumer_threads;
    pthread_create(&consumer_threads, NULL, consumer, &travel_arg);

    pthread_join(producer_thread, NULL);
    pthread_join(consumer_threads, NULL);

    queue_destroy(&queue);

    return is_travel_fail(&travel_arg) ? 1 : 0;
}

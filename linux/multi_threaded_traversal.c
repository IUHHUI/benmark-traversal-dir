#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <pthread.h>
#include <unistd.h> 
#include "multi_threaded_traversal.h"

#define THREAD_POOL_SIZE 6

typedef struct
{
    pthread_t thread_id;
    TravelArg *travel_arg;
    char *dirPath;
    Queue *queue_sub_dir;
} IterDirTask;

IterDirTask iter_task_queue[THREAD_POOL_SIZE];
int iter_task_queue_size = 0;
pthread_mutex_t iter_task_queue_mutex = PTHREAD_MUTEX_INITIALIZER;
pthread_cond_t iter_task_queue_not_empty = PTHREAD_COND_INITIALIZER;
pthread_cond_t iter_task_queue_not_full = PTHREAD_COND_INITIALIZER;

// 生产者线程函数
void *producer_iter_dir(void *arg)
{
    IterDirTask *task = (IterDirTask *)arg;
    char *dirPath = task->dirPath;
    DIR *dir = opendir(dirPath);
    if (dir == NULL)
    {
        perror("Failed to open directory");
        perror(dirPath);
        return NULL;
    }
    struct dirent *entry;
    while ((entry = readdir(dir)) != NULL)
    {
        if (entry->d_type == DT_REG)
        {
            int fullPathLen = strlen(dirPath) + strlen(entry->d_name) + 20;
            char *fullPath = malloc(fullPathLen);
            snprintf(fullPath, fullPathLen, "%s/%s", dirPath, entry->d_name);
            //for output.
            queue_enqueue(task->travel_arg->queue, fullPath);
            continue;
        }
        else if (entry->d_type == DT_DIR)
        {
            if (strcmp(entry->d_name, ".") != 0 && strcmp(entry->d_name, "..") != 0)
            {
                int fullPathLen = strlen(dirPath) + strlen(entry->d_name) + 2;
                char *fullPath = malloc(fullPathLen);
                snprintf(fullPath, fullPathLen, "%s/%s", dirPath, entry->d_name);
                //for start new task
                queue_enqueue(task->queue_sub_dir, fullPath);
            }
        }
    }
    closedir(dir);

    pthread_mutex_lock(&iter_task_queue_mutex);
    iter_task_queue_size--;
    task->thread_id=0;
    queue_enqueue(task->queue_sub_dir, "EOF");
    pthread_cond_signal(&iter_task_queue_not_full);
    pthread_mutex_unlock(&iter_task_queue_mutex);
    return NULL;
}

void producer_add_task(char *dirPath, Queue *queue_sub_dir, TravelArg *travel_arg)
{
    pthread_mutex_lock(&iter_task_queue_mutex);
    while (iter_task_queue_size >= THREAD_POOL_SIZE)
    {
	    pthread_cond_wait(&iter_task_queue_not_full, &iter_task_queue_mutex);
    }

    iter_task_queue_size++;
    IterDirTask *new_task;
    for (size_t i = 0; i < THREAD_POOL_SIZE; i++)
    {
        IterDirTask *task = &iter_task_queue[i];
        if (task->thread_id == 0)
        {
		new_task=task;
		break;
        }
    }
    new_task->dirPath = strdup(dirPath);
    new_task->queue_sub_dir = queue_sub_dir;
    new_task->travel_arg = travel_arg;
    pthread_create(&new_task->thread_id, NULL, producer_iter_dir, new_task);
    pthread_mutex_unlock(&iter_task_queue_mutex);
}

// 生产者线程函数
static void *producer(void *arg) {
    TravelArg *t = (TravelArg *)arg;
    Queue q_sub_dir;
    queue_init(&q_sub_dir);
    queue_enqueue(&q_sub_dir, t->path);

    int eof_count=0;
    int task_count=0;

    while (1) {
        char *s = queue_dequeue(&q_sub_dir);
        if (strcmp(s, "EOF") == 0)
        {
            eof_count++;
            if (task_count > 0 && task_count == eof_count)
            {
                break;
            }
            continue;
        }else
        {
            task_count++;
            producer_add_task(s, &q_sub_dir, t);
        }
    }

    queue_destroy(&q_sub_dir);

    // stop consumer
    queue_enqueue(t->queue, "EOF");
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
        if (strcmp(filename, "EOF") == 0)
        {
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

int traversal_multi_threaded(char *path, char *output, char *suffix_matcher)
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

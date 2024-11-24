#ifndef __TRAVERSAL_DIR_BLOCK_QUEUE_H
#define __TRAVERSAL_DIR_BLOCK_QUEUE_H

#include <stdlib.h>
#include <pthread.h>

// 队列节点结构体
typedef struct QueueNode {
    char *data;
    struct QueueNode *next;
} QueueNode;

// 队列结构体
typedef struct {
    QueueNode *head;
    QueueNode *tail;
    pthread_mutex_t mutex;
    pthread_cond_t cond;
} Queue;

// 初始化队列
void queue_init(Queue *queue);
// 销毁队列
void queue_destroy(Queue *queue);
// 入队
void queue_enqueue(Queue *queue, char *data);
// 出队
char *queue_dequeue(Queue *queue);

#endif
#ifndef __TRAVERSAL_DIR_TASK_ARG_H
#define __TRAVERSAL_DIR_TASK_ARG_H

#include <stdbool.h>
#include <stdatomic.h>
#include <string.h>
#include "block_queue.h"

typedef struct
{
    char *path;
    char *output;
    char *suffix_matcher;
    Queue *queue;
    atomic_bool *state;
} TravelArg;

void set_travel_fail(TravelArg *t);
bool is_travel_fail(TravelArg *t);
bool is_endwith(char *filename, char *suffix_matcher);

#endif
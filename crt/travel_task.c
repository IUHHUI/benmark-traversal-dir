#include "travel_task.h"

void set_travel_fail(TravelArg *t)
{
    atomic_store(t->state, false);
}

bool is_travel_fail(TravelArg *t)
{
    return !atomic_load(t->state);
}

bool is_endwith(char *filename, char *suffix_matcher)
{
    if (suffix_matcher == NULL)
    {
        return true;
    }
    int len = strlen(filename);
    if(len == 0)
    {
        return true;
    }
    int suffix_len = strlen(suffix_matcher);
    if (len < suffix_len)
    {
        return false;
    }
    return strcmp(filename + len - suffix_len, suffix_matcher) == 0;
}
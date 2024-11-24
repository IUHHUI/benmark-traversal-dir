
#include <dirent.h>
#include "travel_task.h"

void producer_travel_dir0(TravelArg *task, char *dirPath, DIR *dir);
void producer_travel_dir(char *parent, char *dirName, TravelArg *task);
int traversal_single_threaded(char *path, char *output, char *suffix_matcher);
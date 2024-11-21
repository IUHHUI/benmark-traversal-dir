SRCS := block_queue.c travel_task.c single_threaded_traversal.c multi_threaded_traversal.c
HDRS := block_queue.h travel_task.h single_threaded_traversal.h multi_threaded_traversal.h
CFLAGS := ${CFLAGS} -Wall -Wextra -Werror
LIB_SO := libtraversal.so

ifeq (${DEBUG}, 1)
    CFLAGS += -g
    CFLAGS += -DDEBUG=1
endif

all: build_so

build_so: ${SRCS} ${HDRS}
	@echo "Building ${LIB_SO}..."
	@echo "---> DEBUG : ${DEBUG} (0:release, 1:debug)"
	@echo "---> SRCS: ${SRCS}"
	@echo "---> HDRS: ${HDRS}"

	gcc ${CFLAGS} -fPIC -shared -o ${LIB_SO} ${INCLUDES} ${LDPATH} -lpthread ${SRCS}

clean:
	@echo "Cleaning up ${LIB_SO}..."
	rm -f *.so

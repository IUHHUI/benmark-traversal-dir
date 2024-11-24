SRCS := block_queue.c travel_task.c single_threaded_traversal.c multi_threaded_traversal.c
HDRS := block_queue.h travel_task.h single_threaded_traversal.h multi_threaded_traversal.h
CFLAGS := ${CFLAGS} -Wall -Wextra -Werror

ifeq (${DEBUG}, 1)
    CFLAGS += -g
    CFLAGS += -DDEBUG=1
endif

# 检测操作系统
UNAME_S := $(shell uname -s 2>/dev/null)
ifeq ($(UNAME_S), Darwin)
    LIB_SO := libtraversal.dylib
else ifeq ($(UNAME_S), Linux)
    LIB_SO := libtraversal.so
else
    $(error This platform is not supported)
endif

all: build_so

build_so: ${SRCS} ${HDRS}
	@echo "Building ${LIB_SO} on ${UNAME_S} ..."
	@echo "---> DEBUG : ${DEBUG} (0:release, 1:debug)"
	@echo "---> SRCS: ${SRCS}"
	@echo "---> HDRS: ${HDRS}"

	gcc ${CFLAGS} -fPIC -shared -o ${LIB_SO} ${INCLUDES} ${LDPATH} -lpthread ${SRCS}

clean:
	@echo "Cleaning up ${LIB_SO}..."
	rm -f *.so

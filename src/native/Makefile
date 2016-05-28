TARGET := libglvideo.so
OBJS := impl.o
CC := gcc
PLATFORM := $(shell uname -s)


CFLAGS := -fPIC -O2 -mtune=native
ifeq ($(PLATFORM),Linux)
	CFLAGS += -I$(shell dirname $(shell realpath $(shell which javac)))/../include
	CFLAGS += -I$(shell dirname $(shell realpath $(shell which javac)))/../include/linux
	CFLAGS += -I/opt/vc/include
	CFLAGS += -I/opt/vc/include/interface/vcos/pthreads
	CFLAGS += $(shell pkg-config gstreamer-1.0 --cflags-only-I)
endif

LDFLAGS := -shared
ifeq ($(PLATFORM),Linux)
	LDFLAGS += -L/opt/vc/lib
	LDFLAGS += $(shell pkg-config gstreamer-1.0 --libs)
	LDFLAGS += $(shell pkg-config gstreamer-gl-1.0 --libs)
	LDFLAGS += -L../../library/linux-armv6hf
	LDFLAGS += -Wl,-R,'$$ORIGIN'
endif

$(TARGET): $(OBJS)
	$(CC) $(CFLAGS) $(LDFLAGS) $^ -o $@
	mkdir -p ../../library/linux-armv6hf
	cp $(TARGET) ../../library/linux-armv6hf

iface.h:
	javah -classpath .. -o iface.h gohai.glvideo.GLVideo

clean:
	rm -f $(TARGET) $(OBJS)

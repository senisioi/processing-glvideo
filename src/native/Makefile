TARGET := libglvideo.so
OBJS := impl.o
CC := gcc
PLATFORM := $(shell uname -s)


CFLAGS := -fPIC -g
ifeq ($(PLATFORM),Linux)
	# TODO: derive from javac location
	CFLAGS += -I/usr/lib/jvm/jdk-8-oracle-arm32-vfp-hflt/include
	CFLAGS += -I/usr/lib/jvm/jdk-8-oracle-arm32-vfp-hflt/include/linux
	CFLAGS += -I/opt/vc/include
	CFLAGS += -I/opt/vc/include/interface/vcos/pthreads
	CFLAGS += $(shell pkg-config gstreamer-1.0 --cflags-only-I)
endif

LDFLAGS := -shared
ifeq ($(PLATFORM),Linux)
	LDFLAGS += -L/opt/vc/lib
	LDFLAGS += $(shell pkg-config gstreamer-1.0 --libs)
	LDFLAGS += $(shell pkg-config gstreamer-gl-1.0 --libs)
endif

$(TARGET): $(OBJS)
	$(CC) $(CFLAGS) $(LDFLAGS) $^ -o $@

iface.h:
	javah -classpath .. -o iface.h processing.glvideo.GLVideo

clean:
	rm -f $(TARGET) $(OBJS)

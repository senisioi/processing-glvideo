TARGET := libglvideo.so
OBJS := impl.o
CC := gcc
PLATFORM := $(shell uname -s)
RPI := $(shell test -e /opt/vc/include; echo $$?)


CFLAGS := -fPIC -g -O2 -ffast-math
ifeq ($(RPI),0)
	# Raspberry Pi
	CFLAGS += -DGLES2 -DRPI
	CFLAGS += -march=armv6zk -mcpu=arm1176jzf-s -mfloat-abi=hard -mfpu=vfp
	CFLAGS += -I$(shell dirname $(shell realpath $(shell which javac)))/../include
	CFLAGS += -I$(shell dirname $(shell realpath $(shell which javac)))/../include/linux
	CFLAGS += -I/opt/vc/include
	CFLAGS += -I/opt/vc/include/interface/vcos/pthreads
	CFLAGS += $(shell pkg-config gstreamer-1.0 --cflags-only-I)
	CFLAGS += $(shell pkg-config gstreamer-gl-1.0 --cflags-only-I)
else ifeq ($(PLATFORM),Linux)
	# regular Linux
	CFLAGS += -I$(shell dirname $(shell realpath $(shell which javac)))/../include
	CFLAGS += -I$(shell dirname $(shell realpath $(shell which javac)))/../include/linux
	CFLAGS += $(shell pkg-config gstreamer-1.0 --cflags-only-I)
	CFLAGS += $(shell pkg-config gstreamer-gl-1.0 --cflags-only-I)
else ifeq ($(PLATFORM),Darwin)
	# this is currently 64-bit only
	# download and install latest gstreamer-1.0-devel package for x86_64 from
	# https://gstreamer.freedesktop.org/data/pkg/osx/
	CFLAGS += -I/Library/Frameworks/GStreamer.framework/Versions/1.0/Headers
	CFLAGS += -I/System/Library/Frameworks/JavaVM.framework/Versions/Current/Headers
endif

LDFLAGS := -shared
ifeq ($(RPI),0)
	# Raspberry Pi
	LDFLAGS += -L/opt/vc/lib
	LDFLAGS += $(shell pkg-config gstreamer-1.0 --libs)
	LDFLAGS += $(shell pkg-config gstreamer-gl-1.0 --libs)
	LDFLAGS += -L../../library/linux-armv6hf
	LDFLAGS += -Wl,-R,'$$ORIGIN'
	TARGET_DIR = linux-armv6hf
	TARGET_FILE = $(TARGET)
else ifeq ($(PLATFORM),Linux)
	# regular Linux
	LDFLAGS += $(shell pkg-config gstreamer-1.0 --libs)
	LDFLAGS += $(shell pkg-config gstreamer-gl-1.0 --libs)
	# pkg-config for gstreamer-gl-1.0 on Fedora pulls in a lot of unrelated dependencies, e.g. wayland
	# try this instead
	# LDFLAGS += -lgstgl-1.0 -lGL
	TARGET_DIR = linux64
	TARGET_FILE = $(TARGET)
else ifeq ($(PLATFORM),Darwin)
	# this is currently 64-bit only
	LDFLAGS += -L../../library/macosx
	LDFLAGS += -lgstgl-1.0.0 -lgstreamer-1.0.0 -lgstapp-1.0.0 -lglib-2.0.0 -lgobject-2.0.0
	TARGET_DIR = macosx
	# extension can't be .so on OS X
	LDFLAGS += -install_name @loader_path/libglvideo.jnilib
	TARGET_FILE = libglvideo.jnilib
endif

$(TARGET): $(OBJS)
	$(CC) $(CFLAGS) $^ -o $@ $(LDFLAGS)
	mkdir -p ../../library/$(TARGET_DIR)
	cp $(TARGET) ../../library/$(TARGET_DIR)/$(TARGET_FILE)

iface.h:
	javah -classpath .. -o iface.h gohai.glvideo.GLVideo

clean:
	rm -f $(TARGET) $(OBJS)

install_macosx_libraries: $(TARGET)
	rm -rf ../../library/macosx/*
	# download and install latest gstreamer-1.0 devel package for x86_64 from
	# https://gstreamer.freedesktop.org/data/pkg/osx/
	mkdir -p ../../library/macosx
	cp /Library/Frameworks/GStreamer.framework/Libraries/*.dylib ../../library/macosx
	# pip install osxrelocator
	cd ../../library/macosx && osxrelocator . "/Library/Frameworks/GStreamer.framework/Versions/1.0/lib" "@loader_path/" && cd ../../src/native
	# seems to have a bug, so this is needed to set the desired install name
	cd ../../library/macosx && find * -name "*.dylib" -exec install_name_tool -id @loader_path/{} {} \; && cd ../../src/native
	mkdir -p ../../library/macosx/gstreamer-1.0
	cp /Library/Frameworks/GStreamer.framework/Libraries/gstreamer-1.0/*.so ../../library/macosx/gstreamer-1.0
	cd ../../library/macosx/gstreamer-1.0 && osxrelocator . "/Library/Frameworks/GStreamer.framework/Versions/1.0/lib" "@loader_path/../" && cd ../../../src/native
	# install name is not relevant for those
	# libglvideo.so is required for the python script below
	cp libglvideo.so ../../library/macosx/libglvideo.jnilib
	python ./macosx_remove_extra_libs.py
	# silence runtime error
	rm -f ../../library/macosx/gstreamer-1.0/libgstopenjpeg.so

.PHONY: iface.h clean install_macosx_libraries

## Complie gstreamer
To have this working properly, it is essential to compile the right gstreamer locally and recompile the library from scratch.
Install the prerequisites:
```bash
sudo apt-get install autoconf autopoint bison flex gettext libasound2-dev libglib2.0-dev libpulse-dev libtool
```
Clone and install gstreamer and plugins (optionally, you can provide a `--prefix=/usr/local` to every autogen.sh command to install gstreamer in a speciffic directory):
```bash
git clone --recursive git://anongit.freedesktop.org/gstreamer/gstreamer
cd gstreamer
git checkout tags/1.12.0
git submodule update
./autogen.sh --disable-debug --disable-gtk-doc
time make -j$(nproc)
sudo make install
cd ..

# depends on libglib-2.0.so

git clone --recursive git://anongit.freedesktop.org/gstreamer/gst-plugins-base
cd gst-plugins-base
git checkout tags/1.12.0
git submodule update
./autogen.sh --disable-debug --disable-gtk-doc
time make -j$(nproc)
sudo make install
cd ..

git clone --recursive git://anongit.freedesktop.org/gstreamer/gst-plugins-good
cd gst-plugins-good
git checkout tags/1.12.0
git submodule update
./autogen.sh --disable-debug --disable-gtk-doc --disable-libpng --disable-oss --disable-oss4
time make -j$(nproc)
sudo make install
cd ..

git clone --recursive git://anongit.freedesktop.org/gstreamer/gst-plugins-bad
cd gst-plugins-bad
git checkout tags/1.12.0
git submodule update
./autogen.sh --disable-debug --disable-decklink --disable-dvb --disable-fbdev --disable-gtk-doc --disable-x11 --disable-opengl --disable-glx --enable-gles2 --disable-vcd --disable-wayland --with-gles2-module-name=/opt/vc/lib/libGLESv2.so --with-egl-module-name=/opt/vc/lib/libEGL.so
time make -j$(nproc)
sudo make install
cd ..

git clone https://github.com/FFmpeg/gas-preprocessor.git
cd gas-preprocessor
chmod a+x gas-preprocessor.pl
sudo cp gas-preprocessor.pl /usr/local/bin
cd ..

git clone --recursive git://anongit.freedesktop.org/gstreamer/gst-libav
cd gst-libav
git checkout tags/1.12.0
git submodule update
AS=gcc ./autogen.sh --disable-debug --disable-gtk-doc
time make -j$(nproc)
sudo make install
cd ..

git clone https://github.com/GStreamer/gstreamer-vaapi.git
git checkout tags/1.12.0
git submodule update
./autogen.sh --disable-debug --disable-gtk-doc
time make -j$(nproc)
sudo make install
cd ..
```

## Installing
1. Clone this repository in the same directory where processing is installed. The `build.xml` file is hardcoded for `processing-3.3.6`.

2. Compile the library:
```bash
cd src/native
make
cd ../..
ant clean && ant && ant dist
```

## OpenGL video playback for Processing

This library makes use of GStreamer and OpenGL hardware (or software) acceleration to display video in Processing's P2D or P3D renderers. It should run on macOS, Linux and Raspbian.

### Linux

Install the GStreamer 1.x software from your distribution's repositories. The actual packages might be named differently between distributions, but you want the the equivalents of:

`gstreamer1.0 gstreamer1.0-plugins-base gstreamer1.0-plugins-good gstreamer1.0-gst-plugins-bad` and either `gstreamer1.0-ffmpeg` or `gstreamer1.0-libav`

Things might not work if the gstreamer version is too recent so it's better to just compile it from scratch. 
You could encounter crashes, the video might not run slick or even the following error:
```
OpenGL error 1282 at bot endDraw()
```

Additional plugins, such as `omx` or `vaapi`, could additionally unlock hardware-accelerated decoding, but it is advised to install those from the source.

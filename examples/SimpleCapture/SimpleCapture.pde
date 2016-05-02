/**
 *  Please note that the code for interfacing with Capture devices
 *	will change in future releases of this library. This is just a
 *	filler till something more permanent becomes available.
 */

import gohai.glvideo.GLVideo;
GLVideo video;

void setup() {
  size(320, 240, P2D);
  // this will use the first recognized camera
  video = new GLVideo(this, "v4l2:///dev/video0");
  video.play();
}

void draw() {
  if (video.available()) {
    video.read();
  }
  image(video, 0, 0, width, height);
}

/**
 *  you might need to increase your GPU memory, to avoid:
 *  OpenGL error 1285 at top endDraw(): out of memory
 *
 *  test.h264 is a 1080p25 video file with H.264 Main Profile Level 3.0,
 *  no audio track
 */

import gohai.glvideo.GLVideo;
GLVideo video;

void setup() {
  size(1280, 720, P2D);
  noCursor();
  video = new GLVideo(this, "/opt/vc/src/hello_pi/hello_video/test.h264");
  video.loop();
}

void draw() {
  if (video.available()) {
    video.read();
    image(video, 0, 0, width, height);
  }
}

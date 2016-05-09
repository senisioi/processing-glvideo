/**
 *  Video Edge Detect, based on Edge Detect example
 *
 *  you might need to increase your GPU memory, to avoid:
 *  OpenGL error 1285 at top endDraw(): out of memory
 */

import gohai.glvideo.GLVideo;

GLVideo video;
PShader edges;
boolean enabled = true;

void setup() {
  size(560, 406, P2D);
  video = new GLVideo(this, "launch2.mp4");
  video.loop();
  edges = loadShader("edges.glsl");
}

void draw() {
  if (video.available()) {
  	video.read();
  }
  if (enabled == true) {
    shader(edges);
  }
  image(video, 0, 0, width, height);
}

void mousePressed() {
  enabled = !enabled;
  if (!enabled == true) {
    resetShader();
  }
}

/**
 *	Video Edge Detect, based on Edge Detect example
 *
 *  place this file in the sketch's data folder
 *  http://download.blender.org/peach/bigbuckbunny_movies/big_buck_bunny_480p_h264.mov
 *
 *  you might need to increase your GPU memory, to avoid:
 *  OpenGL error 1285 at top endDraw(): out of memory
 */

import gohai.glvideo.GLVideo;

GLVideo video;
PShader edges;
boolean enabled = true;

void setup() {
  size(427, 240, P2D);
  video = new GLVideo(this, "big_buck_bunny_480p_h264.mov");
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

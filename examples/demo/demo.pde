// place this file in the sketch's data folder
// http://download.blender.org/peach/bigbuckbunny_movies/big_buck_bunny_480p_h264.mov

import processing.glvideo.GLVideo;
GLVideo video;

void setup() {
  size(400, 400, P3D);
  video = new GLVideo(this, "big_buck_bunny_480p_h264.mov");
  frameRate(24);
}

void draw() {
  if (video.available()) {
    int tex = video.getFrame();
    PGL pgl = beginPGL();
    pgl.drawTexture(PGL.TEXTURE_2D, tex, width, height,
                    0, 0, width, height);
    endPGL();
  }
}

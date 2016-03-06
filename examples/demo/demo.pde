// place this file in the sketch's data folder
// http://download.blender.org/peach/bigbuckbunny_movies/big_buck_bunny_480p_h264.mov

import processing.glvideo.GLVideo;
GLVideo video;

void setup() {
  size(400, 400, P3D);
  video = new GLVideo(this, "big_buck_bunny_480p_h264.mov");
  video.play();
}

void draw() {
  if (video.playing() && video.available()) {
    int tex = video.getFrame();
    PGL pgl = beginPGL();
    pgl.drawTexture(PGL.TEXTURE_2D,
                    tex,
                    width, height,        // texture size
                    0, 0, width, height,  // viewport
                    0, height, width, 0,  // texture coords
                    0, 0, width, width);  // screen coords the texture
                                          // coords are mapped onto
    endPGL();
  }
}

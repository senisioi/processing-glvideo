/**
 *  place this file in the sketch's data folder
 *  http://download.blender.org/peach/bigbuckbunny_movies/big_buck_bunny_480p_h264.mov
 *
 *  you might need to increase your GPU memory, to avoid:
 *  OpenGL error 1285 at top endDraw(): out of memory
 */

import gohai.glvideo.GLVideo;
GLVideo video1;
GLVideo video2;

void setup() {
  size(427, 240, P3D);
  video1 = new GLVideo(this, "big_buck_bunny_480p_h264.mov", GLVideo.MUTE);
  video2 = new GLVideo(this, "big_buck_bunny_480p_h264.mov", GLVideo.MUTE);
  video2.jump(5.0);
  video1.play();
  video2.play();
}

void draw() {
  if (video1.playing() && video1.available()) {
    video1.read();
    image(video1, 0, 0, width/2, height);
  }
  if (video2.playing() && video2.available()) {
    video2.read();
    image(video2, width/2, 0, width/2, height);
  }
}

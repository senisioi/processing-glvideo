// place this file in the sketch's data folder
// http://download.blender.org/peach/bigbuckbunny_movies/big_buck_bunny_480p_h264.mov

import gohai.glvideo.GLVideo;
GLVideo video;

void setup() {
  size(427, 240, P3D);
  video = new GLVideo(this, "big_buck_bunny_480p_h264.mov");
  video.play();
}

void draw() {
  if (video.playing() && video.available()) {
    video.read();
    image(video, 0, 0, width, height);
  }
}

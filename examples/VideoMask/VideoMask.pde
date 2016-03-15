/**
 *  Video Mask, based on Image Mask example
 *  move the mouse to reveal the video through the dynamic mask
 *
 *  place this file in the sketch's data folder
 *  http://download.blender.org/peach/bigbuckbunny_movies/big_buck_bunny_480p_h264.mov
 */

import gohai.glvideo.GLVideo;

GLVideo video;
PShader maskShader;
PGraphics maskImage;

void setup() {
  size(427, 240, P2D);
  video = new GLVideo(this, "big_buck_bunny_480p_h264.mov");
  video.loop();
  maskImage = createGraphics(video.width(), video.height(), P2D);
  maskImage.noSmooth();
  maskShader = loadShader("mask.glsl");
  maskShader.set("mask", maskImage);
  background(255);
}

void draw() {
  if (video.available()) {
    video.read();
  }

  maskImage.beginDraw();
  if (mousePressed || frameCount == 1) {
    maskImage.background(0);
  }
  if (mouseX != 0 && mouseY != 0) {
    maskImage.noStroke();
    maskImage.fill(255, 0, 0);
    maskImage.ellipse(mouseX, mouseY, 50, 50);
  }
  maskImage.endDraw();

  shader(maskShader);
  image(video, 0, 0, width, height);
}

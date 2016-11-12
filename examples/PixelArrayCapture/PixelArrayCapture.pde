/**
 *  Please note that the code for interfacing with Capture devices
 *  will change in future releases of this library. This is just a
 *  filler till something more permanent becomes available.
 *
 *  For use with the Raspberry Pi camera, make sure the camera is
 *  enabled in the Raspberry Pi Configuration tool and add the line
 *  "bcm2835_v4l2" (without quotation marks) to the file
 *  /etc/modules. After a restart you should be able to see the
 *  camera device as /dev/video0.
 */

import gohai.glvideo.*;
GLCapture video;

void setup() {
  size(320, 240, P2D);
  noStroke();

  // this will use the first recognized camera
  video = new GLCapture(this);
  video.play();
}

void draw() {
  background(0);
  if (video.available()) {
    video.read();
  }
  image(video, 0, 0, width, height);

  color center;
  center = video.get(video.width/2, video.height/2);
  fill(center);
  ellipse(width/2, height/2, 125, 125);

  // alternatively, the pixel value can also be read like this:
  //if (0 < video.width) {
  //  video.loadPixels();
  //  center = video.pixels[video.height/2*video.width + video.width/2];
  //}
}

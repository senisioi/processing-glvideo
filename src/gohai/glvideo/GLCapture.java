/* -*- mode: java; c-basic-offset: 2; indent-tabs-mode: nil -*- */

/*
  Copyright (c) The Processing Foundation 2016
  Developed by Gottfried Haider

  This library is free software; you can redistribute it and/or
  modify it under the terms of the GNU Lesser General Public
  License as published by the Free Software Foundation; either
  version 2.1 of the License, or (at your option) any later version.

  This library is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
  Lesser General Public License for more details.

  You should have received a copy of the GNU Lesser General
  Public License along with this library; if not, write to the
  Free Software Foundation, Inc., 59 Temple Place, Suite 330,
  Boston, MA  02111-1307  USA
*/

package gohai.glvideo;

import processing.core.*;

/**
 *  @webref
 */
public class GLCapture extends GLVideo {

  protected static String[][] devices;

  public GLCapture(PApplet parent) {
    super(parent, 0);

    // open the first capture device
    if (devices == null) {
      devices = gstreamer_getDevices("Video/Source");
    }

    if (devices.length == 0) {
      throw new RuntimeException("No capture devices found");
    }

    handle = gstreamer_open_device(devices[0][0], 0);
    if (handle == 0) {
      throw new RuntimeException("Could not open capture device " + devices[0][0]);
    } else {
      throw new RuntimeException("Pipeline bringup not implemented yet");
    }
  }

  public GLCapture(PApplet parent, int index) {
    super(parent, 0);
    String pipeline;

    if (PApplet.platform == LINUX) {
      pipeline = "v4l2src device=/dev/video";
    } else if (PApplet.platform == MACOSX) {
      pipeline = "qtkitvideosrc device-index=";
    } else {
      throw new RuntimeException("Currently not supported on Windows");
    }

    handle = gstreamer_open_pipeline(pipeline + index, 0);
    if (handle == 0) {
      throw new RuntimeException("Could not open capture device");
    }
  }

  public GLCapture(PApplet parent, String deviceName) {
    super(parent, 0);

    handle = gstreamer_open_device(deviceName, 0);
    if (handle == 0) {
      throw new RuntimeException("Could not open capture device " + deviceName);
    } else {
      throw new RuntimeException("Pipeline bringup not implemented yet");
    }
  }

  public static String[] list() {
    // make sure the library is loaded
    loadGStreamer();
    // re-fetch the devices list, even if we have it already
    devices = gstreamer_getDevices("Video/Source");

    String[] device_names = new String[devices.length];
    for (int i=0; i < devices.length; i++) {
      device_names[i] = devices[i][0];
    }
    return device_names;
  }

  public static String[] configs(String deviceName) {
    if (devices == null) {
      loadGStreamer();
      devices = gstreamer_getDevices("Video/Source");
    }

    for (int i=0; i < devices.length; i++) {
      if (devices[i][0].equals(deviceName)) {
        if (devices[i][2].equals("")) {
          return new String[0];
        } else {
          return devices[i][2].split("; ");
        }
      }
    }

    return null;
  }
}

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
import java.util.ArrayList;

/**
 *  @webref
 */
public class GLCapture extends GLVideo {

  protected static String[][] devices;

  public GLCapture(PApplet parent) {
    super(parent, 0);

    if (devices == null) {
      devices = gstreamer_getDevices();
    }

    if (devices.length == 0) {
      throw new RuntimeException("No capture devices found");
    }

    // this is using whatever config GStreamer hands us as the default
    handle = gstreamer_openDevice(devices[0][0], "video/x-raw", 0);
    if (handle == 0) {
      throw new RuntimeException("Could not open capture device " + devices[0][0]);
    }
  }

  public GLCapture(PApplet parent, String deviceName) {
    super(parent, 0);

    if (devices == null) {
      devices = gstreamer_getDevices();
    }

    for (int i=0; i < devices.length; i++) {
      if (devices[i][0].equals(deviceName)) {

        // this is using whatever config GStreamer hands us as the default
        handle = gstreamer_openDevice(devices[i][0], "video/x-raw", 0);
        if (handle == 0) {
          throw new RuntimeException("Could not open capture device " + devices[i][0]);
        }

        return;
      }
    }

    throw new RuntimeException("Cannot find capture device " + deviceName);
  }

  public GLCapture(PApplet parent, String deviceName, float fps) {
    // XXX: this picks e.g. 640x480 for 25 fps
    this(parent, deviceName, "video/x-raw, framerate=" + fpsToFramerate(fps));
  }

  public GLCapture(PApplet parent, String deviceName, int width, int height) {
    // XXX: this picks e.g. 10 fps for 1280x720
    this(parent, deviceName, "video/x-raw, width=" + width + ", height=" + height);
  }

  public GLCapture(PApplet parent, String deviceName, int width, int height, float fps) {
    this(parent, deviceName, "video/x-raw, width=" + width + ", height=" + height + ", framerate=" + fpsToFramerate(fps));
  }

  public GLCapture(PApplet parent, String deviceName, String config) {
    super(parent, 0);

    handle = gstreamer_openDevice(deviceName, config, 0);
    if (handle == 0) {
      throw new RuntimeException("Could not open capture device " + deviceName);
    }
  }

  public static String[] list() {
    // make sure the library is loaded
    loadGStreamer();
    // re-fetch the devices list, even if we have it already
    devices = gstreamer_getDevices();

    // XXX: is the device name guaranteed to be unique?
    String[] device_names = new String[devices.length];
    for (int i=0; i < devices.length; i++) {
      device_names[i] = devices[i][0];
    }
    return device_names;
  }

  public static String[] configs(String deviceName) {
    if (devices == null) {
      loadGStreamer();
      devices = gstreamer_getDevices();
    }

    for (int i=0; i < devices.length; i++) {
      if (deviceName.equals(devices[i][0])) {
        if (devices[i][2] == null || devices[i][2].length() == 0) {
          return new String[0];
        } else {
          String[] lines = filterCaps(devices[i][2].split("; "));
          System.out.println("The format returned by configs() is informational only and might change in future versions of the library.");
          return lines;
        }
      }
    }

    throw new RuntimeException("Cannot find capture device " + deviceName);
  }

  protected static String[] filterCaps(String in[]) {
    ArrayList<String> filtered = new ArrayList<String>();
    String needle;

    // the first format should be the most preferable (presumably native) one
    // return all caps with the same format
    if (in.length == 0) {
      return new String[]{};
    } else {
      needle = in[0].substring(0, in[0].indexOf(',', in[0].indexOf(',') + 1) + 1);
    }

    for (int i=0; i < in.length; i++) {
      if (in[i].startsWith(needle)) {
        filtered.add(in[i]);
      }
    }
    return filtered.toArray(new String[filtered.size()]);
  }

  public static float framerateToFps(String fraction) {
    int i = fraction.indexOf('/');
    if (i != -1) {
      float num = Float.parseFloat(fraction.substring(0, i));
      float denom = Float.parseFloat(fraction.substring(i+1));
      return num / denom;
    } else {
      throw new RuntimeException("Unexpected argument " + fraction);
    }
  }

  public static String fpsToFramerate(float fps) {
    String formatted = Float.toString(fps);
    // this presumes the delimitter is always a dot
    int i = formatted.indexOf('.');
    if (Math.floor(fps) != fps) {
      int denom = (int)Math.pow(10, formatted.length()-i-1);
      int num = (int)(fps * denom);
      return num + "/" + denom;
    } else {
      return (int)fps + "/1";
    }
  }

  public void start() {
    // emulate the start method of the original Video library
    play();
  }
}

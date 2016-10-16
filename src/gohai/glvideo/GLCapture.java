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

    // open the first capture device
    if (PApplet.platform == LINUX) {
      if (devices == null) {
        devices = gstreamer_getDevices("Video/Source");
      }

      if (devices.length == 0) {
        throw new RuntimeException("No capture devices found");
      }

      handle = gstreamer_open_device(devices[0][0], 0);
      if (handle == 0) {
        throw new RuntimeException("Could not open capture device " + devices[0][0]);
      }
    } else if (PApplet.platform == MACOSX) {
      handle = gstreamer_open_pipeline("qtkitvideosrc device-index=0", 0);
      if (handle == 0) {
        throw new RuntimeException("Could not open capture device");
      }
    } else {
      throw new RuntimeException("Currently not supported on Windows");
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


  static class Caps implements Cloneable {

    String full_caps;
    String mime;
    String format;
    int width;
    int height;
    String interlace_mode;
    float fps;

    public Object clone() throws CloneNotSupportedException {
      return super.clone();
    }

    public String toString() {
      return width + "x" + height + "@" + fps;
    }


    public static ArrayList<Caps> getCaps(String s) {
      ArrayList<Caps> caps = new ArrayList<Caps>();
      Caps cap = new Caps();
      caps.add(cap);

      cap.full_caps = s;

      // mime type
      cap.mime = s.substring(0, s.indexOf(", "));

      int i = s.indexOf("format=(string)");
      if (i != -1) {
        int j = s.indexOf(", ", i);
        if (j != -1) {
          cap.format = s.substring(i+15, j);
        } else {
          cap.format = s.substring(i+15);
        }
      }

      i = s.indexOf("width=(int)");
      if (i != -1) {
        int j = s.indexOf(", ", i);
        if (j != -1) {
          cap.width = Integer.parseInt(s.substring(i+11, j));
        } else {
          cap.width = Integer.parseInt(s.substring(i+11));
        }
      }

      i = s.indexOf("height=(int)");
      if (i != -1) {
        int j = s.indexOf(", ", i);
        if (j != -1) {
          cap.height = Integer.parseInt(s.substring(i+12, j));
        } else {
          cap.height = Integer.parseInt(s.substring(i+12));
        }
      }

      i = s.indexOf("interlace-mode=(string)");
      if (i != -1) {
        int j = s.indexOf(", ", i);
        if (j != -1) {
          cap.interlace_mode = s.substring(i+23, j);
        } else {
          cap.interlace_mode = s.substring(i+23);
        }
      }

      i = s.indexOf("framerate=(fraction)");
      if (i != -1) {
        String[] temp = null;
        if (s.charAt(i+20) == '{') {
          // handle array of values
          int j = s.indexOf("}, ", i);
          if (j != -1) {
            temp = s.substring(i+22, j-1).split(", ");
          } else {
            temp = s.substring(i+22, s.length()-2).split(", ");
          }
        } else {
          int j = s.indexOf(", ", i);
          temp = new String[1];
          if (j != -1) {
            temp[0] = s.substring(i+20, j);
          } else {
            temp[0] = s.substring(i+20);
          }
        }

        for (i=0; i < temp.length; i++) {
          // create a new Caps instance for each framerate
          if (0 < i) {
            try {
              cap = (Caps)cap.clone();
            } catch (CloneNotSupportedException e) {
            }
            caps.add(cap);
          }

          // also convert fraction to float
          int j = temp[i].indexOf('/');
          if (j != -1) {
            float num = Float.parseFloat(temp[i].substring(0, j));
            float denom = Float.parseFloat(temp[i].substring(j+1));
            cap.fps = num / denom;
          }
        }
      }

      return caps;
    }
  }
}

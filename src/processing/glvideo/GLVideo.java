/* -*- mode: java; c-basic-offset: 2; indent-tabs-mode: nil -*- */

/*
  Copyright (c) The Processing Foundation 2016
  Developed by Gottfried Haider & Andres Colubri

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

package processing.glvideo;

import java.io.File;
import processing.core.*;

public class GLVideo {

  protected static boolean loaded = false;
  protected static boolean error = false;

  protected PApplet parent;
  protected long handle = 0;

  public GLVideo(PApplet parent, String fn) {
    super();
    this.parent = parent;

    if (!loaded) {
      System.loadLibrary("glvideo");
      loaded = true;
      if (gstreamer_init() == false) {
        error = true;
      }
    }

    if (error) {
      throw new RuntimeException("Could not load gstreamer");
    }

    // get absolute path for fn
    if (fn.indexOf("://") != -1) {
      // got URI, use as is
    } else {
      // first, check Processing's dataPath
      File file = new File(parent.dataPath(fn));
      if (file.exists() == false) {
        // next, the current directory
        file = new File(fn);
      }
      if (file.exists()) {
        fn = file.getAbsolutePath();
      }
    }

    handle = gstreamer_open("foo", fn);
    if (handle == 0) {
      throw new RuntimeException("Could not load video");
    }
  }

  public boolean available() {
    if (handle == 0) {
      return false;
    } else {
      return gstreamer_available(handle);
    }
  }

  public int getFrame() {
    return gstreamer_getFrame(handle);
  }

  public void close() {
    if (handle != 0) {
      gstreamer_close(handle);
      handle = 0;
    }
  }


  private static native boolean gstreamer_init();
  private native long gstreamer_open(String pipeline, String fn);
  private native boolean gstreamer_available(long handle);
  private native int gstreamer_getFrame(long handle);
  private native void gstreamer_close(long handle);
}

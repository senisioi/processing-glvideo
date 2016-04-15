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

import gohai.glvideo.PerspectiveTransform;
import java.awt.geom.Point2D;

/**
 *  @webref
 */
public class WarpPerspective {

  protected PerspectiveTransform transform;

  /**
   *  Class for calculating the perspective transformation for a point
   *  @param transform PerspectiveTransform instance to use
   */
  public WarpPerspective(PerspectiveTransform transform) {
    this.transform = transform;
  }

  /**
   *  Calculate the transformation for a point
   *  @param point input (Point2D)
   *  @return Point2D output
   */
  public Point2D mapDestPoint(Point2D point) {
    return transform.transform(point, null);
  }

  /**
   *  Calculate the transformation for a point
   *  @param x X coordinate of input point
   *  @param y Y coordinate of input point
   *  @return Point2D output
   */
  public Point2D mapDestPoint(float x, float y) {
    return transform.transform(x, y);
  }
}

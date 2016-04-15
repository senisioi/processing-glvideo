/* -*- mode: java; c-basic-offset: 2; indent-tabs-mode: nil -*- */

/*
  Copyright (c) WiimoteTUIO 2008
  Copyright (c) The Processing Foundation 2016
  Ported to Java, and made to fit to javax.media.jai by Gottfried Haider

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

import java.awt.geom.Point2D;

/**
 *  @webref
 */
public class PerspectiveTransform {

  protected float[] srcX = new float[4];
  protected float[] srcY = new float[4];
  protected float[] dstX = new float[4];
  protected float[] dstY = new float[4];
  protected float[] srcMat = new float[16];
  protected float[] dstMat = new float[16];
  protected float[] warpMat = new float[16];
  protected boolean dirty;

  /**
   *  Return the perspective transformation between two quads.
   *  The points (x/y0 to x/y3 and x/y0p to x/y3p) are assigned clockwise, starting
   *  from the top-left corner, followed by the top-right corner, etc.
   *  @param x0 X coordinate of the top left corner of the source quad
   *  @param y0 Y coordinate of the top left corner of the source quad
   *  @param x0p X coordinate of the top left corner of the destination quad
   *  @param x0p Y coordinate of the top left corner of the destination quad
   *  @return PerspectiveTransform instance that can be used as an argument to WarpPerspective()
   */
  public static PerspectiveTransform getQuadToQuad(float x0, float y0,    // top left
                                                   float x1, float y1,    // top right
                                                   float x2, float y2,    // bottom right
                                                   float x3, float y3,    // bottom left
                                                   float x0p, float y0p,
                                                   float x1p, float y1p,
                                                   float x2p, float y2p,
                                                   float x3p, float y3p) {
    PerspectiveTransform transform = new PerspectiveTransform(x0, y0,
                                                              x1, y1,
                                                              x2, y2,
                                                              x3, y3,
                                                              x0p, y0p,
                                                              x1p, y1p,
                                                              x2p, y2p,
                                                              x3p, y3p);
    return transform;
  }


  public PerspectiveTransform() {
    setIdentity();
  }

  /**
   *  Class for calculating the perspective transformation between two quads.
   *  The points (x/y0 to x/y3 and x/y0p to x/y3p) are assigned clockwise, starting
   *  from the top-left corner, followed by the top-right corner, etc.
   *  @param x0 X coordinate of the top left corner of the source quad
   *  @param y0 Y coordinate of the top left corner of the source quad
   *  @param x0p X coordinate of the top left corner of the destination quad
   *  @param x0p Y coordinate of the top left corner of the destination quad
   */
  public PerspectiveTransform(float x0, float y0,
                              float x1, float y1,
                              float x2, float y2,
                              float x3, float y3,
                              float x0p, float y0p,
                              float x1p, float y1p,
                              float x2p, float y2p,
                              float x3p, float y3p) {
    setSource     (x0, y0,
                   x1, y1,
                   x2, y2,
                   x3, y3);
    setDestination(x0p, y0p,
                   x1p, y1p,
                   x2p, y2p,
                   x3p, y3p);
  }

  public Point2D transform(Point2D point, Point2D dest) {
    float[] tmp = warp((float)point.getX(), (float)point.getY());

    if (dest != null) {
      dest.setLocation(tmp[0], tmp[1]);
    }

    return new Point2D.Float(tmp[0], tmp[1]);
  }

  public Point2D transform(float x, float y) {
    float[] tmp = warp(x, y);
    return new Point2D.Float(tmp[0], tmp[1]);
  }

  protected void setIdentity() {
    setSource     (0.0f, 0.0f,
                   1.0f, 0.0f,
                   0.0f, 1.0f,
                   1.0f, 1.0f);
    setDestination(0.0f, 0.0f,
                   1.0f, 0.0f,
                   0.0f, 1.0f,
                   1.0f, 1.0f);
    // WiimoteTUIO would computeWarp() here, rather than computing it on-demand
  }

  protected void setSource(float x0, float y0,
                           float x1, float y1,
                           float x2, float y2,
                           float x3, float y3) {
    srcX[0] = x0;
    srcY[0] = y0;
    srcX[1] = x1;
    srcY[1] = y1;
    srcX[2] = x2;
    srcY[2] = y2;
    srcX[3] = x3;
    srcY[3] = y3;
    dirty = true;
  }

  protected void setDestination(float x0, float y0,
                                float x1, float y1,
                                float x2, float y2,
                                float x3, float y3) {
    dstX[0] = x0;
    dstY[0] = y0;
    dstX[1] = x1;
    dstY[1] = y1;
    dstX[2] = x2;
    dstY[2] = y2;
    dstX[3] = x3;
    dstY[3] = y3;
    dirty = true;
  }

  protected void computeWarp() {
    computeQuadToSquare(srcX[0], srcY[0],
                        srcX[1], srcY[1],
                        srcX[2], srcY[2],
                        srcX[3], srcY[3],
                        srcMat);
    computeSquareToQuad(dstX[0], dstY[0],
                        dstX[1], dstY[1],
                        dstX[2], dstY[2],
                        dstX[3], dstY[3],
                        dstMat);
    multMats(srcMat, dstMat, warpMat);
    dirty = false;
  }

  protected void multMats(float[] srcMat, float[] dstMat, float[] resMat) {
    // DSTDO/CBB: could be faster, but not called often enough to matter
    for (int r = 0; r < 4; r++) {
      int ri = r * 4;
      for (int c = 0; c < 4; c++) {
        resMat[ri + c] = (srcMat[ri    ] * dstMat[c     ] +
                          srcMat[ri + 1] * dstMat[c +  4] +
                          srcMat[ri + 2] * dstMat[c +  8] +
                          srcMat[ri + 3] * dstMat[c + 12]);
      }
    }
  }

  protected void computeSquareToQuad(float x0, float y0,
                                     float x1, float y1,
                                     float x2, float y2,
                                     float x3, float y3,
                                     float[] mat) {
    float dx1 = x1 - x2,  dy1 = y1 - y2;
    float dx2 = x3 - x2,  dy2 = y3 - y2;
    float sx = x0 - x1 + x2 - x3;
    float sy = y0 - y1 + y2 - y3;
    float g = (sx * dy2 - dx2 * sy) / (dx1 * dy2 - dx2 * dy1);
    float h = (dx1 * sy - sx * dy1) / (dx1 * dy2 - dx2 * dy1);
    float a = x1 - x0 + g * x1;
    float b = x3 - x0 + h * x3;
    float c = x0;
    float d = y1 - y0 + g * y1;
    float e = y3 - y0 + h * y3;
    float f = y0;

    mat[ 0] = a;  mat[ 1] = d;  mat[ 2] = 0;  mat[ 3] = g;
    mat[ 4] = b;  mat[ 5] = e;  mat[ 6] = 0;  mat[ 7] = h;
    mat[ 8] = 0;  mat[ 9] = 0;  mat[10] = 1;  mat[11] = 0;
    mat[12] = c;  mat[13] = f;  mat[14] = 0;  mat[15] = 1;
  }

  protected void computeQuadToSquare(float x0, float y0,
                                     float x1, float y1,
                                     float x2, float y2,
                                     float x3, float y3,
                                     float[] mat) {
    computeSquareToQuad(x0, y0, x1, y1, x2, y2, x3, y3, mat);

    // invert through adjoint
    float a = mat[ 0],  d = mat[ 1],  /* ignore */    g = mat[ 3];
    float b = mat[ 4],  e = mat[ 5],  /* 3rd col*/    h = mat[ 7];
    /* ignore 3rd row */
    float c = mat[12],  f = mat[13];

    float A =     e - f * h;
    float B = c * h - b;
    float C = b * f - c * e;
    float D = f * g - d;
    float E =     a - c * g;
    float F = c * d - a * f;
    float G = d * h - e * g;
    float H = b * g - a * h;
    float I = a * e - b * d;

    // Probably unnecessary since 'I' is also scaled by the determinant,
    //   and 'I' scales the homogeneous coordinate, which, in turn,
    //   scales the X,Y coordinates.
    // Determinant  =   a * (e - f * h) + b * (f * g - d) + c * (d * h - e * g);
    float idet = 1.0f / (a * A           + b * D           + c * G);

    mat[ 0] = A * idet; mat[ 1] = D * idet; mat[ 2] = 0;  mat[ 3] = G * idet;
    mat[ 4] = B * idet; mat[ 5] = E * idet; mat[ 6] = 0;  mat[ 7] = H * idet;
    mat[ 8] = 0       ; mat[ 9] = 0       ; mat[10] = 1;  mat[11] = 0       ;
    mat[12] = C * idet; mat[13] = F * idet; mat[14] = 0;  mat[15] = I * idet;
  }

  protected float[] getWarpMatrix() {
    // WiimoteTUIO would return the matrix regardless if warpMat was current or not
    if (dirty) {
      computeWarp();
    }
    return warpMat;
  }

  protected float[] warp(float srcX, float srcY) {
    if (dirty) {
      computeWarp();
    }
    return warp(warpMat, srcX, srcY);
  }

  protected static float[] warp(float[] mat, float srcX, float srcY) {
    float[] result = new float[4];
    float z = 0;
    result[0] = (float)(srcX * mat[0] + srcY*mat[4] + z*mat[8]  + 1*mat[12]);
    result[1] = (float)(srcX * mat[1] + srcY*mat[5] + z*mat[9]  + 1*mat[13]);
    result[2] = (float)(srcX * mat[2] + srcY*mat[6] + z*mat[10] + 1*mat[14]);
    result[3] = (float)(srcX * mat[3] + srcY*mat[7] + z*mat[11] + 1*mat[15]);
    float[] dst = new float[2];
    dst[0] = result[0]/result[3];   // x
    dst[1] = result[1]/result[3];   // y
    return dst;
  }
}

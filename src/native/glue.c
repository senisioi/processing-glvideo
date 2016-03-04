#include "glue.h"
#include "iface.h"

JNIEXPORT jboolean JNICALL Java_processing_glvideo_GLVideo_gstreamer_1init
  (JNIEnv *env, jclass cls) {
  	return JNI_TRUE;
  }

JNIEXPORT jlong JNICALL Java_processing_glvideo_GLVideo_gstreamer_1open
  (JNIEnv *env, jobject obj, jstring fn_or_uri) {
  	return 1;
  }

JNIEXPORT jboolean JNICALL Java_processing_glvideo_GLVideo_gstreamer_1available
  (JNIEnv *env, jobject obj, jlong handle) {
  	return JNI_FALSE;
  }

JNIEXPORT jint JNICALL Java_processing_glvideo_GLVideo_gstreamer_1getFrame
  (JNIEnv *env, jobject obj, jlong handle) {
  	return 0;
  }

JNIEXPORT void JNICALL Java_processing_glvideo_GLVideo_gstreamer_1close
  (JNIEnv *env, jobject obj, jlong handle) {

  }

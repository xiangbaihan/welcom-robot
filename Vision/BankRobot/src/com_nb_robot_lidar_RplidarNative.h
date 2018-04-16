/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class com_nb_robot_lidar_RplidarNative */

#ifndef _Included_com_nb_robot_lidar_RplidarNative
#define _Included_com_nb_robot_lidar_RplidarNative
#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     com_nb_robot_lidar_RplidarNative
 * Method:    hello
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_com_nb_robot_lidar_RplidarNative_hello
  (JNIEnv *, jobject);

/*
 * Class:     com_nb_robot_lidar_RplidarNative
 * Method:    createDriver
 * Signature: ()J
 */
JNIEXPORT jlong JNICALL Java_com_nb_robot_lidar_RplidarNative_createDriver
  (JNIEnv *, jobject);

/*
 * Class:     com_nb_robot_lidar_RplidarNative
 * Method:    connect
 * Signature: (JLjava/lang/String;)Z
 */
JNIEXPORT jboolean JNICALL Java_com_nb_robot_lidar_RplidarNative_connect
  (JNIEnv *, jobject, jlong, jstring);

/*
 * Class:     com_nb_robot_lidar_RplidarNative
 * Method:    disposeDriver
 * Signature: (J)V
 */
JNIEXPORT void JNICALL Java_com_nb_robot_lidar_RplidarNative_disposeDriver
  (JNIEnv *, jobject, jlong);

/*
 * Class:     com_nb_robot_lidar_RplidarNative
 * Method:    checkHealth
 * Signature: (J)Z
 */
JNIEXPORT jboolean JNICALL Java_com_nb_robot_lidar_RplidarNative_checkHealth
  (JNIEnv *, jobject, jlong);

/*
 * Class:     com_nb_robot_lidar_RplidarNative
 * Method:    startScan
 * Signature: (J)V
 */
JNIEXPORT void JNICALL Java_com_nb_robot_lidar_RplidarNative_startScan
  (JNIEnv *, jobject, jlong);

/*
 * Class:     com_nb_robot_lidar_RplidarNative
 * Method:    stopScan
 * Signature: (J)V
 */
JNIEXPORT void JNICALL Java_com_nb_robot_lidar_RplidarNative_stopScan
  (JNIEnv *, jobject, jlong);

/*
 * Class:     com_nb_robot_lidar_RplidarNative
 * Method:    getScanData
 * Signature: (J)[Lcom/nb/robot/lidar/MeasurementNode;
 */
JNIEXPORT jobjectArray JNICALL Java_com_nb_robot_lidar_RplidarNative_getScanData
  (JNIEnv *, jobject, jlong);

#ifdef __cplusplus
}
#endif
#endif

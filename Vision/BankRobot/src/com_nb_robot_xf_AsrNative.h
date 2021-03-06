/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class com_nb_robot_xf_AsrNative */

#ifndef _Included_com_nb_robot_xf_AsrNative
#define _Included_com_nb_robot_xf_AsrNative
#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     com_nb_robot_xf_AsrNative
 * Method:    hello
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_com_nb_robot_xf_AsrNative_hello
  (JNIEnv *, jobject);

/*
 * Class:     com_nb_robot_xf_AsrNative
 * Method:    login
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_com_nb_robot_xf_AsrNative_login
  (JNIEnv *, jobject);

/*
 * Class:     com_nb_robot_xf_AsrNative
 * Method:    logout
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_com_nb_robot_xf_AsrNative_logout
  (JNIEnv *, jobject);

/*
 * Class:     com_nb_robot_xf_AsrNative
 * Method:    buildGrammar
 * Signature: (Ljava/lang/String;)Lcom/nb/robot/xf/UserData;
 */
JNIEXPORT jobject JNICALL Java_com_nb_robot_xf_AsrNative_buildGrammar
  (JNIEnv *, jobject, jstring);

/*
 * Class:     com_nb_robot_xf_AsrNative
 * Method:    updateLexicon
 * Signature: (Lcom/nb/robot/xf/UserData;Ljava/lang/String;Ljava/lang/String;)Lcom/nb/robot/xf/UserData;
 */
JNIEXPORT jobject JNICALL Java_com_nb_robot_xf_AsrNative_updateLexicon
  (JNIEnv *, jobject, jobject, jstring, jstring);

/*
 * Class:     com_nb_robot_xf_AsrNative
 * Method:    runAsr
 * Signature: (Lcom/nb/robot/xf/UserData;)Lcom/nb/robot/xf/UserData;
 */
JNIEXPORT jobject JNICALL Java_com_nb_robot_xf_AsrNative_runAsr
  (JNIEnv *, jobject, jobject);

#ifdef __cplusplus
}
#endif
#endif

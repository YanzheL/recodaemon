/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class org_tensorflow_TensorFlow */

#ifndef _Included_org_tensorflow_TensorFlow
#define _Included_org_tensorflow_TensorFlow
#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     org_tensorflow_TensorFlow
 * Method:    version
 * Signature: ()Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_org_tensorflow_TensorFlow_version
  (JNIEnv *, jclass);

/*
 * Class:     org_tensorflow_TensorFlow
 * Method:    registeredOpList
 * Signature: ()[B
 */
JNIEXPORT jbyteArray JNICALL Java_org_tensorflow_TensorFlow_registeredOpList
  (JNIEnv *, jclass);

/*
 * Class:     org_tensorflow_TensorFlow
 * Method:    libraryLoad
 * Signature: (Ljava/lang/String;)J
 */
JNIEXPORT jlong JNICALL Java_org_tensorflow_TensorFlow_libraryLoad
  (JNIEnv *, jclass, jstring);

/*
 * Class:     org_tensorflow_TensorFlow
 * Method:    libraryDelete
 * Signature: (J)V
 */
JNIEXPORT void JNICALL Java_org_tensorflow_TensorFlow_libraryDelete
  (JNIEnv *, jclass, jlong);

/*
 * Class:     org_tensorflow_TensorFlow
 * Method:    libraryOpList
 * Signature: (J)[B
 */
JNIEXPORT jbyteArray JNICALL Java_org_tensorflow_TensorFlow_libraryOpList
  (JNIEnv *, jclass, jlong);

#ifdef __cplusplus
}
#endif
#endif

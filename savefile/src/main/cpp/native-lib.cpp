#include <jni.h>
#include <string>
#include <stdio.h>
#include "stdlib.h"

extern "C" {
JNIEXPORT void JNICALL
Java_com_example_songhui_savefile_FileUtil_saveJpg(JNIEnv *env, jobject instance,
                                                                jstring savePath_, jbyteArray jpg_,
                                                                jint length) {
    const char *savePath = env->GetStringUTFChars(savePath_, 0);
    jbyte *jpg = env->GetByteArrayElements(jpg_, NULL);

    FILE *fp;
    if ((fp = fopen(savePath, "wb")) == NULL) {
        printf("The file can not be opened.\n");
        exit(1);
    }
    fwrite(jpg,
           1, length,
           fp
    );
    fclose(fp);

    env->ReleaseStringUTFChars(savePath_, savePath);
    env->ReleaseByteArrayElements(jpg_, jpg, 0);
}
}
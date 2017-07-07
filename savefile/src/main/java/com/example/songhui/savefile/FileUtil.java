package com.example.songhui.savefile;

/**
 * Created by sh7781 on 2017/7/7.
 */

public class FileUtil {
    static {
        System.loadLibrary("native-lib");
    }
    public native void saveJpg(String savePath, byte[] jpg, int length);
}

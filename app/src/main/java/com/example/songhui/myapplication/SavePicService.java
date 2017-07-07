package com.example.songhui.myapplication;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.songhui.savefile.FileUtil;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by sh7781 on 2017/7/5.
 */

public class SavePicService extends Service {
    private String mCurrentPhotoPath;
    private byte[] bytes;
    private int length;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.v("result", "onCreate() called");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.v("result", "onStartCommand() called");
        mCurrentPhotoPath = intent.getStringExtra("savePath");
        bytes = intent.getByteArrayExtra("bytes");
        length = intent.getIntExtra("length", 0);
//        MyThread thread = new MyThread(mCurrentPhotoPath, bytes, length);
//        thread.start();
        new FileUtil().saveJpg(mCurrentPhotoPath, bytes, length);
        return super.onStartCommand(intent, flags, startId);
    }


    class MyThread extends Thread {
        private String mCurrentPhotoPath;
        private byte[] bytes;
        private int length;
        public MyThread(String mCurrentPhotoPath, byte[] bytes, int length) {
            this.mCurrentPhotoPath = mCurrentPhotoPath;
            this.length = length;
            this.bytes = bytes;
        }
        @Override
        public void run() {
            super.run();
            new FileUtil().saveJpg(mCurrentPhotoPath, bytes, length);
            stopSelf();
        }
    }
}

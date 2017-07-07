package com.example.songhui.myapplication;

import android.content.Context;
import android.hardware.Camera;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.songhui.savefile.FileUtil;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class MyCameraActivity extends AppCompatActivity {
    private Button btnTakPic;
    private ImageView imgview;
    private FrameLayout preview;

    private Camera camera;
    private CameraPreviewView surfacePreview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_camera);

        btnTakPic = (Button) findViewById(R.id.btnTakePic);
        imgview = (ImageView) findViewById(R.id.imgview);
        preview = (FrameLayout) findViewById(R.id.preview);

        camera = getCameraInstance();
        surfacePreview = new CameraPreviewView(this, camera);
        preview.addView(surfacePreview);
        btnTakPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // take picture
                Camera.Parameters params = camera.getParameters();
                List<Camera.Size> supportedSizes = params.getSupportedPictureSizes();
                Camera.Size sizePicture = (supportedSizes.get(0));

                params.setPictureSize(sizePicture.width, sizePicture.height);
                params.setJpegQuality(100);
                camera.setParameters(params);

                camera.takePicture(null, null, null, picCallback);
            }
        });
    }

    private Camera.PictureCallback picCallback = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] bytes, Camera camera) {
            Toast.makeText(MyCameraActivity.this, "picture token", Toast.LENGTH_SHORT).show();
//            camera.stopPreview();
            // save pic data on device
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String imageFileName = "JPEG_" + timeStamp + "_";
            if (isExternalStorageWritable()) {
                Log.v("result", "able to access externalStorage");
                File storageDir = Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_PICTURES);
                File image = null;
                try {
                    image = File.createTempFile(
                            imageFileName,  /* prefix */
                            ".jpg",         /* suffix */
                            storageDir      /* directory */
                    );
                } catch (IOException e) {
                    e.printStackTrace();
                }

                // Save a file: path for use with ACTION_VIEW intents
                String mCurrentPhotoPath = image.getAbsolutePath();
                Log.v("result", "filePath :" + mCurrentPhotoPath);
                int length = bytes.length;

                /*
                 * save picture in different way
                 * in java way
                 * with native code and using service
                 * with native code in main thread
                 * wih native code in work thread
                 * */
                /*try {
                    FileOutputStream outputStream = new FileOutputStream(mCurrentPhotoPath);
                    outputStream.write(bytes);
                    outputStream.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }*/

                /*Intent intent = new Intent(MyCameraActivity.this, SavePicService.class);
                intent.putExtra("savePath", mCurrentPhotoPath);
                intent.putExtra("bytes", bytes);
                intent.putExtra("length", length);
                startService(intent);*/

                new FileUtil().saveJpg(mCurrentPhotoPath, bytes, length);

                /*MyThread thread = new MyThread(mCurrentPhotoPath, bytes, length);
                thread.start();*/
            } else {
                Log.v("result", "unable to access externalStorage");
            }

        }
    };

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
        }
    }

    /**
     * Checks if external storage is available for read and write
     */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    class CameraPreviewView extends SurfaceView implements SurfaceHolder.Callback {
        private SurfaceHolder holder;
        private Camera camera;

        public CameraPreviewView(Context context, Camera camera) {
            super(context);
            this.camera = camera;
            holder = getHolder();
            holder.addCallback(this);
            holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }

        @Override
        public void surfaceCreated(SurfaceHolder surfaceHolder) {
            try {
                camera.setDisplayOrientation(90);
                camera.setPreviewDisplay(holder);
                camera.startPreview();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

        }

        @Override
        public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
            if (holder.getSurface() == null)
                return;
            camera.stopPreview();
            try {
                camera.setPreviewDisplay(holder);
            } catch (IOException e) {
                e.printStackTrace();
            }
            camera.startPreview();
        }
    }

    public static Camera getCameraInstance() {
        Camera camera = null;
        try {
            camera = Camera.open();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return camera;
    }

    @Override
    protected void onPause() {
        super.onPause();
        camera.stopPreview();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (camera != null) {
            camera.release();
            camera = null;
        }
    }
}

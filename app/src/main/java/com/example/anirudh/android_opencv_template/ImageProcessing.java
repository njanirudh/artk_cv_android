package com.example.anirudh.android_opencv_template;

/**
 * Created by anirudh on 24/1/18.
 */

public class ImageProcessing {

    static {
        System.loadLibrary("native-lib");
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();
    public native Boolean initializeAR(String cam_para_path, String marker_path);
    public native Boolean runDetection(long inputMatrix);

}

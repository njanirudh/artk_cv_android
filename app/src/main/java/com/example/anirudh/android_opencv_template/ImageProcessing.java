package com.example.anirudh.android_opencv_template;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;

import org.opencv.core.CvType;
import org.opencv.core.Mat;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;

/**
 * Created by anirudh on 24/1/18.
 */

public class ImageProcessing {

    final static String TARGET_BASE_PATH = Environment.getExternalStorageDirectory().getPath();

    static {
        System.loadLibrary("native-lib");
    }

    private Context ctx;
    Mat frame;
    Mat refMat;
    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();
    public native void initializeAR(String cam_para_path, String marker_path);
    public native void runDetection(long inputMatrix);

    public  ImageProcessing(Context context) {
        ctx = context;
        frame = new Mat(640, 480, CvType.CV_8UC1);
        System.out.println("###############################################");
    }


    // If targetLocation does not exist, it will be created.
    public void copyDirectory(File sourceLocation , File targetLocation)
            throws IOException {

        if (sourceLocation.isDirectory()) {
            if (!targetLocation.exists() && !targetLocation.mkdirs()) {
                throw new IOException("Cannot create dir " + targetLocation.getAbsolutePath());
            }

            String[] children = sourceLocation.list();
            for (int i=0; i<children.length; i++) {
                copyDirectory(new File(sourceLocation, children[i]),
                        new File(targetLocation, children[i]));
            }
        } else {

            // make sure the directory we plan to store the recording in exists
            File directory = targetLocation.getParentFile();
            if (directory != null && !directory.exists() && !directory.mkdirs()) {
                throw new IOException("Cannot create dir " + directory.getAbsolutePath());
            }

            InputStream in = new FileInputStream(sourceLocation);
            OutputStream out = new FileOutputStream(targetLocation);

            // Copy the bits from instream to outstream
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            in.close();
            out.close();
        }
    }


    public void InitializeAR()
    {

             try {

            Log.i("===>>>>",ctx.getCacheDir().getPath() + "/" + "Data" + "/camera_para.dat");
            Log.i("===>>>>",ctx.getCacheDir().getPath() + "/" + "Data" + "/markers.dat");

            initializeAR(ctx.getCacheDir().getPath() + "/" + "Data" + "/camera_para.dat",
                    ctx.getCacheDir().getPath() + "/" + "Data" + "/markers.dat");

        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Bitmap RunDetection(Bitmap input)
    {
        Mat frame = new Mat(640, 480, CvType.CV_8UC1);
        org.opencv.android.Utils.bitmapToMat(input, frame);

        runDetection(frame.getNativeObjAddr());

        org.opencv.android.Utils.matToBitmap(frame, input);
        return input;
    }

}

package com.example.anirudhnj.ar_java.Utilities;

/**
 * Created by anirudhnj on 09/11/17.
 */

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.provider.Settings;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class GlobalUtils {

    public static Context context;
    public static SettingsManager settingsManager;
    public static float screenRatio;

    public static int screenWidth;
    public static int screenHeight;

    //Set the Image size to be sent to OpenCv
    public static int processingImgWidth = 640;
    public static int processingImgHeight = 480;

    public static String currentPathVideo = "";

    public static boolean isRecordVideo = false;
    public static boolean isCanRotateOrientation = false;

    public static int mOrientation =  -1;

    public static final int ORIENTATION_PORTRAIT_NORMAL =  1;
    public static final int ORIENTATION_PORTRAIT_INVERTED =  2;
    public static final int ORIENTATION_LANDSCAPE_NORMAL =  3;
    public static final int ORIENTATION_LANDSCAPE_INVERTED =  4;

    public static String getCurrentDateAndTime() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        String formattedDate = df.format(c.getTime());
        return formattedDate;
    }

    public static Bitmap rotate(Bitmap bitmap, int degree) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        Matrix mtx = new Matrix();
        mtx.setRotate(degree);
        return Bitmap.createBitmap(bitmap, 0, 0, w, h, mtx, true);
    }
    public static final float GEOFENCE_RADIUS_IN_METERS = 2000;

    public static boolean getAutoRotationStatus(Context context) {
        if (Settings.System.getInt(context.getContentResolver(), Settings.System.ACCELEROMETER_ROTATION, 0) == 1)

            return true;
        else
            return false;
    }

    //Rotate Bitmap based on the orientation and camera position
    public static Bitmap rotateBitmapOnOrientation(Bitmap bitmap, Context ctx) {
        if (GlobalUtils.settingsManager.getTypeCamera() == Camera.CameraInfo.CAMERA_FACING_BACK) {
            bitmap = GlobalUtils.rotate(bitmap, 90);
            if (GlobalUtils.getAutoRotationStatus(ctx) == true) {
                if (GlobalUtils.mOrientation == GlobalUtils.ORIENTATION_LANDSCAPE_NORMAL)
                    bitmap = GlobalUtils.rotate(bitmap, -90);

                else if (GlobalUtils.mOrientation == GlobalUtils.ORIENTATION_LANDSCAPE_INVERTED)
                    bitmap = GlobalUtils.rotate(bitmap, 90);
            }
        } else if (GlobalUtils.mOrientation == GlobalUtils.ORIENTATION_PORTRAIT_NORMAL) {
            bitmap = GlobalUtils.rotate(bitmap, -90);
        } else if (GlobalUtils.mOrientation == GlobalUtils.ORIENTATION_LANDSCAPE_NORMAL) {
            bitmap = GlobalUtils.rotate(bitmap, 90);
        } else if (GlobalUtils.mOrientation == GlobalUtils.ORIENTATION_LANDSCAPE_INVERTED) {
            bitmap = GlobalUtils.rotate(bitmap, -270);
        }
        return bitmap;
    }
}

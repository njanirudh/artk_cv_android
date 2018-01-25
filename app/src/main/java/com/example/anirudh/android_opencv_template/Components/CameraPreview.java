package com.example.anirudhnj.ar_java.Components;

/**
 * Created by anirudhnj on 09/11/17.
 */

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.hardware.Camera;
import android.media.MediaRecorder;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicYuvToRGB;
import android.renderscript.Type;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;

import com.example.anirudhnj.ar_java.ImageProcessingCpp;
import com.example.anirudhnj.ar_java.ImageProcessing.ImageProcessingJava;
import com.example.anirudhnj.ar_java.Utilities.GlobalUtils;

import org.opencv.core.Rect2d;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback, Camera.PreviewCallback  {

    private SurfaceHolder mHolder;
    public Camera mCamera;

    private RenderScript rs;
    private ScriptIntrinsicYuvToRGB yuvToRgbIntrinsic;
    private Type.Builder yuvType, rgbaType;
    private Allocation in, out;
    private Context ctx;
    private SurfaceHolder surfaceHolder;

    ImageProcessingJava imageProcessingJava;
    ImageProcessingCpp imageProcessingCpp;

    int PROCESSING_TYPE = 2;

    //record video
    private MediaRecorder mMediaRecorder;
    private ImageView liveCamera;


    private float mDist = 0;

    private boolean trackingReset = false;
    private static final boolean  PROCESS_IMAGE = true;


    public CameraPreview(Context context, ImageView cameraView) {
        super(context);

        mHolder = getHolder();
        mHolder.addCallback(this);
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        ctx = context;
        rs = RenderScript.create(context);
        yuvToRgbIntrinsic = ScriptIntrinsicYuvToRGB.create(rs, Element.U8_4(rs));

        liveCamera = cameraView;

        imageProcessingJava = new ImageProcessingJava(ctx);
        imageProcessingCpp = new ImageProcessingCpp(ctx);

        initCamera();
        imageProcessingCpp.SetReferenceImage();

        liveCamera.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                System.out.println("Count " + event.getPointerCount());
                int count = event.getPointerCount();
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if (count > 1) {
                            mDist = getFingerSpacing(event);
                        } else {
                            float x = event.getX();
                            float y = event.getY();
                            focusCamera(x, y);
                        }
                        return true;
                    case MotionEvent.ACTION_UP:
                        if (count > 1) {
                            handleZoom(event);
                        }
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        if (count > 1) {
                            handleZoom(event);
                        }
                        return true;
                    default:
                        return true;
                }
            }
        });
    }

    private void initCamera() {
        mCamera = openCamera();
        setParametersCamera();
        try {
            mCamera.setPreviewDisplay(mHolder);
            mCamera.startPreview();
        } catch (IOException e) {
        }
    }

    public int getPreviewFrameRate(){
        return 0;
    }

    private void shutDownRecordCamera() {
        if (mMediaRecorder != null) {
            mMediaRecorder.reset();
            mMediaRecorder.release();
            mMediaRecorder = null;
        }
    }

    private Camera openCamera() {
        int cameraID = GlobalUtils.settingsManager.getTypeCamera();
        return Camera.open(cameraID);
    }

    private void setParametersCamera() {
        Camera.Parameters params = mCamera.getParameters();
        params.setPreviewSize(GlobalUtils.processingImgWidth,GlobalUtils.processingImgHeight);
        params.setPreviewFrameRate(25);
        params.setRecordingHint(true);
        params.setWhiteBalance(Camera.Parameters.WHITE_BALANCE_AUTO);
        params.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
        params.setSceneMode(Camera.Parameters.SCENE_MODE_STEADYPHOTO);
        params.setAntibanding(Camera.Parameters.ANTIBANDING_AUTO);
        params.setPictureSize(640, 480);

        setFocusable(true);
        setFocusableInTouchMode(true);

        if (GlobalUtils.settingsManager.getTypeCamera() == Camera.CameraInfo.CAMERA_FACING_BACK)
            params.setRotation(0);
        else
            params.setRotation(270);

        mCamera.setDisplayOrientation(90);
        try {
            mCamera.setParameters(params);
        } catch (Exception e) {
        }
    }

    public void switchCamera() {
        if (GlobalUtils.settingsManager.getTypeCamera() == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            GlobalUtils.settingsManager.setTypeCamera(Camera.CameraInfo.CAMERA_FACING_BACK);
        } else {
            GlobalUtils.settingsManager.setTypeCamera(Camera.CameraInfo.CAMERA_FACING_FRONT);
        }
        resetCamera();
    }

    public void resetCamera() {
        releaseCamera();
        initCamera();

        try {
            mCamera.setPreviewCallback(this);
            mCamera.setPreviewDisplay(surfaceHolder);
        } catch (IOException e) {
            e.printStackTrace();
        }
        mCamera.startPreview();
    }

    private void releaseCamera() {

        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.setPreviewCallback(null);
            mCamera.release();
            mCamera = null;
        }
    }

    public void focusCamera(float x, float y) {
        if (mCamera != null) {
            mCamera.cancelAutoFocus();
            Rect touchRect = new Rect(
                    (int) (x - 100),
                    (int) (y - 100),
                    (int) (x + 100),
                    (int) (y + 100));
            final Rect targetFocusRect = new Rect(
                    touchRect.left * 2000 / this.getWidth() - 1000,
                    touchRect.top * 2000 / this.getHeight() - 1000,
                    touchRect.right * 2000 / this.getWidth() - 1000,
                    touchRect.bottom * 2000 / this.getHeight() - 1000);
            doTouchFocus(targetFocusRect);
        }

        imageProcessingJava.resetTracker();
        imageProcessingCpp.resetTracking();
    }

    public void doTouchFocus(final Rect tfocusRect) {
        try {
            final List<Camera.Area> focusList = new ArrayList<Camera.Area>();
            Camera.Area focusArea = new Camera.Area(tfocusRect, 1000);
            focusList.add(focusArea);

            Camera.Parameters para = mCamera.getParameters();
            para.setFocusAreas(focusList);
            para.setMeteringAreas(focusList);
            mCamera.setParameters(para);

            mCamera.autoFocus(myAutoFocusCallback);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    Camera.AutoFocusCallback myAutoFocusCallback = new Camera.AutoFocusCallback() {

        @Override
        public void onAutoFocus(boolean arg0, Camera arg1) {
            if (arg0) {
                mCamera.cancelAutoFocus();
            }
        }
    };

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        try {
            if (mCamera == null) {
                initCamera();
            }

            this.surfaceHolder = surfaceHolder;
            mCamera.setPreviewDisplay(surfaceHolder);
            mCamera.startPreview();

        } catch (Exception e) {
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int format, int width, int height) {
        if (!GlobalUtils.isRecordVideo) {
            if (mHolder.getSurface() == null)
                return;

            try {
                mCamera.stopPreview();
            } catch (Exception e) {
            }

            try {
                this.surfaceHolder = surfaceHolder;
                mCamera.setPreviewCallback(this);
                mCamera.setPreviewDisplay(mHolder);
                mCamera.startPreview();
            } catch (IOException e) {
            }
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        destroyCamera();
    }

    public void destroyCamera() {
        releaseCamera();
        shutDownRecordCamera();
    }

    Camera.ShutterCallback shutterCallback = new Camera.ShutterCallback() {
        public void onShutter() {
            // TODO Auto-generated method stub
        }
    };

    Camera.PictureCallback rawCallback = new Camera.PictureCallback() {
        public void onPictureTaken(byte[] data, Camera camera) {
            // TODO Auto-generated method stub
        }
    };

    Camera.PictureCallback jpegCallback = new Camera.PictureCallback() {
        public void onPictureTaken(byte[] data, Camera camera) {
            resetCamera();
        }
    };

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        // TODO Auto-generated method stub

        Camera.Parameters parameters = camera.getParameters();
        int width = parameters.getPreviewSize().width;
        int height = parameters.getPreviewSize().height;

        mCamera.addCallbackBuffer(data);

        if (yuvType == null) {
            yuvType = new Type.Builder(rs, Element.U8(rs)).setX(data.length);
            in = Allocation.createTyped(rs, yuvType.create(), Allocation.USAGE_SCRIPT);

            rgbaType = new Type.Builder(rs, Element.RGBA_8888(rs)).setX(width).setY(height);
            out = Allocation.createTyped(rs, rgbaType.create(), Allocation.USAGE_SCRIPT);
        }

        in.copyFrom(data);

        yuvToRgbIntrinsic.setInput(in);
        yuvToRgbIntrinsic.forEach(out);

        Bitmap mBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        out.copyTo(mBitmap);

        mBitmap = GlobalUtils.rotateBitmapOnOrientation(mBitmap,ctx);

        liveCamera.setVisibility(VISIBLE);

        if(PROCESS_IMAGE) {

            if(PROCESSING_TYPE == 1) {

                Rect2d rect2d = new Rect2d(100, 100, 100, 100);
                mBitmap = imageProcessingJava.trackImage(mBitmap, rect2d);
                liveCamera.setImageBitmap(mBitmap);
            }else{
                //Rect2d rect2d = new Rect2d(100, 100, 100, 100);
                mBitmap = imageProcessingCpp.PerformComparison(mBitmap);
                liveCamera.setImageBitmap(mBitmap);

            }

        } else{
            liveCamera.setImageBitmap(mBitmap);
        }

    }

    public void setFlashMode(String m) {
        if (mCamera == null)
            return;

        Camera.Parameters params = mCamera.getParameters();
        switch (m) {
            case "on":
                params.setFlashMode("on");
                break;
            case "off":
                params.setFlashMode("off");
                break;
            case "auto":
                params.setFlashMode("auto");
                break;
            default:
                break;
        }
        mCamera.setParameters(params);
    }

    public boolean hasFlash() {
        if (mCamera == null)
            return false;
        Camera.Parameters params = mCamera.getParameters();
        List<String> flashModes = params.getSupportedFlashModes();
        if (flashModes == null) {
            return false;
        }

        for (String flashMode : flashModes) {
            if (Camera.Parameters.FLASH_MODE_ON.equals(flashMode)) {
                return true;
            }
        }
        return false;
    }

    public void freezeCamera() {
        mCamera.stopPreview();
        mCamera.setPreviewCallback(null);
    }

    public void unfreezeCamera() {
        mCamera.startPreview();
    }

    public void handleZoom(MotionEvent event) {
        Camera.Parameters params = mCamera.getParameters();
        int maxZoom = params.getMaxZoom();
        int zoom = params.getZoom();
        float newDist = getFingerSpacing(event);
        if (newDist > mDist) {
            //zoom in
            if (zoom < maxZoom)
                zoom++;
        } else if (newDist < mDist) {
            //zoom out
            if (zoom > 0)
                zoom--;
        }
        mDist = newDist;
        params.setZoom(zoom);
        mCamera.setParameters(params);
    }

    private float getFingerSpacing(MotionEvent event) {
        // ...
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }
}


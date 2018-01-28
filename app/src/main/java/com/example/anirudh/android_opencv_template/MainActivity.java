package com.example.anirudh.android_opencv_template;

import android.Manifest;
import android.content.pm.PackageManager;
import android.hardware.SensorManager;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.OrientationEventListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.anirudh.android_opencv_template.AssetUtils.AssetHelper;
import com.example.anirudh.android_opencv_template.Components.CameraPreview;
import com.example.anirudh.android_opencv_template.Utilities.GlobalUtils;
import com.example.anirudh.android_opencv_template.Utilities.SettingsManager;

import org.opencv.android.OpenCVLoader;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ImageView liveCamera;
    private CameraPreview cameraPreview;

    private OrientationEventListener mOrientationEventListener;
    private boolean autoRotation = true;

    static {
        if(!OpenCVLoader.initDebug()){
            Log.d("OpenCV <<", "OpenCV not loaded");
        } else {
            Log.d("OpenCV <<", "OpenCV loaded");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeInstance();

        liveCamera = (ImageView) findViewById(R.id.live_camera);
        GlobalUtils.settingsManager = SettingsManager.getInstance(getApplicationContext());

        // Example of a call to a native method
        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentapiVersion >= 23) {
            ArrayList<String> requestList = new ArrayList<>();

            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                requestList.add(Manifest.permission.CAMERA);
            }

            if (requestList.size() > 0) {
                String[] requestArr = new String[requestList.size()];
                requestArr = requestList.toArray(requestArr);
                ActivityCompat.requestPermissions(this, requestArr, 1);
            } else {
                initCamera();
            }
        } else {
            initCamera();
        }

    }

    protected void initializeInstance() {

        // Unpack assets to cache directory so native library can read them.
        // N.B.: If contents of assets folder changes, be sure to increment the
        // versionCode integer in the AndroidManifest.xml file.
        AssetHelper assetHelper = new AssetHelper(getAssets());
        assetHelper.cacheAssetFolder(this, "Data");
        assetHelper.cacheAssetFolder(this, "DataNFT");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        //initView();
        initCamera();
    }

    private void initCamera() {
        cameraPreview = new CameraPreview(this, liveCamera);
        final FrameLayout camera_view = (FrameLayout) findViewById(R.id.camera_view);
        camera_view.addView(cameraPreview);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    int previousOrientation = 0, nextOrientation = 0;
    int typeOrientation = 0;
    private void changeRotation(int orientation) {
        switch (orientation) {
            case GlobalUtils.ORIENTATION_PORTRAIT_NORMAL:
                setNextOrientationPotNormal();
                typeOrientation = 0;
                previousOrientation = nextOrientation;
                break;
            case GlobalUtils.ORIENTATION_LANDSCAPE_NORMAL:
                setNextOrientationLandNormal();
                typeOrientation = 1;
                previousOrientation = nextOrientation;
                break;
            case GlobalUtils.ORIENTATION_PORTRAIT_INVERTED:
                setNextOrientationPotInverted();
                previousOrientation = nextOrientation;
                typeOrientation = 2;
                break;
            case GlobalUtils.ORIENTATION_LANDSCAPE_INVERTED:
                setNextOrientationLanInverted();
                previousOrientation = nextOrientation;
                typeOrientation = 3;
                break;
            default:
                break;
        }
    }

    private void setNextOrientationPotNormal() {
        if (typeOrientation == 3)
            nextOrientation += 90;
        else if (typeOrientation == 1)
            nextOrientation -= 90;
        else if (typeOrientation == 2)
            nextOrientation += 180;
    }

    private void setNextOrientationLandNormal() {
        if (typeOrientation == 0)
            nextOrientation += 90;
        else if (typeOrientation == 2)
            nextOrientation -= 90;
        else if (typeOrientation == 3)
            nextOrientation +=180;
    }

    private void setNextOrientationPotInverted() {
        if (typeOrientation == 1)
            nextOrientation += 90;
        else if (typeOrientation == 3)
            nextOrientation -= 90;
        else if (typeOrientation == 0)
            nextOrientation -= 180;
    }

    private void setNextOrientationLanInverted() {
        if (typeOrientation == 2)
            nextOrientation += 90;
        else if (typeOrientation == 0)
            nextOrientation -= 90;
        else if (typeOrientation == 1)
            nextOrientation -=180;
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            if (mOrientationEventListener == null) {
                mOrientationEventListener = new OrientationEventListener(this, SensorManager.SENSOR_DELAY_NORMAL) {
                    @Override
                    public void onOrientationChanged(int orientation) {
                        if (!GlobalUtils.isCanRotateOrientation) {
                            int lastOrientation = GlobalUtils.mOrientation;
                            if (orientation >= 315 || orientation < 45) {
                                if (GlobalUtils.mOrientation != GlobalUtils.ORIENTATION_PORTRAIT_NORMAL) {
                                    GlobalUtils.mOrientation = GlobalUtils.ORIENTATION_PORTRAIT_NORMAL;
                                }
                            } else if (orientation < 315 && orientation >= 225) {
                                if (GlobalUtils.mOrientation != GlobalUtils.ORIENTATION_LANDSCAPE_NORMAL) {
                                    GlobalUtils.mOrientation = GlobalUtils.ORIENTATION_LANDSCAPE_NORMAL;
                                }
                            } else if (orientation < 225 && orientation >= 135) {
                                if (GlobalUtils.mOrientation != GlobalUtils.ORIENTATION_PORTRAIT_INVERTED) {
                                    GlobalUtils.mOrientation = GlobalUtils.ORIENTATION_PORTRAIT_INVERTED;
                                }
                            } else {
                                if (GlobalUtils.mOrientation != GlobalUtils.ORIENTATION_LANDSCAPE_INVERTED) {
                                    GlobalUtils.mOrientation = GlobalUtils.ORIENTATION_LANDSCAPE_INVERTED;
                                }
                            }
                            if (lastOrientation != GlobalUtils.mOrientation) {
                                if (!GlobalUtils.getAutoRotationStatus(getApplicationContext()))
                                {
                                    autoRotation = false;
                                    changeRotation(GlobalUtils.mOrientation);
                                }
                            }
                            if (GlobalUtils.getAutoRotationStatus(getApplicationContext()) == true && autoRotation == false)
                            {
                                autoRotation = true;
                            }
                        }
                    }
                };
            }
            if (mOrientationEventListener.canDetectOrientation()) {
                mOrientationEventListener.enable();
            }
        } catch (Exception e) {

        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mOrientationEventListener != null) {
            mOrientationEventListener.disable();

        }
    }
}
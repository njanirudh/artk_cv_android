package com.example.anirudhnj.ar_java.Utilities;

/**
 * Created by anirudhnj on 09/11/17.
 */

import android.content.Context;
import android.content.SharedPreferences;

public class SettingsManager {

    private final String MyPREFERENCES = "";
    private final String TypeCamera = "TypeCamera";
    private final String FlashMode = "FlashMode";

    private static SharedPreferences sharedpreferences;
    private SharedPreferences.Editor editor;

    private static SettingsManager instance = null;

    private SettingsManager(Context context) {
        sharedpreferences = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        editor = sharedpreferences.edit();
    }

    public static SettingsManager getInstance(Context context) {
        if (instance == null)
            instance = new SettingsManager(context);
        return instance;
    }

    public void setTypeCamera(int value) {
        editor.putInt(TypeCamera, value);
        editor.commit();
    }

    public int getTypeCamera() {
        return sharedpreferences.getInt(TypeCamera, 0);
    }

    public void setFlashMode(String value) {
        editor.putString(FlashMode, value);
        editor.commit();
    }

    public String getFlashMode() {
        return sharedpreferences.getString(FlashMode, "on");
    }


}

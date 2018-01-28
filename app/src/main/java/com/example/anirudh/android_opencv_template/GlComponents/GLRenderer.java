package com.example.anirudh.android_opencv_template.GlComponents;

import android.opengl.GLSurfaceView;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by anirudh on 25/1/18.
 */

public class GLRenderer implements GLSurfaceView.Renderer {

    private MovieController mMovieController = null;

    // Accessors.
    public MovieController getMovieController() {
        return mMovieController;
    }

    public void setMovieController(MovieController mc) {
        mMovieController = mc;
    }

    // Delegates.

    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        //ARMovieActivity.nativeSurfaceCreated();
    }

    public void onSurfaceChanged(GL10 gl, int w, int h) {
        //ARMovieActivity.nativeSurfaceChanged(w, h);
    }

    public void onDrawFrame(GL10 gl) {

        mMovieController.updateTexture();
        //ARMovieActivity.nativeDrawFrame(mMovieController.mMovieWidth, mMovieController.mMovieHeight, mMovieController.mGLTextureID, mMovieController.mGLTextureMtx);
    }

}

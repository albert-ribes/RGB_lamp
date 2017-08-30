package com.example.rgb;

import android.view.SurfaceHolder;

/**
 * Created by albert on 01/01/2017.
 */

public class MyCallback implements SurfaceHolder.Callback {
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format,
                               int width, int height) {
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        // you need to start your drawing thread here
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        // and here you need to stop it
    }
}
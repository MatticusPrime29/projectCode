package com.saskpolytech.cst129;

import android.content.Context;
import android.hardware.Camera;
import android.hardware.camera2.CameraManager;
import android.nfc.Tag;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;

/**
 * Created by cst129 on 5/22/2018.
 */

public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback{



    private SurfaceHolder holder;
    private CameraManager camera;

    public CameraPreview(Context context, Camera camera)
    {
        super(context);
        camera = camera;

        holder = getHolder();
        holder.addCallback(this);
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }
    @Override
    public void surfaceCreated(SurfaceHolder holder)
    {
//        try
//        {
//            camera.setPreviewDisplay(holder);
//        }
//        catch(IOException e)
//        {
//            Log.d(VIEW_LOG_TAG,"Error setting camera preview: " + e.getMessage());
//        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height)
    {
//        if(holder.getSurface() == null)
//        {
//            return;
//        }
//
//        try
//        {
//            camera.stopPreview();
//        }
//        catch(Exception e)
//        {
//
//        }
//
//        try
//        {
//            camera.setPreviewDisplay(holder);
//            camera.startPreview();
//        }
//        catch(Exception e)
//        {
//            Log.d(VIEW_LOG_TAG, "Error starting camera preview: " + e.getMessage());
//        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder)
    {

    }
}

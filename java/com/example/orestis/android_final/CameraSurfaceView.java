package com.example.orestis.android_final;

import android.content.Context;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;

/**
 * Created by Orestis on 31-Jul-15.
 */
public class CameraSurfaceView extends SurfaceView implements SurfaceHolder.Callback {
    private SurfaceHolder mHolder;
    private Camera mCamera;
    public CameraSurfaceView(Context context, Camera camera) {
        super(context);
        mCamera = camera;

// Προσθήκη ενός SurfaceHolder.Callback για να ειδοποιηθούμε
// όταν το surface δημιουργηθεί και καταστραφεί
        mHolder = getHolder ();
        mHolder.addCallback(this);
// deprecated αλλά απαραίτητο για εκδόσεις πριν της 3.0
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }
    public void surfaceCreated(SurfaceHolder holder) {
// Το Surface έχει δημιουργηθεί και λέμε στην κάμερα
//που να εμφανίσει την προεπισκόπηση
        try {
            mCamera.setPreviewDisplay(holder);
            mCamera.setDisplayOrientation(90);
            mCamera.startPreview();
        } catch (IOException e) {
            Log.d("ERROR", " Error setting camera preview:" + e.getMessage());
        }
    }
    public void surfaceDestroyed(SurfaceHolder holder) {
// Εδώ μπαίνει κώδικας για την αποδέσμευση της κάμερας
    }
    public void surfaceChanged(SurfaceHolder holder, int format,int w, int h) {
// Χειρισμός των events περιστροφής της οθόνης εδώ
        if (mHolder.getSurface() == null){
// το preview surface δεν έχει δημιουργηθεί
            return;
        }
// σταμάτημα του preview πριν τις αλλαγές
        try {
            mCamera.stopPreview();
        } catch (Exception e){
// Στην περίπτωση που δεν υπάρχει η προεπισκόπηση
        }
// εκκίνηση του preview
        try {
            mCamera.setPreviewDisplay(mHolder);
            mCamera.startPreview();
        } catch (Exception e){
            Log.d("Error", " Error starting camera preview:" + e.getMessage());
        }
    }
}
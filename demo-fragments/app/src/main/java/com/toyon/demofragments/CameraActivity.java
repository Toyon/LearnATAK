/**
 * Copyright (c) 2023 Toyon Research Corporation. No Rights Reserved.
 * Report problems or provide feedback at: https://github.com/Toyon/LearnATAK/issues
 */
package com.toyon.demofragments;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;

/**
 * This activity opens up the android's default camera app and allows a user to take a photo.
 * When the user confirms the captured photo, this activity exits and sends the photo to the
 * MainDropDownReceiver to be classified.
 */
public class CameraActivity extends Activity {

    public static final String TAG = CameraActivity.class.getSimpleName();
    private static final int CAMERA_REQUEST = 8888; // Define the pic id
    private static final String CAMERA_INFO = "com.toyon.demofragments.PHOTO";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent camera_intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        camera_intent.putExtra("android.intent.extras.CAMERA_FACING", 1);
        camera_intent.putExtra("android.intent.extras.LENS_FACING_FRONT", 0);
        camera_intent.putExtra("android.intent.extra.USE_FRONT_CAMERA", false);
        // Start the activity with camera_intent, and request pic id
        startActivityForResult(camera_intent, CAMERA_REQUEST);
    }

    /** This method will help to retrieve the image */
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Intent i = new Intent(CAMERA_INFO);
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            Bundle extras = data.getExtras();
            if (extras != null) {
                Bitmap photo = (Bitmap) extras.get("data");
                i.putExtra("image", photo);
            }
        }
        sendBroadcast(i);
        finish();
    }

    public interface CameraDataReceiver {
        void onCameraDataReceived(Bitmap b);
    }

    /** Broadcast Receiver that is responsible for getting the data back to the plugin. */
    public static class CameraDataListener extends BroadcastReceiver {
        private boolean registered = false;
        public CameraDataReceiver cdr = null;

        synchronized public void register(Context context, CameraDataReceiver cdr) {
            if (!registered)
                context.registerReceiver(this, new IntentFilter(CAMERA_INFO));
            this.cdr = cdr;
            registered = true;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            synchronized (this) {
                try {
                    Bundle extras = intent.getExtras();
                    if (extras != null) {
                        Bitmap bm = (Bitmap) extras.get("image");
                        if (bm != null && cdr != null)
                            cdr.onCameraDataReceived(bm);
                    }
                } catch (Exception ignored) {
                }
                if (registered) {
                    context.unregisterReceiver(this);
                    registered = false;
                }
            }
        }
    }
}

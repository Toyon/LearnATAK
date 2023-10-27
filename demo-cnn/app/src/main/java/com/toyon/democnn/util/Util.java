package com.toyon.democnn.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.atakmap.coremap.filesystem.FileSystemUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;

public class Util {

    private static final String TAG = Util.class.getSimpleName();

    /** Save Bitmap to conventional ATAK File location */
    public static File bitmapToFile(Bitmap bitmap, String atakPath) {
        File f = FileSystemUtils.getItem(atakPath);
        try {
            Objects.requireNonNull(f.getParentFile()).mkdirs();
            boolean created = f.createNewFile();
            if (!created) return f;
            FileOutputStream fStream = new FileOutputStream(f);
            bitmap.compress(Bitmap.CompressFormat.PNG, 85, fStream);
            fStream.flush();
            fStream.close();
        } catch (IOException e) {
            Log.e(TAG, "Issue writing bitmap to atak resource directory.\n" + e);
            e.printStackTrace();
        }
        return f;
    }

    /**
     * Add a long click listener to the provided button which displays a Toast message with the
     * specified text when the button is long pressed.
     *
     * @param atakContext ATAK map context required for Toast notifications
     * @param btn Button to add the long click listener
     * @param str The string to display for the toast
     */
    public static void setButtonToast(Context atakContext, @NonNull Button btn, String str) {
        btn.setOnLongClickListener(view -> {
            Toast.makeText(atakContext, str, Toast.LENGTH_LONG).show();
            return true;
        });
    }
}

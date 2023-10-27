/**
 * Copyright (c) 2023 Toyon Research Corporation. No Rights Reserved.
 * Report problems or provide feedback at: https://github.com/Toyon/LearnATAK/issues
 */
package com.toyon.demohelloworld;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import android.util.TypedValue;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.atakmap.coremap.filesystem.FileSystemUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;

/** Additional miscellaneous helpful functions to use throughout the plugin */
public class Util {

    private static final String TAG = "DemoHelloWorld.Util";

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

    /**
     * Add an asset to ATAK managed folder structure and return the absolute path to the copy.
     * @param pluginContext plugin context
     * @param atakPath relative path to save the file at from the root of ATAK managed files
     * @param assetName file name of the asset to save
     * @return absolute file path to the created file (null if failed)
     */
    @Nullable
    public static String assetToFile(Context pluginContext, String atakPath, String assetName) {
        File f = FileSystemUtils.getItem(atakPath);
        Objects.requireNonNull(f.getParentFile()).mkdir();
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(f);
            FileSystemUtils.copyFromAssets(pluginContext, assetName, fos);
        } catch (java.io.FileNotFoundException notFoundException) {
            Log.e(TAG, "Failed to extract " + f + " !!!");
            return null;
        } finally {
            if (fos != null)
                try { fos.close(); } catch (java.io.IOException ignored) { }
        }
        return f.getAbsolutePath();
    }

    /**
     * Convenience method to load contents of a small text file into a single string to operate on.
     * @param file File to load into string (large files will likely run out of memory)
     * @return String of the contents of the file
     */
    public static String loadJsonFromFile(File file) {
        if (!file.exists())
            return "";
        try {
            FileInputStream inputStream = new FileInputStream(file);
            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            inputStream.close();
            return new String(buffer, "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    };


    /**
     * Converts a `dp` number to the proper pixel value for the device
     * @param dip Density-Independent Pixel to convert from
     * @param resources application resources
     * @return integer value of the pixel size corresponding to the provided dp value
     */
    public static int dipToPixel(int dip, Resources resources) {
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, dip, resources.getDisplayMetrics());
    }


}

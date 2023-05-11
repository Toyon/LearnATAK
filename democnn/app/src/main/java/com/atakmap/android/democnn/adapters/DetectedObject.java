package com.atakmap.android.democnn.adapters;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.File;

/** Class to parse info from detected object cropped images based on file name format */
public class DetectedObject {

    public String label;
    public float certainty;
    public Bitmap image;

    public DetectedObject(File file) {
        image = BitmapFactory.decodeFile(file.getAbsolutePath());
        String[] splitName = file.getName().split("_");
        label = splitName[1];
        certainty = Float.parseFloat(splitName[2].substring(0, splitName[2].length() - 4))*100;
    }
}

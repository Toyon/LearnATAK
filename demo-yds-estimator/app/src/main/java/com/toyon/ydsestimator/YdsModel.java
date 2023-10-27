/**
 * Copyright (c) 2023 Toyon Research Corporation. No Rights Reserved.
 * Report problems or provide feedback at: https://github.com/Toyon/LearnATAK/issues
 */
package com.toyon.ydsestimator;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.toyon.ydsestimator.plugin.ml.Yds256x2562classYdsnet100EpochsAugsize2;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.IOException;

/** Utility to run inference on images using the trained TFLite model */
public class YdsModel {

    public static final String TAG = YdsModel.class.getSimpleName();

    /** Load the TFLite model and classify the trail in the provided image */
    public static String classify(Bitmap img, Context pluginCtx) {
        try {
            Yds256x2562classYdsnet100EpochsAugsize2 model =
                    Yds256x2562classYdsnet100EpochsAugsize2.newInstance(pluginCtx);
            int imageSize = 256;

            // Creates inputs for reference.
            Bitmap scaledImg = Bitmap.createScaledBitmap(img, imageSize, imageSize, true);
            TensorImage image = TensorImage.fromBitmap(scaledImg);
            TensorImage image2 = TensorImage.createFrom(image, DataType.FLOAT32);
            TensorBuffer image_buff = image2.getTensorBuffer();

            // Runs model inference and gets result.
            Yds256x2562classYdsnet100EpochsAugsize2.Outputs outputs = model.process(image_buff);
            TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();
            System.out.println(outputFeature0);

            float[] featArray = outputFeature0.getFloatArray();

            // Find the index with the maximum probability
            // Each index corresponds to a YDS classification value
            int maxIndex = 0;
            float maxProbability = featArray[0];
            for (int i = 1; i < featArray.length; i++) {
                if (featArray[i] > maxProbability) {
                    maxProbability = featArray[i];
                    maxIndex = i;
                }
            }
            // Releases model resources if no longer used.
            model.close();
            return resultToString(maxIndex);
        } catch (IOException e) {
            Log.e(TAG, "Unable to load TFLite model", e);
            return "Unable to Classify";
        }
    }

    /** Convert model's result integer to comprehensible  text */
    public static String resultToString(int result) {
        String classificationResult = "";
        if (result == 0) {
            classificationResult = "Result: YDS Class 1";
        } else if (result == 1) {
            classificationResult = "Result: YDS Class 5";
        } else {
            classificationResult = "YDS Class Unknown (try again)";
        }
        return classificationResult;
    }
}



/**
 * Copyright (c) 2023 Toyon Research Corporation. No Rights Reserved.
 * Report problems or provide feedback at: https://github.com/Toyon/LearnATAK/issues
 */
package com.toyon.tfliteexample;

import android.content.Context;
import android.util.Log;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.Tensor;
import org.tensorflow.lite.support.common.FileUtil;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;
import org.tensorflow.lite.support.tensorbuffer.TensorBufferFloat;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.MappedByteBuffer;
import java.util.HashMap;
import java.util.Map;

/**
 * A helper class for using the TF Lite Interpreter.
 */
public class TFLiteUtils {

    private final static String TAG = TFLiteUtils.class.getSimpleName();

    /**
     * Create an interpreter. (Remember that interpreters are not thread-safe)
     *
     * @param context  plugin context from the activity / application / DropDownReceiver
     * @param fileName file name of the model asset (no path needed)
     * @return a newly created interpreter with default options
     * @throws IOException if the file cannot be read
     */
    public static Interpreter loadAsset(Context context, String fileName) throws IOException {
        MappedByteBuffer modelFile = FileUtil.loadMappedFile(context, fileName);
        return new Interpreter(modelFile, new Interpreter.Options());
    }

    /**
     * Log inputs and outputs for a signature
     *
     * @param in      interpreter for the model file
     * @param sigName signature name
     */
    public static void debugSignature(Interpreter in, String sigName) {
        Log.i(TAG, sigName);
        Log.i(TAG, String.join(", ", in.getSignatureInputs(sigName)));
        Log.i(TAG, String.join(", ", in.getSignatureOutputs(sigName)));
    }

    /**
     * Allocate an empty output buffer of the correct size
     *
     * @param in         interpreter
     * @param outputName output name
     * @param sigName    signature name
     * @return fixed size bytebuffer created from tensor description
     */
    public static ByteBuffer getOutputBuffer(Interpreter in, String outputName, String sigName) {
        Tensor t = in.getOutputTensorFromSignature(outputName,
                sigName);
        return TensorBuffer.createFixedSize(
                t.shape(),
                t.dataType()
        ).getBuffer();
    }

    /**
     * Setup an output map with empty buffers
     *
     * @param inter   interpreter
     * @param sigName signature name
     * @return a newly created output map that is setup with all output buffers
     */
    public static Map<String, Object> setupOutputMap(Interpreter inter, String sigName) {
        Map<String, Object> map = new HashMap<>();
        setupOutput(inter, map, sigName);
        return map;
    }

    /**
     * Setup an output map with empty buffers
     *
     * @param inter     interpreter
     * @param outputMap map to modify
     * @param sigName   signature name
     */
    public static void setupOutput(Interpreter inter, Map<String, Object> outputMap, String sigName) {
        for (String output : inter.getSignatureOutputs(sigName)) {
            ByteBuffer bb = getOutputBuffer(inter, output, sigName);
            outputMap.put(output, bb);
        }
    }

    /**
     * Create an EMPTY input buffer.
     * You probably don't want to do this, its easier to create a dynamic one and load an array in.
     * See createFloatBuffer for an example.
     * You will use this if you have an dummy argument that is not actually used.
     *
     * @param in        interpreter
     * @param inputName name of input
     * @param sigName   name of signature
     * @return empty input buffer
     */
    public static ByteBuffer getEmptyInputBuffer(Interpreter in, String inputName, String sigName) {
        Tensor t = in.getInputTensorFromSignature(inputName,
                sigName);
        return TensorBuffer.createFixedSize(
                t.shape(),
                t.dataType()
        ).getBuffer();
    }

    /**
     * Create a new ByteBuffer from a float array
     *
     * @param data  your data flattened. i.e. a shape of [2, 2, 2] means data.length == 8
     * @param shape shape description, i.e. [2, 2, 2] is a 2x2x2 matrix
     * @return a freshly allocated FLOAT32 buffer
     */
    public static ByteBuffer createFloatBuffer(float[] data, int[] shape) {
        TensorBuffer in = TensorBufferFloat.createDynamic(DataType.FLOAT32);
        in.loadArray(data, shape);
        return in.getBuffer();
    }

    /**
     * Turn a byte buffer into a float array.
     * Side effects: the buffer is modified. Be careful if re-using the buffer.
     *
     * @param buf byte buffer
     * @return new float array
     */
    public static float[] getFloatArray(ByteBuffer buf) {
        buf.rewind();
        FloatBuffer fb = buf.asFloatBuffer();
        float[] fa = new float[fb.capacity()];
        fb.get(fa);
        return fa;
    }
}

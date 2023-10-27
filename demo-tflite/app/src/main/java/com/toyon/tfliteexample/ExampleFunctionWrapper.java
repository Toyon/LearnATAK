/**
 * Copyright (c) 2023 Toyon Research Corporation. No Rights Reserved.
 * Report problems or provide feedback at: https://github.com/Toyon/LearnATAK/issues
 */
package com.toyon.tfliteexample;

import android.content.Context;
import android.util.Log;

import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.Tensor;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

/**
 * A wrapper for the get_weights and set_weights tf.functions
 * from the example exported tflite model (example.tflite).
 * example.tflite was exported from Python, see 'ml_training/tflite' for more information.
 * See comments below for the signatures of the get_weights and set_weights functions.
 * (This class is not thread-safe)
 */
public class ExampleFunctionWrapper {

    private final Interpreter interpreter;
    private final static String TAG = "ExampleFunctionWrapper";

    /**
     * @param context
     * @throws IOException
     */
    public ExampleFunctionWrapper(Context context) throws IOException {
        this.interpreter = TFLiteUtils.loadAsset(context, "example.tflite");
        // for debugging:
        TFLiteUtils.debugSignature(this.interpreter, "get_weights");
        TFLiteUtils.debugSignature(this.interpreter, "set_weights");
    }

    /**
     * Get the weights of the model. Corresponds to this signature:
     * <pre>
     * {@code
     *     @tf.function(input_signature=[
     *         tf.TensorSpec([1, 1], tf.float32)
     *     ])
     *     def get_weights(self, dummy_input):
     *         # ...
     *         return {'dense': dense, 'bias': bias}
     * }
     * </pre>
     *
     * @return the weights of the model (dense and bias inside a Weights container)
     */
    public Weights getWeights() {
        final String signature = "get_weights";
        final String inputName = "dummy_input"; // See signature arguments
        Map<String, Object> input = new HashMap<>();
        ByteBuffer dummy_buffer = TFLiteUtils.getEmptyInputBuffer(interpreter,
                inputName, signature);
        input.put(inputName,
                dummy_buffer);
        Map<String, Object> output = new HashMap<>();
        TFLiteUtils.setupOutput(interpreter, output, signature);

        this.interpreter.runSignature(input, output, signature);

        ByteBuffer biasBuffer = (ByteBuffer) output.get("bias");
        ByteBuffer denseBuffer = (ByteBuffer) output.get("dense");

        if (biasBuffer == null || denseBuffer == null) {
            Log.e(TAG, "Could not read expected output from `get_weights`!");
            return null;
        }

        float[] biasArray = TFLiteUtils.getFloatArray(biasBuffer);
        float[] denseArray = TFLiteUtils.getFloatArray(denseBuffer);
        return new Weights(denseArray, biasArray);
    }

    /**
     * Set the weights of the model. Corresponds to this signature:
     * <pre>
     * {@code
     *     @tf.function(input_signature=[
     *         tf.TensorSpec([INPUT_SIZE, DENSE_LAYER_SIZE], tf.float32)
     *     ])
     *     def set_weights(self, t: tf.Tensor):
     *         # ...
     *         return {'dense': dense, 'bias': bias}
     * }
     * </pre>
     *
     * @param newWeights new weights, must be correct size and flattened
     * @return the weights of the model (getDense() will be equal to newWeights if successful)
     */
    public Weights setWeights(float[] newWeights) {
        // newWeights is expected to be the correct size, INPUT_SIZE * DENSE_LAYER_SIZE
        final String signature = "set_weights";
        final String inputName = "t"; // See signature arguments
        Map<String, Object> input = new HashMap<>();
        Tensor t = this.interpreter.getInputTensorFromSignature(inputName, signature);
        ByteBuffer floatBuffer = TFLiteUtils.createFloatBuffer(newWeights, t.shape());
        input.put(inputName,
                floatBuffer);
        Map<String, Object> output = new HashMap<>();
        TFLiteUtils.setupOutput(interpreter, output, signature);

        this.interpreter.runSignature(input, output, signature);

        ByteBuffer biasBuffer = (ByteBuffer) output.get("bias");
        ByteBuffer denseBuffer = (ByteBuffer) output.get("dense");

        if (biasBuffer == null || denseBuffer == null) {
            Log.e(TAG, "Could not read expected output from `get_weights`!");
            return null;
        }

        float[] biasArray = TFLiteUtils.getFloatArray(biasBuffer);
        float[] denseArray = TFLiteUtils.getFloatArray(denseBuffer);
        return new Weights(denseArray, biasArray);
    }

    /**
     * Helper container for holding dense weights and biases
     */
    public static class Weights {

        private final float[] dense;
        private final float[] bias;

        public Weights(float[] dense, float[] bias) {
            this.dense = dense;
            this.bias = bias;
        }

        public float[] getDense() {
            return dense;
        }

        public float[] getBias() {
            return bias;
        }
    }
}

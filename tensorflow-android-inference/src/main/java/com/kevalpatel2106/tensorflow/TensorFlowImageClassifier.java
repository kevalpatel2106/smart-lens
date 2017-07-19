/*
 * Copyright 2017 Keval Patel.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.kevalpatel2106.tensorflow;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.util.Log;

import org.tensorflow.contrib.android.TensorFlowInferenceInterface;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * A classifier specialized to label images using TensorFlow.
 */
public class TensorFlowImageClassifier implements Classifier {

    private static final String TAG = TensorFlowImageClassifier.class.getSimpleName();

    // These are the settings for the original v1 Inception model. If you want to
    // use a model that's been produced from the TensorFlow for Poets codelab,
    // you'll need to set IMAGE_SIZE = 299, IMAGE_MEAN = 128, IMAGE_STD = 128,
    // INPUT_NAME = "Mul:0", and OUTPUT_NAME = "final_result:0".
    // You'll also need to update the MODEL_FILE and LABEL_FILE paths to point to
    // the ones you produced.
    private static final int INPUT_SIZE = 224;

    // These are the settings for the original v1 Inception model. If you want to
    // use a model that's been produced from the TensorFlow for Poets codelab,
    // you'll need to set IMAGE_SIZE = 299, IMAGE_MEAN = 128, IMAGE_STD = 128,
    // INPUT_NAME = "Mul:0", and OUTPUT_NAME = "final_result:0".
    // You'll also need to update the MODEL_FILE and LABEL_FILE paths to point to
    // the ones you produced.
    // Note: the actual number of classes for Inception is 1001, but the output layer size is 1008.
    private static final int NUM_CLASSES = 1008;
    private static final int IMAGE_MEAN = 117;
    private static final float IMAGE_STD = 1;
    private static final String INPUT_NAME = "input:0";
    private static final String OUTPUT_NAME = "output:0";

    private static final String MODEL_FILE = "file:///android_asset/tensorflow_inception_graph.pb";
    private static final String LABEL_FILE = "file:///android_asset/imagenet_comp_graph_label_strings.txt";


    // Only return this many results with at least this confidence.
    private static final int MAX_RESULTS = 3;
    private static final float THRESHOLD = 0.1f;

    // Config values.
    private String inputName;
    private String outputName;
    private int inputSize;
    private int imageMean;
    private float imageStd;

    // Pre-allocated buffers.
    private List<String> labels;
    private float[] floatValues;
    private float[] outputs;
    private String[] outputNames;
    private int[] mBmpPixelValues;

    private TensorFlowInferenceInterface mTensorFlowInferenceInterface;
    private Comparator<Recognition> mConfidenceComparator = new Comparator<Recognition>() {
        @Override
        public int compare(Recognition lhs, Recognition rhs) {
            // Intentionally reversed to put high confidence at the head of the queue.
            return Float.compare(rhs.getConfidence(), lhs.getConfidence());
        }
    };

    /**
     * Initializes a native TensorFlow session for classifying images.
     *
     * @param context The context from which to get the asset manager to be used to load assets.
     */
    public TensorFlowImageClassifier(Context context) {
        this(context.getAssets(),
                MODEL_FILE,
                LABEL_FILE,
                NUM_CLASSES,
                INPUT_SIZE,
                IMAGE_MEAN,
                IMAGE_STD,
                INPUT_NAME,
                OUTPUT_NAME);
    }

    /**
     * Initializes a native TensorFlow session for classifying images.
     *
     * @param assetManager  The asset manager to be used to load assets.
     * @param modelFilename The filepath of the model GraphDef protocol buffer.
     * @param labelFilename The filepath of label file for classes.
     * @param numClasses    The number of classes output by the model.
     * @param inputSize     The input size. A square image of inputSize x inputSize is assumed.
     * @param imageMean     The assumed mean of the image values.
     * @param imageStd      The assumed std of the image values.
     * @param inputName     The label of the image input node.
     * @param outputName    The label of the output node.
     */
    @SuppressWarnings("WeakerAccess")
    public TensorFlowImageClassifier(AssetManager assetManager,
                                     String modelFilename,
                                     String labelFilename,
                                     int numClasses,
                                     int inputSize,
                                     int imageMean,
                                     float imageStd,
                                     String inputName,
                                     String outputName) {
        this.inputName = inputName;
        this.outputName = outputName;

        // Read the label names into memory.
        String actualFilename = labelFilename.split("file:///android_asset/")[1];

        this.labels = readLabels(assetManager, actualFilename);
        Log.i(TAG, "Read " + labels.size() + ", " + numClasses + " specified");

        this.inputSize = inputSize;
        this.imageMean = imageMean;
        this.imageStd = imageStd;

        // Pre-allocate buffers.
        this.outputNames = new String[]{outputName};
        this.floatValues = new float[inputSize * inputSize * 3];
        this.outputs = new float[numClasses];
        this.mBmpPixelValues = new int[inputSize * inputSize];

        //Initialize TF
        mTensorFlowInferenceInterface = new TensorFlowInferenceInterface(assetManager, modelFilename);
    }

    /**
     * Read the labels from {@link #LABEL_FILE}.
     *
     * @param assetManager The asset manager to be used to load assets.
     * @param filename     Name of the label file. (By default it is {@link #LABEL_FILE}.)
     * @return Array list of label names.
     */
    private ArrayList<String> readLabels(AssetManager assetManager, String filename) {
        ArrayList<String> result = new ArrayList<>();
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(assetManager.open(filename)));
            String line;
            while ((line = br.readLine()) != null) {
                result.add(line);
            }
            br.close();
        } catch (IOException ex) {
            ex.printStackTrace();
            throw new IllegalStateException("Cannot read labels from " + filename);
        } finally {
            try {
                if (br != null) br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    /**
     * Classify image.
     *
     * @param bitmap Image to recognize.
     * @return List of top three confidant {@link com.kevalpatel2106.tensorflow.Classifier.Recognition}.
     */
    @Override
    public List<Recognition> recognizeImage(Bitmap bitmap) {
        bitmap = Bitmap.createScaledBitmap(bitmap,
                TensorFlowImageClassifier.INPUT_SIZE,
                TensorFlowImageClassifier.INPUT_SIZE,
                false);
        bitmap.getPixels(mBmpPixelValues,
                0, bitmap.getWidth(),
                0, 0,
                bitmap.getWidth(), bitmap.getHeight());

        // Preprocess the image data from 0-255 int to normalized float based
        // on the provided parameters.
        for (int i = 0; i < mBmpPixelValues.length; ++i) {
            final int val = mBmpPixelValues[i];
            floatValues[i * 3] = (((val >> 16) & 0xFF) - imageMean) / imageStd;
            floatValues[i * 3 + 1] = (((val >> 8) & 0xFF) - imageMean) / imageStd;
            floatValues[i * 3 + 2] = ((val & 0xFF) - imageMean) / imageStd;
        }

        // Copy the input data into TensorFlow.
        mTensorFlowInferenceInterface.feed(
                inputName, floatValues, 1, inputSize, inputSize, 3);

        // Run the inference call.
        mTensorFlowInferenceInterface.run(outputNames);

        // Copy the output Tensor back into the output array.
        mTensorFlowInferenceInterface.fetch(outputName, outputs);

        // Find the best classifications.
        ArrayList<Recognition> recognitions = new ArrayList<>();
        for (int i = 0; i < outputs.length; ++i) {
            if (outputs[i] > THRESHOLD) {
                recognitions.add(new Recognition("" + i, labels.get(i), outputs[i], null));
            }
        }

        //If no match found...
        if (recognitions.size() <= 0) return recognitions;

        //Sort based on the confidence level
        Collections.sort(recognitions, mConfidenceComparator);

        //Return max top 3 results.
        int recognitionsSize = Math.min(recognitions.size(), MAX_RESULTS);
        return recognitions.subList(0, recognitionsSize - 1);
    }

    /**
     * Close the TF and release resources.
     */
    @Override
    public void close() {
        mTensorFlowInferenceInterface.close();
    }
}
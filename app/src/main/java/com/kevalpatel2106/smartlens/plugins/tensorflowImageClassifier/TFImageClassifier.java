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

package com.kevalpatel2106.smartlens.plugins.tensorflowImageClassifier;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.kevalpatel2106.smartlens.downloader.ModelDownloadService;
import com.kevalpatel2106.smartlens.imageProcessors.imageClassifier.BaseImageClassifier;
import com.kevalpatel2106.smartlens.imageProcessors.imageClassifier.Recognition;

import org.tensorflow.contrib.android.TensorFlowInferenceInterface;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

/**
 * A classifier specialized to label images using TensorFlow.
 * <p>
 * numClasses The number of classes output by the model.
 * inputSize  The input size. A square image of inputSize x inputSize is assumed.
 * imageMean  The assumed mean of the image values.
 * imageStd   The assumed std of the image values.
 * inputName  The label of the image input node.
 * outputName The label of the output node.
 */
public final class TFImageClassifier extends BaseImageClassifier {

    private static final String TAG = TFImageClassifier.class.getSimpleName();

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


    // Only return this many results with at least this confidence.
    private static final int MAX_RESULTS = 3;
    private static final float THRESHOLD = 0.1f;
    TensorFlowInferenceInterface mTensorFlowInferenceInterface;
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
    private Comparator<Recognition> mConfidenceComparator = (lhs, rhs) -> {
        // Intentionally reversed to put high confidence at the head of the queue.
        return Float.compare(rhs.getConfidence(), lhs.getConfidence());
    };

    /**
     * Initializes a native TensorFlow session for classifying images.
     *
     * @param context The context from which to get the asset manager to be used to load assets.
     */
    public TFImageClassifier(Context context) {
        super(context);
    }

    @Override
    public void init() {
        //Check if the models downloaded?
        if (!isModelDownloaded())
            throw new RuntimeException("Models are not downloaded yet. Download them first.");

        this.inputName = INPUT_NAME;
        this.outputName = OUTPUT_NAME;

        // Read the label names into memory.
        this.labels = readLabels(TFUtils.getImageLabels(getContext()));
        Log.i(TAG, "Read " + labels.size() + ", " + NUM_CLASSES + " specified");

        this.inputSize = INPUT_SIZE;
        this.imageMean = IMAGE_MEAN;
        this.imageStd = IMAGE_STD;

        // Pre-allocate buffers.
        this.outputNames = new String[]{outputName};
        this.floatValues = new float[inputSize * inputSize * 3];
        this.outputs = new float[NUM_CLASSES];
        this.mBmpPixelValues = new int[inputSize * inputSize];

        //Initialize TF
        mTensorFlowInferenceInterface = new TensorFlowInferenceInterface(getContext().getAssets(),
                TFUtils.getImageGraph(getContext()).getAbsolutePath());
    }

    /**
     * Read the labels from {@link TFUtils#getImageLabels(Context)}.
     *
     * @param labelFile Name of the label file. (By default it is {@link TFUtils#getImageLabels(Context)}.)
     * @return Array list of label names.
     */
    private ArrayList<String> readLabels(File labelFile) {
        ArrayList<String> result = new ArrayList<>();
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(labelFile));
            String line;
            while ((line = br.readLine()) != null) {
                result.add(line);
            }
            br.close();
        } catch (IOException ex) {
            ex.printStackTrace();
            throw new IllegalStateException("Cannot read labels from " + labelFile);
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
     * @return List of top three confidant {@link Recognition}.
     */
    @Override
    public List<Recognition> scan(Bitmap bitmap) {
        bitmap = Bitmap.createScaledBitmap(bitmap,
                TFImageClassifier.INPUT_SIZE,
                TFImageClassifier.INPUT_SIZE,
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

    @Override
    public boolean isSafeToStart() {
        return mTensorFlowInferenceInterface != null;
    }

    /**
     * Close the TF and release resources.
     */
    @Override
    public void close() {
        mTensorFlowInferenceInterface.close();
        mTensorFlowInferenceInterface = null;
    }

    @Override
    public void downloadModels() {
        HashMap<String, File> downloads = new HashMap<>();
        downloads.put(TFUtils.TENSORFLOW_GRAPH_URL, TFUtils.getImageGraph(getContext()));
        downloads.put(TFUtils.TENSORFLOW_LABEL_URL, TFUtils.getImageLabels(getContext()));
        ModelDownloadService.startDownload(getContext(), downloads);
    }

    @Override
    public boolean isModelDownloaded() {
        return TFUtils.isModelsDownloaded(getContext());
    }
}
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

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.kevalpatel2106.smartlens.imageProcessors.imageClassifier.Recognition;
import com.kevalpatel2106.smartlens.testUtils.BaseTestClass;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by Keval on 21-Jul-17.
 */
@RunWith(AndroidJUnit4.class)
public class TFImageClassifierTest extends BaseTestClass {
    private TFImageClassifier mTFImageClassifier;

    @Before
    public void init() {
        System.gc();
        mTFImageClassifier = new TFImageClassifier(InstrumentationRegistry.getTargetContext());
        mTFImageClassifier.init();
    }

    @Test
    public void canRecognizeImage() throws Exception {
        validateImageRecognition(com.kevalpatel2106.smartlens.R.drawable.keyboard, "keyboard");
        validateImageRecognition(com.kevalpatel2106.smartlens.R.drawable.laptop, "laptop");

        //Check if can close?
        mTFImageClassifier.close();
        assertNull(mTFImageClassifier.mTensorFlowInferenceInterface);
    }

    private void validateImageRecognition(int imageRes, String correctLabel) {
        //Generate mock image
        Bitmap mockImage = BitmapFactory.decodeResource(InstrumentationRegistry
                .getTargetContext()
                .getResources(), imageRes);
        assertNotNull("Test image not found. Did you set correctly?", mockImage);

        // Classify image
        List<Recognition> labels = mTFImageClassifier.scan(mockImage);

        //Validate cases
        assertNotNull(labels);
        assertTrue(!labels.isEmpty());

        boolean isMatched = false;
        for (Recognition recognition : labels) {
            if (recognition.getTitle().contains(correctLabel)) {
                isMatched = true;
                break;
            }
        }
        assertTrue(isMatched);
        //noinspection UnusedAssignment
        mockImage = null;
    }

    @Override
    public Activity getActivity() {
        return null;
    }
}
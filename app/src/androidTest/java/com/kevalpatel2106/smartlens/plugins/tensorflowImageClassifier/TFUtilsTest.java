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
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.kevalpatel2106.smartlens.testUtils.BaseTestClass;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by Keval Patel on 05/08/17.
 *
 * @author 'https://github.com/kevalpatel2106'
 */
@RunWith(AndroidJUnit4.class)
public class TFUtilsTest extends BaseTestClass {
    @Test
    public void checkImageGraphFile() throws Exception {
        File file = TFUtils.getImageGraph(InstrumentationRegistry.getTargetContext());

        assertNotNull(file);
        assertTrue(file.getName().contains(".pb"));
    }

    @Test
    public void checkImageLabelFiles() throws Exception {
        File file = TFUtils.getImageLabels(InstrumentationRegistry.getTargetContext());

        assertNotNull(file);
        assertTrue(file.getName().contains(".txt"));
    }

    @Test
    public void checkModelDownload() throws Exception {
        File imageGraph = TFUtils.getImageGraph(InstrumentationRegistry.getTargetContext());
        File imageLabels = TFUtils.getImageLabels(InstrumentationRegistry.getTargetContext());

        if (imageGraph.exists() && imageGraph.length() > 0
                && imageLabels.exists() && imageLabels.length() > 0) {
            assertTrue(TFUtils.isModelsDownloaded(InstrumentationRegistry.getTargetContext()));
        } else {
            assertFalse(TFUtils.isModelsDownloaded(InstrumentationRegistry.getTargetContext()));
        }
    }

    @Override
    public Activity getActivity() {
        return null;
    }
}
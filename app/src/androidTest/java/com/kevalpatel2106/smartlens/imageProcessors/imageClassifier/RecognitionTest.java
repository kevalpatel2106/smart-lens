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

package com.kevalpatel2106.smartlens.imageProcessors.imageClassifier;

import android.app.Activity;
import android.graphics.RectF;
import android.support.test.runner.AndroidJUnit4;

import com.kevalpatel2106.smartlens.testUtils.BaseTestClass;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;

/**
 * Created by Keval Patel on 29/07/17.
 *
 * @author 'https://github.com/kevalpatel2106'
 */
@RunWith(AndroidJUnit4.class)
public class RecognitionTest extends BaseTestClass {

    @Test
    public void canInitiate() {
        String mockId = "1";
        String mockTitle = "Computer";
        float mockConfidence = 96.45f;
        RectF rectF = new RectF();

        Recognition recognition = new Recognition(mockId, mockTitle, mockConfidence, rectF);
        assertEquals(recognition.getId(), mockId);
        assertEquals(recognition.getTitle(), mockTitle);
        assertEquals(recognition.getConfidence(), mockConfidence, 0f);
        assertEquals(recognition.getLocation(), rectF);
    }

    @Override
    public Activity getActivity() {
        return null;
    }
}
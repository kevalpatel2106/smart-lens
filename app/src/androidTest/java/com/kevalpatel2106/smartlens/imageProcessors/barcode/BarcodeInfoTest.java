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

package com.kevalpatel2106.smartlens.imageProcessors.barcode;

import android.app.Activity;
import android.graphics.Rect;
import android.support.test.runner.AndroidJUnit4;

import com.kevalpatel2106.smartlens.testUtils.BaseTestClass;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;

/**
 * Created by Keval Patel on 05/08/17.
 *
 * @author 'https://github.com/kevalpatel2106'
 */
@RunWith(AndroidJUnit4.class)
public class BarcodeInfoTest extends BaseTestClass {

    @Test
    public void checkBoundingBox() throws Exception {
        //Set the bound
        Rect rect = new Rect(1, 2, 3, 4);
        BarcodeInfo barcodeInfo = new BarcodeInfo();
        barcodeInfo.setBoundingBox(rect);

        assertEquals(barcodeInfo.getBoundingBox(), rect);
    }

    @Override
    public Activity getActivity() {
        return null;
    }
}
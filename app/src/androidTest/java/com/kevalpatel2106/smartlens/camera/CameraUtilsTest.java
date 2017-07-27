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

package com.kevalpatel2106.smartlens.camera;

import android.app.Activity;
import android.graphics.Bitmap;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.kevalpatel2106.smartlens.camera.config.CameraImageFormat;
import com.kevalpatel2106.smartlens.camera.config.CameraRotation;
import com.kevalpatel2106.smartlens.testUtils.BaseTestClass;
import com.kevalpatel2106.smartlens.utils.FileUtils;
import com.kevalpatel2106.smartlens.utils.Utils;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Created by Keval on 20-Jul-17.
 */
@RunWith(AndroidJUnit4.class)
public final class CameraUtilsTest extends BaseTestClass {

    @Test
    public void canInitiate() throws Exception {
        try {
            Class<?> c = Class.forName("CameraUtils");
            Constructor constructor = c.getDeclaredConstructors()[0];
            constructor.newInstance();
            fail("Should have thrown Arithmetic exception");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void checkIfBitmapRotate() throws Exception {
        //Generate mock bmp
        int mockBmpWidth = 100;
        int mockBmpHeight = 50;
        Bitmap mockBmp = Bitmap.createBitmap(mockBmpWidth, mockBmpHeight, Bitmap.Config.RGB_565);

        //Rotate by 90.
        Bitmap rotatedBmp = CameraUtils.rotateBitmap(mockBmp, CameraRotation.ROTATION_90);

        //Validate conditions
        assertNotNull(rotatedBmp);
        assertEquals(rotatedBmp.getWidth(), mockBmpHeight);
        assertEquals(rotatedBmp.getHeight(), mockBmpWidth);
    }

    @Test
    public void checkIfCameraPermissionGranted() throws Exception {
        assertTrue(CameraUtils.checkIfCameraPermissionGranted(InstrumentationRegistry.getTargetContext()));
    }

    @Override
    public Activity getActivity() {
        return null;
    }
}
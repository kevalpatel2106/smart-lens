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

package com.kevalpatel2106.smartlens.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.kevalpatel2106.smartlens.camera.config.CameraImageFormat;
import com.kevalpatel2106.smartlens.testUtils.BaseTestClass;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Created by Keval on 19-Jul-17.
 */
@RunWith(AndroidJUnit4.class)
public final class UtilsTest extends BaseTestClass {

    /**
     * Test for {@link Utils#getDeviceName()}.
     */
    @Test
    public void getDeviceName() throws Exception {
        assertNotNull(Utils.getDeviceName());
        assertTrue(Utils.getDeviceName().contains("-"));
    }

    @SuppressLint("WrongConstant")
    @Test
    public void checkIfSaveFileWorks() throws Exception {
        //Generate mock bmp
        int mockBmpWidth = 100;
        int mockBmpHeight = 50;
        Bitmap mockBmp = Bitmap.createBitmap(mockBmpWidth, mockBmpHeight, Bitmap.Config.RGB_565);

        //Create mock file with cache directory. Cache file has always write permission.
        File mockSaveFile = new File(FileUtils.getCacheDir(InstrumentationRegistry.getTargetContext())
                + "/IMG" + System.currentTimeMillis() + ".jpg");

        //Check the output of jpg
        assertTrue(Utils.saveImageFromFile(mockBmp,
                mockSaveFile,
                CameraImageFormat.FORMAT_JPEG));
        assertTrue(mockSaveFile.length() > 0);

        //Check with the webp
        //Create mock file with cache directory. Cache file has always write permission.
        mockSaveFile = new File(FileUtils.getCacheDir(InstrumentationRegistry.getTargetContext())
                + "/IMG" + System.currentTimeMillis() + ".webp");
        assertTrue(Utils.saveImageFromFile(mockBmp,
                mockSaveFile,
                CameraImageFormat.FORMAT_WEBP));
        assertTrue(mockSaveFile.length() > 0);


        //Try with invalid format
        try {
            Utils.saveImageFromFile(mockBmp, mockSaveFile, 734);
            fail();
        } catch (IllegalArgumentException e) {
            //Pass
        }
    }

    @Test
    public void canInitiate() throws Exception {
        try {
            Class<?> c = Class.forName("Utils");
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
    public void getDeviceId() throws Exception {
        assertNotNull(Utils.getDeviceId(InstrumentationRegistry.getTargetContext()));
    }

    @Override
    public Activity getActivity() {
        return null;
    }
}
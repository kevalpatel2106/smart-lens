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

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Environment;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.kevalpatel2106.smartlens.camera.config.CameraFacing;
import com.kevalpatel2106.smartlens.camera.config.CameraImageFormat;
import com.kevalpatel2106.smartlens.camera.config.CameraResolution;
import com.kevalpatel2106.smartlens.camera.config.CameraRotation;
import com.kevalpatel2106.smartlens.testUtils.BaseTestClass;
import com.kevalpatel2106.smartlens.utils.FileUtils;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Created by Keval on 20-Jul-17.
 */
@RunWith(AndroidJUnit4.class)
public final class CameraConfigTest extends BaseTestClass {
    private CameraConfig.Builder mBuilder;

    @Before
    public void init() throws Exception {
        mBuilder = new CameraConfig().getBuilder(InstrumentationRegistry.getTargetContext());
    }

    @Test
    public void canInitiate() throws Exception {
        assertNotNull(mBuilder);
        assertNotNull(mBuilder.build());
    }

    @Test
    public void checkDefaultValues() throws Exception {
        CameraConfig cameraConfig = mBuilder.build();
        assertEquals(cameraConfig.getImageRotation(), CameraRotation.ROTATION_0);
        assertEquals(cameraConfig.getImageFormat(), CameraImageFormat.FORMAT_JPEG);
        assertEquals(cameraConfig.getFacing(), CameraFacing.REAR_FACING_CAMERA);
        assertEquals(cameraConfig.getResolution(), CameraResolution.MEDIUM_RESOLUTION);
        assertTrue(mBuilder.build().getImageFile().getAbsolutePath().contains(
                FileUtils.getCacheDir(InstrumentationRegistry.getTargetContext()).getAbsolutePath()));
    }

    @SuppressLint("WrongConstant")
    @Test
    public void checkSetResolution() throws Exception {
        // Valid input check
        mBuilder.setCameraResolution(CameraResolution.HIGH_RESOLUTION);
        assertEquals(mBuilder.build().getResolution(), CameraResolution.HIGH_RESOLUTION);

        // Invalid input check
        try {
            mBuilder.setCameraResolution(453);
            fail();
        } catch (IllegalArgumentException e) {
            //Success
        }
    }

    @SuppressLint("WrongConstant")
    @Test
    public void checkSetFacing() throws Exception {
        // Check if it throws error when device has no camera
        try {
            mBuilder.setCameraFacing(CameraFacing.REAR_FACING_CAMERA);
            assertEquals(mBuilder.build().getFacing(), CameraFacing.REAR_FACING_CAMERA);
        } catch (IllegalStateException e) {
            if (CameraUtils.isCameraAvailable(InstrumentationRegistry.getTargetContext())) {
                //Device has camera. Test fail
                fail();
            }
        }

        // Valid input check with front facing camera.
        try {
            mBuilder.setCameraFacing(CameraFacing.FRONT_FACING_CAMERA);
            assertEquals(mBuilder.build().getFacing(), CameraFacing.FRONT_FACING_CAMERA);
        } catch (IllegalStateException e) {
            if (CameraUtils.isFrontCameraAvailable(InstrumentationRegistry.getTargetContext())) {
                //Fail. Device have front camera
                fail();
            }
        }

        // Invalid input check
        try {
            mBuilder.setCameraFacing(3);
            fail();
        } catch (IllegalArgumentException e) {
            //Success
        }
    }

    @SuppressLint("WrongConstant")
    @Test
    public void checkSetImageFormat() throws Exception {
        // Valid input check
        mBuilder.setImageFormat(CameraImageFormat.FORMAT_JPEG);
        assertEquals(mBuilder.build().getImageFormat(), CameraImageFormat.FORMAT_JPEG);

        // Invalid input check
        try {
            mBuilder.setImageFormat(3);
            fail();
        } catch (IllegalArgumentException e) {
            //Success
        }
    }

    @Test
    public void checkSetImageFile() throws Exception {
        //Check with the file
        File mockFile = new File(Environment.getExternalStorageDirectory()
                + "/IMG" + System.currentTimeMillis() + "jpg");
        mBuilder.setImageFile(mockFile);
        assertNotNull(mBuilder.build().getImageFile());
        assertEquals(mBuilder.build().getImageFile().getAbsolutePath(), mockFile.getAbsolutePath());

        // Check with the null file.
        mBuilder.setImageFile(null);
        assertNotNull(mBuilder.build().getImageFile());
        assertTrue(mBuilder.build().getImageFile().getAbsolutePath().contains(
                FileUtils.getCacheDir(InstrumentationRegistry.getTargetContext()).getAbsolutePath()));

    }

    @SuppressLint("WrongConstant")
    @Test
    public void checkSetImageRotation() throws Exception {
        mBuilder.setImageRotation(CameraRotation.ROTATION_180);
        assertEquals(mBuilder.build().getImageRotation(), CameraRotation.ROTATION_180);

        // Invalid input check
        try {
            mBuilder.setImageRotation(3);
            fail();
        } catch (IllegalArgumentException e) {
            //Success
        }
    }

    @Override
    public Activity getActivity() {
        return null;
    }
}
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
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.kevalpatel2106.smartlens.testUtils.BaseTestClass;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

/**
 * Created by Keval on 22-Jul-17.
 */
@RunWith(AndroidJUnit4.class)
public class CameraPreviewTest extends BaseTestClass {

    @SuppressWarnings("ConstantConditions")
    @Test
    public void canInitiate() {
        new Handler(Looper.getMainLooper()).post(() -> {
            CameraPreview cameraPreview = new CameraPreview(InstrumentationRegistry.getTargetContext(),
                    new CameraCallbacks() {
                        @Override
                        public void onImageCapture(@NonNull Bitmap imageCaptured) {

                        }

                        @Override
                        public void onCameraError(int errorCode) {

                        }
                    });
            assertNotNull(cameraPreview);

            try {
                new CameraPreview(InstrumentationRegistry.getTargetContext(), null);
                fail("Should have thrown Arithmetic exception");
            } catch (IllegalArgumentException e) {
                //success
            }
        });
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    public void canClose() {
        new Handler(Looper.getMainLooper()).post(() -> {
            CameraPreview cameraPreview = new CameraPreview(InstrumentationRegistry.getTargetContext(),
                    new CameraCallbacks() {
                        @Override
                        public void onImageCapture(@NonNull Bitmap imageCaptured) {

                        }

                        @Override
                        public void onCameraError(int errorCode) {

                        }
                    });
            try {
                cameraPreview.stopPreviewAndReleaseCamera();
                assertFalse(cameraPreview.isCameraOpen());
            } catch (Exception e) {
                fail("Cannot close the camera.");
            }
        });
    }

    @Override
    public Activity getActivity() {
        return null;
    }
}
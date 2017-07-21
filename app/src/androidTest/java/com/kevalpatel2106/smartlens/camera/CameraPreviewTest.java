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
import android.support.annotation.NonNull;
import android.support.test.InstrumentationRegistry;

import com.kevalpatel2106.smartlens.testUtils.BaseTestClass;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * Created by Keval on 21-Jul-17.
 */
public class CameraPreviewTest extends BaseTestClass implements CameraCallbacks {
    @Rule
    public ExpectedException mException = ExpectedException.none();
    private CameraPreview mCameraPreview;

    @Before
    public void initialize() {
        mCameraPreview = new CameraPreview(InstrumentationRegistry.getTargetContext(), this);
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    public void checkConstructor() throws Exception {
        mException.expect(IllegalArgumentException.class);
        mCameraPreview = new CameraPreview(null, this);
        mCameraPreview = new CameraPreview(InstrumentationRegistry.getTargetContext(), null);
    }

    @Test
    public void startCamera() throws Exception {
    }

    @Test
    public void stopPreviewAndReleaseCamera() throws Exception {
    }

    @Override
    public Activity getActivity() {
        return null;
    }

    @Override
    public void onImageCapture(@NonNull Bitmap imageCaptured) {

    }

    @Override
    public void onCameraError(int errorCode) {

    }
}
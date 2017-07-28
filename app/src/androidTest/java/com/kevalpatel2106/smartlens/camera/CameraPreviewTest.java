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

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.hardware.Camera;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.base.MainThread;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.rule.GrantPermissionRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.kevalpatel2106.smartlens.R;
import com.kevalpatel2106.smartlens.TestActivity;
import com.kevalpatel2106.smartlens.camera.config.CameraResolution;
import com.kevalpatel2106.smartlens.testUtils.BaseTestClass;
import com.kevalpatel2106.smartlens.testUtils.Delay;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

/**
 * Created by Keval on 22-Jul-17.
 */
@LargeTest
@RunWith(AndroidJUnit4.class)
public class CameraPreviewTest extends BaseTestClass {
    @Rule
    public GrantPermissionRule mRuntimePermissionRule = GrantPermissionRule.grant(Manifest.permission.CAMERA);
    @Rule
    public ActivityTestRule<TestActivity> mActivityTestRule = new ActivityTestRule<>(TestActivity.class);

    private CameraCallbacks mMockCallbacks = new CameraCallbacks() {
        @Override
        public void onImageCapture(@NonNull byte[] imageCaptured) {
            //Do nothing
        }

        @Override
        public void onCameraError(int errorCode) {
            //Do nothing
        }
    };

    private CameraPreview mCameraPreview;

    @MainThread
    public void launchCameraPreview(@NonNull CameraCallbacks cameraCallbacks,
                                    @NonNull CameraConfig cameraConfig) {
        InstrumentationRegistry.getInstrumentation().runOnMainSync(() -> {
            FrameLayout frameLayout = mActivityTestRule.getActivity().findViewById(R.id.container);
            frameLayout.removeAllViews();

            //Add the camera preview.
            mCameraPreview = new CameraPreview(getActivity(), cameraCallbacks);
            mCameraPreview.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT));
            frameLayout.addView(mCameraPreview);

            mCameraPreview.startCamera(cameraConfig);
        });

        //Wait for the camera to start
        Delay.startDelay(5000);
        Espresso.onView(ViewMatchers.withId(R.id.toolbar)).perform(ViewActions.closeSoftKeyboard());
        Delay.stopDelay();
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    public void canInitiate() throws Exception {
        new Handler(Looper.getMainLooper()).post(() -> {
            CameraPreview cameraPreview = new CameraPreview(InstrumentationRegistry.getTargetContext(),
                    mMockCallbacks);
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
    public void canClose() throws Exception {
        InstrumentationRegistry.getInstrumentation().runOnMainSync(() -> {
            CameraPreview cameraPreview = new CameraPreview(InstrumentationRegistry.getTargetContext(),
                    mMockCallbacks);
            try {
                cameraPreview.stopPreviewAndReleaseCamera();
                assertFalse(cameraPreview.isCameraOpen());
            } catch (Exception e) {
                fail("Cannot close the camera.");
            }
        });
    }

    @SuppressLint("WrongConstant")
    @Test
    public void checkPictureSize() throws Exception {
        launchCameraPreview(mMockCallbacks, new CameraConfig());

        List<Camera.Size> pictureSizes = mCameraPreview.getCamera()
                .getParameters()
                .getSupportedPictureSizes();

        //Check for the high res
        assertEquals(mCameraPreview.getValidPictureSize(pictureSizes,
                CameraResolution.HIGH_RESOLUTION), pictureSizes.get(0));

        //Check for the medium res
        assertEquals(mCameraPreview.getValidPictureSize(pictureSizes,
                CameraResolution.MEDIUM_RESOLUTION), pictureSizes.get(pictureSizes.size() / 2));

        //Check for the low res
        assertEquals(mCameraPreview.getValidPictureSize(pictureSizes,
                CameraResolution.LOW_RESOLUTION), pictureSizes.get(pictureSizes.size() - 1));

        //Check for invalid resolution
        try {
            mCameraPreview.getValidPictureSize(pictureSizes, 123);
            fail();
        } catch (IllegalArgumentException e) {
            //Pass
        }

        //Check for empty supported picture sizes
        try {
            pictureSizes.clear();
            mCameraPreview.getValidPictureSize(pictureSizes, 123);
            fail();
        } catch (IllegalArgumentException e) {
            //Pass
        }
    }

    @Test
    public void checkPreviewSize() throws Exception {
        launchCameraPreview(mMockCallbacks, new CameraConfig());

        List<Camera.Size> previewSizes = mCameraPreview.getCamera()
                .getParameters()
                .getSupportedPreviewSizes();

        //Check for empty supported picture sizes
        try {
            previewSizes.clear();
            mCameraPreview.getValidPreviewSize(previewSizes);
            fail();
        } catch (IllegalArgumentException e) {
            //Pass
        }
    }

    @SuppressLint("WrongConstant")
    @Test
    public void checkFlashMode() throws Exception {
        launchCameraPreview(mMockCallbacks, new CameraConfig());

        Camera.Parameters parameters = mCameraPreview.getCamera().getParameters();

        if (parameters.getSupportedFlashModes() == null)
            assertEquals(mCameraPreview.getFlashMode(parameters), null);
        else if (parameters.getSupportedFlashModes().contains(Camera.Parameters.FLASH_MODE_AUTO))
            assertEquals(mCameraPreview.getFlashMode(parameters), Camera.Parameters.FLASH_MODE_AUTO);
        else
            assertEquals(mCameraPreview.getFlashMode(parameters), Camera.Parameters.FLASH_MODE_ON);
    }

    @SuppressLint("WrongConstant")
    @Test
    public void checkFocusMode() throws Exception {
        launchCameraPreview(mMockCallbacks, new CameraConfig());

        Camera.Parameters parameters = mCameraPreview.getCamera().getParameters();

        if (parameters.getSupportedFocusModes() == null)
            assertEquals(mCameraPreview.getFocusMode(parameters), null);
        else if (parameters.getSupportedFocusModes().contains(Camera.Parameters.FOCUS_MODE_AUTO))
            assertEquals(mCameraPreview.getFocusMode(parameters), Camera.Parameters.FOCUS_MODE_AUTO);
        else
            assertEquals(mCameraPreview.getFocusMode(parameters), null);
    }

    @Override
    public Activity getActivity() {
        return mActivityTestRule.getActivity();
    }
}
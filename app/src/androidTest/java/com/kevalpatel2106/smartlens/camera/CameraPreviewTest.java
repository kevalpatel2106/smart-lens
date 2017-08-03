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
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.rule.GrantPermissionRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.kevalpatel2106.smartlens.R;
import com.kevalpatel2106.smartlens.TestActivity;
import com.kevalpatel2106.smartlens.camera.camera1.CameraPreview;
import com.kevalpatel2106.smartlens.camera.config.CameraResolution;
import com.kevalpatel2106.smartlens.testUtils.BaseTestClass;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static org.junit.Assert.assertEquals;
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
        public void onImageCapture(@NonNull Bitmap bitmap) {

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
        FrameLayout frameLayout = mActivityTestRule.getActivity().findViewById(R.id.container);
        frameLayout.removeAllViews();

        //Add the camera preview.
        mCameraPreview = new CameraPreview(getActivity(), cameraCallbacks);
        mCameraPreview.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        frameLayout.addView(mCameraPreview);

        mCameraPreview.startCamera(cameraConfig);

        try {
            Thread.sleep(30000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("WrongConstant")
    @Test
    public void checkFocusMode() throws Exception {
        InstrumentationRegistry.getInstrumentation().runOnMainSync(() -> {
            launchCameraPreview(mMockCallbacks, new CameraConfig());

            Camera.Parameters parameters = mCameraPreview.getCamera().getParameters();

            if (parameters.getSupportedFocusModes() == null)
                assertEquals(CameraPreview.getFocusMode(parameters), null);
            else if (parameters.getSupportedFocusModes().contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE))
                assertEquals(CameraPreview.getFocusMode(parameters), Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
            else if (parameters.getSupportedFocusModes().contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO))
                assertEquals(CameraPreview.getFocusMode(parameters), Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
            else
                assertEquals(CameraPreview.getFocusMode(parameters), null);
        });
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    public void canInitiate() throws Exception {
        InstrumentationRegistry.getInstrumentation().runOnMainSync(() -> {
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

    @SuppressLint("WrongConstant")
    @Test
    public void checkPictureSize() throws Exception {
        InstrumentationRegistry.getInstrumentation().runOnMainSync(() -> {
            launchCameraPreview(mMockCallbacks, new CameraConfig());
        });

        List<Camera.Size> pictureSizes = mCameraPreview.getCamera()
                .getParameters()
                .getSupportedPictureSizes();

        //Check for the high res
        assertEquals(CameraPreview.getValidPictureSize(pictureSizes,
                CameraResolution.HIGH_RESOLUTION), pictureSizes.get(0));

        //Check for the medium res
        assertEquals(CameraPreview.getValidPictureSize(pictureSizes,
                CameraResolution.MEDIUM_RESOLUTION), pictureSizes.get(pictureSizes.size() / 2));

        //Check for the low res
        assertEquals(CameraPreview.getValidPictureSize(pictureSizes,
                CameraResolution.LOW_RESOLUTION), pictureSizes.get(pictureSizes.size() - 1));

        //Check for invalid resolution
        try {
            CameraPreview.getValidPictureSize(pictureSizes, 123);
            fail();
        } catch (IllegalArgumentException e) {
            //Pass
        }

        //Check for empty supported picture sizes
        try {
            pictureSizes.clear();
            CameraPreview.getValidPictureSize(pictureSizes, 123);
            fail();
        } catch (IllegalArgumentException e) {
            //Pass
        }
    }


    @Override
    public Activity getActivity() {
        return mActivityTestRule.getActivity();
    }
}
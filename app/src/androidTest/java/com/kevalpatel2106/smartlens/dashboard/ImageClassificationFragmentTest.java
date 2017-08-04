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

package com.kevalpatel2106.smartlens.dashboard;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.NoMatchingViewException;

import com.kevalpatel2106.smartlens.R;
import com.kevalpatel2106.smartlens.camera.CameraError;
import com.kevalpatel2106.smartlens.camera.CameraUtils;
import com.kevalpatel2106.smartlens.testUtils.BaseTestClass;
import com.kevalpatel2106.smartlens.testUtils.Delay;
import com.kevalpatel2106.smartlens.testUtils.FragmentTestRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Created by Keval on 20-Jul-17.
 */
public class ImageClassificationFragmentTest extends BaseTestClass {

    @Rule
    public FragmentTestRule<ImageClassifierFragment> mCameraFragmentTestRule =
            new FragmentTestRule<>(ImageClassifierFragment.class);
    private ImageClassifierFragment mImageClassifierFragment;

    @Before
    public void init() {

        if (CameraUtils.isCameraAvailable(InstrumentationRegistry.getTargetContext())) {
            mCameraFragmentTestRule.launchActivity(null);
        } else {
            try {
                mCameraFragmentTestRule.launchActivity(null);
                fail("Should not initialize this ImageClassifierFragment if camera not there.");
            } catch (IllegalArgumentException e) {
                //Success
            }
        }

        //Wait for 1200 ms.
        //Wait for the camera to get stable
        Delay.startDelay(1200);
        Delay.stopDelay();
        Espresso.onView(withId(R.id.container));

        mImageClassifierFragment = mCameraFragmentTestRule.getFragment();
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    public void checkNewInstance() throws Exception {
        assertTrue(ImageClassifierFragment.getNewInstance() instanceof ImageClassifierFragment);
    }

    @Test
    public void checkIfInitialized() throws Exception {
        assertNotNull(mImageClassifierFragment);
        assertNotNull(mImageClassifierFragment.mCamera2Api);
        assertNotNull(mImageClassifierFragment.mImageClassifier);

        mImageClassifierFragment.mCamera2Api.closeCamera();
    }

    @Test
    public void checkCameraPermissionUnavailableError() throws Exception {
        //Doesn't have permission
        mImageClassifierFragment.onCameraError(CameraError.ERROR_CAMERA_PERMISSION_NOT_AVAILABLE);
        try {
            Espresso.onView(withId(android.support.design.R.id.snackbar_text))
                    .check(matches(isDisplayed()));
            fail("Snackbar should not display.");
        } catch (NoMatchingViewException e) {
            //Pass
        }
        mImageClassifierFragment.mCamera2Api.closeCamera();
    }

    @Test
    public void checkCameraOpenFailError() throws Exception {
        //Generate open failed.
        mImageClassifierFragment.onCameraError(CameraError.ERROR_CAMERA_OPEN_FAILED);
        Espresso.onView(allOf(withId(android.support.design.R.id.snackbar_text),
                withText(R.string.image_classifier_frag_error_camera_open)))
                .check(matches(isDisplayed()));
        mImageClassifierFragment.mCamera2Api.closeCamera();
    }

    @SuppressLint("WrongConstant")
    @Test
    public void checkUnrecognizedCameraError() throws Exception {
        //Generate any other error.
        mImageClassifierFragment.onCameraError(785);
        Espresso.onView(allOf(withId(android.support.design.R.id.snackbar_text),
                withText(R.string.image_classifier_frag_error_camera_open)))
                .check(matches(isDisplayed()));

        mImageClassifierFragment.mCamera2Api.closeCamera();
    }

    @Test
    public void checkNoFrontCameraError() throws Exception {
        //Generate doesn't have the front camera
        mImageClassifierFragment.onCameraError(CameraError.ERROR_DOES_NOT_HAVE_FRONT_CAMERA);
        Espresso.onView(allOf(withId(android.support.design.R.id.snackbar_text),
                withText(R.string.image_classifier_frag_error_no_front_camera)))
                .check(matches(isDisplayed()));
        mImageClassifierFragment.mCamera2Api.closeCamera();
    }

    @Test
    public void checkSaveFailedError() throws Exception {
        //Generate cannot save image
        mImageClassifierFragment.onCameraError(CameraError.ERROR_IMAGE_WRITE_FAILED);
        Espresso.onView(allOf(withId(android.support.design.R.id.snackbar_text),
                withText(R.string.image_classifier_frag_error_save_image)))
                .check(matches(isDisplayed()));
        mImageClassifierFragment.mCamera2Api.closeCamera();
    }

    @Override
    public Activity getActivity() {
        return mCameraFragmentTestRule.getActivity();
    }
}
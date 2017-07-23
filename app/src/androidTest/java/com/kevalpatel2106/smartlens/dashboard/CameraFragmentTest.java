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

import android.app.Activity;
import android.support.test.InstrumentationRegistry;

import com.kevalpatel2106.smartlens.camera.CameraUtils;
import com.kevalpatel2106.smartlens.testUtils.BaseTestClass;
import com.kevalpatel2106.smartlens.testUtils.FragmentTestRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Created by Keval on 20-Jul-17.
 */
public class CameraFragmentTest extends BaseTestClass {

    @Rule
    public FragmentTestRule<CameraFragment> mCameraFragmentTestRule =
            new FragmentTestRule<>(CameraFragment.class);
    private CameraFragment mCameraFragment;

    @Before
    public void init() {

        if (CameraUtils.isCameraAvailable(InstrumentationRegistry.getTargetContext())) {
            mCameraFragmentTestRule.launchActivity(null);
        } else {
            try {
                mCameraFragmentTestRule.launchActivity(null);
                fail("Should not initialize this CameraFragment if camera not there.");
            } catch (IllegalArgumentException e) {
                //Success
            }
        }


        try {
            Thread.sleep(1200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            mCameraFragment = mCameraFragmentTestRule.getFragment();
        }

    }

    @SuppressWarnings("ConstantConditions")
    @Test
    public void checkNewInstance() throws Exception {
        assertTrue(CameraFragment.getNewInstance() instanceof CameraFragment);
    }

    @Test
    public void checkIfInitialized() throws Exception {
        assertNotNull(mCameraFragment);
        assertNotNull(mCameraFragment.mCameraPreview);
        assertNotNull(mCameraFragment.mImageClassifier);

        mCameraFragment.mCameraPreview.stopPreviewAndReleaseCamera();
    }

    @Test
    public void checkIfCameraWorking() throws Exception {
        //Test if the camera opened
        assertTrue(mCameraFragment.mCameraPreview.isSafeToTakePicture());

        //Test take the picture
        mCameraFragment.mCameraPreview.takePicture();
        assertFalse(mCameraFragment.mCameraPreview.isSafeToTakePicture());

        // Check if it can release the camera.
        mCameraFragment.mCameraPreview.stopPreviewAndReleaseCamera();
        assertFalse(mCameraFragment.mCameraPreview.isSafeToTakePicture());
    }


    @Override
    public Activity getActivity() {
        return mCameraFragmentTestRule.getActivity();
    }
}
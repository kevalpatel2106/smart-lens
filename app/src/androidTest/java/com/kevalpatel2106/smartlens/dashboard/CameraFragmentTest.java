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

import com.kevalpatel2106.smartlens.camera.CameraUtils;
import com.kevalpatel2106.smartlens.testUtils.BaseTestClass;
import com.kevalpatel2106.smartlens.testUtils.Delay;
import com.kevalpatel2106.smartlens.testUtils.FragmentTestRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static junit.framework.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by Keval on 20-Jul-17.
 */
public class CameraFragmentTest extends BaseTestClass {

    @Rule
    public ExpectedException mException = ExpectedException.none();

    @Rule
    public FragmentTestRule<CameraFragment> mCameraFragmentTestRule =
            new FragmentTestRule<>(CameraFragment.class);

    private CameraFragment mCameraFragment;

    @Before
    public void init() {
        if (CameraUtils.isCameraAvailable(mCameraFragmentTestRule.getActivity())) {
            mCameraFragmentTestRule.launchActivity(null);
        } else {
            mException.expect(IllegalStateException.class);
            mCameraFragmentTestRule.launchActivity(null);
        }

        mCameraFragment = mCameraFragmentTestRule.getFragment();

        // We will wait for 300 seconds until the camera starts.
        Delay.startDelay(2000);
        Delay.stopDelay();
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    public void checkNewInstance() throws Exception {
        assertTrue(CameraFragment.getNewInstance() instanceof CameraFragment);
    }

    @Test
    public void checkIfCameraWorking() throws Exception {
        //Test if the camera opened
        assertTrue(mCameraFragment.mCameraPreview.isCameraOpen());
        assertTrue(mCameraFragment.mCameraPreview.isSafeToTakePicture());

        //Test take the picture
        mCameraFragment.mCameraPreview.takePicture();
        assertFalse(mCameraFragment.mCameraPreview.isSafeToTakePicture());

        // Check if it can release the camera.
        mCameraFragment.mCameraPreview.stopPreviewAndReleaseCamera();
        assertFalse(mCameraFragment.mCameraPreview.isCameraOpen());
    }


    @Override
    public Activity getActivity() {
        return mCameraFragmentTestRule.getActivity();
    }
}
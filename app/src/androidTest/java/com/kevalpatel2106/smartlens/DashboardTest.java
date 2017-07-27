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

package com.kevalpatel2106.smartlens;

import android.Manifest;
import android.app.Activity;
import android.support.test.rule.ActivityTestRule;
import android.support.test.rule.GrantPermissionRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v4.app.Fragment;

import com.kevalpatel2106.smartlens.imageClassifier.ImageClassifierFragment;
import com.kevalpatel2106.smartlens.testUtils.BaseTestClass;
import com.kevalpatel2106.smartlens.wikipage.WikiFragment;

import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertTrue;

/**
 * Created by Keval on 21-Jul-17.
 */
@RunWith(AndroidJUnit4.class)
public class DashboardTest extends BaseTestClass {
    @Rule
    public GrantPermissionRule mRuntimePermissionRule = GrantPermissionRule.grant(Manifest.permission.CAMERA);
    @Rule
    public ActivityTestRule<Dashboard> mDashboardActivityTestRule = new ActivityTestRule<>(Dashboard.class);

    @Test
    public void checkCameraFragmentLoaded() throws Exception {
        Thread.sleep(1000);
        Fragment fragment = mDashboardActivityTestRule
                .getActivity()
                .getSupportFragmentManager()
                .findFragmentById(R.id.dashboard_camera_container);
        assertTrue(fragment instanceof ImageClassifierFragment);
    }

    @Test
    public void checkWikiFragmentLoaded() throws Exception {
        Thread.sleep(1000);
        Fragment fragment = mDashboardActivityTestRule
                .getActivity()
                .getSupportFragmentManager()
                .findFragmentById(R.id.dashboard_bottom_sheet_container);
        assertTrue(fragment instanceof WikiFragment);
    }

    @After
    public void tearDown() {
        mDashboardActivityTestRule.getActivity().finish();
    }

    @Override
    public Activity getActivity() {
        return mDashboardActivityTestRule.getActivity();
    }
}
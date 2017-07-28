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

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.widget.NestedScrollView;
import android.view.View;
import android.widget.Toast;

import com.kevalpatel2106.smartlens.base.BaseActivity;
import com.kevalpatel2106.smartlens.camera.CameraUtils;
import com.kevalpatel2106.smartlens.imageClassifier.ImageClassifierFragment;
import com.kevalpatel2106.smartlens.infopage.InfoFragment;

import butterknife.BindView;

public class Dashboard extends BaseActivity {

    @BindView(R.id.bottom_sheet)
    NestedScrollView mNestedScrollView;

    ImageClassifierFragment mCameraFragment;
    InfoFragment mInfoFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mCameraFragment = ImageClassifierFragment.getNewInstance();
        mInfoFragment = InfoFragment.getNewInstance();

        //First check if the camera available?
        if (CameraUtils.isCameraAvailable(this)) {
            //Set the camera fragment.
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.dashboard_camera_container, mCameraFragment)
                    .commit();
        } else {
            Toast.makeText(this,
                    R.string.dashboard_error_no_camera_found,
                    Toast.LENGTH_LONG).show();
            finish();
        }


        setBottomSheet();
    }

    @SuppressWarnings("ConstantConditions")
    private void setBottomSheet() {
        BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from(mNestedScrollView);
        bottomSheetBehavior.setPeekHeight(100);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {

            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });

        //Set the wiki fragment.
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.dashboard_bottom_sheet_container, mInfoFragment)
                .commit();
    }
}

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

import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.widget.Toast;

import com.kevalpatel2106.smartlens.R;
import com.kevalpatel2106.smartlens.base.BaseActivity;
import com.kevalpatel2106.smartlens.camera.CameraUtils;

import butterknife.BindView;

public class Dashboard extends BaseActivity {

    @BindView(R.id.navigation)
    BottomNavigationView mBottomNavigationView;

    ImageClassifierFragment mImageClassifierFragment;
    BarcodeScannerFragment mBarcodeScannerFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        if (!CameraUtils.isCameraAvailable(this)) {
            Toast.makeText(this,
                    R.string.dashboard_error_no_camera_found,
                    Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        mImageClassifierFragment = ImageClassifierFragment.getNewInstance();
        mBarcodeScannerFragment = BarcodeScannerFragment.getNewInstance();

        //By default object recognition is selected.
        mBottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.action_image_recognition:
                    //Set the image classifier fragment.
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.dashboard_container, mImageClassifierFragment)
                            .commit();
                    break;
                case R.id.action_ocr:
                    //Set the camera fragment.
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.dashboard_container, mImageClassifierFragment)
                            .commit();
                    break;
                case R.id.action_qr_scanner:
                    //Set the barcode scanner fragment.
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.dashboard_container, mBarcodeScannerFragment)
                            .commit();
                    break;
                default:
                    throw new IllegalStateException("This bottom bar tab is invalid.");
            }
            return true;
        });
        mBottomNavigationView.setSelectedItemId(R.id.action_image_recognition);
    }
}

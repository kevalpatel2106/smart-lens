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

package com.kevalpatel2106.smartlens.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

/**
 * Created by Keval on 20-Dec-16.
 * General utility functions.
 *
 * @author {@link 'https://github.com/kevalpatel2106'}
 */

public class Utils {

    /**
     * Check if the device has camera.
     *
     * @param context instance of the caller.
     * @return true if the device has camera.
     */
    public static boolean checkIfHasCamera(@NonNull Context context) {
        PackageManager pm = context.getPackageManager();
        return pm.hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }

    /**
     * Get current device id.
     *
     * @param context instance of caller.
     * @return Device unique id.
     */
    @NonNull
    @SuppressLint("HardwareIds")
    public static String getDeviceId(@NonNull Context context) {
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    /**
     * Get the device model name.
     *
     * @return Device model (e.g. Samsung Galaxy S4)
     */
    public static String getDeviceName() {
        return String.format("%s-%s", Build.MANUFACTURER, Build.MODEL);
    }

    /**
     * Hide the keyboard.
     *
     * @param view View in focus.
     */
    @SuppressWarnings("ConstantConditions")
    public static void hideKeyboard(@NonNull View view) {
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) view.getContext()
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}

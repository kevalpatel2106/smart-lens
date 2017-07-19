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

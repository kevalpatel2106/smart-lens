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
import android.graphics.Bitmap;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.kevalpatel2106.smartlens.camera.config.CameraImageFormat;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by Keval on 20-Dec-16.
 * General utility functions.
 *
 * @author {@link 'https://github.com/kevalpatel2106'}
 */

@SuppressWarnings("WeakerAccess")
public class Utils {

    private Utils() {
        throw new RuntimeException("Cannot initialize this class.");
    }

    /**
     * Get the string from the input stream.
     *
     * @param inputStream {@link InputStream} to read.
     * @return String.
     * @throws IOException - If unable to read
     */
    public static String getStringFromStream(@NonNull InputStream inputStream) throws IOException {
        //noinspection ConstantConditions
        BufferedReader r = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder total = new StringBuilder();
        String line;
        while ((line = r.readLine()) != null) total.append(line).append('\n');
        return total.toString();
    }

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

    /**
     * Save image to the file.
     *
     * @param bitmap      bitmap to store.
     * @param fileToSave  file where bitmap should stored
     * @param imageFormat Format of the output file. {@link CameraImageFormat}
     * @return True if the file saved successfully.
     */
    public static boolean saveImageFromFile(@NonNull Bitmap bitmap,
                                            @NonNull File fileToSave,
                                            @CameraImageFormat.SupportedImageFormat int imageFormat) {
        FileOutputStream out = null;
        boolean isSuccess;

        //Decide the image format
        Bitmap.CompressFormat compressFormat;
        switch (imageFormat) {
            case CameraImageFormat.FORMAT_JPEG:
                compressFormat = Bitmap.CompressFormat.JPEG;
                break;
            case CameraImageFormat.FORMAT_WEBP:
                compressFormat = Bitmap.CompressFormat.WEBP;
                break;
            case CameraImageFormat.FORMAT_PNG:
                compressFormat = Bitmap.CompressFormat.PNG;
                break;
            default:
                throw new IllegalArgumentException("Invalid image format.");
        }

        try {
            if (!fileToSave.exists()) //noinspection ResultOfMethodCallIgnored
                fileToSave.createNewFile();

            out = new FileOutputStream(fileToSave);
            bitmap.compress(compressFormat, 100, out); // bmp is your Bitmap instance
            isSuccess = true;
        } catch (Exception e) {
            e.printStackTrace();
            isSuccess = false;
        } finally {
            try {
                if (out != null) out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return isSuccess;
    }

    /**
     * Convert the given text to caps word.
     *
     * @param text Text to convert. (e.g. computer keyboard)
     * @return Word caps text (e.g. Computer Keyboard)
     */
    public static String getCamelCaseText(@Nullable String text) {
        if (text == null) return null;

        String[] words = text.split("\\s");
        for (int i = 0; i < words.length; i++) {
            if (words[i].length() > 1) {
                words[i] = Character.toUpperCase(words[i].charAt(0)) + words[i].substring(1).toLowerCase();
            } else {
                words[i] = words[i].toUpperCase();
            }
        }
        return TextUtils.join(" ", words);
    }
}

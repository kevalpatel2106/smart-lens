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

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.support.annotation.NonNull;
import android.support.annotation.WorkerThread;

import com.kevalpatel2106.smartlens.camera.config.CameraImageFormat;
import com.kevalpatel2106.smartlens.camera.config.CameraRotation;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Keval on 11-Nov-16.
 * This class holds common camera utils.
 *
 * @author {@link 'https://github.com/kevalpatel2106'}
 */

public final class HiddenCameraUtils {

    /**
     * Check if the device has front camera or not?
     *
     * @param context context
     * @return true if the device has front camera.
     */
    @SuppressWarnings("deprecation")
    public static boolean isFrontCameraAvailable(@NonNull Context context) {
        int numCameras = Camera.getNumberOfCameras();
        return numCameras > 0 && context.getPackageManager()
                .hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT);
    }


    /**
     * Rotate the bitmap by 90 degree.
     *
     * @param bitmap original bitmap
     * @return rotated bitmap
     */
    @WorkerThread
    static Bitmap rotateBitmap(@NonNull Bitmap bitmap, @CameraRotation.SupportedRotation int rotation) {
        Matrix matrix = new Matrix();
        matrix.postRotate(rotation);
        return Bitmap.createBitmap(bitmap,
                0, 0,
                bitmap.getWidth(),
                bitmap.getHeight(),
                matrix, true);
    }

    /**
     * Save image to the file.
     *
     * @param bitmap     bitmap to store.
     * @param fileToSave file where bitmap should stored
     */
    static boolean saveImageFromFile(@NonNull Bitmap bitmap,
                                     @NonNull File fileToSave,
                                     @CameraImageFormat.SupportedImageFormat int imageFormat) {
        FileOutputStream out = null;
        boolean isSuccess = false;

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
            default:
                compressFormat = Bitmap.CompressFormat.PNG;
        }

        try {
            if (!fileToSave.exists()) fileToSave.createNewFile();
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
}

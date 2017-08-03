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
import android.support.annotation.NonNull;

import com.kevalpatel2106.smartlens.camera.config.CameraFacing;
import com.kevalpatel2106.smartlens.camera.config.CameraImageFormat;
import com.kevalpatel2106.smartlens.camera.config.CameraResolution;
import com.kevalpatel2106.smartlens.camera.config.CameraRotation;
import com.kevalpatel2106.smartlens.utils.FileUtils;

import java.io.File;

/**
 * Created by Keval on 12-Nov-16.
 * This class will manage the camera configuration parameters. User the {@link Builder} class to
 * set different parameters for the camera.
 *
 * @author {@link 'https://github.com/kevalpatel2106'}
 */

@SuppressWarnings("WeakerAccess")
public final class CameraConfig {
    private Context mContext;

    @CameraResolution.SupportedResolution
    private int mResolution = CameraResolution.MEDIUM_RESOLUTION;

    @CameraFacing.SupportedCameraFacing
    private int mFacing = CameraFacing.REAR_FACING_CAMERA;

    @CameraImageFormat.SupportedImageFormat
    private int mImageFormat = CameraImageFormat.FORMAT_JPEG;

    @CameraRotation.SupportedRotation
    private int mImageRotation = CameraRotation.ROTATION_0;

    private File mImageFile;

    /**
     * Public constructor.
     */
    public CameraConfig() {
        //Do nothing
    }

    /**
     * @param context Instance.
     * @return {@link Builder}
     */
    public Builder getBuilder(@NonNull Context context) {
        mContext = context;
        return new Builder();
    }

    @CameraResolution.SupportedResolution
    public int getResolution() {
        return mResolution;
    }

    @CameraFacing.SupportedCameraFacing
    public int getFacing() {
        return mFacing;
    }

    @CameraImageFormat.SupportedImageFormat
    public int getImageFormat() {
        return mImageFormat;
    }

    public File getImageFile() {
        return mImageFile;
    }

    @CameraRotation.SupportedRotation
    public int getImageRotation() {
        return mImageRotation;
    }

    /**
     * This is the builder class for {@link CameraConfig}.
     */
    @SuppressWarnings({"unused", "UnusedReturnValue", "WeakerAccess"})
    public class Builder {

        /**
         * Set the resolution of the output camera image. If you don't specify any resolution,
         * default image resolution will set to {@link CameraResolution#MEDIUM_RESOLUTION}.
         *
         * @param resolution Any resolution from:
         *                   <li>{@link CameraResolution#HIGH_RESOLUTION}</li>
         *                   <li>{@link CameraResolution#MEDIUM_RESOLUTION}</li>
         *                   <li>{@link CameraResolution#LOW_RESOLUTION}</li>
         * @return {@link Builder}
         * @see CameraResolution
         */
        public Builder setCameraResolution(@CameraResolution.SupportedResolution int resolution) {

            //Validate input
            if (resolution != CameraResolution.HIGH_RESOLUTION &&
                    resolution != CameraResolution.MEDIUM_RESOLUTION &&
                    resolution != CameraResolution.LOW_RESOLUTION) {
                throw new IllegalArgumentException("Invalid camera resolution.");
            }

            mResolution = resolution;
            return this;
        }

        /**
         * Set the camera facing with which you want to capture image.
         * Either rear facing camera or front facing camera. If you don't provide any camera facing,
         * default camera facing will be {@link CameraFacing#FRONT_FACING_CAMERA}.
         *
         * @param cameraFacing Any camera facing from:
         *                     <li>{@link CameraFacing#REAR_FACING_CAMERA}</li>
         *                     <li>{@link CameraFacing#FRONT_FACING_CAMERA}</li>
         * @return {@link Builder}
         * @see CameraFacing
         */
        public Builder setCameraFacing(@CameraFacing.SupportedCameraFacing int cameraFacing) {
            //Validate input
            if (cameraFacing != CameraFacing.REAR_FACING_CAMERA &&
                    cameraFacing != CameraFacing.FRONT_FACING_CAMERA) {
                throw new IllegalArgumentException("Invalid camera facing value.");
            }

            //Check if the any camera available?
            if (!CameraUtils.isCameraAvailable(mContext)) {
                throw new IllegalStateException("Device camera is not available.");
            }

            //Check if the front camera available?
            if (cameraFacing == CameraFacing.FRONT_FACING_CAMERA
                    && !CameraUtils.isFrontCameraAvailable(mContext)) {
                throw new IllegalStateException("Front camera is not available.");
            }

            mFacing = cameraFacing;
            return this;
        }

        /**
         * Specify the image format for the output image. If you don't specify any output format,
         * default output format will be {@link CameraImageFormat#FORMAT_JPEG}.
         *
         * @param imageFormat Any supported image format from:
         *                    <li>{@link CameraImageFormat#FORMAT_JPEG}</li>
         *                    <li>{@link CameraImageFormat#FORMAT_PNG}</li>
         * @return {@link Builder}
         * @see CameraImageFormat
         */
        public Builder setImageFormat(@CameraImageFormat.SupportedImageFormat int imageFormat) {
            //Validate input
            if (imageFormat != CameraImageFormat.FORMAT_JPEG &&
                    imageFormat != CameraImageFormat.FORMAT_PNG) {
                throw new IllegalArgumentException("Invalid output image format.");
            }

            mImageFormat = imageFormat;
            return this;
        }

        /**
         * Specify the output image rotation. The output image will be rotated by amount of degree specified
         * before stored to the output file. By default there is no rotation applied.
         *
         * @param rotation Any supported rotation from:
         *                 <li>{@link CameraRotation#ROTATION_0}</li>
         *                 <li>{@link CameraRotation#ROTATION_90}</li>
         *                 <li>{@link CameraRotation#ROTATION_180}</li>
         *                 <li>{@link CameraRotation#ROTATION_270}</li>
         * @return {@link Builder}
         * @see CameraRotation
         */
        public Builder setImageRotation(@CameraRotation.SupportedRotation int rotation) {
            //Validate input
            if (rotation != CameraRotation.ROTATION_0
                    && rotation != CameraRotation.ROTATION_90
                    && rotation != CameraRotation.ROTATION_180
                    && rotation != CameraRotation.ROTATION_270) {
                throw new IllegalArgumentException("Invalid image rotation.");
            }

            mImageRotation = rotation;
            return this;
        }

        /**
         * Set the location of the out put image. If you do not set any file for the output image, by
         * default image will be stored in the application's cache directory.
         *
         * @param imageFile {@link File} where you want to store the image.
         * @return {@link Builder}
         */
        public Builder setImageFile(File imageFile) {
            mImageFile = imageFile;
            return this;
        }

        /**
         * Build the configuration.
         *
         * @return {@link CameraConfig}
         */
        public CameraConfig build() {
            if (mImageFile == null) mImageFile = getDefaultStorageFile();
            return CameraConfig.this;
        }

        /**
         * If no file is supplied to save the captured image, image will be saved by default to the
         * cache directory.
         *
         * @return File to save image bitmap.
         */
        @NonNull
        private File getDefaultStorageFile() {
            return new File(FileUtils.getCacheDir(mContext).getAbsolutePath()
                    + File.pathSeparator
                    + "IMG_" + System.currentTimeMillis()   //IMG_214515184113123.png
                    + (mImageFormat == CameraImageFormat.FORMAT_JPEG ? ".jpeg" : ".png"));
        }
    }
}

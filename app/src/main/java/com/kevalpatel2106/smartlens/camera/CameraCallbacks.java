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

import android.support.annotation.Nullable;

import java.io.Serializable;

/**
 * Created by Keval on 14-Oct-16.
 * Interface to receive the camera callbacks.
 *
 * @author {@link 'https://github.com/kevalpatel2106'}
 */
public interface CameraCallbacks extends Serializable {

    /**
     * This callback will notify whenever new image is captured.
     */
    void onImageCapture(@Nullable byte[] imageBytes);

    /**
     * This callback will notify whenever any error occurs while working with the camera.
     *
     * @param errorCode Error code for the error occurred.
     * @see CameraError
     */
    void onCameraError(@CameraError.CameraErrorCodes int errorCode);
}

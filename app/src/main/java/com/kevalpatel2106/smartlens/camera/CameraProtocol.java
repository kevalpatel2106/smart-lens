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

/**
 * Created by Keval on 02-Aug-17.
 */

public abstract class CameraProtocol {

    public CameraProtocol(@SuppressWarnings("unused") CameraCallbacks cameraCallbacks) {
        //Dp nothing
    }

    public abstract void startCamera(CameraConfig cameraConfig);

    public abstract void closeCamera();
}

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

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Point;
import android.hardware.Camera;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;

import com.kevalpatel2106.smartlens.camera.config.CameraResolution;

import java.io.IOException;
import java.util.List;

/**
 * Created by Keval Patel on 19/07/17.
 * This surface view works as the preview for the camera.
 *
 * @author 'https://github.com/kevalpatel2106'
 */


@SuppressLint("ViewConstructor")
public final class CameraPreview extends SurfaceView implements SurfaceHolder.Callback, Camera.ErrorCallback {
    private static final String TAG = "CameraPreview";

    private final Context mContext;

    private SurfaceHolder mHolder;          //Surface holder for that surface view.

    private Camera mCamera;
    private CameraConfig mCameraConfig;     //Camera configurations.

    private CameraCallbacks mCameraCallbacks;   //Camera callbacks for getting camera.

    private volatile boolean isSafeToTakePicture = false;

    //Create the picture callback.
    private Camera.PictureCallback mPictureCallback = (bytes, camera) -> {
        Log.d(TAG, "image captured: " + bytes.length);
        mCameraCallbacks.onImageCapture(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));
        isSafeToTakePicture = true;

        //Restart the preview.
        //https://stackoverflow.com/a/31260958/4690731
        mCamera.stopPreview();
        mCamera.startPreview();
    };

    /**
     * Public constructor.
     *
     * @param context         Instance of the caller.
     * @param cameraCallbacks {@link CameraCallbacks} to get the camera information.
     */
    @SuppressWarnings("deprecation")
    public CameraPreview(@NonNull Context context, @NonNull CameraCallbacks cameraCallbacks) {
        super(context);
        mContext = context;
        mCameraCallbacks = cameraCallbacks;

        //noinspection ConstantConditions
        if (context == null || cameraCallbacks == null)
            throw new IllegalArgumentException("Null arguments not allowed.");

        //Set surface holder
        mHolder = getHolder();
        mHolder.addCallback(this);
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    @Override
    protected void onLayout(boolean b, int i, int i1, int i2, int i3) {
        //Do nothing
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        //Do nothing
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
        if (mCamera == null) {  //Camera is not initialized yet.
            mCameraCallbacks.onCameraError(CameraError.ERROR_CAMERA_OPEN_FAILED);
            return;
        } else if (surfaceHolder.getSurface() == null) { //Surface preview is not initialized yet
            mCameraCallbacks.onCameraError(CameraError.ERROR_CAMERA_OPEN_FAILED);
            return;
        }

        isSafeToTakePicture = false;

        // stop preview before making changes
        try {
            mCamera.stopPreview();
        } catch (Exception e) {
            // Ignore: tried to stop a non-existent preview
        }

        try {
            mCamera.setPreviewDisplay(surfaceHolder);
            mCamera.setParameters(modifyCameraParameters(mCamera));
            mCamera.startPreview();

            isSafeToTakePicture = true;
        } catch (IOException e) {
            e.printStackTrace();
            mCameraCallbacks.onCameraError(CameraError.ERROR_CAMERA_OPEN_FAILED);
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        // Surface will be destroyed when we return, so stop the preview.
        // Call stopPreview() to stop updating the preview surface.
        if (mCamera != null) mCamera.stopPreview();
    }

    /**
     * Initialize the camera and start the preview of the camera.
     *
     * @param cameraConfig camera configurations.
     * @see CameraConfig
     */
    public void startCamera(@NonNull CameraConfig cameraConfig) {
        //Check if the camera permission is available or not?
        if (!CameraUtils.checkIfCameraPermissionGranted(mContext)) {
            mCameraCallbacks.onCameraError(CameraError.ERROR_CAMERA_PERMISSION_NOT_AVAILABLE);
            return;
        }

        mCameraConfig = cameraConfig;
        if (safeCameraOpen(mCameraConfig.getFacing())) {
            requestLayout();

            try {
                //Start the preview.
                mCamera.setPreviewDisplay(mHolder);
                mCamera.setDisplayOrientation(90);
                mCamera.setErrorCallback(this);
                mCamera.startPreview();
            } catch (IOException e) {
                e.printStackTrace();

                //Error occurred while starting the camera preview.
                mCameraCallbacks.onCameraError(CameraError.ERROR_CAMERA_OPEN_FAILED);
            }
        } else {
            //Was not able to initialize the camera.
            mCameraCallbacks.onCameraError(CameraError.ERROR_CAMERA_OPEN_FAILED);
        }
    }

    /**
     * Open the camera safely.
     *
     * @param cameraFacing Id of the camera.
     * @return True if the camera opened successfully else false.
     */
    private boolean safeCameraOpen(int cameraFacing) {
        try {
            //Stop the camera first to be safe.
            stopPreviewAndReleaseCamera();

            //Try to open  the camera
            mCamera = Camera.open(cameraFacing);

            //All good. Camera opened.
            return mCamera != null;
        } catch (Exception e) {
            //Exception occurred.
            Log.e(TAG, "safeCameraOpen: ");
            e.printStackTrace();
        }
        return false;
    }

    /**
     * You can check if it is safe to take picture right now?
     *
     * @return true if you can capture the image else false.
     */
    public boolean isSafeToTakePicture() {
        return isCameraOpen() && isSafeToTakePicture;
    }

    public boolean isCameraOpen() {
        return mCamera != null;
    }

    /**
     * Take the picture.
     */
    public void takePicture() {
        //Check if it is safe to take image?
        if (!isSafeToTakePicture()) return;

        //Mark unsafe
        isSafeToTakePicture = false;

        //Take the picture.
        mCamera.takePicture(null, null, mPictureCallback);
    }

    /**
     * Get the valid picture sizes based on the supported picture sizes.
     *
     * @param pictureSizes  List of {@link Camera.Size} supported picture size by the hardware camera.
     * @param cameraQuality Quality of the camera supplied.
     * @return Supported image.
     */
    @NonNull
    private Camera.Size getValidPictureSize(@NonNull List<Camera.Size> pictureSizes,
                                            @CameraResolution.SupportedResolution int cameraQuality) {
        if (pictureSizes.isEmpty())
            throw new IllegalArgumentException("Picture sizes cannot be null.");

        switch (cameraQuality) {
            case CameraResolution.HIGH_RESOLUTION:
                return pictureSizes.get(0);   //Highest res
            case CameraResolution.MEDIUM_RESOLUTION:
                return pictureSizes.get(pictureSizes.size() / 2);     //Resolution at the middle
            case CameraResolution.LOW_RESOLUTION:
                return pictureSizes.get(pictureSizes.size() - 1);       //Lowest res
            default:
                throw new IllegalStateException("Invalid camera resolution.");
        }
    }


    /**
     * Get the valid preview sizes based on the supported preview sizes.
     *
     * @param previewSizes List of {@link Camera.Size} supported preview size by the hardware camera.
     * @return Supported image.
     */
    @NonNull
    private Camera.Size getValidPreviewSize(@NonNull List<Camera.Size> previewSizes) {
        if (previewSizes.isEmpty())
            throw new IllegalArgumentException("Picture sizes cannot be null.");

        Point sizePoint = new Point();
        ((WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay()
                .getSize(sizePoint);
        int viewWidth = sizePoint.x;
        int viewHeight = sizePoint.y;

        final double ASPECT_TOLERANCE = 0.1;
        double targetRatio = (double) viewWidth / viewHeight;

        Camera.Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;

        // Try to find an size match aspect ratio and size
        for (Camera.Size size : previewSizes) {
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue;

            if (Math.abs(size.height - viewHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - viewHeight);
            }
        }

        // Cannot find the one match the aspect ratio, ignore the requirement
        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Camera.Size size : previewSizes) {
                if (Math.abs(size.height - viewHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - viewHeight);
                }
            }
        }
        return optimalSize == null ? previewSizes.get(0) : optimalSize;
    }

    /**
     * Modify the parameters of the camera control. This will set preview sizes and picture sizes
     * according to the phone display size.
     *
     * @param camera {@link Camera} open.
     * @return Modified {@link Camera.Parameters}.
     */
    private Camera.Parameters modifyCameraParameters(@NonNull Camera camera) {
        Camera.Parameters parameters = camera.getParameters();

        //Set the image format
        parameters.setPictureFormat(ImageFormat.JPEG);

        //If the camera support focus mode auto, set it.
        List<String> focusModes = parameters.getSupportedFocusModes();
        if (focusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO))
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);

        //set the flash modes
        List<String> flashModes = parameters.getSupportedFlashModes();
        parameters.setFocusMode(flashModes.contains(Camera.Parameters.FLASH_MODE_AUTO) ?
                Camera.Parameters.FLASH_MODE_AUTO : Camera.Parameters.FLASH_MODE_ON);

        //Set the picture size size
        Camera.Size pictureSize = getValidPictureSize(mCamera.getParameters().getSupportedPictureSizes(),
                mCameraConfig.getResolution());
        parameters.setPictureSize(pictureSize.width, pictureSize.height);

        //Set the preview size
        Camera.Size previewSize = getValidPreviewSize(mCamera.getParameters().getSupportedPreviewSizes());
        parameters.setPreviewSize(previewSize.width, previewSize.height);

        requestLayout();
        return parameters;
    }

    /**
     * Stop camera preview and release the camera.
     */
    public void stopPreviewAndReleaseCamera() {
        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.release();
        }
        mCamera = null;
    }

    @Override
    public void onError(int i, Camera camera) {
        stopPreviewAndReleaseCamera();
        mCameraCallbacks.onCameraError(CameraError.ERROR_CAMERA_OPEN_FAILED);
    }
}
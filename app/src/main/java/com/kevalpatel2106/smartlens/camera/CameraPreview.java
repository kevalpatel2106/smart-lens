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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.kevalpatel2106.smartlens.camera.config.CameraResolution;
import com.kevalpatel2106.smartlens.camera.config.CameraRotation;

import org.reactivestreams.Subscription;

import java.io.IOException;
import java.util.List;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Keval Patel on 19/07/17.
 * This surface view works as the preview for the camera.
 *
 * @author 'https://github.com/kevalpatel2106'
 */


@SuppressLint("ViewConstructor")
public final class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
    private CameraCallbacks mCameraCallbacks;   //Callbacks.

    private SurfaceHolder mHolder;
    private Camera mCamera;

    private CameraConfig mCameraConfig;

    private volatile boolean isSafeToTakePicture = false;

    /**
     * Public constructor.
     *
     * @param context         Instance of the caller.
     * @param cameraCallbacks {@link CameraCallbacks} to get the camera information.
     */
    public CameraPreview(@NonNull Context context, @NonNull CameraCallbacks cameraCallbacks) {
        super(context);

        mCameraCallbacks = cameraCallbacks;

        //Set surface holder
        initSurfaceView();
    }

    /**
     * Initialize the surface view holder.
     */
    private void initSurfaceView() {
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

        // stop preview before making changes
        try {
            mCamera.stopPreview();
        } catch (Exception e) {
            // Ignore: tried to stop a non-existent preview
        }

        // Make changes in preview size
        //set the camera image size based on config provided
        Camera.Parameters parameters = mCamera.getParameters();
        Camera.Size cameraSize = getValidCameraSize(mCamera.getParameters().getSupportedPictureSizes(),
                mCameraConfig.getResolution());
        parameters.setPictureSize(cameraSize.width, cameraSize.height);

        requestLayout();

        mCamera.setParameters(parameters);

        try {
            mCamera.setDisplayOrientation(90);
            mCamera.setPreviewDisplay(surfaceHolder);
            mCamera.startPreview();

            //You are safe now.
            isSafeToTakePicture = true;
        } catch (IOException | NullPointerException e) {
            //Cannot start preview
            mCameraCallbacks.onCameraError(CameraError.ERROR_CAMERA_OPEN_FAILED);
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        // Surface will be destroyed when we return, so stop the preview.
        if (mCamera != null) {

            // Call stopPreview() to stop updating the preview surface.
            mCamera.stopPreview();
        }
    }

    /**
     * Initialize the camera and start the preview of the camera.
     *
     * @param cameraConfig camera configurations.
     * @see CameraConfig
     */
    public void startCamera(@NonNull CameraConfig cameraConfig) {
        //TODO Keval Validate camera config
        mCameraConfig = cameraConfig;

        if (safeCameraOpen(mCameraConfig.getFacing())) {
            requestLayout();

            try {
                //Start the preview.
                mCamera.setPreviewDisplay(mHolder);
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
            Log.e("CameraPreview", "failed to open Camera");
            e.printStackTrace();
        }

        return false;
    }

    /**
     * You can check if it is safe to take picture right now?
     *
     * @return true if you can capture the image else false.
     */
    boolean isSafeToTakePictureInternal() {
        return isSafeToTakePicture;
    }

    /**
     * Take the picture.
     */
    public void takePicture() {
        //Check if it is safe to take image?
        if (!isSafeToTakePicture) return;

        if (mCamera != null) {

            //Mark unsafe
            isSafeToTakePicture = false;

            //Create the picture callback.
            Camera.PictureCallback mPictureCallback = (byte[] bytes, Camera camera) -> {

                Flowable<Bitmap> flowable = Flowable.create(bitmapEmitter -> {
                    //Convert byte array to bitmap
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

                    //Rotate the bitmap
                    Bitmap rotatedBitmap;
                    if (mCameraConfig.getImageRotation() != CameraRotation.ROTATION_0) {
                        rotatedBitmap = CameraUtils.rotateBitmap(bitmap, mCameraConfig.getImageRotation());

                        //noinspection UnusedAssignment
                        bitmap = null;
                    } else {
                        rotatedBitmap = bitmap;
                    }

                    bitmapEmitter.onNext(rotatedBitmap);
                    bitmapEmitter.onComplete();
                }, BackpressureStrategy.DROP);

                final Subscription[] subscriptions = {null};
                flowable.subscribeOn(Schedulers.io())
                        .filter(bitmap -> bitmap != null && bitmap.getByteCount() > 0)
                        .doOnSubscribe(subscription -> subscriptions[0] = subscription)
                        .doOnError(throwable -> {
                            mCameraCallbacks.onCameraError(CameraError.ERROR_CAMERA_OPEN_FAILED);
                            isSafeToTakePicture = true;
                        })
                        .doOnNext(bitmap -> {
                            mCameraCallbacks.onImageCapture(bitmap);
                            isSafeToTakePicture = true;
                        })
                        .doOnComplete(() -> {
                            if (subscriptions[0] != null) subscriptions[0].cancel();
                        })
                        .observeOn(AndroidSchedulers.mainThread());
            };

            //Take the picture.
            mCamera.takePicture(null, null, mPictureCallback);
        } else {
            mCameraCallbacks.onCameraError(CameraError.ERROR_CAMERA_OPEN_FAILED);
            isSafeToTakePicture = true;
        }
    }

    /**
     * Get the valid camera sizes based on the supported picture sizes.
     *
     * @param pictureSizes  List of {@link Camera.Size} supported by the hardware camera.
     * @param cameraQuality Quality of the camera supplied.
     * @return Supported image.
     */
    @NonNull
    private Camera.Size getValidCameraSize(@NonNull List<Camera.Size> pictureSizes,
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
     * Stop camera preview and release the camera.
     */
    void stopPreviewAndReleaseCamera() {
        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.release();
        }
        mCamera = null;
    }
}
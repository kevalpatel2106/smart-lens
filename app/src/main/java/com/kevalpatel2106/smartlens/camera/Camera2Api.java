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

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.ImageReader;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.Surface;
import android.view.TextureView;

import com.kevalpatel2106.smartlens.camera.config.CameraFacing;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import timber.log.Timber;

import static com.bumptech.glide.gifdecoder.GifHeaderParser.TAG;

/**
 * Created by Keval on 02-Aug-17.
 * This class uses camera 2 apis to display camera.
 * Steps:
 * <li>Open camera</li>
 * <li>Start camera preview.</li>
 * <li>Take picture</li>
 */

public class Camera2Api {

    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();

    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 90);
        ORIENTATIONS.append(Surface.ROTATION_90, 0);
        ORIENTATIONS.append(Surface.ROTATION_180, 270);
        ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }

    private final Activity mActivity;
    private final TextureView mTextureView;
    private final CameraCallbacks mCameraCallbacks;
    private CameraConfig mCameraConfig;
    private Size mImageDimension;

    //Camera device
    private CameraDevice mCameraDevice;

    /**
     * Camera capture session.
     */
    private CameraCaptureSession mCameraCaptureSession;
    private CaptureRequest.Builder mCaptureRequestBuilder;

    //Background threads to handle camera callbacks
    private Handler mBackgroundHandler;
    /**
     * Listener to get callbacks when camera state changes.
     */
    private final CameraDevice.StateCallback mCameraStateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(@NonNull CameraDevice camera) {
            //This is called when the camera is open
            mCameraDevice = camera;
            createCameraPreview();
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice camera) {
            mCameraDevice.close();
        }

        @Override
        public void onError(@NonNull CameraDevice camera, int error) {
            mCameraDevice.close();
            mCameraDevice = null;
        }
    };
    private HandlerThread mBackgroundThread;
    /**
     * Listener to get callbacks when surface texture get updated.
     */
    private TextureView.SurfaceTextureListener mSurfaceTextureListener = new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
            //open your camera here
            openCamera();
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
            // Transform you image captured size according to the surface width and height
        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            return false;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {
        }
    };

    /**
     * Public constructor.
     *
     * @param activity    Instance of the caller.
     * @param textureView {@link AutoFitTextureView} from the view.
     */
    public Camera2Api(@NonNull Activity activity,
                      @NonNull TextureView textureView,
                      @NonNull CameraCallbacks cameraCallbacks) {
        mActivity = activity;
        mCameraCallbacks = cameraCallbacks;

        //Surface texture.
        mTextureView = textureView;
    }

    /**
     * Start the camera preview.
     *
     * @param cameraConfig {@link CameraConfig}
     */
    public void startCamera(@NonNull CameraConfig cameraConfig) {
        //Validate argument
        //noinspection ConstantConditions
        if (cameraConfig == null) throw new IllegalStateException("Camera config cannot be null.");

        //Set the new camera config
        mCameraConfig = cameraConfig;

        //Start background threads to handle camera operations
        startBackgroundThread();

        //Register the surface texture callbacks
        mTextureView.setSurfaceTextureListener(mSurfaceTextureListener);
    }

    /**
     * Close and release the camera.
     */
    public void closeCamera() {
        if (mCameraCaptureSession != null) {
            mCameraCaptureSession.close();
            mCameraCaptureSession = null;
        }
        if (null != mCameraDevice) {
            mCameraDevice.close();
            mCameraDevice = null;
        }
        stopBackgroundThread();
    }

    /**
     * Create the camera preview and display it according to the camera configuration.
     */
    private void createCameraPreview() {
        try {
            //Get the texture.
            SurfaceTexture texture = mTextureView.getSurfaceTexture();
            assert texture != null;
            texture.setDefaultBufferSize(mImageDimension.getWidth(), mImageDimension.getHeight());
            Surface surface = new Surface(texture);

            mCaptureRequestBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            mCaptureRequestBuilder.addTarget(surface);

            //noinspection ArraysAsListWithZeroOrOneArgument
            mCameraDevice.createCaptureSession(Arrays.asList(surface), new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(@NonNull CameraCaptureSession cameraCaptureSession) {
                    //The camera is already closed
                    if (null == mCameraDevice) {
                        mCameraCallbacks.onCameraError(CameraError.ERROR_CAMERA_OPEN_FAILED);
                        return;
                    }

                    // When the session is ready, we start displaying the preview.
                    mCameraCaptureSession = cameraCaptureSession;

                    //Update the preview according to the configurations.
                    updatePreview();
                }

                @Override
                public void onConfigureFailed(@NonNull CameraCaptureSession cameraCaptureSession) {
                    mCameraCallbacks.onCameraError(CameraError.ERROR_CAMERA_OPEN_FAILED);
                }
            }, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * Open the camera. This is the internal method to handle camera. This method will be called once
     * whenever the texture is ready in {@link #mSurfaceTextureListener}.
     */
    private void openCamera() {
        // Add permission for camera and let user grant the permission
        if (ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            mCameraCallbacks.onCameraError(CameraError.ERROR_CAMERA_PERMISSION_NOT_AVAILABLE);
            return;
        }

        CameraManager manager = (CameraManager) mActivity.getSystemService(Context.CAMERA_SERVICE);
        assert manager != null;
        try {
            String[] availableCamIds = manager.getCameraIdList();

            String cameraIdToOpen = null;
            if (mCameraConfig.getFacing() == CameraFacing.FRONT_FACING_CAMERA) {

                //Find out the front camera id.
                for (String camId : availableCamIds) {
                    CameraCharacteristics characteristics = manager.getCameraCharacteristics(camId);
                    //noinspection ConstantConditions
                    if (characteristics.get(CameraCharacteristics.LENS_FACING)
                            == CameraCharacteristics.LENS_FACING_FRONT) {
                        cameraIdToOpen = camId;
                    }
                }

                if (cameraIdToOpen == null) {
                    //No front facing camera found.
                    mCameraCallbacks.onCameraError(CameraError.ERROR_DOES_NOT_HAVE_FRONT_CAMERA);
                    return;
                }
            } else {
                cameraIdToOpen = availableCamIds[0];
            }

            //Get the camera characteristic
            CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraIdToOpen);
            StreamConfigurationMap map = characteristics
                    .get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
            assert map != null;

            mImageDimension = map.getOutputSizes(SurfaceTexture.class)[0];
            manager.openCamera(cameraIdToOpen, mCameraStateCallback, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
        Log.e(TAG, "openCamera X");
    }

    /**
     * Update the preview of camera acceding to new config.
     */
    private void updatePreview() {
        if (mCameraDevice == null) {
            mCameraCallbacks.onCameraError(CameraError.ERROR_CAMERA_OPEN_FAILED);
            return;
        }

        //Auto control
        mCaptureRequestBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);

        //Set the auto focus mode.
        mCaptureRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE,
                CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
        try {
            mCameraCaptureSession.setRepeatingRequest(mCaptureRequestBuilder.build(), null,
                    mBackgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * Take the picture.
     */
    public void takePicture() {
        if (mCameraDevice == null) {
            mCameraCallbacks.onCameraError(CameraError.ERROR_CAMERA_OPEN_FAILED);
            return;
        }

        CameraManager manager = (CameraManager) mActivity.getSystemService(Context.CAMERA_SERVICE);
        assert manager != null;

        try {
            CameraCharacteristics characteristics = manager.getCameraCharacteristics(mCameraDevice.getId());
            //noinspection ConstantConditions
            Size[] jpegSizes = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)
                    .getOutputSizes(ImageFormat.JPEG);

            //Set the height and width of the captured image
            int width = 640;
            int height = 480;
            if (jpegSizes != null && 0 < jpegSizes.length) {
                width = jpegSizes[0].getWidth();
                height = jpegSizes[0].getHeight();
            }

            ImageReader reader = ImageReader.newInstance(width, height, ImageFormat.JPEG, 1);

            List<Surface> outputSurfaces = new ArrayList<>(2);
            outputSurfaces.add(reader.getSurface());
            outputSurfaces.add(new Surface(mTextureView.getSurfaceTexture()));

            final CaptureRequest.Builder captureBuilder = mCameraDevice
                    .createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
            captureBuilder.addTarget(reader.getSurface());
            captureBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);

            //Set the orientation Orientation
            int rotation = mActivity.getWindowManager().getDefaultDisplay().getRotation();
            captureBuilder.set(CaptureRequest.JPEG_ORIENTATION, ORIENTATIONS.get(rotation));

            //Callback when image captured
            reader.setOnImageAvailableListener(reader1 -> {
                Bitmap bitmap = CameraUtils.imageToBitmap(reader1.acquireLatestImage());
                reader1.close();

                if (bitmap != null) {
                    Timber.d(bitmap.getByteCount() + " Bytes");
                    mCameraCallbacks.onImageCapture(bitmap);
                }
            }, mBackgroundHandler);

            mCameraDevice.createCaptureSession(outputSurfaces, new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(@NonNull CameraCaptureSession session) {
                    try {
                        session.capture(captureBuilder.build(),
                                new CameraCaptureSession.CaptureCallback() {
                                    @Override
                                    public void onCaptureCompleted(@NonNull CameraCaptureSession session,
                                                                   @NonNull CaptureRequest request,
                                                                   @NonNull TotalCaptureResult result) {
                                        super.onCaptureCompleted(session, request, result);
                                        createCameraPreview();
                                    }
                                }, mBackgroundHandler);
                    } catch (CameraAccessException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onConfigureFailed(@NonNull CameraCaptureSession session) {
                    Timber.e("Capture failed.");
                }
            }, mBackgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void startBackgroundThread() {
        mBackgroundThread = new HandlerThread("Camera Background");
        mBackgroundThread.start();
        mBackgroundHandler = new Handler(mBackgroundThread.getLooper());
    }

    private void stopBackgroundThread() {
        if (mBackgroundThread == null) return;

        mBackgroundThread.quitSafely();
        try {
            mBackgroundThread.join();
            mBackgroundThread = null;
            mBackgroundHandler = null;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

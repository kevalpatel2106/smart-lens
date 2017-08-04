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

package com.kevalpatel2106.smartlens.plugins.camera2;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.Surface;
import android.view.TextureView;

import com.kevalpatel2106.smartlens.camera.CameraCallbacks;
import com.kevalpatel2106.smartlens.camera.CameraConfig;
import com.kevalpatel2106.smartlens.camera.CameraError;
import com.kevalpatel2106.smartlens.camera.CameraProtocol;
import com.kevalpatel2106.smartlens.camera.config.CameraFacing;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import timber.log.Timber;

/**
 * Created by Keval on 02-Aug-17.
 * This class uses camera 2 apis to display camera.
 * Steps:
 * <li>Open camera</li>
 * <li>Start camera preview.</li>
 * <li>Take picture</li>
 */

public class Camera2Api extends CameraProtocol {

    /**
     * The camera preview size will be chosen to be the smallest frame by pixel size capable of
     * containing a DESIRED_SIZE x DESIRED_SIZE square.
     */
    private static final int MINIMUM_PREVIEW_SIZE = 320;

    //Camera orientation based on the screen orientation.
    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();

    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 90);
        ORIENTATIONS.append(Surface.ROTATION_90, 0);
        ORIENTATIONS.append(Surface.ROTATION_180, 270);
        ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }

    //The caller activity
    private final Activity mActivity;

    //Texture view
    private final AutoFitTextureView mAutoFitTextureView;
    /**
     * A {@link Semaphore} to prevent the app from exiting before closing the camera.
     */
    private final Semaphore mCameraOpenCloseLock = new Semaphore(1);
    //Camera callbacks to get notify when image is captured or error occurred.
    private CameraCallbacks mCameraCallbacks;
    //Camera config. You can set the camera config while starting the camera.
    private CameraConfig mCameraConfig;
    private Size mPreviewSize;

    //Camera device
    private CameraDevice mCameraDevice;

    /**
     * Camera capture session.
     */
    private CameraCaptureSession mCameraCaptureSession;
    private CaptureRequest.Builder mCaptureRequestBuilder;
    private ImageReader mPreviewReader;

    //Background threads to handle camera callbacks
    private Handler mBackgroundHandler;
    private HandlerThread mBackgroundThread;
    /**
     * Listener to get callbacks when surface texture get updated.
     */
    private TextureView.SurfaceTextureListener mSurfaceTextureListener = new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
            //open your camera here
            openCamera(width, height);
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
            // Transform you image captured size according to the surface width and height
            configureTransform(width, height);
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
     * Callback to receive when the image is captured. This runs on the background thread.
     */
    private ImageReader.OnImageAvailableListener mOnImageAvailableListener = imageReader -> {
        if (mCameraCallbacks != null) {
            Image image = imageReader.acquireLatestImage();
            if (image != null) {

                //Convert byte to bitmap
                ByteBuffer buffer = image.getPlanes()[0].getBuffer();
                byte[] bytes = new byte[buffer.capacity()];
                buffer.get(bytes);

                mCameraCallbacks.onImageCapture(bytes);

                //Close the image
                image.close();
            }
        }
    };
    /**
     * Listener to get callbacks when camera state changes.
     */
    private final CameraDevice.StateCallback mCameraStateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(@NonNull CameraDevice camera) {
            mCameraOpenCloseLock.release();

            //This is called when the camera is open
            mCameraDevice = camera;
            createCameraPreview();
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice camera) {
            mCameraOpenCloseLock.release();

            //Close camera
            mCameraDevice.close();
            mCameraDevice = null;
        }

        @Override
        public void onError(@NonNull CameraDevice camera, int error) {
            mCameraOpenCloseLock.release();

            //Close camera
            mCameraDevice.close();
            mCameraDevice = null;
        }
    };

    /**
     * Public constructor.
     *
     * @param activity           Instance of the caller.
     * @param autoFitTextureView {@link AutoFitTextureView} from the view.
     */
    public Camera2Api(@NonNull Activity activity,
                      @NonNull AutoFitTextureView autoFitTextureView,
                      @NonNull CameraCallbacks cameraCallbacks) {
        super(cameraCallbacks);
        mActivity = activity;
        mCameraCallbacks = cameraCallbacks;

        //Surface texture.
        mAutoFitTextureView = autoFitTextureView;
    }

    /**
     * Given {@code choices} of {@code Size}s supported by a camera, chooses the smallest one whose
     * width and height are at least as large as the minimum of both, or an exact match if possible.
     *
     * @param choices The list of sizes that the camera supports for the intended output class
     * @return The optimal {@code Size}, or an arbitrary one if none were big enough
     */
    private static Size chooseOptimalSize(final Size[] choices, int minWidth, int minHeight) {
        final int minSize = Math.max(Math.min(minWidth, minHeight), MINIMUM_PREVIEW_SIZE);
        final Size desiredSize = new Size(minWidth, minHeight);

        // Collect the supported resolutions that are at least as big as the preview Surface
        boolean exactSizeFound = false;
        final List<Size> bigEnough = new ArrayList<>();
        //noinspection MismatchedQueryAndUpdateOfCollection
        final List<Size> tooSmall = new ArrayList<>();
        for (final Size option : choices) {
            if (option.equals(desiredSize)) {
                // Set the size but don't return yet so that remaining sizes will still be logged.
                exactSizeFound = true;
            }

            if (option.getHeight() >= minSize && option.getWidth() >= minSize) {
                bigEnough.add(option);
            } else {
                tooSmall.add(option);
            }
        }

        if (exactSizeFound) return desiredSize;

        // Pick the smallest of those, assuming we found any
        if (bigEnough.size() > 0) {
            return Collections.min(bigEnough, new CompareSizesByArea());
        } else {
            return choices[0];
        }
    }

    /**
     * Start the camera preview.
     *
     * @param cameraConfig {@link CameraConfig}
     */
    @Override
    public void startCamera(@NonNull CameraConfig cameraConfig) {
        //Validate argument
        //noinspection ConstantConditions
        if (cameraConfig == null) throw new IllegalStateException("Camera config cannot be null.");

        //Set the new camera config
        mCameraConfig = cameraConfig;

        //Start background threads to handle camera operations
        startBackgroundThread();

        //Register the surface texture callbacks
        if (mAutoFitTextureView.isAvailable()) {
            openCamera(mAutoFitTextureView.getWidth(), mAutoFitTextureView.getHeight());
        } else {
            mAutoFitTextureView.setSurfaceTextureListener(mSurfaceTextureListener);
        }
    }

    /**
     * Close and release the camera.
     */
    @Override
    public void closeCamera() {
        try {
            mCameraOpenCloseLock.acquire();
            if (mCameraCaptureSession != null) {
                mCameraCaptureSession.close();
                mCameraCaptureSession = null;
            }
            if (null != mCameraDevice) {
                mCameraDevice.close();
                mCameraDevice = null;
            }
            if (mPreviewReader != null) {
                mPreviewReader.close();
                mPreviewReader = null;
            }
        } catch (final InterruptedException e) {
            throw new RuntimeException("Interrupted while trying to lock camera closing.", e);
        } finally {
            stopBackgroundThread();
            mCameraOpenCloseLock.release();
        }
    }

    /**
     * Create the camera preview and display it according to the camera configuration.
     */
    private void createCameraPreview() {
        try {
            //Get the texture.
            SurfaceTexture texture = mAutoFitTextureView.getSurfaceTexture();
            assert texture != null;
            texture.setDefaultBufferSize(mPreviewSize.getWidth(), mPreviewSize.getHeight());

            Surface surface = new Surface(texture);

            mCaptureRequestBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            mCaptureRequestBuilder.addTarget(surface);

            // Create the reader for the preview frames.
            mPreviewReader = ImageReader.newInstance(mPreviewSize.getWidth(), mPreviewSize.getHeight(),
                    ImageFormat.JPEG, 2);
            mPreviewReader.setOnImageAvailableListener(mOnImageAvailableListener, mBackgroundHandler);
            mCaptureRequestBuilder.addTarget(mPreviewReader.getSurface());

            //noinspection ArraysAsListWithZeroOrOneArgument
            mCameraDevice.createCaptureSession(Arrays.asList(surface, mPreviewReader.getSurface()),
                    new CameraCaptureSession.StateCallback() {
                        @Override
                        public void onConfigured(@NonNull CameraCaptureSession cameraCaptureSession) {
                            //The camera is already closed
                            if (mCameraDevice == null) {
                                mCameraCallbacks.onCameraError(CameraError.ERROR_CAMERA_OPEN_FAILED);
                                return;
                            }

                            // When the session is ready, we start displaying the preview.
                            mCameraCaptureSession = cameraCaptureSession;

                            try {
                                // Auto focus should be continuous for camera preview.
                                mCaptureRequestBuilder.set(
                                        CaptureRequest.CONTROL_AF_MODE,
                                        CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
                                // Flash is automatically enabled when necessary.
                                mCaptureRequestBuilder.set(
                                        CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH);

                                // Finally, we start displaying the camera preview.
                                CaptureRequest previewRequest = mCaptureRequestBuilder.build();
                                mCameraCaptureSession.setRepeatingRequest(
                                        previewRequest, null, mBackgroundHandler);
                            } catch (final CameraAccessException | IllegalStateException e) {
                                mCameraCallbacks.onCameraError(CameraError.ERROR_CAMERA_OPEN_FAILED);
                            }
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
    private void openCamera(final int viewWidth, final int viewHeight) {
        // Add permission for camera and let user grant the permission
        if (ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            mCameraCallbacks.onCameraError(CameraError.ERROR_CAMERA_PERMISSION_NOT_AVAILABLE);
            return;
        }

        configureTransform(viewWidth, viewHeight);

        try {
            CameraManager manager = (CameraManager) mActivity.getSystemService(Context.CAMERA_SERVICE);
            assert manager != null;
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
            StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
            assert map != null;

            mPreviewSize = map.getOutputSizes(SurfaceTexture.class)[0];

            // Danger, W.R.! Attempting to use too large a preview size could  exceed the camera
            // bus' bandwidth limitation, resulting in gorgeous previews but the storage of
            // garbage capture data.
            mPreviewSize = chooseOptimalSize(map.getOutputSizes(SurfaceTexture.class), viewWidth, viewHeight);

            // We fit the aspect ratio of TextureView to the size of preview we picked.
            final int orientation = mActivity.getResources().getConfiguration().orientation;
            if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                mAutoFitTextureView.setAspectRatio(mPreviewSize.getWidth(), mPreviewSize.getHeight());
            } else {
                mAutoFitTextureView.setAspectRatio(mPreviewSize.getHeight(), mPreviewSize.getWidth());
            }

            if (!mCameraOpenCloseLock.tryAcquire(2500, TimeUnit.MILLISECONDS)) {
                throw new RuntimeException("Time out waiting to lock camera opening.");
            }
            manager.openCamera(cameraIdToOpen, mCameraStateCallback, null);
        } catch (CameraAccessException | InterruptedException e) {
            Timber.e(e);
            mCameraCallbacks.onCameraError(CameraError.ERROR_CAMERA_OPEN_FAILED);
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

    /**
     * Configures the necessary {@link android.graphics.Matrix} transformation to `mTextureView`.
     * This method should be called after the camera preview size is determined in
     * setUpCameraOutputs and also the size of `mTextureView` is fixed.
     *
     * @param viewWidth  The width of `mTextureView`
     * @param viewHeight The height of `mTextureView`
     */
    private void configureTransform(final int viewWidth, final int viewHeight) {
        if (mPreviewSize == null) return;

        final int rotation = mActivity.getWindowManager().getDefaultDisplay().getRotation();
        final Matrix matrix = new Matrix();
        final RectF viewRect = new RectF(0, 0, viewWidth, viewHeight);
        final RectF bufferRect = new RectF(0, 0, mPreviewSize.getHeight(), mPreviewSize.getWidth());
        final float centerX = viewRect.centerX();
        final float centerY = viewRect.centerY();
        if (Surface.ROTATION_90 == rotation || Surface.ROTATION_270 == rotation) {
            bufferRect.offset(centerX - bufferRect.centerX(), centerY - bufferRect.centerY());
            matrix.setRectToRect(viewRect, bufferRect, Matrix.ScaleToFit.FILL);
            final float scale = Math.max((float) viewHeight / mPreviewSize.getHeight(),
                    (float) viewWidth / mPreviewSize.getWidth());
            matrix.postScale(scale, scale, centerX, centerY);
            matrix.postRotate(90 * (rotation - 2), centerX, centerY);
        } else if (Surface.ROTATION_180 == rotation) {
            matrix.postRotate(180, centerX, centerY);
        }
        mAutoFitTextureView.setTransform(matrix);
    }

    /**
     * Compares two {@code Size}s based on their areas.
     */
    static class CompareSizesByArea implements Comparator<Size> {
        @Override
        public int compare(final Size lhs, final Size rhs) {
            // We cast here to ensure the multiplications won't overflow
            return Long.signum(
                    (long) lhs.getWidth() * lhs.getHeight() - (long) rhs.getWidth() * rhs.getHeight());
        }
    }
}

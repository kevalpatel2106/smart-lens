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

package com.kevalpatel2106.smartlens.imageClassifier;


import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.kevalpatel2106.smartlens.R;
import com.kevalpatel2106.smartlens.base.BaseFragment;
import com.kevalpatel2106.smartlens.camera.CameraCallbacks;
import com.kevalpatel2106.smartlens.camera.CameraConfig;
import com.kevalpatel2106.smartlens.camera.CameraError;
import com.kevalpatel2106.smartlens.camera.CameraPreview;
import com.kevalpatel2106.smartlens.camera.CameraUtils;
import com.kevalpatel2106.smartlens.camera.config.CameraFacing;
import com.kevalpatel2106.smartlens.camera.config.CameraResolution;
import com.kevalpatel2106.smartlens.tensorflow.TFDownloadProgressEvent;
import com.kevalpatel2106.smartlens.tensorflow.TFImageClassifier;
import com.kevalpatel2106.smartlens.tensorflow.TFModelDownloadService;
import com.kevalpatel2106.smartlens.tensorflow.TFUtils;
import com.kevalpatel2106.smartlens.utils.rxBus.RxBus;

import org.reactivestreams.Subscription;

import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

/**
 * A simple {@link Fragment} subclass.
 */
public final class ImageClassifierFragment extends BaseFragment implements CameraCallbacks {
    private static final String TAG = "ImageClassifierFragment";
    private static final long FIRST_CAPTURE_DELAY = 4000L;
    private static final long INTERVAL_DELAY = 2000L;
    private static final int REQ_CODE_CAMERA_PERMISSION = 7436;

    @BindView(R.id.camera_preview_container)
    FrameLayout mContainer;

    ProgressDialog mProgressDialog;
    CameraPreview mCameraPreview;
    TFImageClassifier mImageClassifier;
    private Disposable mTakePicDisposable;

    public ImageClassifierFragment() {
        // Required empty public constructor
    }

    /**
     * @return new instance of {@link ImageClassifierFragment}.
     */
    public static ImageClassifierFragment getNewInstance() {
        return new ImageClassifierFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        //Create the download progressbar
        mProgressDialog = new ProgressDialog(mContext);
        mProgressDialog.setMessage(getString(R.string.image_classifire_download_progressbar_message));
        mProgressDialog.setCancelable(false);

        registerModelDownloadProgressListener();

        return inflater.inflate(R.layout.fragment_camera, container, false);
    }

    /**
     * Register the bus to receive the download progress. This will update the download progress.
     */
    private void registerModelDownloadProgressListener() {
        RxBus.getDefault().register(TFDownloadProgressEvent.class)
                .doOnSubscribe(this::addSubscription)
                .doOnNext(event -> {
                    TFDownloadProgressEvent TFDownloadProgressEvent =
                            (TFDownloadProgressEvent) event.getObject();

                    if (TFDownloadProgressEvent.getErrorMsg() == null) {
                        //Error occurred while downloading
                        mProgressDialog.cancel();

                        new AlertDialog.Builder(mContext)
                                .setMessage(TFDownloadProgressEvent.getErrorMsg())
                                .setPositiveButton(android.R.string.ok, (dialogInterface, i) -> {
                                    //Kill the activity.
                                    finish();
                                })
                                .setCancelable(false)
                                .show();
                    } else if (TFDownloadProgressEvent.isDownloading() && !mProgressDialog.isShowing()) {
                        //Progress updated
                        //Display the progress percentage
                        mProgressDialog.setMessage(getString(R.string.image_classifire_download_progressbar_message)
                                + "(" + TFDownloadProgressEvent.getPercent() + "%)");
                        mProgressDialog.show();
                    } else if (!TFDownloadProgressEvent.isDownloading() && mProgressDialog.isShowing()) {
                        //Download complete
                        mProgressDialog.dismiss();

                        //Initiate the recognizer
                        safeStartImageRecognition();
                    }
                })
                .doOnDispose(() -> {
                    //Hide the progressbar
                    if (mProgressDialog != null && mProgressDialog.isShowing())
                        mProgressDialog.cancel();
                })
                .subscribe();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //Add the camera preview.
        mCameraPreview = new CameraPreview(getActivity(), this);
        mContainer.removeAllViews();
        mContainer.addView(mCameraPreview);
    }

    @Override
    public void onStart() {
        super.onStart();
        safeStartImageRecognition();
    }

    /**
     * Start the image recognition if
     * <li>Camera permission is granted</li>
     * <li>Tensorflow models are downloaded</li>
     */
    private void safeStartImageRecognition() {
        if (!TFUtils.isModelsDownloaded(mContext)) {    //Check if the tensorflow models are there
            downloadDataDialog();
        } else if (CameraUtils.checkIfCameraPermissionGranted(getActivity())) {//Start the camera.
            //Initiate the tensorflow classifier.
            if (mImageClassifier == null)
                mImageClassifier = new TFImageClassifier(getActivity());

            //Start the camera.
            mCameraPreview.startCamera(new CameraConfig().getBuilder(mContext)
                    .setCameraResolution(CameraResolution.LOW_RESOLUTION)
                    .setCameraFacing(CameraFacing.REAR_FACING_CAMERA)
                    .build());

            //Start taking picture after every second.
            Observable.interval(FIRST_CAPTURE_DELAY, INTERVAL_DELAY, TimeUnit.MILLISECONDS)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(AndroidSchedulers.mainThread())
                    .filter(l -> mCameraPreview != null
                            && mCameraPreview.isSafeToTakePicture()
                            && mImageClassifier != null)
                    .doOnSubscribe(disposable -> mTakePicDisposable = disposable)
                    .doOnNext(aLong -> mCameraPreview.takePicture())
                    .doOnError(throwable -> Snackbar.make(mContainer,
                            R.string.image_classifier_frag_error_image_detection_failed,
                            Toast.LENGTH_LONG).show())
                    .subscribe();
        } else {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, REQ_CODE_CAMERA_PERMISSION);
        }
    }

    @Override
    public void onStop() {
        super.onStop();

        //Stop and release the camera
        if (mCameraPreview != null) {
            mCameraPreview.stopPreviewAndReleaseCamera();
            if (mTakePicDisposable != null) mTakePicDisposable.dispose();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQ_CODE_CAMERA_PERMISSION:

                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //Start the camera.
                    safeStartImageRecognition();
                } else {
                    //Permission not granted. Explain dialog.
                    Snackbar.make(mContainer, R.string.camera_frag_permission_denied_statement,
                            Snackbar.LENGTH_INDEFINITE)
                            .setAction(R.string.camera_frag_btn_grant_access,
                                    view -> requestPermissions(new String[]{Manifest.permission.CAMERA},
                                            REQ_CODE_CAMERA_PERMISSION))
                            .show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public void onImageCapture(@NonNull byte[] imageCaptured) {

        //Process the image using Tf.
        Flowable<List<Recognition>> flowable = Flowable.create(e -> {
            Bitmap bitmap = BitmapFactory.decodeByteArray(imageCaptured, 0, imageCaptured.length);
            e.onNext(mImageClassifier.recognizeImage(bitmap));
            e.onComplete();
        }, BackpressureStrategy.DROP);

        final Subscription[] subscriptions = new Subscription[1];
        flowable.subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(subscription -> subscriptions[0] = subscription)
                .doOnError(t -> {
                    Timber.e(t.getMessage());
                    subscriptions[0].cancel();
                })
                .doOnComplete(() -> subscriptions[0].cancel())
                .subscribe(labels -> {
                    if (!labels.isEmpty()) {
                        Log.d(TAG, "onImageCapture: " + labels.get(0).getTitle());
                        //TODO Send the info fragment
                    }
                });
    }

    @Override
    public void onCameraError(int errorCode) {
        switch (errorCode) {
            case CameraError.ERROR_CAMERA_PERMISSION_NOT_AVAILABLE:
                requestPermissions(new String[]{Manifest.permission.CAMERA}, REQ_CODE_CAMERA_PERMISSION);
                break;
            case CameraError.ERROR_DOES_NOT_HAVE_FRONT_CAMERA:
                Snackbar.make(mContainer, R.string.image_classifier_frag_error_no_front_camera, Snackbar.LENGTH_LONG)
                        .setAction(android.R.string.ok, view -> getActivity().finish())
                        .show();
            case CameraError.ERROR_IMAGE_WRITE_FAILED:
                Snackbar.make(mContainer, R.string.image_classifier_frag_error_save_image, Snackbar.LENGTH_LONG)
                        .setAction(android.R.string.ok, view -> getActivity().finish())
                        .show();
            case CameraError.ERROR_CAMERA_OPEN_FAILED:
            default:
                Snackbar.make(mContainer, R.string.image_classifier_frag_error_camera_open, Snackbar.LENGTH_LONG)
                        .setAction(android.R.string.ok, view -> getActivity().finish())
                        .show();
                break;
        }
        if (mTakePicDisposable != null) mTakePicDisposable.dispose();
    }

    /**
     * Confirmation dialog to download the tensorflow models.
     */
    private void downloadDataDialog() {
        new AlertDialog.Builder(mContext)
                .setMessage(R.string.image_classifire_frag_additional_download_explain)
                .setPositiveButton(R.string.image_classifire_frag_btn_download, (dialogInterface, i) -> {
                    mProgressDialog.show();

                    if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.O) {
                        mContext.startService(new Intent(mContext, TFModelDownloadService.class));
                    } else {
                        mContext.startForegroundService(new Intent(mContext, TFModelDownloadService.class));
                    }
                })
                .setNegativeButton(android.R.string.cancel, (dialogInterface, i) -> finish())
                .setCancelable(false)
                .show();
    }
}

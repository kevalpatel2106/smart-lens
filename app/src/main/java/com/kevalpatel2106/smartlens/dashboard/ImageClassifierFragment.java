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

package com.kevalpatel2106.smartlens.dashboard;


import android.Manifest;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kevalpatel2106.smartlens.R;
import com.kevalpatel2106.smartlens.base.BaseFragment;
import com.kevalpatel2106.smartlens.base.BaseTextView;
import com.kevalpatel2106.smartlens.camera.CameraCallbacks;
import com.kevalpatel2106.smartlens.camera.CameraConfig;
import com.kevalpatel2106.smartlens.camera.CameraError;
import com.kevalpatel2106.smartlens.camera.CameraUtils;
import com.kevalpatel2106.smartlens.camera.config.CameraFacing;
import com.kevalpatel2106.smartlens.camera.config.CameraResolution;
import com.kevalpatel2106.smartlens.downloader.ModelDownloadService;
import com.kevalpatel2106.smartlens.imageProcessors.imageClassifier.Recognition;
import com.kevalpatel2106.smartlens.infopage.InfoActivity;
import com.kevalpatel2106.smartlens.plugins.camera2.AutoFitTextureView;
import com.kevalpatel2106.smartlens.plugins.camera2.Camera2Api;
import com.kevalpatel2106.smartlens.plugins.tensorflowImageClassifier.TFImageClassifier;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import timber.log.Timber;

/**
 * A simple {@link Fragment} subclass.
 */
public final class ImageClassifierFragment extends BaseFragment implements CameraCallbacks {
    private static final int REQ_CODE_CAMERA_PERMISSION = 7436;

    @BindView(R.id.camera_preview_container)
    AutoFitTextureView mTextureView;
    @BindView(R.id.recognition_tv)
    BaseTextView mClassifiedTv;

    List<Recognition> mLastRecognition;

    Camera2Api mCamera2Api;
    TFImageClassifier mImageClassifier;
    private volatile boolean mIsProcessing;
    private ProgressDialog mProgressDialog;
    private BroadcastReceiver mDownloadProgressReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (mProgressDialog != null && mProgressDialog.isShowing())
                mProgressDialog.cancel();

            LocalBroadcastManager.getInstance(mContext).unregisterReceiver(mDownloadProgressReceiver);

            if (intent.hasExtra(ModelDownloadService.DOWNLOAD_ERROR_MESSAGE)) {
                new AlertDialog.Builder(mContext, R.style.AlertDialogTheme)
                        .setMessage(intent.getStringExtra(ModelDownloadService.DOWNLOAD_ERROR_MESSAGE))
                        .setPositiveButton(android.R.string.ok, (dialogInterface, i) -> {
                            //Kill the activity.
                            finish();
                        })
                        .setCancelable(false)
                        .show();
            } else {
                safeStartImageRecognition();
            }
        }
    };

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
        mImageClassifier = new TFImageClassifier(mContext);
        mLastRecognition = new ArrayList<>();

        return inflater.inflate(R.layout.fragment_image_classifire, container, false);
    }

    /**
     * Open the {@link com.kevalpatel2106.smartlens.infopage.InfoActivity} if there are any last
     * recognized items.
     */
    @OnClick(R.id.recognition_tv)
    void openInfoScreen() {
        //If there are no labels.
        if (mLastRecognition.isEmpty()) return;

        //Stop the image recognition and taking pictures.
        stopImageRecognition();

        //Open the labels.
        ArrayList<String> labels = new ArrayList<>(mLastRecognition.size());
        for (Recognition recognition : mLastRecognition) labels.add(recognition.getTitle());
        InfoActivity.launch(getActivity(), labels, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mClassifiedTv.setVisibility(View.GONE);

        //Add the camera preview.
        mCamera2Api = new Camera2Api(getActivity(),
                mTextureView,
                this);
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
        if (!mImageClassifier.isModelDownloaded()) {    //Check if the tensorflow models are there
            downloadDataDialog();
        } else if (CameraUtils.checkIfCameraPermissionGranted(getActivity())) {//Start the camera.
            if (!mImageClassifier.isSafeToStart()) mImageClassifier.init();

            //Start the camera.
            mCamera2Api.startCamera(new CameraConfig().getBuilder(mContext)
                    .setCameraResolution(CameraResolution.LOW_RESOLUTION)
                    .setCameraFacing(CameraFacing.REAR_FACING_CAMERA)
                    .build());
        } else {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, REQ_CODE_CAMERA_PERMISSION);
        }
    }

    private void stopImageRecognition() {
        mCamera2Api.closeCamera();
    }

    @Override
    public void onStop() {
        super.onStop();
        stopImageRecognition();
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
                    Snackbar.make(mTextureView, R.string.camera_frag_permission_denied_statement,
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
    public void onImageCapture(@Nullable byte[] imageBytes) {
        if (mIsProcessing) return;
        mIsProcessing = true;

        Bitmap bitmap = null;
        if (imageBytes != null) bitmap = CameraUtils.bytesToBitmap(imageBytes);

        if (bitmap != null) {
            List<Recognition> labels = mImageClassifier.scan(bitmap);
            new Handler(Looper.getMainLooper()).post(() -> {
                if (!labels.isEmpty()) {
                    Timber.d("Image label: " + labels.get(0).getTitle());
                    mClassifiedTv.setVisibility(View.VISIBLE);
                    mClassifiedTv.setText(labels.get(0).getTitle());

                    //Load as the last recognition
                    mLastRecognition.clear();
                    mLastRecognition.addAll(labels);
                }

                mIsProcessing = false;
            });
        }
    }

    @Override
    public void onCameraError(int errorCode) {
        switch (errorCode) {
            case CameraError.ERROR_CAMERA_PERMISSION_NOT_AVAILABLE:
                requestPermissions(new String[]{Manifest.permission.CAMERA}, REQ_CODE_CAMERA_PERMISSION);
                break;
            case CameraError.ERROR_DOES_NOT_HAVE_FRONT_CAMERA:
                Snackbar.make(mTextureView, R.string.image_classifier_frag_error_no_front_camera, Snackbar.LENGTH_LONG)
                        .setAction(android.R.string.ok, view -> getActivity().finish())
                        .show();
                break;
            case CameraError.ERROR_IMAGE_WRITE_FAILED:
                Snackbar.make(mTextureView, R.string.image_classifier_frag_error_save_image, Snackbar.LENGTH_LONG)
                        .setAction(android.R.string.ok, view -> getActivity().finish())
                        .show();
                break;
            case CameraError.ERROR_CAMERA_OPEN_FAILED:
            default:
                Snackbar.make(mTextureView, R.string.image_classifier_frag_error_camera_open, Snackbar.LENGTH_LONG)
                        .setAction(android.R.string.ok, view -> getActivity().finish())
                        .show();
                break;
        }
        stopImageRecognition();
    }

    /**
     * Confirmation dialog to download the tensorflow models.
     */
    private void downloadDataDialog() {
        new AlertDialog.Builder(mContext, R.style.AlertDialogTheme)
                .setMessage(R.string.image_classifire_frag_additional_download_explain)
                .setPositiveButton(R.string.image_classifire_frag_btn_download, (dialogInterface, i) -> {
                    mProgressDialog = new ProgressDialog(mContext);
                    mProgressDialog.setMessage(getString(R.string.image_classifire_download_progressbar_message));
                    mProgressDialog.setCancelable(false);
                    mProgressDialog.show();

                    //Register the local broadcast
                    LocalBroadcastManager.getInstance(mContext).registerReceiver(mDownloadProgressReceiver,
                            new IntentFilter(ModelDownloadService.DOWNLOAD_COMPLETE_BROADCAST));

                    //Start downloading message.
                    mImageClassifier.downloadModels();
                })
                .setNegativeButton(android.R.string.cancel, (dialogInterface, i) -> finish())
                .setCancelable(false)
                .show();
    }
}

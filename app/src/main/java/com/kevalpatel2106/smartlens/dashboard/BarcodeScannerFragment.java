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
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
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
import com.kevalpatel2106.smartlens.imageProcessors.barcode.BarcodeInfo;
import com.kevalpatel2106.smartlens.infopage.InfoActivity;
import com.kevalpatel2106.smartlens.plugins.camera2.AutoFitTextureView;
import com.kevalpatel2106.smartlens.plugins.camera2.Camera2Api;
import com.kevalpatel2106.smartlens.plugins.visionBarcodeScanner.BarcodeScanner;

import org.reactivestreams.Subscription;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

/**
 * A simple {@link Fragment} subclass.
 */
public final class BarcodeScannerFragment extends BaseFragment implements CameraCallbacks {
    private static final int REQ_CODE_CAMERA_PERMISSION = 7436;

    @BindView(R.id.camera_preview_container)
    AutoFitTextureView mAutoFitTextureView;
    @BindView(R.id.recognition_tv)
    BaseTextView mScannedInfoTv;
    Camera2Api mCamera2Api;
    private BarcodeScanner mBarcodeScanner;

    public BarcodeScannerFragment() {
        // Required empty public constructor
    }

    /**
     * @return new instance of {@link BarcodeScannerFragment}.
     */
    public static BarcodeScannerFragment getNewInstance() {
        return new BarcodeScannerFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        mBarcodeScanner = new BarcodeScanner(mContext);
        return inflater.inflate(R.layout.fragment_image_classifire, container, false);
    }

    /**
     * Open the {@link InfoActivity} if there are any last
     * recognized items.
     */
    @OnClick(R.id.recognition_tv)
    void openInfoScreen() {
        stopBarcodeScanner();
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mScannedInfoTv.setVisibility(View.GONE);

        //Add the camera preview.
        mCamera2Api = new Camera2Api(getActivity(), mAutoFitTextureView, this);
    }

    @Override
    public void onStart() {
        super.onStart();
        safeStartBarcodeScanner();
    }

    /**
     * Start the image recognition if
     * <li>Camera permission is granted</li>
     * <li>Tensorflow models are downloaded</li>
     */
    private void safeStartBarcodeScanner() {
        if (CameraUtils.checkIfCameraPermissionGranted(getActivity())) {//Start the camera.
            //Start the camera.
            mCamera2Api.startCamera(new CameraConfig().getBuilder(mContext)
                    .setCameraResolution(CameraResolution.LOW_RESOLUTION)
                    .setCameraFacing(CameraFacing.REAR_FACING_CAMERA)
                    .build());
        } else {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, REQ_CODE_CAMERA_PERMISSION);
        }
    }

    private void stopBarcodeScanner() {
        if (mCamera2Api != null) mCamera2Api.closeCamera();
    }

    @Override
    public void onStop() {
        super.onStop();
        stopBarcodeScanner();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQ_CODE_CAMERA_PERMISSION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //Start the camera.
                    safeStartBarcodeScanner();
                } else {
                    //Permission not granted. Explain dialog.
                    Snackbar.make(mAutoFitTextureView, R.string.camera_frag_permission_denied_statement,
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
        //Process the image using Tf.
        Flowable<BarcodeInfo> flowable = Flowable.create(e -> {
            Bitmap bitmap = null;
            if (imageBytes != null) bitmap = CameraUtils.bytesToBitmap(imageBytes);
            if (bitmap != null) e.onNext(mBarcodeScanner.scan(bitmap));
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
                .subscribe(barcodeInfo -> {
                    //TODO Display the info
                });
    }

    @Override
    public void onCameraError(int errorCode) {
        switch (errorCode) {
            case CameraError.ERROR_CAMERA_PERMISSION_NOT_AVAILABLE:
                requestPermissions(new String[]{Manifest.permission.CAMERA}, REQ_CODE_CAMERA_PERMISSION);
                break;
            case CameraError.ERROR_DOES_NOT_HAVE_FRONT_CAMERA:
                Snackbar.make(mAutoFitTextureView, R.string.image_classifier_frag_error_no_front_camera, Snackbar.LENGTH_LONG)
                        .setAction(android.R.string.ok, view -> getActivity().finish())
                        .show();
            case CameraError.ERROR_IMAGE_WRITE_FAILED:
                Snackbar.make(mAutoFitTextureView, R.string.image_classifier_frag_error_save_image, Snackbar.LENGTH_LONG)
                        .setAction(android.R.string.ok, view -> getActivity().finish())
                        .show();
            case CameraError.ERROR_CAMERA_OPEN_FAILED:
            default:
                Snackbar.make(mAutoFitTextureView, R.string.image_classifier_frag_error_camera_open, Snackbar.LENGTH_LONG)
                        .setAction(android.R.string.ok, view -> getActivity().finish())
                        .show();
                break;
        }
        stopBarcodeScanner();
    }
}

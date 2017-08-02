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

package com.kevalpatel2106.smartlens.plugins.tensorflowObjectRecogniser;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.kevalpatel2106.smartlens.R;
import com.kevalpatel2106.smartlens.utils.FileUtils;
import com.kevalpatel2106.smartlens.utils.rxBus.Event;
import com.kevalpatel2106.smartlens.utils.rxBus.RxBus;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Action;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Keval on 31-Jul-17.
 */

public class TFModelDownloadService extends Service {
    private static final String ARG_CANCEL_DOWNLOAD = "arg_cancel_download";
    private static final String TENSORFLOW_GRAPH_URL = "https://github.com/kevalpatel2106/smart-lens/blob/master/tf_models/tensorflow_inception_graph.pb?raw=true";
    private static final String TENSORFLOW_LABEL_URL = "https://raw.githubusercontent.com/kevalpatel2106/smart-lens/master/tf_models/imagenet_comp_graph_label_strings.txt";
    private CompositeDisposable mCompositeDisposable;

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Override
    public void onCreate() {
        super.onCreate();
        //Make foreground
        startForeground(TFModelDownloadingNotification.NOTIFICATION_TAG,
                TFModelDownloadingNotification.getNotification(this, 0, true));

        mCompositeDisposable = new CompositeDisposable();

        //Download tensorflow graph
        File file = TFUtils.getImageGraph(this);
        if (file.exists()) file.delete();
        downloadFile(TENSORFLOW_GRAPH_URL, file, () -> {

            //Download supported labels
            File fileLabel = TFUtils.getImageLabels(TFModelDownloadService.this);
            if (fileLabel.exists()) file.delete();
            downloadFile(TENSORFLOW_LABEL_URL, file, this::stopSelf);
        });
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent.hasExtra(ARG_CANCEL_DOWNLOAD)) stopSelf();
        return START_NOT_STICKY;
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void downloadFile(@NonNull String urlToDownload,
                              @NonNull File file,
                              @NonNull Action actionOnComplete) {
        Observable<Integer> observable = Observable.create(e -> {
            try {
                e.onNext(-1);       //Start indeterminant process

                //Create temp file.
                File tempFile = new File(FileUtils.getCacheDir(TFModelDownloadService.this)
                        + "/" + file.getName());
                tempFile.createNewFile();

                //Create URL
                URL url = new URL(urlToDownload);
                URLConnection connection = url.openConnection();
                connection.connect();
                // this will be useful so that you can show a typical 0-100% progress bar
                int fileLength = connection.getContentLength();

                // download the file
                InputStream input = new BufferedInputStream(connection.getInputStream());
                OutputStream output = new FileOutputStream(tempFile);

                byte data[] = new byte[2048];
                long total = 0;
                int count;
                while ((count = input.read(data)) != -1) {
                    total += count;
                    output.write(data, 0, count);

                    // publishing the progress....
                    int percent = (int) (total * 100 / fileLength);
                    e.onNext(percent);
                }
                output.flush();
                output.close();
                input.close();

                //Make indeterminate
                TFModelDownloadingNotification.notify(this, 0, true);

                //Copy the file to the main url
                if (FileUtils.copyFile(file, tempFile)) {
                    e.onComplete();
                } else {
                    e.onError(new Throwable(getString(R.string.model_download_service_error_download)));
                }
            } catch (IOException exp) {
                //noinspection ResultOfMethodCallIgnored
                file.delete();
                exp.printStackTrace();
                e.onError(new Throwable(getString(R.string.model_download_service_error_download)));
            }
        });

        observable.observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .doOnSubscribe(disposable -> mCompositeDisposable.add(disposable))
                .doOnComplete(actionOnComplete)
                .subscribe(integer -> {
                            //Update the notification with the new percentage
                            TFModelDownloadingNotification.notify(TFModelDownloadService.this,
                                    integer,
                                    integer < 0);

                            //Publish the event to other activity
                            RxBus.getDefault()
                                    .post(new Event(new TFDownloadProgressEvent(true, integer)));
                        },
                        throwable -> {
                            RxBus.getDefault()
                                    .post(new Event(new TFDownloadProgressEvent(throwable.getMessage())));
                            stopSelf();
                        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mCompositeDisposable.dispose();

        //Remove the notification
        TFModelDownloadingNotification.cancel(this);

        //Hide the dialog.
        RxBus.getDefault().post(new Event(new TFDownloadProgressEvent(false, 0)));
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}

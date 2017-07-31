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

package com.kevalpatel2106.smartlens.tensorflow;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.kevalpatel2106.smartlens.utils.FileUtils;
import com.kevalpatel2106.smartlens.utils.rxBus.Event;
import com.kevalpatel2106.smartlens.utils.rxBus.RxBus;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Keval on 31-Jul-17.
 */

public class ModelDownloadService extends Service {
    private static final String TENSORFLOW_GRAPH_URL = "https://github.com/kevalpatel2106/smart-lens/blob/master/tf_models/tensorflow_inception_graph.pb?raw=true";
    private static final String TENSORFLOW_LABEL_URL = "https://raw.githubusercontent.com/kevalpatel2106/smart-lens/master/tf_models/imagenet_comp_graph_label_strings.txt";
    private Disposable mDisposable;

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Override
    public void onCreate() {
        super.onCreate();
        //Make foreground
        startForeground(ModelDownloadingNotification.NOTIFICATION_TAG,
                ModelDownloadingNotification.notify(this, 0, true));

        Observable<File> observable = Observable.create(e -> {
            //Download tensorflow graph
            File file = TensorflowUtils.getImageGraph(this);
            if (file.exists()) file.delete();
            if (!downloadFile(TENSORFLOW_GRAPH_URL, file)) e.onError(new Throwable());
            else e.onNext(file);

            //Download supported labels
            file = TensorflowUtils.getImageLabels(this);
            if (file.exists()) file.delete();
            if (!downloadFile(TENSORFLOW_LABEL_URL, file)) e.onError(new Throwable());
            else e.onNext(file);
            e.onComplete();
        });

        observable.observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .doOnSubscribe(disposable -> mDisposable = disposable)
                .doOnComplete(this::stopSelf)
                .doOnError(throwable -> stopSelf())
                .subscribe();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_NOT_STICKY;
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private boolean downloadFile(@NonNull String urlToDownload, @NonNull File file) {
        try {
            File tempFile = new File(FileUtils.getCacheDir(this)
                    + "/" + file.getName());
            tempFile.createNewFile();

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
                ModelDownloadingNotification.notify(this,
                        percent,
                        false);
                RxBus.getDefault().post(new Event(new DownloadProgressEvent(true, percent)));
            }
            output.flush();
            output.close();
            input.close();
            return copyFile(file, tempFile);
        } catch (IOException e) {
            //noinspection ResultOfMethodCallIgnored
            file.delete();
            e.printStackTrace();
            return false;
        }
    }

    private boolean copyFile(@NonNull File destFile, @NonNull File srcFile) {
        //Make indeterminate
        ModelDownloadingNotification.notify(this, 0, true);
        try {
            // download the file
            InputStream input = new FileInputStream(srcFile);
            OutputStream output = new FileOutputStream(destFile);

            byte data[] = new byte[4096];
            int count;
            while ((count = input.read(data)) != -1) output.write(data, 0, count);

            output.flush();
            output.close();
            input.close();

            //noinspection ResultOfMethodCallIgnored
            srcFile.delete();
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mDisposable.dispose();

        //Remove the notification
        ModelDownloadingNotification.cancel(this);

        //Hide the dialog.
        RxBus.getDefault().post(new Event(new DownloadProgressEvent(false, 0)));
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}

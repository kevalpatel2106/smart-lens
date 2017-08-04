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

package com.kevalpatel2106.smartlens.downloader;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;

import com.kevalpatel2106.smartlens.R;
import com.kevalpatel2106.smartlens.utils.FileUtils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Iterator;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Keval on 31-Jul-17.
 */

public class ModelDownloadService extends Service {

    public static final String DOWNLOAD_COMPLETE_BROADCAST = "download_progress";
    public static final String DOWNLOAD_ERROR_MESSAGE = "download_progress_error_message";

    private static final String ARG_CANCEL_DOWNLOAD = "arg_cancel_download";
    private static final String ARG_DOWNLOAD = "arg_download";
    private CompositeDisposable mCompositeDisposable;
    private HashMap<String, File> mDownloads;
    private Iterator<String> mUrlIterator;

    public static void startDownload(@NonNull Context context,
                                     @NonNull HashMap<String, File> downloads) {
        Intent intent = new Intent(context, ModelDownloadService.class);
        intent.putExtra(ARG_DOWNLOAD, downloads);

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.O) {
            context.startService(intent);
        } else {
            context.startForegroundService(intent);
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Override
    public void onCreate() {
        super.onCreate();
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.O) { //Make foreground
            startForeground(ModelDownloadingNotification.NOTIFICATION_TAG,
                    ModelDownloadingNotification.getNotification(this, 0, true));
        }

        mCompositeDisposable = new CompositeDisposable();
    }

    @SuppressWarnings("unchecked")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent.hasExtra(ARG_CANCEL_DOWNLOAD)) {
            stopSelf();
        } else {
            //Get the download
            mDownloads = (HashMap<String, File>) intent.getSerializableExtra(ARG_DOWNLOAD);
            if (mDownloads == null || mDownloads.isEmpty())
                throw new IllegalArgumentException("Downloads cannot be null or empty.");

            mUrlIterator = mDownloads.keySet().iterator();

            //Download
            String firstUrl = mUrlIterator.next();
            downloadFile(firstUrl, mDownloads.get(firstUrl));
        }
        return START_NOT_STICKY;
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void downloadFile(@NonNull String urlToDownload,
                              @NonNull File file) {

        //Create the observable.
        //This will download files.
        Observable<Integer> observable = Observable.create(e -> {
            try {
                e.onNext(-1);       //Start indeterminant process

                //Create temp file.
                File tempFile = new File(FileUtils.getCacheDir(ModelDownloadService.this)
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
                ModelDownloadingNotification.notify(this, 0, true);

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
                .doOnComplete(() -> {
                    //Check if there are more files to download?
                    if (mUrlIterator.hasNext()) {

                        //Start downloading the next file.
                        String url = mUrlIterator.next();
                        downloadFile(url, mDownloads.get(url));
                    } else {

                        //There are no more files to download
                        //Stop the service.
                        LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(DOWNLOAD_COMPLETE_BROADCAST));
                        stopSelf();
                    }
                })
                .subscribe(percentage -> {
                            //Update the notification with the new percentage
                            ModelDownloadingNotification.notify(ModelDownloadService.this,
                                    percentage, percentage < 0);
                        },
                        throwable -> {
                            //Error occurred.
                            //Stop the service.
                            Intent intent = new Intent(DOWNLOAD_COMPLETE_BROADCAST);
                            intent.putExtra(DOWNLOAD_ERROR_MESSAGE, throwable.getMessage());
                            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
                            stopSelf();
                        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mCompositeDisposable.dispose();

        //Remove the notification
        ModelDownloadingNotification.cancel(this);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}

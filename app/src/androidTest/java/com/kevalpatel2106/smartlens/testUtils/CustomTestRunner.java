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

package com.kevalpatel2106.smartlens.testUtils;

/**
 * Created by Keval on 27-Jul-17.
 */

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.test.runner.AndroidJUnitRunner;
import android.util.Log;

import com.kevalpatel2106.smartlens.plugins.tensorflowImageClassifier.TFUtils;
import com.kevalpatel2106.smartlens.utils.FileUtils;

import org.junit.Assert;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLConnection;

import timber.log.Timber;

/**
 * Tests can fail for other reasons than code, it´ because of the animations and espresso sync and
 * emulator state (screen off or locked)
 * <p/>
 * Before all the tests prepare the device to run tests and avoid these problems.
 * <p/>
 * - Disable animations
 * - Disable keyguard lock
 * - Set it to be awake all the time (dont let the processor sleep)
 *
 * @see <a href="u2020 open source app by Jake Wharton">https://github.com/JakeWharton/u2020</a>
 * @see <a href="Daj gist">https://gist.github.com/daj/7b48f1b8a92abf960e7b</a>
 */
public final class CustomTestRunner extends AndroidJUnitRunner {
    private static final String TAG = "CustomTestRunner";
    private static final String TENSORFLOW_GRAPH_URL = "https://github.com/kevalpatel2106/smart-lens/blob/master/tf_models/tensorflow_inception_graph.pb?raw=true";
    private static final String TENSORFLOW_LABEL_URL = "https://raw.githubusercontent.com/kevalpatel2106/smart-lens/master/tf_models/imagenet_comp_graph_label_strings.txt";

    @Override
    public void onStart() {
        Context context = CustomTestRunner.this.getTargetContext().getApplicationContext();
        runOnMainSync(() -> {
            disableAnimations(context);
            unlockScreen(context, CustomTestRunner.class.getSimpleName());
            keepScreenAwake(context, CustomTestRunner.class.getSimpleName());
        });

        //Download models.
        downloadTfModelsIfNot(context);
        super.onStart();
    }

    /**
     * This will download the tensorflow models if the models are not already present before each test.
     *
     * @param context Instance of the application.
     */
    private void downloadTfModelsIfNot(Context context) {
        //Check if the TF models are available?
        if (!TFUtils.isModelsDownloaded(context)) {
            try {
                //If TF models are not available,
                //Download TF graph
                if (download(TENSORFLOW_GRAPH_URL, context, TFUtils.getImageGraph(context))) {
                    //Download TF label
                    download(TENSORFLOW_LABEL_URL, context, TFUtils.getImageLabels(context));
                }
            } catch (IOException e) {
                e.printStackTrace();
                Assert.fail("Cannot download the tensorflow models.");
            }
        }
    }

    /**
     * Download file and print the download percentage.
     *
     * @param urlToDownload Url to download.
     * @param context       Instance of the caller.
     * @param file          File where download will store.
     * @return True if the download is successful.
     * @throws IOException if socket timeout or file write fails.
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    private boolean download(String urlToDownload, Context context, File file) throws IOException {
        //Create temp file.
        File tempFile = new File(FileUtils.getCacheDir(context)
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
            Timber.d(percent + "%");
        }
        output.flush();
        output.close();
        input.close();

        //Copy the file to the main url
        return FileUtils.copyFile(file, tempFile);
    }


    @Override
    public void finish(int resultCode, Bundle results) {
        super.finish(resultCode, results);

        //Re-enable all the animations.
        enableAnimations(getContext());
    }

    /**
     * Acquire the wakelock to keep the screen awake.
     *
     * @param context Instance of the app.
     * @param name    Name of the wakelock. (Tag)
     */
    @SuppressLint("WakelockTimeout")
    private void keepScreenAwake(Context context, String name) {
        PowerManager power = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        //noinspection ConstantConditions
        power.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP
                | PowerManager.ON_AFTER_RELEASE, name)
                .acquire();
    }

    /**
     * Unlock the screen.
     *
     * @param context Instance of the app.
     * @param name    Name of the keyguard. (Tag)
     */
    @SuppressWarnings({"ConstantConditions", "deprecation"})
    private void unlockScreen(Context context, String name) {
        KeyguardManager keyguard = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
        keyguard.newKeyguardLock(name).disableKeyguard();
    }

    /**
     * Disable all animations by applying 0 scale to {@link #setSystemAnimationsScale(float)}.
     *
     * @param context Instance of the caller.
     */
    private void disableAnimations(Context context) {
        int permStatus = context.checkCallingOrSelfPermission(Manifest.permission.SET_ANIMATION_SCALE);
        if (permStatus == PackageManager.PERMISSION_GRANTED) {
            setSystemAnimationsScale(0.0f);
        }
    }

    /**
     * Enable all animations by applying 1 scale to {@link #setSystemAnimationsScale(float)}.
     *
     * @param context Instance of the caller.
     */
    private void enableAnimations(Context context) {
        int permStatus = context.checkCallingOrSelfPermission(Manifest.permission.SET_ANIMATION_SCALE);
        if (permStatus == PackageManager.PERMISSION_GRANTED) {
            setSystemAnimationsScale(1.0f);
        }
    }

    /**
     * Set the system animation scale.
     *
     * @param animationScale Scale.
     */
    private void setSystemAnimationsScale(float animationScale) {
        try {
            Class<?> windowManagerStubClazz = Class.forName("android.view.IWindowManager$Stub");
            Method asInterface = windowManagerStubClazz.getDeclaredMethod("asInterface", IBinder.class);
            @SuppressLint("PrivateApi") Class<?> serviceManagerClazz = Class.forName("android.os.ServiceManager");
            Method getService = serviceManagerClazz.getDeclaredMethod("getService", String.class);
            Class<?> windowManagerClazz = Class.forName("android.view.IWindowManager");
            Method setAnimationScales = windowManagerClazz.getDeclaredMethod("setAnimationScales", float[].class);
            Method getAnimationScales = windowManagerClazz.getDeclaredMethod("getAnimationScales");

            IBinder windowManagerBinder = (IBinder) getService.invoke(null, "window");
            Object windowManagerObj = asInterface.invoke(null, windowManagerBinder);
            float[] currentScales = (float[]) getAnimationScales.invoke(windowManagerObj);
            for (int i = 0; i < currentScales.length; i++) {
                currentScales[i] = animationScale;
            }
            setAnimationScales.invoke(windowManagerObj, new Object[]{currentScales});
            Log.d(TAG, "Changed permissions of animations");
        } catch (Exception e) {
            Log.e(TAG, "Could not change animation scale to " + animationScale + " :'(");
        }
    }
}
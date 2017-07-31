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

package com.kevalpatel2106.smartlens.utils;

import android.content.Context;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import io.reactivex.annotations.NonNull;

/**
 * Created by Keval on 20-Dec-16.
 * Utility functions related to file and storage.
 *
 * @author {@link 'https://github.com/kevalpatel2106'}
 */

public class FileUtils {

    private FileUtils() {
        throw new RuntimeException("Cannot initialize this class.");
    }


    /**
     * @param context Instance of the caller.
     * @return If the external cache is available than this will return external cache directory or
     * else it will display internal cache directory.
     */
    @NonNull
    public static File getCacheDir(@NonNull Context context) {
        return context.getExternalCacheDir() != null ? context.getExternalCacheDir() : context.getCacheDir();
    }

    public static boolean copyFile(@NonNull File destFile,
                                   @NonNull File srcFile) {
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

}

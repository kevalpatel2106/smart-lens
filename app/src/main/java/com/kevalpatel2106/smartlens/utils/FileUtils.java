package com.kevalpatel2106.smartlens.utils;

import android.content.Context;

import java.io.File;

import io.reactivex.annotations.NonNull;

/**
 * Created by Keval on 20-Dec-16.
 * Utility functions related to file and storage.
 *
 * @author {@link 'https://github.com/kevalpatel2106'}
 */

public class FileUtils {

    /**
     * @param context Instance of the caller.
     * @return If the external cache is available than this will return external cache directory or
     * else it will display internal cache directory.
     */
    @NonNull
    public static File getCacheDir(@NonNull Context context) {
        return context.getExternalCacheDir() != null ? context.getExternalCacheDir() : context.getCacheDir();
    }
}

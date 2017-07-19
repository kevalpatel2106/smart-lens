package com.kevalpatel2106.smartlens.utils;

import android.content.Context;

import java.io.File;

/**
 * Created by Keval on 20-Dec-16.
 * Utility functions related to file and storage.
 *
 * @author {@link 'https://github.com/kevalpatel2106'}
 */

public class FileUtils {

    public static File getCacheDir(Context context) {
        return context.getCacheDir() == null ? context.getExternalCacheDir() : context.getCacheDir();
    }
}

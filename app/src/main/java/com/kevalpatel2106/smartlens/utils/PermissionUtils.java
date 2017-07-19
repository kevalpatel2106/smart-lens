package com.kevalpatel2106.smartlens.utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;

/**
 * Created by Keval on 20-Dec-16.
 * Utility methods related to permissions.
 *
 * @author {@link 'https://github.com/kevalpatel2106'}
 */

public class PermissionUtils {

    public static boolean checkPermission(@NonNull Context context, @NonNull String permission) {
        return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED;
    }
}

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

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.support.v4.app.NotificationCompat;

import com.kevalpatel2106.smartlens.R;

/**
 * Helper class for showing and canceling model downloading
 * notifications.
 * <p>
 * This class makes heavy use of the {@link NotificationCompat.Builder} helper
 * class to create notifications in a backward-compatible way.
 */
final class ModelDownloadingNotification {
    /**
     * The unique identifier for this type of notification.
     */
    static final int NOTIFICATION_TAG = 5634;

    /**
     * Shows the notification, or updates a previously shown notification of
     * this type, with the given parameters.
     *
     * @see #cancel(Context)
     */
    static void notify(final Context context, int progress, boolean isIndeterminant) {
        final NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (nm != null)
            nm.notify(NOTIFICATION_TAG, getNotification(context, progress, isIndeterminant));
    }

    @SuppressWarnings("deprecation")
    static Notification getNotification(Context context, int progress, boolean isIndeterminant) {
        final NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_stat_model_downloading)
                .setContentTitle(context.getString(R.string.model_downloading_notification_title))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setProgress(100, progress, isIndeterminant)
                .setOngoing(true)
                .setAutoCancel(false);
        return builder.build();
    }

    /**
     * Cancels any notifications of this type previously shown
     */
    static void cancel(final Context context) {
        final NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (nm != null) nm.cancel(NOTIFICATION_TAG);
    }
}

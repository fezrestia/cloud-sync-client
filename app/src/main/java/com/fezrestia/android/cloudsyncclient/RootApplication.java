package com.fezrestia.android.cloudsyncclient;

import android.app.Application;
import android.content.Context;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import com.fezrestia.android.util.log.Log;

public class RootApplication extends Application {
    // Log tag.
    public static final String TAG = "RootApplication";
    // Log flag.
    public static final boolean IS_DEBUG = false || Log.IS_DEBUG;

    // Notification ID.
    private static final int NOTIFICATION_ERROR_ID = 1000;

    /**
     * Send notification to system tray.
     *
     * @param context Master context.
     * @param title Notification title.
     * @param text Notification body.
     */
    public static void sendNotification(Context context, String title, String text) {
        NotificationCompat.Builder builder  = new NotificationCompat.Builder(context);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setContentTitle(title);
        builder.setContentText(text);

        NotificationManagerCompat manager = NotificationManagerCompat.from(context);

        manager.notify(NOTIFICATION_ERROR_ID, builder.build());
    }
}

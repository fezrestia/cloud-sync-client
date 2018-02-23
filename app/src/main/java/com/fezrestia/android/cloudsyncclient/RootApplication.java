package com.fezrestia.android.cloudsyncclient;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import com.fezrestia.android.cloudsyncclient.firebase.FcmMessagingService;
import com.fezrestia.android.cloudsyncclient.zerosim.ZeroSimConstants;
import com.fezrestia.android.cloudsyncclient.zerosim.ZeroSimFcmCallback;
import com.fezrestia.android.util.log.Log;

public class RootApplication extends Application {
    // Log tag.
    public static final String TAG = "RootApplication";
    // Log flag.
    @SuppressWarnings("PointlessBooleanExpression")
    public static final boolean IS_DEBUG = false || Log.IS_DEBUG;

    // Application context.
    private static Context mAppContext = null;

    // Notification ID.
    private static final int NOTIFICATION_ERROR_ID = 1000;

    // Global SharedPreferences.
    private static SharedPreferences mGlobalSharedPreferences = null;
    // SharedPreferences version.
    private static final String KEY_SHARED_PREFERENCES_VERSION = "key-version";
    private static final int VAL_SHARED_PREFERENCES_VERSION = 1;

    /**
     * Get application context from anywhere.
     *
     * @return Context.
     */
    public static Context getContext() {
        return mAppContext;
    }

    @Override
    public void onCreate() {
        if (IS_DEBUG) Log.logDebug(TAG, "onCreate() : E");
        super.onCreate();

        mAppContext = this;

        // SharedPreferences.
        setupSharedPreferences(this);

        // Firebase.
        setupFcmCallbacks();

        if (IS_DEBUG) Log.logDebug(TAG, "onCreate() : X");
    }

    @Override
    public void onTerminate() {
        if (IS_DEBUG) Log.logDebug(TAG, "onTerminat() : E");

        // Release.
        mGlobalSharedPreferences = null;

        super.onTerminate();
        if (IS_DEBUG) Log.logDebug(TAG, "onTerminat() : X");
    }

    private static void setupSharedPreferences(Context context) {
        // Get SharedPreferences accessor.
        mGlobalSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        // Check version.
        int curVer = mGlobalSharedPreferences.getInt(KEY_SHARED_PREFERENCES_VERSION, 0);
        if (curVer != VAL_SHARED_PREFERENCES_VERSION) {
            mGlobalSharedPreferences.edit().clear().apply();
            mGlobalSharedPreferences.edit().putInt(
                    KEY_SHARED_PREFERENCES_VERSION,
                    VAL_SHARED_PREFERENCES_VERSION)
                    .apply();
        }
    }

    /**
     * Get global SharedPreference instance.
     *
     * @param context Context
     * @return SharedPrefereces
     */
    public static SharedPreferences getGlobalSharedPreferences(Context context) {
        if (mGlobalSharedPreferences == null) {
            setupSharedPreferences(context);
        }
        return mGlobalSharedPreferences;
    }

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

    private void setupFcmCallbacks() {
        FcmMessagingService.registerCallback(
                ZeroSimConstants.SIM_STATS_FCM_CALLBACK_REG_KEY,
                new ZeroSimFcmCallback());

    }
}

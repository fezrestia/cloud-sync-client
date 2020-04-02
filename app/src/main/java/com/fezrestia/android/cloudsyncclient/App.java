package com.fezrestia.android.cloudsyncclient;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;

import com.fezrestia.android.util.log.Log;

public class App extends Application {
    // Log tag.
    public static final String TAG = "RootApplication";
    // Log flag.
    @SuppressWarnings("PointlessBooleanExpression")
    public static final boolean IS_DEBUG = false || Log.IS_DEBUG;

    // Total UI thread handler.
    public static Handler ui = new Handler();

    // Global SharedPreferences.
    private static SharedPreferences mGlobalSharedPreferences = null;
    // SharedPreferences version.
    private static final String KEY_SHARED_PREFERENCES_VERSION = "key-version";
    private static final int VAL_SHARED_PREFERENCES_VERSION = 3;

    @Override
    public void onCreate() {
        if (IS_DEBUG) Log.logDebug(TAG, "onCreate() : E");
        super.onCreate();

        // SharedPreferences.
        setupSharedPreferences(this);

        if (IS_DEBUG) Log.logDebug(TAG, "onCreate() : X");
    }

    @Override
    public void onTerminate() {
        if (IS_DEBUG) Log.logDebug(TAG, "onTerminate() : E");

        // Release.
        mGlobalSharedPreferences = null;

        super.onTerminate();
        if (IS_DEBUG) Log.logDebug(TAG, "onTerminate() : X");
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
     * @return SharedPreferences
     */
    public static SharedPreferences sp(Context context) {
        if (mGlobalSharedPreferences == null) {
            setupSharedPreferences(context);
        }
        return mGlobalSharedPreferences;
    }

}

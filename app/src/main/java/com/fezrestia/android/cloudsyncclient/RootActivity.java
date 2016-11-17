package com.fezrestia.android.cloudsyncclient;

import android.app.Activity;
import android.os.Bundle;

import com.fezrestia.android.util.log.Log;
import com.google.firebase.iid.FirebaseInstanceId;

public class RootActivity extends Activity {
    // Log tag.
    public static final String TAG = "RootActivity";
    // Log flag.
    public static final boolean IS_DEBUG = true || Log.IS_DEBUG;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (IS_DEBUG) Log.logDebug(TAG, "onCreate() : E");
        super.onCreate(savedInstanceState);
        if (IS_DEBUG) Log.logDebug(TAG, "onCreate() : X");
    }

    @Override
    protected void onResume() {
        if (IS_DEBUG) Log.logDebug(TAG, "onResume() : E");
        super.onResume();

        // Firebase cloud messaging token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        if (IS_DEBUG) Log.logDebug(TAG, "getToken() : Token=" + refreshedToken);

        if (IS_DEBUG) Log.logDebug(TAG, "onResume() : X");
    }

    @Override
    protected void onPause() {
        if (IS_DEBUG) Log.logDebug(TAG, "onPause() : E");
        super.onPause();
        if (IS_DEBUG) Log.logDebug(TAG, "onPause() : X");
    }

    @Override
    protected void onDestroy() {
        if (IS_DEBUG) Log.logDebug(TAG, "onDestroy() : E");
        super.onDestroy();
        if (IS_DEBUG) Log.logDebug(TAG, "onDestroy() : X");
    }
}

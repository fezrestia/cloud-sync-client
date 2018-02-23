package com.fezrestia.android.cloudsyncclient.firebase;

import com.fezrestia.android.util.log.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.HashMap;
import java.util.Map;

public class FcmMessagingService extends FirebaseMessagingService {
    // Log tag.
    public static final String TAG = "FcmMessagingService";
    // Log flag.
    @SuppressWarnings("PointlessBooleanExpression")
    public static final boolean IS_DEBUG = false || Log.IS_DEBUG;

    private static final Map<String, Callback> mCallbackMap = new HashMap<>();

    /**
     * FCM remote message handler callback.
     */
    public interface Callback {
        void handleMessage(RemoteMessage msg);
    }

    /**
     * Register callback.
     *
     * @param key Each app key
     * @param callback Callback instance.
     */
    public static void registerCallback(String key, Callback callback) {
        mCallbackMap.put(key, callback);
    }

    @Override
    public void onMessageReceived(RemoteMessage msg) {
        if (IS_DEBUG) Log.logDebug(TAG, "onMessageReceived() : E");

        // Check app mode.
        String appMode = msg.getData().get("app");

        if (appMode == null) {
            // Unexpected.
            if (IS_DEBUG) Log.logError(TAG, "Received Msg is NULL app mode. Unexpected.");
        } else {
            Callback callback = mCallbackMap.get(appMode);
            if (callback != null) {
                callback.handleMessage(msg);
            } else {
                // Unexpected.
                if (IS_DEBUG) Log.logError(TAG, "Received app mode is unexpected.");
            }
        }

        if (IS_DEBUG) Log.logDebug(TAG, "onMessageReceived() : X");
    }

}

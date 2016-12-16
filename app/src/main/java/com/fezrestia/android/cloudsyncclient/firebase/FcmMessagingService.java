package com.fezrestia.android.cloudsyncclient.firebase;

import com.fezrestia.android.util.log.Log;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class FcmMessagingService extends FirebaseMessagingService {
    // Log tag.
    public static final String TAG = "FcmMessagingService";
    // Log flag.
    public static final boolean IS_DEBUG = false || Log.IS_DEBUG;

    @Override
    public void onMessageReceived(RemoteMessage msg) {
        if (IS_DEBUG) Log.logDebug(TAG, "onMessageReceived() : E");

        if (IS_DEBUG) {
            Log.logDebug(TAG, "    Msg From = " + msg.getFrom());
            if (msg.getData().size() > 0) {
                Log.logDebug(TAG, "    Data = " + msg.getData());
            }
            if (msg.getNotification() != null) {
                Log.logDebug(TAG, "    Msg Title = " + msg.getNotification().getTitle());
                Log.logDebug(TAG, "    Msg Body = " + msg.getNotification().getBody());
            }
        }

        if (IS_DEBUG) Log.logDebug(TAG, "onMessageReceived() : X");
    }
}

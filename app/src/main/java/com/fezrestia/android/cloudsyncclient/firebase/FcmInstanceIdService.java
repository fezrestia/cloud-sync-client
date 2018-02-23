package com.fezrestia.android.cloudsyncclient.firebase;

import com.fezrestia.android.util.log.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

public class FcmInstanceIdService extends FirebaseInstanceIdService {
    // Log tag.
    public static final String TAG = "FcmInstanceIdService";
    // Log flag.
    @SuppressWarnings("PointlessBooleanExpression")
    public static final boolean IS_DEBUG = false || Log.IS_DEBUG;

    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        if (IS_DEBUG) Log.logDebug(TAG, "onTokenRefresh() : Token=" + refreshedToken);

        //TODO: Send token to server.
        Log.logError(TAG, "onTokenRefresh() : Token=" + refreshedToken);

    }

}

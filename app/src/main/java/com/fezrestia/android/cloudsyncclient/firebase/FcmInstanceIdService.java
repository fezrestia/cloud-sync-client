package com.fezrestia.android.cloudsyncclient.firebase;

import com.fezrestia.android.util.log.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

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

        Thread thread = new FcmTokenPostThread(refreshedToken);
        thread.start();
    }

    private static class FcmTokenPostThread extends Thread {
        private static final String TAG = "FcmTokenPostThread";
        private final String mFcmToken;

        FcmTokenPostThread(String fcmToken) {
            super("FcmTokenPostThread");
            mFcmToken = fcmToken;
        }

        @Override
        public void run() {
            if (IS_DEBUG) Log.logDebug(TAG, "run() : E");

            String payload = "fcm_token=" + mFcmToken;

            HttpURLConnection con = null;
            OutputStream os = null;
            try {
                URL url = new URL(FcmConstants.FCM_TOKEN_REGISTRATION_URL);
                con = (HttpURLConnection) url.openConnection();

                con.setRequestMethod("POST");
                con.setInstanceFollowRedirects(false);
                con.setDoOutput(true);
                con.setReadTimeout(10000);
                con.setConnectTimeout(30000);

                con.connect();

                os = con.getOutputStream();
                os.write(payload.getBytes("UTF-8"));
                os.flush();
                os.close();

                final int status = con.getResponseCode();
                if (status == HttpURLConnection.HTTP_OK) {
                    if (IS_DEBUG) Log.logDebug(TAG, "POST Success");
                } else {
                    if (IS_DEBUG) Log.logDebug(TAG, "POST Failed. status=" + status);
                }

                con.disconnect();
            } catch (UnsupportedEncodingException e) {
                Log.logError(TAG, "UnsupportedEncodingException");
            } catch (ProtocolException e) {
                Log.logError(TAG, "ProtocolException");
            } catch (MalformedURLException e) {
                Log.logError(TAG, "MalformedURLException");
            } catch (IOException e) {
                Log.logError(TAG, "IOException");
            } finally {
                if (os != null) {
                    try {
                        os.close();
                    } catch (IOException e) {
                        Log.logError(TAG, "finally os.close IOException");
                    }
                }
                if (con != null) {
                    con.disconnect();
                }
            }

           if (IS_DEBUG) Log.logDebug(TAG, "run() : X");
        }
    }
}

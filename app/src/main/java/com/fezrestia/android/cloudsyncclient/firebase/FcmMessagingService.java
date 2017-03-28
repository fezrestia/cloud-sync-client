package com.fezrestia.android.cloudsyncclient.firebase;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;

import com.fezrestia.android.cloudsyncclient.RootApplication;
import com.fezrestia.android.util.log.Log;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

public class FcmMessagingService extends FirebaseMessagingService {
    // Log tag.
    public static final String TAG = "FcmMessagingService";
    // Log flag.
    @SuppressWarnings("PointlessBooleanExpression")
    public static final boolean IS_DEBUG = false || Log.IS_DEBUG;

    // Limit month used.
    private static final int MONTH_USED_MB_LIMIT = 450;

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

        // Check month used limit.
        if (msg.getData().size() > 0) {
            Map data = msg.getData();
            int monthUsed = Integer.parseInt((String) data.get("month_used_current"));

            if (IS_DEBUG) Log.logDebug(TAG, "Month Used MB = " + monthUsed);

            if (MONTH_USED_MB_LIMIT < monthUsed) {
                if (IS_DEBUG) Log.logDebug(TAG, "Force turn on WiFI AP.");
                // Turn on WiFi AP.

                WifiManager wm = (WifiManager)
                        getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                ConnectivityManager cm = (ConnectivityManager)
                        getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

                // Currently, Wi-Fi is connected or not.
                NetworkInfo activeNetworkInfo = cm.getActiveNetworkInfo();
                boolean isWiFiConnected =
                        activeNetworkInfo != null
                        && activeNetworkInfo.isConnected()
                        && activeNetworkInfo.getType() == ConnectivityManager.TYPE_WIFI;

                if (isWiFiConnected) {
                    // NOP. Already in valid Wi-Fi environment.
                    if (IS_DEBUG) Log.logDebug(TAG, "In valid Wi-Fi Environment. NOP.");
                } else {
                    if (IS_DEBUG) Log.logDebug(TAG, "Valid Wi-Fi is unavailable. Wi-Fi AP ON.");

                    try {
                        Method method;
                        method = wm.getClass().getMethod(
                                "setWifiApEnabled",
                                WifiConfiguration.class,
                                boolean.class);
                        method.invoke(wm, null, true);
                    } catch (NoSuchMethodException e) {
                        e.printStackTrace();
                        RootApplication.sendNotification(
                                getApplicationContext(),
                                "WiFi AP ON ERROR",
                                "NoSuchMethodException");
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                        RootApplication.sendNotification(
                                getApplicationContext(),
                                "WiFi AP ON ERROR",
                                "InvocationTargetException");
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                        RootApplication.sendNotification(
                                getApplicationContext(),
                                "WiFi AP ON ERROR",
                                "IllegalAccessException");
                    }
                }
            }
        }

        if (IS_DEBUG) Log.logDebug(TAG, "onMessageReceived() : X");
    }
}

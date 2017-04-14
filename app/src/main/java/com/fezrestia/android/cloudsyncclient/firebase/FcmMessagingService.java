package com.fezrestia.android.cloudsyncclient.firebase;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import com.fezrestia.android.cloudsyncclient.R;
import com.fezrestia.android.cloudsyncclient.RootApplication;
import com.fezrestia.android.cloudsyncclient.zerosim.ZeroSimConstants;
import com.fezrestia.android.cloudsyncclient.zerosim.ZeroSimWidgetProvider;
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

    @Override
    public void onMessageReceived(RemoteMessage msg) {
        if (IS_DEBUG) Log.logDebug(TAG, "onMessageReceived() : E");

        // Check app mode.
        String appMode = msg.getData().get("app");

        if (appMode == null) {
            // Unexpected.
            if (IS_DEBUG) Log.logError(TAG, "Received Msg is NULL app mode. Unexpected.");
        } else switch (appMode) {
            case "zero-sim-stats":
                handleZeroSimStatsMsg(msg);
                break;

            default:
                // Unexpected.
                if (IS_DEBUG) Log.logError(TAG, "Received app mode is unexpected.");
                break;
        }


        if (IS_DEBUG) Log.logDebug(TAG, "onMessageReceived() : X");
    }

    //// ZERO SIM STATS ///////////////////////////////////////////////////////////////////////////

    // Limit month used.
    private static final int MONTH_USED_MB_LIMIT = 450;

    // Notification ID.
    private static final int ZERO_SIM_NOTIFICATION_ID = 1000;
    private static final int WIFI_AP_ON_NOTIFICATION_ID = 1001;

    private void handleZeroSimStatsMsg(RemoteMessage msg) {
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

            // Store.
            RootApplication.getGlobalSharedPreferences(getApplicationContext()).edit().putInt(
                    ZeroSimConstants.SP_KEY_CURRENT_MONTH_USED,
                    monthUsed)
                    .apply();

            // Update widget.
            ZeroSimWidgetProvider.updateWidget(getApplicationContext());

            // Notification.
            notifyZeroSimStats(monthUsed);

            // Enable Wi-Fi AP.
            if (MONTH_USED_MB_LIMIT < monthUsed) {
                if (IS_DEBUG) Log.logDebug(TAG, "Force turn on WiFI AP.");
                enableWiFiAp();
                notifyWiFiApOn();
            }
        }
    }

    // Add notification.
    private void notifyZeroSimStats(int curMonthUsedMb) {
        NotificationCompat.Builder builder
                = new NotificationCompat.Builder(getApplicationContext());
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setContentTitle("Zero SIM Stats");
        builder.setContentText("Month Used : " + curMonthUsedMb + " MB");

        NotificationManagerCompat manager
                = NotificationManagerCompat.from(getApplicationContext());
        manager.notify(ZERO_SIM_NOTIFICATION_ID, builder.build());
    }

    // Notify Wi-Fi AP ON.
    private void notifyWiFiApOn() {
        NotificationCompat.Builder builder
                = new NotificationCompat.Builder(getApplicationContext());
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setContentTitle("Zero SIM Stats");
        builder.setContentText("Wi-Fi AP Enabled");

        NotificationManagerCompat manager
                = NotificationManagerCompat.from(getApplicationContext());
        manager.notify(WIFI_AP_ON_NOTIFICATION_ID, builder.build());
    }

    // Turn on Wi-Fi AP.
    private void enableWiFiAp() {
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

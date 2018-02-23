package com.fezrestia.android.cloudsyncclient.zerosim;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import com.fezrestia.android.cloudsyncclient.R;
import com.fezrestia.android.cloudsyncclient.RootApplication;
import com.fezrestia.android.cloudsyncclient.firebase.FcmMessagingService;
import com.fezrestia.android.util.log.Log;
import com.google.firebase.messaging.RemoteMessage;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

public class ZeroSimFcmCallback implements FcmMessagingService.Callback {
    // Log tag.
    private static final String TAG = "ZeroSimFcmCallback";

    // Log flag.
    @SuppressWarnings("PointlessBooleanExpression")
    public static final boolean IS_DEBUG = false || Log.IS_DEBUG;

    // Notification ID.
    private static final int ZERO_SIM_NOTIFICATION_ID = 1000;
    private static final int WIFI_AP_ON_NOTIFICATION_ID = 1001;

    @Override
    public void handleMessage(RemoteMessage msg) {
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
        int zerosimUsed = storeRemoteToLocal(
                msg,
                "zerosim_month_used_current_mb",
                ZeroSimConstants.SP_KEY_CURRENT_MONTH_USED_ZEROSIM);
        storeRemoteToLocal(
                msg,
                "nuro_month_used_current_mb",
                ZeroSimConstants.SP_KEY_CURRENT_MONTH_USED_NURO);
        storeRemoteToLocal(
                msg,
                "docomo_month_used_current_mb",
                ZeroSimConstants.SP_KEY_CURRENT_MONTH_USED_DOCOMO);

        // Update widget.
        ZeroSimWidgetProvider.updateWidget(RootApplication.getContext());

        // Check limit over.
        if (ZeroSimConstants.MONTH_USED_MB_WARNING_ZEROSIM <= zerosimUsed) {
            notifyZeroSimStats(zerosimUsed);

            if (IS_DEBUG) Log.logDebug(TAG, "Force turn on WiFI AP.");
            enableWiFiAp();
            notifyWiFiApOn();
        }
    }

    private int storeRemoteToLocal(RemoteMessage remoteMsg, String remoteKey, String localKey) {
        Map<String, String> data = remoteMsg.getData();

        String remoteVal = data.get(remoteKey);
        int localVal;
        if (remoteVal == null) {
            localVal = ZeroSimConstants.INVALID_USED_AMOUNT;
        } else {
            localVal = Integer.parseInt(remoteVal);
        }
        // Store.
        RootApplication.getGlobalSharedPreferences(RootApplication.getContext()).edit().putInt(
                localKey,
                localVal)
                .apply();

        if (IS_DEBUG) Log.logDebug(TAG, "key=" + localKey + " = " + localVal);
        return localVal;
    }

    // Add notification.
    private void notifyZeroSimStats(int curMonthUsedMb) {
        NotificationCompat.Builder builder
                = new NotificationCompat.Builder(RootApplication.getContext());
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setContentTitle("Zero SIM Stats");
        builder.setContentText("Month Used : " + curMonthUsedMb + " MB");

        NotificationManagerCompat manager
                = NotificationManagerCompat.from(RootApplication.getContext());
        manager.notify(ZERO_SIM_NOTIFICATION_ID, builder.build());
    }

    // Notify Wi-Fi AP ON.
    private void notifyWiFiApOn() {
        NotificationCompat.Builder builder
                = new NotificationCompat.Builder(RootApplication.getContext());
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setContentTitle("Zero SIM Stats");
        builder.setContentText("Wi-Fi AP Enabled");

        NotificationManagerCompat manager
                = NotificationManagerCompat.from(RootApplication.getContext());
        manager.notify(WIFI_AP_ON_NOTIFICATION_ID, builder.build());
    }

    // Turn on Wi-Fi AP.
    private void enableWiFiAp() {
        WifiManager wm = (WifiManager)
                RootApplication.getContext().getSystemService(Context.WIFI_SERVICE);
        ConnectivityManager cm = (ConnectivityManager)
                RootApplication.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        if (wm == null || cm == null) {
            throw new RuntimeException("WifiManager or ConnectivityManager is null.");
        }

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
                        RootApplication.getContext(),
                        "WiFi AP ON ERROR",
                        "NoSuchMethodException");
            } catch (InvocationTargetException e) {
                e.printStackTrace();
                RootApplication.sendNotification(
                        RootApplication.getContext(),
                        "WiFi AP ON ERROR",
                        "InvocationTargetException");
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                RootApplication.sendNotification(
                        RootApplication.getContext(),
                        "WiFi AP ON ERROR",
                        "IllegalAccessException");
            }
        }
    }



}

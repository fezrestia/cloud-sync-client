package com.fezrestia.android.cloudsyncclient.simstats;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.fezrestia.android.cloudsyncclient.R;
import com.fezrestia.android.cloudsyncclient.App;
import com.fezrestia.android.util.log.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 * WidgetProvider for SIM stats widget.
 */
public class SimStatsWidgetProvider extends AppWidgetProvider {
    // Log tag.
    public static final String TAG = "SimStatsWidgetProvider";
    // Log flag.
    @SuppressWarnings("PointlessBooleanExpression")
    public static final boolean IS_DEBUG = false || Log.IS_DEBUG;

    @Override
    public void onUpdate(Context context, AppWidgetManager awm, int[] appWidgetIds) {
        if (IS_DEBUG) Log.logDebug(TAG, "onUpdate() : E");

        Thread updater = new UpdateLatestSimStatsThread(context);
        updater.start();

        if (IS_DEBUG) Log.logDebug(TAG, "onUpdate() : X");
    }

    public static void requestUpdate(Context context) {
        Thread updater = new UpdateLatestSimStatsThread(context);
        updater.start();
    }

    private static class UpdateLatestSimStatsThread extends Thread {
        private final Context context;

        /**
         * CONSTRUCTOR.
         */
        UpdateLatestSimStatsThread(Context context) {
            this.context = context;
        }

        @Override
        public void run() {
            HttpURLConnection httpConn = null;
            BufferedReader br = null;
            InputStream is = null;
            InputStreamReader isr = null;

            final StringBuilder sb = new StringBuilder();
            boolean isOk = true;

            try {
                URL url = new URL(SimStatsConstants.REST_GET_LATEST_SIM_STATS);
                httpConn = (HttpURLConnection) url.openConnection();
                httpConn.setConnectTimeout(5000);
                httpConn.setReadTimeout(5000);
                httpConn.setRequestMethod("GET");
                httpConn.setUseCaches(false);
                httpConn.setDoOutput(false);
                httpConn.setDoInput(true);

                httpConn.connect();

                final int responseCode = httpConn.getResponseCode();

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    // OK.

                    is = httpConn.getInputStream();
                    isr = new InputStreamReader(is, StandardCharsets.UTF_8);
                    br = new BufferedReader(isr);

                    String line;
                    while ((line = br.readLine()) != null) {
                        sb.append(line);
                    }

                } else {
                    // NG.
                    Log.logError(TAG, "Failed to GET latest SIM stats.");
                    isOk = false;
                }
            } catch (IOException e) {
                e.printStackTrace();
                Log.logError(TAG, "Failed to GET latest SIM stats.");
                isOk = false;
            } finally {
                if (br != null) {
                    try {
                        br.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.logError(TAG, "Failed to close br.");
                    }
                }
                if (isr != null) {
                    try {
                        isr.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.logError(TAG, "Failed to close isr.");
                    }
                }
                if (is != null) {
                    try {
                        is.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.logError(TAG, "Failed to close is.");
                    }
                }
                if (httpConn != null) {
                    httpConn.disconnect();
                }
            }

            if (isOk) {
                final String res = sb.toString();

                try {
                    JSONObject latestStats = new JSONObject(res);
                    final int dcmMonthUsed = latestStats.getInt("month_used_dcm");
                    final int nuroMonthUsed = latestStats.getInt("month_used_nuro");

                    App.ui.post(new UpdateWidgetTask(
                            context,
                            dcmMonthUsed,
                            nuroMonthUsed));

                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.logError(TAG, "Failed to parse JSON.");
                }
            }
        }
    }

    private static class UpdateWidgetTask implements Runnable {
        private final Context context;

        private final int dcmMonthUsed;
        private final int nuroMonthUsed;

        /**
         * CONSTRUCTOR.
         */
        UpdateWidgetTask(
                Context context,
                int dcmMonthUsed,
                int nuroMonthUsed) {
            this.context = context;
            this.dcmMonthUsed = dcmMonthUsed;
            this.nuroMonthUsed = nuroMonthUsed;
        }

        @Override
        public void run() {
            // Initialize layout.
            RemoteViews remoteViews = new RemoteViews(
                    context.getPackageName(),
                    R.layout.simstats_widget_layout);

            String dcmText = getDataString(dcmMonthUsed);
            String nuroText = getDataString(nuroMonthUsed);

            remoteViews.setTextViewText(R.id.simstats_widget_dcm_used, dcmText);
            remoteViews.setTextViewText(R.id.simstats_widget_nuro_used, nuroText);

            // Get last updated timestamp.
            long lastUpdatedMillis = App.sp(context).getLong(
                    SimStatsConstants.SP_KEY_SIM_STATS_LAST_UPDATED_TIMESTAMP,
                    0);
            long now = System.currentTimeMillis();
            long threshold = SimStatsConstants.TIMESTAMP_DIFF_THRESHOLD_MILLIS;
            boolean isDataOldError = (now - lastUpdatedMillis) > threshold;

            // Error indicator.
            if (isDataOldError) {
                int errorColor = 0x00FF0000;
                remoteViews.setTextColor(R.id.simstats_widget_dcm_used, errorColor);
                remoteViews.setTextColor(R.id.simstats_widget_nuro_used, errorColor);
            }

            // Click event.
            remoteViews.setOnClickPendingIntent(
                    R.id.simstats_widget_root,
                    getWidgetClickCallback(context));

            // Update.
            ComponentName widget = new ComponentName(context, SimStatsWidgetProvider.class);
            AppWidgetManager.getInstance(context).updateAppWidget(widget, remoteViews);

            // Update timestamp.
            App.sp(context).edit().putLong(
                    SimStatsConstants.SP_KEY_SIM_STATS_LAST_UPDATED_TIMESTAMP,
                    now)
                    .apply();
        }
    }

    private static String getDataString(float curUsed) {
        int used = (int) curUsed;
        return used + " MB  ";
    }

    public static PendingIntent getWidgetClickCallback(Context context) {
        Intent intent = new Intent();
        intent.setAction(SimStatsConstants.WIDGET_CLICK_CALLBACK_INTENT);
        intent.setPackage(context.getPackageName());
        return PendingIntent.getBroadcast(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
    }
}

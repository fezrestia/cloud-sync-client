package com.fezrestia.android.cloudsyncclient.simstats;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.fezrestia.android.cloudsyncclient.R;
import com.fezrestia.android.cloudsyncclient.RootApplication;
import com.fezrestia.android.util.log.Log;

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

        updateWidget(context);

        if (IS_DEBUG) Log.logDebug(TAG, "onUpdate() : X");
    }

    /**
     * Update widget UI.
     *
     * @param context Master context.
     */
    public static void updateWidget(Context context) {
        if (IS_DEBUG) Log.logDebug(TAG, "updateWidget() : E");

        // Initialize layout.
        RemoteViews remoteViews = new RemoteViews(
                context.getPackageName(),
                R.layout.simstats_widget_layout);

        // Get current value.
        float curUsedZeroSim = RootApplication.getGlobalSharedPreferences(context).getFloat(
                SimStatsConstants.SP_KEY_CURRENT_MONTH_USED_ZEROSIM,
                SimStatsConstants.INVALID_USED_AMOUNT); // Default value.
        float curUsedNuro = RootApplication.getGlobalSharedPreferences(context).getFloat(
                SimStatsConstants.SP_KEY_CURRENT_MONTH_USED_NURO,
                SimStatsConstants.INVALID_USED_AMOUNT); // Default value.
        float curUsedDcm = RootApplication.getGlobalSharedPreferences(context).getFloat(
                SimStatsConstants.SP_KEY_CURRENT_MONTH_USED_DCM,
                SimStatsConstants.INVALID_USED_AMOUNT); // Default value.

        // Get last updated timestamp.
        long lastZeroSimUpdatedMillis = RootApplication.getGlobalSharedPreferences(context).getLong(
                SimStatsConstants.SP_KEY_SIM_STATS_LAST_UPDATED_TIMESTAMP_ZEROSIM,
                0); // Default
        long lastNuroUpdatedMillis = RootApplication.getGlobalSharedPreferences(context).getLong(
                SimStatsConstants.SP_KEY_SIM_STATS_LAST_UPDATED_TIMESTAMP_NURO,
                0); // Default
        long lastDcmUpdatedMillis = RootApplication.getGlobalSharedPreferences(context).getLong(
                SimStatsConstants.SP_KEY_SIM_STATS_LAST_UPDATED_TIMESTAMP_DCM,
                0); // Default

        long now = System.currentTimeMillis();
        long threshold = SimStatsConstants.TIMESTAMP_DIFF_THRESHOLD_MILLIS;
        boolean isZeroSimUpdateError = (now - lastZeroSimUpdatedMillis) > threshold;
        boolean isNuroUpdateError = (now - lastNuroUpdatedMillis) > threshold;
        boolean isDcmUpdateError = (now - lastDcmUpdatedMillis) > threshold;

        // Text.
        String zero_sim_text = getDataString(curUsedZeroSim);
        String nuro_text = getDataString(curUsedNuro);
        String dcm_text = getDataString(curUsedDcm);
        remoteViews.setTextViewText(R.id.simstats_widget_zerosim_used, zero_sim_text);
        remoteViews.setTextViewText(R.id.simstats_widget_nuro_used, nuro_text);
        remoteViews.setTextViewText(R.id.simstats_widget_dcm_used, dcm_text);

        int errorColor = 0x00FF0000;
        if (isZeroSimUpdateError) {
            remoteViews.setTextColor(R.id.simstats_widget_zerosim_used, errorColor);
        }
        if (isNuroUpdateError) {
            remoteViews.setTextColor(R.id.simstats_widget_nuro_used, errorColor);
        }
        if (isDcmUpdateError) {
            remoteViews.setTextColor(R.id.simstats_widget_dcm_used, errorColor);
        }

        // Click event.
        remoteViews.setOnClickPendingIntent(
                R.id.simstats_widget_root,
                getWidgetClickCallback(context));

        // Update.
        ComponentName widget = new ComponentName(context, SimStatsWidgetProvider.class);
        AppWidgetManager.getInstance(context).updateAppWidget(widget, remoteViews);

        if (IS_DEBUG) Log.logDebug(TAG, "updateWidget() : X");
    }

    private static String getDataString(float curUsed) {
        String text = "";
        if (curUsed != SimStatsConstants.INVALID_USED_AMOUNT) {
            int used = (int) curUsed;
            text = text + used + " MB  ";
        } else {
            text = "NO DATA";
        }
        return text;
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

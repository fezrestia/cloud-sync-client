package com.fezrestia.android.cloudsyncclient.zerosim;

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
 * WidgetProvider for Zero SIM Widget.
 */
public class ZeroSimWidgetProvider extends AppWidgetProvider {
    // Log tag.
    public static final String TAG = "ZeroSimWidgetProvider";
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
     * @param context
     */
    public static void updateWidget(Context context) {
        if (IS_DEBUG) Log.logDebug(TAG, "updateWidget() : E");

        // Initialize layout.
        RemoteViews remoteViews = new RemoteViews(
                context.getPackageName(),
                R.layout.zerosim_widget_layout);

        // Get current value.
        int curUsed = RootApplication.getGlobalSharedPreferences(context).getInt(
                ZeroSimConstants.SP_KEY_CURRENT_MONTH_USED,
                -1); // Default value.

        // Text.
        String text = "";
        if (curUsed != -1) {
            text = text + curUsed + "MB";
        } else {
            text = "NO DATA";
        }
        remoteViews.setTextViewText(R.id.zerosim_widget_month_used, text);

        // Click event.
        remoteViews.setOnClickPendingIntent(
                R.id.zerosim_widget_root,
                getWidgetClickCallback(context));

        // Update.
        ComponentName widget = new ComponentName(context, ZeroSimWidgetProvider.class);
        AppWidgetManager.getInstance(context).updateAppWidget(widget, remoteViews);

        if (IS_DEBUG) Log.logDebug(TAG, "updateWidget() : X");
    }

    public static PendingIntent getWidgetClickCallback(Context context) {
        Intent intent = new Intent();
        intent.setAction(ZeroSimConstants.WIDGET_CLICK_CALLBACK_INTENT);
        intent.setPackage(context.getPackageName());
        return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }
}

package com.fezrestia.android.cloudsyncclient.simstats;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.fezrestia.android.util.log.Log;

/**
 * WidgetProvider for SIM stats widget.
 */
public class SimStatsWidgetReceiver extends BroadcastReceiver {
    // Log tag.
    public static final String TAG = "SimStatsWidgetReceiver";
    // Log flag.
    @SuppressWarnings("PointlessBooleanExpression")
    public static final boolean IS_DEBUG = false || Log.IS_DEBUG;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (IS_DEBUG) Log.logDebug(TAG, "onReceive()");

        String action = intent.getAction();
        if (IS_DEBUG) Log.logDebug(TAG, "    ACTION = " + action);
        if (action == null) {
            return;
        }

        switch (action) {
            case SimStatsConstants.WIDGET_CLICK_CALLBACK_INTENT:
                // Update widget.
                SimStatsWidgetProvider.updateWidget(context.getApplicationContext());
                break;

            default:
                if (IS_DEBUG) Log.logDebug(TAG, "Unexpected Intent Action.");
                break;
        }
    }
}

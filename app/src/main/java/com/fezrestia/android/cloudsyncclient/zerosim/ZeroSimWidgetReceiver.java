package com.fezrestia.android.cloudsyncclient.zerosim;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.fezrestia.android.cloudsyncclient.RootApplication;
import com.fezrestia.android.util.log.Log;

/**
 * WidgetProvider for Zero SIM Widget.
 */
public class ZeroSimWidgetReceiver extends BroadcastReceiver {
    // Log tag.
    public static final String TAG = "ZeroSimWidgetReceiver";
    // Log flag.
    @SuppressWarnings("PointlessBooleanExpression")
    public static final boolean IS_DEBUG = false || Log.IS_DEBUG;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (IS_DEBUG) Log.logDebug(TAG, "onReceive()");

        String action = intent.getAction();
        if (IS_DEBUG) Log.logDebug(TAG, "    ACTION = " + action);

        switch (action) {
            case ZeroSimConstants.WIDGET_CLICK_CALLBACK_INTENT:
                // Update widget.
                ZeroSimWidgetProvider.updateWidget(context.getApplicationContext());
                break;

            default:
                if (IS_DEBUG) Log.logDebug(TAG, "Unexpected Intent Action.");
                break;
        }
    }
}

package com.fezrestia.android.cloudsyncclient;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.fezrestia.android.cloudsyncclient.simstats.SimStatsConstants;
import com.fezrestia.android.cloudsyncclient.simstats.SimStatsSettingActivity;
import com.fezrestia.android.util.log.Log;
import com.google.firebase.iid.FirebaseInstanceId;

public class RootActivity extends Activity {
    // Log tag.
    public static final String TAG = "RootActivity";
    // Log flag.
    @SuppressWarnings("PointlessBooleanExpression")
    public static final boolean IS_DEBUG = false || Log.IS_DEBUG;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (IS_DEBUG) Log.logDebug(TAG, "onCreate() : E");
        super.onCreate(savedInstanceState);

        setContentView(R.layout.root_layout);

        Button simstats = findViewById(R.id.start_simstats_activity);
        simstats.setOnClickListener(new SimStatsButtonOnClickListener());

        Button syncSimStats = findViewById(R.id.sync_request_on_server);
        syncSimStats.setOnClickListener(new SyncSimStatsButtonOnClickListener());

        Button updateSimStats = findViewById(R.id.update_sim_load_stats);
        updateSimStats.setOnClickListener(new UpdateSimStatsButtonOnClickListener());

        if (IS_DEBUG) Log.logDebug(TAG, "onCreate() : X");
    }

    @Override
    protected void onResume() {
        if (IS_DEBUG) Log.logDebug(TAG, "onResume() : E");
        super.onResume();

        // Firebase cloud messaging token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        if (IS_DEBUG) Log.logDebug(TAG, "getToken() : Token=" + refreshedToken);

        if (IS_DEBUG) Log.logDebug(TAG, "onResume() : X");
    }

    @Override
    protected void onPause() {
        if (IS_DEBUG) Log.logDebug(TAG, "onPause() : E");
        super.onPause();
        if (IS_DEBUG) Log.logDebug(TAG, "onPause() : X");
    }

    @Override
    protected void onDestroy() {
        if (IS_DEBUG) Log.logDebug(TAG, "onDestroy() : E");
        super.onDestroy();
        if (IS_DEBUG) Log.logDebug(TAG, "onDestroy() : X");
    }

    private class SimStatsButtonOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent();
            intent.setClassName(
                    RootActivity.this.getApplicationContext().getPackageName(),
                    SimStatsSettingActivity.class.getName());
            startActivity(intent);
        }
    }

    private class SyncSimStatsButtonOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Intent browser = new Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse(SimStatsConstants.SIM_STATS_SYNC_GET_URL_ZEROSIM));
            startActivity(browser);
        }
    }

    private class UpdateSimStatsButtonOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Intent browser = new Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse(SimStatsConstants.SIM_STATS_NOTIFY_GET_URL_ZEROSIM));
            startActivity(browser);
        }
    }
}

package com.fezrestia.android.cloudsyncclient;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.fezrestia.android.cloudsyncclient.zerosim.ZeroSimConstants;
import com.fezrestia.android.cloudsyncclient.zerosim.ZeroSimSettingActivity;
import com.fezrestia.android.util.log.Log;
import com.google.firebase.iid.FirebaseInstanceId;

public class RootActivity extends Activity {
    // Log tag.
    public static final String TAG = "RootActivity";
    // Log flag.
    public static final boolean IS_DEBUG = false || Log.IS_DEBUG;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (IS_DEBUG) Log.logDebug(TAG, "onCreate() : E");
        super.onCreate(savedInstanceState);

        setContentView(R.layout.root_layout);

        Button zerosim = (Button) findViewById(R.id.start_zerosim_activity);
        zerosim.setOnClickListener(new ZeroSimButtonOnClickListener());

        Button updateSimStats = (Button) findViewById(R.id.update_sim_load_stats);
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

    private class ZeroSimButtonOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent();
            intent.setClassName(
                    RootActivity.this.getApplicationContext().getPackageName(),
                    ZeroSimSettingActivity.class.getName());
            startActivity(intent);
        }
    }

    private class UpdateSimStatsButtonOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Intent browser = new Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse(ZeroSimConstants.SIM_STATS_NOTIFY_GET_URL));
            startActivity(browser);
        }
    }
}

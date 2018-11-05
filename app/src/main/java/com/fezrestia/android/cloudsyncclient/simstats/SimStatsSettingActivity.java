package com.fezrestia.android.cloudsyncclient.simstats;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;

import com.fezrestia.android.cloudsyncclient.R;
import com.fezrestia.android.util.log.Log;

public class SimStatsSettingActivity extends PreferenceActivity {
    // Log tag.
    public static final String TAG = "SimStatsSettingActivity";
    // Log flag.
    @SuppressWarnings("PointlessBooleanExpression")
    public static final boolean IS_DEBUG = false || Log.IS_DEBUG;

    public static final int JOB_ID_NOTIFY = 100;
    public static final int JOB_ID_SYNC = 200;

    // Preference fragment.
    public static class SimStatsPreferenceFragment extends PreferenceFragment {
        public static final String TAG = "SimStatsPreferenceFragment";

        @Override
        public void onCreate(Bundle savedInstanceState) {
            if (IS_DEBUG) Log.logDebug(TAG, "onCreate()");
            super.onCreate(savedInstanceState);

            addPreferencesFromResource(R.xml.preferences_simstats);
            setCallbacks();
        }

        private void setCallbacks() {
            // DCM.
            findPreference("request_dcm_sync").setOnPreferenceClickListener(preference -> {
                requestRest(SimStatsConstants.SIM_STATS_SYNC_GET_URL_DCM);
                return false;
            });
            findPreference("request_dcm_notify").setOnPreferenceClickListener(preference -> {
                requestRest(SimStatsConstants.SIM_STATS_NOTIFY_GET_URL_DCM);
                return false;
            });

            // NURO.
            findPreference("request_nuro_sync").setOnPreferenceClickListener(preference -> {
                requestRest(SimStatsConstants.SIM_STATS_SYNC_GET_URL_NURO);
                return false;
            });
            findPreference("request_nuro_notify").setOnPreferenceClickListener(preference -> {
                requestRest(SimStatsConstants.SIM_STATS_NOTIFY_GET_URL_NURO);
                return false;
            });

            // ZEROSIM.
            findPreference("request_zerosim_sync").setOnPreferenceClickListener(preference -> {
                requestRest(SimStatsConstants.SIM_STATS_SYNC_GET_URL_ZEROSIM);
                return false;
            });
            findPreference("request_zerosim_notify").setOnPreferenceClickListener(preference -> {
                requestRest(SimStatsConstants.SIM_STATS_NOTIFY_GET_URL_ZEROSIM);
                return false;
            });
        }

        private void requestRest(String uri) {
            Intent browser = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
            startActivity(browser);
        }
    }

    @Override
    public void onCreate(Bundle bundle) {
        if (IS_DEBUG) Log.logDebug(TAG, "onCreate()");
        super.onCreate(bundle);

        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SimStatsPreferenceFragment())
                .commit();
    }
}

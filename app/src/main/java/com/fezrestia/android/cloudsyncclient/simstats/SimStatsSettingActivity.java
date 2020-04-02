package com.fezrestia.android.cloudsyncclient.simstats;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceFragmentCompat;

import com.fezrestia.android.cloudsyncclient.R;
import com.fezrestia.android.util.log.Log;

public class SimStatsSettingActivity extends AppCompatActivity {
    // Log tag.
    public static final String TAG = "SimStatsSettingActivity";
    // Log flag.
    @SuppressWarnings("PointlessBooleanExpression")
    public static final boolean IS_DEBUG = false || Log.IS_DEBUG;

    // Preference fragment.
    public static class     SimStatsPreferenceFragment extends PreferenceFragmentCompat {
        static final String TAG = "SimStatsPreferenceFragment";

        @Override
        public void onCreate(Bundle savedInstanceState) {
            if (IS_DEBUG) Log.logDebug(TAG, "onCreate()");
            super.onCreate(savedInstanceState);

            addPreferencesFromResource(R.xml.preferences_simstats);
            setCallbacks();
        }

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {

        }

        @SuppressWarnings("ConstantConditions")
        private void setCallbacks() {
            // DCM.
            findPreference("request_dcm_sync").setOnPreferenceClickListener(preference -> {
                requestRest(SimStatsConstants.REST_GET_UPDATE_DCM_STATS);
                return false;
            });

            // NURO.
            findPreference("request_nuro_sync").setOnPreferenceClickListener(preference -> {
                requestRest(SimStatsConstants.REST_GET_UPDATE_NURO_STATS);
                return false;
            });

            // ZEROSIM.
            findPreference("request_zerosim_sync").setOnPreferenceClickListener(preference -> {
                requestRest(SimStatsConstants.REST_GET_UPDATE_ZERO_SIM_STATS);
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

        getSupportFragmentManager()
                .beginTransaction()
                .replace(android.R.id.content, new SimStatsPreferenceFragment())
                .commit();
    }
}

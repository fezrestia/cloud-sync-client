package com.fezrestia.android.cloudsyncclient.zerosim;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;

import com.fezrestia.android.cloudsyncclient.R;
import com.fezrestia.android.util.log.Log;

public class ZeroSimSettingActivity extends PreferenceActivity {
    // Log tag.
    public static final String TAG = "ZeroSimSettingActivity";
    // Log flag.
    public static final boolean IS_DEBUG = false || Log.IS_DEBUG;

    @Override
    public void onCreate(Bundle bundle) {
        if (IS_DEBUG) Log.logDebug(TAG, "onCreate() : E");
        super.onCreate(bundle);

        // Add preferences.
        addPreferencesFromResource(R.xml.preferences_zerosim);

        if (IS_DEBUG) Log.logDebug(TAG, "onCreate() : X");
    }

    @Override
    public void onResume() {
        if (IS_DEBUG) Log.logDebug(TAG, "onResume() : E");
        super.onResume();

        findPreference("is_zerosim_cron_enabled").setOnPreferenceChangeListener(
                new ZeroSimEnabledChangeListener());

        if (IS_DEBUG) Log.logDebug(TAG, "onResume() : X");
    }

    @Override
    public void onPause() {
        if (IS_DEBUG) Log.logDebug(TAG, "onPause() : E");

        // NOP.

        super.onPause();
        if (IS_DEBUG) Log.logDebug(TAG, "onPause() : X");
    }

    @Override
    public void onDestroy() {
        if (IS_DEBUG) Log.logDebug(TAG, "onDestroy() : E");

        // NOP.

        super.onPause();
        if (IS_DEBUG) Log.logDebug(TAG, "onDestroy() : X");
    }



    private class ZeroSimEnabledChangeListener implements Preference.OnPreferenceChangeListener {
        @Override
        public boolean onPreferenceChange(Preference preferene, Object value) {
            if (IS_DEBUG) Log.logDebug(TAG, "ZeroSimEnabledChangeListener.onPreferenceChange()");

            Boolean enabled = (Boolean) value;

            if (IS_DEBUG) Log.logDebug(TAG, "    Change to : " + enabled.booleanValue());


            //TODO: implements register/unregister job scheduler.


            return true;
        }
    }

}

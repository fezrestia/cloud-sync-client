package com.fezrestia.android.cloudsyncclient.simstats;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
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
            // En/Disable.
            findPreference("is_simstats_cron_enabled").setOnPreferenceChangeListener(
                    new SimStatsEnabledChangeListener());

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

        private class SimStatsEnabledChangeListener
                implements Preference.OnPreferenceChangeListener {
            @Override
            public boolean onPreferenceChange(Preference preference, Object value) {
                if (IS_DEBUG) Log.logDebug(TAG, "onPreferenceChange()");

                Boolean enabled = (Boolean) value;
                if (IS_DEBUG) Log.logDebug(TAG, "    Change to : " + enabled);

                // Job scheduler service.
                JobScheduler scheduler = (JobScheduler)
                        getContext().getSystemService(Context.JOB_SCHEDULER_SERVICE);
                if (scheduler == null) throw new RuntimeException("scheduler is null.");

                if (enabled) {
                    // Enabled.

                    ComponentName serviceName = new ComponentName(
                            getContext().getPackageName(),
                            SimStatsJobSchedulerService.class.getName());

                    JobInfo notifyJobInfo = getPeriodicJobInfo(
                            JOB_ID_NOTIFY,
                            serviceName,
                            2 * 60 * 60 * 1000); // 2 hours

                    JobInfo syncJobInfo = getPeriodicJobInfo(
                            JOB_ID_SYNC,
                            serviceName,
                            6 * 60 * 60 * 1000); // 6 hours

                    scheduler.schedule(notifyJobInfo);
                    scheduler.schedule(syncJobInfo);

                } else {
                    // Disabled.

                    scheduler.cancel(JOB_ID_NOTIFY);
                    scheduler.cancel(JOB_ID_SYNC);
                }

                return true;
            }
        }

        /**
         * Get periodic JobInfo.
         *
         * @param jobId Job ID
         * @param service Job scheduler service.
         * @param intervalMillis Interval time.
         * @return JobInfo.
         */
        private JobInfo getPeriodicJobInfo(int jobId, ComponentName service,  int intervalMillis) {
            return new JobInfo.Builder(jobId, service)
                    .setBackoffCriteria(60 * 1000, JobInfo.BACKOFF_POLICY_LINEAR) // 60 sec
                    .setPeriodic(intervalMillis)
                    .setPersisted(true) // Auto register after reboot.
                    .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY) // All network.
                    .setRequiresCharging(false) // Run always.
                    .setRequiresDeviceIdle(false) // Run always.
                    .build();
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

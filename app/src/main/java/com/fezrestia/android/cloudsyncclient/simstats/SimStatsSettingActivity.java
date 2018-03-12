package com.fezrestia.android.cloudsyncclient.simstats;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;

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

    @Override
    public void onCreate(Bundle bundle) {
        if (IS_DEBUG) Log.logDebug(TAG, "onCreate() : E");
        super.onCreate(bundle);

        // Add preferences.
        //noinspection deprecation
        addPreferencesFromResource(R.xml.preferences_simstats);

        if (IS_DEBUG) Log.logDebug(TAG, "onCreate() : X");
    }

    @Override
    public void onResume() {
        if (IS_DEBUG) Log.logDebug(TAG, "onResume() : E");
        super.onResume();

        //noinspection deprecation
        findPreference("is_simstats_cron_enabled").setOnPreferenceChangeListener(
                new SimStatsEnabledChangeListener());

// DEBUG
//        JobScheduler scheduler = (JobScheduler)
//                getSystemService(Context.JOB_SCHEDULER_SERVICE);
//        List<JobInfo> infoList = scheduler.getAllPendingJobs();
//        for (int i = 0; i < infoList.size(); ++i) {
//            android.util.Log.e("TraceLog", "###### JobInfo = " + infoList.get(i).toString());
//        }

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

        super.onDestroy();
        if (IS_DEBUG) Log.logDebug(TAG, "onDestroy() : X");
    }



    private class SimStatsEnabledChangeListener implements Preference.OnPreferenceChangeListener {
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            if (IS_DEBUG) Log.logDebug(TAG, "onPreferenceChange()");

            Boolean enabled = (Boolean) value;
            if (IS_DEBUG) Log.logDebug(TAG, "    Change to : " + enabled);

            // Job scheduler service.
            JobScheduler scheduler = (JobScheduler)
                    getSystemService(Context.JOB_SCHEDULER_SERVICE);
            if (scheduler == null) throw new RuntimeException("scheduler is null.");

            if (enabled) {
                // Enabled.

                ComponentName serviceName = new ComponentName(
                        SimStatsSettingActivity.this.getApplicationContext().getPackageName(),
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

package com.fezrestia.android.cloudsyncclient.zerosim;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
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

    public static final int JOB_ID_NOTIFY = 100;
    public static final int JOB_ID_SYNC = 200;

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



    private class ZeroSimEnabledChangeListener implements Preference.OnPreferenceChangeListener {
        @Override
        public boolean onPreferenceChange(Preference preferene, Object value) {
            if (IS_DEBUG) Log.logDebug(TAG, "ZeroSimEnabledChangeListener.onPreferenceChange()");

            Boolean enabled = (Boolean) value;
            if (IS_DEBUG) Log.logDebug(TAG, "    Change to : " + enabled.booleanValue());

            // Job sheduler service.
            JobScheduler scheduler = (JobScheduler)
                    getSystemService(Context.JOB_SCHEDULER_SERVICE);

            if (enabled.booleanValue()) {
                // Enabled.

                ComponentName serviceName = new ComponentName(
                        ZeroSimSettingActivity.this.getApplicationContext().getPackageName(),
                        ZeroSimJobSchedulerService.class.getName());

                JobInfo notifyJobInfo = getPeriodicJobInfo(
                        JOB_ID_NOTIFY,
                        serviceName,
                        1 * 60 * 60 * 1000); // 1 hours

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
     * @param jobId
     * @param service
     * @param intervalMillis
     * @return
     */
    private JobInfo getPeriodicJobInfo(int jobId, ComponentName service,  int intervalMillis) {
        JobInfo jobInfo = new JobInfo.Builder(jobId, service)
                .setBackoffCriteria(60 * 1000, JobInfo.BACKOFF_POLICY_LINEAR) // 60 sec
                .setPeriodic(intervalMillis)
                .setPersisted(true) // Auto register after reboot.
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY) // All network.
                .setRequiresCharging(false) // Run always.
                .setRequiresDeviceIdle(false) // Run always.
                .build();
        return jobInfo;
    }
}
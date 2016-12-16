package com.fezrestia.android.cloudsyncclient.zerosim;

import android.app.job.JobParameters;
import android.app.job.JobService;

import com.fezrestia.android.util.log.Log;

public class ZeroSimJobSchedulerService extends JobService {
    // Log tag.
    public static final String TAG = "ZeroSimJobSchedulerService";
    // Log flag.
    public static final boolean IS_DEBUG = false || Log.IS_DEBUG;

    @Override
    public boolean onStartJob(JobParameters params) {
        if (IS_DEBUG) Log.logDebug(TAG, "onStartJob() : E");



        //TODO: Implements switch sync or notify.



        // Notify.
        Thread notifyThread = new NotifyThread(params);
        notifyThread.setName(TAG + "-NotifyThread");
        notifyThread.setPriority(Thread.MIN_PRIORITY);

        if (IS_DEBUG) Log.logDebug(TAG, "onStartJob() : X");
        return true; // Run on background thread.
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        if (IS_DEBUG) Log.logDebug(TAG, "onStopJob() : E");

        // NOP.

        if (IS_DEBUG) Log.logDebug(TAG, "onStopJob() : X");
        return false;
    }

    private class NotifyThread extends Thread {
        private final JobParameters mParams;

        /**
         * CONSTRUCTOR.
         */
        private NotifyThread(JobParameters params) {
            mParams = params;
        }

        @Override
        public void run() {
            if (IS_DEBUG) Log.logDebug(TAG, "NotifyThread.run() : E");



            //TODO: Implements submit REST API



            jobFinished(mParams, false); // Not reschedule.

            if (IS_DEBUG) Log.logDebug(TAG, "NotifyThread.run() : X");
        }
    }

    private class SyncThread extends Thread {
        private final JobParameters mParams;

        /**
         * CONSTRUCTOR.
         */
        private SyncThread(JobParameters params) {
            mParams = params;
        }

        @Override
        public void run() {
            if (IS_DEBUG) Log.logDebug(TAG, "SyncThread.run() : E");



            //TODO: Implements submit REST API



            jobFinished(mParams, false); // Not reschedule.

            if (IS_DEBUG) Log.logDebug(TAG, "SyncThread.run() : X");
        }

    }

}

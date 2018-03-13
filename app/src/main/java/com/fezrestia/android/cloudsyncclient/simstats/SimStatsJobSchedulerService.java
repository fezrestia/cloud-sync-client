package com.fezrestia.android.cloudsyncclient.simstats;

import android.app.job.JobParameters;
import android.app.job.JobService;

import com.fezrestia.android.cloudsyncclient.RootApplication;
import com.fezrestia.android.util.log.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class SimStatsJobSchedulerService extends JobService {
    // Log tag.
    public static final String TAG = "SimStatsJobSchedulerService";
    // Log flag.
    @SuppressWarnings("PointlessBooleanExpression")
    public static final boolean IS_DEBUG = false || Log.IS_DEBUG;

    @Override
    public boolean onStartJob(JobParameters params) {
        if (IS_DEBUG) Log.logDebug(TAG, "onStartJob() : E");

        switch(params.getJobId()) {
            case SimStatsSettingActivity.JOB_ID_NOTIFY:
                Thread notifyThread = new NotifyThread(params);
                notifyThread.setName(TAG + "-NotifyThread");
                notifyThread.setPriority(Thread.MIN_PRIORITY);
                notifyThread.start();
                break;

            case SimStatsSettingActivity.JOB_ID_SYNC:
                Thread syncThread = new SyncThread(params);
                syncThread.setName(TAG + "-SyncThread");
                syncThread.setPriority(Thread.MIN_PRIORITY);
                syncThread.start();
                break;

            default:
                Log.logError(TAG, "onStartJob() : UnExpected Job ID = " + params.getJobId());
                break;
        }

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

        @SuppressWarnings("UnusedAssignment")
        @Override
        public void run() {
            if (IS_DEBUG) Log.logDebug(TAG, "NotifyThread.run() : E");

            String response;

            response = submitGetRequest(SimStatsConstants.SIM_STATS_NOTIFY_GET_URL_DCM);
            if (IS_DEBUG) Log.logDebug(TAG, "DCM response = " + response);

            response = submitGetRequest(SimStatsConstants.SIM_STATS_NOTIFY_GET_URL_NURO);
            if (IS_DEBUG) Log.logDebug(TAG, "NURO response = " + response);

            response = submitGetRequest(SimStatsConstants.SIM_STATS_NOTIFY_GET_URL_ZEROSIM);
            if (IS_DEBUG) Log.logDebug(TAG, "ZEROSIM response = " + response);

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

        @SuppressWarnings("UnusedAssignment")
        @Override
        public void run() {
            if (IS_DEBUG) Log.logDebug(TAG, "SyncThread.run() : E");

            String response;

            response = submitGetRequest(SimStatsConstants.SIM_STATS_SYNC_GET_URL_DCM);
            if (IS_DEBUG) Log.logDebug(TAG, "DCM response = " + response);

            response = submitGetRequest(SimStatsConstants.SIM_STATS_SYNC_GET_URL_NURO);
            if (IS_DEBUG) Log.logDebug(TAG, "NURO response = " + response);

            response = submitGetRequest(SimStatsConstants.SIM_STATS_SYNC_GET_URL_ZEROSIM);
            if (IS_DEBUG) Log.logDebug(TAG, "ZEROSIM response = " + response);

            jobFinished(mParams, false); // Not reschedule.

            if (IS_DEBUG) Log.logDebug(TAG, "SyncThread.run() : X");
        }
    }

    /**
     * Submit HTTP GET request.
     *
     * @param userReadableUrl url
     * @return response
     */
    private String submitGetRequest(String userReadableUrl) {
        String ret = null;

        try {
            URL url = new URL(userReadableUrl);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setDoInput(true);

            con.connect();

            InputStream in = con.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(in));

            StringBuilder sb = new StringBuilder();

            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            ret = sb.toString();

            if (IS_DEBUG) {
                Log.logDebug(TAG, "Notify Response:");
                Log.logDebug(TAG, "    " + ret);
            }

            br.close();
            in.close();

        } catch(MalformedURLException e) {
            e.printStackTrace();
            RootApplication.sendNotification(
                    getApplicationContext(),
                    "JobScheduler ERROR",
                    "MalformedURLException");
        } catch (ProtocolException e) {
            e.printStackTrace();
            RootApplication.sendNotification(
                    getApplicationContext(),
                    "JobScheduler ERROR",
                    "ProtocolException");
        } catch(IOException e) {
            e.printStackTrace();
            RootApplication.sendNotification(
                    getApplicationContext(),
                    "JobScheduler ERROR",
                    "IOException");
        }

        return ret;
    }
}

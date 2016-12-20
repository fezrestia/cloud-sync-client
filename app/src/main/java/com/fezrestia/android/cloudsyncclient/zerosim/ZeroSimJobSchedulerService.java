package com.fezrestia.android.cloudsyncclient.zerosim;

import android.app.job.JobParameters;
import android.app.job.JobService;

import com.fezrestia.android.util.log.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class ZeroSimJobSchedulerService extends JobService {
    // Log tag.
    public static final String TAG = "ZeroSimJobSchedulerService";
    // Log flag.
    public static final boolean IS_DEBUG = false || Log.IS_DEBUG;

    private static final String URL_NOTIFY
            = "https://cloud-sync-service.herokuapp.com/zero_sim_usages/api/notify";
    private static final String URL_SYNC
            = "https://cloud-sync-service.herokuapp.com/zero_sim_usages/api/sync";

    @Override
    public boolean onStartJob(JobParameters params) {
        if (IS_DEBUG) Log.logDebug(TAG, "onStartJob() : E");

        switch(params.getJobId()) {
            case ZeroSimSettingActivity.JOB_ID_NOTIFY:
                Thread notifyThread = new NotifyThread(params);
                notifyThread.setName(TAG + "-NotifyThread");
                notifyThread.setPriority(Thread.MIN_PRIORITY);
                notifyThread.start();
                break;

            case ZeroSimSettingActivity.JOB_ID_SYNC:
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

        @Override
        public void run() {
            if (IS_DEBUG) Log.logDebug(TAG, "NotifyThread.run() : E");

            submitGetRequest(URL_NOTIFY);

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

            submitGetRequest(URL_SYNC);

            jobFinished(mParams, false); // Not reschedule.

            if (IS_DEBUG) Log.logDebug(TAG, "SyncThread.run() : X");
        }
    }

    /**
     * Submit HTTP GET request.
     *
     * @param userReadableUrl
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
            throw new RuntimeException("URL Malform Exception");
        } catch (ProtocolException e) {
            e.printStackTrace();
            throw new RuntimeException("Protocol Exception");
        } catch(IOException e) {
            e.printStackTrace();
            throw new RuntimeException("IOException");
        }

        return ret;
    }
}

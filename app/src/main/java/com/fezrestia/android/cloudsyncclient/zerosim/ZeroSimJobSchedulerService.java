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

    @Override
    public boolean onStartJob(JobParameters params) {
        if (IS_DEBUG) Log.logDebug(TAG, "onStartJob() : E");



        //TODO: Implements switch sync or notify.



        // Notify.
        Thread notifyThread = new NotifyThread(params);
        notifyThread.setName(TAG + "-NotifyThread");
        notifyThread.setPriority(Thread.MIN_PRIORITY);
        notifyThread.start();

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

            try {
                URL url = new URL(URL_NOTIFY);
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

                if (IS_DEBUG) {
                    Log.logDebug(TAG, "Notify Response:");
                    Log.logDebug(TAG, "    " + sb.toString());
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

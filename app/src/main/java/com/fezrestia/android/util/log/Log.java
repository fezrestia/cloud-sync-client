package com.fezrestia.android.util.log;

import android.os.SystemClock;

public class Log {
    // All area total log trigger.
    public static final boolean IS_DEBUG = false;

    /**
     * Debug log.
     *
     * @param tag Log tag.
     * @param event Log message.
     */
    public static void logDebug(String tag, String event) {
        log("DEBUG", tag, event);
    }

    /**
     * Error log.
     *
     * @param tag Log tag.
     * @param event Log message.
     */
    public static void logError(String tag, String event) {
        log("ERROR", tag, event);
    }

    private static void log(String globalTag, String localTag, String event) {
        String builder = "[" + globalTag + "] " +
                "[TIME = " + SystemClock.uptimeMillis() + "] " +
                "[" + localTag + "]" +
                "[" + Thread.currentThread().getName() + "] " +
                ": " + event;
        android.util.Log.e("TraceLog", builder);
    }
}

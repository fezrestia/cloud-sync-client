package com.fezrestia.android.cloudsyncclient.simstats;

class SimStatsConstants {

    static final String WIDGET_CLICK_CALLBACK_INTENT
            = "com.fezrestia.android.cloudsyncclient.simstats.ACTION_ON_WIDGET_CLICKED";

    // Timestamp definitions.
    static final String SP_KEY_SIM_STATS_LAST_UPDATED_TIMESTAMP
            = "key-sim-stats-last-updated-timestamp";

    // Timestamp.
    static final long TIMESTAMP_DIFF_THRESHOLD_MILLIS = 24 * 60 * 60 * 1000; // 24 hour.

    // Get latest SIM stats web API.
    static final String REST_GET_LATEST_SIM_STATS
            = "https://asia-northeast1-cloud-sync-service.cloudfunctions.net/httpsGetLatestSimStats";

    // Update DCM stats.
    static final String REST_GET_UPDATE_DCM_STATS
            = "https://asia-northeast1-cloud-sync-service.cloudfunctions.net/httpsUpdateDcmStats";

    // Update NURO stats.
    static final String REST_GET_UPDATE_NURO_STATS
            = "https://asia-northeast1-cloud-sync-service.cloudfunctions.net/httpsUpdateNuroStats";

}

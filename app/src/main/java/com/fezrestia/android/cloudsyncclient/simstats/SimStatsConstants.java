package com.fezrestia.android.cloudsyncclient.simstats;

public class SimStatsConstants {

    static final String WIDGET_CLICK_CALLBACK_INTENT
            = "com.fezrestia.android.cloudsyncclient.simstats.ACTION_ON_WIDGET_CLICKED";

    static final String SP_KEY_CURRENT_MONTH_USED_ZEROSIM
            = "key-sim-stats-current-month-used-zerosim";
    static final String SP_KEY_CURRENT_MONTH_USED_NURO
            = "key-sim-stats-current-month-used-nuro";
    static final String SP_KEY_CURRENT_MONTH_USED_DCM
            = "key-sim-stats-current-month-used-dcm";

    static final float INVALID_USED_AMOUNT = -1.0f;

    static final float MONTH_USED_MB_LIMIT_ZEROSIM = 5000.0f;
    static final float MONTH_USED_MB_LIMIT_NURO = 2000.0f;
    static final float MONTH_USED_MB_LIMIT_DCM = 20000.0f;

    static final float MONTH_USED_MB_CLEARANCE = 50.0f;

    static final float MONTH_USED_MB_WARNING_ZEROSIM =
            MONTH_USED_MB_LIMIT_ZEROSIM - MONTH_USED_MB_CLEARANCE;

    // Notify API.
    public static final String SIM_STATS_NOTIFY_GET_URL_ZEROSIM =
            "https://cloud-sync-service.herokuapp.com/zero_sim_stats/api/notify";
    public static final String SIM_STATS_NOTIFY_GET_URL_NURO =
            "https://cloud-sync-service.herokuapp.com/nuro_sim_stats/api/notify";
    public static final String SIM_STATS_NOTIFY_GET_URL_DCM =
            "https://cloud-sync-service.herokuapp.com/dcm_sim_stats/api/notify";

    // Sync request API.
    public static final String SIM_STATS_SYNC_GET_URL_ZEROSIM =
            "https://cloud-sync-service.herokuapp.com/zero_sim_stats/api/sync";
    public static final String SIM_STATS_SYNC_GET_URL_NURO =
            "https://cloud-sync-service.herokuapp.com/nuro_sim_stats/api/sync";
    public static final String SIM_STATS_SYNC_GET_URL_DCM =
            "https://cloud-sync-service.herokuapp.com/dcm_sim_stats/api/sync";

    // Protocol definitions.
    static final String KEY_SIM_STATS_MONTH_USED_MB_NOTIFY_ZEROSIM =
            "zerosim_month_used_current_mb";
    static final String KEY_SIM_STATS_MONTH_USED_MB_NOTIFY_NURO =
            "nuro_month_used_current_mb";
    static final String KEY_SIM_STATS_MONTH_USED_MB_NOTIFY_DCM =
            "dcm_month_used_current_mb";

    // FCM message callback register key.
    public static final String SIM_STATS_FCM_CALLBACK_REG_KEY = "sim-stats";

}

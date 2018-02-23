package com.fezrestia.android.cloudsyncclient.zerosim;

public class ZeroSimConstants {

    static final String WIDGET_CLICK_CALLBACK_INTENT
            = "com.fezrestia.android.cloudsyncclient.zerosim.ACTION_ON_WIDGET_CLICKED";

    static final String SP_KEY_CURRENT_MONTH_USED_ZEROSIM
            = "key-zerosim-current-month-used-zerosim";
    static final String SP_KEY_CURRENT_MONTH_USED_NURO
            = "key-zerosim-current-month-used-nuro";
    static final String SP_KEY_CURRENT_MONTH_USED_DOCOMO
            = "key-zerosim-current-month-used-docomo";

    static final int INVALID_USED_AMOUNT = -1;

    static final int MONTH_USED_MB_LIMIT_ZEROSIM = 5000;
    static final int MONTH_USED_MB_LIMIT_NURO = 2000;
    static final int MONTH_USED_MB_LIMIT_DOCOMO = 20000;

    static final int MONTH_USED_MB_CLEARANCE = 50;

    static final int MONTH_USED_MB_WARNING_ZEROSIM =
            MONTH_USED_MB_LIMIT_ZEROSIM - MONTH_USED_MB_CLEARANCE;

    // Notify API.
    public static final String SIM_STATS_NOTIFY_GET_URL =
            "https://cloud-sync-service.herokuapp.com/zero_sim_stats/api/notify";
    // Sync request API.
    public static final String SIM_STATS_SYNC_GET_URL =
            "https://cloud-sync-service.herokuapp.com/zero_sim_stats/api/sync";

    // FCM message callback register key.
    public static final String SIM_STATS_FCM_CALLBACK_REG_KEY = "zero-sim-stats";

}

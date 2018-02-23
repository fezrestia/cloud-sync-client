package com.fezrestia.android.cloudsyncclient.zerosim;

public class ZeroSimConstants {

    public static final String WIDGET_CLICK_CALLBACK_INTENT
            = "com.fezrestia.android.cloudsyncclient.zerosim.ACTION_ON_WIDGET_CLICKED";

    public static final String SP_KEY_CURRENT_MONTH_USED_ZEROSIM
            = "key-zerosim-current-month-used-zerosim";
    public static final String SP_KEY_CURRENT_MONTH_USED_NURO
            = "key-zerosim-current-month-used-nuro";
    public static final String SP_KEY_CURRENT_MONTH_USED_DOCOMO
            = "key-zerosim-current-month-used-docomo";

    public static final int INVALID_USED_AMOUNT = -1;

    public static final int MONTH_USED_MB_LIMIT_ZEROSIM = 5000;
    public static final int MONTH_USED_MB_LIMIT_NURO = 2000;
    public static final int MONTH_USED_MB_LIMIT_DOCOMO = 20000;

    public static final int MONTH_USED_MB_CLEARANCE = 50;

    public static final int MONTH_USED_MB_WARNING_ZEROSIM =
            MONTH_USED_MB_LIMIT_ZEROSIM - MONTH_USED_MB_CLEARANCE;

    // Notify API.
    public static final String SIM_STATS_NOTIFY_GET_URL =
            "https://cloud-sync-service.herokuapp.com/zero_sim_usages/api/notify";

    // FCM message callback register key.
    public static final String SIM_STATS_FCM_CALLBACK_REG_KEY = "zero-sim-stats";

}

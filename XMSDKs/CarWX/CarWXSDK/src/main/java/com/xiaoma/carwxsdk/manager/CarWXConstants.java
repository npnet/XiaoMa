package com.xiaoma.carwxsdk.manager;


public class CarWXConstants {
    public static final int CODE_REMOTE_ERROR = 1001;
    public static final int CODE_CLIENT_ERROR_SERVICE_UNLINK = 2001;
    public static final int CODE_CLIENT_ERROR_PARAMS_ILLEGAL = 2002;

    public static final String REMOTE_SERVICE_UNLINK = "remote service unlink";
    public static final String PARAMS_ILLEGAL = "params is illegal,please check!!!";
    /**
     * 皮肤变化事件
     */
    public static final String ACTION_THEME_CHANGED = "com.xiaoma.carwxsdk.THEME_CHANGED";

    /**
     * 皮肤id字段
     */
    public static final String EXTRA_THEME_ID_INT = "themeId";
    /**
     * 默认主题:智慧
     */
    public static final int THEME_ID_ZHIHUI = 0;

    /**
     * 轻奢主题
     */
    public static final int THEME_ID_QINGSHE = 1;

    /**
     * 盗梦主题
     */
    public static final int THEME_ID_DAOMENG = 2;

    /**
     * 默认主题
     */
    public static final int DEFAULT_THEME_ID = THEME_ID_ZHIHUI;
}

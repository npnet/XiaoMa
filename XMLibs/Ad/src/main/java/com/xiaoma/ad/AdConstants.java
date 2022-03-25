package com.xiaoma.ad;

import com.xiaoma.config.ConfigManager;

/**
 * Created by youthyj on 2018/9/7.
 */
public class AdConstants {
    private AdConstants() throws Exception {
        throw new Exception();
    }

    public static final String getAdUrl = ConfigManager.EnvConfig.getEnv().getBusiness() + "loadingScreen/main.action";
}

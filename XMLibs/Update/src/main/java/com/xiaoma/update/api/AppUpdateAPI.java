package com.xiaoma.update.api;

import com.xiaoma.config.ConfigManager;

/**
 * Created by Thomas on 2018/10/13 0013
 */

public interface AppUpdateAPI {

    String BASE_URL = ConfigManager.EnvConfig.getEnv().getBusiness();
    String GET_LASTED_VERSION = BASE_URL + "apk/getLastedVersion.action";

}

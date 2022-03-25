package com.xiaoma.dialect.api;

import com.xiaoma.config.ConfigManager;

/**
 * Created by Thomas on 2018/10/13 0013
 * DialectAPI API
 */

public interface DialectAPI {

    String BASE_URL = ConfigManager.EnvConfig.getEnv().getBusiness();

}

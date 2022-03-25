package com.xiaoma.oilconsumption.api;

import com.xiaoma.config.ConfigManager;

/**
 * Created by Thomas on 2018/10/13 0013
 * OilConsumptionApi API
 */

public interface OilConsumptionApi {

    String BASE_URL = ConfigManager.EnvConfig.getEnv().getBusiness();
    
}

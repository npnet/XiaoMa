package com.xiaoma.instruction.api;

import com.xiaoma.config.ConfigManager;

/**
 * Created by Thomas on 2018/10/13 0013
 * InstructionAPI API
 */

public interface InstructionAPI {

    String BASE_URL = ConfigManager.EnvConfig.getEnv().getBusiness();

    String GET_MANUAL_LIST = BASE_URL +"usersManual/applicationList";
    String GET_MANUAL_ITEM_LIST = BASE_URL +"usersManual/applicationInfos";
}

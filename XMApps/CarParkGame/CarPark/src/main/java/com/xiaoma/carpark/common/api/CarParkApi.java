package com.xiaoma.carpark.common.api;

import com.xiaoma.config.ConfigManager;

public interface CarParkApi {
    String BASE_URL = ConfigManager.EnvConfig.getEnv().getBusiness();

    String GET_GAME_LIST = BASE_URL + "carPark/getGameList.action";
}

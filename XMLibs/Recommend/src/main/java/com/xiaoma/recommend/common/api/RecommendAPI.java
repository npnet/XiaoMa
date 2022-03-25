package com.xiaoma.recommend.common.api;

import com.xiaoma.config.ConfigManager;

/**
 * @author: iSun
 * @date: 2018/12/5 0005
 */

public class RecommendAPI {

    public static String BASE_URL = ConfigManager.EnvConfig.getEnv().getBusiness();
    public static String GET_RECOMMEND_LIST = BASE_URL +"recommend/getRecommendList.action";

}

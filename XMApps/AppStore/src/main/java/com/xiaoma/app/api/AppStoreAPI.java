package com.xiaoma.app.api;

import com.xiaoma.config.ConfigManager;

/**
 * Created by Thomas on 2018/10/13 0013
 * AppStore API
 */

public interface AppStoreAPI {

    String BASE_URL = ConfigManager.EnvConfig.getEnv().getBusiness();
    String GET_ALL_CATEGORY = BASE_URL + "apkCategory/getAllCategory";
    String GET_APK_BY_CATEGORY = BASE_URL + "apkCategory/getApkByCategory";
    String GET_APKINFO_BY_PACKGENAME = BASE_URL + "apk/getApkByNameAndTypeAndChanneId.action";
}

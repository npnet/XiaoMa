package com.qiming.fawcard.synthesize.base;

import com.qiming.fawcard.synthesize.base.util.PropertiesLoaderUtil;

import java.util.Properties;

/**
 * Created by summit on 3/9/17.
 */

public class AppConfigManager {

    private static AppConfigManager instance = null;
    private String serverRoot;
    private String appKey;
    private String appSecretKey;

    private AppConfigManager() {
        Properties properties = PropertiesLoaderUtil.load(getClass(), "app_config.properties");
        serverRoot = properties.getProperty("SERVER_HOST");
        appKey = properties.getProperty("APP_KEY");
        appSecretKey = properties.getProperty("SECRET_KEY");
    }

    public static AppConfigManager getInstance() {
        if (instance == null) {
            synchronized (AppConfigManager.class) {
                if (instance == null) {
                    instance = new AppConfigManager();
                }
            }
        }
        return instance;
    }

    public String getAppKey() {
        return appKey;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }

    public String getServerRoot() {
        return serverRoot;
    }

    public void setServerRoot(String serverRoot) {
        this.serverRoot = serverRoot;
    }

    public String getAppSecretKey() {
        return appSecretKey;
    }

    public void setAppSecretKey(String appSecretKey) {
        this.appSecretKey = appSecretKey;
    }

}

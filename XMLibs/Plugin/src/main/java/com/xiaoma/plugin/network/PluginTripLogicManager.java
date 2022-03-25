package com.xiaoma.plugin.network;

import com.xiaoma.config.ConfigManager;
import com.xiaoma.network.XmHttp;
import com.xiaoma.network.callback.StringCallback;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ZYao.
 * Date ：2018/9/25 0025
 */
public class PluginTripLogicManager {

    public static PluginTripLogicManager getInstance() {
        return InstanceHolder.instance;
    }

    private static class InstanceHolder {
        static final PluginTripLogicManager instance = new PluginTripLogicManager();
    }

    private String getPreUrl() {
        return ConfigManager.EnvConfig.getEnv().getBusiness();
    }

    public void getAllPlugin(StringCallback callback) {
        String url = getPreUrl() + "plugin/getAllPlugin.action";
        XmHttp.getDefault().getString(url, null, callback);
    }

    public void getPluginByPackageName(String packageName, StringCallback callback) {
        String url = getPreUrl() + "plugin/getPluginByPackageNameAndChannelId.action";
        Map<String, Object> params = new HashMap<>();
        params.put("packageName", packageName);
        XmHttp.getDefault().getString(url, params, callback);
    }

}

package com.xiaoma.carpark.plugin.listener;

import com.qihoo360.replugin.model.PluginInfo;

/**
 * Created by Thomas on 2018/11/7 0007
 * 插件安装及预加载状态通知
 */

public interface IPluginStateListener {

    /**
     * 开始
     *
     * @param pluginInfo
     */
    void onStart(PluginInfo pluginInfo);

    /**
     * 成功
     *
     * @param pluginInfo
     */
    void onSuccess(PluginInfo pluginInfo);

    /**
     * 失败
     *
     * @param pluginInfo
     */
    void onFail(PluginInfo pluginInfo);
}

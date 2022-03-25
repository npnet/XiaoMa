package com.xiaoma.carpark.plugin.listener;

import android.content.Context;

import com.qihoo360.replugin.model.PluginInfo;

/**
 * Created by Thomas on 2018/11/7 0007
 */

public interface IPluginManager {

    /**
     * 打开插件指定页面
     *
     * @param context
     * @param packageName 插件包名
     * @param className   插件类名
     */
    void startRePluginActivity(Context context, String packageName, String className);

    /**
     * 安装插件
     *
     * @param pluginFilePath
     * @param listener
     */
    void installPlugin(String pluginFilePath, IPluginStateListener listener);

    /**
     * 卸载插件
     *
     * @param pluginName
     * @param listener
     */
    void unInstallPlugin(String pluginName, IPluginStateListener listener);

    /**
     * 插件是否安装
     *
     * @param packageName 包名
     * @return
     */
    boolean isPluginInstalled(String packageName);

    /**
     * 根据包名获取插件信息
     *
     * @param packageName
     * @return
     */
    PluginInfo getPluginInfo(String packageName);
}

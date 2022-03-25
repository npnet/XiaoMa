package com.xiaoma.plugin.mamager;

import com.xiaoma.plugin.download.PluginDownloadListener;
import com.xiaoma.plugin.model.PluginInfo;

import java.util.HashMap;

/**
 * Created by ZYao.
 * Date ：2018/9/17 0017
 */
public interface IPlugin {

    //下载所有的插件
    void fetchAllPluginList();

    //安装插件
    boolean installPlugin(PluginInfo pluginInfo);

    //卸载插件
    boolean uninstallPlugin(PluginInfo pluginInfo);

    //判断插件是否安装
    boolean isPluginInstalled(String packageName);

    //下载插件
    void downloadPlugin(PluginInfo pluginInfo, PluginDownloadListener pluginDownloadListener);

    //打开插件
    void startPluginByPkgName(String packageName, final String className, HashMap<String, String> params,
                              IStartPluginListener startPluginListener);


}

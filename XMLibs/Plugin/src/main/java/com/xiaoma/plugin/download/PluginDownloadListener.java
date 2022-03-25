package com.xiaoma.plugin.download;

import com.xiaoma.plugin.model.PluginInfo;

/**
 * Created by vincenthu on 2017/12/6.
 */

public interface PluginDownloadListener {

    void onSuccess(PluginInfo pluginInfo, String path);

    void onFailed(PluginInfo pluginInfo, String path);

}

package com.xiaoma.plugin.callback;

import android.content.Context;
import android.content.Intent;

import com.qihoo360.replugin.RePluginCallbacks;
import com.xiaoma.utils.log.KLog;

/**
 * Created by Administrator on 2017/12/26 0026.
 */

public class XiaoMaRePluginCallbacks extends RePluginCallbacks {

    public XiaoMaRePluginCallbacks(Context context) {
        super(context);
    }

    @Override
    public boolean onLoadLargePluginForActivity(Context context, String plugin, Intent intent, int process) {
        return super.onLoadLargePluginForActivity(context, plugin, intent, process);
    }

    @Override
    public boolean onPluginNotExistsForActivity(final Context context, final String plugin, Intent intent, int process) {
        // FIXME 当插件"没有安装"时触发此逻辑，可打开您的"下载对话框"并开始下载。
        // FIXME 其中"intent"需传递到"对话框"内，这样可在下载完成后，打开这个插件的Activity
        KLog.d("XiaoMaRePluginCallbacks", "onPluginNotExistsForActivity: Failed! plugin=" + plugin);
        return super.onPluginNotExistsForActivity(context, plugin, intent, process);
    }
}
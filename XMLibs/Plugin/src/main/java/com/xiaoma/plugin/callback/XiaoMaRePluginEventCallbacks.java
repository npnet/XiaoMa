package com.xiaoma.plugin.callback;

import android.content.Context;

import com.qihoo360.replugin.RePluginEventCallbacks;
import com.qihoo360.replugin.model.PluginInfo;
import com.xiaoma.utils.log.KLog;

/**
 * Created by Administrator on 2017/12/26 0026.
 */

public class XiaoMaRePluginEventCallbacks extends RePluginEventCallbacks {
    public XiaoMaRePluginEventCallbacks(Context context) {
        super(context);
    }

    @Override
    public void onInstallPluginSucceed(PluginInfo info) {
        KLog.d("XiaoMaRePluginEventCallbacks", "onInstallPluginSucceed: Failed! info=" + info);
        super.onInstallPluginSucceed(info);
    }

    @Override
    public void onInstallPluginFailed(String path, InstallResult code) {
        // FIXME 当插件安装失败时触发此逻辑。您可以在此处做“打点统计”，也可以针对安装失败情况做“特殊处理”
        // 大部分可以通过RePlugin.install的返回值来判断是否成功
        KLog.d("XiaoMaRePluginEventCallbacks", "onInstallPluginFailed: Failed! path=" + path + "; r=" + code);
        super.onInstallPluginFailed(path, code);
    }

    @Override
    public void onStartActivityCompleted(String plugin, String activity, boolean result) {
        // FIXME 当打开Activity成功时触发此逻辑，可在这里做一些APM、打点统计等相关工作
        KLog.d("XiaoMaRePluginEventCallbacks", "onStartActivityCompleted: plugin=" + plugin + "\r\n result=" + result);
        super.onStartActivityCompleted(plugin, activity, result);
    }
}
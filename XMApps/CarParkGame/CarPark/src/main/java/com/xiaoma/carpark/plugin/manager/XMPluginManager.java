package com.xiaoma.carpark.plugin.manager;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import com.qihoo360.replugin.RePlugin;
import com.qihoo360.replugin.model.PluginInfo;
import com.xiaoma.carpark.plugin.listener.IPluginManager;
import com.xiaoma.carpark.plugin.listener.IPluginStateListener;
import com.xiaoma.thread.ThreadDispatcher;

/**
 * Created by Thomas on 2018/11/6 0006
 * 插件管理器
 */

public class XMPluginManager implements IPluginManager {

    private static XMPluginManager sXMPluginManager = new XMPluginManager();

    private XMPluginManager() {
    }

    public static XMPluginManager getInstance() {
        return sXMPluginManager;
    }

    @Override
    public void installPlugin(String pluginFilePath, IPluginStateListener listener) {
        preloadPlugin(RePlugin.install(pluginFilePath), listener);
    }

    @Override
    public void startRePluginActivity(Context context, String packageName, String className) {
        Intent intent = new Intent();
        intent.setComponent(new ComponentName(packageName, className));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        RePlugin.startActivity(context, intent);
    }

    public void startRePluginActivity(Context context, String packageName, String className, String uId) {
        Intent intent = new Intent();
        intent.setComponent(new ComponentName(packageName, className));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("uid", uId);
        RePlugin.startActivity(context, intent);
    }

    @Override
    public PluginInfo getPluginInfo(String packageName) {
        return RePlugin.getPluginInfo(packageName);
    }

    @Override
    public boolean isPluginInstalled(String packageName) {
        return RePlugin.isPluginInstalled(packageName);
    }

    /**
     * 预加载插件
     *
     * @param pluginInfo
     * @param listener
     */
    private void preloadPlugin(final PluginInfo pluginInfo, final IPluginStateListener listener) {
        ThreadDispatcher.getDispatcher().postNormalPriority(new Runnable() {
            @Override
            public void run() {
                if (listener == null) {
                    return;
                }
                if (pluginInfo == null) {
                    listener.onStart(pluginInfo);
                    listener.onFail(pluginInfo);
                    return;
                }

                listener.onStart(pluginInfo);
                boolean preload = RePlugin.preload(pluginInfo);
                if (preload) {
                    listener.onSuccess(pluginInfo);

                } else {
                    listener.onFail(pluginInfo);
                }
            }
        });
    }

    @Override
    public void unInstallPlugin(final String pluginName, final IPluginStateListener listener) {
        ThreadDispatcher.getDispatcher().postNormalPriority(new Runnable() {
            @Override
            public void run() {
                if (listener == null) {
                    return;
                }
                PluginInfo pluginInfo = getPluginInfo(pluginName);
                if (pluginInfo == null) {
                    listener.onStart(pluginInfo);
                    listener.onFail(pluginInfo);
                    return;
                }

                listener.onStart(pluginInfo);
                boolean uninstall = RePlugin.uninstall(pluginName);
                if (uninstall) {
                    listener.onSuccess(pluginInfo);

                } else {
                    listener.onFail(pluginInfo);
                }
            }
        });
    }
}

package com.xiaoma.plugin.application;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;

import com.qihoo360.replugin.RePlugin;
import com.qihoo360.replugin.RePluginCallbacks;
import com.qihoo360.replugin.RePluginConfig;
import com.xiaoma.plugin.callback.XiaoMaRePluginCallbacks;
import com.xiaoma.plugin.callback.XiaoMaRePluginEventCallbacks;


/**
 * Created by vincenthu on 2017/11/28.
 */

public abstract class PluginApplication extends Application {

    private static PluginApplication application;

    /**
     * 子类可以复写此方法来自定义RePluginConfig。请参见 RePluginConfig 的说明
     *
     * @return 新的RePluginConfig对象
     * @see RePluginConfig
     */
    protected RePluginConfig createConfig() {
        RePluginConfig rePluginConfig = new RePluginConfig();
        return rePluginConfig;
    }

    /**
     * 子类可以复写此方法来自定义RePluginCallbacks。请参见 RePluginCallbacks 的说明 <p>
     * 注意：若在createConfig的RePluginConfig内同时也注册了Callbacks，则以这里创建出来的为准
     *
     * @return 新的RePluginCallbacks对象，可以为空
     * @see RePluginCallbacks
     */
    protected RePluginCallbacks createCallbacks() {
        return new XiaoMaRePluginCallbacks(this);
    }


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        RePluginConfig c = createConfig();
        if (c == null) {
            c = new RePluginConfig();
        }

        RePluginCallbacks cb = createCallbacks();
        if (cb != null) {
            c.setCallbacks(cb);
        }
        // 针对“安装失败”等情况来做进一步的事件处理
        c.setEventCallbacks(new XiaoMaRePluginEventCallbacks(base));
        RePlugin.App.attachBaseContext(this, c);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        RePlugin.App.onCreate();
        application = this;
    }


    @Override
    public void onLowMemory() {
        super.onLowMemory();
        // 如果App的minSdkVersion >= 14，该方法可以不调用
        RePlugin.App.onLowMemory();
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);

        // 如果App的minSdkVersion >= 14，该方法可以不调用
        RePlugin.App.onTrimMemory(level);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // 如果App的minSdkVersion >= 14，该方法可以不调用
        RePlugin.App.onConfigurationChanged(newConfig);
    }

    public static Application getAppContext() {
        // TODO: 2018/9/7 接入Tinker时, 需要重写这个方法
        return application;
    }
}

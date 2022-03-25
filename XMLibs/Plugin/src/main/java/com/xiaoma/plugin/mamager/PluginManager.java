package com.xiaoma.plugin.mamager;

/**
 * Created by ZYao.
 * Date ：2018/9/17 0017
 */
public class PluginManager {
    public static IPlugin getRePlugin() {
        return RePluginHolder.getInstance();
    }
}

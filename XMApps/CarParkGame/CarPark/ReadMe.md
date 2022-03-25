车乐园或基于插件，或H5集成活动和游戏

package:
    activity活动模块
    game游戏模块
    recommend热门推荐模块
    wallet钱包模块
    common通用组件模块
    main主页面模块


插件使用:
private void test1() {
        String packageName = "com.xiaoma.dialect";
        String className = "com.xiaoma.dialect.ui.activity.MainActivity";
        String pluginFilePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/plugin/Dialect.apk";
        testPlugin(packageName, className, pluginFilePath);
}

private void test2() {
        String packageName = "com.xiaoma.songname";
        String className = "com.xiaoma.songname.ui.activity.MainActivity";
        String pluginFilePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/plugin/SongName.apk";
        testPlugin(packageName, className, pluginFilePath);
}

private void test3() {
        String packageName = "com.xiaoma.app";
        String className = "com.xiaoma.app.ui.activity.MainActivity";
        String pluginFilePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/plugin/AppStore.apk";
        testPlugin(packageName, className, pluginFilePath);
}

private void startRePluginActivity(Context context) {
        boolean pluginInstalled = XMPluginManager.getIntance().isPluginInstalled(packageName);
        if (pluginInstalled) {
            Log.e(TAG, "插件已经安装: " + pluginInstalled);
            XMPluginManager.getIntance().startRePluginActivity(context, packageName, className);
        }
}

private void installPlugin(String packageName, String className, String pluginFilePath) {
        XMPluginManager.getIntance().installPlugin(pluginFilePath, new IPluginLoadStateListener() {
            @Override
            public void onStart(PluginInfo pluginInfo) {
                if (pluginInfo == null) {
                    Log.e(TAG, "插件不存在");
                } else {
                    Log.e(TAG, "插件开始安装...");
                }
            }

            @Override
            public void onComplete(PluginInfo pluginInfo) {
                if (pluginInfo == null) {
                    Log.e(TAG, "插件不存在");
                } else {
                    Log.e(TAG, "插件安装成功...");
                }
            }

            @Override
            public void onFail(PluginInfo pluginInfo) {
                if (pluginInfo == null) {
                    Log.e(TAG, "插件不存在");
                } else {
                    Log.e(TAG, "插件安装失败...");
                }
            }
        });
}

private void updatePlugin(String packageName, String className, String pluginFilePath) {
        XMPluginManager.getIntance().updatePlugin(pluginFilePath, new IPluginLoadStateListener() {
            @Override
            public void onStart(PluginInfo pluginInfo) {
                if (pluginInfo == null) {
                    Log.e(TAG, "插件不存在");
                } else {
                    Log.e(TAG, "插件开始升级安装...");
                }
            }

            @Override
            public void onComplete(PluginInfo pluginInfo) {
                if (pluginInfo == null) {
                    Log.e(TAG, "插件不存在");
                } else {
                    Log.e(TAG, "插件升级安装成功...");
                }
            }

            @Override
            public void onFail(PluginInfo pluginInfo) {
                if (pluginInfo == null) {
                    Log.e(TAG, "插件不存在");
                } else {
                    Log.e(TAG, "插件升级安装失败...");
                }
            }
        });
}

XMPluginManager.getIntance().unInstallPlugin("", new IPluginStateListener() {
                @Override
                public void onStart(PluginInfo pluginInfo) {
                    if (pluginInfo == null) {
                        Log.e(TAG, "插件不存在");
                     } else {
                        Log.e(TAG, "插件开始卸载...");
                    }
                }

                @Override
                public void onComplete(PluginInfo pluginInfo) {
                    if (pluginInfo == null) {
                        Log.e(TAG, "插件不存在");
                     } else {
                        Log.e(TAG, "插件卸载成功...");
                    }
                }

                @Override
                public void onFail(PluginInfo pluginInfo) {
                    if (pluginInfo == null) {
                        Log.e(TAG, "插件不存在");
                     } else {
                        Log.e(TAG, "插件卸载失败...");
                    }
                }
});

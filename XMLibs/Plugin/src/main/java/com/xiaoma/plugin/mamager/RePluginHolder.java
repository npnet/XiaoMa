package com.xiaoma.plugin.mamager;

import android.content.Intent;
import android.text.TextUtils;

import com.qihoo360.replugin.RePlugin;
import com.xiaoma.network.callback.StringCallback;
import com.xiaoma.network.model.Response;
import com.xiaoma.plugin.PluginConstants;
import com.xiaoma.plugin.application.PluginApplication;
import com.xiaoma.plugin.download.DownLoadTask;
import com.xiaoma.plugin.download.PluginDownloadListener;
import com.xiaoma.plugin.download.PluginDownloadManager;
import com.xiaoma.plugin.model.PluginInfo;
import com.xiaoma.plugin.model.XMPlugin;
import com.xiaoma.plugin.network.PluginTripLogicManager;
import com.xiaoma.thread.ThreadDispatcher;
import com.xiaoma.utils.GsonHelper;
import com.xiaoma.utils.ListUtils;
import com.xiaoma.utils.StringUtil;
import com.xiaoma.utils.log.KLog;
import com.xiaoma.utils.tputils.TPUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by ZYao.
 * Date ：2018/9/17 0017
 */
public class RePluginHolder implements IPlugin {

    public static RePluginHolder getInstance() {
        return InstanceHolder.instance;
    }

    private static class InstanceHolder {
        static final RePluginHolder instance = new RePluginHolder();
    }

    private PluginDownloadManager pluginDownloadManager = new PluginDownloadManager();

    @Override
    public void downloadPlugin(PluginInfo pluginInfo, PluginDownloadListener pluginDownloadListener) {
        if (pluginInfo == null || TextUtils.isEmpty(pluginInfo.getPluginFilePath())) {
            pluginDownloadListener.onFailed(pluginInfo, "");
            return;
        }
        pluginDownloadManager.downloadPlugin(pluginInfo, pluginDownloadListener);
    }

    @Override
    public boolean installPlugin(PluginInfo pluginInfo) {
        if (pluginInfo == null) return false;
        String localPath = StringUtil.getRealFilePath(pluginDownloadManager.getPluginPackageDirPath(),
                pluginInfo.getPluginFilePath(), StringUtil.getFileSuffixByUrl(pluginInfo.getPluginFilePath()));
        return RePlugin.install(localPath) != null;
    }

    @Override
    public boolean uninstallPlugin(PluginInfo pluginInfo) {
        boolean result = RePlugin.uninstall(pluginInfo.getPackageName());
        if (result) {
            String pluginPackageDirPath = pluginDownloadManager.getPluginPackageDirPath();
            String pluginFilePath = pluginInfo.getPluginFilePath();
            String localPath = StringUtil.getRealFilePath(pluginPackageDirPath, pluginFilePath,
                    StringUtil.getFileSuffixByUrl(pluginFilePath));
            File apkFile = new File(localPath);
            if (apkFile != null && apkFile.exists()) {
                apkFile.delete();
            }
            String localTempPath = StringUtil.getRealFilePath(pluginPackageDirPath, pluginFilePath, DownLoadTask.TEMP_SUFFIX);
            File tempFile = new File(localTempPath);
            if (tempFile != null && tempFile.exists()) {
                tempFile.delete();
            }
        }
        KLog.d("unInstall plugin " + pluginInfo.getPluginName() + ", and delete file, the uninstall result = " + result);
        return result;
    }

    @Override
    public boolean isPluginInstalled(String packageName) {
        return RePlugin.isPluginInstalled(packageName);
    }

    @Override
    public void fetchAllPluginList() {
        if (needRequestToday()) {
            PluginTripLogicManager.getInstance().getAllPlugin(new StringCallback() {
                @Override
                public void onSuccess(Response<String> response) {
                    final String result = response.body();
                    KLog.d("onSuccess: " + result);
                    TPUtils.put(PluginApplication.getAppContext(),
                            PluginConstants.FETCH_PLUGIN_DATE_KEY, StringUtil.getDateByYMD());
                    TPUtils.put(PluginApplication.getAppContext(),
                            PluginConstants.PLUGIN_DATA_KEY, result);
                    fixPlugin(result);
                }

                @Override
                public void onError(Response<String> response) {
                    super.onError(response);
                    KLog.d("onError: " + response.message());
                }
            });

        } else {
            String pluginStr = (String) TPUtils.get(PluginApplication.getAppContext(),
                    PluginConstants.PLUGIN_DATA_KEY, "");
            if (TextUtils.isEmpty(pluginStr)) return;
            XMPlugin plugin = (XMPlugin) GsonHelper.fromJson(pluginStr, XMPlugin.class);
            List<PluginInfo> data = new ArrayList<>();
            if (plugin != null && plugin.getData() != null) {
                data = plugin.getData();
            }
            updatePlugin(data);
        }
    }

    @Override
    public void startPluginByPkgName(final String packageName, final String className,
                                     final HashMap<String, String> params,
                                     final IStartPluginListener startPluginListener) {
        ThreadDispatcher.getDispatcher().postLowPriority(new Runnable() {
            @Override
            public void run() {
                //判断是否已安装，已安装直接启动
                if (isPluginInstalled(packageName)) {
                    callBackStartPlugin(intentToPlugin(packageName, className, params),
                            startPluginListener);
                } else {
                    //判断缓存列表中是否已包含，是否已安装，若未包含则通过包名拉取插件数据，再下载安装，
                    // 若包含但未安装成功，则进行下载安装，安装成功后进行跳转。
                    String pluginStr = TPUtils.get(PluginApplication.getAppContext(),
                            PluginConstants.PLUGIN_DATA_KEY, "");
                    XMPlugin plugins = GsonHelper.fromJson(pluginStr, XMPlugin.class);
                    List<PluginInfo> data = new ArrayList<>();
                    if (plugins != null && plugins.getData() != null) {
                        data = plugins.getData();
                    }
                    if (!ListUtils.isEmpty(data)) {
                        for (PluginInfo plugin : data) {
                            if (plugin.getPackageName().equals(packageName)) {
                                if (installPlugin(plugin)) {
                                    callBackStartPlugin(intentToPlugin(plugin.getPackageName(),
                                            plugin.getClassName(), params), startPluginListener);
                                } else {
                                    downloadPlugin(plugin, createDownloadListener(params, startPluginListener));
                                }
                                return;
                            }
                        }
                    }
                    getPluginByPackageName(packageName, createDownloadListener(params, startPluginListener));
                }
            }
        });
    }


    private List<com.qihoo360.replugin.model.PluginInfo> getInstallPluginList() {
        List<com.qihoo360.replugin.model.PluginInfo> pluginInfoList = RePlugin.getPluginInfoList();
        if (pluginInfoList == null || pluginInfoList.size() <= 0) {
            return new ArrayList<>();
        }
        return pluginInfoList;
    }

    /**
     * 下载插件列表
     *
     * @param pluginInfoList
     */
    private void updatePlugin(List<PluginInfo> pluginInfoList) {
        if (pluginInfoList == null || pluginInfoList.size() <= 0) {
            return;
        }
        pluginDownloadManager.downloadPlugin(pluginInfoList, new PluginDownloadListener() {
            @Override
            public void onSuccess(PluginInfo pluginInfo, String path) {
                KLog.d("downloadPlugin success:" + pluginInfo.getPluginName());
            }

            @Override
            public void onFailed(PluginInfo pluginInfo, String path) {

            }
        });
    }

    /**
     * 修复插件，将无效的插件卸载
     *
     * @param result
     */
    private void fixPlugin(String result) {
        XMPlugin plugin = GsonHelper.fromJson(result, XMPlugin.class);
        List<PluginInfo> cacheData = new ArrayList<>();
        if (plugin != null && plugin.getData() != null) {
            cacheData = plugin.getData();
        }
        List<com.qihoo360.replugin.model.PluginInfo> installPlugins = getInstallPluginList();
        for (PluginInfo pluginInfo : cacheData) {
            //卸载过期的
            if (pluginInfo.getExpireDate() <= System.currentTimeMillis()) {
                uninstallPlugin(pluginInfo);
            }
        }
        for (com.qihoo360.replugin.model.PluginInfo pluginInfo : installPlugins) {
            boolean contains = false;
            //卸载列表中不包含的
            for (PluginInfo xmPlugin : cacheData) {
                if (xmPlugin.getPackageName().equals(pluginInfo.getPackageName())) {
                    contains = true;
                    break;
                }
            }
            if (!contains) {
                boolean isInstall = RePlugin.uninstall(pluginInfo.getPackageName());
                KLog.d("unInstall plugin " + pluginInfo.getPackageName() + ", the uninstall result = " + isInstall);
            }
        }
        updatePlugin(cacheData);
    }


    private boolean needRequestToday() {
        return !TPUtils.get(PluginApplication.getAppContext(), PluginConstants.FETCH_PLUGIN_DATE_KEY, "")
                .equals(StringUtil.getDateByYMD());
    }

    /**
     * 根据启动结果回调是否启动成功
     *
     * @param result
     * @param startPluginListener
     */
    private void callBackStartPlugin(final boolean result, final IStartPluginListener startPluginListener) {
        ThreadDispatcher.getDispatcher().postOnMain(new Runnable() {
            @Override
            public void run() {
                if (startPluginListener != null) {
                    if (result) startPluginListener.onSuccess();
                    else startPluginListener.onFailed();
                }
            }
        });
    }

    /**
     * 跳转至插件，并携带参数
     *
     * @param packageName
     * @param className
     * @param params
     * @return
     */
    private boolean intentToPlugin(String packageName, String className, HashMap<String, String> params) {
        Intent intent = RePlugin.createIntent(packageName, className);
        if (params != null && params.size() > 0) {
            Iterator<Map.Entry<String, String>> iterator = params.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, String> entry = iterator.next();
                intent.putExtra(entry.getKey(), entry.getValue());
            }
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return RePlugin.startActivity(PluginApplication.getAppContext(), intent);
    }

    /**
     * 根据包名拉取插件信息，并下载
     *
     * @param packageName
     * @param listener
     */
    private void getPluginByPackageName(String packageName, final PluginDownloadListener listener) {
        PluginTripLogicManager.getInstance().getPluginByPackageName(packageName, new StringCallback() {
            @Override
            public void onSuccess(Response<String> response) {
                final String result = response.body();
                PluginInfo data = GsonHelper.fromJson(result, PluginInfo.class);
                if (data == null) {
                    if (listener != null) {
                        listener.onFailed(null, null);
                    }
                    KLog.d("getPluginByPackageName : the data is null");
                    return;
                }
                downloadPlugin(data, listener);
            }

            @Override
            public void onError(Response<String> response) {
                super.onError(response);
                if (listener != null) {
                    listener.onFailed(null, null);
                }
            }
        });
    }


    private PluginDownloadListener createDownloadListener(final HashMap<String, String> params,
                                                          final IStartPluginListener startPluginListener) {
        return new PluginDownloadListener() {
            @Override
            public void onSuccess(PluginInfo pluginInfo, String path) {
                boolean result = installPlugin(pluginInfo);
                KLog.d("install XMPlugin:" + result);
                if (result) {
                    callBackStartPlugin(intentToPlugin(pluginInfo.getPackageName(), pluginInfo.getClassName(), params), startPluginListener);
                } else {
                    callBackStartPlugin(false, startPluginListener);
                }
            }

            @Override
            public void onFailed(PluginInfo pluginInfo, String path) {
                callBackStartPlugin(false, startPluginListener);
            }
        };
    }
}

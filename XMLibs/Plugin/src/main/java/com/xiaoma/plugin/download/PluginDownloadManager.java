package com.xiaoma.plugin.download;

import android.text.TextUtils;

import com.xiaoma.config.ConfigManager;
import com.xiaoma.plugin.model.PluginInfo;
import com.xiaoma.thread.ThreadDispatcher;
import com.xiaoma.utils.ListUtils;
import com.xiaoma.utils.log.KLog;

import java.io.File;
import java.util.Hashtable;
import java.util.List;

/**
 * Created by vincenthu on 2017/12/7.
 */

public class PluginDownloadManager {

    private static final String FILE_PLUGIN = "plugin";

    private List<PluginInfo> pluginInfoList;
    private PluginDownloadListener pluginDownloadListener;
    private Hashtable<String, DownLoadTask> downLoadTaskHashtable = new Hashtable<>();

    public void downloadPlugin(List<PluginInfo> pluginInfoList, PluginDownloadListener pluginDownloadListener) {
        this.pluginInfoList = pluginInfoList;
        this.pluginDownloadListener = pluginDownloadListener;
        startDownload();
    }


    private void startDownload() {
        if (ListUtils.isEmpty(pluginInfoList)) {
            return;
        }
        final PluginInfo pluginInfo = pluginInfoList.get(0);
        //下载地址为空、过期、已经在下载的不进行下载
        if (pluginInfo == null || TextUtils.isEmpty(pluginInfo.getPluginFilePath()) ||
                pluginInfo.getExpireDate() <= System.currentTimeMillis() || downLoadTaskHashtable.containsKey(pluginInfo.getPluginFilePath())) {
            if (pluginDownloadListener != null) {
                pluginDownloadListener.onFailed(pluginInfo, "");
            }
            pluginInfoList.remove(pluginInfo);
            startDownload();
            return;
        }
        DownLoadTask downLoadTask = new DownLoadTask(pluginInfo.getPluginFilePath(), getPluginPackageDirPath(), pluginInfo.getFileSize(), new DownLoadTask.IDownLoadListen() {
            @Override
            public void onFailed(String filePath) {
                if (pluginDownloadListener != null) {
                    pluginDownloadListener.onFailed(pluginInfo, filePath);
                }
                pluginInfoList.remove(pluginInfo);
                startDownload();
                downLoadTaskHashtable.remove(pluginInfo.getPluginFilePath());
            }

            @Override
            public void onFinished(String filePath) {
                if (pluginDownloadListener != null) {
                    pluginDownloadListener.onSuccess(pluginInfo, filePath);
                }
                pluginInfoList.remove(pluginInfo);
                startDownload();
                downLoadTaskHashtable.remove(pluginInfo.getPluginFilePath());
            }
        });
        downLoadTaskHashtable.put(pluginInfo.getPluginFilePath(), downLoadTask);
        KLog.d("start download plugin " + pluginInfo.getPluginName());
        ThreadDispatcher.getDispatcher().postLowPriority(downLoadTask);
    }

    public void downloadPlugin(final PluginInfo pluginInfo, final PluginDownloadListener pluginDownloadListener) {
        //下载地址为空、过期、已经在下载的不进行下载
        if (pluginInfo == null || TextUtils.isEmpty(pluginInfo.getPluginFilePath()) ||
                pluginInfo.getExpireDate() <= System.currentTimeMillis() || downLoadTaskHashtable.containsKey(pluginInfo.getPluginFilePath())) {
            if (pluginDownloadListener != null) {
                pluginDownloadListener.onFailed(pluginInfo, "");
            }
            return;
        }
        DownLoadTask downLoadTask = new DownLoadTask(pluginInfo.getPluginFilePath(), getPluginPackageDirPath(), pluginInfo.getFileSize(), new DownLoadTask.IDownLoadListen() {

            @Override
            public void onFailed(String filePath) {
                if (pluginDownloadListener != null) {
                    pluginDownloadListener.onFailed(pluginInfo, filePath);
                }
                downLoadTaskHashtable.remove(pluginInfo.getPluginFilePath());
            }

            @Override
            public void onFinished(String filePath) {
                if (pluginDownloadListener != null) {
                    pluginDownloadListener.onSuccess(pluginInfo, filePath);
                }
                downLoadTaskHashtable.remove(pluginInfo.getPluginFilePath());
            }
        });
        downLoadTaskHashtable.put(pluginInfo.getPluginFilePath(), downLoadTask);
        KLog.d("start download plugin " + pluginInfo.getPluginName());
        ThreadDispatcher.getDispatcher().postLowPriority(downLoadTask);
    }

    public String getPluginPackageDirPath() {
        File pluginFolder = ConfigManager.FileConfig.getPluginFolder();
        return pluginFolder.getAbsolutePath();
    }
}

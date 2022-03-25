package com.xiaoma.shop.business.download.impl;

import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import com.xiaoma.config.ConfigManager;
import com.xiaoma.shop.business.download.BaseDownload;
import com.xiaoma.shop.business.download.DownloadCompleteCallback;
import com.xiaoma.shop.business.model.RefreshDataInfo;
import com.xiaoma.shop.business.model.SkinVersionsBean;
import com.xiaoma.shop.common.callback.OnRefreshCallback;
import com.xiaoma.shop.common.constant.ResourceType;

import java.io.File;

/**
 * Created by LKF on 2019-6-20 0020.
 * 皮肤下载
 */
public class SkinDownload extends BaseDownload<SkinVersionsBean> {
    private static final SkinDownload sInstance = new SkinDownload();
    private final DownloadCompleteCallback completeCallback;


    public static SkinDownload getInstance() {
        return sInstance;
    }

    private SkinDownload() {
        completeCallback = new DownloadCompleteCallback(ResourceType.SKIN);
        addDownloadListener(completeCallback);
    }

    @Override
    public String getDownloadDir() {
        return ConfigManager.FileConfig.getShopSkinDownloadFolder().getPath();
    }

    @Override
    protected String getDownloadUrl(SkinVersionsBean skin) {
        return skin != null ? skin.getUrl() : "";
    }

    @Override
    protected String getModelId(SkinVersionsBean skin) {
        return String.valueOf(skin != null ? skin.getId() : "");
    }

    /**
     * 加入皮肤下载队列
     *
     * @return 是否成功加入下载队列
     */
    @Override
    public boolean start(SkinVersionsBean skin) {
        if (!TextUtils.isEmpty(skin.getApkFilePath())) {
            RefreshDataInfo refreshDataInfo = completeCallback.getMaps().get(skin.getApkFilePath());
            if (refreshDataInfo == null) {
                refreshDataInfo = new RefreshDataInfo();
                completeCallback.getMaps().put(skin.getApkFilePath(), refreshDataInfo);
            }
            refreshDataInfo.setId(skin.getId());
            refreshDataInfo.setFilePath(skin.getApkFilePath());
        }
        Log.i(TAG, String.format("start: { name : %s, url : %s }", skin.getAppName(), skin.getUrl()));
        return super.start(skin);
    }

    /**
     * 获取所有已下载的皮肤文件
     */
    @Nullable
    public File[] getAllDownloadFiles() {
        File dir = ConfigManager.FileConfig.getShopSkinDownloadFolder();
        return dir.listFiles();
    }

    public void addRefreshCallback(OnRefreshCallback callback) {
        if (callback == null || completeCallback == null || completeCallback.getCallbacks().contains(callback)) return;
        completeCallback.getCallbacks().add(callback);
    }

    public void removeRefreshCallback(OnRefreshCallback callback) {
        if (callback == null || completeCallback == null || !completeCallback.getCallbacks().contains(callback)) return;
        completeCallback.getCallbacks().remove(callback);
    }

}

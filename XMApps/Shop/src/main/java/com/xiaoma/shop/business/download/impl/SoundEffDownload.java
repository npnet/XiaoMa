package com.xiaoma.shop.business.download.impl;

import android.text.TextUtils;
import com.xiaoma.shop.business.download.BaseDownload;
import com.xiaoma.shop.business.download.DownloadCompleteCallback;
import com.xiaoma.shop.business.model.RefreshDataInfo;
import com.xiaoma.shop.business.model.VehicleSoundEntity;
import com.xiaoma.shop.common.callback.OnRefreshCallback;
import com.xiaoma.shop.common.constant.ResourceType;
import com.xiaoma.shop.common.util.VehicleSoundUtils;

/**
 * Created by LKF on 2019-6-28 0028.
 * 音效下载类,抽象类
 */
public abstract class SoundEffDownload extends BaseDownload<VehicleSoundEntity.SoundEffectListBean> {
    private final DownloadCompleteCallback completeCallback;

    private @ResourceType
    int resType;

    public SoundEffDownload(int resType) {
        this.resType = resType;
        completeCallback = new DownloadCompleteCallback(resType);
        completeCallback.setSaveRecord(true);
        addDownloadListener(completeCallback);
    }

    @Override
    protected String getDownloadUrl(VehicleSoundEntity.SoundEffectListBean soundEff) {
        if (soundEff != null)
            return soundEff.getFilePath();
        return "";
    }

    @Override
    public String getDownloadFileName(VehicleSoundEntity.SoundEffectListBean model) {
        String url = getDownloadUrl(model);
        String fileName = VehicleSoundUtils.getFileName(url);
        return fileName;
    }

    @Override
    protected String getModelId(VehicleSoundEntity.SoundEffectListBean soundEff) {
        if (soundEff != null)
            return String.valueOf(soundEff.getId());
        return "";
    }

    public void addRefreshCallback(OnRefreshCallback callback) {
        if (callback == null || completeCallback == null || completeCallback.getCallbacks().contains(callback)) return;
        completeCallback.getCallbacks().add(callback);
    }

    public void removeRefreshCallback(OnRefreshCallback callback) {
        if (callback == null || completeCallback == null || !completeCallback.getCallbacks().contains(callback)) return;
        completeCallback.getCallbacks().remove(callback);
    }

    @Override
    public boolean start(VehicleSoundEntity.SoundEffectListBean model) {
        if (!TextUtils.isEmpty(model.getFilePath())) {
            RefreshDataInfo refreshDataInfo = completeCallback.getMaps().get(model.getFilePath());
            if (refreshDataInfo == null) {
                refreshDataInfo = new RefreshDataInfo();
                completeCallback.getMaps().put(model.getFilePath(), refreshDataInfo);
            }
            refreshDataInfo.setFilePath(model.getFilePath());
            refreshDataInfo.setId(model.getId());
        }
        return super.start(model);
    }


}

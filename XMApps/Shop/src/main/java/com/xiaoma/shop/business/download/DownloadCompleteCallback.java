package com.xiaoma.shop.business.download;

import android.app.DownloadManager;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import com.xiaoma.model.ResultCallback;
import com.xiaoma.model.XMResult;
import com.xiaoma.shop.business.model.RefreshDataInfo;
import com.xiaoma.shop.common.RequestManager;
import com.xiaoma.shop.common.callback.OnRefreshCallback;
import com.xiaoma.shop.common.constant.ResourceType;
import com.xiaoma.shop.common.util.VehicleSoundDbUtils;
import com.xiaoma.utils.log.KLog;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Author: Ljb
 * Time  : 2019/7/11
 * Description:
 */
public class DownloadCompleteCallback implements DownloadListener {
    private final List<OnRefreshCallback> callbacks = new ArrayList<>();
    private final ConcurrentHashMap<String,RefreshDataInfo> maps = new ConcurrentHashMap<>();

    private @ResourceType int resType;
    private boolean isSaveRecord=false;

    public DownloadCompleteCallback(int resType) {
        this.resType = resType;
    }


    public void setSaveRecord(boolean saveRecord) {
        isSaveRecord = saveRecord;
    }

    @Override
    public void onDownloadStatus(@Nullable DownloadStatus downloadStatus) {
        if(downloadStatus == null || TextUtils.isEmpty(downloadStatus.downUrl)) return;
        if (downloadStatus.status == DownloadManager.STATUS_FAILED) {// 下载失败
            String fileUrl = downloadStatus.downUrl;
            RefreshDataInfo refreshDataInfo = maps.get(fileUrl);
            if (refreshDataInfo == null) return;
            maps.remove(refreshDataInfo);
        }else if (downloadStatus.status == DownloadManager.STATUS_SUCCESSFUL) {//下载成功
            String fileUrl = downloadStatus.downUrl;
            RefreshDataInfo refreshDataInfo = maps.get(fileUrl);
            if (refreshDataInfo == null) return;
            // 上报下载成功
            submitDownload(refreshDataInfo.getId(), refreshDataInfo.getFilePath());
            // 保存下载记录
            if (isSaveRecord) {
                boolean isDeleted = VehicleSoundDbUtils.saveAndDeleteExpiredRecords(refreshDataInfo.getId(), refreshDataInfo.getFilePath(), resType);
                if (isDeleted) {//如果有删除，全局刷新，更新其他Button 状态
                    for (OnRefreshCallback callback : callbacks) {
                        callback.onRefreshAll();
                    }
                }
            }
            maps.remove(refreshDataInfo);
        }
    }


    private void submitDownload(final long id, final String filePath) {
        KLog.i("filOut| "+"[submitDownload]->提交下载次数  resType: " +resType);
        RequestManager.addUseNum(resType, id, new ResultCallback<XMResult<String>>() {
            @Override
            public void onSuccess(XMResult<String> result) {
                KLog.i("filOut| "+"[onSuccess]->提交成功");
                for (OnRefreshCallback callback : callbacks) {
                    callback.onSingleRefresh(id, filePath);
                }
            }

            @Override
            public void onFailure(int code, String msg) {
            }
        });
    }

    public List<OnRefreshCallback> getCallbacks() {
        return callbacks;
    }

    public ConcurrentHashMap<String, RefreshDataInfo> getMaps() {
        return maps;
    }
}

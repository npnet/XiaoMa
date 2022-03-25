package com.xiaoma.pet.common.callback;

import com.xiaoma.network.model.Progress;
import com.xiaoma.network.okserver.download.DownloadListener;
import com.xiaoma.utils.log.KLog;


/**
 * <pre>
 *       @author Created by Gillben
 *       date: 2019/4/8 0008 18:11
 *       desc：宠物资源下载回调
 * </pre>
 */
public abstract class OnDownLoadCallback extends DownloadListener {

    public OnDownLoadCallback(Object tag) {
        super(tag);
    }

    @Override
    public void onError(Progress progress) {
        Throwable throwable = progress.exception;
        KLog.e(throwable.getMessage());
    }


    @Override
    public void onRemove(Progress progress) {
        KLog.w(progress.tag);
    }
}

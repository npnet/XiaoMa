package com.xiaoma.shop.business.adapter;

import com.xiaoma.shop.business.model.SkusBean;

/**
 * Created by LKF on 2019-7-1 0001.
 */
public interface TTSAdapterCallback {
    /**
     * @return 当前item是否正在试听
     */
    boolean isAuditionPlaying(SkusBean item);

    /**
     * 开始试听
     */
    void startAudition(SkusBean item);

    /**
     * 停止试听
     */
    void stopAudition(SkusBean item);

    /**
     * 购买
     */
    void onBuy(SkusBean item);

    /**
     * 使用音色
     */
    boolean onUseTTS(SkusBean item);

    /**
     * 下载中
     */
    void onDownloading(SkusBean item);

    /**
     * 下载
     */
    void onDownload(SkusBean item);
}

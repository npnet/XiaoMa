package com.xiaoma.xting.sdk;

import com.xiaoma.xting.sdk.bean.XMAdvertis;
import com.xiaoma.xting.sdk.bean.XMAdvertisList;
import com.xiaoma.xting.sdk.bean.XMPlayableModel;

/**
 * @author youthyJ
 * @date 2018/10/18
 */
public interface PlayerStatusListener {
    // 普通节目状态变化 ↓
    void onPlayStart();

    void onPlayPause();

    void onPlayStop();

    void onSoundPlayComplete();

    void onSoundPrepared();

    void onSoundSwitch(XMPlayableModel curSound);

    void onBufferingStart();

    void onBufferingStop();

    void onBufferProgress(int percent);

    void onPlayProgress(int currPos, int duration);

    void onError(Exception exception);
    // 普通节目状态变化 ↑

    // 广告状态变化 ↓
    void onStartGetAdsInfo();

    void onGetAdsInfo(XMAdvertisList ads);

    void onAdsStartBuffering();

    void onAdsStopBuffering();

    void onStartPlayAds(XMAdvertis ad, int position);

    void onCompletePlayAds();

    void onError(int what, int extra);
    // 广告状态变化 ↑
}

package com.xiaoma.xting.koala.contract;

import com.xiaoma.xting.koala.bean.XMPlayItem;

/**
 * <des>
 *
 * @author YangGang
 * @date 2019/6/10
 */
public interface IKoalaPlayerCallback<T> {

    //播放状态的监听
    void onIdle(XMPlayItem var);

    void onPlayerPreparing(XMPlayItem var);

    void onPlayerPlaying(XMPlayItem var);

    void onPlayerPaused(XMPlayItem var);

    void onProgress(String url, int position, int duration, boolean isPreDownloadComplete);

    void onPlayerFailed(XMPlayItem var, int var2, int var3);

    void onPlayerEnd(XMPlayItem var);

    void onSeekStart(String url);

    void onSeekComplete(String url);

    void onBufferingStart(XMPlayItem var);

    void onBufferingEnd(XMPlayItem var);

    //    播放碎片
    void onGeneralResult(T t);

    void onGeneralError(int var);

    void onGeneralException(Throwable var);
}

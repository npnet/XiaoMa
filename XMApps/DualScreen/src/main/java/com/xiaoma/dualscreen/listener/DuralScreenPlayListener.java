package com.xiaoma.dualscreen.listener;

import com.xiaoma.player.AudioConstants;
import com.xiaoma.player.AudioInfo;
import com.xiaoma.player.ProgressInfo;

/**
 * 桌面控制监听
 * <p>
 * Created by zhushi.
 * Date: 2019/1/24
 */
public interface DuralScreenPlayListener {
    /**
     * 播放进度
     *
     * @param progressInfo
     */
    void onProgress(ProgressInfo progressInfo);

    /**
     * 音频
     *
     * @param audioInfo
     */
    void onAudioInfo(AudioInfo audioInfo);

    /**
     * 播放状态
     *
     * @param playState
     */
    void onPlayState(int playState);

    /**
     * source 区分数据来源是接口还是喜马拉雅
     */
    void onDataSource(@AudioConstants.OnlineInfoSource int dataSource);

    /**
     * 收藏
     *
     * @param favorite
     */
    void onAudioFavorite(boolean favorite);
//
//    /**
//     * 播放模式
//     *
//     * @param playMode
//     */
//    void onPlayMode(int playMode);
}

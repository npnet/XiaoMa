package com.xiaoma.launcher.player.callback;

/**
 * 播放器音频控制监听
 * Created by zhushi.
 * Date: 2019/2/22
 */
public interface OnPlayControlListener {

    /**
     * 上一曲
     *
     * @param audioType 音源类型
     */
    void onPre(int audioType);

    /**
     * 下一曲
     *
     * @param audioType 音源类型
     */
    void onNext(int audioType);

    /**
     * 暂停、播放
     *
     * @param audioType        音源类型
     * @param currentPlayState 当前播放状态
     */
    void onPlayOrPause(int audioType, int currentPlayState);

    /**
     * 进入音频列表
     *
     * @param currentPlayState 当前播放状态
     */
    void onStartListActivity(int currentPlayState);

    /**
     * 收藏、取消收藏
     *
     * @param audioType
     */
    void onFavorite(int audioType, boolean favoriteState);
}

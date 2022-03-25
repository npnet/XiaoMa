package com.xiaoma.xting.common.playerSource.control;

import android.content.Context;

import com.xiaoma.xting.common.playerSource.contract.PlayerOperate;
import com.xiaoma.xting.common.playerSource.contract.PlayerPlayMode;
import com.xiaoma.xting.common.playerSource.contract.PlayerSourceType;
import com.xiaoma.xting.common.playerSource.info.model.PlayerInfo;

import java.util.List;

/**
 * <des>
 * 常规操作的封装
 *
 * @author YangGang
 * @date 2019/6/4
 */
public interface IPlayerControl<T> {

    /**
     * 初始化播放器
     *
     * @param context appContext
     */
    void init(Context context);

    /**
     * 判断是否正在播放
     */
    boolean isPlaying();

    /**
     * 判断播放器中是否有播放数据
     */
    boolean isCurPlayerInfoAlive();

    /**
     * 播放
     *
     * @return {@link PlayerOperate}
     */
    @PlayerOperate
    int play();

    @PlayerOperate
    int pause();

    @PlayerOperate
    int pause(boolean abandonFocus);

    void stop();
    /**
     * 调用播放数据源的Bean类
     *
     * @param t 数据源播放Bean类
     * @return
     */
    @PlayerOperate
    int playWithModel(T t);

    /**
     * 播放列表中位于index的节目
     *
     * @param index 播放条目所在index
     * @return
     */
    @PlayerOperate
    int playWithIndex(int index);

    @PlayerOperate
    int playPre();

    @PlayerOperate
    int playNext();

    @PlayerOperate
    int seekProgress(long progress);

    /**
     * 用于进行收藏
     */
    @PlayerOperate
    int subscribe(boolean subscribe);

    /**
     * 设置播放模式
     *
     * @param mode
     */
    @PlayerOperate
    int setPlayMode(@PlayerPlayMode int mode);

    /**
     * @return 获取当前正在播放的节目位置
     */
    int getPlayIndex();

    int getPlayListSize();

    /**
     * @return 播放器中的播放列表
     */
    List<PlayerInfo> getPlayList();

    PlayerInfo getCurPlayerInfo();

    void setVoiceScale(float voiceScale);

    /**
     * 统一播放源中进行节目切换时调用
     */
    void switchPlayerAlbum(T t);

    /**
     * 切换数据源的时候调用
     */
    void switchPlayerSource(@PlayerSourceType int type);

    /**
     * 退出播放器
     */
    void exitPlayer();
}

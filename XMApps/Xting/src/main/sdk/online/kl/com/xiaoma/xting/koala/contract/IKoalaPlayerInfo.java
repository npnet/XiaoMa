package com.xiaoma.xting.koala.contract;

import android.content.Context;

import com.xiaoma.xting.common.playerSource.info.model.PlayerInfo;

import java.util.List;

/**
 * <des>
 *
 * @author YangGang
 * @date 2019/6/10
 */
public interface IKoalaPlayerInfo {

    void init(Context context);

    void enablePlayer();

    void disablePlayer();

    boolean isCurPlayerInfoAlive();

    boolean hasPre();

    boolean hasNext();

    boolean isPlaying();

    boolean isPaused();

    boolean isPlayerEnable();

    boolean isPlayerFailed();

    boolean canReStartPlayer();

    PlayerInfo getCurPlayerInfo();

    int getPlayIndex();

    int getCurPosition();

    List<PlayerInfo> getPlayList();

    int getPlayListSize();

    void addPlayerStateListener();

    void removePlayerStateListener();

    boolean isPGCRadio();
}

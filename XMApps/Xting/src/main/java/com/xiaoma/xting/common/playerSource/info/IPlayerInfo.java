package com.xiaoma.xting.common.playerSource.info;

import com.xiaoma.xting.common.playerSource.contract.PlayerStatus;
import com.xiaoma.xting.common.playerSource.info.model.PlayerInfo;

/**
 * <des>
 *
 * @author YangGang
 * @date 2019/5/27
 */
public interface IPlayerInfo {

    void onPlayerInfoChanged(PlayerInfo playerInfo);

    void onPlayerStatusChanged(@PlayerStatus int status);

    void onPlayerProgress(long progress, long duration);

    void onProgramSubscribeChanged(boolean subscribe);

}

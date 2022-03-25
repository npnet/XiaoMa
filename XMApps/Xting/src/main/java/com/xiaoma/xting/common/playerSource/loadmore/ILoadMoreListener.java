package com.xiaoma.xting.common.playerSource.loadmore;

import com.xiaoma.xting.common.playerSource.info.model.PlayerInfo;

import java.util.List;

/**
 * <des>
 *
 * @author YangGang
 * @date 2019/6/3
 */
public interface ILoadMoreListener {

    void notifyLoadMoreResult(boolean isDownload, int loadMoreStatus, List<PlayerInfo> list);
}

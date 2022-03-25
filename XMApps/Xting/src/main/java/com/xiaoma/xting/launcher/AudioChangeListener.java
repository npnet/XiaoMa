package com.xiaoma.xting.launcher;

import com.xiaoma.xting.sdk.bean.XMPlayableModel;

/**
 * <des>
 *
 * @author YangGang
 * @date 2019/1/23
 */
public interface AudioChangeListener {

    void onFavoriteChanged(boolean favorite);

    void onPlayListChanged(XMPlayableModel bean);
}

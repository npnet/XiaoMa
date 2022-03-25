package com.xiaoma.music.kuwo.handler;

import com.xiaoma.music.kuwo.listener.OnAudioFetchListener;
import com.xiaoma.music.kuwo.model.XMAlbumInfo;
import com.xiaoma.music.kuwo.model.XMBaseOnlineSection;
import com.xiaoma.utils.ListUtils;

import java.util.ArrayList;
import java.util.List;

import cn.kuwo.base.bean.online.BaseOnlineSection;
import cn.kuwo.base.bean.quku.AlbumInfo;
import cn.kuwo.base.bean.quku.BaseQukuItem;

/**
 * Author: loren
 * Date: 2018/10/24 0024
 */

public class AlbumHandler implements IHandler {
    @Override
    public void handle(OnAudioFetchListener listener, List<XMBaseOnlineSection> list) {
        if (ListUtils.isEmpty(list)) {
            listener.onFetchFailed("data is empty");
            return;
        }
        List<XMAlbumInfo> albumList = new ArrayList<>();
        for (XMBaseOnlineSection xmBaseOnlineSection : list) {
            if (xmBaseOnlineSection == null || xmBaseOnlineSection.getSDKBean() == null) {
                continue;
            }
            BaseOnlineSection sdkBean = xmBaseOnlineSection.getSDKBean();
            List<BaseQukuItem> onlineInfoList = sdkBean.getOnlineInfos();
            if (ListUtils.isEmpty(onlineInfoList)) {
                continue;
            }
            for (BaseQukuItem item : onlineInfoList) {
                if (item == null) {
                    continue;
                }
                final AlbumInfo albumInfo = (AlbumInfo) item;
                albumList.add(new XMAlbumInfo(albumInfo));
            }
        }

        listener.onFetchSuccess(albumList);
    }
}

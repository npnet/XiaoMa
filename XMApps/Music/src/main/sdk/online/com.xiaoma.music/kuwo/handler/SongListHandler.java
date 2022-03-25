package com.xiaoma.music.kuwo.handler;

import com.xiaoma.music.kuwo.listener.OnAudioFetchListener;
import com.xiaoma.music.kuwo.model.XMBaseOnlineSection;
import com.xiaoma.music.kuwo.model.XMSongListInfo;
import com.xiaoma.utils.ListUtils;

import java.util.ArrayList;
import java.util.List;

import cn.kuwo.base.bean.online.BaseOnlineSection;
import cn.kuwo.base.bean.quku.BaseQukuItem;
import cn.kuwo.base.bean.quku.SongListInfo;

/**
 * Created by ZYao.
 * Date ：2018/10/23 0023
 */
class SongListHandler implements IHandler {
    @Override
    public void handle(OnAudioFetchListener listener, List<XMBaseOnlineSection> list) {
        if (ListUtils.isEmpty(list)) {
            listener.onFetchFailed("data is empty");
            return;
        }
        List<XMSongListInfo> xmSongListInfos = new ArrayList<>();
        for (XMBaseOnlineSection xmBaseOnlineSection : list) {
            if (xmBaseOnlineSection == null || xmBaseOnlineSection.getSDKBean() == null) {
                continue;
            }
            BaseOnlineSection sdkBean = xmBaseOnlineSection.getSDKBean();
            List<BaseQukuItem> onlineInfos = sdkBean.getOnlineInfos();
            if (ListUtils.isEmpty(onlineInfos)) {
                continue;
            }
            for (BaseQukuItem item : onlineInfos) {
                if (item == null) {
                    continue;
                }
                try {
                    xmSongListInfos.add(new XMSongListInfo((SongListInfo) item));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        listener.onFetchSuccess(xmSongListInfos);
    }
}

package com.xiaoma.music.kuwo.handler;

import com.xiaoma.music.kuwo.listener.OnAudioFetchListener;
import com.xiaoma.music.kuwo.model.XMBaseOnlineSection;
import com.xiaoma.music.kuwo.model.XMMusic;
import com.xiaoma.utils.ListUtils;
import com.xiaoma.utils.log.KLog;

import java.util.ArrayList;
import java.util.List;

import cn.kuwo.base.bean.online.BaseOnlineSection;
import cn.kuwo.base.bean.quku.BaseQukuItem;
import cn.kuwo.base.bean.quku.MusicInfo;

/**
 * Created by ZYao.
 * Date ：2018/10/17 0017
 */
class MusicHandler implements IHandler {
    @Override
    public void handle(OnAudioFetchListener listener, List<XMBaseOnlineSection> list) {
        if (ListUtils.isEmpty(list)) {
            listener.onFetchFailed("data is empty");
            return;
        }
        List<XMMusic> musicList = new ArrayList<>();
        for (XMBaseOnlineSection xmBaseOnlineSection : list) {
            if (xmBaseOnlineSection == null || xmBaseOnlineSection.getSDKBean() == null) {
                continue;
            }
            BaseOnlineSection sdkBean = xmBaseOnlineSection.getSDKBean();
            if (sdkBean == null) {
                continue;
            }
            List<BaseQukuItem> onlineInfoList = sdkBean.getOnlineInfos();
            if (ListUtils.isEmpty(onlineInfoList)) {
                continue;
            }
            for (BaseQukuItem item : onlineInfoList) {
                if (item == null) {
                    continue;
                }
                final MusicInfo musicInfo = (MusicInfo) item;
                if (musicInfo.getMusic() == null){
                    continue;
                }
                musicList.add(new XMMusic(musicInfo.getMusic()));
            }
        }

        listener.onFetchSuccess(musicList);
    }
}

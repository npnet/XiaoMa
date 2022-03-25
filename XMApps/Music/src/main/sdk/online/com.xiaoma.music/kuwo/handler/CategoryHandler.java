package com.xiaoma.music.kuwo.handler;

import com.xiaoma.music.kuwo.listener.OnAudioFetchListener;
import com.xiaoma.music.kuwo.model.XMBaseOnlineSection;
import com.xiaoma.music.kuwo.model.XMBaseQukuItem;
import com.xiaoma.utils.ListUtils;

import java.util.ArrayList;
import java.util.List;

import cn.kuwo.base.bean.online.BaseOnlineSection;
import cn.kuwo.base.bean.quku.BaseQukuItem;

/**
 * Created by ZYao.
 * Date ：2018/10/22 0022
 */
class CategoryHandler implements IHandler {
    @Override
    public void handle(OnAudioFetchListener listener, List<XMBaseOnlineSection> list) {
        if (ListUtils.isEmpty(list)) {
            listener.onFetchFailed("data is empty");
            return;
        }
        List<XMBaseQukuItem> xmBaseQukuItems = new ArrayList<>();
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
                xmBaseQukuItems.add(new XMBaseQukuItem(item));
            }
        }
        listener.onFetchSuccess(xmBaseQukuItems);
    }
}

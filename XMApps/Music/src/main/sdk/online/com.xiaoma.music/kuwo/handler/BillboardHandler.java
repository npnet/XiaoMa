package com.xiaoma.music.kuwo.handler;

import com.xiaoma.music.kuwo.listener.OnAudioFetchListener;
import com.xiaoma.music.kuwo.model.XMBaseOnlineSection;
import com.xiaoma.music.kuwo.model.XMBillboardInfo;
import com.xiaoma.utils.ListUtils;

import java.util.ArrayList;
import java.util.List;

import cn.kuwo.base.bean.online.BaseOnlineSection;
import cn.kuwo.base.bean.quku.BaseQukuItem;
import cn.kuwo.base.bean.quku.BillboardInfo;

/**
 * Created by ZYao.
 * Date ：2018/10/17 0017
 */
public class BillboardHandler implements IHandler {
    @Override
    public void handle(OnAudioFetchListener listener, List<XMBaseOnlineSection> list) {
        if (ListUtils.isEmpty(list)) {
            listener.onFetchFailed("data is empty");
            return;
        }
        List<XMBillboardInfo> infoList = new ArrayList<>();
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
                BillboardInfo billboardInfo = (BillboardInfo) item;
                infoList.add(new XMBillboardInfo(billboardInfo));
            }
        }
        listener.onFetchSuccess(infoList);
    }
}

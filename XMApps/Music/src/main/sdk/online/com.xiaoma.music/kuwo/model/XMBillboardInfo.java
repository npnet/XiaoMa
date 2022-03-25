package com.xiaoma.music.kuwo.model;

import com.xiaoma.adapter.base.XMBean;

import java.util.ArrayList;
import java.util.List;

import cn.kuwo.base.bean.quku.BillboardInfo;
import cn.kuwo.base.bean.quku.TabInfo;

/**
 * Created by ZYao.
 * Date ：2018/10/15 0015
 */
public class XMBillboardInfo extends XMBean<BillboardInfo> {
    public XMBillboardInfo(BillboardInfo billboardInfo) {
        super(billboardInfo);
    }

    public int getRadioId() {
        return getSDKBean().getRadioId();
    }

    public void setRadioId(String id) {
        getSDKBean().setRadioId(id);
    }

    public List<XMTabInfo> getTabList() {
        List<TabInfo> tabList = getSDKBean().getTabList();
        List<XMTabInfo> xmTabInfos = new ArrayList<>();
        for (TabInfo tabInfo : tabList) {
            xmTabInfos.add(new XMTabInfo(tabInfo));
        }
        return xmTabInfos;
    }

}

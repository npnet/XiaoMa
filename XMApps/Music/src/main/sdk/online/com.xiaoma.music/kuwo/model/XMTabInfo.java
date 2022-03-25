package com.xiaoma.music.kuwo.model;

import com.xiaoma.adapter.base.XMBean;

import cn.kuwo.base.bean.quku.TabInfo;

/**
 * Created by ZYao.
 * Date ：2018/10/15 0015
 */
public class XMTabInfo extends XMBean<TabInfo> {
    public XMTabInfo(TabInfo tabInfo) {
        super(tabInfo);
    }

    public void setType(String type) {
        getSDKBean().setType(type);
    }

    public String getType() {
        return getSDKBean().getType();
    }

    public int getRadioId() {
        return getSDKBean().getRadioId();
    }

    public void setRadioId(int id) {
        getSDKBean().setRadioId(id);
    }
}

package com.xiaoma.music.kuwo.model;

import com.xiaoma.adapter.base.XMBean;

import java.util.ArrayList;
import java.util.List;

import cn.kuwo.base.bean.online.BaseOnlineSection;
import cn.kuwo.base.bean.online.OnlineRootInfo;

/**
 * Created by ZYao.
 * Date ：2018/10/15 0015
 */
public class XMOnlineRootInfo extends XMBean<OnlineRootInfo> {
    public XMOnlineRootInfo(OnlineRootInfo onlineRootInfo) {
        super(onlineRootInfo);
    }

    public void add(XMBaseOnlineSection var1) {
        getSDKBean().add(var1.getSDKBean());
    }

    public List<XMBaseOnlineSection> getOnlineSections() {
        List<BaseOnlineSection> onlineSections = getSDKBean().getOnlineSections();
        List<XMBaseOnlineSection> xmBaseOnlineSections = new ArrayList<>();
        for (BaseOnlineSection onlineSection : onlineSections) {
            if (onlineSection == null) {
                continue;
            }
            xmBaseOnlineSections.add(new XMBaseOnlineSection(onlineSection));
        }
        return xmBaseOnlineSections;
    }

    public XMBaseOnlineSection getFirstSection() {
        final BaseOnlineSection firstSection = getSDKBean().getFirstSection();
        return new XMBaseOnlineSection(firstSection);
    }

    public XMBaseOnlineSection getLastSection() {
        final BaseOnlineSection lastSection = getSDKBean().getLastSection();
        return new XMBaseOnlineSection(lastSection);
    }


}

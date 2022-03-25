package com.xiaoma.music.kuwo.model;

import com.xiaoma.adapter.base.XMBean;

import cn.kuwo.base.bean.NetResource;

/**
 * Created by ZYao.
 * Date ：2018/10/16 0016
 */
public class XMNetResource extends XMBean<NetResource> {
    public XMNetResource(NetResource netResource) {
        super(netResource);
    }

    public boolean isEQ() {
        return getSDKBean().isEQ();
    }

    public boolean isFLAC() {
        return getSDKBean().isFLAC();
    }
}

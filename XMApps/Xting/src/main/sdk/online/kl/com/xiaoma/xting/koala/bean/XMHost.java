package com.xiaoma.xting.koala.bean;

import com.kaolafm.opensdk.api.media.model.Host;
import com.xiaoma.adapter.base.XMBean;

/**
 * <des>
 *
 * @author YangGang
 * @date 2019/6/10
 */
public class XMHost extends XMBean<Host> {

    public XMHost(Host host) {
        super(host);
    }

    public String getName() {
        return getSDKBean().getName();
    }

    public void setName(String name) {
        getSDKBean().setName(name);
    }

    public String getDes() {
        return getSDKBean().getDes();
    }

    public void setDes(String des) {
        getSDKBean().setDes(des);
    }

    public String getImg() {
        return getSDKBean().getImg();
    }

    public void setImg(String img) {
        getSDKBean().setImg(img);
    }

    @Override
    public String toString() {
        return getSDKBean().toString();
    }
}

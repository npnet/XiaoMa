package com.xiaoma.adapter.base;

import java.io.Serializable;

/**
 * Created by youthyj on 2018/9/21.
 */
public class XMBean<SDKBean> implements Serializable {
    private SDKBean sdkBean;

    public XMBean(SDKBean sdkBean) {
        this.sdkBean = sdkBean;
    }

    public SDKBean getSDKBean() {
        return sdkBean;
    }
}

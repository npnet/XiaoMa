package com.xiaoma.adapter.base;

/**
 * Created by youthyj on 2018/9/19.
 */
public interface ISDKCallbackConvert<XMBean extends com.xiaoma.adapter.base.XMBean> {
    void onCallback(XMBean bean);
}

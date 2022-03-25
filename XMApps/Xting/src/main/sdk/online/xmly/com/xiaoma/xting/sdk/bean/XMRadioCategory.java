package com.xiaoma.xting.sdk.bean;

import com.xiaoma.adapter.base.XMBean;
import com.ximalaya.ting.android.opensdk.model.live.radio.RadioCategory;

/**
 * @author youthyJ
 * @date 2018/10/13
 */
public class XMRadioCategory extends XMBean<RadioCategory> {
    public XMRadioCategory(RadioCategory radioCategory) {
        super(radioCategory);
    }

    public long getId() {
        return getSDKBean().getId();
    }

    public void setId(long id) {
        getSDKBean().setId(id);
    }

    public String getKind() {
        return getSDKBean().getKind();
    }

    public void setKind(String kind) {
        getSDKBean().setKind(kind);
    }

    public String getRadioCategoryName() {
        return getSDKBean().getRadioCategoryName();
    }

    public void setRadioCategoryName(String radioCategoryName) {
        getSDKBean().setRadioCategoryName(radioCategoryName);
    }

    public int getOrderNum() {
        return getSDKBean().getOrderNum();
    }

    public void setOrderNum(int orderNum) {
        getSDKBean().setOrderNum(orderNum);
    }
}

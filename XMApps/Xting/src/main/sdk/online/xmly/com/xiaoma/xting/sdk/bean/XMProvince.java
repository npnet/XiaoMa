package com.xiaoma.xting.sdk.bean;

import com.xiaoma.adapter.base.XMBean;
import com.ximalaya.ting.android.opensdk.model.live.provinces.Province;

/**
 * @author youthyJ
 * @date 2018/10/13
 */
public class XMProvince extends XMBean<Province> {
    public XMProvince(Province province) {
        super(province);
    }

    public long getProvinceId() {
        return getSDKBean().getProvinceId();
    }

    public void setProvinceId(long provinceId) {
        getSDKBean().setProvinceId(provinceId);
    }

    public long getProvinceCode() {
        return getSDKBean().getProvinceCode();
    }

    public void setProvinceCode(long provinceCode) {
        getSDKBean().setProvinceCode(provinceCode);
    }

    public String getProvinceName() {
        return getSDKBean().getProvinceName();
    }

    public void setProvinceName(String provinceName) {
        getSDKBean().setProvinceName(provinceName);
    }

    public long getCreatedAt() {
        return getSDKBean().getCreatedAt();
    }

    public void setCreatedAt(long createdAt) {
        getSDKBean().setCreatedAt(createdAt);
    }
}

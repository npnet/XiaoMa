package com.xiaoma.xting.sdk.bean;

import com.xiaoma.adapter.base.XMBean;
import com.ximalaya.ting.android.opensdk.model.live.radio.City;

/**
 * @author youthyJ
 * @date 2018/10/13
 */
public class XMCity extends XMBean<City> {
    public XMCity(City city) {
        super(city);
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

    public int getCityCode() {
        return getSDKBean().getCityCode();
    }

    public void setCityCode(int cityCode) {
        getSDKBean().setCityCode(cityCode);
    }

    public String getCityName() {
        return getSDKBean().getCityName();
    }

    public void setCityName(String cityName) {
        getSDKBean().setCityName(cityName);
    }

}

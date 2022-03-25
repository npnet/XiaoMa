package com.xiaoma.xting.sdk.bean;

import com.xiaoma.adapter.base.XMBean;
import com.ximalaya.ting.android.opensdk.model.live.radio.City;
import com.ximalaya.ting.android.opensdk.model.live.radio.CityList;

import java.util.ArrayList;
import java.util.List;

/**
 * @author youthyJ
 * @date 2018/10/13
 */
public class XMCityList extends XMBean<CityList> {
    public XMCityList(CityList cityList) {
        super(cityList);
    }

    public List<XMCity> getCities() {
        List<City> cities = getSDKBean().getCities();
        List<XMCity> xmCities = new ArrayList<>();
        if (cities != null && !cities.isEmpty()) {
            for (City city : cities) {
                if (city == null) {
                    continue;
                }
                xmCities.add(new XMCity(city));
            }
        }
        return xmCities;
    }

    public void setCities(List<XMCity> xmCities) {
        if (xmCities == null) {
            getSDKBean().setCities(null);
            return;
        }
        List<City> cities = new ArrayList<>();
        for (XMCity xmCity : xmCities) {
            if (xmCity == null) {
                continue;
            }
            cities.add(xmCity.getSDKBean());
        }
        getSDKBean().setCities(cities);
    }
}
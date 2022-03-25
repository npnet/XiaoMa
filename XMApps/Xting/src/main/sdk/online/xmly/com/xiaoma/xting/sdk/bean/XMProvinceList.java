package com.xiaoma.xting.sdk.bean;

import com.xiaoma.adapter.base.XMBean;
import com.ximalaya.ting.android.opensdk.model.live.provinces.Province;
import com.ximalaya.ting.android.opensdk.model.live.provinces.ProvinceList;

import java.util.ArrayList;
import java.util.List;

/**
 * @author youthyJ
 * @date 2018/10/13
 */
public class XMProvinceList extends XMBean<ProvinceList> {
    public XMProvinceList(ProvinceList provinceList) {
        super(provinceList);
    }

    public List<XMProvince> getProvinceList() {
        List<Province> provinceList = getSDKBean().getProvinceList();
        List<XMProvince> xmProvinces = new ArrayList<>();
        if (provinceList != null && !provinceList.isEmpty()) {
            for (Province province : provinceList) {
                if (province == null) {
                    continue;
                }
                xmProvinces.add(new XMProvince(province));
            }
        }
        return xmProvinces;
    }

    public void setProvinceList(List<XMProvince> xmProvinceList) {
        if (xmProvinceList == null) {
            getSDKBean().setProvinceList(null);
            return;
        }
        List<Province> provinceList = new ArrayList<>();
        for (XMProvince xmProvince : xmProvinceList) {
            if (xmProvince == null) {
                continue;
            }
            provinceList.add(xmProvince.getSDKBean());
        }
        getSDKBean().setProvinceList(provinceList);
    }
}

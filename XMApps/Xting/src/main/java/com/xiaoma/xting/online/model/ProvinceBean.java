package com.xiaoma.xting.online.model;

import com.xiaoma.xting.sdk.bean.XMProvince;
import com.xiaoma.xting.sdk.bean.XMProvinceList;
import com.ximalaya.ting.android.opensdk.model.live.provinces.Province;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author KY
 * @date 2018/11/1
 */
public class ProvinceBean extends XMProvince implements Serializable, INamed {

    public ProvinceBean(Province province) {
        super(province);
    }

    public static List<ProvinceBean> convertFromProvince(XMProvinceList provinceList) {
        List<ProvinceBean> provinceBeans = new ArrayList<>(provinceList.getProvinceList().size());
        for (XMProvince xmProvince : provinceList.getProvinceList()) {
            provinceBeans.add(new ProvinceBean(xmProvince.getSDKBean()));
        }
        return provinceBeans;
    }

    @Override
    public String getName() {
        return getProvinceName();
    }
}

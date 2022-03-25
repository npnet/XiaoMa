package com.xiaoma.xting.sdk.bean;

import com.xiaoma.adapter.base.XMBean;
import com.ximalaya.ting.android.opensdk.model.live.radio.RadioCategory;
import com.ximalaya.ting.android.opensdk.model.live.radio.RadioCategoryList;

import java.util.ArrayList;
import java.util.List;

/**
 * @author youthyJ
 * @date 2018/10/13
 */
public class XMRadioCategoryList extends XMBean<RadioCategoryList> {
    public XMRadioCategoryList(RadioCategoryList radioCategoryList) {
        super(radioCategoryList);
    }

    public List<XMRadioCategory> getRadioCategories() {
        List<RadioCategory> radioCategories = getSDKBean().getRadioCategories();
        List<XMRadioCategory> xmRadioCategories = new ArrayList<>();
        if (radioCategories != null && !radioCategories.isEmpty()) {
            for (RadioCategory radioCategory : radioCategories) {
                if (radioCategory == null) {
                    continue;
                }
                xmRadioCategories.add(new XMRadioCategory(radioCategory));
            }
        }
        return xmRadioCategories;
    }

    public void setRadioCategories(List<XMRadioCategory> xmRadioCategories) {
        if (xmRadioCategories == null) {
            getSDKBean().setRadioCategories(null);
            return;
        }
        List<RadioCategory> radioCategories = new ArrayList<>();
        for (XMRadioCategory xmRadioCategory : xmRadioCategories) {
            if (xmRadioCategory == null) {
                continue;
            }
            radioCategories.add(xmRadioCategory.getSDKBean());
        }
        getSDKBean().setRadioCategories(radioCategories);
    }
}
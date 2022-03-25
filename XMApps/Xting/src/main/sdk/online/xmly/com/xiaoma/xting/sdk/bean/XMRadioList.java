package com.xiaoma.xting.sdk.bean;

import com.xiaoma.adapter.base.XMBean;
import com.ximalaya.ting.android.opensdk.model.live.radio.Radio;
import com.ximalaya.ting.android.opensdk.model.live.radio.RadioList;

import java.util.ArrayList;
import java.util.List;

/**
 * @author youthyJ
 * @date 2018/10/13
 */
public class XMRadioList extends XMBean<RadioList> {
    public XMRadioList(RadioList radioList) {
        super(radioList);
    }

    public int getTotalPage() {
        return getSDKBean().getTotalPage();
    }

    public void setTotalPage(int totalPage) {
        getSDKBean().setTotalPage(totalPage);
    }

    public int getTotalCount() {
        return getSDKBean().getTotalCount();
    }

    public void setTotalCount(int totalCount) {
        getSDKBean().setTotalCount(totalCount);
    }

    public int getCurrentPage() {
        return getSDKBean().getCurrentPage();
    }

    public void setCurrentPage(int currentPage) {
        getSDKBean().setCurrentPage(currentPage);
    }

    public List<XMRadio> getRadios() {
        List<Radio> radios = getSDKBean().getRadios();
        List<XMRadio> xmRadios = new ArrayList<>();
        if (radios != null && !radios.isEmpty()) {
            for (Radio radio : radios) {
                if (radio == null) {
                    continue;
                }
                xmRadios.add(new XMRadio(radio));
            }
        }
        return xmRadios;
    }

    public void setRadios(List<XMRadio> xmRadios) {
        if (xmRadios == null) {
            getSDKBean().setRadios(null);
            return;
        }
        List<Radio> radios = new ArrayList<>();
        for (XMRadio xmRadio : xmRadios) {
            if (xmRadio == null) {
                continue;
            }
            radios.add(xmRadio.getSDKBean());
        }
        getSDKBean().setRadios(radios);
    }
}
package com.xiaoma.xting.sdk.bean;

import com.xiaoma.adapter.base.XMBean;
import com.ximalaya.ting.android.opensdk.model.live.radio.Radio;
import com.ximalaya.ting.android.opensdk.model.live.radio.RadioListById;

import java.util.ArrayList;
import java.util.List;

/**
 * @author youthyJ
 * @date 2018/10/13
 */
public class XMRadioListById extends XMBean<RadioListById> {
    public XMRadioListById(RadioListById radioListById) {
        super(radioListById);
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

    public String toString() {
        return getSDKBean().toString();
    }
}

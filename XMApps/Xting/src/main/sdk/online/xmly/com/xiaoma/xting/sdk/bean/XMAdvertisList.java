package com.xiaoma.xting.sdk.bean;

import com.xiaoma.adapter.base.XMBean;
import com.ximalaya.ting.android.opensdk.model.advertis.Advertis;
import com.ximalaya.ting.android.opensdk.model.advertis.AdvertisList;

import java.util.ArrayList;
import java.util.List;

/**
 * @author youthyJ
 * @date 2018/10/18
 */
public class XMAdvertisList extends XMBean<AdvertisList> {
    public XMAdvertisList(AdvertisList advertisList) {
        super(advertisList);
    }

    public int getRet() {
        return getSDKBean().getRet();
    }

    public void setRet(int ret) {
        getSDKBean().setRet(ret);
    }

    public String getMsg() {
        return getSDKBean().getMsg();
    }

    public void setMsg(String msg) {
        getSDKBean().setMsg(msg);
    }

    public int getSource() {
        return getSDKBean().getSource();
    }

    public void setSource(int source) {
        getSDKBean().setSource(source);
    }

    public long getResponseId() {
        return getSDKBean().getResponseId();
    }

    public void setResponseId(long responseId) {
        getSDKBean().setResponseId(responseId);
    }

    public boolean isDuringPlay() {
        return getSDKBean().isDuringPlay();
    }

    public void setDuringPlay(boolean duringPlay) {
        getSDKBean().setDuringPlay(duringPlay);
    }

    public List<XMAdvertis> getAdvertisList() {
        List<Advertis> advertisList = getSDKBean().getAdvertisList();
        List<XMAdvertis> xmAdvertis = new ArrayList<>();
        if (advertisList != null && !advertisList.isEmpty()) {
            for (Advertis advertis : advertisList) {
                if (advertis == null) {
                    continue;
                }
                xmAdvertis.add(new XMAdvertis(advertis));
            }
        }
        return xmAdvertis;
    }

    public void setAdvertisList(List<XMAdvertis> xmAdvertisList) {
        if (xmAdvertisList == null) {
            getSDKBean().setAdvertisList(null);
            return;
        }
        List<Advertis> advertisList = new ArrayList<>();
        for (XMAdvertis xmAdvertis : xmAdvertisList) {
            if (xmAdvertis == null) {
                continue;
            }
            advertisList.add(xmAdvertis.getSDKBean());
        }
        getSDKBean().setAdvertisList(advertisList);
    }

    public String toString() {
        return getSDKBean().toString();
    }
}

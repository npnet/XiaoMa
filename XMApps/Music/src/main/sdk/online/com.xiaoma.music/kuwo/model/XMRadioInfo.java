package com.xiaoma.music.kuwo.model;

import com.xiaoma.adapter.base.XMBean;

import cn.kuwo.base.bean.quku.RadioInfo;

/**
 * Created by ZYao.
 * Date ：2018/10/15 0015
 */
public class XMRadioInfo extends XMBean<RadioInfo> {
    public XMRadioInfo(RadioInfo radioInfo) {
        super(radioInfo);
    }

    public String getNavi() {
        return getSDKBean().getNavi();
    }

    public void setNavi(String var1) {
        getSDKBean().setNavi(var1);
    }

    public int getCid() {
        return getSDKBean().getCid();
    }

    public String getName() {
        return getSDKBean().getName();
    }

    public void setCid(int var1) {
        getSDKBean().setCid(var1);
    }

    public int getType() {
        return getSDKBean().getType();
    }

    public void setType(int var1) {
        getSDKBean().setType(var1);
    }

    public String getDigest() {
        return getSDKBean().getDigest();
    }

    public void setDigest(String var1) {
        getSDKBean().setDigest(var1);
    }

    public int getListenCnt() {
        return getSDKBean().getListenCnt();
    }

    public void setListenCnt(String var1) {
        getSDKBean().setListenCnt(var1);
    }
}

package com.xiaoma.music.kuwo.model;

import com.xiaoma.adapter.base.XMBean;

import cn.kuwo.open.base.PhoneCodeResult;

/**
 * @author zs
 * @date 2018/10/16 0016.
 */
public class XMPhoneCodeResult extends XMBean<PhoneCodeResult> {

    public XMPhoneCodeResult(PhoneCodeResult phoneCodeResult) {
        super(phoneCodeResult);
    }

    public int getStatus() {
        return getSDKBean().getStatus();
    }

    public void setStatus(int var1) {
        getSDKBean().setStatus(var1);
    }

    public String getTm() {
        return getSDKBean().getTm();
    }

    public void setTm(String var1) {
        getSDKBean().setTm(var1);
    }

    public String getMessage() {
        return getSDKBean().getMessage();
    }

    public void setMessage(String var1) {
        getSDKBean().setMessage(var1);
    }

    public boolean isSuccess() {
        return getSDKBean().isSuccess();
    }

    public String toString() {
        return getSDKBean().toString();
    }
}

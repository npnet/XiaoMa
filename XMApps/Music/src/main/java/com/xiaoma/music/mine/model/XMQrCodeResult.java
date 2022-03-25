package com.xiaoma.music.mine.model;

import android.graphics.Bitmap;

import com.xiaoma.adapter.base.XMBean;

import cn.kuwo.mod.userinfo.login.QrCodeResult;

/**
 * Author: loren
 * Date: 2019/6/18 0018
 */
public class XMQrCodeResult extends XMBean<QrCodeResult> {

    public XMQrCodeResult(QrCodeResult qrCodeResult) {
        super(qrCodeResult);
    }

    public Bitmap getBitmap() {
        return getSDKBean().getBitmap();
    }

    public boolean isSuccess() {
        return getSDKBean().getCode() == getSDKBean().CODE_SUCCESS;
    }

}

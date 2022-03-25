package com.xiaoma.xting.koala.bean;

import com.kaolafm.opensdk.api.operation.model.ImageFile;
import com.xiaoma.adapter.base.XMBean;

/**
 * <des>
 *
 * @author YangGang
 * @date 2019/7/2
 */
public class XMImageFile extends XMBean<ImageFile> {
    public XMImageFile(ImageFile imageFile) {
        super(imageFile);
    }

    public String getUrl() {
        return getSDKBean().getUrl();
    }

    public int getWidth() {
        return getSDKBean().getWidth();
    }

    public int getHeight() {
        return getSDKBean().getHeight();
    }

    public String getType() {
        return getSDKBean().getType();
    }

    @Override
    public String toString() {
        return super.toString();
    }
}

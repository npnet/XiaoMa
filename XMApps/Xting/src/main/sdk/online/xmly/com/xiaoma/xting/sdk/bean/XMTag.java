package com.xiaoma.xting.sdk.bean;

import com.xiaoma.adapter.base.XMBean;
import com.ximalaya.ting.android.opensdk.model.tag.Tag;

/**
 * @author youthyJ
 * @date 2018/10/11
 */
public class XMTag extends XMBean<Tag> {

    public XMTag(Tag tag) {
        super(tag);
    }

    public String getTagName() {
        return getSDKBean().getTagName();
    }

    public void setTagName(String tagName) {
        getSDKBean().setTagName(tagName);
    }

    public String getKind() {
        return getSDKBean().getKind();
    }

    public void setKind(String kind) {
        getSDKBean().setKind(kind);
    }

    public String toString() {
        return getSDKBean().toString();
    }
}

package com.xiaoma.xting.sdk.bean;

import com.xiaoma.adapter.base.XMBean;
import com.ximalaya.ting.android.opensdk.model.metadata.ChildAttributes;

/**
 * @author youthyJ
 * @date 2018/10/12
 */
public class XMChildAttributes extends XMBean<ChildAttributes> {
    public XMChildAttributes(ChildAttributes childAttributes) {
        super(childAttributes);
    }

    public String getAttrKey() {
        return getSDKBean().getAttrKey();
    }

    public void setAttrKey(String attrKey) {
        getSDKBean().setAttrKey(attrKey);
    }

    public String getAttrValue() {
        return getSDKBean().getAttrValue();
    }

    public void setAttrValue(String attrValue) {
        getSDKBean().setAttrValue(attrValue);
    }

    public String getDisplayName() {
        return getSDKBean().getDisplayName();
    }

    public void setDisplayName(String displayName) {
        getSDKBean().setDisplayName(displayName);
    }

}

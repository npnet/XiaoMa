package com.xiaoma.xting.sdk.bean;

import com.xiaoma.adapter.base.XMBean;
import com.ximalaya.ting.android.opensdk.model.metadata.Attributes;
import com.ximalaya.ting.android.opensdk.model.metadata.MetaData;

import java.util.ArrayList;
import java.util.List;

/**
 * @author youthyJ
 * @date 2018/10/12
 */
public class XMMetaData extends XMBean<MetaData> {
    public XMMetaData(MetaData metaData) {
        super(metaData);
    }

    public String getDisplayName() {
        return getSDKBean().getDisplayName();
    }

    public void setDisplayName(String displayName) {
        getSDKBean().setDisplayName(displayName);
    }

    public String getKind() {
        return getSDKBean().getKind();
    }

    public void setKind(String kind) {
        getSDKBean().setKind(kind);
    }

    public List<XMAttributes> getAttributes() {
        List<Attributes> attributes = getSDKBean().getAttributes();
        List<XMAttributes> xmAttributes = new ArrayList<>();
        if (attributes != null && !attributes.isEmpty()) {
            for (Attributes attribute : attributes) {
                if (attribute == null) {
                    continue;
                }
                xmAttributes.add(new XMAttributes(attribute));
            }
        }
        return xmAttributes;
    }

    public void setAttributes(List<XMAttributes> xmAttributes) {
        if (xmAttributes == null) {
            getSDKBean().setAttributes(null);
            return;
        }
        List<Attributes> attributes = new ArrayList<>();
        for (XMAttributes xmAttribute : xmAttributes) {
            if (xmAttribute == null) {
                continue;
            }
            attributes.add(xmAttribute.getSDKBean());
        }
        getSDKBean().setAttributes(attributes);
    }
}

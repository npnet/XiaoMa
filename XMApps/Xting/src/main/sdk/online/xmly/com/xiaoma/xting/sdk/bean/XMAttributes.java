package com.xiaoma.xting.sdk.bean;

import com.xiaoma.adapter.base.XMBean;
import com.ximalaya.ting.android.opensdk.model.metadata.Attributes;
import com.ximalaya.ting.android.opensdk.model.metadata.ChildMetadata;

import java.util.ArrayList;
import java.util.List;

/**
 * @author youthyJ
 * @date 2018/10/12
 */
public class XMAttributes extends XMBean<Attributes> {
    public XMAttributes(Attributes attributes) {
        super(attributes);
    }

    public List<XMChildMetadata> getChildMetadatas() {
        List<ChildMetadata> childMetadatas = getSDKBean().getChildMetadatas();
        List<XMChildMetadata> xmChildMetadata = new ArrayList<>();
        if (childMetadatas != null && !childMetadatas.isEmpty()) {
            for (ChildMetadata childMetadata : childMetadatas) {
                if (childMetadata == null) {
                    continue;
                }
                xmChildMetadata.add(new XMChildMetadata(childMetadata));
            }
        }
        return xmChildMetadata;
    }

    public void setChildMetadatas(List<XMChildMetadata> xmChildMetadatas) {
        if (xmChildMetadatas == null) {
            getSDKBean().setChildMetadatas(null);
            return;
        }
        List<ChildMetadata> childMetadatas = new ArrayList<>();
        for (XMChildMetadata xmChildMetadata : xmChildMetadatas) {
            if (xmChildMetadata == null) {
                continue;
            }
            childMetadatas.add(xmChildMetadata.getSDKBean());
        }
        getSDKBean().setChildMetadatas(childMetadatas);
    }
}

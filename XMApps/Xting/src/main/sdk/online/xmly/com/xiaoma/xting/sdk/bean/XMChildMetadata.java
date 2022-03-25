package com.xiaoma.xting.sdk.bean;

import com.xiaoma.adapter.base.XMBean;
import com.ximalaya.ting.android.opensdk.model.metadata.ChildMetadata;

/**
 * @author youthyJ
 * @date 2018/10/12
 */
public class XMChildMetadata extends XMBean<ChildMetadata> {
    public XMChildMetadata(ChildMetadata childMetadata) {
        super(childMetadata);
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

}

package com.xiaoma.xting.sdk.bean;

import com.xiaoma.adapter.base.XMBean;
import com.ximalaya.ting.android.opensdk.model.column.ColumnEditor;

/**
 * @author youthyJ
 * @date 2018/10/16
 */
public class XMColumnEditor extends XMBean<ColumnEditor> {
    public XMColumnEditor(ColumnEditor columnEditor) {
        super(columnEditor);
    }

    public long getUid() {
        return getSDKBean().getUid();
    }

    public void setUid(long uid) {
        getSDKBean().setUid(uid);
    }

    public String getNickName() {
        return getSDKBean().getNickName();
    }

    public void setNickName(String nickName) {
        getSDKBean().setNickName(nickName);
    }

    public String getAvatarUrl() {
        return getSDKBean().getAvatarUrl();
    }

    public void setAvatarUrl(String avatarUrl) {
        getSDKBean().setAvatarUrl(avatarUrl);
    }

    public String getPersonalsignature() {
        return getSDKBean().getPersonalsignature();
    }

    public void setPersonalsignature(String personalsignature) {
        getSDKBean().setPersonalsignature(personalsignature);
    }

    public String toString() {
        return getSDKBean().toString();
    }
}

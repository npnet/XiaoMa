package com.xiaoma.xting.sdk.bean;

import com.xiaoma.adapter.base.XMBean;
import com.ximalaya.ting.android.opensdk.model.column.ColumnDetail;
import com.ximalaya.ting.android.opensdk.model.column.ColumnDetailAlbum;
import com.ximalaya.ting.android.opensdk.model.column.ColumnDetailTrack;

/**
 * @author youthyJ
 * @date 2018/10/16
 */
public class XMColumnDetail extends XMBean<ColumnDetail> {
    public XMColumnDetail(ColumnDetail columnDetail) {
        super(columnDetail);
    }

    public long getColumnId() {
        return getSDKBean().getColumnId();
    }

    public void setColumnId(long columnId) {
        getSDKBean().setColumnId(columnId);
    }

    public String getColumnIntro() {
        return getSDKBean().getColumnIntro();
    }

    public void setColumnIntro(String columnIntro) {
        getSDKBean().setColumnIntro(columnIntro);
    }

    public int getColumnContentType() {
        return getSDKBean().getColumnContentType();
    }

    public void setColumnContentType(int columnContentType) {
        getSDKBean().setColumnContentType(columnContentType);
    }

    public String getCoverUrlLarge() {
        return getSDKBean().getCoverUrlLarge();
    }

    public void setCoverUrlLarge(String coverUrlLarge) {
        getSDKBean().setCoverUrlLarge(coverUrlLarge);
    }

    public String getLogoSmall() {
        return getSDKBean().getLogoSmall();
    }

    public void setLogoSmall(String logoSmall) {
        getSDKBean().setLogoSmall(logoSmall);
    }

    public XMColumnEditor getColumnEditor() {
        return new XMColumnEditor(getSDKBean().getColumnEditor());
    }

    public void setColumnEditor(XMColumnEditor xmColumnEditor) {
        getSDKBean().setColumnEditor(xmColumnEditor.getSDKBean());
    }

    public String toString() {
        return getSDKBean().toString();
    }

    public XMColumnDetailAlbum getColumnDetailAlbum() {
        if (getColumnContentType() == 1) {
            ColumnDetailAlbum albumList = (ColumnDetailAlbum) getSDKBean();
            return new XMColumnDetailAlbum(albumList);
        }
        return null;
    }

    public XMColumnDetailTrack getColumnDetailTrack() {
        if (getColumnContentType() == 2) {
            ColumnDetailTrack trackList = (ColumnDetailTrack) getSDKBean();
            return new XMColumnDetailTrack(trackList);
        }
        return null;
    }

}

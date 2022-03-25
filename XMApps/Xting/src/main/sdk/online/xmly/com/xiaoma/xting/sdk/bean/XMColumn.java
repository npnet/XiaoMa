package com.xiaoma.xting.sdk.bean;

import com.xiaoma.adapter.base.XMBean;
import com.ximalaya.ting.android.opensdk.model.column.Column;

/**
 * @author youthyJ
 * @date 2018/10/16
 */
public class XMColumn extends XMBean<Column> {
    public XMColumn(Column column) {
        super(column);
    }

    public long getColumnId() {
        return getSDKBean().getColumnId();
    }

    public void setColumnId(long columnId) {
        getSDKBean().setColumnId(columnId);
    }

    public String getColumnTitle() {
        return getSDKBean().getColumnTitle();
    }

    public void setColumnTitle(String columnTitle) {
        getSDKBean().setColumnTitle(columnTitle);
    }

    public String getColumnSubTitle() {
        return getSDKBean().getColumnSubTitle();
    }

    public void setColumnSubTitle(String columnSubTitle) {
        getSDKBean().setColumnSubTitle(columnSubTitle);
    }

    public String getColumnFootNote() {
        return getSDKBean().getColumnFootNote();
    }

    public void setColumnFootNote(String columnFootNote) {
        getSDKBean().setColumnFootNote(columnFootNote);
    }

    public int getColumnContentType() {
        return getSDKBean().getColumnContentType();
    }

    public void setColumnContentType(int columnContentType) {
        getSDKBean().setColumnContentType(columnContentType);
    }

    public String getCoverUrlSmall() {
        return getSDKBean().getCoverUrlSmall();
    }

    public void setCoverUrlSmall(String coverUrlSmall) {
        getSDKBean().setCoverUrlSmall(coverUrlSmall);
    }

    public String getCoverUrlLarge() {
        return getSDKBean().getCoverUrlLarge();
    }

    public void setCoverUrlLarge(String coverUrlLarge) {
        getSDKBean().setCoverUrlLarge(coverUrlLarge);
    }

    public long getReleasedAt() {
        return getSDKBean().getReleasedAt();
    }

    public void setReleasedAt(long releasedAt) {
        getSDKBean().setReleasedAt(releasedAt);
    }

    public boolean isHot() {
        return getSDKBean().isHot();
    }

    public void setHot(boolean isHot) {
        getSDKBean().setHot(isHot);
    }

    public String toString() {
        return getSDKBean().toString();
    }
}

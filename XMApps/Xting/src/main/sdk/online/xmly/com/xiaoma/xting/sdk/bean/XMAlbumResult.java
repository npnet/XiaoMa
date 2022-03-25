package com.xiaoma.xting.sdk.bean;

import com.xiaoma.adapter.base.XMBean;
import com.ximalaya.ting.android.opensdk.model.word.AlbumResult;

/**
 * @author youthyJ
 * @date 2018/10/16
 */
public class XMAlbumResult extends XMBean<AlbumResult> {
    public XMAlbumResult(AlbumResult albumResult) {
        super(albumResult);
    }

    public long getAlbumId() {
        return getSDKBean().getAlbumId();
    }

    public void setAlbumId(long albumId) {
        getSDKBean().setAlbumId(albumId);
    }

    public String getAlbumTitle() {
        return getSDKBean().getAlbumTitle();
    }

    public void setAlbumTitle(String albumTitle) {
        getSDKBean().setAlbumTitle(albumTitle);
    }

    public String getHightlightAlbumTitle() {
        return getSDKBean().getHightlightAlbumTitle();
    }

    public void setHightlightAlbumTitle(String hightlightAlbumTitle) {
        getSDKBean().setHightlightAlbumTitle(hightlightAlbumTitle);
    }

    public String getCategoryName() {
        return getSDKBean().getCategoryName();
    }

    public void setCategoryName(String categoryName) {
        getSDKBean().setCategoryName(categoryName);
    }

    public String getCoverUrlSmall() {
        return getSDKBean().getCoverUrlSmall();
    }

    public void setCoverUrlSmall(String coverUrlSmall) {
        getSDKBean().setCoverUrlSmall(coverUrlSmall);
    }

    public String toString() {
        return getSDKBean().toString();
    }
}

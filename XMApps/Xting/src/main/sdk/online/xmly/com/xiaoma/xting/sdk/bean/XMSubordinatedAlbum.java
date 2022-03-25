package com.xiaoma.xting.sdk.bean;

import com.xiaoma.adapter.base.XMBean;
import com.ximalaya.ting.android.opensdk.model.album.SubordinatedAlbum;

/**
 * @author youthyJ
 * @date 2018/10/11
 */
public class XMSubordinatedAlbum extends XMBean<SubordinatedAlbum> {
    public XMSubordinatedAlbum(SubordinatedAlbum subordinatedAlbum) {
        super(subordinatedAlbum);
    }

    public String getRecSrc() {
        return getSDKBean().getRecSrc();
    }

    public void setRecSrc(String recSrc) {
        getSDKBean().setRecSrc(recSrc);
    }

    public String getRecTrack() {
        return getSDKBean().getRecTrack();
    }

    public void setRecTrack(String recTrack) {
        getSDKBean().setRecTrack(recTrack);
    }

    public long getUptoDateTime() {
        return getSDKBean().getUptoDateTime();
    }

    public void setUptoDateTime(long uptoDateTime) {
        getSDKBean().setUptoDateTime(uptoDateTime);
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

    public String getCoverUrlSmall() {
        return getSDKBean().getCoverUrlSmall();
    }

    public void setCoverUrlSmall(String coverUrlSmall) {
        getSDKBean().setCoverUrlSmall(coverUrlSmall);
    }

    public String getCoverUrlMiddle() {
        return getSDKBean().getCoverUrlMiddle();
    }

    public void setCoverUrlMiddle(String coverUrlMiddle) {
        getSDKBean().setCoverUrlMiddle(coverUrlMiddle);
    }

    public String getCoverUrlLarge() {
        return getSDKBean().getCoverUrlLarge();
    }

    public void setCoverUrlLarge(String coverUrlLarge) {
        getSDKBean().setCoverUrlLarge(coverUrlLarge);
    }

    public int getSerializeStatus() {
        return getSDKBean().getSerializeStatus();
    }

    public void setSerializeStatus(int serializeStatus) {
        getSDKBean().setSerializeStatus(serializeStatus);
    }

    public String toString() {
        return getSDKBean().toString();
    }

    public String getValidCover() {
        return getSDKBean().getValidCover();
    }
}

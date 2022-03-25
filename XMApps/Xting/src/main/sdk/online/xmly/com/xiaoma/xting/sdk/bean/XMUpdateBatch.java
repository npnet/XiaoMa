package com.xiaoma.xting.sdk.bean;

import com.xiaoma.adapter.base.XMBean;
import com.ximalaya.ting.android.opensdk.model.album.UpdateBatch;

/**
 * @author youthyJ
 * @date 2018/10/12
 */
public class XMUpdateBatch extends XMBean<UpdateBatch> {
    public XMUpdateBatch(UpdateBatch updateBatch) {
        super(updateBatch);
    }

    public long getAlbumId() {
        return getSDKBean().getAlbumId();
    }

    public void setAlbumId(long albumId) {
        getSDKBean().setAlbumId(albumId);
    }

    public long getTrackId() {
        return getSDKBean().getTrackId();
    }

    public void setTrackId(long trackId) {
        getSDKBean().setTrackId(trackId);
    }

    public String getCoverUrl() {
        return getSDKBean().getCoverUrl();
    }

    public void setCoverUrl(String coverUrl) {
        getSDKBean().setCoverUrl(coverUrl);
    }

    public String getTrackTitle() {
        return getSDKBean().getTrackTitle();
    }

    public void setTrackTitle(String trackTitle) {
        getSDKBean().setTrackTitle(trackTitle);
    }

    public long getUpdateAt() {
        return getSDKBean().getUpdateAt();
    }

    public void setUpdateAt(long updateAt) {
        getSDKBean().setUpdateAt(updateAt);
    }

    public String toString() {
        return getSDKBean().toString();
    }


}

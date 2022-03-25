package com.xiaoma.assistant.model;

import com.xiaoma.adapter.base.XMBean;
import com.ximalaya.ting.android.opensdk.model.album.LastUpTrack;

/**
 * @author youthyJ
 * @date 2018/10/11
 */
public class XMLastUpTrack extends XMBean<LastUpTrack> {
    public XMLastUpTrack(LastUpTrack lastUpTrack) {
        super(lastUpTrack);
    }

    public long getTrackId() {
        return getSDKBean().getTrackId();
    }

    public void setTrackId(long trackId) {
        getSDKBean().setTrackId(trackId);
    }

    public String getTrackTitle() {
        return getSDKBean().getTrackTitle();
    }

    public void setTrackTitle(String trackTitle) {
        getSDKBean().setTrackTitle(trackTitle);
    }

    public long getDuration() {
        return getSDKBean().getDuration();
    }

    public void setDuration(long duration) {
        getSDKBean().setDuration(duration);
    }

    public long getCreatedAt() {
        return getSDKBean().getCreatedAt();
    }

    public void setCreatedAt(long createdAt) {
        getSDKBean().setCreatedAt(createdAt);
    }

    public long getUpdatedAt() {
        return getSDKBean().getUpdatedAt();
    }

    public void setUpdatedAt(long updatedAt) {
        getSDKBean().setUpdatedAt(updatedAt);
    }

    public String toString() {
        return getSDKBean().toString();
    }

}

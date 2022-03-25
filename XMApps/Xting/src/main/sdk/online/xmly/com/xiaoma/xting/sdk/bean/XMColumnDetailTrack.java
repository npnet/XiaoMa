package com.xiaoma.xting.sdk.bean;

import com.xiaoma.adapter.base.XMBean;
import com.ximalaya.ting.android.opensdk.model.column.ColumnDetailTrack;
import com.ximalaya.ting.android.opensdk.model.track.Track;

import java.util.ArrayList;
import java.util.List;

/**
 * @author youthyJ
 * @date 2018/10/16
 */
public class XMColumnDetailTrack extends XMBean<ColumnDetailTrack> {
    public XMColumnDetailTrack(ColumnDetailTrack columnDetailTrack) {
        super(columnDetailTrack);
    }

    public List<XMTrack> getTrackList() {
        List<Track> tracks = getSDKBean().getTrackList();
        List<XMTrack> xmTracks = new ArrayList<>();
        if (tracks != null && !tracks.isEmpty()) {
            for (Track track : tracks) {
                if (track == null) {
                    continue;
                }
                xmTracks.add(new XMTrack(track));
            }
        }
        return xmTracks;
    }

    public void setTrackList(List<XMTrack> xmTracks) {
        if (xmTracks == null) {
            getSDKBean().setTrackList(null);
            return;
        }
        List<Track> tracks = new ArrayList<>();
        for (XMTrack xmTrack : xmTracks) {
            if (xmTrack == null) {
                continue;
            }
            tracks.add(xmTrack.getSDKBean());
        }
        getSDKBean().setTrackList(tracks);
    }

    public String toString() {
        return getSDKBean().toString();
    }
}

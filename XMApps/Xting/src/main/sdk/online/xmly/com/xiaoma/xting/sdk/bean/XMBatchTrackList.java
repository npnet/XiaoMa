package com.xiaoma.xting.sdk.bean;

import com.xiaoma.adapter.base.XMBean;
import com.ximalaya.ting.android.opensdk.model.track.BatchTrackList;
import com.ximalaya.ting.android.opensdk.model.track.Track;

import java.util.ArrayList;
import java.util.List;

/**
 * @author youthyJ
 * @date 2018/10/12
 */
public class XMBatchTrackList extends XMBean<BatchTrackList> {
    public XMBatchTrackList(BatchTrackList batchTrackList) {
        super(batchTrackList);
    }

    public List<XMTrack> getTracks() {
        List<Track> tracks = getSDKBean().getTracks();
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

    public void setTracks(List<XMTrack> xmTracks) {
        if (xmTracks == null) {
            getSDKBean().setTracks(null);
            return;
        }
        List<Track> tracks = new ArrayList<>();
        for (XMTrack xmTrack : xmTracks) {
            if (xmTrack == null) {
                continue;
            }
            tracks.add(xmTrack.getSDKBean());
        }
        getSDKBean().setTracks(tracks);
    }
}

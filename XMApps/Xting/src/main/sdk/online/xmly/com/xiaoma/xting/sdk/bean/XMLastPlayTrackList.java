package com.xiaoma.xting.sdk.bean;

import com.ximalaya.ting.android.opensdk.model.track.LastPlayTrackList;
import com.ximalaya.ting.android.opensdk.model.track.Track;

import java.util.ArrayList;
import java.util.List;

/**
 * @author youthyJ
 * @date 2018/10/12
 */
public class XMLastPlayTrackList extends XMCommonTrackList<LastPlayTrackList> {
    public XMLastPlayTrackList(LastPlayTrackList lastPlayTrackList) {
        super(lastPlayTrackList);
    }

    public int getCategoryId() {
        return getSDKBean().getCategoryId();
    }

    public void setCategoryId(int categoryId) {
        getSDKBean().setCategoryId(categoryId);
    }

    public String getTagname() {
        return getSDKBean().getTagname();
    }

    public void setTagname(String tagname) {
        getSDKBean().setTagname(tagname);
    }

    public int getPageid() {
        return getSDKBean().getPageid();
    }

    public void setPageid(int pageid) {
        getSDKBean().setPageid(pageid);
    }

    public String toString() {
        return getSDKBean().toString();
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

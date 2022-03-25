package com.xiaoma.xting.sdk.bean;

import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.model.track.TrackList;

import java.util.ArrayList;
import java.util.List;

/**
 * @author youthyJ
 * @date 2018/10/11
 */
public class XMTrackList extends XMCommonTrackList<TrackList> {
    public XMTrackList(TrackList trackList) {
        super(trackList);
    }

    public int getAlbumId() {
        return getSDKBean().getAlbumId();
    }

    public void setAlbumId(int albumId) {
        getSDKBean().setAlbumId(albumId);
    }

    public String getAlbumTitle() {
        return getSDKBean().getAlbumTitle();
    }

    public void setAlbumTitle(String albumTitle) {
        getSDKBean().setAlbumTitle(albumTitle);
    }

    public int getCategoryId() {
        return getSDKBean().getCategoryId();
    }

    public void setCategoryId(int categoryId) {
        getSDKBean().setCategoryId(categoryId);
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

    public String getAlbumIntro() {
        return getSDKBean().getAlbumIntro();
    }

    public void setAlbumIntro(String albumIntro) {
        getSDKBean().setAlbumIntro(albumIntro);
    }

    public int getCurrentPage() {
        return getSDKBean().getCurrentPage();
    }

    public void setCurrentPage(int currentPage) {
        getSDKBean().setCurrentPage(currentPage);
    }

    public String toString() {
        return getSDKBean().toString();
    }

    public boolean isCanDownload() {
        return getSDKBean().isCanDownload();
    }

    public void setCanDownload(boolean canDownload) {
        getSDKBean().setCanDownload(canDownload);
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

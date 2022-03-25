package com.xiaoma.assistant.model;

import com.xiaoma.adapter.base.XMBean;
import com.xiaoma.assistant.utils.UnitConverUtils;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.pay.AlbumPriceTypeDetail;
import com.ximalaya.ting.android.opensdk.model.track.Track;

import java.util.ArrayList;
import java.util.List;

/**
 * @author youthyJ
 * @date 2018/10/11
 */
public class XMAlbum extends XMBean<Album> {

    public XMAlbum(Album album) {
        super(album);
    }

    public long getId() {
        return getSDKBean().getId();
    }

    public void setId(long id) {
        getSDKBean().setId(id);
    }

    public String getAlbumTitle() {
        return getSDKBean().getAlbumTitle();
    }

    public void setAlbumTitle(String albumTitle) {
        getSDKBean().setAlbumTitle(albumTitle);
    }

    public String getAlbumTags() {
        return getSDKBean().getAlbumTags();
    }

    public void setAlbumTags(String albumTags) {
        getSDKBean().setAlbumTags(albumTags);
    }

    public String getAlbumIntro() {
        return getSDKBean().getAlbumIntro();
    }

    public void setAlbumIntro(String albumIntro) {
        getSDKBean().setAlbumIntro(albumIntro);
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

    public long getPlayCount() {
        return getSDKBean().getPlayCount();
    }

    public String getFormatCount() {
        return UnitConverUtils.bigNumFormat(getPlayCount());
    }

    public void setPlayCount(long playCount) {
        getSDKBean().setPlayCount(playCount);
    }

    public long getFavoriteCount() {
        return getSDKBean().getFavoriteCount();
    }

    public void setFavoriteCount(long favoriteCount) {
        getSDKBean().setFavoriteCount(favoriteCount);
    }

    public long getIncludeTrackCount() {
        return getSDKBean().getIncludeTrackCount();
    }

    public void setIncludeTrackCount(long includeTrackCount) {
        getSDKBean().setIncludeTrackCount(includeTrackCount);
    }

    public long getUpdatedAt() {
        return getSDKBean().getUpdatedAt();
    }

    public void setUpdatedAt(long updatedAt) {
        getSDKBean().setUpdatedAt(updatedAt);
    }

    public long getCreatedAt() {
        return getSDKBean().getCreatedAt();
    }

    public void setCreatedAt(long createdAt) {
        getSDKBean().setCreatedAt(createdAt);
    }

    public long getSoundLastListenId() {
        return getSDKBean().getSoundLastListenId();
    }

    public void setSoundLastListenId(long soundLastListenId) {
        getSDKBean().setSoundLastListenId(soundLastListenId);
    }

    public int getIsFinished() {
        return getSDKBean().getIsFinished();
    }

    public void setIsFinished(int isFinished) {
        getSDKBean().setIsFinished(isFinished);
    }

    public String getRecommentSrc() {
        return getSDKBean().getRecommentSrc();
    }

    public void setRecommentSrc(String recommentSrc) {
        getSDKBean().setRecommentSrc(recommentSrc);
    }

    public long getBasedRelativeAlbumId() {
        return getSDKBean().getBasedRelativeAlbumId();
    }

    public void setBasedRelativeAlbumId(long basedRelativeAlbumId) {
        getSDKBean().setBasedRelativeAlbumId(basedRelativeAlbumId);
    }

    public String getRecommendTrace() {
        return getSDKBean().getRecommendTrace();
    }

    public void setRecommendTrace(String recommendTrace) {
        getSDKBean().setRecommendTrace(recommendTrace);
    }

    public String getShareCount() {
        return getSDKBean().getShareCount();
    }

    public void setShareCount(String shareCount) {
        getSDKBean().setShareCount(shareCount);
    }

    public String toString() {
        return getSDKBean().toString();
    }

    public int describeContents() {
        return 0;
    }

    public String getValidCover() {
        return getSDKBean().getValidCover();
    }

    public String getMiddleCover() {
        return getSDKBean().getMiddleCover();
    }

    public boolean isCanDownload() {
        return getSDKBean().isCanDownload();
    }

    public void setCanDownload(boolean canDownload) {
        getSDKBean().setCanDownload(canDownload);
    }

    public long getSubscribeCount() {
        return getSDKBean().getSubscribeCount();
    }

    public void setSubscribeCount(long subscribeCount) {
        getSDKBean().setSubscribeCount(subscribeCount);
    }

    public int getCategoryId() {
        return getSDKBean().getCategoryId();
    }

    public void setCategoryId(int categoryId) {
        getSDKBean().setCategoryId(categoryId);
    }

    public boolean isTracksNaturalOrdered() {
        return getSDKBean().isTracksNaturalOrdered();
    }

    public void setTracksNaturalOrdered(boolean tracksNaturalOrdered) {
        getSDKBean().setTracksNaturalOrdered(tracksNaturalOrdered);
    }

    public boolean isPaid() {
        return getSDKBean().isPaid();
    }

    public void setIsPaid(boolean paid) {
        getSDKBean().setIsPaid(paid);
    }

    public int getEstimatedTrackCount() {
        return getSDKBean().getEstimatedTrackCount();
    }

    public void setEstimatedTrackCount(int estimatedTrackCount) {
        getSDKBean().setEstimatedTrackCount(estimatedTrackCount);
    }

    public String getAlbumRichIntro() {
        return getSDKBean().getAlbumRichIntro();
    }

    public void setAlbumRichIntro(String albumRichIntro) {
        getSDKBean().setAlbumRichIntro(albumRichIntro);
    }

    public String getSpeakerIntro() {
        return getSDKBean().getSpeakerIntro();
    }

    public void setSpeakerIntro(String speakerIntro) {
        getSDKBean().setSpeakerIntro(speakerIntro);
    }

    public int getFreeTrackCount() {
        return getSDKBean().getFreeTrackCount();
    }

    public void setFreeTrackCount(int freeTrackCount) {
        getSDKBean().setFreeTrackCount(freeTrackCount);
    }

    public String getFreeTrackIds() {
        return getSDKBean().getFreeTrackIds();
    }

    public void setFreeTrackIds(String freeTrackIds) {
        getSDKBean().setFreeTrackIds(freeTrackIds);
    }

    public String getSaleIntro() {
        return getSDKBean().getSaleIntro();
    }

    public void setSaleIntro(String saleIntro) {
        getSDKBean().setSaleIntro(saleIntro);
    }

    public String getExpectedRevenue() {
        return getSDKBean().getExpectedRevenue();
    }

    public void setExpectedRevenue(String expectedRevenue) {
        getSDKBean().setExpectedRevenue(expectedRevenue);
    }

    public String getBuyNotes() {
        return getSDKBean().getBuyNotes();
    }

    public void setBuyNotes(String buyNotes) {
        getSDKBean().setBuyNotes(buyNotes);
    }

    public String getSpeakerTitle() {
        return getSDKBean().getSpeakerTitle();
    }

    public void setSpeakerTitle(String speakerTitle) {
        getSDKBean().setSpeakerTitle(speakerTitle);
    }

    public String getSpeakerContent() {
        return getSDKBean().getSpeakerContent();
    }

    public void setSpeakerContent(String speakerContent) {
        getSDKBean().setSpeakerContent(speakerContent);
    }

    public boolean isHasSample() {
        return getSDKBean().isHasSample();
    }

    public void setHasSample(boolean hasSample) {
        getSDKBean().setHasSample(hasSample);
    }

    public int getComposedPriceType() {
        return getSDKBean().getComposedPriceType();
    }

    public void setComposedPriceType(int composedPriceType) {
        getSDKBean().setComposedPriceType(composedPriceType);
    }

    public String getDetailBannerUrl() {
        return getSDKBean().getDetailBannerUrl();
    }

    public void setDetailBannerUrl(String detailBannerUrl) {
        getSDKBean().setDetailBannerUrl(detailBannerUrl);
    }

    public String getAlbumScore() {
        return getSDKBean().getAlbumScore();
    }

    public void setAlbumScore(String albumScore) {
        getSDKBean().setAlbumScore(albumScore);
    }

    public String getPriceTypeDetails() {
        return getSDKBean().getPriceTypeDetails();
    }

    public void setPriceTypeDetails(String priceTypeDetails) {
        getSDKBean().setPriceTypeDetails(priceTypeDetails);
    }

    public XMAnnouncer getAnnouncer() {
        return new XMAnnouncer(getSDKBean().getAnnouncer());
    }

    public void setAnnouncer(XMAnnouncer xmAnnouncer) {
        getSDKBean().setAnnouncer(xmAnnouncer.getSDKBean());
    }

    public List<XMAlbumPriceTypeDetail> getPriceTypeInfos() {
        List<AlbumPriceTypeDetail> sdkInfos = getSDKBean().getPriceTypeInfos();
        ArrayList<XMAlbumPriceTypeDetail> xmInfos = new ArrayList<>();
        if (sdkInfos != null && !sdkInfos.isEmpty()) {
            for (AlbumPriceTypeDetail info : sdkInfos) {
                if (info == null) {
                    continue;
                }
                xmInfos.add(new XMAlbumPriceTypeDetail(info));
            }
        }
        return xmInfos;
    }

    public void setPriceTypeInfos(List<XMAlbumPriceTypeDetail> xmInfos) {
        if (xmInfos == null) {
            getSDKBean().setPriceTypeInfos(null);
            return;
        }
        List<AlbumPriceTypeDetail> sdkInfos = new ArrayList<>();
        for (XMAlbumPriceTypeDetail xmInfo : xmInfos) {
            if (xmInfo == null) {
                continue;
            }
            sdkInfos.add(xmInfo.getSDKBean());
        }
        getSDKBean().setPriceTypeInfos(sdkInfos);
    }

    public XMRecommendTrack getRecommendTrack() {
        return new XMRecommendTrack(getSDKBean().getRecommendTrack());
    }

    public void setRecommendTrack(XMRecommendTrack recommendTrack) {
        getSDKBean().setRecommendTrack(recommendTrack.getSDKBean());
    }

    public XMLastUpTrack getLastUptrack() {
        return new XMLastUpTrack(getSDKBean().getLastUptrack());
    }

    public void setLastUptrack(XMLastUpTrack lastUptrack) {
        getSDKBean().setLastUptrack(lastUptrack.getSDKBean());
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

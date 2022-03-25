package com.xiaoma.xting.sdk.bean;

import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.ximalaya.ting.android.opensdk.model.album.SubordinatedAlbum;
import com.ximalaya.ting.android.opensdk.model.track.Track;

/**
 * @author youthyJ
 * @date 2018/10/11
 */
public class XMTrack extends XMPlayableModel<Track> {
    public XMTrack(Track track) {
        super(track);
    }

    public long getUid() {
        return getSDKBean().getUid();
    }

    public void setUid(long uid) {
        getSDKBean().setUid(uid);
    }

    public boolean isPaid() {
        return getSDKBean().isPaid();
    }

    public void setPaid(boolean isPaid) {
        getSDKBean().setPaid(isPaid);
    }

    public boolean isChecked() {
        return getSDKBean().isChecked();
    }

    public void setChecked(boolean checked) {
        getSDKBean().setChecked(checked);
    }

    public long getTrackActivityId() {
        return getSDKBean().getTrackActivityId();
    }

    public void setTrackActivityId(long trackActivityId) {
        getSDKBean().setTrackActivityId(trackActivityId);
    }

    public double getPrice() {
        return getSDKBean().getPrice();
    }

    public void setPrice(double price) {
        getSDKBean().setPrice(price);
    }

    public double getDiscountedPrice() {
        return getSDKBean().getDiscountedPrice();
    }

    public void setDiscountedPrice(double discountedPrice) {
        getSDKBean().setDiscountedPrice(discountedPrice);
    }

    public boolean isFree() {
        return getSDKBean().isFree();
    }

    public void setFree(boolean free) {
        getSDKBean().setFree(free);
    }

    public boolean isPayTrack() {
        return getSDKBean().isPayTrack();
    }

    public boolean isAuthorized() {
        return getSDKBean().isAuthorized();
    }

    public void setAuthorized(boolean authorized) {
        getSDKBean().setAuthorized(authorized);
    }

    public int getBlockIndex() {
        return getSDKBean().getBlockIndex();
    }

    public void setBlockIndex(int blockIndex) {
        getSDKBean().setBlockIndex(blockIndex);
    }

    public int getBlockNum() {
        return getSDKBean().getBlockNum();
    }

    public void setBlockNum(int blockNum) {
        getSDKBean().setBlockNum(blockNum);
    }

    public int getProtocolVersion() {
        return getSDKBean().getProtocolVersion();
    }

    public void setProtocolVersion(int protocolVersion) {
        getSDKBean().setProtocolVersion(protocolVersion);
    }

    public int getChargeFileSize() {
        return getSDKBean().getChargeFileSize();
    }

    public void setChargeFileSize(int chargeFileSize) {
        getSDKBean().setChargeFileSize(chargeFileSize);
    }

    public boolean isAutoPaused() {
        return getSDKBean().isAutoPaused();
    }

    public void setAutoPaused(boolean isAutoPaused) {
        getSDKBean().setAutoPaused(isAutoPaused);
    }

    public String getRadioName() {
        return getSDKBean().getRadioName();
    }

    public void setRadioName(String radioName) {
        getSDKBean().setRadioName(radioName);
    }

    public int getPlaySource() {
        return getSDKBean().getPlaySource();
    }

    public void setPlaySource(int playSource) {
        getSDKBean().setPlaySource(playSource);
    }

    public long getTimeline() {
        return getSDKBean().getTimeline();
    }

    public void setTimeline(long timeline) {
        getSDKBean().setTimeline(timeline);
    }

    public String getTrackTitle() {
        return getSDKBean().getTrackTitle();
    }

    public void setTrackTitle(String trackTitle) {
        getSDKBean().setTrackTitle(trackTitle);
    }

    public int getOrderPositon() {
        return getSDKBean().getOrderPositon();
    }

    public void setOrderPositon(int orderPositon) {
        getSDKBean().setOrderPositon(orderPositon);
    }

    public int getPriceTypeEnum() {
        return getSDKBean().getPriceTypeEnum();
    }

    public void setPriceTypeEnum(int priceTypeEnum) {
        getSDKBean().setPriceTypeEnum(priceTypeEnum);
    }

    public long getDownloadTime() {
        return getSDKBean().getDownloadTime();
    }

    public void setDownloadTime(long downloadTime) {
        getSDKBean().setDownloadTime(downloadTime);
    }

    public String getTrackTags() {
        return getSDKBean().getTrackTags();
    }

    public void setTrackTags(String trackTags) {
        getSDKBean().setTrackTags(trackTags);
    }

    public String getTrackIntro() {
        return getSDKBean().getTrackIntro();
    }

    public void setTrackIntro(String trackIntro) {
        getSDKBean().setTrackIntro(trackIntro);
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

    public int getDuration() {
        return getSDKBean().getDuration();
    }

    public void setDuration(int duration) {
        getSDKBean().setDuration(duration);
    }

    public int getPlayCount() {
        return getSDKBean().getPlayCount();
    }

    public void setPlayCount(int playCount) {
        getSDKBean().setPlayCount(playCount);
    }

    public int getFavoriteCount() {
        return getSDKBean().getFavoriteCount();
    }

    public void setFavoriteCount(int favoriteCount) {
        getSDKBean().setFavoriteCount(favoriteCount);
    }

    public int getCommentCount() {
        return getSDKBean().getCommentCount();
    }

    public void setCommentCount(int commentCount) {
        getSDKBean().setCommentCount(commentCount);
    }

    public int getDownloadCount() {
        return getSDKBean().getDownloadCount();
    }

    public void setDownloadCount(int downloadCount) {
        getSDKBean().setDownloadCount(downloadCount);
    }

    public String getPlayUrl32() {
        return getSDKBean().getPlayUrl32();
    }

    public void setPlayUrl32(String playUrl32) {
        getSDKBean().setPlayUrl32(playUrl32);
    }

    public int getPlaySize32() {
        return getSDKBean().getPlaySize32();
    }

    public void setPlaySize32(int playSize32) {
        getSDKBean().setPlaySize32(playSize32);
    }

    public String getPlayUrl64() {
        return getSDKBean().getPlayUrl64();
    }

    public void setPlayUrl64(String playUrl64) {
        getSDKBean().setPlayUrl64(playUrl64);
    }

    public int getPlaySize64() {
        return getSDKBean().getPlaySize64();
    }

    public void setPlaySize64(int playSize64) {
        getSDKBean().setPlaySize64(playSize64);
    }

    public String getPlayUrl24M4a() {
        return getSDKBean().getPlayUrl24M4a();
    }

    public void setPlayUrl24M4a(String playUrl24M4a) {
        getSDKBean().setPlayUrl24M4a(playUrl24M4a);
    }

    public String getPlaySize24M4a() {
        return getSDKBean().getPlaySize24M4a();
    }

    public void setPlaySize24M4a(String playSize24M4a) {
        getSDKBean().setPlaySize24M4a(playSize24M4a);
    }

    public String getPlayUrl64M4a() {
        return getSDKBean().getPlayUrl64M4a();
    }

    public void setPlayUrl64M4a(String playUrl64M4a) {
        getSDKBean().setPlayUrl64M4a(playUrl64M4a);
    }

    public String getPlaySize64m4a() {
        return getSDKBean().getPlaySize64m4a();
    }

    public void setPlaySize64m4a(String playSize64m4a) {
        getSDKBean().setPlaySize64m4a(playSize64m4a);
    }

    public int getOrderNum() {
        return getSDKBean().getOrderNum();
    }

    public void setOrderNum(int orderNum) {
        getSDKBean().setOrderNum(orderNum);
    }

    public String getDownloadUrl() {
        return getSDKBean().getDownloadUrl();
    }

    public void setDownloadUrl(String downloadUrl) {
        getSDKBean().setDownloadUrl(downloadUrl);
    }

    public long getDownloadSize() {
        return getSDKBean().getDownloadSize();
    }

    public void setDownloadSize(long downloadSize) {
        getSDKBean().setDownloadSize(downloadSize);
    }

    public void setDownloadSizeForDownload(long downloadSize) {
        getSDKBean().setDownloadSizeForDownload(downloadSize);
    }

    public int getSource() {
        return getSDKBean().getSource();
    }

    public void setSource(int source) {
        getSDKBean().setSource(source);
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

    public String getDownloadedSaveFilePath() {
        return getSDKBean().getDownloadedSaveFilePath();
    }

    public void setDownloadedSaveFilePath(String downloadedSaveFilePath) {
        getSDKBean().setDownloadedSaveFilePath(downloadedSaveFilePath);
    }

    public long getDownloadedSize() {
        return getSDKBean().getDownloadedSize();
    }

    public void setDownloadedSize(long downloadedSize) {
        getSDKBean().setDownloadedSize(downloadedSize);
    }

    public int getTrackStatus() {
        return getSDKBean().getTrackStatus();
    }

    public void setTrackStatus(int trackStatus) {
        getSDKBean().setTrackStatus(trackStatus);
    }

    public int getDownloadStatus() {
        return getSDKBean().getDownloadStatus();
    }

    public void setDownloadStatus(int downloadStatus) {
        getSDKBean().setDownloadStatus(downloadStatus);
    }

    public String getSequenceId() {
        return getSDKBean().getSequenceId();
    }

    public void setSequenceId(String sequenceId) {
        getSDKBean().setSequenceId(sequenceId);
    }

    public int getInsertSequence() {
        return getSDKBean().getInsertSequence();
    }

    public void setInsertSequence(int insertSequence) {
        getSDKBean().setInsertSequence(insertSequence);
    }

    public boolean getExtra() {
        return getSDKBean().getExtra();
    }

    public void setExtra(boolean extra) {
        getSDKBean().setExtra(extra);
    }

    public String getStartTime() {
        return getSDKBean().getStartTime();
    }

    public void setStartTime(String startTime) {
        getSDKBean().setStartTime(startTime);
    }

    public String getEndTime() {
        return getSDKBean().getEndTime();
    }

    public void setEndTime(String endTime) {
        getSDKBean().setEndTime(endTime);
    }

    public long getScheduleId() {
        return getSDKBean().getScheduleId();
    }

    public void setScheduleId(long scheduleId) {
        getSDKBean().setScheduleId(scheduleId);
    }

    public String getRadioRate24AacUrl() {
        return getSDKBean().getRadioRate24AacUrl();
    }

    public void setRadioRate24AacUrl(String radioRate24AacUrl) {
        getSDKBean().setRadioRate24AacUrl(radioRate24AacUrl);
    }

    public String getRadioRate24TsUrl() {
        return getSDKBean().getRadioRate24TsUrl();
    }

    public void setRadioRate24TsUrl(String radioRate24TsUrl) {
        getSDKBean().setRadioRate24TsUrl(radioRate24TsUrl);
    }

    public String getRadioRate64AacUrl() {
        return getSDKBean().getRadioRate64AacUrl();
    }

    public void setRadioRate64AacUrl(String radioRate64AacUrl) {
        getSDKBean().setRadioRate64AacUrl(radioRate64AacUrl);
    }

    public String getRadioRate64TsUrl() {
        return getSDKBean().getRadioRate64TsUrl();
    }

    public void setRadioRate64TsUrl(String radioRate64TsUrl) {
        getSDKBean().setRadioRate64TsUrl(radioRate64TsUrl);
    }

    public long getProgramId() {
        return getSDKBean().getProgramId();
    }

    public void setProgramId(long programId) {
        getSDKBean().setProgramId(programId);
    }

    public long getRadioId() {
        return getSDKBean().getRadioId();
    }

    public void setRadioId(long radioId) {
        getSDKBean().setRadioId(radioId);
    }

    public boolean isLike() {
        return getSDKBean().isLike();
    }

    public void setLike(boolean isLike) {
        getSDKBean().setLike(isLike);
    }

    public long getDownloadCreated() {
        return getSDKBean().getDownloadCreated();
    }

    public void setDownloadCreated(long downloadCreated) {
        getSDKBean().setDownloadCreated(downloadCreated);
    }

    public long getLiveRoomId() {
        return getSDKBean().getLiveRoomId();
    }

    public void setLiveRoomId(long liveRoomId) {
        getSDKBean().setLiveRoomId(liveRoomId);
    }

    public int describeContents() {
        return 0;
    }


    public String getPlayPathHq() {
        return getSDKBean().getPlayPathHq();
    }

    public void setPlayPathHq(String playPathHq) {
        getSDKBean().setPlayPathHq(playPathHq);
    }

    public int getPriceTypeId() {
        return getSDKBean().getPriceTypeId();
    }

    public void setPriceTypeId(int priceTypeId) {
        getSDKBean().setPriceTypeId(priceTypeId);
    }

    public boolean isCanDownload() {
        return getSDKBean().isCanDownload();
    }

    public void setCanDownload(boolean canDownload) {
        getSDKBean().setCanDownload(canDownload);
    }

    public int getSampleDuration() {
        return getSDKBean().getSampleDuration();
    }

    public void setSampleDuration(int sampleDuration) {
        getSDKBean().setSampleDuration(sampleDuration);
    }

    public boolean isAudition() {
        return getSDKBean().isAudition();
    }

    public boolean canPlayTrack() {
        return getSDKBean().canPlayTrack();
    }

    public boolean isTrailer() {
        return getSDKBean().isTrailer();
    }

    public void setTrailer(boolean trailer) {
        getSDKBean().setTrailer(trailer);
    }

    public String getPlayUrlAmr() {
        return getSDKBean().getPlayUrlAmr();
    }

    public void setPlayUrlAmr(String playUrlAmr) {
        getSDKBean().setPlayUrlAmr(playUrlAmr);
    }

    public int getPlaySizeAmr() {
        return getSDKBean().getPlaySizeAmr();
    }

    public void setPlaySizeAmr(int playSizeAmr) {
        getSDKBean().setPlaySizeAmr(playSizeAmr);
    }

    public int getCategoryId() {
        return getSDKBean().getCategoryId();
    }

    public void setCategoryId(int categoryId) {
        getSDKBean().setCategoryId(categoryId);
    }

    public String getValidCover() {
        String validCover = getSDKBean().getCoverUrlMiddle();
        if (TextUtils.isEmpty(validCover)) {
            SubordinatedAlbum album = getSDKBean().getAlbum();
            if (album != null) {
                validCover = album.getValidCover();
            }
            if (TextUtils.isEmpty(validCover)) {
                validCover = getSDKBean().getCoverUrlSmall();
                if (TextUtils.isEmpty(validCover)) {
                    validCover = getSDKBean().getCoverUrlLarge();
                }
            }
            if (TextUtils.isEmpty(validCover)) {
                validCover = "";
            }
        }
        return validCover;
    }

    public String getTemplateUrl() {
        return getSDKBean().getTemplateUrl();
    }

    public void setTemplateUrl(String templateUrl) {
        getSDKBean().setTemplateUrl(templateUrl);
    }

    public int getTemplateId() {
        return getSDKBean().getTemplateId();
    }

    public void setTemplateId(int templateId) {
        getSDKBean().setTemplateId(templateId);
    }

    public String getTemplateName() {
        return getSDKBean().getTemplateName();
    }

    public void setTemplateName(String templateName) {
        getSDKBean().setTemplateName(templateName);
    }

    public boolean isHasSample() {
        return getSDKBean().isHasSample();
    }

    public void setHasSample(boolean hasSample) {
        getSDKBean().setHasSample(hasSample);
    }

    public void setOrderPositionInAlbum(int orderPositionInAlbum) {
        getSDKBean().setOrderPositionInAlbum(orderPositionInAlbum);
    }

    public int getOrderPositionInAlbum() {
        return getSDKBean().getOrderPositionInAlbum();
    }

    public boolean isHasCopyRight() {
        return getSDKBean().isHasCopyRight();
    }

    public void setHasCopyRight(boolean hasCopyRight) {
        getSDKBean().setHasCopyRight(hasCopyRight);
    }

    public boolean isUpdateStatus() {
        return getSDKBean().isUpdateStatus();
    }

    public void setUpdateStatus(boolean updateStatus) {
        getSDKBean().setUpdateStatus(updateStatus);
    }

    public long getChannelId() {
        return getSDKBean().getChannelId();
    }

    public void setChannelId(long channelId) {
        getSDKBean().setChannelId(channelId);
    }

    public XMAnnouncer getAnnouncer() {
        return new XMAnnouncer(getSDKBean().getAnnouncer());
    }

    public void setAnnouncer(XMAnnouncer announcer) {
        getSDKBean().setAnnouncer(announcer.getSDKBean());
    }

    @Nullable
    public XMSubordinatedAlbum getAlbum() {
        SubordinatedAlbum album = getSDKBean().getAlbum();
        if (album == null) {
            return null;
        }
        return new XMSubordinatedAlbum(album);
    }

    public void setAlbum(XMSubordinatedAlbum album) {
        getSDKBean().setAlbum(album.getSDKBean());
    }

    public int hashCode() {
        return getSDKBean().hashCode();
    }

    public boolean equals(Object obj) {
        return getSDKBean().equals(obj);
    }

    public String toString() {
        return getSDKBean().toString();
    }
}

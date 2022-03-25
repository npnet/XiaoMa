package com.xiaoma.xting.sdk.bean;

import com.xiaoma.adapter.base.XMBean;
import com.ximalaya.ting.android.opensdk.model.album.Announcer;

/**
 * @author youthyJ
 * @date 2018/10/11
 */
public class XMAnnouncer extends XMBean<Announcer> {
    public XMAnnouncer(Announcer announcer) {
        super(announcer);
    }

    public long getAnnouncerId() {
        return getSDKBean().getAnnouncerId();
    }

    public void setAnnouncerId(long announcerId) {
        getSDKBean().setAnnouncerId(announcerId);
    }

    public String getNickname() {
        return getSDKBean().getNickname();
    }

    public void setNickname(String nickname) {
        getSDKBean().setNickname(nickname);
    }

    public String getAvatarUrl() {
        return getSDKBean().getAvatarUrl();
    }

    public void setAvatarUrl(String avatarUrl) {
        getSDKBean().setAvatarUrl(avatarUrl);
    }

    public boolean isVerified() {
        return getSDKBean().isVerified();
    }

    public void setVerified(boolean verified) {
        getSDKBean().setVerified(verified);
    }

    public String getKind() {
        return getSDKBean().getKind();
    }

    public void setKind(String kind) {
        getSDKBean().setKind(kind);
    }

    public long getvCategoryId() {
        return getSDKBean().getvCategoryId();
    }

    public void setvCategoryId(long vCategoryId) {
        getSDKBean().setvCategoryId(vCategoryId);
    }

    public String getVdesc() {
        return getSDKBean().getVdesc();
    }

    public void setVdesc(String vdesc) {
        getSDKBean().setVdesc(vdesc);
    }

    public String getVsignature() {
        return getSDKBean().getVsignature();
    }

    public void setVsignature(String vsignature) {
        getSDKBean().setVsignature(vsignature);
    }

    public String getAnnouncerPosition() {
        return getSDKBean().getAnnouncerPosition();
    }

    public void setAnnouncerPosition(String announcerPosition) {
        getSDKBean().setAnnouncerPosition(announcerPosition);
    }

    public long getFollowerCount() {
        return getSDKBean().getFollowerCount();
    }

    public void setFollowerCount(long followerCount) {
        getSDKBean().setFollowerCount(followerCount);
    }

    public long getFollowingCount() {
        return getSDKBean().getFollowingCount();
    }

    public void setFollowingCount(long followingCount) {
        getSDKBean().setFollowingCount(followingCount);
    }

    public long getReleasedAlbumCount() {
        return getSDKBean().getReleasedAlbumCount();
    }

    public void setReleasedAlbumCount(long releasedAlbumCount) {
        getSDKBean().setReleasedAlbumCount(releasedAlbumCount);
    }

    public long getReleasedTrackCount() {
        return getSDKBean().getReleasedTrackCount();
    }

    public void setReleasedTrackCount(long releasedTrackCount) {
        getSDKBean().setReleasedTrackCount(releasedTrackCount);
    }

    public int getAnchorGrade() {
        return getSDKBean().getAnchorGrade();
    }

    public void setAnchorGrade(int anchorGrade) {
        getSDKBean().setAnchorGrade(anchorGrade);
    }

    public int getVerifyType() {
        return getSDKBean().getVerifyType();
    }

    public void setVerifyType(int verifyType) {
        getSDKBean().setVerifyType(verifyType);
    }

    public String toString() {
        return getSDKBean().toString();
    }
}

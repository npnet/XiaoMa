package com.xiaoma.utils.share;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Author: loren
 * Date: 2019/4/28 0028
 * 各应用分享内容到车信
 */

public class ShareClubBean implements Parcelable {
    private long carTeamId;//分享来源车队id
    private String fromPackage;//分享来源包名
    private String backAction;//点击分享消息需跳转回的action
    private String coreKey;//核心内容
    private String shareImage;//分享内容的图片
    private String shareTitle;//分享的标题
    private String shareContent;//分享的内容
    private String carTeamDetail;//分享的该车队人数、名字和口令，","隔开

    public String getFromPackage() {
        return fromPackage;
    }

    public void setFromPackage(String fromPackage) {
        this.fromPackage = fromPackage;
    }

    public String getBackAction() {
        return backAction;
    }

    public void setBackAction(String backAction) {
        this.backAction = backAction;
    }

    public String getCoreKey() {
        return coreKey;
    }

    public void setCoreKey(String coreKey) {
        this.coreKey = coreKey;
    }

    public String getShareImage() {
        return shareImage;
    }

    public void setShareImage(String shareImage) {
        this.shareImage = shareImage;
    }

    public String getShareTitle() {
        return shareTitle;
    }

    public void setShareTitle(String shareTitle) {
        this.shareTitle = shareTitle;
    }

    public String getShareContent() {
        return shareContent;
    }

    public void setShareContent(String shareContent) {
        this.shareContent = shareContent;
    }

    public String getCarTeamDetail() {
        return carTeamDetail;
    }

    public void setCarTeamDetail(String carTeamDetail) {
        this.carTeamDetail = carTeamDetail;
    }

    public long getCarTeamId() {
        return carTeamId;
    }

    public void setCarTeamId(long carTeamId) {
        this.carTeamId = carTeamId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.fromPackage);
        dest.writeString(this.backAction);
        dest.writeString(this.coreKey);
        dest.writeString(this.shareImage);
        dest.writeString(this.shareTitle);
        dest.writeString(this.shareContent);
        dest.writeString(this.carTeamDetail);
        dest.writeLong(this.carTeamId);
    }

    public ShareClubBean() {
    }

    protected ShareClubBean(Parcel in) {
        this.fromPackage = in.readString();
        this.backAction = in.readString();
        this.coreKey = in.readString();
        this.shareImage = in.readString();
        this.shareTitle = in.readString();
        this.shareContent = in.readString();
        this.carTeamDetail = in.readString();
        this.carTeamId = in.readLong();
    }

    public static final Parcelable.Creator<ShareClubBean> CREATOR = new Parcelable.Creator<ShareClubBean>() {
        public ShareClubBean createFromParcel(Parcel source) {
            return new ShareClubBean(source);
        }

        public ShareClubBean[] newArray(int size) {
            return new ShareClubBean[size];
        }
    };
}

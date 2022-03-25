package com.xiaoma.ad.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * @author KY
 * @date 2018/9/13
 */
public class Ad implements Serializable, Parcelable {

    private static final long serialVersionUID = 660063483309925906L;
    /**
     * id
     */
    private long id;
    /**
     * 图片地址
     */
    private String imgPath;
    /**
     * 跳转连接类型：1-web页；2-app页面或插件页面
     */
    private LinkType linkType;
    /**
     * 跳转链接
     */
    private String link;
    /**
     * 开始显示时间，时间戳
     */
    private long beginDate;
    /**
     * 结束显示时间，时间戳
     */
    private long endDate;
    /**
     * 显示时间，单位秒
     */
    private int showTime;
    /**
     * 是否可手动结束
     */
    private boolean aheadClosable;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getImgPath() {
        return imgPath;
    }

    public void setImgPath(String imgPath) {
        this.imgPath = imgPath;
    }

    public LinkType getLinkType() {
        return linkType;
    }

    public void setLinkType(LinkType linkType) {
        this.linkType = linkType;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public long getBeginDate() {
        return beginDate;
    }

    public void setBeginDate(long beginDate) {
        this.beginDate = beginDate;
    }

    public long getEndDate() {
        return endDate;
    }

    public void setEndDate(long endDate) {
        this.endDate = endDate;
    }

    public int getShowTime() {
        return showTime;
    }

    public void setShowTime(int showTime) {
        this.showTime = showTime;
    }

    public boolean isAheadClosable() {
        return aheadClosable;
    }

    public void setAheadClosable(boolean aheadClosable) {
        this.aheadClosable = aheadClosable;
    }

    public Ad(long id, String imgPath, LinkType linkType, String link, long beginDate, long endDate, int showTime, boolean aheadClosable) {
        this.id = id;
        this.imgPath = imgPath;
        this.linkType = linkType;
        this.link = link;
        this.beginDate = beginDate;
        this.endDate = endDate;
        this.showTime = showTime;
        this.aheadClosable = aheadClosable;
    }

    /*===============================Parcelable Start========================================*/

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.imgPath);
        dest.writeInt(this.linkType == null ? -1 : this.linkType.ordinal());
        dest.writeString(this.link);
        dest.writeLong(this.beginDate);
        dest.writeLong(this.endDate);
        dest.writeInt(this.showTime);
        dest.writeByte(this.aheadClosable ? (byte) 1 : (byte) 0);
    }

    protected Ad(Parcel in) {
        this.id = in.readLong();
        this.imgPath = in.readString();
        int tmpLinkType = in.readInt();
        this.linkType = tmpLinkType == -1 ? null : LinkType.values()[tmpLinkType];
        this.link = in.readString();
        this.beginDate = in.readLong();
        this.endDate = in.readLong();
        this.showTime = in.readInt();
        this.aheadClosable = in.readByte() != 0;
    }

    public static final Creator<Ad> CREATOR = new Creator<Ad>() {
        @Override
        public Ad createFromParcel(Parcel source) {
            return new Ad(source);
        }

        @Override
        public Ad[] newArray(int size) {
            return new Ad[size];
        }
    };
    /*=============================== Parcelable End ========================================*/

    @Override
    public String toString() {
        return "Ad{" +
                "id=" + id +
                ", imgPath='" + imgPath + '\'' +
                ", linkType=" + linkType +
                ", link='" + link + '\'' +
                ", beginDate=" + beginDate +
                ", endDate=" + endDate +
                ", showTime=" + showTime +
                ", aheadClosable=" + aheadClosable +
                '}';
    }
}

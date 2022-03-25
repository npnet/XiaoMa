package com.xiaoma.shop.business.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * <des>
 *
 * @author YangGang
 * @date 2019/5/20
 */
public class HologramDress implements Parcelable {
    /**
     * id : 102
     * name : 白色西装
     * description : 白色西装
     * version : 1
     * code : 3
     * picUrl : http://www.carbuyin.net/by2/qunHeader/caf4eaee-f31e-4656-95de-b2c3ecc41fe2.jpg
     * iconUrl : http://www.carbuyin.net/by2/qunHeader/8596386f-9e5d-416c-bd3a-4a61e2d25d62.png
     * defaultFlag : 0
     * needScore : 10
     * enableStatus : 1
     * type : 2
     * lockFlag : 1
     * pid : 97
     * hotFlag : 1
     * orderLevel : 3
     * createDate : 1532330479000
     * channelId : AA1090
     * userBuyFlag : 1
     * usedNum : 0
     * defaultUsedNum : 200
     */

    private int id;
    private String name;
    private String description;
    private String version;
    private String code;
    private String picUrl;
    private String iconUrl;
    private int defaultFlag;
    private int needScore;
    private int enableStatus;
    private int type;
    private int lockFlag;
    private int pid;
    private int hotFlag;
    private int orderLevel;
    private long createDate;
    private String channelId;
    private int userBuyFlag;
    private int usedNum;
    private int defaultUsedNum;

    private boolean isSelected;//是否选中状态

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public int getDefaultFlag() {
        return defaultFlag;
    }

    public void setDefaultFlag(int defaultFlag) {
        this.defaultFlag = defaultFlag;
    }

    public int getNeedScore() {
        return needScore;
    }

    public void setNeedScore(int needScore) {
        this.needScore = needScore;
    }

    public int getEnableStatus() {
        return enableStatus;
    }

    public void setEnableStatus(int enableStatus) {
        this.enableStatus = enableStatus;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getLockFlag() {
        return lockFlag;
    }

    public void setLockFlag(int lockFlag) {
        this.lockFlag = lockFlag;
    }

    public int getPid() {
        return pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }

    public int getHotFlag() {
        return hotFlag;
    }

    public void setHotFlag(int hotFlag) {
        this.hotFlag = hotFlag;
    }

    public int getOrderLevel() {
        return orderLevel;
    }

    public void setOrderLevel(int orderLevel) {
        this.orderLevel = orderLevel;
    }

    public long getCreateDate() {
        return createDate;
    }

    public void setCreateDate(long createDate) {
        this.createDate = createDate;
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public int getUserBuyFlag() {
        return userBuyFlag;
    }

    public void setUserBuyFlag(int userBuyFlag) {
        this.userBuyFlag = userBuyFlag;
    }

    public int getUsedNum() {
        return usedNum;
    }

    public void setUsedNum(int usedNum) {
        this.usedNum = usedNum;
    }

    public int getDefaultUsedNum() {
        return defaultUsedNum;
    }

    public void setDefaultUsedNum(int defaultUsedNum) {
        this.defaultUsedNum = defaultUsedNum;
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.name);
        dest.writeString(this.description);
        dest.writeString(this.version);
        dest.writeString(this.code);
        dest.writeString(this.picUrl);
        dest.writeString(this.iconUrl);
        dest.writeInt(this.defaultFlag);
        dest.writeInt(this.needScore);
        dest.writeInt(this.enableStatus);
        dest.writeInt(this.type);
        dest.writeInt(this.lockFlag);
        dest.writeInt(this.pid);
        dest.writeInt(this.hotFlag);
        dest.writeInt(this.orderLevel);
        dest.writeLong(this.createDate);
        dest.writeString(this.channelId);
        dest.writeInt(this.userBuyFlag);
        dest.writeInt(this.usedNum);
        dest.writeInt(this.defaultUsedNum);
    }

    public HologramDress() {}

    protected HologramDress(Parcel in) {
        this.id = in.readInt();
        this.name = in.readString();
        this.description = in.readString();
        this.version = in.readString();
        this.code = in.readString();
        this.picUrl = in.readString();
        this.iconUrl = in.readString();
        this.defaultFlag = in.readInt();
        this.needScore = in.readInt();
        this.enableStatus = in.readInt();
        this.type = in.readInt();
        this.lockFlag = in.readInt();
        this.pid = in.readInt();
        this.hotFlag = in.readInt();
        this.orderLevel = in.readInt();
        this.createDate = in.readLong();
        this.channelId = in.readString();
        this.userBuyFlag = in.readInt();
        this.usedNum = in.readInt();
        this.defaultUsedNum = in.readInt();
    }

    public static final Parcelable.Creator<HologramDress> CREATOR = new Parcelable.Creator<HologramDress>() {
        @Override
        public HologramDress createFromParcel(Parcel source) {return new HologramDress(source);}

        @Override
        public HologramDress[] newArray(int size) {return new HologramDress[size];}
    };
}

package com.xiaoma.pet.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * <pre>
 *   @author Create by on Gillben
 *   date:   2019/5/29 0029 14:34
 *   desc:   关卡资源(下载实体)
 * </pre>
 */
public class ChapterResourceInfo implements Parcelable {


    public static final Creator<ChapterResourceInfo> CREATOR = new Creator<ChapterResourceInfo>() {
        @Override
        public ChapterResourceInfo createFromParcel(Parcel in) {
            return new ChapterResourceInfo(in);
        }

        @Override
        public ChapterResourceInfo[] newArray(int size) {
            return new ChapterResourceInfo[size];
        }
    };
    /**
     * channelId : AA1090
     * createDate : 1554954812000
     * disabledFlag : 1
     * effeIcon : http://www.carbuyin.net/by2/qunHeader/cf9ddb4d-4b2a-429c-b9eb-82af8d998c38.png
     * enableStatus : 1
     * gameChapterId : 1
     * id : 1
     * modifyDate : null
     * resourcesDesc : 版本V1.0 场景资源
     * resourcesName : 场景资源1
     * resourcesRoute : http://www.carbuyin.net/by2/qunHeader/test1.ab
     * resourcesType : 1
     * resourcesVersion : V1.0
     */

    private String channelId;
    private long createDate;
    private int disabledFlag;
    private String effeIcon;
    private int enableStatus;
    private int gameChapterId;
    private int id;
    private String modifyDate;
    private String resourcesDesc;
    private String resourcesName;
    private String resourcesRoute;
    private String resourcesType;
    private String resourcesVersion;

    protected ChapterResourceInfo(Parcel in) {
        channelId = in.readString();
        createDate = in.readLong();
        disabledFlag = in.readInt();
        effeIcon = in.readString();
        enableStatus = in.readInt();
        gameChapterId = in.readInt();
        id = in.readInt();
        modifyDate = in.readString();
        resourcesDesc = in.readString();
        resourcesName = in.readString();
        resourcesRoute = in.readString();
        resourcesType = in.readString();
        resourcesVersion = in.readString();
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public long getCreateDate() {
        return createDate;
    }

    public void setCreateDate(long createDate) {
        this.createDate = createDate;
    }

    public int getDisabledFlag() {
        return disabledFlag;
    }

    public void setDisabledFlag(int disabledFlag) {
        this.disabledFlag = disabledFlag;
    }

    public String getEffeIcon() {
        return effeIcon;
    }

    public void setEffeIcon(String effeIcon) {
        this.effeIcon = effeIcon;
    }

    public int getEnableStatus() {
        return enableStatus;
    }

    public void setEnableStatus(int enableStatus) {
        this.enableStatus = enableStatus;
    }

    public int getGameChapterId() {
        return gameChapterId;
    }

    public void setGameChapterId(int gameChapterId) {
        this.gameChapterId = gameChapterId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getModifyDate() {
        return modifyDate;
    }

    public void setModifyDate(String modifyDate) {
        this.modifyDate = modifyDate;
    }

    public String getResourcesDesc() {
        return resourcesDesc;
    }

    public void setResourcesDesc(String resourcesDesc) {
        this.resourcesDesc = resourcesDesc;
    }

    public String getResourcesName() {
        return resourcesName;
    }

    public void setResourcesName(String resourcesName) {
        this.resourcesName = resourcesName;
    }

    public String getResourcesRoute() {
        return resourcesRoute;
    }

    public void setResourcesRoute(String resourcesRoute) {
        this.resourcesRoute = resourcesRoute;
    }

    public String getResourcesType() {
        return resourcesType;
    }

    public void setResourcesType(String resourcesType) {
        this.resourcesType = resourcesType;
    }

    public String getResourcesVersion() {
        return resourcesVersion;
    }

    public void setResourcesVersion(String resourcesVersion) {
        this.resourcesVersion = resourcesVersion;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(channelId);
        dest.writeLong(createDate);
        dest.writeInt(disabledFlag);
        dest.writeString(effeIcon);
        dest.writeInt(enableStatus);
        dest.writeInt(gameChapterId);
        dest.writeInt(id);
        dest.writeString(modifyDate);
        dest.writeString(resourcesDesc);
        dest.writeString(resourcesName);
        dest.writeString(resourcesRoute);
        dest.writeString(resourcesType);
        dest.writeString(resourcesVersion);
    }
}

package com.xiaoma.pet.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by Gillben on 2019/1/4 0004
 * <p>
 * desc: 礼物
 */
public class PetGiftInfo implements Parcelable {

    public static final Creator<PetGiftInfo> CREATOR = new Creator<PetGiftInfo>() {
        @Override
        public PetGiftInfo createFromParcel(Parcel in) {
            return new PetGiftInfo(in);
        }

        @Override
        public PetGiftInfo[] newArray(int size) {
            return new PetGiftInfo[size];
        }
    };
    /**
     * id : 4
     * createDate : 2019-04-11 17:50:33
     * modifyDate : null
     * enableStatus : 1
     * channelId : AA1090
     * uid : 854533803434188800
     * disabledFlag : 1
     */

    private int id;
    private String createDate;
    private String modifyDate;
    private int enableStatus;
    private List<RewardDetails> gameRewardVoList;
    private String channelId;
    private long uid;
    private int disabledFlag;

    protected PetGiftInfo(Parcel in) {
        id = in.readInt();
        createDate = in.readString();
        modifyDate = in.readString();
        enableStatus = in.readInt();
        gameRewardVoList = in.createTypedArrayList(RewardDetails.CREATOR);
        channelId = in.readString();
        uid = in.readLong();
        disabledFlag = in.readInt();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getModifyDate() {
        return modifyDate;
    }

    public void setModifyDate(String modifyDate) {
        this.modifyDate = modifyDate;
    }

    public int getEnableStatus() {
        return enableStatus;
    }

    public void setEnableStatus(int enableStatus) {
        this.enableStatus = enableStatus;
    }

    public List<RewardDetails> getGameRewardVoList() {
        return gameRewardVoList;
    }

    public void setGameRewardVoList(List<RewardDetails> gameRewardVoList) {
        this.gameRewardVoList = gameRewardVoList;
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public int getDisabledFlag() {
        return disabledFlag;
    }

    public void setDisabledFlag(int disabledFlag) {
        this.disabledFlag = disabledFlag;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(createDate);
        dest.writeString(modifyDate);
        dest.writeInt(enableStatus);
        dest.writeTypedList(gameRewardVoList);
        dest.writeString(channelId);
        dest.writeLong(uid);
        dest.writeInt(disabledFlag);
    }
}

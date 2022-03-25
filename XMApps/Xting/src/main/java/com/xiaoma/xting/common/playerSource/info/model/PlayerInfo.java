package com.xiaoma.xting.common.playerSource.info.model;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.Html;

import com.xiaoma.utils.StringUtil;
import com.xiaoma.xting.R;
import com.xiaoma.xting.common.adapter.IGalleryData;
import com.xiaoma.xting.common.playerSource.contract.PlayerAction;
import com.xiaoma.xting.common.playerSource.contract.PlayerSourceType;
import com.xiaoma.xting.common.playerSource.info.impl.PlayerInfoImpl;

/**
 * <des>
 * 从数据源返回的数据中抽取并获得对应的数据bean类
 *
 * @author YangGang
 * @date 2019/5/27
 */

public class PlayerInfo implements Parcelable, IGalleryData {

    private @PlayerSourceType
    int type;
    private long albumId;
    private long programId;
    private int sourceSubType;
    private String imgUrl;
    private String albumName;
    private String programName;
    private long updateTime;
    private long playCount;
    private int page = 1;
    private long progress;
    private long duration = -1;
    private String extra1;
    private String extra2;
    private int categoryId;
    private transient @PlayerAction
    int action;

    private transient Bundle extraInfo;

    public PlayerInfo() {
    }

    public long getAlbumId() {
        return albumId;
    }

    public void setAlbumId(long albumId) {
        this.albumId = albumId;
    }

    public long getProgramId() {
        return programId;
    }

    public void setProgramId(long programId) {
        this.programId = programId;
    }

    public int getSourceSubType() {
        return sourceSubType;
    }

    public void setSourceSubType(int sourceSubType) {
        this.sourceSubType = sourceSubType;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getAlbumName() {
        return albumName;
    }

    public void setAlbumName(String albumName) {
        this.albumName = albumName;
    }

    public String getProgramName() {
        return programName;
    }

    public void setProgramName(String programName) {
        this.programName = programName;
    }

    public long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
    }

    public long getPlayCount() {
        return playCount;
    }

    public void setPlayCount(long playCount) {
        this.playCount = playCount;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public long getProgress() {
        return progress;
    }

    public int getType() {
        return type;
    }

    public int getAction() {
        return action;
    }

    public void setAction(int action) {
        this.action = action;
    }

    @Override
    public String getCoverUrl() {
        return imgUrl;
    }

    @Override
    public int getSourceType() {
        return type;
    }

    public void setProgress(long progress) {
        this.progress = progress;
    }

    public long getDuration() {
        if (duration == 0) {
            duration = -1;
        }
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public String getExtra2() {
        return extra2;
    }

    public void setExtra2(String extra2) {
        this.extra2 = extra2;
    }

    public String getExtra1() {
        return extra1;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Bundle getExtraInfo() {
        if (extraInfo == null) {
            extraInfo = new Bundle();
        }
        return extraInfo;
    }

    public void setExtraInfo(Bundle extraInfo) {
        this.extraInfo = extraInfo;
    }

    public boolean isFromRecordF() {
        return getExtraInfo().getBoolean(PlayerInfoImpl.EXTRA_HISTORY, false);
    }

    public void setFromRecordF(boolean fromRecordF) {
        getExtraInfo().putBoolean(PlayerInfoImpl.EXTRA_HISTORY, fromRecordF);
    }

    protected PlayerInfo(Parcel in) {
        this.type = in.readInt();
        this.albumId = in.readLong();
        this.programId = in.readLong();
        this.sourceSubType = in.readInt();
        this.imgUrl = in.readString();
        this.albumName = in.readString();
        this.programName = in.readString();
        this.updateTime = in.readLong();
        this.playCount = in.readLong();
        this.page = in.readInt();
        this.progress = in.readLong();
        this.duration = in.readLong();
        this.extra1 = in.readString();
        this.extra2 = in.readString();
        this.categoryId = in.readInt();
    }

    public int getCategoryId() {
        return categoryId;
    }

    public boolean isPreShowF() {
        return getExtraInfo().getBoolean(PlayerInfoImpl.EXTRA_PRE_SHOW);
    }

    public void setPreShowF(boolean preShowF) {
        getExtraInfo().putBoolean(PlayerInfoImpl.EXTRA_PRE_SHOW, preShowF);
    }

    @Override
    public CharSequence getFooterText(Context context) {
        return null;
    }

    @Override
    public CharSequence getBottomText(Context context) {
        return Html.fromHtml(context.getString(R.string.str_count_listener, "<font color=\"#fbd3a4\">" + StringUtil.convertBigDecimal(getPlayCount()) + "</font>"));
    }

    @Override
    public long getUUID() {
        return getAlbumId();
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public void setExtra1(String extra1) {
        this.extra1 = extra1;
    }

    @Override
    public CharSequence getTitleText(Context context) {
        return albumName;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (!(obj instanceof PlayerInfo)) return false;
        PlayerInfo tempPlayerInfo = (PlayerInfo) obj;
        return type == tempPlayerInfo.getType()
                && sourceSubType == tempPlayerInfo.getSourceSubType()
                && programId == tempPlayerInfo.getProgramId()
                && albumId == tempPlayerInfo.getAlbumId();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.type);
        dest.writeLong(this.albumId);
        dest.writeLong(this.programId);
        dest.writeInt(this.sourceSubType);
        dest.writeString(this.imgUrl);
        dest.writeString(this.albumName);
        dest.writeString(this.programName);
        dest.writeLong(this.updateTime);
        dest.writeLong(this.playCount);
        dest.writeInt(this.page);
        dest.writeLong(this.progress);
        dest.writeLong(this.duration);
        dest.writeString(this.extra1);
        dest.writeString(this.extra2);
        dest.writeInt(this.categoryId);
    }

    public static final Creator<PlayerInfo> CREATOR = new Creator<PlayerInfo>() {
        @Override
        public PlayerInfo createFromParcel(Parcel source) {
            return new PlayerInfo(source);
        }

        @Override
        public PlayerInfo[] newArray(int size) {
            return new PlayerInfo[size];
        }
    };

    @Override
    public String toString() {
        return "PlayerInfo{" +
                "type=" + type +
                ", albumId=" + albumId +
                ", programId=" + programId +
                ", sourceSubType=" + sourceSubType +
                ", imgUrl='" + imgUrl + '\'' +
                ", albumName='" + albumName + '\'' +
                ", programName='" + programName + '\'' +
                ", updateTime=" + updateTime +
                ", playCount=" + playCount +
                ", page=" + page +
                ", progress=" + progress +
                ", duration=" + duration +
                ", extra1='" + extra1 + '\'' +
                ", extra2='" + extra2 + '\'' +
                ", categoryId=" + categoryId +
                ", action=" + action +
                ", extraInfo=" + extraInfo +
                '}';
    }
}

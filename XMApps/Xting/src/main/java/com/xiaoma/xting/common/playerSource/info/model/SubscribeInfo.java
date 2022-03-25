package com.xiaoma.xting.common.playerSource.info.model;

import android.arch.persistence.room.Entity;
import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.Html;

import com.xiaoma.utils.TimeUtils;
import com.xiaoma.xting.R;
import com.xiaoma.xting.common.adapter.IGalleryData;
import com.xiaoma.xting.common.playerSource.contract.PlayerSourceType;

/**
 * <des>
 * 本地收藏类
 *
 * @author YangGang
 * @date 2019/5/27
 */
@Entity(primaryKeys = {"type", "albumId"})
public class SubscribeInfo implements Parcelable, IGalleryData {

    //主键
    private @PlayerSourceType
    int type;
    private long albumId;
    private String imgUrl;
    private String albumName;
    private String programName;
    private long subscribeTime;  //收藏时间 用于排序显示
    private int sourceSubType;   //用于知道是某个音源的某种类型的节目
    private long updateTime; //节目更新时间

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public long getAlbumId() {
        return albumId;
    }

    public void setAlbumId(long albumId) {
        this.albumId = albumId;
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

    public long getSubscribeTime() {
        return subscribeTime;
    }

    public void setSubscribeTime(long subscribeTime) {
        this.subscribeTime = subscribeTime;
    }

    public int getSourceSubType() {
        return sourceSubType;
    }

    public void setSourceSubType(int sourceSubType) {
        this.sourceSubType = sourceSubType;
    }

    public long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
    }

    public static final Creator<SubscribeInfo> CREATOR = new Creator<SubscribeInfo>() {
        @Override
        public SubscribeInfo createFromParcel(Parcel source) {
            return new SubscribeInfo(source);
        }

        @Override
        public SubscribeInfo[] newArray(int size) {
            return new SubscribeInfo[size];
        }
    };

    public SubscribeInfo() {
    }

    protected SubscribeInfo(Parcel in) {
        this.type = in.readInt();
        this.albumId = in.readLong();
        this.imgUrl = in.readString();
        this.albumName = in.readString();
        this.programName = in.readString();
        this.subscribeTime = in.readLong();
        this.sourceSubType = in.readInt();
        this.updateTime = in.readLong();
    }

    @Override
    public String getCoverUrl() {
        return imgUrl;
    }

    @Override
    public CharSequence getTitleText(Context context) {
        if (type == PlayerSourceType.RADIO_YQ) {
            return programName;
        }
        return albumName;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.type);
        dest.writeLong(this.albumId);
        dest.writeString(this.imgUrl);
        dest.writeString(this.albumName);
        dest.writeString(this.programName);
        dest.writeLong(this.subscribeTime);
        dest.writeInt(this.sourceSubType);
        dest.writeLong(this.updateTime);
    }

    @Override
    public CharSequence getFooterText(Context context) {
        return null;
    }

    @Override
    public CharSequence getBottomText(Context context) {
        if (type == PlayerSourceType.RADIO_YQ) return null;
        return Html.fromHtml(context.getString(R.string.str_duration_from_update, "<font color=\"#fbd3a4\">" + TimeUtils.getFriendlyTimeSpan(updateTime) + "</font>"));
//
//        return SpanUtils.with(context).append(TimeUtils.getFriendlyTimeSpan(updateTime))
//                .setForegroundColorRes(R.color.mine_item_time).append(context.getString(R.string.update))
//                .setForegroundColorRes(R.color.mine_item_update).create();
    }

    @Override
    public long getUUID() {
        return albumId;
    }

    @Override
    public int getSourceType() {
        return type;
    }
}

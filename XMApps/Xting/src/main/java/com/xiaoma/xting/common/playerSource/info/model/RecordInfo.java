package com.xiaoma.xting.common.playerSource.info.model;

import android.arch.persistence.room.Entity;
import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.Html;

import com.xiaoma.utils.TimeUtils;
import com.xiaoma.xting.R;
import com.xiaoma.xting.common.adapter.IGalleryData;
import com.xiaoma.xting.common.playerSource.contract.PlayerSourceSubType;
import com.xiaoma.xting.common.playerSource.contract.PlayerSourceType;

/**
 * <des>
 * 收听记录类
 *
 * @author YangGang
 * @date 2019/5/27
 */
@Entity(primaryKeys = {"type", "albumId", "programId"})
public class RecordInfo implements Parcelable, IGalleryData {

    //主键
    private @PlayerSourceType
    int type;
    private long albumId;
    private long programId;
    private int sourceSubType;   //用于知道是某个音源的某种类型的节目
    private String imgUrl;
    private String albumName;
    private String programName;
    private int page;
    private long listenTime;  //收听的时间 用于排序显示
    private long progress;
    private long duration = -1;

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

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public long getListenTime() {
        return listenTime;
    }

    public void setListenTime(long listenTime) {
        this.listenTime = listenTime;
    }

    public long getProgress() {
        return progress;
    }

    public void setProgress(long progress) {
        this.progress = progress;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        if (duration == 0) {
            duration = -1;
        }
        this.duration = duration;
    }

    @Override
    public String getCoverUrl() {
        return imgUrl;
    }

    public static final Creator<RecordInfo> CREATOR = new Creator<RecordInfo>() {
        @Override
        public RecordInfo createFromParcel(Parcel source) {
            return new RecordInfo(source);
        }

        @Override
        public RecordInfo[] newArray(int size) {
            return new RecordInfo[size];
        }
    };

    @Override
    public CharSequence getFooterText(Context context) {
        return null;
    }

    @Override
    public CharSequence getBottomText(Context context) {
        if (type == PlayerSourceType.KOALA
                || type == PlayerSourceType.RADIO_XM
                || (type == PlayerSourceType.HIMALAYAN && sourceSubType == PlayerSourceSubType.TRACK)) {
            return Html.fromHtml(context.getString(R.string.str_percent_listened, "<font color=\"#fbd3a4\">" + String.valueOf((int) progress * 100 / duration) + "%" + "</font>"));
//            return SpanUtils.with(context)
//                    .append(context.getString(R.string.listened)).setForegroundColorRes(R.color.mine_item_update)
//                    .append(String.valueOf((int) progress * 100 / duration) + "%").setForegroundColorRes(R.color.mine_item_time)
//                    .create();
        } else {
            return Html.fromHtml(context.getString(R.string.str_time_from_listen, "<font color=\"#fbd3a4\">" + TimeUtils.getFriendlyTimeSpan(listenTime) + "</font>"));
//            return SpanUtils.with(context)
//                    .append(context.getString(R.string.hint_listen_radio, TimeUtils.getFriendlyTimeSpan(listenTime))).setForegroundColorRes(R.color.mine_item_time)
//                    .append(context.getString(R.string.listen_radio)).setForegroundColorRes(R.color.mine_item_update)
//                    .create();
        }

    }

    @Override
    public long getUUID() {
        return programId;
    }

    @Override
    public int getSourceType() {
        return type;
    }

    public RecordInfo() {
    }

    protected RecordInfo(Parcel in) {
        this.type = in.readInt();
        this.albumId = in.readLong();
        this.programId = in.readLong();
        this.sourceSubType = in.readInt();
        this.imgUrl = in.readString();
        this.albumName = in.readString();
        this.programName = in.readString();
        this.page = in.readInt();
        this.listenTime = in.readLong();
        this.progress = in.readLong();
        this.duration = in.readLong();
    }

    @Override
    public CharSequence getTitleText(Context context) {
        if (sourceSubType == PlayerSourceSubType.RADIO) {
            return albumName;
        }
        if (programName == null) {
            return albumName;
        }
        return programName;
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
        dest.writeInt(this.page);
        dest.writeLong(this.listenTime);
        dest.writeLong(this.progress);
        dest.writeLong(this.duration);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (!(obj instanceof RecordInfo)) return false;
        RecordInfo recordInfo = (RecordInfo) obj;
        return type == recordInfo.getType()
                && sourceSubType == recordInfo.getSourceSubType()
                && albumId == recordInfo.getAlbumId()
                && programId == recordInfo.getProgramId();
    }
}

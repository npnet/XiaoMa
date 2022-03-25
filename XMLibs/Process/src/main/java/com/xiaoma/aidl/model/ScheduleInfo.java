package com.xiaoma.aidl.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;

import com.litesuits.orm.db.annotation.PrimaryKey;
import com.litesuits.orm.db.annotation.Table;
import com.litesuits.orm.db.enums.AssignType;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 日程信息
 *
 * @author dengcong
 */
@Table("schedule_info")
public class ScheduleInfo implements Comparable<ScheduleInfo>, Parcelable {
    @PrimaryKey(AssignType.AUTO_INCREMENT)
    public long localId;
    /**
     * 日期  2018/09/16
     */
    private String date;
    /**
     * 时间  10:12
     */
    private String time;
    /**
     * 时间戳
     */
    private long timestamp;
    /**
     * 信息
     */
    private String message;


    private String createId;
    /**
     * 是否同步
     */
    private boolean isUpload;

    private String location;

    private boolean isShowDeleteView;

    private long needDelayTime;


    private String startTime;
    private String endTime;
    private String component;
    private String packageName;
    //提醒类型
    private int type = RemindType.NORMAL;

    //电台fm信息
    private String infoMsg;

    //车服务标题
    private String carServiceTitle;
    private boolean isBefore;
    private String dateBefore;

    public String getDateBefore() {
        return dateBefore;
    }

    public void setDateBefore(String dateBefore) {
        this.dateBefore = dateBefore;
    }

    public boolean isBefore() {
        return isBefore;
    }

    public void setBefore(boolean before) {
        isBefore = before;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getCarServiceTitle() {
        return carServiceTitle;
    }

    public void setCarServiceTitle(String carServiceTitle) {
        this.carServiceTitle = carServiceTitle;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getComponent() {
        return component;
    }

    public void setComponent(String component) {
        this.component = component;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getInfoMsg() {
        return infoMsg;
    }

    public void setInfoMsg(String infoMsg) {
        this.infoMsg = infoMsg;
    }

    public ScheduleInfo() {
    }

    public ScheduleInfo(String date, String time, String message) {
        this.date = date;
        this.time = time;
        this.message = message;
    }


    public long getNeedDelayTime() {
        return needDelayTime;
    }

    public boolean isShowDeleteView() {
        return isShowDeleteView;
    }

    public void setNeedDelayTime(long needDelayTime) {
        this.needDelayTime = needDelayTime;
    }

    public void setShowDeleteView(boolean showDeleteView) {
        isShowDeleteView = showDeleteView;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCreateId() {
        return createId;
    }

    public void setCreateId(String createId) {
        this.createId = createId;
    }

    public boolean getIsUpload() {
        return isUpload;
    }

    public void setIsUpload(boolean upload) {
        isUpload = upload;
    }


    @Override
    public String toString() {
        return "ScheduleInfo{" +
                "date='" + date + '\'' +
                ", time='" + time + '\'' +
                ", timestamp=" + timestamp + '\'' +
                ", message='" + message + '\'' +
                ", createId='" + createId + '\'' +
                ", getIsUpload='" + isUpload + '\'' +
                '}';
    }

    private long getTime(ScheduleInfo o) {
        return o.getTimestamp();
    }

    @Override
    public int compareTo(@NonNull ScheduleInfo o) {
        long time = o.getTime(o);
        long time1 = getTime(this);
        return (int) (time1 - time);
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.localId);
        dest.writeString(this.date);
        dest.writeString(this.time);
        dest.writeLong(this.timestamp);
        dest.writeString(this.message);
        dest.writeString(this.createId);
        dest.writeBoolean(this.isUpload);
        dest.writeString(this.location);
        dest.writeByte(this.isShowDeleteView ? (byte) 1 : (byte) 0);
        dest.writeLong(this.needDelayTime);
        dest.writeString(this.startTime);
        dest.writeString(this.endTime);
        dest.writeString(this.component);
        dest.writeString(this.packageName);
        dest.writeInt(this.type);
        dest.writeString(this.infoMsg);
        dest.writeString(this.carServiceTitle);
    }

    protected ScheduleInfo(Parcel in) {
        this.localId = in.readLong();
        this.date = in.readString();
        this.time = in.readString();
        this.timestamp = in.readLong();
        this.message = in.readString();
        this.createId = in.readString();
        this.isUpload = in.readBoolean();
        this.location = in.readString();
        this.isShowDeleteView = in.readByte() != 0;
        this.needDelayTime = in.readLong();
        this.startTime = in.readString();
        this.endTime = in.readString();
        this.component = in.readString();
        this.packageName = in.readString();
        this.type = in.readInt();
        this.infoMsg = in.readString();
        this.carServiceTitle = in.readString();
    }

    public static final Creator<ScheduleInfo> CREATOR = new Creator<ScheduleInfo>() {
        @Override
        public ScheduleInfo createFromParcel(Parcel source) {
            return new ScheduleInfo(source);
        }

        @Override
        public ScheduleInfo[] newArray(int size) {
            return new ScheduleInfo[size];
        }
    };

    @IntDef(value = {RemindType.NORMAL, RemindType.FM, RemindType.CAR_SERVICE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface RemindType {
        int NORMAL = 0;
        int FM = 1;
        int CAR_SERVICE = 2;
    }
}

package com.xiaoma.aidl.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import com.litesuits.orm.db.annotation.PrimaryKey;
import com.litesuits.orm.db.annotation.Table;
import com.litesuits.orm.db.enums.AssignType;
import java.util.Objects;

/**
 * @Author ZiXu Huang
 * @Data 2018/12/3
 */
@Table("BluetoothPhone_Collection")
public class ContactBean implements Parcelable, Cloneable {

    @PrimaryKey(AssignType.BY_MYSELF)
    private String id;
    private String name;
    private String phoneNum;
    private String date;
    private String time;
    private long callStartTime; //通话开始时间
    private String pinYin;
    private String firstPinYin;
    private boolean isCollected;
    private long elapsedTime;
    private byte[] icon;
    private int callType;
    private String timeStamp;
    private boolean isLastOne;
    private int phoneType;
    private boolean isMute;
    private int beforeState;
    private boolean isAnswerOnPhone = false;
    private Boolean isFullCall ;

    public Boolean getFullCall() {
        return isFullCall;
    }

    public void setFullCall(Boolean fullCall) {
        isFullCall = fullCall;
    }

    public boolean getIsAnswerOnPhone() {
        return isAnswerOnPhone;
    }

    public void setIsAnswerOnPhone(boolean isAnswerOnPhone) {
        this.isAnswerOnPhone = isAnswerOnPhone;
    }

    public int getPhoneType() {
        return phoneType;
    }

    public void setPhoneType(int phoneType) {
        this.phoneType = phoneType;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public int getCallType() {
        return callType;
    }

    public void setCallType(int callType) {
        this.callType = callType;
    }

    public ContactBean() {}

    public ContactBean(String name, String phoneNum) {
        this.name = name;
        this.phoneNum = phoneNum;
    }

    public byte[] getIcon() {
        return icon;
    }

    public void setIcon(byte[] icon) {
        this.icon = icon;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    private int status;//0,1,2.....

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public long getCallStartTime() {
        return callStartTime;
    }

    public void setCallStartTime(long callStartTime) {
        this.callStartTime = callStartTime;
    }

    public long getElapsedTime() {
        return elapsedTime;
    }

    public void setElapsedTime(long elapsedTime) {
        this.elapsedTime = elapsedTime;
    }

    public String getPinYin() {
        return pinYin;
    }

    public void setPinYin(String pinYin) {
        this.pinYin = pinYin;
    }

    public String getFirstPinYin() {
        return firstPinYin;
    }

    public void setFirstPinYin(String firstPinYin) {
        this.firstPinYin = firstPinYin;
    }

    public boolean isCollected() {
        return isCollected;
    }

    public void setCollected(boolean collected) {
        isCollected = collected;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isLastOne() {
        return isLastOne;
    }

    public void setLastOne(boolean lastOne) {
        isLastOne = lastOne;
    }

    public boolean isMute() {
        return isMute;
    }

    public void setMute(boolean mute) {
        isMute = mute;
    }

    public int getBeforeState() {
        return beforeState;
    }

    public void setBeforeState(int beforeState) {
        this.beforeState = beforeState;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if ((obj == null) || (obj.getClass() != this.getClass())) {
            return false;
        }
        ContactBean bean = (ContactBean) obj;
        return TextUtils.equals(id, bean.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.name);
        dest.writeString(this.phoneNum);
        dest.writeString(this.date);
        dest.writeString(this.time);
        dest.writeLong(this.callStartTime);
        dest.writeString(this.pinYin);
        dest.writeString(this.firstPinYin);
        dest.writeByte(this.isCollected ? (byte) 1 : (byte) 0);
        dest.writeLong(this.elapsedTime);
        dest.writeByteArray(this.icon);
        dest.writeInt(this.callType);
        dest.writeString(this.timeStamp);
        dest.writeByte(this.isLastOne ? (byte) 1 : (byte) 0);
        dest.writeInt(this.phoneType);
        dest.writeByte(this.isMute ? (byte) 1 : (byte) 0);
        dest.writeInt(this.beforeState);
        dest.writeByte(this.isAnswerOnPhone ? (byte) 1 : (byte) 0);
        dest.writeInt(this.status);
    }

    protected ContactBean(Parcel in) {
        this.id = in.readString();
        this.name = in.readString();
        this.phoneNum = in.readString();
        this.date = in.readString();
        this.time = in.readString();
        this.callStartTime = in.readLong();
        this.pinYin = in.readString();
        this.firstPinYin = in.readString();
        this.isCollected = in.readByte() != 0;
        this.elapsedTime = in.readLong();
        this.icon = in.createByteArray();
        this.callType = in.readInt();
        this.timeStamp = in.readString();
        this.isLastOne = in.readByte() != 0;
        this.phoneType = in.readInt();
        this.isMute = in.readByte() != 0;
        this.beforeState = in.readInt();
        this.isAnswerOnPhone = in.readByte() != 0;
        this.status = in.readInt();
    }

    public static final Creator<ContactBean> CREATOR = new Creator<ContactBean>() {
        @Override
        public ContactBean createFromParcel(Parcel source) {
            return new ContactBean(source);
        }

        @Override
        public ContactBean[] newArray(int size) {
            return new ContactBean[size];
        }
    };

    @Override
    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            // this shouldn't happen, since we are Cloneable
            throw new InternalError(e);
        }
    }
}

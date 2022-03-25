package com.xiaoma.launcher.travel.hotel.calendar;

import android.os.Parcel;
import android.os.Parcelable;

public class DateBean implements Parcelable {
    private int[] solar;//阳历年、月、日
    private String[] lunar;//农历月、日
    private String solarHoliday;//阳历节假日
    private String lunarHoliday;//阳历节假日
    private int type;//0:上月，1:当月，2:下月
    private String term;//节气
    private boolean isExpire; //是否 是过去的日子


    public int[] getSolar() {
        return solar;
    }

    public void setSolar(int year, int month, int day) {
        this.solar = new int[]{year, month, day};
    }

    public String[] getLunar() {
        return lunar;
    }

    public void setLunar(String[] lunar) {
        this.lunar = lunar;
    }

    public String getSolarHoliday() {
        return solarHoliday;
    }

    public void setSolarHoliday(String solarHoliday) {
        this.solarHoliday = solarHoliday;
    }

    public String getLunarHoliday() {
        return lunarHoliday;
    }

    public void setLunarHoliday(String lunarHoliday) {
        this.lunarHoliday = lunarHoliday;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    public boolean isExpire() {
        return isExpire;
    }

    public void setExpire(boolean expire) {
        isExpire = expire;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeIntArray(this.solar);
        dest.writeStringArray(this.lunar);
        dest.writeString(this.solarHoliday);
        dest.writeString(this.lunarHoliday);
        dest.writeInt(this.type);
        dest.writeString(this.term);
        dest.writeByte(this.isExpire ? (byte) 1 : (byte) 0);
    }

    public DateBean() {
    }

    protected DateBean(Parcel in) {
        this.solar = in.createIntArray();
        this.lunar = in.createStringArray();
        this.solarHoliday = in.readString();
        this.lunarHoliday = in.readString();
        this.type = in.readInt();
        this.term = in.readString();
        this.isExpire = in.readByte() != 0;
    }

    public static final Parcelable.Creator<DateBean> CREATOR = new Parcelable.Creator<DateBean>() {
        @Override
        public DateBean createFromParcel(Parcel source) {
            return new DateBean(source);
        }

        @Override
        public DateBean[] newArray(int size) {
            return new DateBean[size];
        }
    };
}
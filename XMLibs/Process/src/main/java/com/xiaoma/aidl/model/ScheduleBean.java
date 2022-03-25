package com.xiaoma.aidl.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * User:Created by Terence
 * IDE: Android Studio
 * Date:2018/11/14
 * Desc:
 */
public class ScheduleBean implements Parcelable{

    /**
     * datetime : {"dateOrig":"明天","type":"DT_BASIC","time":"15:00:00","timeOrig":"下午3点","date":"2018-05-25"}
     * name : reminder
     * content : 开会
     */

    private DatetimeBean datetime;
    private String name;
    private String content;

    public DatetimeBean getDatetime() {
        return datetime;
    }

    public void setDatetime(DatetimeBean datetime) {
        this.datetime = datetime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public static class DatetimeBean implements Parcelable{
        /**
         * dateOrig : 明天
         * type : DT_BASIC
         * time : 15:00:00
         * timeOrig : 下午3点
         * date : 2018-05-25
         */

        private String dateOrig;
        private String type;
        private String time;
        private String timeOrig;
        private String date;

        public String getDateOrig() {
            return dateOrig;
        }

        public void setDateOrig(String dateOrig) {
            this.dateOrig = dateOrig;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }

        public String getTimeOrig() {
            return timeOrig;
        }

        public void setTimeOrig(String timeOrig) {
            this.timeOrig = timeOrig;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.dateOrig);
            dest.writeString(this.type);
            dest.writeString(this.time);
            dest.writeString(this.timeOrig);
            dest.writeString(this.date);
        }

        public DatetimeBean() {
        }

        protected DatetimeBean(Parcel in) {
            this.dateOrig = in.readString();
            this.type = in.readString();
            this.time = in.readString();
            this.timeOrig = in.readString();
            this.date = in.readString();
        }

        public static final Creator<DatetimeBean> CREATOR = new Creator<DatetimeBean>() {
            @Override
            public DatetimeBean createFromParcel(Parcel source) {
                return new DatetimeBean(source);
            }

            @Override
            public DatetimeBean[] newArray(int size) {
                return new DatetimeBean[size];
            }
        };
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.datetime, flags);
        dest.writeString(this.name);
        dest.writeString(this.content);
    }

    public ScheduleBean() {
    }

    protected ScheduleBean(Parcel in) {
        this.datetime = in.readParcelable(DatetimeBean.class.getClassLoader());
        this.name = in.readString();
        this.content = in.readString();
    }

    public static final Creator<ScheduleBean> CREATOR = new Creator<ScheduleBean>() {
        @Override
        public ScheduleBean createFromParcel(Parcel source) {
            return new ScheduleBean(source);
        }

        @Override
        public ScheduleBean[] newArray(int size) {
            return new ScheduleBean[size];
        }
    };
}

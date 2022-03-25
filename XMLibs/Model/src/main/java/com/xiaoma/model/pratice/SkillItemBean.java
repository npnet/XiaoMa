package com.xiaoma.model.pratice;


/*  @project_name：  XMAgateOS
 *  @package_name：  com.xiaoma.vrpractice.model
 *  @file_name:      SkillItemBean
 *  @author:         Rookie
 *  @create_time:    2019/6/12 14:52
 *  @description：   TODO             */

import android.os.Parcel;
import android.os.Parcelable;

public class SkillItemBean implements Parcelable {


    /**
     * "id": 1,
     * "icon": "http://www.carbuyin.net/by2/filePath/a98f810a-49d6-4beb-9002-39ec1752377a.png",
     * "text": "问候播报",
     * "type": "tts",
     * "sort": 3,
     * "status": "1",
     * "packageName": "",
     * "className": "",
     * "textEng": "tts",
     * "playTts":"来了"
     */
    private int id;
    private String icon;
    private String text;
    private String type;
    private int sort;
    private String status;
    private String packageName;
    private String className;
    private String textEng;
    private String playTts;

    private boolean isDark;

    public boolean isDark() {
        return isDark;
    }

    public void setDark(boolean dark) {
        isDark = dark;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getTextEng() {
        return textEng;
    }

    public void setTextEng(String textEng) {
        this.textEng = textEng;
    }

    public String getPlayTts() {
        return playTts;
    }

    public void setPlayTts(String playTts) {
        this.playTts = playTts;
    }

    public SkillItemBean() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.icon);
        dest.writeString(this.text);
        dest.writeString(this.type);
        dest.writeInt(this.sort);
        dest.writeString(this.status);
        dest.writeString(this.packageName);
        dest.writeString(this.className);
        dest.writeString(this.textEng);
        dest.writeString(this.playTts);
        dest.writeByte(this.isDark ? (byte) 1 : (byte) 0);
    }

    protected SkillItemBean(Parcel in) {
        this.id = in.readInt();
        this.icon = in.readString();
        this.text = in.readString();
        this.type = in.readString();
        this.sort = in.readInt();
        this.status = in.readString();
        this.packageName = in.readString();
        this.className = in.readString();
        this.textEng = in.readString();
        this.playTts = in.readString();
        this.isDark = in.readByte() != 0;
    }

    public static final Creator<SkillItemBean> CREATOR = new Creator<SkillItemBean>() {
        @Override
        public SkillItemBean createFromParcel(Parcel source) {
            return new SkillItemBean(source);
        }

        @Override
        public SkillItemBean[] newArray(int size) {
            return new SkillItemBean[size];
        }
    };
}

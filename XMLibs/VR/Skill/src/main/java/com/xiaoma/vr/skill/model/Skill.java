package com.xiaoma.vr.skill.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author youthyJ
 * @date 2019/6/14
 */
public class Skill implements Parcelable {
    public static final Creator<Skill> CREATOR = new Creator<Skill>() {
        @Override
        public Skill createFromParcel(Parcel in) {
            return new Skill(in);
        }

        @Override
        public Skill[] newArray(int size) {
            return new Skill[size];
        }
    };
    private String packageName;
    private String skillName;
    private String skillDesc;
    private String extra;

    protected Skill(Parcel in) {
        readFromParcel(in);
    }

    public Skill() {

    }

    public Skill(String packageName, String skillName, String skillDesc, String extra) {
        this.packageName = packageName;
        this.skillName = skillName;
        this.skillDesc = skillDesc;
        this.extra = extra;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getSkillName() {
        return skillName;
    }

    public void setSkillName(String skillName) {
        this.skillName = skillName;
    }

    public String getSkillDesc() {
        return skillDesc;
    }

    public void setSkillDesc(String skillDesc) {
        this.skillDesc = skillDesc;
    }

    public String getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(packageName);
        parcel.writeString(skillName);
        parcel.writeString(skillDesc);
        parcel.writeString(extra);
    }

    public void readFromParcel(Parcel in) {
        packageName = in.readString();
        skillName = in.readString();
        skillDesc = in.readString();
        extra = in.readString();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() +
                " packageName:" + packageName +
                " skillName:" + skillName +
                " skillDesc:" + skillDesc +
                " extra:" + extra;
    }
}

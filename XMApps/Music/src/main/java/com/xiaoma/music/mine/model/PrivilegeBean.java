package com.xiaoma.music.mine.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * <pre>
 *   @author Create by on Gillben
 *   date:   2019/6/28 0028 15:34
 *   desc:   优享特权
 * </pre>
 */
public class PrivilegeBean implements Parcelable {


    public static final Creator<PrivilegeBean> CREATOR = new Creator<PrivilegeBean>() {
        @Override
        public PrivilegeBean createFromParcel(Parcel in) {
            return new PrivilegeBean(in);
        }

        @Override
        public PrivilegeBean[] newArray(int size) {
            return new PrivilegeBean[size];
        }
    };
    /**
     * id : 273
     * value : 畅享百万付费曲库
     * label : 畅享百万付费曲库
     * type : vip_music_privilege
     * description : 精品音乐特权项
     * sort : 10
     * delFlag : 0
     * remarks :
     * newRecord : true
     */

    private int id;
    private String value;
    private String label;
    private String type;
    private String description;
    private int sort;
    private int delFlag;
    private String remarks;
    private boolean newRecord;

    protected PrivilegeBean(Parcel in) {
        id = in.readInt();
        value = in.readString();
        label = in.readString();
        type = in.readString();
        description = in.readString();
        sort = in.readInt();
        delFlag = in.readInt();
        remarks = in.readString();
        newRecord = in.readByte() != 0;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }

    public int getDelFlag() {
        return delFlag;
    }

    public void setDelFlag(int delFlag) {
        this.delFlag = delFlag;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public boolean isNewRecord() {
        return newRecord;
    }

    public void setNewRecord(boolean newRecord) {
        this.newRecord = newRecord;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(value);
        dest.writeString(label);
        dest.writeString(type);
        dest.writeString(description);
        dest.writeInt(sort);
        dest.writeInt(delFlag);
        dest.writeString(remarks);
        dest.writeByte((byte) (newRecord ? 1 : 0));
    }
}

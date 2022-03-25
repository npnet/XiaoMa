package com.xiaoma.model.pratice;


/*  @project_name：  XMAgateOS
 *  @package_name：  com.xiaoma.vrpractice.model
 *  @file_name:      UserSkillItemsBean
 *  @author:         Rookie
 *  @create_time:    2019/6/12 15:30
 *  @description：   TODO             */

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.chad.library.adapter.base.entity.MultiItemEntity;

public class UserSkillItemsBean implements MultiItemEntity, Comparable<UserSkillItemsBean>, Parcelable {
    /**
     * id : 1
     * itemId : 1
     * skillId : 1
     * content : 新的一天还是你
     * type : tts
     * skillItem : {"icon":"http://www.carbuyin.net/by2/filePath/a98f810a-49d6-4beb-9002-39ec1752377a.png","text":"问候播报","type":"tts","sort":1,"status":"1"}
     */

    public static final int TYPE_ADD_SKILL = -1;
    public static final int TYPE_ACTION = 0;

    private int itemType;


    @Override
    public int getItemType() {
        return itemType;
    }

    public void setItemType(int itemType) {
        this.itemType = itemType;
    }

    public UserSkillItemsBean(int itemType) {
        this.itemType = itemType;
    }

    public UserSkillItemsBean(int itemId, int itemType) {
        this.itemId = itemId;
        this.itemType = itemType;
    }

    private int id;
    private int itemId;
    private int skillId;
    private String content;
    private String type;
    private SkillItemBean skillItem;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public int getSkillId() {
        return skillId;
    }

    public void setSkillId(int skillId) {
        this.skillId = skillId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public SkillItemBean getSkillItem() {
        return skillItem;
    }

    public void setSkillItem(SkillItemBean skillItem) {
        this.skillItem = skillItem;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeInt(this.itemId);
        dest.writeInt(this.skillId);
        dest.writeString(this.content);
        dest.writeString(this.type);
        dest.writeParcelable(this.skillItem, flags);
    }

    public UserSkillItemsBean() {
    }

    protected UserSkillItemsBean(Parcel in) {
        this.id = in.readInt();
        this.itemId = in.readInt();
        this.skillId = in.readInt();
        this.content = in.readString();
        this.type = in.readString();
        this.skillItem = in.readParcelable(SkillItemBean.class.getClassLoader());
    }

    public static final Parcelable.Creator<UserSkillItemsBean> CREATOR = new Parcelable.Creator<UserSkillItemsBean>() {
        @Override
        public UserSkillItemsBean createFromParcel(Parcel source) {
            return new UserSkillItemsBean(source);
        }

        @Override
        public UserSkillItemsBean[] newArray(int size) {
            return new UserSkillItemsBean[size];
        }
    };

    @Override
    public int compareTo(@NonNull UserSkillItemsBean o) {
        return o.getItemType() - getItemType();
    }
}

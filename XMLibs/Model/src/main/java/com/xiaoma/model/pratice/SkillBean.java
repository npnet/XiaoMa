package com.xiaoma.model.pratice;


/*  @project_name：  XMAgateOS
 *  @package_name：  com.xiaoma.vrpractice.model
 *  @file_name:      SkillBean
 *  @author:         Rookie
 *  @create_time:    2019/6/3 15:43
 *  @description：   TODO             */

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class SkillBean implements Parcelable {

    /**
     * id : 1
     * channelId : AA1090
     * word : 早上好
     * userId : 187
     * userSkillItems : [{"id":1,"itemId":1,"skillId":1,"content":"新的一天还是你","type":"tts","skillItem":{"icon":"http://www.carbuyin.net/by2/filePath/a98f810a-49d6-4beb-9002-39ec1752377a.png","text":"问候播报","type":"tts","sort":1,"status":"1"}},{"id":2,"itemId":2,"skillId":1,"content":"mp3","type":"music","skillItem":{"icon":"http://www.carbuyin.net/by2/filePath/a98f810a-49d6-4beb-9002-39ec1752377a.png","text":"播放音乐","type":"music","sort":2,"status":"1"}}]
     */

    private int id;
    private String channelId;
    private String word;
    private String userId;
    private List<UserSkillItemsBean> userSkillItems;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public List<UserSkillItemsBean> getUserSkillItems() {
        return userSkillItems;
    }

    public void setUserSkillItems(List<UserSkillItemsBean> userSkillItems) {
        this.userSkillItems = userSkillItems;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.channelId);
        dest.writeString(this.word);
        dest.writeString(this.userId);
        dest.writeTypedList(this.userSkillItems);
    }

    public SkillBean() {
    }

    protected SkillBean(Parcel in) {
        this.id = in.readInt();
        this.channelId = in.readString();
        this.word = in.readString();
        this.userId = in.readString();
        this.userSkillItems = in.createTypedArrayList(UserSkillItemsBean.CREATOR);
    }

    public static final Creator<SkillBean> CREATOR = new Creator<SkillBean>() {
        @Override
        public SkillBean createFromParcel(Parcel source) {
            return new SkillBean(source);
        }

        @Override
        public SkillBean[] newArray(int size) {
            return new SkillBean[size];
        }
    };
}

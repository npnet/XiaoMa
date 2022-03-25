package com.xiaoma.smarthome.login.model;


/*  @project_name：  XMAgateOS
 *  @package_name：  com.xiaoma.smarthome.login.model
 *  @file_name:      XiaoMiBean
 *  @author:         Rookie
 *  @create_time:    2019/4/23 10:53
 *  @description：   TODO             */

import android.os.Parcel;
import android.os.Parcelable;

public class XiaoMiBean implements Parcelable {

    /**
     * miId : 1042529189
     * accessToken : V3_umVV_2N3ROXpjfnblKk1hw27aWA9o0yVWGlHE4flGxvOvjXu18mrS2FADl8uStiEOLzvFQ27YdOqXYi-Te8iA9weCuilAdpIgz4yPdfT_BZO7uHeDXiLV7dBNcsoMqWr
     * userId : 1042529189
     * type : android
     * macKey : et0FHa3-qiXh53GgVo6TuCSw4_s
     * macAlgorithm : HmacSHA1
     * mexpiresIn : 7776000
     */

    private String miId;
    private String accessToken;
    private String userId;
    private String type;
    private String macKey;
    private String macAlgorithm;
    private int mexpiresIn;

    public String getMiId() {
        return miId;
    }

    public void setMiId(String miId) {
        this.miId = miId;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMacKey() {
        return macKey;
    }

    public void setMacKey(String macKey) {
        this.macKey = macKey;
    }

    public String getMacAlgorithm() {
        return macAlgorithm;
    }

    public void setMacAlgorithm(String macAlgorithm) {
        this.macAlgorithm = macAlgorithm;
    }

    public int getMexpiresIn() {
        return mexpiresIn;
    }

    public void setMexpiresIn(int mexpiresIn) {
        this.mexpiresIn = mexpiresIn;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.miId);
        dest.writeString(this.accessToken);
        dest.writeString(this.userId);
        dest.writeString(this.type);
        dest.writeString(this.macKey);
        dest.writeString(this.macAlgorithm);
        dest.writeInt(this.mexpiresIn);
    }

    public XiaoMiBean() {
    }

    protected XiaoMiBean(Parcel in) {
        this.miId = in.readString();
        this.accessToken = in.readString();
        this.userId = in.readString();
        this.type = in.readString();
        this.macKey = in.readString();
        this.macAlgorithm = in.readString();
        this.mexpiresIn = in.readInt();
    }

    public static final Parcelable.Creator<XiaoMiBean> CREATOR = new Parcelable.Creator<XiaoMiBean>() {
        @Override
        public XiaoMiBean createFromParcel(Parcel source) {
            return new XiaoMiBean(source);
        }

        @Override
        public XiaoMiBean[] newArray(int size) {
            return new XiaoMiBean[size];
        }
    };
}

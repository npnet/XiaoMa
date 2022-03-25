package com.xiaoma.smarthome.login.model;


/*  @project_name：  XMAgateOS
 *  @package_name：  com.xiaoma.smarthome.login.model
 *  @file_name:      CloudMIBean
 *  @author:         Rookie
 *  @create_time:    2019/4/23 10:42
 *  @description：   TODO             */

import android.os.Parcel;
import android.os.Parcelable;

public class CloudMIBean implements Parcelable {


    /**
     * token : fGGuQDBf8bZyjYbr
     * "headImg": "https://cdn.cnbj2.fds.api.mi-img.com/viomi-fileupload/images/user_head/1437322/nh1wR2htZs46f2EpwD1.png?GalaxyAccessKeyId=EAKC4WAFZQV4K&Expires=361555575496676&Signature=l6mRxl1yH+E9MZe/tADk/T/NdSw=",
     * "nickName": "Jie",
     * xiaomi : {"miId":"1042529189","accessToken":"V3_umVV_2N3ROXpjfnblKk1hw27aWA9o0yVWGlHE4flGxvOvjXu18mrS2FADl8uStiEOLzvFQ27YdOqXYi-Te8iA9weCuilAdpIgz4yPdfT_BZO7uHeDXiLV7dBNcsoMqWr","userId":"1042529189","type":"android","macKey":"et0FHa3-qiXh53GgVo6TuCSw4_s","macAlgorithm":"HmacSHA1","mexpiresIn":7776000}
     */

    private String token;
    private String headImg;
    private String nickName;
    private XiaoMiBean xiaomi;

    public String getHeadImg() {
        return headImg;
    }

    public void setHeadImg(String headImg) {
        this.headImg = headImg;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public XiaoMiBean getXiaomi() {
        return xiaomi;
    }

    public void setXiaomi(XiaoMiBean xiaomi) {
        this.xiaomi = xiaomi;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.token);
        dest.writeString(this.headImg);
        dest.writeString(this.nickName);
        dest.writeParcelable(this.xiaomi, flags);
    }

    public CloudMIBean() {
    }

    protected CloudMIBean(Parcel in) {
        this.token = in.readString();
        this.headImg = in.readString();
        this.nickName = in.readString();
        this.xiaomi = in.readParcelable(XiaoMiBean.class.getClassLoader());
    }

    public static final Parcelable.Creator<CloudMIBean> CREATOR = new Parcelable.Creator<CloudMIBean>() {
        @Override
        public CloudMIBean createFromParcel(Parcel source) {
            return new CloudMIBean(source);
        }

        @Override
        public CloudMIBean[] newArray(int size) {
            return new CloudMIBean[size];
        }
    };
}

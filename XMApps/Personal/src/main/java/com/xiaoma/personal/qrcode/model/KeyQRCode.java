package com.xiaoma.personal.qrcode.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * <pre>
 *   @author Create by on Gillben
 *   date:   2019/6/21 0021 15:46
 *   desc:
 * </pre>
 */
public class KeyQRCode implements Parcelable {

    public static final Creator<KeyQRCode> CREATOR = new Creator<KeyQRCode>() {
        @Override
        public KeyQRCode createFromParcel(Parcel in) {
            return new KeyQRCode(in);
        }

        @Override
        public KeyQRCode[] newArray(int size) {
            return new KeyQRCode[size];
        }
    };
    private String qrcode;
    private String desc;

    protected KeyQRCode(Parcel in) {
        qrcode = in.readString();
        desc = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(qrcode);
        dest.writeString(desc);
    }

    public String getQrcode() {
        return qrcode;
    }

    public void setQrcode(String qrcode) {
        this.qrcode = qrcode;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}

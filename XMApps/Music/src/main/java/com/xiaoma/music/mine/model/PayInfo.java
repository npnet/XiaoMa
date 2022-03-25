package com.xiaoma.music.mine.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * <pre>
 *   @author Create by on Gillben
 *   date:   2019/6/25 0025 17:51
 *   desc:   支付信息
 * </pre>
 */
public class PayInfo implements Parcelable {


    public static final Creator<PayInfo> CREATOR = new Creator<PayInfo>() {
        @Override
        public PayInfo createFromParcel(Parcel in) {
            return new PayInfo(in);
        }

        @Override
        public PayInfo[] newArray(int size) {
            return new PayInfo[size];
        }
    };
    /**
     * qrCode : http://www.carbuyin.net/by2/wechatQr/f02da3fb-df39-474a-a2af-ccb02daaee8f.png
     * id : 2213
     * lastpayDate : 1561434567879
     * createDate : 1561432767879
     */

    private String qrCode;
    private int id;
    private long lastpayDate;
    private long createDate;

    protected PayInfo(Parcel in) {
        qrCode = in.readString();
        id = in.readInt();
        lastpayDate = in.readLong();
        createDate = in.readLong();
    }

    public String getQrCode() {
        return qrCode;
    }

    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getLastpayDate() {
        return lastpayDate;
    }

    public void setLastpayDate(long lastpayDate) {
        this.lastpayDate = lastpayDate;
    }

    public long getCreateDate() {
        return createDate;
    }

    public void setCreateDate(long createDate) {
        this.createDate = createDate;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(qrCode);
        dest.writeInt(id);
        dest.writeLong(lastpayDate);
        dest.writeLong(createDate);
    }
}

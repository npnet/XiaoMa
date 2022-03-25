package com.xiaoma.cariflytek;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author: iSun
 * @date: 2019/3/28 0028
 */
public class WakeUpInfo implements Parcelable {
    /**
     * nMvwScene : 32
     * nMvwId : 0
     * nMvwScore : 1546
     * wakeup : 1
     * nStartBytes : 20800
     * nEndBytes : 37760
     * nKeyword : 导航到
     * PowerValue : 3.5742633951232E13
     */

    private int nMvwScene;
    private int nMvwId;
    private int nMvwScore;
    private int wakeup;
    private int nStartBytes;
    private int nEndBytes;
    private String nKeyword;
    private float PowerValue;
    private boolean isLeft;
    //int nMvwScene, int nMvwId, int nMvwScore, String lParam


    public int getnMvwScene() {
        return nMvwScene;
    }

    public void setnMvwScene(int nMvwScene) {
        this.nMvwScene = nMvwScene;
    }

    public int getnMvwId() {
        return nMvwId;
    }

    public void setnMvwId(int nMvwId) {
        this.nMvwId = nMvwId;
    }

    public int getnMvwScore() {
        return nMvwScore;
    }

    public void setnMvwScore(int nMvwScore) {
        this.nMvwScore = nMvwScore;
    }

    public int getWakeup() {
        return wakeup;
    }

    public void setWakeup(int wakeup) {
        this.wakeup = wakeup;
    }

    public int getnStartBytes() {
        return nStartBytes;
    }

    public void setnStartBytes(int nStartBytes) {
        this.nStartBytes = nStartBytes;
    }

    public int getnEndBytes() {
        return nEndBytes;
    }

    public void setnEndBytes(int nEndBytes) {
        this.nEndBytes = nEndBytes;
    }

    public String getnKeyword() {
        return nKeyword;
    }

    public void setnKeyword(String nKeyword) {
        this.nKeyword = nKeyword;
    }

    public float getPowerValue() {
        return PowerValue;
    }

    public void setPowerValue(float powerValue) {
        PowerValue = powerValue;
    }

    public boolean isLeft() {
        return isLeft;
    }

    public void setLeft(boolean left) {
        isLeft = left;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.nMvwScene);
        dest.writeInt(this.nMvwId);
        dest.writeInt(this.nMvwScore);
        dest.writeInt(this.wakeup);
        dest.writeInt(this.nStartBytes);
        dest.writeInt(this.nEndBytes);
        dest.writeString(this.nKeyword);
        dest.writeFloat(this.PowerValue);
        dest.writeByte(this.isLeft ? (byte) 1 : (byte) 0);
    }

    public WakeUpInfo() {
    }

    protected WakeUpInfo(Parcel in) {
        this.nMvwScene = in.readInt();
        this.nMvwId = in.readInt();
        this.nMvwScore = in.readInt();
        this.wakeup = in.readInt();
        this.nStartBytes = in.readInt();
        this.nEndBytes = in.readInt();
        this.nKeyword = in.readString();
        this.PowerValue = in.readFloat();
        this.isLeft = in.readByte() != 0;
    }

    public static final Creator<WakeUpInfo> CREATOR = new Creator<WakeUpInfo>() {
        @Override
        public WakeUpInfo createFromParcel(Parcel source) {
            return new WakeUpInfo(source);
        }

        @Override
        public WakeUpInfo[] newArray(int size) {
            return new WakeUpInfo[size];
        }
    };
}

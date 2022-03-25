package com.xiaoma.hotfix.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author youthyJ
 * @date 2018/11/15
 */
public class PatchConfig implements Parcelable {
    public static final Creator<PatchConfig> CREATOR = new Creator<PatchConfig>() {
        @Override
        public PatchConfig createFromParcel(Parcel in) {
            return new PatchConfig(in);
        }

        @Override
        public PatchConfig[] newArray(int size) {
            return new PatchConfig[size];
        }
    };
    private String basePkgVersion;
    private int patchVersion;

    public PatchConfig() {
    }

    protected PatchConfig(Parcel in) {
        readFromParcel(in);
    }

    public String getBasePkgVersion() {
        return basePkgVersion;
    }

    public void setBasePkgVersion(String basePkgVersion) {
        this.basePkgVersion = basePkgVersion;
    }

    public int getPatchVersion() {
        return patchVersion;
    }

    public void setPatchVersion(int patchVersion) {
        this.patchVersion = patchVersion;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(basePkgVersion);
        dest.writeInt(patchVersion);
    }

    public void readFromParcel(Parcel in) {
        basePkgVersion = in.readString();
        patchVersion = in.readInt();
    }
}

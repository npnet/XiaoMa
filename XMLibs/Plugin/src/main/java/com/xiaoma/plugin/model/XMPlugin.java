package com.xiaoma.plugin.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.xiaoma.model.XMResult;

import java.io.Serializable;
import java.util.List;

/**
 * Created by ZYao.
 * Date ：2018/9/18 0018
 */
public class XMPlugin extends XMResult<List<PluginInfo>> implements Serializable, Parcelable {
    public static final Creator<XMPlugin> CREATOR = new Creator<XMPlugin>() {
        @Override
        public XMPlugin createFromParcel(Parcel in) {
            return new XMPlugin(in);
        }

        @Override
        public XMPlugin[] newArray(int size) {
            return new XMPlugin[size];
        }
    };

    protected XMPlugin(Parcel in) {
        super(in);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }
}

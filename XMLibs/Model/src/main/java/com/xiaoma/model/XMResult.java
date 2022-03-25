package com.xiaoma.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: Administrator
 * Date: 2015/8/7
 * Time: 16:18
 */
public class XMResult<T> implements Serializable, Parcelable {
    public static final Creator<XMResult> CREATOR = new Creator<XMResult>() {
        @Override
        public XMResult createFromParcel(Parcel in) {
            return new XMResult(in);
        }

        @Override
        public XMResult[] newArray(int size) {
            return new XMResult[size];
        }
    };
    private int resultCode;
    private String resultMessage;
    private T data;

    protected XMResult(Parcel in) {
        resultCode = in.readInt();
        resultMessage = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(resultCode);
        dest.writeString(resultMessage);
    }

    public int getResultCode() {
        return resultCode;
    }

    public void setResultCode(int resultCode) {
        this.resultCode = resultCode;
    }

    public String getResultMessage() {
        return resultMessage;
    }

    public void setResultMessage(String resultMessage) {
        this.resultMessage = resultMessage;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public boolean isSuccess() {
        return resultCode == ModelConstants.ResultCode.RESULT_OK;
    }
}

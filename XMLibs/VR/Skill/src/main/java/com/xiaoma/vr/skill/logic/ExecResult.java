package com.xiaoma.vr.skill.logic;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author youthyJ
 * @date 2019/6/17
 */
public class ExecResult implements Parcelable {
    public static final Creator<ExecResult> CREATOR = new Creator<ExecResult>() {
        @Override
        public ExecResult createFromParcel(Parcel in) {
            return new ExecResult(in);
        }

        @Override
        public ExecResult[] newArray(int size) {
            return new ExecResult[size];
        }
    };
    private int code; // 错误码
    private String tts; // 需要播报的内容
    private String result; // 额外数据

    protected ExecResult(Parcel in) {
        readFromParcel(in);
    }

    public ExecResult(String tts, String result) {
        this.tts = tts;
        this.result = result;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getTts() {
        return tts;
    }

    public void setTts(String tts) {
        this.tts = tts;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(code);
        dest.writeString(tts);
        dest.writeString(result);
    }

    public void readFromParcel(Parcel in) {
        code = in.readInt();
        tts = in.readString();
        result = in.readString();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() +
                " code:" + code +
                " tts:" + tts +
                " result:" + result;
    }

}

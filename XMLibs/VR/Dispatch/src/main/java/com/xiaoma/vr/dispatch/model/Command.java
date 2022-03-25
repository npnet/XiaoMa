package com.xiaoma.vr.dispatch.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Objects;

/**
 * @author youthyJ
 * @date 2019/3/11
 */
public class Command implements Parcelable {
    public static final Creator<Command> CREATOR = new Creator<Command>() {
        @Override
        public Command createFromParcel(Parcel in) {
            return new Command(in);
        }

        @Override
        public Command[] newArray(int size) {
            return new Command[size];
        }
    };
    private final long createDate;
    private final String content;

    protected Command(Parcel in) {
        this.createDate = in.readLong();
        this.content = in.readString();
    }

    public Command(String content) {
        this.createDate = System.currentTimeMillis();
        this.content = content;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(createDate);
        dest.writeString(content);
    }

    @Override
    public String toString() {
        return new StringBuilder()
                .append("Dispatch [")
                .append(content)
                .append("]")
                .toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Command)) {
            return false;
        }
        if (content == null) {
            return false;
        }
        if (createDate < 0) {
            return false;
        }
        Command other = (Command) obj;
        if (!content.equals(other.content)) {
            return false;
        }
        return createDate == other.createDate;
    }

    @Override
    public int hashCode() {
        return Objects.hash(content, createDate);
    }

    public long getCreateDate() {
        return createDate;
    }

    public String getContent() {
        return content;
    }
}

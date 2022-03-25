package com.xiaoma.vr.dispatch.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

/**
 * 语音助理分发指令后,收到的反馈
 *
 * @author youthyJ
 * @date 2019/3/11
 */
public class Result implements Parcelable {
    public static final Creator<Result> CREATOR = new Creator<Result>() {
        @Override
        public Result createFromParcel(Parcel in) {
            return new Result(in);
        }

        @Override
        public Result[] newArray(int size) {
            return new Result[size];
        }
    };
    public static final String NO_HANDLE_APP = "NO_HANDLE_APP";
    private final Command oriCommand;
    private int state = State.Empty; // 必选,该语音指令的处理状态
    private String handleApp;    // 处理该指令的App包名
    private String handleDetail; // 可选,处理这个指令的具体类
    private String followVoice;  // 可选,反馈是否有后续语音播报

    protected Result(Parcel in) {
        oriCommand = in.readParcelable(Command.class.getClassLoader());
        state = in.readInt();
        handleApp = in.readString();
        handleDetail = in.readString();
        followVoice = in.readString();
    }

    public Result(Command command) {
        if (command == null) {
            oriCommand = null;
            state = State.CommandError;
            handleApp = NO_HANDLE_APP;
            return;
        }
        oriCommand = command;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(oriCommand, flags);
        dest.writeInt(state);
        dest.writeString(handleApp);
        dest.writeString(handleDetail);
        dest.writeString(followVoice);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(Result.class.getSimpleName());
        sb.append(" - - - - - - - - - - - - - -");
        if (oriCommand != null) {
            sb.append("\n * [ time: ").append(oriCommand.getCreateDate()).append(" ]");
            sb.append("\n * [ command: ").append(oriCommand.getContent()).append(" ]");
        } else {
            sb.append("\n * [ time: ]");
            sb.append("\n * [ command: ]");
        }
        sb.append("\n * - - - - - - - - - - - - - - - - - -");
        sb.append("\n * [ state: ").append(State.getStateDesc(state)).append(" ]");
        sb.append("\n * [ handler: ").append(handleApp).append(" ]");
        if (!TextUtils.isEmpty(handleDetail) && !handleDetail.trim().isEmpty()) {
            sb.append("\n * [ detail: ").append(handleDetail).append(" ]");
        }
        if (!TextUtils.isEmpty(followVoice) && !followVoice.trim().isEmpty()) {
            sb.append("\n * [ follow: ").append(followVoice).append(" ]");
        }
        sb.append("\n * - - - - - - - - - - - - - - - - - -");
        return sb.toString();
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getHandleApp() {
        return handleApp;
    }

    public void setHandleApp(String handleApp) {
        this.handleApp = handleApp;
    }

    public String getHandleDetail() {
        return handleDetail;
    }

    public void setHandleDetail(String handleDetail) {
        this.handleDetail = handleDetail;
    }

    public String getFollowVoice() {
        return followVoice;
    }

    public void setFollowVoice(String followVoice) {
        this.followVoice = followVoice;
    }

    public boolean isHandled() {
        return State.OK == state // 状态是OK
                && !(TextUtils.isEmpty(handleApp) || handleApp.trim().isEmpty()) // 有App进行处理
                && !NO_HANDLE_APP.equals(handleApp);
    }

    public static final class State {
        public static final int Empty = -999;
        public static final int ClientError = -3;
        public static final int No_Handle = -2;
        public static final int CommandError = -1;
        public static final int OK = 0;

        public static String getStateDesc(int state) {
            switch (state) {
                case Empty:
                    return "Empty";
                case OK:
                    return "OK";
                case CommandError:
                    return "CommandError";
                default:
                    return "Unknown";
            }
        }
    }
}

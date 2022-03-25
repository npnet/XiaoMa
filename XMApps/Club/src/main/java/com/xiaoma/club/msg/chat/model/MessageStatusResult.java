package com.xiaoma.club.msg.chat.model;

import com.hyphenate.chat.EMMessage;

/**
 * Created by LKF on 2019-1-17 0017.
 */
public class MessageStatusResult {
    private EMMessage mMessage;
    private int mErrorCode;
    private String mErrorMsg;

    public MessageStatusResult(EMMessage message) {
        mMessage = message;
    }

    public MessageStatusResult(EMMessage message, int errorCode, String errorMsg) {
        mMessage = message;
        mErrorCode = errorCode;
        mErrorMsg = errorMsg;
    }

    public EMMessage getMessage() {
        return mMessage;
    }

    public int getErrorCode() {
        return mErrorCode;
    }

    public String getErrorMsg() {
        return mErrorMsg;
    }
}

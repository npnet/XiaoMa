package com.xiaoma.club.msg.chat.model;

/**
 * Author: loren
 * Date: 2019/1/22 0022
 */

public class  IsMyFriendResult {

    private boolean isMyFriend;
    private String resultCode;
    private String resultMessage;

    public boolean isMyFriend() {
        return isMyFriend;
    }

    public void setMyFriend(boolean myFriend) {
        isMyFriend = myFriend;
    }

    public String getResultCode() {
        return resultCode;
    }

    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }

    public String getResultMessage() {
        return resultMessage;
    }

    public void setResultMessage(String resultMessage) {
        this.resultMessage = resultMessage;
    }
}

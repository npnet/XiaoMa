package com.xiaoma.club.common.model;

/**
 * Author: loren
 * Date: 2019/1/22 0022
 */

public class ClubBaseResult {

    private String resultCode;
    private String resultMessage;

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

    public boolean isSuccess() {
        return resultCode.equals("1");
    }

    public boolean isRepeat() {
        return resultCode.equals("1008");//1008是加群专用的code，重复加群时被认为是成功操作
    }

    public boolean isKickOut() {
        return resultCode.equals("40040");//40040是加群专用的code，表示该用户被群主踢出群，无法再加入
    }

    public boolean isDissolution() {
        return resultCode.equals("40053");//40053是加群专用的code，表示该部落已经被解散，无法再加入
    }

    public boolean isFrequently() {
        return resultCode.equals("40047");//40047是加好友专用的code，频繁多次发送好友请求时的失败返回
    }

    public boolean isFriend() {
        return resultCode.equals("40063");//40063是加好友专用的code，已经是好友时再发送请求返回
    }
}

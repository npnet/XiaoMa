package com.xiaoma.songname.model;


/*  @project_name：  XMAgateOS
 *  @package_name：  com.xiaoma.songname.model
 *  @file_name:      UserSignBean
 *  @author:         Rookie
 *  @create_time:    2019/5/28 17:40
 *  @description：   TODO             */

public class UserSignBean {

    /**
     * personalSignature : 我不喜欢玩这个游戏
     * isFriend : true
     */

    private String personalSignature;
    private boolean isFriend;
    private String hxAccount;

    public String getPersonalSignature() {
        return personalSignature;
    }

    public void setPersonalSignature(String personalSignature) {
        this.personalSignature = personalSignature;
    }

    public boolean isIsFriend() {
        return isFriend;
    }

    public void setIsFriend(boolean isFriend) {
        this.isFriend = isFriend;
    }

    public String getHxAccount() {
        return hxAccount;
    }

    public void setHxAccount(String hxAccount) {
        this.hxAccount = hxAccount;
    }
}

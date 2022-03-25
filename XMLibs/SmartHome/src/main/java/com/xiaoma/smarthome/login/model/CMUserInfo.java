package com.xiaoma.smarthome.login.model;


/*  @project_name：  XMAgateOS
 *  @package_name：  com.xiaoma.smarthome.login.model
 *  @file_name:
 *  @author:         Rookie
 *  @create_time:    2019/5/5 16:21
 *  @description：   TODO             */

import java.util.List;

public class CMUserInfo {
    /**
     * accountType : 1
     * loginType : 4
     * userId : 1437322
     * userCode : Y8wgJWHaFWORTHUbMAw
     * headImg : https://cdn.cnbj2.fds.api.mi-img.com/viomi-fileupload/images/user_head/1437322/nh1wR2htZs46f2EpwD1.png?GalaxyAccessKeyId=EAKC4WAFZQV4K&Expires=361555575496676&Signature=l6mRxl1yH+E9MZe/tADk/T/NdSw=
     * mobile : 15271575900
     * nickName : Jie
     * roles : ["VMALL_CUSTOMER"]
     * token : QEy1B4f3ZKmTf8yM
     * xiaomi : {"miId":"1042529189","accessToken":"V3_b2IYWWYNa6O18Z64p1oh44EFpsvo7b9Sv9WTWh0gEo82i04PboDbokNLH3rsLIgFv2oXsO_kzVWcxW3y7xC8Idh0JSjnDBcNqdk9txhuLxEgIyf0Y6TkMUHLj8_EeeX8","userId":"1042529189","type":"android","macKey":"0Jf8RnYi2tuNnmF2f0YlLOYp71k","macAlgorithm":"HmacSHA1","mexpiresIn":7776000}
     */

    private int accountType;
    private int loginType;
    private int userId;
    private String userCode;
    private String headImg;
    private String mobile;
    private String nickName;
    private String token;
    private XiaoMiBean xiaomi;
    private List<String> roles;

    public int getAccountType() {
        return accountType;
    }

    public void setAccountType(int accountType) {
        this.accountType = accountType;
    }

    public int getLoginType() {
        return loginType;
    }

    public void setLoginType(int loginType) {
        this.loginType = loginType;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

    public String getHeadImg() {
        return headImg;
    }

    public void setHeadImg(String headImg) {
        this.headImg = headImg;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public XiaoMiBean getXiaomi() {
        return xiaomi;
    }

    public void setXiaomi(XiaoMiBean xiaomi) {
        this.xiaomi = xiaomi;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

}

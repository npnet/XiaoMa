package com.xiaoma.music.kuwo.model;

import com.xiaoma.adapter.base.XMBean;

import cn.kuwo.base.bean.UserInfo;
import cn.kuwo.base.bean.VipInfo;

/**
 * Created by ZYao.
 * Date ：2018/10/16 0016
 */
public class XMKwUserInfo extends XMBean<UserInfo> {
    public XMKwUserInfo(UserInfo userInfo) {
        super(userInfo);
    }

    public int getLoginStatus() {
        return  getSDKBean().getLoginStatus();
    }

    public void setLoginStatus(int var1) {
        getSDKBean().setLoginStatus(var1);
    }

    public int getOnLineStatus() {
        return  getSDKBean().getOnLineStatus();
    }

    public void setOnLineStatus(int var1) {
        getSDKBean().setOnLineStatus(var1);
    }

    public void setLoginType(String var1) {
        getSDKBean().setLoginType(var1);
    }

    public String getLoginType() {
        return  getSDKBean().getLoginType();
    }

    public int getUid() {
        return  getSDKBean().getUid();
    }

    public void setUid(int var1) {
       getSDKBean().setUid(var1);
    }

    public String getSessionId() {
        return  getSDKBean().getSessionId();
    }

    public void setSessionId(String var1) {
       getSDKBean().setSessionId(var1);
    }

    public String getUserName() {
        return  getSDKBean().getUserName();
    }

    public void setUserName(String var1) {
        getSDKBean().setUserName(var1);
    }

    public String getPassword() {
        return  getSDKBean().getPassword();
    }

    public void setPassword(String var1) {
        getSDKBean().setPassword(var1);
    }

    public int getLevel() {
        return  getSDKBean().getLevel();
    }

    public void setLevel(int var1) {
        getSDKBean().setLevel(var1);
    }

    public byte[] getPortrait() {
        return  getSDKBean().getPortrait();
    }

    public void setPortrait(byte[] var1) {
        getSDKBean().setPortrait(var1);
    }

    public boolean isMerged() {
        return  getSDKBean().isMerged();
    }

    public void setMerged(boolean var1) {
        getSDKBean().setMerged(var1);
    }

    public String getNickName() {
        return  getSDKBean().getNickName();
    }

    public void setNickName(String var1) {
        getSDKBean().setNickName(var1);
    }

    public VipInfo getVipInfo() {
        return  getSDKBean().getVipInfo();
    }

    public void setVipInfo(VipInfo var1) {
        getSDKBean().setVipInfo(var1);
    }

    public int getScore() {
        return  getSDKBean().getScore();
    }

    public void setScore(int var1) {
        getSDKBean().setScore(var1);
    }

    public String getHeadPic() {
        return  getSDKBean().getHeadPic();
    }

    public void setHeadPic(String var1) {
        getSDKBean().setHeadPic(var1);
    }

    public String getTm() {
        return  getSDKBean().getTm();
    }

    public void setTm(String var1) {
        getSDKBean().setTm(var1);
    }

    public String getCode() {
        return  getSDKBean().getCode();
    }

    public void setCode(String var1) {
        getSDKBean().setCode(var1);
    }

    public String getAccessToken() {
        return  getSDKBean().getAccessToken();
    }

    public void setAccessToken(String var1) {
        getSDKBean().setAccessToken(var1);
    }

    public String getExpiresIn() {
        return  getSDKBean().getExpiresIn();
    }

    public void setExpiresIn(String var1) {
        getSDKBean().setExpiresIn(var1);
    }

    public int getGender() {
        return  getSDKBean().getGender();
    }

    public String getBirthday() {
        return  getSDKBean().getBirthday();
    }

    public String getResidentCity() {
        return  getSDKBean().getResidentCity();
    }

    public String getSignature() {
        return  getSDKBean().getSignature();
    }

    public void setBirthday(String var1) {
        getSDKBean().setBirthday(var1);
    }

    public void setResidentCity(String var1) {
        getSDKBean().setResidentCity(var1);
    }

    public void setSignature(String var1) {
        getSDKBean().setSignature(var1);
    }

    public String getLoginFrom() {
        return  getSDKBean().getLoginFrom();
    }

    public void setLoginFrom(String var1) {
        getSDKBean().setLoginFrom(var1);
    }

    public String getPhone() {
        return  getSDKBean().getPhone();
    }

    public void setPhone(String var1) {
        getSDKBean().setPhone(var1);
    }

    public int getFlowerCnt() {
        return  getSDKBean().getFlowerCnt();
    }

    public void setFlowerCnt(int var1) {
        getSDKBean().setFlowerCnt(var1);
    }

    public int getKwbCnt() {
        return  getSDKBean().getKwbCnt();
    }

    public void setKwbCnt(int var1) {
        getSDKBean().setKwbCnt(var1);
    }

    public String getPic() {
        return  getSDKBean().getPic();
    }

    public void setPic(String var1) {
        getSDKBean().setPic(var1);
    }

    public String getSource() {
        return  getSDKBean().getSource();
    }

    public void setSource(String var1) {
        getSDKBean().setSource(var1);
    }

    public String getPwdPhone() {
        return  getSDKBean().getPwdPhone();
    }

    public void setPwdPhone(String var1) {
        getSDKBean().setPwdPhone(var1);
    }

    public int getOnlineStatus() {
        return  getSDKBean().getOnlineStatus();
    }

    public void setOnlineStatus(int var1) {
        getSDKBean().setOnlineStatus(var1);
    }

    public void setIsMerged(boolean var1) {
        getSDKBean().setIsMerged(var1);
    }

    public String getPwdEmail() {
        return  getSDKBean().getPwdEmail();
    }

    public void setPwdEmail(String var1) {
        getSDKBean().setPwdEmail(var1);
    }

    public String getSysTag() {
        return  getSDKBean().getSysTag();
    }

    public void setSysTag(String var1) {
        getSDKBean().setSysTag(var1);
    }

    public String getConstellation() {
        return  getSDKBean().getConstellation();
    }

    public void setConstellation(String var1) {
        getSDKBean().setConstellation(var1);
    }

    public int getFollowCnt() {
        return  getSDKBean().getFollowCnt();
    }

    public void setFollowCnt(int var1) {
        getSDKBean().setFollowCnt(var1);
    }

    public String getResource() {
        return  getSDKBean().getResource();
    }

    public void setResource(String var1) {
        getSDKBean().setResource(var1);
    }

    public String getQrCode() {
        return  getSDKBean().getQrCode();
    }

    public void setQrCode(String var1) {
        getSDKBean().setQrCode(var1);
    }

    public String getPasswordAnswer() {
        return  getSDKBean().getPasswordAnswer();
    }

    public void setPasswordAnswer(String var1) {
        getSDKBean().setPasswordAnswer(var1);
    }

    public String getRegtm() {
        return  getSDKBean().getRegtm();
    }

    public void setRegtm(String var1) {
        getSDKBean().setRegtm(var1);
    }

    public String getPasswordQuestion() {
        return  getSDKBean().getPasswordQuestion();
    }

    public void setPasswordQuestion(String var1) {
        getSDKBean().setPasswordQuestion(var1);
    }

    public String getAddress() {
        return  getSDKBean().getAddress();
    }

    public void setAddress(String var1) {
        getSDKBean().setAddress(var1);
    }

    public String getFansCnt() {
        return  getSDKBean().getFansCnt();
    }

    public void setFansCnt(String var1) {
        getSDKBean().setFansCnt(var1);
    }

    public String getQq() {
        return  getSDKBean().getQq();
    }

    public void setQq(String var1) {
        getSDKBean().setQq(var1);
    }

    public boolean isNewUser() {
        return  getSDKBean().isNewUser();
    }

    public void setIsNewUser(boolean var1) {
        getSDKBean().setIsNewUser(var1);
    }

    public String getHeadPendant() {
        return  getSDKBean().getHeadPendant();
    }

    public void setHeadPendant(String var1) {
        getSDKBean().setHeadPendant(var1);
    }

    public long getFamilyId() {
        return  getSDKBean().getFamilyId();
    }

    public void setFamilyId(long var1) {
        getSDKBean().setFamilyId(var1);
    }

    public boolean isFamilyLeader() {
        return  getSDKBean().isFamilyLeader();
    }

    public void setFamilyLeader(boolean var1) {
        getSDKBean().setFamilyLeader(var1);
    }

    public String getFamilyName() {
        return  getSDKBean().getFamilyName();
    }

    public void setFamilyName(String var1) {
        getSDKBean().setFamilyName(var1);
    }

    public String getFamilyUrl() {
        return  getSDKBean().getFamilyUrl();
    }

    public void setFamilyUrl(String var1) {
        getSDKBean().setFamilyUrl(var1);
    }

}

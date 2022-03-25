package com.xiaoma.motorcade.setting.model;

/**
 * 简介: 获取车队成员详细信息model
 *
 * @author lingyan
 */
public class UserDetailInfo {
    private String name;
    private String gender;
    private String age;
    private String picPath;
    private int isFriend;
    private String personalSignature;
    private String hxAccountService;
    private long id;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getPicPath() {
        return picPath;
    }

    public void setPicPath(String picPath) {
        this.picPath = picPath;
    }

    public int getIsFriend() {
        return isFriend;
    }

    public void setIsFriend(int isFriend) {
        this.isFriend = isFriend;
    }

    public String getPersonalSignature() {
        return personalSignature;
    }

    public void setPersonalSignature(String personalSignature) {
        this.personalSignature = personalSignature;
    }

    public String getHxAccountService() {
        return hxAccountService;
    }

    public void setHxAccountService(String hxAccountService) {
        this.hxAccountService = hxAccountService;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}

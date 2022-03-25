package com.xiaoma.app.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by LKF on 2018/9/25 0025
 */
public class AppInfo implements Serializable {
    /**
     * id : 69
     * versionName : 1.0.101
     * versionCode : 101
     * appName : 想聊
     * appNameEn : Chat
     * versionDesc : 测试
     * packageName : com.xiaoma.club
     * size : 20780230
     * url : http://www.carbuyin.net/by2/app/club_1.0.101_TT0000.apk
     * bigImgUrl1 : http://www.carbuyin.net/by2/appImg/8d54e2cb-0e19-4156-8b63-550836d2a59d.png
     * bigImgUrl2 : http://www.carbuyin.net/by2/appImg/c58bd8a4-22f9-4232-8cd2-bb9094fd0217.png
     * updateContent : 123456798
     * iconPathUrl : http://www.carbuyin.net/by2/appImg/690c07bb-52a8-47ee-9c63-dd443be1c09a.png
     * orderList : 10
     */

    private int id;
    private String versionName;
    private int versionCode;
    private String appName;
    private String appNameEn;
    private String versionDesc;
    private String packageName;
    private int size;
    private String url;
    private String bigImgUrl1;
    private String bigImgUrl2;
    private String updateContent;
    private List<String> updateContentList;

    private String iconPathUrl;
    private int orderList;
    private long createDate;
    private int uninstall;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public int getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(int versionCode) {
        this.versionCode = versionCode;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getAppNameEn() {
        return appNameEn;
    }

    public void setAppNameEn(String appNameEn) {
        this.appNameEn = appNameEn;
    }

    public String getVersionDesc() {
        return versionDesc;
    }

    public void setVersionDesc(String versionDesc) {
        this.versionDesc = versionDesc;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getBigImgUrl1() {
        return bigImgUrl1;
    }

    public void setBigImgUrl1(String bigImgUrl1) {
        this.bigImgUrl1 = bigImgUrl1;
    }

    public String getBigImgUrl2() {
        return bigImgUrl2;
    }

    public void setBigImgUrl2(String bigImgUrl2) {
        this.bigImgUrl2 = bigImgUrl2;
    }

    public String getUpdateContent() {
        return updateContent;
    }

    public void setUpdateContent(String updateContent) {
        this.updateContent = updateContent;
    }

    public List<String> getUpdateContentList() {
        return updateContentList;
    }

    public void setUpdateContentList(List<String> updateContentList) {
        this.updateContentList = updateContentList;
    }

    public String getIconPathUrl() {
        return iconPathUrl;
    }

    public void setIconPathUrl(String iconPathUrl) {
        this.iconPathUrl = iconPathUrl;
    }

    public int getOrderList() {
        return orderList;
    }

    public void setOrderList(int orderList) {
        this.orderList = orderList;
    }

    public long getCreateDate() {
        return createDate;
    }

    public void setCreateDate(long createDate) {
        this.createDate = createDate;
    }

    public int getUninstall() {
        return uninstall;
    }

    public void setUninstall(int uninstall) {
        this.uninstall = uninstall;
    }

    @Override
    public String toString() {
        return appName + "--" + packageName;
    }
}

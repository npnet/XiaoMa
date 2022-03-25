package com.xiaoma.update.model;

import java.io.Serializable;

/**
 * Created by Thomas on 2018/10/17 0017
 */

public class ApkVersionInfo implements Serializable {

    /**
     * id : 49
     * versionName : 1.0.99
     * versionCode : 99
     * appName : 车应用
     * appNameEn : Application
     * versionDesc : 提供小马OS APP版本更新与管理，并且提供海量轻应用，用户无需下载安装，一键打开，立刻开始使用。应用涵盖“美食”、“生活”、“维修”、“购物”、“社交”等10余种分类。
     * packageName : com.xiaoma.app
     * size : 6310072
     * url : http://www.carbuyin.net/by2/app/app_1.0.99_TT0000.apk
     * isForceUpdate : false
     * isAddUpdate : false
     * isNeedUpdate : false
     * bigImgUrl1 : http://www.carbuyin.net/by2/appImg/2f274b5f-93ac-4fa3-88ce-974991d47f3c.png
     * bigImgUrl2 : http://www.carbuyin.net/by2/appImg/1b6706d4-d5d7-4364-8f16-5e41ae4675fb.png
     * updateContent : 提供小马OS APP版本更新与管理，并且提供海量轻应用，用户无需下载安装，一键打开，立刻开始使用。应用涵盖“美食”、“生活”、“维修”、“购物”、“社交”等10余种分类。
     * iconPathUrl : http://www.carbuyin.net/by2/appImg/e24f7c1c-1ae8-4b4f-a936-65a2fb1f3435.png
     * status : 0
     * orderList : 7
     * systemCatalogType : 1000
     */
    private int id;
    private String versionName;
    private String versionCode;
    private String appName;
    private String appNameEn;
    private String versionDesc;
    private String packageName;
    private int size;
    private String url;
    private boolean isForceUpdate;
    private boolean isAddUpdate;
    private boolean isNeedUpdate;
    private String bigImgUrl1;
    private String bigImgUrl2;
    private String updateContent;
    private String iconPathUrl;
    private int status;
    private int orderList;
    private String systemCatalogType;

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

    public String getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(String versionCode) {
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

    public boolean isIsForceUpdate() {
        return isForceUpdate;
    }

    public void setIsForceUpdate(boolean isForceUpdate) {
        this.isForceUpdate = isForceUpdate;
    }

    public boolean isIsAddUpdate() {
        return isAddUpdate;
    }

    public void setIsAddUpdate(boolean isAddUpdate) {
        this.isAddUpdate = isAddUpdate;
    }

    public boolean isIsNeedUpdate() {
        return isNeedUpdate;
    }

    public void setIsNeedUpdate(boolean isNeedUpdate) {
        this.isNeedUpdate = isNeedUpdate;
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

    public String getIconPathUrl() {
        return iconPathUrl;
    }

    public void setIconPathUrl(String iconPathUrl) {
        this.iconPathUrl = iconPathUrl;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getOrderList() {
        return orderList;
    }

    public void setOrderList(int orderList) {
        this.orderList = orderList;
    }

    public String getSystemCatalogType() {
        return systemCatalogType;
    }

    public void setSystemCatalogType(String systemCatalogType) {
        this.systemCatalogType = systemCatalogType;
    }

}

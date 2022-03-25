package com.xiaoma.plugin.model;

/**
 * Created by Administrator on 2017/12/6.
 */

public class PluginInfo {
    private long id;
    private String pluginName;
    private long createDate;
    private String md5String;
    private String pluginFilePath;
    private String pluginFileName;
    private String packageName;
    private String versionCode;
    private String versionDesc;
    private String versionName;
    private Long fileSize;
    private String iconPath;
    private String bigImg1;
    private String bigImg2;
    private int status; //状态：-1：下架；0：正常
    private long modifyDate;
    private String updateContent;//更新内容
    private String sizeFomat;
    private long expireDate;//过期时间
    private String className;//启动的类名

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getPluginName() {
        return pluginName;
    }

    public void setPluginName(String pluginName) {
        this.pluginName = pluginName;
    }

    public long getCreateDate() {
        return createDate;
    }

    public void setCreateDate(long createDate) {
        this.createDate = createDate;
    }

    public String getMd5String() {
        return md5String;
    }

    public void setMd5String(String md5String) {
        this.md5String = md5String;
    }

    public String getPluginFilePath() {
        return pluginFilePath;
    }

    public void setPluginFilePath(String pluginFilePath) {
        this.pluginFilePath = pluginFilePath;
    }

    public String getPluginFileName() {
        return pluginFileName;
    }

    public void setPluginFileName(String pluginFileName) {
        this.pluginFileName = pluginFileName;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(String versionCode) {
        this.versionCode = versionCode;
    }

    public String getVersionDesc() {
        return versionDesc;
    }

    public void setVersionDesc(String versionDesc) {
        this.versionDesc = versionDesc;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public String getIconPath() {
        return iconPath;
    }

    public void setIconPath(String iconPath) {
        this.iconPath = iconPath;
    }

    public String getBigImg1() {
        return bigImg1;
    }

    public void setBigImg1(String bigImg1) {
        this.bigImg1 = bigImg1;
    }

    public String getBigImg2() {
        return bigImg2;
    }

    public void setBigImg2(String bigImg2) {
        this.bigImg2 = bigImg2;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public long getModifyDate() {
        return modifyDate;
    }

    public void setModifyDate(long modifyDate) {
        this.modifyDate = modifyDate;
    }

    public String getUpdateContent() {
        return updateContent;
    }

    public void setUpdateContent(String updateContent) {
        this.updateContent = updateContent;
    }

    public String getSizeFomat() {
        return sizeFomat;
    }

    public void setSizeFomat(String sizeFomat) {
        this.sizeFomat = sizeFomat;
    }

    public long getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(long expireDate) {
        this.expireDate = expireDate;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }
}

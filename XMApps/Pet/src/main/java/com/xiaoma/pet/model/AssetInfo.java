package com.xiaoma.pet.model;

/**
 * Created by Gillben
 * date: 2019/2/19 0019
 * desc: 资源下载本地转换实体
 */
public class AssetInfo {

    private int assetType;
    private String appVersion;
    private String levelVersion;
    private String tagNode;
    private String attr;
    private String value;


    public AssetInfo() {
    }

    public AssetInfo(int assetType, String appVersion, String levelVersion, String tagNode, String attr, String value) {
        this.assetType = assetType;
        this.appVersion = appVersion;
        this.tagNode = tagNode;
        this.levelVersion = levelVersion;
        this.attr = attr;
        this.value = value;
    }


    public int getAssetType() {
        return assetType;
    }

    public void setAssetType(int assetType) {
        this.assetType = assetType;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }

    public String getTagNode() {
        return tagNode;
    }

    public void setTagNode(String tagNode) {
        this.tagNode = tagNode;
    }

    public String getLevelVersion() {
        return levelVersion;
    }

    public void setLevelVersion(String levelVersion) {
        this.levelVersion = levelVersion;
    }

    public String getAttr() {
        return attr;
    }

    public void setAttr(String attr) {
        this.attr = attr;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}

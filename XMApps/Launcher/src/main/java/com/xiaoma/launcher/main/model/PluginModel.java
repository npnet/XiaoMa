package com.xiaoma.launcher.main.model;

import java.io.Serializable;

/**
 * @author: iSun
 * @date: 2018/12/12 0012
 */
public class PluginModel implements Serializable {
    /**
     * itemName : 蓝牙电话
     * iconUrl :
     * packageName : com.xiaoma.launcher
     * className :
     * tabName :
     * isInstalled : true
     */

    private String itemName;
    private String itemNameEng;
    private String iconUrl;
    private String packageName;
    private String className;
    private String tabName;
    private String isInstalled;

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getItemNameEng() {
        return itemNameEng;
    }

    public void setItemNameEng(String itemNameEng) {
        this.itemNameEng = itemNameEng;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getTabName() {
        return tabName;
    }

    public void setTabName(String tabName) {
        this.tabName = tabName;
    }

    public String getIsInstalled() {
        return isInstalled;
    }

    public void setIsInstalled(String isInstalled) {
        this.isInstalled = isInstalled;
    }
}

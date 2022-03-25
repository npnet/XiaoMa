package com.xiaoma.launcher.app.model;

import android.os.Bundle;
import android.text.TextUtils;

import com.litesuits.orm.db.annotation.PrimaryKey;
import com.litesuits.orm.db.annotation.Table;
import com.litesuits.orm.db.enums.AssignType;

import java.io.Serializable;

/**
 * User:Created by Terence
 * IDE: Android Studio
 * Date:2017/4/25
 */

@Table("app")
public class AppItem implements Serializable {
    /**
     * appName : 高德导航
     * packageName : com.autonavi.amapauto
     * className :
     * tabName :
     * iconUrl : http://img1.mm131.com/pic/2194/1.jpg
     * type : 0
     */

    @PrimaryKey(AssignType.AUTO_INCREMENT)
    public long id;
    public String appName;
    public String appNameEn;
    public String packageName;
    public String className;
    public String tabName;
    public String iconUrl;
    public boolean isInstalled;
    /**
     * 显示类型
     * 0 ： 默认，有安装就显示，没安装就不显示
     * 1 ： 不安装也要显示
     * 3 ： 代表插件
     */
    public String type;

    public Bundle toBundle() {
        Bundle bundle = new Bundle();
        if (!TextUtils.isEmpty(className))
            bundle.putString("forward", className);
        if (!TextUtils.isEmpty(tabName))
            bundle.putString("tab", tabName);
        return bundle;
    }
}

package com.xiaoma.launcher.app.model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * User:Created by Terence
 * IDE: Android Studio
 * Date:2017/4/25
 */

public class WhiteApp implements Serializable {
    /**
     * itemName : 导航
     * iconUrl : http://img1.mm131.com/pic/2194/1.jpg
     * type : 0
     * list : [{"appName":"高德导航","packageName":"com.autonavi.amapauto","className":"","tabName":"","iconUrl":"http://img1.mm131.com/pic/2194/1.jpg","type":"0"},{"appName":"百度导航","packageName":"com.baidu.BaiduMap.pad","className":"","tabName":"","iconUrl":"http://img1.mm131.com/pic/2194/1.jpg","type":"0"}]
     */
    public String itemName;
    public String iconUrl;
    /**
     * 显示类型
     * 0 ： 默认不显示
     * 1 ： 显示（追加到后面）
     * 2 ： 置顶（追加到首屏，位置待定）
     */
    public String type;
    public ArrayList<AppItem> list;
    public int sortNumber;

    public LauncherItemBean toLauncherItemBean() {
        LauncherItemBean item = new LauncherItemBean();
        item.sortNumber = sortNumber;
        item.itemName = itemName;
        item.iconUrl = iconUrl;
        item.type = type;
        item.list = list;
        if (list != null && list.size() == 1) {
            item.packageName = list.get(0).packageName;
        }
        if (item.list != null && item.list.size() == 1) {
            AppItem appItem = item.list.get(0);
            appItem.isInstalled = true;
        }
        return item;
    }
}

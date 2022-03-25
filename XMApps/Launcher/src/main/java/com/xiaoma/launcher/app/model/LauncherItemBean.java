package com.xiaoma.launcher.app.model;

import android.text.TextUtils;

import com.litesuits.orm.db.annotation.Ignore;
import com.litesuits.orm.db.annotation.Mapping;
import com.litesuits.orm.db.annotation.PrimaryKey;
import com.litesuits.orm.db.annotation.Table;
import com.litesuits.orm.db.enums.AssignType;
import com.litesuits.orm.db.enums.Relation;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: vincenthu
 * Date: 15/8/3
 * Time: 15:22
 */

@Table("launcher_app")
public class LauncherItemBean implements Serializable {

    @PrimaryKey(AssignType.AUTO_INCREMENT)
    public long id;
    /**
     * 包名（选中的应用，为空表示没有默认应用）
     */
    public String packageName;
    public String itemName;
    public String itemNameEn;
    public String iconUrl;
    /**
     * 显示类型
     * 0 ： 默认状态，有安装就显示，没安装不显示
     * 1 ： 不安装也显示（追加到后面）
     * 2 ： 不安装也显示且置顶（追加到首屏，位置待定）
     */
    public String type;
    @Mapping(Relation.OneToMany)
    public ArrayList<AppItem> list;

    //播放的状态
    @Ignore
    public int status;
    //播放的信息
    @Ignore
    public String musicInfo;
    //通知消息
    @Ignore
    public String message;
    @Ignore
    public int sortNumber;


    public boolean hasDefaultApp() {
        return (list != null && list.size() == 1) || !TextUtils.isEmpty(packageName);
    }

    public AppItem getDefaultApp() {
        if (list != null && list.size() == 1) {
            return list.get(0);
        }
        if (!TextUtils.isEmpty(packageName)) {
            for (AppItem item : list) {
                if (packageName.equals(item.packageName)) {
                    return item;
                }
            }
        }
        if (list != null && list.size() > 1) {
            return list.get(0);
        }
        return null;
    }

    public LauncherItemBean toLauncherItemBean() {
        LauncherItemBean launcherItemBeanV2 = new LauncherItemBean();
        launcherItemBeanV2.id = id;
        launcherItemBeanV2.itemName = itemName;
        launcherItemBeanV2.type = type;
        launcherItemBeanV2.iconUrl = iconUrl;
        launcherItemBeanV2.list = list;
        launcherItemBeanV2.packageName = packageName;
        return launcherItemBeanV2;
    }

    public boolean isSystemSetting() {
        if (TextUtils.isEmpty(packageName)) {
            return false;
        }
        return packageName.equals("com.android.settings");
    }

    @Override
    public String toString() {
        return "LauncherItemBean{" +
                "id=" + id +
                ", packageName='" + packageName + '\'' +
                ", itemName='" + itemName + '\'' +
                ", iconUrl='" + iconUrl + '\'' +
                ", type='" + type + '\'' +
                ", list=" + list +
                ", status=" + status +
                ", musicInfo='" + musicInfo + '\'' +
                ", message='" + message + '\'' +
                ", sortNumber=" + sortNumber +
                '}';
    }
}
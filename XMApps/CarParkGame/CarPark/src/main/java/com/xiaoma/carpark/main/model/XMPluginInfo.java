package com.xiaoma.carpark.main.model;

import java.io.Serializable;

/**
 * Created by Thomas on 2018/11/6 0006
 * 插件信息
 */

public class XMPluginInfo implements Serializable {

    //活动或游戏model
    /**
     * id
     */
    public int id;
    /**
     * 主标题
     */
    public String mainTitle;

    /**
     * 描述
     */
    public String subTitle;
    /**
     * 参与人数
     */
    public int joinNumber;
    /**
     * 主题icon
     */
    public String iconPath;
    /**
     * 类型:2 游戏 3 比赛
     */
    public int type;

    /**
     * 标签
     */
    public String iconText;

    /**
     * 映射类型：1外链 2内链
     */
    public int reflectType;

    /**
     * 外链地址
     */
    public String path;

    //活动或游戏plugin
    /**
     * 插件apk url
     */
    public String pluginUrl;
    /**
     * 插件文件MD5
     */
    public String md5String;
    /**
     * 格式化size：5.79 MB
     */
    public String sizeFomat;
    /**
     * 插件文件大小：6069636
     */
    public long fileSize;
    /**
     * 插件本地存在路径
     */
    public String pluginFilePath;
    /**
     * 插件文件本地存储名称
     */
    public String pluginFileName;
    /**
     * 插件过期时间
     */
    public long expireDate;
    /**
     * 创建时间
     */
    public long createDate;
    /**
     * 插件名称
     */
    public String pluginName;
    /**
     * 插件包名
     */
    public String pluginPackageName;
    /**
     * 插件版本号
     */
    public String pluginVersionCode;
    /**
     * 插件版本名
     */
    public String pluginVersionName;
    /**
     * 插件启动类入口
     */
    public String pluginClassName;
}

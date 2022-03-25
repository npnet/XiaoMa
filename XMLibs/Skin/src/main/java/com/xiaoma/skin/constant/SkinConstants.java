package com.xiaoma.skin.constant;

import com.xiaoma.config.ConfigManager;

import java.io.File;

/**
 * Created by Thomas on 2018/12/19 0019
 */

public interface SkinConstants {

    File SKIN_FOLDER = ConfigManager.FileConfig.getSkinFolder();
    String SKIN_FILE_SUFFIX_1 = ".skin";
    String SKIN_FILE_SUFFIX_2 = ".apk";
    String SKIN_ACTION = "com.xiaoma.skin.action";
    String SKIN_ACTION_QINGSHE= "com.xiaoma.skin.action.qingshe";
    String SKIN_ACTION_XM = "com.xiaoma.skin.action.xm";
    String SKIN_ACTION_DAOMENG = "com.xiaoma.skin.action.daomeng";
    String SKIN_SUCCESS = "com.xiaoma.skin.success";

    String SKIN_NAME_RENWEN = "RenWen.skin";
    String SKIN_NAME_QINGSHE = "QingShe.skin";
    String SKIN_NAME_DAOMENG = "DaoMeng.skin";

    //主题ID
    int THEME_ZHIHUI = 0;// 智慧
    int THEME_QINGSHE = 1;// 轻奢
    int THEME_DAOMENG = 2;// 盗梦
    int THEME_DEFAULT = THEME_ZHIHUI;// 默认智慧
    // 主题ID取值范围
    int THEME_MIN_ID = THEME_ZHIHUI;
    int THEME_MAX_ID = THEME_DAOMENG;
}

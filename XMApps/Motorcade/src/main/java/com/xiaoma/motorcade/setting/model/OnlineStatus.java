package com.xiaoma.motorcade.setting.model;


import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static com.xiaoma.motorcade.setting.model.OnlineStatus.OFFLINE;
import static com.xiaoma.motorcade.setting.model.OnlineStatus.ONLINE;

/**
 * @author zs
 * @date 2019/1/21 0021.
 */
@Retention(RetentionPolicy.SOURCE)
@StringDef({ONLINE, OFFLINE})
public @interface OnlineStatus {
    String ONLINE = "在线";
    String OFFLINE = "离线";
}

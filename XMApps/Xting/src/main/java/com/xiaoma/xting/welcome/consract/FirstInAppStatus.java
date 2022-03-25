package com.xiaoma.xting.welcome.consract;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * <des>
 *
 * @author YangGang
 * @date 2018/11/19
 */

@Retention(RetentionPolicy.SOURCE)
@IntDef({FirstInAppStatus.PREFERENCE_PAGE_FIRST,
        FirstInAppStatus.AUTO_UPDATE_PREFERENCE,
        FirstInAppStatus.FM_PAGE_FIRST})
public @interface FirstInAppStatus {

    int PREFERENCE_PAGE_FIRST = 0; // 初次进入 需要进行偏好选择
    int AUTO_UPDATE_PREFERENCE = 1; // 自动上穿上次 传递失败的标签
    int FM_PAGE_FIRST = 2; // 跳过splash界面标志
}

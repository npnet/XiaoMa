package com.xiaoma.ui.constract;


/*  @project_name：  XMAgateOS
 *  @package_name：  com.xiaoma.ui.constract
 *  @file_name:      ScrollBarDirection
 *  @author:         Rookie
 *  @create_time:    2019/4/3 10:16
 *  @description：   TODO             */

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@IntDef({ScrollBarDirection.DIRECTION_HORIZONTAL, ScrollBarDirection.DIRECTION_VERTICAL})
@Retention(RetentionPolicy.SOURCE)
public @interface ScrollBarDirection {
    int DIRECTION_HORIZONTAL=0;
    int DIRECTION_VERTICAL=1;
}

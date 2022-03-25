package com.xiaoma.vr.model;

import android.support.annotation.StringDef;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by qiuboxiang on 2019/7/25 21:54
 * Desc:
 */
@Retention(RetentionPolicy.SOURCE)
@StringDef(value = {NaviState.noNavi, NaviState.routing, NaviState.navigation})
public @interface NaviState {
    String noNavi = "noNavi";
    String routing = "routing";
    String navigation = "navigation";
}


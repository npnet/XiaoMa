package com.xiaoma.vr.model;

import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by qiuboxiang on 2019/7/25 11:50
 * Desc:
 */
@Retention(RetentionPolicy.SOURCE)
@StringDef(value = {AppType.MUSIC, AppType.RADIO, AppType.NAVI, AppType.WEIXIN, AppType.INTERNET_RADIO})
public @interface AppType {
    String MUSIC = "MUSIC";
    String RADIO = "RADIO";
    String INTERNET_RADIO = "INTERNET_RADIO";
    String NAVI = "NAVI";
    String WEIXIN = "WEIXIN";
}


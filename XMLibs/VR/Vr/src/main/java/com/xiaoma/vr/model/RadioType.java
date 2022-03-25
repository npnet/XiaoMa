package com.xiaoma.vr.model;

import android.support.annotation.StringDef;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by qiuboxiang on 2019/8/10 10:26
 * Desc:
 */
@Retention(RetentionPolicy.SOURCE)
@StringDef(value = {RadioType.Local, RadioType.Internet, RadioType.None})
public @interface RadioType {
    String Local = "Local";
    String Internet = "Internet";
    String None = "None";
}


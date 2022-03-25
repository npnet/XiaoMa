package com.xiaoma.ui.constract;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * <des>
 *
 * @author YangGang
 * @date 2018/12/18
 */
@IntDef({ToastLevel.DEFAULT, ToastLevel.LOADING,
        ToastLevel.SUCCESS, ToastLevel.EXCEPTION})
@Retention(RetentionPolicy.SOURCE)
public @interface ToastLevel {

    int DEFAULT = 0;
    int LOADING = 1;
    int SUCCESS = 2;
    int EXCEPTION = 3;
}

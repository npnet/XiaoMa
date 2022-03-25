package com.xiaoma.model;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * <pre>
 *     author : wutao
 *     time   : 2018/10/15
 *     desc   :
 * </pre>
 */
@IntDef({Status.LOADING, Status.SUCCESS, Status.FAILURE, Status.ERROR})
@Retention(RetentionPolicy.SOURCE)
public @interface Status {
    int LOADING = 0;
    int SUCCESS = 1;
    int FAILURE = 2;
    int ERROR = 3;
}

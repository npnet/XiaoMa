package com.xiaoma.shop.common.constant;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * <pre>
 *   @author Create by on Gillben
 *   date:   2019/4/24 0024 11:32
 *   desc:   一键缓存、账户绑定
 * </pre>
 */
@Retention(RetentionPolicy.SOURCE)
@IntDef({CacheBindStatus.NONE, CacheBindStatus.CLEAN_CACHE, CacheBindStatus.ACCOUNT_BIND})
public @interface CacheBindStatus {

    int NONE = -1;
    int CLEAN_CACHE = 0;
    int ACCOUNT_BIND = 1;
}

package com.xiaoma.pet.common.annotation;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * <pre>
 *   @author Create by on Gillben
 *   date:   2019/5/23 0023 11:33
 *   desc:
 * </pre>
 */
@Retention(RetentionPolicy.SOURCE)
@IntDef({PetToastType.NORMAL, PetToastType.LOADING, PetToastType.EXCEPTION})
public @interface PetToastType {

    int NORMAL = 0;
    int LOADING = 1;
    int EXCEPTION = 2;

}

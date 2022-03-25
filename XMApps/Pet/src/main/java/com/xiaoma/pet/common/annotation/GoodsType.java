package com.xiaoma.pet.common.annotation;

import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * <pre>
 *   @author Create by on Gillben
 *   date:   2019/5/21 0021 10:56
 *   desc:   商品类型
 * </pre>
 */
@Retention(RetentionPolicy.SOURCE)
@StringDef({GoodsType.FOOD, GoodsType.DECORATOR, GoodsType.COS_PLAY})
public @interface GoodsType {

    String FOOD = "1";
    String DECORATOR = "2";
    String COS_PLAY = "3";
}

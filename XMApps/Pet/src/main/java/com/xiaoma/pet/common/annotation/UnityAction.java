package com.xiaoma.pet.common.annotation;

import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * <pre>
 *   @author Create by on Gillben
 *   date:   2019/5/25 0025 16:35
 *   desc:   unity对应业务action
 * </pre>
 */
@Retention(RetentionPolicy.SOURCE)
@StringDef({UnityAction.HIT_GIFT,
        UnityAction.ANIMATION_CHANGE,
        UnityAction.COMPLETE,
        UnityAction.REFRESH,
        UnityAction.SPEED,
        UnityAction.GEN_GIFT,
        UnityAction.CURRENT_ANIMATION})
public @interface UnityAction {

    /**
     * U -> A
     */
    String HIT_GIFT = "hitGift";
    String ANIMATION_CHANGE = "animChange";
    String COMPLETE = "Complete";

    /**
     * A -> U
     */
    String REFRESH = "refresh";
    String INTERNAL_REFRESH = "internalRefresh";
    String SPEED = "speed";
    String GEN_GIFT = "genGift";
    String CURRENT_ANIMATION = "curAnim";
}

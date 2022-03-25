package com.xiaoma.pet.common.annotation;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * <pre>
 *   @author Create by on Gillben
 *   date:   2019/5/29 0029 14:45
 *   desc:   宠物动画状态
 * </pre>
 */
@Retention(RetentionPolicy.SOURCE)
@IntDef({PlayerStatus.BREATHE, PlayerStatus.TURN_AROUND, PlayerStatus.RUN, PlayerStatus.OK})
public @interface PlayerStatus {

    int BREATHE = 0;
    int TURN_AROUND = 1;
    int RUN = 2;
    int OK = 3;
}

package com.xiaoma.drivingscore.historyRecord.constract;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * <des>
 *
 * @author YangGang
 * @date 2019/1/7
 */

@Retention(RetentionPolicy.SOURCE)
@IntDef({OperateType.SPEED_UP_RAPIDLY,
        OperateType.SLOW_DOWN_RAPIDLY, OperateType.TURN_RAPIDLY})
public @interface OperateType {
    /**
     * {@see R.arrays.OperateType}
     */
    int SPEED_UP_RAPIDLY = 0;//急加速
    int SLOW_DOWN_RAPIDLY = 1;//急减速
    int TURN_RAPIDLY = 2; //急转弯
}

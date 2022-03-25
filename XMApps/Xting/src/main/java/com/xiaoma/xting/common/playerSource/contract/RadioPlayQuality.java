package com.xiaoma.xting.common.playerSource.contract;

import android.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * <des>
 *
 * @author YangGang
 * @date 2019/7/2
 */
@IntDef({RadioPlayQuality.LOW_QUALITY, RadioPlayQuality.STANDARD_QUALITY,
        RadioPlayQuality.HIGH_QUALITY, RadioPlayQuality.HIGHER_QUALITY})
@Retention(RetentionPolicy.SOURCE)
public @interface RadioPlayQuality {

    int LOW_QUALITY = 1; //低品质
    int STANDARD_QUALITY = 2; //标准品质
    int HIGH_QUALITY = 3;//高品质
    int HIGHER_QUALITY = 4;//超高品质
}

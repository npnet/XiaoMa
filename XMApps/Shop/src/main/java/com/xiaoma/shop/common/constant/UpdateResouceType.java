package com.xiaoma.shop.common.constant;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @Author: LiangJingBo.
 * @Date: 2019/06/21
 * @Describe:
 */
@IntDef({UpdateResouceType.THREE_D,
        UpdateResouceType.ICH,
        UpdateResouceType.ICL,
        UpdateResouceType.HU})
@Retention(RetentionPolicy.SOURCE)
public @interface UpdateResouceType {
    int THREE_D = 0;    //3D个性化OTA
    int ICH=1;          //ICH音效个性化OTA
    int ICL=2;          //ICL音效个性化OTA
    int HU=3;           //HU音效个性化OTA
}

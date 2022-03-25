package com.xiaoma.pet.common.annotation;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by Gillben
 * date: 2019/2/14 0014
 * desc：资源保存相关目录
 */
@Retention(RetentionPolicy.SOURCE)
@IntDef({ResourceType.LEVEL,
        ResourceType.PET,
        ResourceType.SCENE,
        ResourceType.DECORATOR,
        ResourceType.GIFT})
public @interface ResourceType {
    int LEVEL = 0;          //关卡、活动
    int PET = 1;            //宠物
    int SCENE = 2;          //场景
    int DECORATOR = 3;      //饰品
    int GIFT = 4;           //礼物
}

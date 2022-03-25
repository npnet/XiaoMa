package com.xiaoma.shop.common.constant;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * <pre>
 *   @author Create by on Gillben
 *   date:   2019/4/18 0018 14:58
 *   desc:   资源类型
 * </pre>
 */
@Retention(RetentionPolicy.SOURCE)
@IntDef({ResourceType.INVALID,
        ResourceType.SKIN,
        ResourceType.ASSISTANT,
        ResourceType.HOLOGRAM,
        ResourceType.VEHICLE_SOUND,
        ResourceType.FLOW,
        ResourceType.INSTRUMENT_SOUND,
        ResourceType.TRY
})
public @interface ResourceType {
    int INVALID = -1;       // 无效

    int SKIN = 0;           //皮肤
    int ASSISTANT = 1;      //语音音色
    int HOLOGRAM = 2;       //全息影像
    int VEHICLE_SOUND = 3;  //音响音效(整车音效)
    int FLOW=4;             //流量
    int INSTRUMENT_SOUND=5; //仪表音效(整车音效)

    int TRY = 6; //试听资源
}

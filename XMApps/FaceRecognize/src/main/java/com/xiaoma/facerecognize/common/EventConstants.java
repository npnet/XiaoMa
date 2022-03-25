package com.xiaoma.facerecognize.common;

/**
 * Created by kaka
 * on 19-4-12 上午10:56
 * <p>
 * desc: #a
 * </p>
 */
public interface EventConstants {
    interface PageDescClick {

        String FatigueDriving = "行为及疲劳提醒页面";
        String HeavyFatigueDriving = "重度疲劳驾驶提醒页面";
    }

    interface NormalClick {
        String close = "取消重度疲劳提醒";
        String cancel = "取消行为及疲劳提醒";
        String ok = "疲劳提醒播放音乐";
        String restArea = "重度疲劳-休息区";
        String coffeeShop = "重度疲劳-咖啡店";
        String serviceArea = "重度疲劳-服务区";
        String parkingLot = "重度疲劳-停车场";
    }
}

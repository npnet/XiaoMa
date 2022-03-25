package com.xiaoma.shop.common.constant;


import android.support.annotation.IntDef;
import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @Author: LiangJingBo.
 * @Date: 2019/05/27
 * @Describe: 整车音效类型
 */

public interface VehicleSoundType {
    @StringDef({
            Type.REVERSING_RADAR_DISTANCE,
            Type.LANE_CHANGE_AID,
            Type.BLUETOOTH_PHONE_BUTTON_TONE,
            Type.FRONT_COLLISION_WARNING,
            Type.TURNING_TIPS,
            Type.SEAT_BELT_IS_NOT_REMINDED,
            Type.ALARM_PROMPT,
            Type.MESSAGE_NOTIFICATION,
    })
    @Retention(RetentionPolicy.SOURCE)
    @interface Type {
        String REVERSING_RADAR_DISTANCE = "reversing_radar_distance";        // 倒车雷达距离
        String LANE_CHANGE_AID = "lane_change_aid";                          // 换道辅助提示
        String BLUETOOTH_PHONE_BUTTON_TONE = "bluetooth_phone_button_tone";  // 蓝牙电话按键音
        String FRONT_COLLISION_WARNING = "front_collision_warning";    // 前碰撞预警
        String TURNING_TIPS = "turning_tips";                          // 转向提示
        String SEAT_BELT_IS_NOT_REMINDED = "seat_belt_is_not_reminded";// 安全带未系提醒
        String ALARM_PROMPT = "alarm_prompt";                          // 报警提示
        String MESSAGE_NOTIFICATION = "message_notification";          // 信息提示
    }
    @StringDef({ProductType.AUDIO_SOUND,ProductType.INSTRUMENT_SOUND})
    @Retention(RetentionPolicy.SOURCE)
    @interface  ProductType{
        String AUDIO_SOUND="audio_sound";// 音响音效
        String INSTRUMENT_SOUND="instrument_sound";// 仪表音效
        String BOUGHT_AUDIO_SOUND="bought_audio_sound";// 已购买音响音效
        String BOUGHT_INSTRUMENT_SOUND="bought_instrument_sound";// 已购买仪表音效
    }
    @IntDef({DownloadStatus.NONE,DownloadStatus.UPDATE,DownloadStatus.COMPLETE})
    @Retention(RetentionPolicy.SOURCE)
    @interface  DownloadStatus{
        int NONE = 0;       //下载
        int UPDATE=1;       //更新
        int COMPLETE =2;    //使用
    }
}

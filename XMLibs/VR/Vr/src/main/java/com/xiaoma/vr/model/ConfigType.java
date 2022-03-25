package com.xiaoma.vr.model;

/**
 * @author: iSun
 * @date: 2019/3/20 0020
 */
public enum ConfigType {
    KEYWORD(1),//唤醒词修改
    WAKEUP_SWITCH(2),//唤醒开关修改
    SEOPT(3),//定向识别修改
    REPLY(3),//回复语修改
    VOICE_SWITCH(4);//语音功能

    private int configType;

    ConfigType(int type) {
        this.configType = type;
    }
}

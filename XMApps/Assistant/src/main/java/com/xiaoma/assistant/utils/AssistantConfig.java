package com.xiaoma.assistant.utils;

/**
 * User:Created by Terence
 * IDE: Android Studio
 * Date:2018/10/26
 * Desc：语音助手配置设置
 */
public class AssistantConfig {

    /**
     * 是否展示语音助手界面
     * true     展示
     * false    黑屏操作
     * 默认是true
     */
    private boolean needShowWindow = false;


    public boolean isNeedShowWindow() {
        return needShowWindow;
    }

    public void setNeedShowWindow(boolean needShowWindow) {
        this.needShowWindow = needShowWindow;
    }
}

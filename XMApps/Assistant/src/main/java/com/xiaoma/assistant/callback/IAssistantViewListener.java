package com.xiaoma.assistant.callback;

import android.content.DialogInterface;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;

/**
 * User:Created by Terence
 * IDE: Android Studio
 * Date:2018/9/26
 * Desc：语音界面
 */
public interface IAssistantViewListener {

    //dialog的dismiss回调
    void onDialogDismiss(DialogInterface dialogInterface);

    //dialog的keyEvent的事件回调
    boolean onDialogKeyEvent(DialogInterface dialogInterface, int keyCode, KeyEvent keyEvent);

    //view界面需要通知执行tts播报内容
    void onSpeak(String content);

    //通知需要执行关闭相关操作
    void onViewClose();

    //view触摸事件
    void onViewTouch(MotionEvent event);

    //view点击事件
    void onViewClick(int viewId);
}

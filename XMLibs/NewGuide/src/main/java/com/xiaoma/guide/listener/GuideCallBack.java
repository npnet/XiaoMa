package com.xiaoma.guide.listener;

public interface GuideCallBack {
    void onHighLightClicked();

    // 有弹框时 添加引导层 返回按键无效，需用回调
//    void onWindowBackCallBack();
}

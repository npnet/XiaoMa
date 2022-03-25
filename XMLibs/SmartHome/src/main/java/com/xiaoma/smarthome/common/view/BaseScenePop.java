package com.xiaoma.smarthome.common.view;


/*  @project_name：  XMAgateOS
 *  @package_name：  com.xiaoma.smarthome.common.view
 *  @file_name:      BaseScenePop
 *  @author:         Rookie
 *  @create_time:    2019/4/25 10:31
 *  @description：   TODO             */

import android.view.View;
import android.widget.PopupWindow;

import com.xiaoma.smarthome.R;

public abstract class BaseScenePop extends PopupWindow {

    public BaseScenePop(View contentView, int width, int height) {
        super(contentView, width, height);
        init();

    }

    public BaseScenePop(View contentView, int width, int height, boolean focusable) {
        super(contentView, width, height, focusable);
        init();
    }

    protected  void init(){
        setOutsideTouchable(true);
        setAnimationStyle(R.style.PopAnimTranslate);
    }
}

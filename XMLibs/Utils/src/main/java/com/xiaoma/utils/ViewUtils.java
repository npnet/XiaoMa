package com.xiaoma.utils;


/*  @project_name：  XMAgateOS
 *  @package_name：  com.xiaoma.utils
 *  @file_name:      ViewUtils
 *  @author:         Rookie
 *  @create_time:    2018/12/21 11:26
 *  @description：   view 工具类             */

import android.graphics.Rect;
import android.view.TouchDelegate;
import android.view.View;

public class ViewUtils {
    //增加点击区域 默认值
    private static final int EXPAND_TOUCH_DELEGATE_VALUE = 30;

    // 两次点击按钮之间的点击间隔不能少于1000毫秒
    private static final int MIN_CLICK_DELAY_TIME = 1000;
    private static long lastClickTime;

    public static void expandViewTouchDelegate(View view) {
        expandViewTouchDelegate(view, EXPAND_TOUCH_DELEGATE_VALUE, EXPAND_TOUCH_DELEGATE_VALUE, EXPAND_TOUCH_DELEGATE_VALUE, EXPAND_TOUCH_DELEGATE_VALUE);
    }

    public static void expandViewTouchDelegate(View view, int range) {

        if (range <= 0) {
            return;
        }

        expandViewTouchDelegate(view, range, range, range, range);
    }

    /**
     * 扩大View的触摸和点击响应范围,最大不超过其父View范围
     * 1.若View的自定义触摸范围超出Parent的大小，则超出的那部分无效。
     * 2.一个Parent只能设置一个View的TouchDelegate，设置多个时只有最后设置的生效。
     *
     * @param view
     * @param top
     * @param bottom
     * @param left
     * @param right
     */
    public static void expandViewTouchDelegate(final View view, final int top,
                                               final int bottom, final int left, final int right) {

        ((View) view.getParent()).post(new Runnable() {
            @Override
            public void run() {
                Rect bounds = new Rect();
                view.setEnabled(true);
                view.getHitRect(bounds);

                bounds.top -= top;
                bounds.bottom += bottom;
                bounds.left -= left;
                bounds.right += right;

                TouchDelegate touchDelegate = new TouchDelegate(bounds, view);

                if (View.class.isInstance(view.getParent())) {
                    ((View) view.getParent()).setTouchDelegate(touchDelegate);
                }
            }
        });
    }

    /**
     * 防止快速重复点击
     *
     * @return
     */
    public static boolean isFastClick() {
        boolean flag = false;
        long curClickTime = System.currentTimeMillis();
        if ((curClickTime - lastClickTime) <= MIN_CLICK_DELAY_TIME) {
            flag = true;
        }
        lastClickTime = curClickTime;
        return flag;
    }
}

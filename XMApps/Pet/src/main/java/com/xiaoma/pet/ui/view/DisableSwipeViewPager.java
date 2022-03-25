package com.xiaoma.pet.ui.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * <pre>
 *       @author Created by Gillben
 *       date: 2019/3/12 0012 15:30
 *       desc：禁止滑动
 * </pre>
 */
public class DisableSwipeViewPager extends ViewPager {


    private boolean swipeFlag = false;

    public DisableSwipeViewPager(@NonNull Context context) {
        super(context);
    }

    public DisableSwipeViewPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void setCurrentItem(int item, boolean smoothScroll) {
        super.setCurrentItem(item, smoothScroll);
    }

    @Override
    public void setCurrentItem(int item) {
        //去除页面切换时的滑动翻页效果
        super.setCurrentItem(item, false);
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return swipeFlag;
    }

    public void setSwipeFlag(boolean swipeFlag) {
        this.swipeFlag = swipeFlag;
    }
}

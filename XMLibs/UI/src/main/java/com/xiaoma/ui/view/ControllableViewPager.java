package com.xiaoma.ui.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * 控制 ViewPager 是否滑动
 */
public class ControllableViewPager extends ViewPager {

    private boolean isScrollable = true;

    public ControllableViewPager(Context context) {
        super(context);
    }

    public ControllableViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * 是否可滑动
     *
     * @param isScrollable true:可滑动，false:禁止滑动
     */
    public void setScrollable(boolean isScrollable) {
        this.isScrollable = isScrollable;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return isScrollable && super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return isScrollable && super.onTouchEvent(ev);
    }
}

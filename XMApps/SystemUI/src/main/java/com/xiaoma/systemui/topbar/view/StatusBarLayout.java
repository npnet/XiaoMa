package com.xiaoma.systemui.topbar.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

/**
 * Created by LKF on 2018/11/19 0019.
 */
public class StatusBarLayout extends LinearLayout {
    public StatusBarLayout(Context context) {
        super(context);
    }

    public StatusBarLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public StatusBarLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean addViewInLayout(View child, int index, ViewGroup.LayoutParams params) {
        return addViewInLayout(child, index, params, true);
    }

    @Override
    public boolean addViewInLayout(View child, int index, ViewGroup.LayoutParams params, boolean preventRequestLayout) {
        return super.addViewInLayout(child, index, params, preventRequestLayout);
    }
}

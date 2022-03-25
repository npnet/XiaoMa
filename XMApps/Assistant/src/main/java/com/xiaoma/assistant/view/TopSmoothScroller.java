package com.xiaoma.assistant.view;

import android.content.Context;
import android.support.v7.widget.LinearSmoothScroller;

/**
 * @Author ZiXu Huang
 * @Data 2019/3/26
 */
public class TopSmoothScroller extends LinearSmoothScroller {
    public TopSmoothScroller(Context context) {
        super(context);
    }

    @Override
    protected int getHorizontalSnapPreference() {
        return SNAP_TO_START;
    }

    @Override
    protected int getVerticalSnapPreference() {
        return SNAP_TO_START;
    }
}

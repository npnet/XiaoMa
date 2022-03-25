package com.xiaoma.systemui.common.util;

import android.widget.OverScroller;
import android.widget.ScrollView;

import java.lang.reflect.Field;

/**
 * Created by LKF on 2019-3-14 0014.
 */
public class ViewUtil {
    private static final String TAG = "ViewUtil";

    public static boolean isScrollViewIdle(ScrollView sv) {
        final Class clz = ScrollView.class;

        boolean isBeingDragged = false;
        try {
            final Field mIsBeingDragged = clz.getDeclaredField("mIsBeingDragged");
            mIsBeingDragged.setAccessible(true);
            isBeingDragged = (boolean) mIsBeingDragged.get(sv);
        } catch (Exception e) {
            e.printStackTrace();
        }

        boolean isScrollerFinished = false;
        boolean isScrollerOverScrolled = false;
        try {
            Field mScroller = clz.getDeclaredField("mScroller");
            mScroller.setAccessible(true);
            final OverScroller scroller = (OverScroller) mScroller.get(sv);
            isScrollerFinished = scroller.isFinished();
            isScrollerOverScrolled = scroller.isOverScrolled();
        } catch (Exception e) {
            e.printStackTrace();
        }

        LogUtil.logI(TAG, "isScrollViewIdle() [ isBeingDragged: %s, isScrollerFinished: %s, isScrollerOverScrolled: %s ]",
                isBeingDragged, isScrollerFinished, isScrollerOverScrolled);

        return !isBeingDragged && isScrollerFinished;
    }
}

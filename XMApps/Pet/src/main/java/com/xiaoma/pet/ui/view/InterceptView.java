package com.xiaoma.pet.ui.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;

/**
 * <pre>
 *   @author Create by on Gillben
 *   date:   2019/5/29 0029 20:47
 *   desc:
 * </pre>
 */
public class InterceptView extends LinearLayout {

    public InterceptView(Context context) {
        this(context, null);
    }

    public InterceptView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public InterceptView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            return ev.getRawX() > 1814;
        }
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return true;
    }
}

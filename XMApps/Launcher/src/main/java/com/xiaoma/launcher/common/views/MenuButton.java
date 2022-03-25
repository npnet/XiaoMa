package com.xiaoma.launcher.common.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.RadioGroup;
import com.xiaoma.launcher.common.listener.SlideListener;

/**
 * @author: iSun
 * @date: 2018/12/11 0011
 */
public class MenuButton extends RadioGroup {

    private boolean mScrolling;
    private float touchDownX;
    private float touchDownY;
    private SlideListener moveListener;
    private static final float DISTANCE = 100;

    public MenuButton(Context context) {
        super(context);
    }

    public MenuButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        mScrolling = false;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                touchDownX = event.getX();
                touchDownY = event.getY();
                mScrolling = false;
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                float endX = event.getX();
                float endY = event.getY();
                float distanceX = Math.abs(touchDownX - endX);
                float distanceY = Math.abs(touchDownY - endY);
                if (distanceX >= distanceY && distanceX >= DISTANCE) {
                    mScrolling = true;
                    if ((touchDownX - endX) < 0) {
                        if (moveListener != null) {
                            moveListener.onSlideToRight();
                        }
                    }
                } else {
                    mScrolling = false;
                }
                break;
        }
        return mScrolling;
    }

    public void setSlideListener(SlideListener listener) {
        this.moveListener = listener;
    }

}

package com.xiaoma.launcher.common.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.RelativeLayout;
import com.xiaoma.launcher.common.listener.SlideListener;

/**
 * Created by Thomas on 2019/6/27 0027
 */

public class MenuRelativeLayout extends RelativeLayout {

    private float touchDownX;
    private float touchDownY;
    private SlideListener moveListener;
    private static final float DISTANCE = 50;

    public MenuRelativeLayout(Context context) {
        super(context);
    }

    public MenuRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                touchDownX = event.getX();
                touchDownY = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                float endX = event.getX();
                float endY = event.getY();
                float distanceX = Math.abs(touchDownX - endX);
                float distanceY = Math.abs(touchDownY - endY);
                if (distanceX >= distanceY && distanceX >= DISTANCE) {
                    if ((touchDownX - endX) < 0) {
                        if (moveListener != null) {
                            moveListener.onSlideToRight();
                        }
                    } else {
                        if (moveListener != null) {
                            moveListener.onSlideToLeft();
                        }
                    }
                }
                break;
        }
        return true;
    }

    public void setSlideListener(SlideListener listener) {
        this.moveListener = listener;
    }

}

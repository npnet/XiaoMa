package com.xiaoma.app.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import me.kaelaela.verticalviewpager.VerticalViewPager;

public class UpAndDownViewPager extends VerticalViewPager {

    private int mLastX;
    private int mLastY;
    private int mDeltaX;
    private int mDeltaY;

    public UpAndDownViewPager(Context context) {
        super(context);
    }

    public UpAndDownViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {

        int x = (int) ev.getX();
        int y = (int) ev.getY();

        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                //记录点击位置在·
                mLastX = x;
                mLastY = y;
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                //水平移动的增量
                mDeltaX = x - mLastX;
                //竖直移动的增量
                mDeltaY = y - mLastY;
                if (Math.abs(mDeltaX) > 1 && Math.abs(mDeltaX) < Math.abs(mDeltaY) && Math.abs(mDeltaY) > 1) {
                    if (getCurrentItem() == 0 && y < mLastY) {
                        return true;
                    }
                }
                if(Math.abs(mDeltaX) > Math.abs(mDeltaY)){
                    return false;
                }
                break;
            }
            case MotionEvent.ACTION_UP: {
                break;
            }
            default:
                break;
        }
        try {
            return super.onInterceptTouchEvent(ev);
        } catch (IllegalArgumentException ex) {
            setCurrentItem(1);
            ex.printStackTrace();
        }
        return false;
    }
}

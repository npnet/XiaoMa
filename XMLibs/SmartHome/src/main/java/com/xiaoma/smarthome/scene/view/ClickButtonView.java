package com.xiaoma.smarthome.scene.view;

import android.content.Context;
import android.support.v7.widget.AppCompatButton;
import android.util.AttributeSet;
import android.view.MotionEvent;


/**
 * Created by dengcong
 */

public class ClickButtonView extends AppCompatButton {

    private static final float SCALE_SIZE = 1.0f;
    private int mLeft;
    private int mTop;
    private int mRight;
    private int mBottom;
    private boolean mIsAnimStarting;

    public ClickButtonView(Context context) {
        this(context, null);
    }

    public ClickButtonView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    public ClickButtonView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        int[] location = new int[2];
        getLocationOnScreen(location);
        mLeft = location[0];
        mTop = location[1];
        mRight = mLeft + getMeasuredWidth();
        mBottom = mTop + getMeasuredHeight();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isEnabled()) {
            return super.onTouchEvent(event);
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mIsAnimStarting = true;
                this.setScaleX(SCALE_SIZE);
                this.setScaleY(SCALE_SIZE);
                break;
            case MotionEvent.ACTION_MOVE:
                if (mIsAnimStarting) {
                    if (!innerView(event.getRawX(), event.getRawY())) {
                        mIsAnimStarting = false;
                        this.setScaleX(1.0f);
                        this.setScaleY(1.0f);
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (mIsAnimStarting) {
                    mIsAnimStarting = false;
                    this.setScaleX(1.0f);
                    this.setScaleY(1.0f);
                }
                break;
        }
        return super.onTouchEvent(event);
    }

    //按下的点是否在View内
    protected boolean innerView(float x, float y) {
        if (y >= mTop && y <= mBottom && x >= mLeft && x <= mRight) {
            return true;
        }
        return false;
    }
}

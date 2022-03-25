package com.xiaoma.pet.ui.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * <pre>
 *   @author Create by on Gillben
 *   date:   2019/5/29 0029 10:14
 *   desc:
 * </pre>
 */
public class AccelerateView extends android.support.v7.widget.AppCompatImageView {

    private static final int MAX_SPEED = 180;
    private static final String TAG = "AccelerateView";
    private int minLimitY = 0;
    private int maxLimitY = 500;
    private int startY;
    private int lastTop;

    private OnAccelerateLocationCallback onAccelerateLocationCallback;

    public AccelerateView(Context context) {
        this(context, null);
    }

    public AccelerateView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AccelerateView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

    }


    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startY = (int) event.getY();
                break;

            case MotionEvent.ACTION_MOVE:
                int distanceY = (int) event.getY() - startY;
                int left = getLeft();
                int top = getTop() + distanceY;
                int right = getRight();
                int bottom = getBottom() + distanceY;

                if (onAccelerateLocationCallback != null) {
                    onAccelerateLocationCallback.updateProgress(top);
                }

                if (top < minLimitY || top > maxLimitY) {
                    return false;
                }

                lastTop = top;
                this.layout(left, top, right, bottom);
                break;

            case MotionEvent.ACTION_UP:
                if (onAccelerateLocationCallback != null) {
                    if (lastTop >= 450) {
                        lastTop = maxLimitY;
                    }
                    float percent = (float) (maxLimitY - lastTop) / maxLimitY;
                    onAccelerateLocationCallback.currentLocation(percent * MAX_SPEED);
                }
                break;
        }
        return true;
    }


    public void addLimit(int min, int max) {
        this.minLimitY = min;
        this.maxLimitY = max;
    }


    public void setOnAccelerateLocationCallback(OnAccelerateLocationCallback callback) {
        this.onAccelerateLocationCallback = callback;
    }


    public interface OnAccelerateLocationCallback {
        void updateProgress(int location);

        void currentLocation(float speed);
    }

}

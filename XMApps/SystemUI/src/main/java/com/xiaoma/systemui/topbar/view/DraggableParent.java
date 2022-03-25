package com.xiaoma.systemui.topbar.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import com.xiaoma.systemui.common.util.LogUtil;

import java.util.Arrays;

/**
 * Created by LKF on 2018/11/15 0015.
 */
public class DraggableParent extends FrameLayout {
    private ViewDragHelper mDragHelper;
    private DragCallback mDragCallback;
    private boolean mNestedScrolling;
    private float mLastVelX;
    private float mLastVelY;

    private void logI(String format, Object... args) {
        LogUtil.logI(getClass().getSimpleName(), format, args);
    }

    public DraggableParent(@NonNull Context context) {
        super(context);
    }

    public DraggableParent(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public DraggableParent(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        setNestedScrollingEnabled(true);// 启动嵌套滑动机制
        mDragHelper = ViewDragHelper.create(this, new ViewDragHelper.Callback() {
            @Override
            public boolean tryCaptureView(@NonNull View child, int pointerId) {
                return true;
            }

            @Override
            public void onViewCaptured(@NonNull View capturedChild, int activePointerId) {
                if (mDragCallback != null) {
                    mDragCallback.onViewCaptured(capturedChild, activePointerId);
                }
            }

            @Override
            public int clampViewPositionHorizontal(@NonNull View child, int left, int dx) {
                // 屏蔽横向滑动
                return child.getLeft();
            }

            @Override
            public int clampViewPositionVertical(@NonNull View child, int top, int dy) {
                if (mDragCallback != null) {
                    return mDragCallback.clampViewPositionVertical(child, top, dy);
                }
                return child.getTop();
            }

            @Override
            public void onViewReleased(@NonNull View releasedChild, float xVel, float yVel) {
                if (mDragCallback != null) {
                    mDragCallback.onViewReleased(releasedChild, xVel, yVel);
                }
            }
        });
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return mDragHelper.shouldInterceptTouchEvent(ev);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mDragHelper.processTouchEvent(event);
        return true;
    }

    public void setDragCallback(DragCallback callback) {
        mDragCallback = callback;
    }

    //================Nested begin================
    @Override
    public boolean onStartNestedScroll(@NonNull View child, @NonNull View target, int nestedScrollAxes) {
        logI("onStartNestedScroll( child: %s, target: %s, nestedScrollAxes: %s )", child.getClass().getSimpleName(), target.getClass().getSimpleName(), nestedScrollAxes);
        return true;
    }

    @Override
    public void onNestedScrollAccepted(@NonNull View child, @NonNull View target, int axes) {
        super.onNestedScrollAccepted(child, target, axes);
        logI("onNestedScrollAccepted( child: %s, target: %s, axes: %s )", child.getClass().getSimpleName(), target.getClass().getSimpleName(), axes);
        mNestedScrolling = true;
        mLastVelX = mLastVelY = 0;
        if (mDragCallback != null) {
            mDragCallback.onViewCaptured(child, 0);
        }
    }

    @Override
    public void onNestedPreScroll(@NonNull View target, int dx, int dy, @NonNull int[] consumed) {
        if (mDragCallback != null && mDragCallback.shouldConsumeNestedScroll(target, dx, dy)) {
            consumed[0] = dx;
            consumed[1] = dy;
            if (Math.abs(dy) > Math.abs(dx)) {
                mDragCallback.clampViewPositionVertical(target, 0, -dy);
            }
        }
        logI("onNestedPreScroll( target: %s, dx: %s, dy: %s, consumed: %s )", target.getClass().getSimpleName(), dx, dy, Arrays.toString(consumed));
    }

    @Override
    public void onNestedScroll(@NonNull View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
        super.onNestedScroll(target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed);
        logI("onNestedScroll( target: %s, dxConsumed: %s, dyConsumed: %s, dxUnconsumed: %s, dyUnconsumed: %s ) NestedScrolling: %s",
                target.getClass().getSimpleName(), dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, mNestedScrolling);
        if (!mNestedScrolling)
            return;
        if (mDragCallback != null && Math.abs(dyUnconsumed) > Math.abs(dxUnconsumed)) {
            mDragCallback.clampViewPositionVertical(target, 0, -dyUnconsumed);
        }
    }

    @Override
    public boolean onNestedPreFling(@NonNull View target, float velocityX, float velocityY) {
        logI("onNestedPreFling( target: %s, xVel: %s, yVel: %s )", target.getClass().getSimpleName(), velocityX, velocityY);
        return super.onNestedPreFling(target, velocityX, velocityY);
    }

    @Override
    public boolean onNestedFling(@NonNull View target, float velocityX, float velocityY, boolean consumed) {
        logI("onNestedFling( target: %s, xVel: %s, yVel: %s, consumed: %s )", target.getClass().getSimpleName(), velocityX, velocityY, consumed);
        mLastVelX = velocityX;
        mLastVelY = velocityY;
        if (mDragCallback != null) {
            mDragCallback.onViewReleased(target, velocityX, velocityY);
        }
        mNestedScrolling = false;
        return super.onNestedFling(target, velocityX, velocityY, consumed);
    }

    @Override
    public void onStopNestedScroll(@NonNull View child) {
        super.onStopNestedScroll(child);
        logI("onStopNestedScroll( child: %s )", child.getClass().getSimpleName());
        if (mNestedScrolling) {
            mNestedScrolling = false;
            if (mDragCallback != null) {
                mDragCallback.onViewReleased(child, mLastVelX, mLastVelY);
            }
        }
    }
    //================Nested end==================

    public interface DragCallback {
        void onViewCaptured(@NonNull View capturedChild, int activePointerId);

        int clampViewPositionVertical(@NonNull View child, int top, int dy);

        void onViewReleased(@NonNull View releasedChild, float xVel, float yVel);

        boolean shouldConsumeNestedScroll(@NonNull View target, int dx, int dy);
    }
}
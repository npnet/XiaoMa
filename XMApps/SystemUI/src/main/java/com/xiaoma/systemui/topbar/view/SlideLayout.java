package com.xiaoma.systemui.topbar.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.FrameLayout;

import com.xiaoma.systemui.common.util.LogUtil;

/**
 * Created by LKF on 2019-3-15 0015.
 * 侧滑删除控件
 */
public class SlideLayout extends FrameLayout {
    private final String TAG = getClass().getSimpleName();
    private ViewDragHelper mDragHelper;
    private View mMenu;
    private View mContent;
    private OnClickListener mItemClickListener;

    public SlideLayout(Context context) {
        super(context);
        init();
    }

    public SlideLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SlideLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mDragHelper = ViewDragHelper.create(this, new ViewDragHelper.Callback() {
            private int mScrollDirection;

            private boolean isContent(View v) {
                return v == mContent;
            }

            @Override
            public boolean tryCaptureView(@NonNull View child, int pointerId) {
                mScrollDirection = 0;
                if (isContent(child)) {
                    LogUtil.logI(TAG, "tryCaptureView() Content");
                } else {
                    LogUtil.logI(TAG, "tryCaptureView() Menu");
                }
                return true;
            }

            @Override
            public int clampViewPositionHorizontal(@NonNull View child, int left, int dx) {
                LogUtil.logI(TAG, "clampViewPositionHorizontal( left: %s, dx: %s )", left, dx);
                if (mScrollDirection == 0) {
                    mScrollDirection = ViewDragHelper.DIRECTION_HORIZONTAL;
                }
                if (ViewDragHelper.DIRECTION_HORIZONTAL == mScrollDirection
                        && isContent(child)) {
                    getParent().requestDisallowInterceptTouchEvent(true);
                    int newLeft = left + dx;
                    if (newLeft < 0) {
                        newLeft = 0;
                    } else {
                        final int menuW = mMenu.getMeasuredWidth();
                        if (newLeft > menuW) {
                            newLeft = menuW;
                        }
                    }
                    return newLeft;
                }
                return super.clampViewPositionHorizontal(child, left, dx);
            }

            @Override
            public int clampViewPositionVertical(@NonNull View child, int top, int dy) {
                LogUtil.logI(TAG, "clampViewPositionVertical( top: %s, dy: %s )", top, dy);
                if (mScrollDirection == 0) {
                    mScrollDirection = ViewDragHelper.DIRECTION_VERTICAL;
                }
                if (ViewDragHelper.DIRECTION_VERTICAL == mScrollDirection) {
                    getParent().requestDisallowInterceptTouchEvent(false);
                }
                return super.clampViewPositionVertical(child, top, dy);
            }

            @Override
            public void onViewReleased(@NonNull final View releasedChild, final float xvel, final float yvel) {
                if (!isContent(releasedChild) || mScrollDirection == 0) {
                    // 点击事件
                    LogUtil.logI(TAG, "onViewReleased( releasedChild: %s,  xvel: %s, yvel: %s ) Click", releasedChild, xvel, yvel);
                    if (mItemClickListener != null) {
                        mItemClickListener.onClick(releasedChild);
                    }
                } else {
                    // 拖动事件
                    final boolean openMenu;
                    if (Math.abs(xvel) >= ViewConfiguration.get(getContext()).getScaledMinimumFlingVelocity()) {
                        openMenu = xvel > 0;
                    } else {
                        openMenu = releasedChild.getLeft() > mMenu.getMeasuredWidth() / 2;
                    }
                    LogUtil.logI(TAG, "onViewReleased( releasedChild: %s, xvel: %s, yvel: %s ) { openMenu: %s } Drag", releasedChild, xvel, yvel, openMenu);
                    if (mDragHelper.settleCapturedViewAt(openMenu ? mMenu.getMeasuredWidth() : 0, 0)) {
                        ViewCompat.postOnAnimation(releasedChild, new Runnable() {
                            @Override
                            public void run() {
                                if (mDragHelper.continueSettling(true)) {
                                    ViewCompat.postOnAnimation(releasedChild, this);
                                }
                            }
                        });
                    }
                    // 回调打开状态变化
                    if (mSlideChangeListener != null) {
                        if (openMenu) {
                            mSlideChangeListener.onMenuOpen(SlideLayout.this);
                        } else {
                            mSlideChangeListener.onMenuClose(SlideLayout.this);
                        }
                    }
                }
                getParent().requestDisallowInterceptTouchEvent(false);
                mScrollDirection = 0;
            }
        });
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mMenu = getChildAt(0);
        mContent = getChildAt(1);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        final boolean intercept = mDragHelper.shouldInterceptTouchEvent(ev);
        //LogUtil.logI(TAG, "onInterceptTouchEvent( ev: %s ) intercept: %s", MotionEvent.actionToString(ev.getAction()), intercept);
        return intercept;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        //LogUtil.logI(TAG, "onTouchEvent( ev: %s )", MotionEvent.actionToString(ev.getAction()));
        mDragHelper.processTouchEvent(ev);
        return true;
    }

    public void smoothOpenMenu() {
        LogUtil.logI(TAG, "smoothOpenMenu()");
        if (mSlideChangeListener != null && !isMenuOpen()) {
            mSlideChangeListener.onMenuOpen(this);
        }
        final View view = mContent;
        if (mDragHelper.smoothSlideViewTo(view, mMenu.getMeasuredWidth(), 0)) {
            ViewCompat.postOnAnimation(view, new Runnable() {
                @Override
                public void run() {
                    if (mDragHelper.continueSettling(false)) {
                        ViewCompat.postOnAnimation(view, this);
                    }
                }
            });
        }
    }

    public void smoothCloseMenu() {
        LogUtil.logI(TAG, "smoothCloseMenu()");
        if (mSlideChangeListener != null && isMenuOpen()) {
            mSlideChangeListener.onMenuClose(this);
        }
        final View view = mContent;
        if (mDragHelper.smoothSlideViewTo(view, 0, 0)) {
            ViewCompat.postOnAnimation(view, new Runnable() {
                @Override
                public void run() {
                    if (mDragHelper.continueSettling(false)) {
                        ViewCompat.postOnAnimation(view, this);
                    }
                }
            });
        }
    }

    public boolean isMenuOpen() {
        return mContent.getLeft() > 0;
    }

    public void setItemClickListener(OnClickListener onClickListener) {
        mItemClickListener = onClickListener;
    }

    private SlideChangeListener mSlideChangeListener;

    public void setSlideChangeListener(SlideChangeListener slideChangeListener) {
        mSlideChangeListener = slideChangeListener;
    }

    public interface SlideChangeListener {
        void onMenuOpen(SlideLayout parent);

        void onMenuClose(SlideLayout parent);
    }
}
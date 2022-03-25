package com.xiaoma.bluetooth.phone.common.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Scroller;
import android.widget.TextView;

import com.xiaoma.bluetooth.phone.R;

public class RightSlideView extends LinearLayout {

    private static final String TAG = "RightSlideView";

    private LinearLayout mContentLayout;
    private RelativeLayout mDeleteLayout;
    private Scroller mScroller;
    private OnSlidingListener mSlideListener;

    private int mScrollWidth;
    private int mLastX = 0;
    private int mLastY = 0;
    private int mDownX = 0;
    private int mDownY = 0;
    private static final int TAN = 2;
    private boolean isOpen;
    private int mTouchSlop;
    private boolean isClickEvent;

    public RightSlideView(Context context) {
        this(context, null);
    }

    public RightSlideView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    private void initView(Context context) {
        mScroller = new Scroller(context);
        setOrientation(LinearLayout.HORIZONTAL);
        View.inflate(context, R.layout.layout_right_slide_view, this);
        mContentLayout = findViewById(R.id.layout_content);
        mDeleteLayout = findViewById(R.id.layout_delete);
        mDeleteLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSlideListener != null) {
                    mSlideListener.onDelete(RightSlideView.this);
                }
            }
        });
        mTouchSlop = getResources().getDimensionPixelSize(R.dimen.right_slide_view_touch_slop);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        mContentLayout.getLayoutParams().width = MeasureSpec.getSize(widthMeasureSpec);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (changed) {
            mScrollWidth = mDeleteLayout.getWidth();
            scrollTo(mScrollWidth, 0);
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int x = (int) event.getX();
        int y = (int) event.getY();
        int scrollX = getScrollX();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                Log.d(TAG, "ACTION_DOWN X: " + x + "  " + y+ this);
                isClickEvent = true;
                mDownX = x;
                mDownY = y;
                if (!mScroller.isFinished()) {
                    mScroller.abortAnimation();
                }
                break;
            }

            case MotionEvent.ACTION_MOVE: {
                Log.d(TAG, "ACTION_MOVE X: " + x + "  " + y+ this);
                int deltaX = x - mLastX;
                int deltaY = y - mLastY;
                if (Math.abs(x - mDownX) > mTouchSlop || Math.abs(y - mDownY) > mTouchSlop) {
                    isClickEvent = false;
                }
                if (Math.abs(deltaX) < Math.abs(deltaY) * TAN) {
                    break;
                }
                if (getParent() != null) {
                    getParent().requestDisallowInterceptTouchEvent(true);
                }
                int newScrollX = scrollX - deltaX;
                if (deltaX != 0) {
                    if (newScrollX < 0) {
                        newScrollX = 0;
                    } else if (newScrollX > mScrollWidth) {
                        newScrollX = mScrollWidth;
                    }
                    this.scrollTo(newScrollX, 0);
                }

                if (mSlideListener != null) {
                    mSlideListener.onMove(this);
                }
                break;
            }

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL: {

                Log.d(TAG, "ACTION_UP X: " + x + "  " + y+ this);
                int mMinX = mDownX - mTouchSlop;
                int mMaxX = mDownX + mTouchSlop;
                int mMinY = mDownY - mTouchSlop;
                int mMaxY = mDownY + mTouchSlop;
                if (isClickEvent && (x >= mMinX && x <= mMaxX) && (y >= mMinY && y <= mMaxY)) {
                    if (mSlideListener != null) {
                        mSlideListener.onDown(this);
                        scrollTo(mScrollWidth, 0);
                    }
                    break;
                }
                int newScrollX;
                if (scrollX < (mScrollWidth / 2)) {
                    isOpen = true;
                    newScrollX = 0;
                    if (mSlideListener != null) {
                        mSlideListener.onMenuIsOpen(this);
                    }
                } else {
                    isOpen = false;
                    newScrollX = mScrollWidth;
                }
                this.smoothScrollTo(newScrollX);
                break;
            }

            default:
                break;
        }

        mLastX = x;
        mLastY = y;
        return true;
    }

    private void smoothScrollTo(int destX) {
        int scrollX = getScrollX();
        int delta = destX - scrollX;
        mScroller.startScroll(scrollX, 0, delta, 0, Math.abs(delta) * 3);
        invalidate();
    }

    public void closeMenu() {
        if (!isOpen) {
            return;
        }
        isOpen = false;
        if (getScrollX() != mScrollWidth) {
            this.smoothScrollTo(mScrollWidth);
        }
    }

    public boolean isOpen() {
        return isOpen;
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            postInvalidate();
        }
    }

    public void setDeleteButtonText(CharSequence text) {
        ((TextView) findViewById(R.id.tv_delete)).setText(text);
    }

    public void addContentView(View view) {
        mContentLayout.addView(view);
    }

    public void setOnSlidingListener(OnSlidingListener listener) {
        mSlideListener = listener;
    }

    public interface OnSlidingListener {

        void onMenuIsOpen(RightSlideView view);

        void onDown(RightSlideView view);

        void onMove(RightSlideView view);

        void onDelete(RightSlideView view);

    }

}
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

public class LeftSlideView extends LinearLayout {

    private static final String TAG = "LeftSlideView";

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

    public LeftSlideView(Context context) {
        this(context, null);
    }

    public LeftSlideView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    private void initView(Context context) {
        mScroller = new Scroller(context);
        setOrientation(LinearLayout.HORIZONTAL);
        View.inflate(context, R.layout.layout_left_slide_view, this);
        mContentLayout = findViewById(R.id.layout_content);
        mDeleteLayout = findViewById(R.id.layout_delete);
        mDeleteLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSlideListener != null) {
                    mSlideListener.onDelete(LeftSlideView.this);
                }
            }
        });
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

                mDownX = x;
                mDownY = y;
                if (!mScroller.isFinished()) {
                    mScroller.abortAnimation();
                }
                break;
            }

            case MotionEvent.ACTION_MOVE: {

                int deltaX = x - mLastX;
                int deltaY = y - mLastY;
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

                Log.d(TAG, "X: " + mDownX + "  " + x);
                Log.d(TAG, "Y: " + mDownY + "  " + y);
                if (mDownX == x && mDownY == y) {
                    if (mSlideListener != null) {
                        mSlideListener.onDown(this);
                    }
                    break;
                }
                int newScrollX = 0;
                if (scrollX >= (mScrollWidth / 2)) {
                    isOpen = true;
                    newScrollX = mScrollWidth;
                    if (mSlideListener != null) {
                        mSlideListener.onMenuIsOpen(this);
                    }
                } else {
                    isOpen = false;
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
        if (getScrollX() != 0) {
            this.smoothScrollTo(0);
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

        void onMenuIsOpen(LeftSlideView view);

        void onDown(LeftSlideView view);

        void onMove(LeftSlideView view);

        void onDelete(LeftSlideView view);

    }

}
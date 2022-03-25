package com.xiaoma.personal.feedback.ui.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.NestedScrollView;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Gillben on 2019/1/21 0021
 * <p>
 * desc: 扩展NestedScrollView 的内容变化监听
 */
public class XMNestedScrollView extends NestedScrollView {

    private static final String TAG = "XMNestedScrollView";
    private OnNestedStatusCallback onNestedStatusCallback;

    public XMNestedScrollView(@NonNull Context context) {
        this(context, null);
    }

    public XMNestedScrollView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public XMNestedScrollView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (onNestedStatusCallback != null) {
            onNestedStatusCallback.scrollEnable();
        }
    }


    /**
     * 判断是否可滑动
     */
    private boolean canScroll() {
        View child = getChildAt(0);
        if (child != null) {
            int childHeight = child.getHeight();
            return getHeight() < childHeight + getPaddingTop() + getPaddingBottom();
        }
        return false;
    }


    public void setOnNestedStatusCallback(OnNestedStatusCallback onNestedStatusCallback) {
        this.onNestedStatusCallback = onNestedStatusCallback;
    }


    public interface OnNestedStatusCallback {
        void scrollEnable();
    }
}

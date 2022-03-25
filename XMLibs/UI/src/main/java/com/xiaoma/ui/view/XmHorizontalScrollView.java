package com.xiaoma.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.HorizontalScrollView;

public class XmHorizontalScrollView extends HorizontalScrollView implements XmScrollViewBar.Scrollable {
    public XmHorizontalScrollView(Context context) {
        super(context);
    }

    public XmHorizontalScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public XmHorizontalScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public int getVerticalScrollOffset() {
        return 0;
    }

    @Override
    public int getVerticalScrollRange() {
        return 0;
    }

    public int getHorizontalScrollOffset() {
        return computeHorizontalScrollOffset();
    }

    public int getHorizontalScrollRange() {
        return computeHorizontalScrollRange();
    }


    @Override
    public void addOnLayoutChangeListener(OnLayoutChangeListener listener) {
        super.addOnLayoutChangeListener(listener);
    }
}

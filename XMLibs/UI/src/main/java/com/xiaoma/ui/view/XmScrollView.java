package com.xiaoma.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;

public class XmScrollView extends ScrollView implements XmScrollViewBar.Scrollable {
    public XmScrollView(Context context) {
        super(context);
    }

    public XmScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public XmScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public int getVerticalScrollOffset() {
        return computeVerticalScrollOffset();
    }

    public int getVerticalScrollRange() {
        return computeVerticalScrollRange();
    }

    @Override
    public int getHorizontalScrollOffset() {
        return 0;
    }

    @Override
    public int getHorizontalScrollRange() {
        return 0;
    }
}

package com.xiaoma.dualscreen.views;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

/**
 * @author: iSun
 * @date: 2019/3/7 0007
 */
public class PhoneWindowView extends BaseView {
    public PhoneWindowView(Context context) {
        super(context);
    }

    public PhoneWindowView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public PhoneWindowView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public PhoneWindowView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public void onRefresh() {

    }

    @Override
    public int contentViewId() {
        return 0;
    }
}

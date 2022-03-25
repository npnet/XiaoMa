package com.xiaoma.assistant.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xiaoma.assistant.R;
import com.xiaoma.assistant.model.LimitInfo;

/**
 * User:Created by Terence
 * IDE: Android Studio
 * Date:2018/12/7
 * Desc:
 */
public class LimitDesView extends LinearLayout {
    private TextView time;
    private TextView place;
    private LimitInfo.Des desInfo;

    public LimitDesView(Context context) {
        super(context);
        initView();
    }

    public LimitDesView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public LimitDesView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        inflate(getContext(), R.layout.view_assistant_limit_des, this);
        time = findViewById(R.id.tv_limit_time);
        place = findViewById(R.id.tv_limit_place);
    }

    public void setInfo(LimitInfo.Des info) {
        if (info == null) {
            return;
        }
        this.desInfo = info;

        time.setText(desInfo.getTime());
        place.setText(desInfo.getPlace());
    }
}


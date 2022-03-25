package com.xiaoma.assistant.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xiaoma.assistant.R;

/**
 * User:Created by Terence
 * IDE: Android Studio
 * Date:2018/12/6
 * Desc:日期时间
 */
public class TimeItemView extends RelativeLayout{

    private TextView tvContent;

    public TimeItemView(Context context) {
        super(context);
        initView();
    }

    public TimeItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public TimeItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }


    private void initView() {
        inflate(getContext(), R.layout.view_assistant_time,this);
        tvContent = findViewById(R.id.tv_assistant_time_content);
    }


    public void setData(String content) {
        tvContent.setText(content);
    }
}

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
 * Desc:翻译
 */
public class TranslationView  extends RelativeLayout{

    private TextView tvContent;

    public TranslationView(Context context) {
        super(context);
        initView();
    }

    public TranslationView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public TranslationView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }


    private void initView() {
        inflate(getContext(), R.layout.view_assistant_translation,this);
        tvContent = findViewById(R.id.tv_assistant_translation_content);
    }


    public void setData(String content) {
        tvContent.setText(content);
    }
}

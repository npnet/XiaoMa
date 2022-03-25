package com.xiaoma.launcher.common.views;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.xiaoma.launcher.R;

/**
 * @author taojin
 * @date 2019/1/2
 */
public class EntertainmentView extends FrameLayout {

    private ImageView ivEntertainment;
    private TextView tvEntertainment;
    private Button btnOperation;

    public EntertainmentView(@NonNull Context context) {
        super(context);
        initView();
    }

    public EntertainmentView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public EntertainmentView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }


    private void initView() {
        inflate(getContext(), R.layout.view_entertainment, this);
        ivEntertainment = findViewById(R.id.iv_entertainment);
        tvEntertainment = findViewById(R.id.tv_entertainment);
        btnOperation = findViewById(R.id.btn_operation);
    }


    public void setBtnText(String text) {
        btnOperation.setText(text);
    }

    public void setTextView(String text) {
        tvEntertainment.setText(text);
    }
}

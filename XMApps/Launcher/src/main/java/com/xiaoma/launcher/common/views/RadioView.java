package com.xiaoma.launcher.common.views;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.xiaoma.launcher.R;
import com.xiaoma.ui.view.MarqueeTextView;

/**
 * @author taojin
 * @date 2019/1/2
 */
public class RadioView extends FrameLayout {

    private ImageView ivRadio;
    private ImageView ivRadioPrevious;
    private ImageView ivRadioNext;
    private ImageView ivRadioPlayPause;
    private MarqueeTextView marqueeTextView;

    public RadioView(@NonNull Context context) {
        super(context);
        initView();
    }

    public RadioView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public RadioView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }


    private void initView() {
        inflate(getContext(), R.layout.view_radio, this);
        ivRadio = findViewById(R.id.iv_radio);
        ivRadioPrevious = findViewById(R.id.iv_radio_previous);
        ivRadioNext = findViewById(R.id.iv_radio_next);
        ivRadioPlayPause = findViewById(R.id.iv_radio_play_pause);
        marqueeTextView = findViewById(R.id.tv_navi_tip);


    }
}

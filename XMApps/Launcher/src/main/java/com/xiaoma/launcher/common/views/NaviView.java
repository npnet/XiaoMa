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
public class NaviView extends FrameLayout {

    private ImageView ivNavi;
    private ImageView ivMap;
    private ImageView ivSearch;
    private MarqueeTextView marqueeTextView;

    public NaviView(@NonNull Context context) {
        super(context);
        initView();
    }

    public NaviView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public NaviView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }


    private void initView() {
        inflate(getContext(), R.layout.view_navi, this);
        ivNavi = findViewById(R.id.iv_navi);
        ivMap = findViewById(R.id.iv_map);
        ivSearch = findViewById(R.id.iv_search);
        marqueeTextView = findViewById(R.id.tv_navi_tip);
        marqueeTextView.startMarquee();


    }
}

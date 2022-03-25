package com.xiaoma.ui.StateControl;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.xiaoma.ui.R;

public class NoNetworkView extends FrameLayout implements IStateViewDataChange {
    private StateViewConfig config;
    private TextView tvNoNetWork, tvRetry;
    private ImageView ivTips;

    public NoNetworkView(@NonNull Context context, StateViewConfig config) {
        super(context);
        init(context, config);
    }

    public NoNetworkView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public NoNetworkView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void init(Context context, StateViewConfig config) {
        if (context == null || config == null) {
            throw new NullPointerException();
        }
        this.config = config;
        config.addDataChangeListener(this);
        View.inflate(context, R.layout.state_no_network_view, this);
        tvNoNetWork = findViewById(R.id.tv_tips);
        tvRetry = findViewById(R.id.tv_retry);
        ivTips = findViewById(R.id.iv_tips);
    }

    @Override
    public void dataChange() {
        setText();
        setImage();
    }

    public void setText() {
        if (config == null) {
            return;
        }
        if (tvRetry != null) {
            tvRetry.setText(config.getNoNetWorkRetryText());
            tvRetry.setTextSize(config.getReTryTextSize());
            tvRetry.setTextColor(config.getReTryTextColor());
        }
        if (tvNoNetWork != null) {
            tvNoNetWork.setText(config.getNoNetWorkText());
            tvNoNetWork.setTextSize(config.getTextSize());
            tvNoNetWork.setTextColor(config.getTextColor());
        }
    }

    public void setImage() {
        if (ivTips != null) {
            ivTips.setImageResource(config.getNoNewWorkImage());
        }
    }
}

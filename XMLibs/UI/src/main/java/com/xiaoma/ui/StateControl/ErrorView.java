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

public class ErrorView extends FrameLayout implements IStateViewDataChange {
    private StateViewConfig config;
    private TextView tvError, tvRetry;
    private ImageView ivTips;

    public ErrorView(@NonNull Context context, StateViewConfig config) {
        super(context);
        init(context, config);
    }

    public ErrorView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ErrorView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void init(Context context, StateViewConfig config) {
        if (context == null || config == null) {
            throw new NullPointerException();
        }
        this.config = config;
        config.addDataChangeListener(this);
        View.inflate(context, R.layout.state_error_view, this);
        tvError = findViewById(R.id.tv_tips);
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
            tvRetry.setText(config.getErrorRetryText());
            tvRetry.setTextSize(config.getReTryTextSize());
            tvRetry.setTextColor(config.getReTryTextColor());
        }
        if (tvError != null) {
            tvError.setText(config.getErrorText());
            tvError.setTextSize(config.getTextSize());
            tvError.setTextColor(config.getTextColor());
        }
    }

    public void setImage() {
        if (config == null || ivTips == null) {
            return;
        }
        ivTips.setImageResource(config.getErrorImage());
    }
}

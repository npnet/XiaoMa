package com.xiaoma.ui.StateControl;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.xiaoma.ui.R;


public class EmptyView extends FrameLayout implements IStateViewDataChange {
    private TextView tvEmpty;
    private StateViewConfig config;
    private ImageView ivTips;

    public EmptyView(Context context, StateViewConfig config) {
        super(context);
        init(context, config);
    }

    public EmptyView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public EmptyView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void init(Context context, StateViewConfig config) {
        if (context == null || config == null) {
            throw new NullPointerException();
        }
        this.config = config;
        config.addDataChangeListener(this);
        View.inflate(context, R.layout.state_empty_view, this);
        tvEmpty = findViewById(R.id.tv_tips);
        ivTips = findViewById(R.id.iv_tips);
    }


    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }


    @Override
    public void dataChange() {
        setText();
        setImage();
    }

    public void setText() {
        if (config == null || tvEmpty == null) {
            return;
        }
        tvEmpty.setText(config.getEmptyText());
        tvEmpty.setTextSize(config.getTextSize());
        tvEmpty.setTextColor(config.getTextColor());
    }

    public void setImage() {
        if (config == null || ivTips == null) {
            return;
        }
        ivTips.setImageResource(config.getEmptyImage());
    }
}

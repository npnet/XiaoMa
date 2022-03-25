package com.xiaoma.ui.StateControl;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import com.xiaoma.ui.R;
import com.xiaoma.ui.progress.loadview.LoadingIndicatorView;

public class LoadingView extends FrameLayout implements IStateViewDataChange {
    private StateViewConfig config;
    public LoadingView(@NonNull Context context,StateViewConfig config) {
        super(context);
        init(context,config);
    }

    public LoadingView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public LoadingView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void init(Context context, StateViewConfig config) {
        if (context == null || config == null) {
            throw new NullPointerException();
        }
        this.config = config;
        config.addDataChangeListener(this);
       View.inflate(context, R.layout.state_loading_view, this);
    }

    @Override
    public void dataChange() {

    }
}

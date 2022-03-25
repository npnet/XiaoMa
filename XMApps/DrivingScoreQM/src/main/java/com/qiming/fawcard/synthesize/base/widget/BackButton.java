package com.qiming.fawcard.synthesize.base.widget;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import com.qiming.fawcard.synthesize.R;

public class BackButton extends FrameLayout {
    public BackButton(Context context) {
        super(context);
        init(context);
    }

    public BackButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        // 加载布局
        View view = LayoutInflater.from(context).inflate(R.layout.back_home, null);
        view.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ((Activity) v.getContext()).finish();
            }
        });
        this.addView(view);
    }
}

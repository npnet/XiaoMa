package com.qiming.fawcard.synthesize.base.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.qiming.fawcard.synthesize.R;

public class CircularView extends FrameLayout {
    private TextView mTvNum;
    private TextView mTvTitle;

    public CircularView(@NonNull Context context) {
        super(context);
        init(context);
    }

    public CircularView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CircularView);
        String defaultNum = typedArray.getString(R.styleable.CircularView_defaultNum);
        String defaultTitle = typedArray.getString(R.styleable.CircularView_defaultTitle);
        mTvNum.setText(defaultNum);
        mTvTitle.setText(defaultTitle);
        typedArray.recycle();
    }

    private void init(Context context) {
        // 加载布局
        @SuppressLint("InflateParams") View view = LayoutInflater.from(context).inflate(R.layout.circular_view, null);
        view.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        mTvNum = view.findViewById(R.id.tvNum);
        mTvTitle = view.findViewById(R.id.tvTitle);
        this.addView(view);
    }

    public void setTvNum(String num) {
        mTvNum.setText(num);
    }
}

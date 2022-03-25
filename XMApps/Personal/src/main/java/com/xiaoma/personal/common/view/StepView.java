package com.xiaoma.personal.common.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xiaoma.personal.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kaka
 * on 19-1-16 下午3:58
 * <p>
 * desc: 简单抽取的通用步骤View
 * </p>
 */
public class StepView extends LinearLayout {

    public static final int DEF_SIZE = 3;
    private int mSize;
    private int mCurrent;
    private List<View> mItems;

    public StepView(Context context) {
        super(context);
    }

    public StepView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.StepView);
        mSize = typedArray.getInteger(R.styleable.StepView_size, DEF_SIZE);
        typedArray.recycle();
        initView();
    }

    public StepView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr, 0);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.StepView);
        mSize = typedArray.getInteger(R.styleable.StepView_size, DEF_SIZE);
        typedArray.recycle();
        initView();
    }

    private void initView() {
        mItems = new ArrayList<>(mSize);
        addItemView(0, mSize);
        refreshCurrent();
    }

    private void addItemView(int start, int end) {
        for (int i = start; i < end; i++) {
            View item = LayoutInflater.from(getContext()).inflate(R.layout.include_setp_item, this, false);
            TextView tvStep = item.findViewById(R.id.tv_step);
            View stepLine = item.findViewById(R.id.step_line);
            tvStep.setText(String.valueOf(i + 1));
            if (i == 0) {
                stepLine.setVisibility(GONE);
            }
            mItems.add(item);
            addViewInLayout(item, i, generateDefaultLayoutParams());
//            addView(item);
        }
        requestLayout();
    }

    public void setSize(int size) {
        if (size > mSize) {
            addItemView(mSize, size);
        } else if (size < mSize) {
            for (int i = mSize; i > size; i--) {
                removeView(mItems.get(i));
            }
        }

        mSize = size;

        if (mCurrent > mSize) mCurrent = mSize - 1;
    }

    public void setCurrent(int current) {
        mCurrent = current;
        if (mCurrent < 0 || mCurrent > mSize) {
            throw new RuntimeException("Index out of item count!");
        }
        refreshCurrent();
    }

    private void refreshCurrent() {
        for (int i = 0; i < mSize; i++) {
            View view = mItems.get(i);
            if (i < mCurrent) {
                view.setSelected(true);
            } else {
                view.setSelected(false);
            }
        }
    }
}

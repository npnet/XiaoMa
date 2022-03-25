package com.xiaoma.setting.common.views;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.xiaoma.setting.R;

/**
 * Created by Administrator on 2018/11/6 0006.
 */

public class ConfirmDialog extends Dialog{

    private TextView mLeftButton, mRightButton, mTvContent;

    public ConfirmDialog(@NonNull Context context) {
        super(context);
        initView();
    }

    public ConfirmDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        initView();
    }

    protected ConfirmDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        initView();
    }

    private void initView(){
        setContentView(R.layout.dialog_confirm);
        mLeftButton = findViewById(R.id.tv_left_button);
        mRightButton = findViewById(R.id.tv_right_button);
        mTvContent = findViewById(R.id.tv_content);
    }

    public void setLeftClickListener(View.OnClickListener onClickListener){
        mLeftButton.setOnClickListener(onClickListener);
    }

    public void setRightClickListener(View.OnClickListener onClickListener){
        mRightButton.setOnClickListener(onClickListener);
    }

    public void setContent(String content){
        mTvContent.setText(content);
    }
}

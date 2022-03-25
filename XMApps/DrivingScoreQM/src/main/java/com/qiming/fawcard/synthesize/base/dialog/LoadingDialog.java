package com.qiming.fawcard.synthesize.base.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.WindowManager;

import com.qiming.fawcard.synthesize.R;

import butterknife.ButterKnife;

public class LoadingDialog extends Dialog {
    private static final String TAG = "LoadingDialog";
    private Context mContext;
    public LoadingDialog(@NonNull Context context, int resID) {
        super(context);
        mContext = context;
        setContentView(resID);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {

        // 设置窗口大小
        WindowManager.LayoutParams attributes = getWindow().getAttributes();
        // 设置全屏
        attributes.width= WindowManager.LayoutParams.MATCH_PARENT;
        attributes.height= WindowManager.LayoutParams.MATCH_PARENT;
        attributes.horizontalMargin = 0;
        attributes.verticalMargin = 0;
        //DecorView填充为0，颜色为黑
        getWindow().getDecorView().setPadding(0, 0, 0, 0);
        getWindow().getDecorView().setBackgroundColor(mContext.getResources().getColor(R.color.colorDark));
        // 设置透明度
        attributes.alpha = 1f;
        getWindow().setAttributes(attributes);
        //点击以外的区域会不消失
        setCancelable(false);
    }

    @Override
    public void dismiss() {
        super.dismiss();
    }

    @Override
    public void show() {
        super.show();
    }
}

package com.xiaoma.setting.common.views;

import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.xiaoma.setting.R;

/**
 * @Author ZiXu Huang
 * @Data 2018/10/15
 */
public class ErrorDialog extends Dialog {

    public static final int DEFAULT_VALUE = -1;
    private ImageView errorImg;
    private TextView errorMsg;
    private long showDuration = 3 * 1000;
    private CountDownTimer countDownTimer;

    public ErrorDialog(@NonNull Context context) {
        super(context, R.style.CommonSettingDialog);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCanceledOnTouchOutside(false);
        setContentView(R.layout.dialog_error);
        initView();
    }

    private void initView() {
        errorImg = findViewById(R.id.error_image);
        errorMsg = findViewById(R.id.error_msg);
    }

    public void setErrorImg(@DrawableRes int img) {
        errorImg.setImageResource(img);
    }

    public void setErrorMsg(@StringRes int msg) {
        errorMsg.setText(msg);
    }

    public void setErrorMsg(String msg){
        errorMsg.setText(msg);
    }

    public void setShowDuration(long duration) {
        showDuration = duration;
    }

    @Override
    public void show() {
        setType();
        super.show();
        countDownDismiss();
    }

    private void setType() {
        if (Build.VERSION.SDK_INT>=26) {//8.0新特性
            getWindow().setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY);
        }else{
//            getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        }
    }

    private void countDownDismiss() {
        countDownTimer = new CountDownTimer(showDuration, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                dismiss();
            }
        };
        countDownTimer.start();
    }

    @Override
    public void dismiss() {
        super.dismiss();
        countDownTimer.cancel();
    }
}

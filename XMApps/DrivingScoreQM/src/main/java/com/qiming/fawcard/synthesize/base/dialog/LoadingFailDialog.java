package com.qiming.fawcard.synthesize.base.dialog;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.qiming.fawcard.synthesize.R;
import com.qiming.fawcard.synthesize.base.widget.BackButton;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;

public class LoadingFailDialog extends LoadingDialog {
    private static final String TAG = "LoadingFailDialog";
    @BindView(R.id.btn_fail)
    Button btnFail;
    @BindView(R.id.ibBackButton)
    BackButton ibBackButton;
    @BindView(R.id.tv_loading_fail)
    TextView tvLoadingFail;

    private PriorityListener mListener;

    @Inject
    public LoadingFailDialog(@NonNull Context context, PriorityListener listener) {
        super(context, R.layout.dialog_fail_loading);
        this.mListener = listener;
    }

    @OnClick({R.id.ibBackButton, R.id.btn_fail})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ibBackButton:
                dismiss();
                break;
            case R.id.btn_fail:
                if (null != mListener) {
                    mListener.setResult(true);
                }
                break;
            default:
                break;
        }
    }

    /**
     * mListener设定
     */
    public void setPriorityListener(PriorityListener listener) {
        this.mListener = listener;
    }

    /**
     * 自定义Dialog监听器
     */
    public interface PriorityListener {
        /**
         * 回调函数，用于在Dialog的监听事件触发后刷新Activity的UI显示
         */
        void setResult(Boolean result);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void showDialog(String message) {
        super.show();
        tvLoadingFail.setText(message);
    }
}

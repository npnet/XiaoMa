package com.xiaoma.music.common.pay.ui;

import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.xiaoma.image.ImageLoader;
import com.xiaoma.music.R;
import com.xiaoma.ui.toast.XMToast;

import java.util.Locale;

/**
 * <pre>
 *   @author Create by on Gillben
 *   date:   2019/6/27 0027 11:12
 *   desc:   支付窗口
 * </pre>
 */
public class QRCodePayDialog extends DialogFragment {


    private static final long INTERVAL_TIME = 1000;
    private static final long TOTAL_TIME = 10 * 60 * 1000;

    private ImageView qrCode;
    private TextView payDescText;
    private String qrCodeUrl;

    private CountDownTimer mCountDownTimer;
    private OnQRCodePayDismissCallback onQRCodePayDismissCallback;


    public void show(FragmentManager fragmentManager, String tag, String qrCodeUrl) {
        this.qrCodeUrl = qrCodeUrl;
        show(fragmentManager, tag);
    }


    public boolean isShowing() {
        return getDialog() != null && getDialog().isShowing();
    }


    public void setOnQRCodePayDismissCallback(OnQRCodePayDismissCallback callback) {
        this.onQRCodePayDismissCallback = callback;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.view_pay_page, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        startTimer();
    }

    @Override
    public void onStart() {
        super.onStart();
        initAttrs();
    }

    private void initAttrs() {
        getDialog().setCanceledOnTouchOutside(false);
        Window window = getDialog().getWindow();
        if (window != null) {
            WindowManager.LayoutParams layoutParams = window.getAttributes();
            layoutParams.width = 600;
            layoutParams.height = 400;
            window.setAttributes(layoutParams);
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
    }


    private void initView(View view) {
        qrCode = view.findViewById(R.id.iv_pay_qr_code);
        payDescText = view.findViewById(R.id.tv_pay_desc_text);
        view.findViewById(R.id.iv_close_vip_pay_dialog).setOnClickListener(v -> QRCodePayDialog.this.dismiss());

        ImageLoader.with(getContext())
                .load(qrCodeUrl)
                .placeholder(R.drawable.iv_nomer_default)
                .into(qrCode);
    }


    private void startTimer() {
        mCountDownTimer = new CountDownTimer(TOTAL_TIME, INTERVAL_TIME) {
            @Override
            public void onTick(long millisUntilFinished) {
                showTime(millisUntilFinished);
            }

            @Override
            public void onFinish() {
                XMToast.toastException(getContext(), R.string.pay_timeout);
                if (mCountDownTimer != null) {
                    mCountDownTimer.cancel();
                }
            }
        };
        mCountDownTimer.start();
    }

    private void showTime(long surplus) {
        if (isDetached()) {
            return;
        }

        int currentSurplus = (int) (surplus / 1000);
        if (currentSurplus <= 0) {
            XMToast.toastException(getContext(), R.string.pay_timeout);
            if (mCountDownTimer != null) {
                mCountDownTimer.cancel();
            }
            QRCodePayDialog.this.dismiss();
            return;
        }

        if (currentSurplus <= 5) {
            payDescText.setTextColor(Color.RED);
        }

        int min = currentSurplus / 60;
        int second = currentSurplus % 60;

        String minContent = String.format(Locale.CHINA, "%02d", min);
        String secondContent = String.format(Locale.CHINA, "%02d", second);
        payDescText.setText(getString(R.string.pay_surplus_timer, minContent, secondContent));
    }


    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if (mCountDownTimer != null) {
            mCountDownTimer.cancel();
        }
        if (onQRCodePayDismissCallback != null) {
            onQRCodePayDismissCallback.onDismiss();
        }
    }


    public interface OnQRCodePayDismissCallback {
        void onDismiss();
    }
}

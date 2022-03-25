package com.xiaoma.music.common.util;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.xiaoma.music.R;
import com.xiaoma.music.common.vm.ScanLoginVM;
import com.xiaoma.music.mine.model.XMLoginResult;
import com.xiaoma.music.mine.model.XMQrCodeResult;
import com.xiaoma.ui.dialog.XmDialog;
import com.xiaoma.utils.log.KLog;

/**
 * Author: loren
 * Date: 2019/7/3 0003
 * <p>
 * 暂时不用弹窗扫码登录，白做了
 */
public class KWScanLoginDialogUtils {

    private FragmentActivity activity;
    private ScanLoginVM vm;
    private static final String TAG = "KWScanLoginDialogUtils";

    public KWScanLoginDialogUtils(Fragment fragment) {
        this.activity = fragment.getActivity();
        vm = ViewModelProviders.of(fragment).get(ScanLoginVM.class);
    }

    public KWScanLoginDialogUtils(FragmentActivity activity) {
        this.activity = activity;
        vm = ViewModelProviders.of(activity).get(ScanLoginVM.class);
    }

    public void createDialog(OnLoginStateCallBack loginCallBack) {
        if (vm == null || activity == null) {
            return;
        }
        View view = LayoutInflater.from(activity).inflate(R.layout.dialog_kw_scan_login, null);
        XmDialog dialog = new XmDialog.Builder(activity)
                .setView(view)
                .create();
        ImageView codeIv = view.findViewById(R.id.login_code_iv);
        TextView text = view.findViewById(R.id.login_state_tv);
        view.findViewById(R.id.login_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                //销毁轮询
                vm.stopCheckQrCode();
            }
        });
        vm.getQrCodeResult().observe(activity, new Observer<XMQrCodeResult>() {
            @Override
            public void onChanged(@Nullable XMQrCodeResult qrCodeResult) {
                if (qrCodeResult != null && qrCodeResult.isSuccess()) {
                    codeIv.setImageBitmap(qrCodeResult.getBitmap());
                    vm.startCheckQrCode();
                } else {
                    //二维码获取失败
                    text.setText(R.string.get_login_code_failed);
                }
            }
        });
        vm.getLoginResult().observe(activity, new Observer<XMLoginResult>() {
            @Override
            public void onChanged(@Nullable XMLoginResult xmLoginResult) {
                if (xmLoginResult == null) {
                    text.setText(R.string.kw_login_failed);
                    vm.stopCheckQrCode();
                    if (loginCallBack != null) {
                        loginCallBack.onLoginFailed();
                    }
                    return;
                }
                KLog.d(TAG, "login kw state : " + xmLoginResult.getSDKBean().getState() + " messabe : " + xmLoginResult.getSDKBean().getMessage());
                if (xmLoginResult.isWait()) {
//                mLoginDescText.setText(R.string.obtain_qr_code_failed);
                    if (loginCallBack != null) {
                        loginCallBack.onLoginWait();
                    }
                } else if (xmLoginResult.isLogin()) {
                    text.setText(R.string.kw_login_success);
                    vm.stopCheckQrCode();
                    if (loginCallBack != null) {
                        loginCallBack.onLoginSuccess();
                    }
                    dialog.dismiss();
                } else {
                    text.setText(R.string.kw_login_failed_scan_retry);
                    if (loginCallBack != null) {
                        loginCallBack.onLoginFailed();
                    }
                    vm.stopCheckQrCode();
                    vm.requestQrCode();
                }
            }
        });
        vm.requestQrCode();
        dialog.show();
    }

    public interface OnLoginStateCallBack {

        void onLoginSuccess();

        void onLoginWait();

        void onLoginFailed();
    }
}

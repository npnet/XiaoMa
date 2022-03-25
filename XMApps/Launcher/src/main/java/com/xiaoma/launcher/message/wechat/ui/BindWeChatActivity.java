package com.xiaoma.launcher.message.wechat.ui;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;

import android.widget.TextView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;

import com.xiaoma.component.base.BaseActivity;

import com.xiaoma.launcher.R;
import com.xiaoma.launcher.message.wechat.model.QrCodeBean;
import com.xiaoma.launcher.message.wechat.vm.UserBindVM;
import com.xiaoma.model.XmResource;
import com.xiaoma.thread.ThreadDispatcher;
import com.xiaoma.ui.StateControl.StateView;
import com.xiaoma.ui.toast.XMToast;
import android.graphics.drawable.Drawable;

import com.xiaoma.utils.log.KLog;
import com.xiaoma.utils.tputils.TPUtils;

/**
 * Created by minxiwen on 2018/8/29 0029.
 */

public class BindWeChatActivity extends BaseActivity implements View.OnClickListener {
    private static final int CHECK_INTERVAL = 6000;
    private static final int  WECHAT_NOT_BIND = 0;
    private static final int  WECHAT_BIND = 1;
    private static final int   WECHAT_BIND_OTHER= 2;
    private static final String KEY_IS_BIND = "KEY_IS_BIND";
    private static final String KEY_QR_CODE_URL = "KEY_QR_CODE_URL";
    private TextView tvBindWeChat, tvBindWeChatSuccess, tvBindWeChatTip;
    private ImageView imageQrCode;
    private TextView unbindWeChat;
    private ProgressBar mGetQrProgress ;

    private boolean isPolling = false;
    private boolean onPolling = true;
    private boolean isBindSuccess = false;
    private String qrCodeUrl;

    private UserBindVM mUserBindVM;
    private Runnable checkBindStatusRunnable = new Runnable() {
        @Override
        public void run() {
            KLog.d("检查绑定状态");

            if (!onPolling){
                onPolling = true;
                mUserBindVM.fetchUserBindStatus();
            }

        }
    };
    private StateView mWechatBindQr,mWechatLoding,mWechatUpdate,mWechatBindSuccer;
    private ImageView mClear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getNaviBar().setVisibility(View.GONE);
        setContentView(R.layout.layout_bind_wechat);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        getWindow().setGravity(Gravity.START);
        initView();
        initData();
    }

    private void initView() {
        tvBindWeChat = findViewById(R.id.tv_bind_wechat);
        tvBindWeChatSuccess = findViewById(R.id.tv_bind_wechat_success);
        tvBindWeChatTip = findViewById(R.id.tv_bind_wechat_tip);
        imageQrCode = findViewById(R.id.image_qr_code);
        unbindWeChat = findViewById(R.id.btn_unbind_wechat);
        mGetQrProgress = findViewById(R.id.get_qr_progress);
        unbindWeChat.setOnClickListener(this);
        if (android.os.Build.VERSION.SDK_INT > 22) {
            final Drawable drawable = getApplicationContext().getResources().getDrawable(R.drawable.fetch_qr_code_progressbar_anim);
            mGetQrProgress.setIndeterminateDrawable(drawable);
        }
        mClear = findViewById(R.id.clean);
        mWechatBindQr = findViewById(R.id.wechat_bind_qr);
        mWechatLoding = findViewById(R.id.wechat_loding);
        mWechatUpdate = findViewById(R.id.wechat_update);
        mWechatBindSuccer = findViewById(R.id.wechat_bind_succer);
        mWechatUpdate.setOnClickListener(this);
        mClear.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_unbind_wechat:
                ThreadDispatcher.getDispatcher().removeOnMain(checkBindStatusRunnable);
                mUserBindVM.fetchUnBind();
                break;
            case R.id.ll_retry_qr_code:
                fetchQrCode();
                break;
                case R.id.clean:
                finish();
                break;
            default:
                break;
        }
    }

    private void initData() {
        isBindSuccess =  TPUtils.get(this, KEY_IS_BIND, false);
        qrCodeUrl = TPUtils.get(this, KEY_QR_CODE_URL, "");
        updateViews(isBindSuccess);
        mUserBindVM = ViewModelProviders.of(this).get(UserBindVM.class);
        checkBindStatus();
        initfetchQrCode();
        initUnbindWeChat();
    }

    private void updateViews(boolean isBindSuccess) {
        if (isBindSuccess) {
            mWechatBindQr.setVisibility(View.INVISIBLE);
            mWechatLoding.setVisibility(View.INVISIBLE);
            mWechatUpdate.setVisibility(View.INVISIBLE);
            mWechatBindSuccer.setVisibility(View.VISIBLE);
        } else {
                mWechatBindQr.setVisibility(View.VISIBLE);
                mWechatLoding.setVisibility(View.INVISIBLE);
                mWechatUpdate.setVisibility(View.INVISIBLE);
                mWechatBindSuccer.setVisibility(View.INVISIBLE);
            if (!TextUtils.isEmpty(qrCodeUrl)) {
                Glide.with(this).load(qrCodeUrl).into(imageQrCode);
            }
        }
    }
    /**
     * 检查绑定状态
     */
    private void checkBindStatus() {
        mUserBindVM.getBindStatusInfo().observe(this, new Observer<XmResource<Integer>>() {
            @Override
            public void onChanged(@Nullable XmResource<Integer> integerXmResource) {
                if (integerXmResource == null){
                    return;
                }

                integerXmResource.handle(new OnCallback<Integer>() {
                    @Override
                    public void onSuccess(Integer data) {
                        if (data == null) {
                            return;
                        }
                        if (data == WECHAT_NOT_BIND) {
                            isBindSuccess = false;
                            updateViews(false);
                            TPUtils.put(BindWeChatActivity.this, KEY_IS_BIND, false);
                            if (!isPolling) {
                                isPolling = true;
                                fetchQrCode();
                            } else {
                                if (onPolling){
                                    onPolling = false;
                                    ThreadDispatcher.getDispatcher().postOnMainDelayed(checkBindStatusRunnable, CHECK_INTERVAL);
                                }
                            }
                        } else if (data == WECHAT_BIND) {
                            isBindSuccess = true;
                            updateViews(true);
                            TPUtils.put(BindWeChatActivity.this, KEY_IS_BIND, true);
                            if (onPolling){
                                onPolling = false;
                                ThreadDispatcher.getDispatcher().postOnMainDelayed(checkBindStatusRunnable, CHECK_INTERVAL);
                            }
                            //ThreadDispatcher.getDispatcher().removeOnMain(checkBindStatusRunnable);
                        } else if (data == WECHAT_BIND_OTHER) {
                            //用户已经绑定了同型号别的车机了
                            // ToastUtil.showToast(BindWeChatActivity.this, baseResult.resultMessage);

                        }
                    }

                    @Override
                    public void onFailure(String msg) {
                        super.onFailure(msg);
                    }
                });
            }
        });
        mUserBindVM.fetchUserBindStatus();
    }

    /**
     * 加载绑定二维码
     */
    private void initfetchQrCode() {

        mUserBindVM.getOrCode().observe(this, new Observer<XmResource<QrCodeBean>>() {
            @Override
            public void onChanged(@Nullable XmResource<QrCodeBean> baseResultXmResource) {
                if (baseResultXmResource == null) {
                    return;
                }

                baseResultXmResource.handle(new OnCallback<QrCodeBean>() {
                    @Override
                    public void onSuccess(QrCodeBean qrcodeBean) {
                        mWechatLoding.setVisibility(View.GONE);
                        mWechatBindQr.setVisibility(View.VISIBLE);
                        if (qrcodeBean != null && !TextUtils.isEmpty(qrcodeBean.getQrCode())) {
                            qrCodeUrl = qrcodeBean.getQrCode();
                            TPUtils.put(BindWeChatActivity.this, KEY_QR_CODE_URL, qrCodeUrl);
                            Glide.with(BindWeChatActivity.this).load(qrcodeBean.getQrCode()).into(imageQrCode);
                            if (onPolling){
                                onPolling = false;
                                ThreadDispatcher.getDispatcher().postOnMainDelayed(checkBindStatusRunnable, CHECK_INTERVAL);
                            }

                        } else {
                            //获取二维码失败
                            mWechatUpdate.setVisibility(View.VISIBLE);
                            mWechatLoding.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onFailure(String msg) {
                        mWechatUpdate.setVisibility(View.VISIBLE);
                        mWechatLoding.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError(int code, String message) {
                        super.onError(code, message);
                    }
                });
            }
        });
    }
    private void fetchQrCode() {
        mWechatLoding.setVisibility(View.VISIBLE);
        mWechatUpdate.setVisibility(View.GONE);
        mWechatBindQr.setVisibility(View.GONE);
        mUserBindVM.fetchBindQRCode();
    }

    /**
     * 解除为新绑定
     */
    private void initUnbindWeChat() {
        mUserBindVM.getUnBindStatusInfo().observe(this, new Observer<XmResource<Integer>>() {
            @Override
            public void onChanged(@Nullable XmResource<Integer> integerXmResource) {
                if (integerXmResource == null){
                    return;
                }

                integerXmResource.handle(new OnCallback<Integer>() {
                    @Override
                    public void onSuccess(Integer data) {
                        dismissProgress();
                        if (data != null) {
                            TPUtils.put(BindWeChatActivity.this, KEY_IS_BIND, false);
                            mWechatBindQr.setVisibility(View.VISIBLE);
                            mWechatLoding.setVisibility(View.INVISIBLE);
                            mWechatUpdate.setVisibility(View.INVISIBLE);
                            mWechatBindSuccer.setVisibility(View.INVISIBLE);
                            if (!TextUtils.isEmpty(qrCodeUrl)) {
                                Glide.with(BindWeChatActivity.this).load(qrCodeUrl);
                                onPolling = false;
                                ThreadDispatcher.getDispatcher().postOnMainDelayed(checkBindStatusRunnable, CHECK_INTERVAL);
                            } else {
                                fetchQrCode();
                            }
                        } else {
                            XMToast.showToast(BindWeChatActivity.this, R.string.unbind_wechat_fail);
                        }
                    }

                    @Override
                    public void onFailure(String msg) {
                        dismissProgress();
                        XMToast.showToast(BindWeChatActivity.this, R.string.unbind_wechat_fail);
                    }
                });
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        onPolling = false;
        ThreadDispatcher.getDispatcher().postOnMain(checkBindStatusRunnable);
    }

    @Override
    protected void onPause() {
        super.onPause();
        ThreadDispatcher.getDispatcher().removeOnMain(checkBindStatusRunnable);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ThreadDispatcher.getDispatcher().removeOnMain(checkBindStatusRunnable);
    }

    /**
     * 推送消息到手机
     * @param view
     */
    public void pushPhone(View view) {
        mUserBindVM.getPushInfo().observe(this, new Observer<XmResource<Integer>>() {
            @Override
            public void onChanged(@Nullable XmResource<Integer> integerXmResource) {
                if (integerXmResource == null) {
                    return;
                }

                integerXmResource.handle(new OnCallback<Integer>() {
                    @Override
                    public void onSuccess(Integer data) {
                        XMToast.showToast(BindWeChatActivity.this,"success");
                    }

                    @Override
                    public void onFailure(String msg) {
                        XMToast.showToast(BindWeChatActivity.this,"success");
                    }

                    @Override
                    public void onError(int code, String message) {
                        super.onError(code, message);
                    }
                });

            }
        });
        mUserBindVM.pushMessageToPhone("","","","","");
    }
}

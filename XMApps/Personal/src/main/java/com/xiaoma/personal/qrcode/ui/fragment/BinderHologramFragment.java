package com.xiaoma.personal.qrcode.ui.fragment;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.xiaoma.component.base.BaseFragment;
import com.xiaoma.image.ImageLoader;
import com.xiaoma.model.ResultCallback;
import com.xiaoma.model.XMResult;
import com.xiaoma.personal.R;
import com.xiaoma.personal.common.RequestManager;
import com.xiaoma.personal.common.util.Utils;
import com.xiaoma.personal.qrcode.callback.WrapOnHandlepCallback;
import com.xiaoma.personal.qrcode.model.BindStatusBean;
import com.xiaoma.personal.qrcode.model.HologramQRCode;
import com.xiaoma.personal.qrcode.utils.LoopHologramStatusUtils;
import com.xiaoma.personal.qrcode.vm.QRCodeVM;
import com.xiaoma.ui.dialog.XmDialog;
import com.xiaoma.ui.progress.loading.XMProgress;
import com.xiaoma.ui.toast.XMToast;
import com.xiaoma.utils.NetworkUtils;
import com.xiaoma.utils.log.KLog;

import java.util.Objects;

/**
 * <pre>
 *   @author Create by on Gillben
 *   date:   2019/5/5 0005 11:43
 *   desc:   绑定全息影像
 * </pre>
 */
public class BinderHologramFragment extends BaseFragment implements View.OnClickListener {

    private static final String TAG = BinderHologramFragment.class.getSimpleName();
    private static final int LOOP_BIND_STATUS_INTERVAL = 2000;
    private ConstraintLayout mBindAccountLayout;
    private ImageView mDownloadAppImage;
    private ImageView mBindAccountImage;

    private ConstraintLayout mBoundAccountLayout;
    private ImageView mBoundAccountImage;
    private TextView mBoundAccountDesc;
    private Button mCancelBoundAccountBt;

    private QRCodeVM mQRCodeVM;

    public static BinderHologramFragment newInstance() {
        return new BinderHologramFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View contentView = inflater.inflate(R.layout.fragment_binder_hologram, container, false);
        return onCreateWrapView(contentView);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        iniData();
    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        //页面显示开启循环获取绑定状态、隐藏则关闭轮询
        if (isVisibleToUser) {
            startLoopBindStatus();
        } else {
            stopLoopBindStatus();
        }
    }

    private void initView(View view) {
        mBindAccountLayout = view.findViewById(R.id.bind_hologram_account_root);
        mDownloadAppImage = view.findViewById(R.id.iv_load_app_qr_code);
        mBindAccountImage = view.findViewById(R.id.iv_phone_app_qr_code);

        mBoundAccountLayout = view.findViewById(R.id.bound_hologram_account_root);
        mBoundAccountImage = view.findViewById(R.id.iv_bound_hologram_icon);
        mBoundAccountDesc = view.findViewById(R.id.tv_bound_account_content);
        mCancelBoundAccountBt = view.findViewById(R.id.bt_cancel_bound);

        mCancelBoundAccountBt.setOnClickListener(this);
    }


    private void iniData() {
        if (!NetworkUtils.isConnected(mContext)) {
            showNoNetView();
            return;
        }
        showContentView();
        mQRCodeVM = ViewModelProviders.of(this).get(QRCodeVM.class);
        checkHologramBindStatus();
    }


    @Override
    protected void noNetworkOnRetry() {
        iniData();
    }


    @Override
    public void onStop() {
        super.onStop();
        XMProgress.dismissProgressDialog(this);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        stopLoopBindStatus();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.bt_cancel_bound) {
            cancelBoundDialog();
        }
    }


    private void fetchBindData() {
        mQRCodeVM.getHologramQRCode().observe(this, hologramQRCodeXmResource -> {
            XMProgress.dismissProgressDialog(BinderHologramFragment.this);
            if (hologramQRCodeXmResource == null) {
                KLog.w(TAG, "BinderHologram's HologramQRCode instance is null.");
                return;
            }

            hologramQRCodeXmResource.handle(new WrapOnHandlepCallback<HologramQRCode>() {
                @Override
                public void onSuccess(HologramQRCode hologramQRCode) {
                    if (hologramQRCode == null) {
                        KLog.w(TAG, "HologramQRCode instance is null.");
                        return;
                    }
                    refreshData(hologramQRCode);
                }

                @Override
                public void onFailure(String message) {
                    showNoNetView();
                }
            });
        });
    }


    private void refreshData(HologramQRCode hologramQRCode) {
        mBoundAccountLayout.setVisibility(View.GONE);
        mBindAccountLayout.setVisibility(View.VISIBLE);

        ImageLoader.with(this)
                .load(hologramQRCode.getHoloApp().getQrcode())
                .placeholder(R.drawable.default_cover)
                .into(mDownloadAppImage);

        ImageLoader.with(this)
                .load(hologramQRCode.getBind().getQrcode())
                .placeholder(R.drawable.default_cover)
                .into(mBindAccountImage);
    }


    private void startLoopBindStatus() {
        LoopHologramStatusUtils.startLoop(LOOP_BIND_STATUS_INTERVAL, this::loopBindStatus);
    }


    private void stopLoopBindStatus() {
        LoopHologramStatusUtils.stopLoop();
    }


    private void loopBindStatus() {
        RequestManager.loopObtainHologramBindStatus(new ResultCallback<XMResult<BindStatusBean>>() {
            @Override
            public void onSuccess(XMResult<BindStatusBean> result) {
                stopLoopBindStatus();
                hologramBindSuccess(result.getData());
            }

            @Override
            public void onFailure(int code, String msg) {
                KLog.d(TAG, "bind status: " + code);
            }
        });

    }

    private void checkHologramBindStatus() {
        XMProgress.showProgressDialog(this, mContext.getString(R.string.base_loading));
        RequestManager.loopObtainHologramBindStatus(new ResultCallback<XMResult<BindStatusBean>>() {
            @Override
            public void onSuccess(XMResult<BindStatusBean> result) {
                XMProgress.dismissProgressDialog(BinderHologramFragment.this);
                hologramBindSuccess(result.getData());
            }

            @Override
            public void onFailure(int code, String msg) {
                KLog.w(TAG, "Hologram not bind.");
                XMProgress.dismissProgressDialog(BinderHologramFragment.this);

                if (code == 1040 || code == 1045) {
                    //没有绑定用户
                    fetchBindData();
                } else {
                    showNoNetView();
                }
            }
        });
    }


    private void hologramBindSuccess(BindStatusBean bindStatusBean) {
        if (bindStatusBean == null) {
            KLog.w(TAG, "BindStatusBean instance is null.");
            showEmptyView();
            return;
        }
        mBindAccountLayout.setVisibility(View.GONE);
        mBoundAccountLayout.setVisibility(View.VISIBLE);

        ImageLoader.with(mContext)
                .load(bindStatusBean.getIcon())
                .placeholder(R.drawable.default_cover)
                .into(mBoundAccountImage);
        String phone = Utils.convertSimplePhoneNumber(bindStatusBean.getPhone());
        mBoundAccountDesc.setText(mContext.getString(R.string.hologram_bound_phone_text, phone));
    }


    private void cancelBoundDialog() {
        View childView = View.inflate(mContext, R.layout.dialog_bind_hologram, null);
        TextView contentDesc = childView.findViewById(R.id.tv_pay_message);
        TextView cancelBoundBt = childView.findViewById(R.id.confirm_bt);
        contentDesc.setText(R.string.cancel_bound_account_desc);
        cancelBoundBt.setText(R.string.confirm_cancel_bound);
        contentDesc.setGravity(Gravity.START);

        XmDialog dialog = new XmDialog.Builder(Objects.requireNonNull(getActivity()))
                .setView(childView)
                .setWidth(mContext.getResources().getDimensionPixelOffset(R.dimen.width_big_dialog))
                .setHeight(mContext.getResources().getDimensionPixelOffset(R.dimen.height_big_dialog))
                .setCancelableOutside(false)
                .addOnClickListener(R.id.confirm_bt, R.id.cancel_bt)
                .setOnViewClickListener((view, xmDialog) -> {
                    xmDialog.dismiss();
                    if (view.getId() == R.id.confirm_bt) {
                        //TODO 处理解绑全息账号逻辑
                        unbindHologram();
                    }
                })
                .create();
        dialog.show();
    }


    private void unbindHologram() {
        RequestManager.unbindHologram(new ResultCallback<XMResult<String>>() {
            @Override
            public void onSuccess(XMResult<String> result) {
                //解绑成功，重新拉取二维码信息
                checkHologramBindStatus();
                startLoopBindStatus();
            }

            @Override
            public void onFailure(int code, String msg) {
                XMToast.showToast(mContext, R.string.hologram_unbind_failed);
            }
        });
    }

}

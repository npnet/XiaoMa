package com.xiaoma.music.mine.ui;

import android.arch.lifecycle.ViewModelProviders;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.xiaoma.component.base.BaseFragment;
import com.xiaoma.model.annotation.PageDescComponent;
import com.xiaoma.music.OnlineMusicFactory;
import com.xiaoma.music.R;
import com.xiaoma.music.common.constant.EventBusTags;
import com.xiaoma.music.common.constant.EventConstants;
import com.xiaoma.music.common.model.LoginOutEvent;
import com.xiaoma.music.common.pay.PayBusiness;
import com.xiaoma.music.kuwo.observer.IVipMessageObserver;
import com.xiaoma.music.mine.adapter.PriceOptionsAdapter;
import com.xiaoma.music.mine.adapter.PrivilegeOptionsAdapter;
import com.xiaoma.music.mine.callback.OnRefreshKWUserStatusCallback;
import com.xiaoma.music.mine.callback.VipOptionsHandleCallback;
import com.xiaoma.music.mine.model.VipListBean;
import com.xiaoma.music.mine.model.VipOptionsBean;
import com.xiaoma.music.mine.model.XMQrCodeResult;
import com.xiaoma.music.mine.vm.PurchasedVM;
import com.xiaoma.music.mine.vm.VipOptionVm;
import com.xiaoma.thread.ThreadDispatcher;
import com.xiaoma.ui.progress.loading.XMProgress;
import com.xiaoma.utils.NetworkUtils;
import com.xiaoma.utils.log.KLog;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import cn.kuwo.base.bean.VipUserInfo;

/**
 * Author: loren
 * Date: 2019/6/17 0017
 */
@PageDescComponent(EventConstants.PageDescribe.vipCenterFragment)
public class VipCenterFragment extends BaseFragment implements View.OnClickListener {

    private static final String TAG = VipCenterFragment.class.getSimpleName();
    private static final int LOGIN_SUCCESS_DELAY_TIME = 3000;
    private static final int REQUEST_LOGIN_QR_CODE_DELAY_TIME = 1000;
    private FrameLayout mLoginLayout;
    private ImageView mLoginQRCode;
    private TextView mLoginDescText;
    private Button mLoginUpdateBt;

    private ConstraintLayout mContentLayout;
    private TextView mVipValidityText;
    private RecyclerView mPrivilegeRecyclerView;
    private RecyclerView mRecyclerView;
    private Button mOpenVipBt;
    private PriceOptionsAdapter mPriceOptionsAdapter;
    private PrivilegeOptionsAdapter mPrivilegeOptionsAdapter;

    private VipOptionVm mVipOptionVm;
    private PurchasedVM mPurchasedVM;
    private VipOptionsBean vipOptionsBean;
    private OnRefreshKWUserStatusCallback onRefreshKWUserStatusCallback;
    private boolean isLoginSuccess;


    public static VipCenterFragment newInstance() {
        return new VipCenterFragment();
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        onRefreshKWUserStatusCallback = (OnRefreshKWUserStatusCallback) getParentFragment();
        OnlineMusicFactory.getKWLogin().attachVipMessage(observer);
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View contentView = inflater.inflate(R.layout.fragment_vip_center, container, false);
        return onCreateWrapView(contentView);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mVipOptionVm = ViewModelProviders.of(this).get(VipOptionVm.class);
        mPurchasedVM = ViewModelProviders.of(this).get(PurchasedVM.class);
        initView(view);
        fetchData();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mPurchasedVM != null) {
            mPurchasedVM.stopCheckQrCode();
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        // 取消监听VIP信息的获取
        OnlineMusicFactory.getKWLogin().detachVipMessage(observer);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_open_vip:
                openVip();
                break;

            case R.id.login_qrcode_state_btn:
                startLogin();
                break;
        }
    }


    private void initView(View view) {
        mLoginLayout = view.findViewById(R.id.login_scan_qrcode_fl);
        mLoginQRCode = view.findViewById(R.id.login_scan_qrcode_iv);
        mLoginDescText = view.findViewById(R.id.login_qrcode_state_tv);
        mLoginUpdateBt = view.findViewById(R.id.login_qrcode_state_btn);
        mLoginUpdateBt.setOnClickListener(this);
        mContentLayout = view.findViewById(R.id.vip_content_layout);
        mVipValidityText = view.findViewById(R.id.tv_vip_validity_period_title);
        mPrivilegeRecyclerView = view.findViewById(R.id.rv_vip_privilege);
        mRecyclerView = view.findViewById(R.id.rv_vip_price_options);
        mOpenVipBt = view.findViewById(R.id.bt_open_vip);
        mOpenVipBt.setOnClickListener(this);

        //vip价格选项
        mRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                outRect.set(outRect.left, outRect.top, outRect.right + 30, outRect.bottom);
            }
        });
        mPriceOptionsAdapter = new PriceOptionsAdapter();
        LinearLayoutManager manager = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
        mRecyclerView.setLayoutManager(manager);
        mRecyclerView.setAdapter(mPriceOptionsAdapter);

        mPriceOptionsAdapter.setOnItemClickListener((adapter, view1, position) -> {
            selectVipOptions(position);
        });

        //优享特权
        mPrivilegeRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                outRect.set(outRect.left, outRect.top, outRect.right, outRect.bottom + 20);
            }
        });
        mPrivilegeOptionsAdapter = new PrivilegeOptionsAdapter();
        LinearLayoutManager privilegeManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        mPrivilegeRecyclerView.setLayoutManager(privilegeManager);
        mPrivilegeRecyclerView.setAdapter(mPrivilegeOptionsAdapter);

    }

    @Override
    protected void noNetworkOnRetry() {
        fetchData();
    }


    @Subscriber(tag = EventBusTags.LOGIN_OUT_TAG)
    public void loginOut(LoginOutEvent loginOutEvent) {
        ThreadDispatcher.getDispatcher().postOnMain(() -> {
            //强制更新列表内容
            if (mPriceOptionsAdapter != null) {
                mPriceOptionsAdapter.notifyItemRangeChanged(0, mPriceOptionsAdapter.getItemCount());
            }
            mOpenVipBt.setText(R.string.open_vip_bt);
            mVipValidityText.setVisibility(View.GONE);
        });
    }

    private IVipMessageObserver observer = new IVipMessageObserver() {
        @Override
        public void IVipMgrObserver_OnStateChanged() {
            // VIP状态发生变化，暂时用不到
        }

        @Override
        public void IVipMgrObserver_OnLoaded(List<VipUserInfo> vipUserInfoList) {
            // 获取到的vip信息，包含（豪华VIP，音乐包，  车载VIP）
            if (!refreshVipStatus()) {
                return;
            }

            for (VipUserInfo vipUserInfo : vipUserInfoList) {
                if (vipUserInfo != null) {
                    KLog.d(TAG, "Vip type: " + vipUserInfo.mType);
                    //是否是车载vip
                    if (VipUserInfo.CATEGRAY_VIP.equalsIgnoreCase(vipUserInfo.mType)) {
                        updateVipValidityDateMessage(vipUserInfo);
                    }

                    if (isLoginSuccess && onRefreshKWUserStatusCallback != null) {
                        onRefreshKWUserStatusCallback.onRefreshKWUserStatus();
                        isLoginSuccess = false;
                    }
                }
            }
        }

        @Override
        public void IVIPMgrObserver_OnLoadFaild(int errorCode, String msg) {
            KLog.w(TAG, "errorCode: " + errorCode + "   msg: " + msg);
        }
    };

    private void openVip() {
        if (OnlineMusicFactory.getKWLogin().isUserLogon()) {
            payVip();
        } else {
            startLogin();
        }
    }


    private void payVip() {
        if (vipOptionsBean == null) {
            KLog.w(TAG, "vipOptionsBean is null.");
            return;
        }

        PayBusiness.getInstance().generateOrder(getActivity(),
                OnlineMusicFactory.getKWLogin().isCarVipUser(),
                vipOptionsBean.getId(),
                vipOptionsBean.getPrice(),
                vipOptionsBean.getCnt(),
                vipOptionsBean.getType(),
                () -> {
                    //更新会员有效期
                    fetchData();
                    //取消vip气泡窗口
                    EventBus.getDefault().post(false, EventBusTags.MAIN_SHOW_BUY_VIP_VIEW);
                    if (onRefreshKWUserStatusCallback != null) {
                        onRefreshKWUserStatusCallback.onRefreshKWUserStatus();
                    }
                });
    }


    private void startLogin() {
        XMProgress.showProgressDialog(this, mContext.getString(R.string.base_loading));
        //每次登录先释放旧数据，必须执行该操作，否则会造成登录异常
        mPurchasedVM.releaseData();

        mPurchasedVM.getQrCodeResult().observe(this, xmQrCodeResult ->
                ThreadDispatcher.getDispatcher().postOnMainDelayed(() -> {
                    XMProgress.dismissProgressDialog(VipCenterFragment.this);
                    handleLoginQRCode(xmQrCodeResult);
                }, REQUEST_LOGIN_QR_CODE_DELAY_TIME));
        mPurchasedVM.requestQrCode();
    }


    private void handleLoginQRCode(XMQrCodeResult xmQrCodeResult) {
        mContentLayout.setVisibility(View.GONE);
        mLoginLayout.setVisibility(View.VISIBLE);
        if (xmQrCodeResult != null && xmQrCodeResult.isSuccess()) {
            mLoginUpdateBt.setVisibility(View.GONE);
            mLoginDescText.setText(R.string.login_desc_content);
            mLoginQRCode.setImageBitmap(xmQrCodeResult.getBitmap());
            checkLoginStatus();
            mPurchasedVM.startCheckQrCode();
        } else {
            //TODO 二维码获取失败，进行刷新操作
            mLoginQRCode.setImageResource(R.drawable.obtain_qr_code_failed);
            mLoginDescText.setText(R.string.obtain_qr_code_failed);
            mLoginUpdateBt.setVisibility(View.VISIBLE);
        }
    }

    private void checkLoginStatus() {
        mPurchasedVM.getLoginResult().observe(this, xmLoginResult -> {
            if (xmLoginResult == null) {
                mLoginDescText.setText(R.string.kw_login_failed);
                mPurchasedVM.stopCheckQrCode();
                return;
            }

            KLog.d(TAG, "login kw state : " + xmLoginResult.getSDKBean().getState() + " msg : " + xmLoginResult.getSDKBean().getMessage());
            if (xmLoginResult.isWait()) {
//                mLoginDescText.setText(R.string.obtain_qr_code_failed);
            } else if (xmLoginResult.isLogin()) {
                mLoginQRCode.setImageResource(R.drawable.login_success);
                mLoginDescText.setText(R.string.kw_login_success);
                mPurchasedVM.stopCheckQrCode();
                loginSuccess();
            } else {
                mLoginUpdateBt.setVisibility(View.VISIBLE);
                mLoginDescText.setText(R.string.login_failed_refresh_qr_code);
                mPurchasedVM.stopCheckQrCode();
            }
        });
    }


    private void loginSuccess() {
        ThreadDispatcher.getDispatcher().postOnMainDelayed(() -> {
            mLoginLayout.setVisibility(View.GONE);
            mContentLayout.setVisibility(View.VISIBLE);
            isLoginSuccess = true;
            fetchData();
            handleVipPower();
        }, LOGIN_SUCCESS_DELAY_TIME);
    }

    private void handleVipPower() {
        //登录或开通成功后，如果用户是vip则隐藏vip气泡且自动开始播放
        if (OnlineMusicFactory.getKWLogin().isCarVipUser()) {
            EventBus.getDefault().post(false, EventBusTags.MAIN_SHOW_BUY_VIP_VIEW);
            OnlineMusicFactory.getKWPlayer().continuePlay();
        }
    }

    private void selectVipOptions(int position) {
        List<VipOptionsBean> optionsBeans = mPriceOptionsAdapter.getData();
        for (int index = 0; index < optionsBeans.size(); index++) {
            VipOptionsBean bean = optionsBeans.get(index);
            if (index == position) {
                bean.setSelect(true);
                vipOptionsBean = bean;
            } else {
                bean.setSelect(false);
            }
        }
        mPriceOptionsAdapter.notifyDataSetChanged();
    }

    private void fetchData() {
        if (!NetworkUtils.isConnected(mContext)) {
            showNoNetView();
            return;
        }

        showContentView();
        XMProgress.showProgressDialog(this, mContext.getString(R.string.base_loading));
        mVipOptionVm.getVipOptions().observe(this, listXmResource -> {
            XMProgress.dismissProgressDialog(VipCenterFragment.this);
            if (listXmResource == null) {
                KLog.w(TAG, "XmResource instance is null.");
                showEmptyView();
                return;
            }

            listXmResource.handle(new VipOptionsHandleCallback<VipListBean>() {
                @Override
                public void onSuccess(VipListBean data) {
                    if (data == null) {
                        KLog.w(TAG, "VipListBean instance is null.");
                        showEmptyView();
                        return;
                    }
                    mContentLayout.setVisibility(View.VISIBLE);
                    //优享特权
                    mPrivilegeOptionsAdapter.setNewData(data.getPrivileges());

                    //vip价格选项
                    List<VipOptionsBean> optionsBeans = data.getData();
                    if (optionsBeans != null && optionsBeans.size() > 0) {
                        //只显示三个价位期限
                        if (optionsBeans.size() > 3) {
                            optionsBeans.remove(2);
                        }
                        VipOptionsBean optionsBean = optionsBeans.get(0);
                        optionsBean.setSelect(true);
                        vipOptionsBean = optionsBean;
                    }
                    mPriceOptionsAdapter.setNewData(optionsBeans);
                }

                @Override
                public void onError(int code, String message) {
                    KLog.w(TAG, "code -> " + code + "   message -> " + message);
                    showNoNetView();
                }
            });
        });

        OnlineMusicFactory.getKWLogin().fetchVipInfo();

        if (isLoginSuccess && onRefreshKWUserStatusCallback != null) {
            onRefreshKWUserStatusCallback.onRefreshKWUserStatus();
            isLoginSuccess = false;
        }
    }


    private boolean refreshVipStatus() {
        if (OnlineMusicFactory.getKWLogin().isUserLogon() && OnlineMusicFactory.getKWLogin().isCarVipUser()) {
            mOpenVipBt.setText(R.string.renewal_vip_bt);
            return true;
        } else {
            mOpenVipBt.setText(R.string.open_vip_bt);
            mVipValidityText.setVisibility(View.GONE);
        }
        return false;
    }


    private void updateVipValidityDateMessage(VipUserInfo vipUserInfo) {
        if (vipUserInfo == null) {
            KLog.w(TAG, "VipUserInfo instance is null.");
            return;
        }

        String beginDateStr = convertDateFormat(vipUserInfo.mBeginDate);
        String endDateStr = convertDateFormat(vipUserInfo.mEndDate);
        mVipValidityText.setText(getString(R.string.vip_validity_date, beginDateStr, endDateStr));
        mVipValidityText.setVisibility(View.VISIBLE);
    }


    private String convertDateFormat(long date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
        String convertDate = dateFormat.format(date);
        if (TextUtils.isEmpty(convertDate)) {
            return null;
        }
        return convertDate.replace("-", ".");
    }


}

package com.xiaoma.shop.business.ui.flow;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.xiaoma.model.XmResource;
import com.xiaoma.shop.R;
import com.xiaoma.shop.business.model.EventMsg;
import com.xiaoma.shop.business.model.FlowBean;
import com.xiaoma.shop.business.model.PayInfo;
import com.xiaoma.shop.business.ui.ShopBaseFragment;
import com.xiaoma.shop.business.ui.theme.PersonalityThemeFragment;
import com.xiaoma.shop.business.ui.theme.VoiceToneFragment;
import com.xiaoma.shop.business.vm.FlowVm;
import com.xiaoma.shop.common.callback.OnPayFromPersonalCallback;
import com.xiaoma.shop.common.constant.PaySourceType;
import com.xiaoma.ui.StateControl.StateView;
import com.xiaoma.utils.NetworkUtils;
import com.xiaoma.utils.log.KLog;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;

/**
 * Author: Ljb
 * Time  : 2019/7/19
 * Description:
 */
public class MainFlowFragment2 extends ShopBaseFragment implements View.OnClickListener , OnPayFromPersonalCallback {

    private TextView tvAvailableFlow;
    private TextView tvAllFlow;
    private TextView tvCoinPurchasing;
    private TextView tvCashPurchasing;


    private Fragment mCurrentFragment;
    private AbsFlowPurchasingFragment[] mFragments = new AbsFlowPurchasingFragment[]{
            CoinPurchasingFragment.newInstance()
            , CashPurchasingFragment.newInstance()};

    private String[] tags = new String[]{
            CoinPurchasingFragment.class.getSimpleName(),
            CashPurchasingFragment.class.getSimpleName()};

    private FlowVm flowVm;
    public static String mUnUsedFlow = "";
    public static String mTotalFlow = "";

    private int currentIndex = -1;

    public static MainFlowFragment2 newInstance() {
        return new MainFlowFragment2();
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_flow_2, container, false);
        return super.onCreateWrapView(view);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        initEvent();
        initVm();
        if (checkNet()) {
            currentIndex = 0;
            switchTab(currentIndex);
        }
        EventBus.getDefault().registerSticky(this);
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroyView() {
        EventBus.getDefault().unregister(this);
        super.onDestroyView();
    }

    @Subscriber
    public void getAction(EventMsg eventMsg) {
        if (eventMsg == null || TextUtils.isEmpty(eventMsg.getAction())) return;
        switch (eventMsg.getAction()) {
            case EventMsg.ACTION.ACTION_COIN:
                if (currentIndex != 0) {
                    currentIndex = 0;
                    switchTab(currentIndex);
                }
                break;
            case EventMsg.ACTION.ACTION_CASH:
                if (currentIndex != 1) {
                    currentIndex = 1;
                    switchTab(currentIndex);
                }
                break;
        }
    }
    private void initVm() {
        flowVm = ViewModelProviders.of(this).get(FlowVm.class);
        flowVm.getFlowMarginBean().observe(this, new Observer<XmResource<FlowBean>>() {
            @Override
            public void onChanged(@Nullable XmResource<FlowBean> flowBeanXmResource) {
                if (flowBeanXmResource == null) return;
                flowBeanXmResource.handle(new OnCallback<FlowBean>() {
                    @Override
                    public void onSuccess(FlowBean data) {
                        setFlow(data.getBalance(), data.getTotal());
                        KLog.i("filOut| "+"[onSuccess]->刷新了流量余量");
                    }

                    @Override
                    public void onError(int code, String message) {
                    }

                    @Override
                    public void onFailure(String msg) {
                    }

                });
            }
        });
    }

    /**
     * 设置流量
     *
     * @param availFlow 可用流量
     * @param totalFlow 总流量
     */
    private void setFlow(String availFlow, String totalFlow) {
        mUnUsedFlow = availFlow;
        mTotalFlow = totalFlow;
        tvAvailableFlow.setText(getString(R.string.avaliable_flow) + availFlow);
        tvAllFlow.setText(getString(R.string.total_flow) + totalFlow);
    }


    private void initEvent() {
        tvCoinPurchasing.setOnClickListener(this);
        tvCashPurchasing.setOnClickListener(this);
    }

    private void initView(View view) {
        tvAvailableFlow = view.findViewById(R.id.tv_available_flow);
        tvAllFlow = view.findViewById(R.id.tv_all_flow);
        tvCoinPurchasing = view.findViewById(R.id.tv_coin_purchasing);
        tvCashPurchasing = view.findViewById(R.id.tv_cash_purchasing);
    }

    private boolean checkNet() {
        if (!NetworkUtils.isConnected(mActivity)) {
            showNoNetView();
            return false;
        }
        return true;
    }

    @Override
    protected void noNetworkOnRetry() {
        if (checkNet()) {
            if (currentIndex == -1) {
                currentIndex = 0;
                switchTab(currentIndex);
            }else{
                if (currentIndex == 0) {
                    EventBus.getDefault().post(AbsFlowPurchasingFragment.COINPURCHASINGFRAGMENT_TAG);
                }else{
                    EventBus.getDefault().post(AbsFlowPurchasingFragment.CASHPURCHASINGFRAGMENT_TAG);
                }
            }
            showContentView();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_coin_purchasing:
                if (currentIndex != 0) {
                    currentIndex = 0;
                    switchTab(currentIndex);
                }
                break;
            case R.id.tv_cash_purchasing:
                if (currentIndex != 1) {
                    currentIndex = 1;
                    switchTab(currentIndex);
                }
                break;
        }
    }

    @Override
    protected void showNoNetView() {
        super.showNoNetView();
    }

    private void switchTab(int index) {
        switchFragment(index);
        restoreStyle();
        switch (index) {
            case 0:
                tvCoinPurchasing.setTextAppearance(R.style.text_view_light_white);
                break;
            case 1:
                tvCashPurchasing.setTextAppearance(R.style.text_view_light_white);
                break;
        }
    }

    private void switchFragment(int index) {
        FragmentManager fragmentManager = getChildFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Fragment fragmentByTag = fragmentManager.findFragmentByTag(tags[index]);
        if (fragmentByTag == null) {
            fragmentByTag = mFragments[index];
        }
        if (mCurrentFragment == null) {
            mCurrentFragment = new Fragment();
        }
        if (!fragmentByTag.isAdded()) {
            fragmentTransaction.add(R.id.fl_contain, fragmentByTag, tags[index]).hide(mCurrentFragment);
        } else {
            fragmentTransaction.show(fragmentByTag).hide(mCurrentFragment);
        }
        mCurrentFragment = fragmentByTag;
        fragmentTransaction.commit();
    }

    private void restoreStyle() {
        tvCoinPurchasing.setTextAppearance(R.style.text_view_normal);
        tvCashPurchasing.setTextAppearance(R.style.text_view_normal);
    }

    @Override
    public void handlePay(PayInfo payInfo, boolean immediateExecute) {
        //判断哪个界面的
        if (PaySourceType.TRAFFIC.equals(payInfo.getProductType())) {
            CoinPurchasingFragment coinPurchasingFragment = (CoinPurchasingFragment) mFragments[0];
            if (coinPurchasingFragment instanceof OnPayFromPersonalCallback) {
                EventBus.getDefault().postSticky(new EventMsg(EventMsg.ACTION.ACTION_COIN));
                EventBus.getDefault().post(new EventMsg(EventMsg.ACTION.ACTION_COIN));
                ((OnPayFromPersonalCallback) coinPurchasingFragment).handlePay(payInfo, false);
            }
        }else{
            CashPurchasingFragment cashPurchasingFragment = (CashPurchasingFragment) mFragments[1];
            if (cashPurchasingFragment instanceof OnPayFromPersonalCallback) {
                EventBus.getDefault().postSticky(new EventMsg(EventMsg.ACTION.ACTION_CASH));
                EventBus.getDefault().post(new EventMsg(EventMsg.ACTION.ACTION_CASH));
                ((OnPayFromPersonalCallback) cashPurchasingFragment).handlePay(payInfo, false);
            }
        }
    }
}

package com.xiaoma.shop.business.ui.flow;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.xiaoma.config.ConfigManager;
import com.xiaoma.login.LoginManager;
import com.xiaoma.model.XmResource;
import com.xiaoma.shop.R;
import com.xiaoma.shop.business.adapter.FlowShopCashAdapter;
import com.xiaoma.shop.business.model.FetchQrCodeBean;
import com.xiaoma.shop.business.model.FlowItemForCash;
import com.xiaoma.shop.business.model.PayInfo;
import com.xiaoma.shop.business.pay.PayHandler;
import com.xiaoma.shop.business.pay.PaySuccessResultCallback;
import com.xiaoma.shop.business.vm.FlowVm;
import com.xiaoma.shop.common.callback.OnPayFromPersonalCallback;
import com.xiaoma.shop.common.util.NotifyUpdateOrderInfo;
import com.xiaoma.ui.toast.XMToast;
import com.xiaoma.utils.ListUtils;
import com.xiaoma.utils.NetworkUtils;
import org.simple.eventbus.Subscriber;
import org.simple.eventbus.ThreadMode;

import java.util.List;

/**
 * Author: Ljb
 * Time  : 2019/7/19
 * Description:
 */
public class CashPurchasingFragment extends AbsFlowPurchasingFragment<FlowItemForCash, FlowShopCashAdapter> implements OnPayFromPersonalCallback {

    private FlowVm flowVm;

    public static CashPurchasingFragment newInstance() {
        return new CashPurchasingFragment();
    }

    @Override
    protected FlowShopCashAdapter createAdapter() {
        return new FlowShopCashAdapter();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initVm();
        loadData();
    }

    protected void loadData() {
        if (!NetworkUtils.isConnected(mContext)) {
            showNoNetView();
        } else {
            showContentView();
            showProgressDialog(R.string.loading);
            // 流量商城
            flowVm.fetchTrafficMallFromUnicom();
        }
    }

    private void initVm() {
        flowVm = ViewModelProviders.of(getParentFragment()).get(FlowVm.class);
        flowVm.getFlowItemForCash().observe(this, new Observer<XmResource<List<FlowItemForCash>>>() {
            @Override
            public void onChanged(@Nullable XmResource<List<FlowItemForCash>> listXmResource) {
                dismissProgress();
                if (listXmResource == null) return;
                listXmResource.handle(new OnCallback<List<FlowItemForCash>>() {
                    @Override
                    public void onSuccess(List<FlowItemForCash> data) {
                        if (!ListUtils.isEmpty(data)) {
                            mChildStateView.showContent();
                            // 默认选择第一个
                            data.get(0).setSelected(true);
                            mAdapter.setSelectedPos(0);
                            mAdapter.setNewData(data);
                            if (mPayInfo != null) {
                                handlePay(mPayInfo, true);
                                mPayInfo = null;
                            }
                        } else {
                            mChildStateView.showEmpty();
                        }
                    }

                    @Override
                    public void onFailure(String msg) {
                        mChildStateView.showEmpty();
                    }

                    @Override
                    public void onError(int code, String message) {
                        if (!NetworkUtils.isConnected(mActivity) ||  getString(R.string.network_error).equals(message)) {//没有网络
                            showNoNetView();
                            XMToast.toastException(mContext, mContext.getString(R.string
                                    .network_anomaly));
                        } else {
                            showErrorView();
                            XMToast.toastException(mContext, message);
                        }

                    }

                    @Override
                    public void onCompleted() {

                    }
                });
            }
        });
    }


    @Override
    protected void onClickBuy() {
        int selectedPos = mAdapter.getSelectedPos();
        if (selectedPos >= 0 && selectedPos < mAdapter.getData().size()) {
            FlowItemForCash flowItemForCash = mAdapter.getData().get(selectedPos);
            if (flowItemForCash == null) return;
            tryBuy(flowItemForCash);
        } else {
            XMToast.showToast(mContext, R.string.please_select_flow);
        }
    }

    /**
     * 购买
     */
    private void tryBuy(final FlowItemForCash flowItemForCash) {
//        FlowTrackInfo flowTrackInfo = productBean.toTrackInfo();
//        flowTrackInfo.setUnUsedFlow(MainFlowFragment2.mUnUsedFlow);
//        flowTrackInfo.setTotalFlow(MainFlowFragment2.mUnUsedFlow);
//        ShopTrackManager.newSingleton().setBaseInfo(ResourceType.FLOW, flowTrackInfo.toString(), MainFlowFragment.class.getName());
//        XmAutoTracker.getInstance().onEvent(EventConstant.NormalClick.ACTION_BUY, flowTrackInfo.toString(), this.getClass().getName(), EventConstant.PageDesc.FRAGMENT_GROW_STORE);
        FetchQrCodeBean fetchQrCodeBean = buildFetchCodeBean(flowItemForCash);
        PayHandler.getInstance().scanCodePayWindow(mActivity,
                fetchQrCodeBean,
                new PaySuccessResultCallback() {
                    @Override
                    public void confirm() {
                        refreshFlow();
                    }

                    @Override
                    public void cancel() {

                    }
                });
    }

    @NonNull
    private FetchQrCodeBean buildFetchCodeBean(FlowItemForCash flowItemForCash) {
        float price = flowItemForCash.getMarketPrice();// 购买需要的现金
        final String productId = flowItemForCash.getCommoNo();
        FetchQrCodeBean fetchQrCodeBean = new FetchQrCodeBean();
        //"951642686056837120"
        fetchQrCodeBean.setUid(LoginManager.getInstance().getLoginUserId());
        fetchQrCodeBean.setChannelId(ConfigManager.ApkConfig.getChannelID());

        FetchQrCodeBean.OrderItemsBean orderItemsBean = new FetchQrCodeBean.OrderItemsBean();
        orderItemsBean.setCommoNo(productId);
        orderItemsBean.setCount(1);
        orderItemsBean.setPrice(String.valueOf(price));

        fetchQrCodeBean.getOrderItems().add(orderItemsBean);
        return fetchQrCodeBean;
    }

    @Override
    protected void onOwnItemClick(BaseQuickAdapter adapter, View view, int position) {
        mAdapter.selected(position);
    }


    //刷新流量获取
    protected void refreshFlow() {
        if (NetworkUtils.isConnected(mActivity)) {
            doGetFlowMargin();
            showBuySuccess();
        } else {
            showErrorToast();
        }
    }

    /**
     * 获取流量余量
     */
    private void doGetFlowMargin() {
        String vin = ConfigManager.DeviceConfig.getVIN(mContext);
        flowVm.fetchFlowMarginBean(vin);
    }

    @Subscriber(mode = ThreadMode.MAIN)
    public void getMessage(String msg) {
        if (CASHPURCHASINGFRAGMENT_TAG.equals(msg)) {
            loadData();
        }
    }

    @Override
    public void handlePay(PayInfo payInfo, boolean immediateExecute) {
        if (payInfo == null) return;
        mPayInfo = payInfo;
        if (immediateExecute || (mAdapter != null && !ListUtils.isEmpty(mAdapter.getData()))) {
            int pos = mAdapter.searchItemByProductPrice(payInfo.getPrice());
            if (pos != -1) {
                mAdapter.selected(pos);
            }
            if (payInfo.isCoinPay()) {
                carCoinPay(payInfo);
            } else {
                cashPay(payInfo);
            }
        }
    }


    @Override
    protected PaySuccessResultCallback getPaySuccessCallback(long productId) {
        if (mAdapter == null || ListUtils.isEmpty(mAdapter.getData())) return retInitCallback(productId);
        return new PaySuccessResultCallback() {
            @Override
            public void confirm() {
                NotifyUpdateOrderInfo.sendNotifyMsg();
                refreshFlow();
            }

            @Override
            public void cancel() {

            }
        };
    }
}

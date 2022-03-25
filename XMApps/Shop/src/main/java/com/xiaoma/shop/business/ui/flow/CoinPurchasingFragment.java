package com.xiaoma.shop.business.ui.flow;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.xiaoma.autotracker.XmAutoTracker;
import com.xiaoma.config.ConfigManager;
import com.xiaoma.model.XmResource;
import com.xiaoma.shop.R;
import com.xiaoma.shop.business.adapter.FlowShopCoinAdapter;
import com.xiaoma.shop.business.model.FlowBean;
import com.xiaoma.shop.business.model.PayInfo;
import com.xiaoma.shop.business.model.ScoreProductBean.ProductInfoBean.ChildProductBean;
import com.xiaoma.shop.business.pay.PayHandler;
import com.xiaoma.shop.business.pay.PaySuccessResultCallback;
import com.xiaoma.shop.business.vm.FlowVm;
import com.xiaoma.shop.common.callback.OnPayFromPersonalCallback;
import com.xiaoma.shop.common.constant.ResourceType;
import com.xiaoma.shop.common.track.EventConstant;
import com.xiaoma.shop.common.track.ShopTrackManager;
import com.xiaoma.shop.common.track.model.FlowTrackInfo;
import com.xiaoma.shop.common.util.NotifyUpdateOrderInfo;
import com.xiaoma.shop.common.util.TaskPool;
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
public class CoinPurchasingFragment extends AbsFlowPurchasingFragment<ChildProductBean, FlowShopCoinAdapter> implements OnPayFromPersonalCallback {

    private FlowVm flowVm;
    private TaskPool mTaskPool = new TaskPool();


    public static CoinPurchasingFragment newInstance() {
        return new CoinPurchasingFragment();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initVm();
        loadData();
    }

    @Override
    protected void onOwnItemClick(BaseQuickAdapter adapter, View view, int position) {
        mAdapter.selected(position);
    }


    @Override
    protected FlowShopCoinAdapter createAdapter() {
        return new FlowShopCoinAdapter();
    }

    protected void loadData() {
        if (!NetworkUtils.isConnected(mContext)) {
            showNoNetView();
        } else {
            showContentView();
            showProgressDialog(R.string.loading);
            // 获取流量余量
            doGetFlowMargin();
            // 流量商城
            doGetTrafficMall();
        }
    }

    private void initVm() {
        flowVm = ViewModelProviders.of(getParentFragment()).get(FlowVm.class);
        flowVm.getTrafficMall().observe(this, new Observer<XmResource<List<ChildProductBean>>>() {
            @Override
            public void onChanged(@Nullable XmResource<List<ChildProductBean>> listXmResource) {
                if (listXmResource == null) return;
                listXmResource.handle(new OnCallback<List<ChildProductBean>>() {
                    @Override
                    public void onSuccess(List<ChildProductBean> data) {
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
                        dismissProgress();
                        if (!NetworkUtils.isConnected(mActivity) || getString(R.string.network_error).equals(message)) {//没有网络
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
                        doTaskFinish();
                    }
                });
            }
        });

        // 流量余量
        flowVm.getFlowMarginBean().observe(this, new Observer<XmResource<FlowBean>>() {
            @Override
            public void onChanged(@Nullable XmResource<FlowBean> flowBeanXmResource) {
                if (flowBeanXmResource == null) return;
                flowBeanXmResource.handle(new OnCallback<FlowBean>() {
                    @Override
                    public void onSuccess(FlowBean data) {

                    }

                    @Override
                    public void onError(int code, String message) {
                    }

                    @Override
                    public void onFailure(String msg) {
                    }

                    @Override
                    public void onCompleted() {
                        doTaskFinish();
                    }
                });
            }
        });


    }

    /**
     * 获取流量余量
     */
    private void doGetFlowMargin() {
        mTaskPool.addTask(new TaskPool.TaskMode());
        String vin = ConfigManager.DeviceConfig.getVIN(mContext);
        flowVm.fetchFlowMarginBean(vin);
    }

    /**
     * 流量商场
     */
    private void doGetTrafficMall() {
        mTaskPool.addTask(new TaskPool.TaskMode());
        flowVm.fetchTrafficMall();
    }

    private void doTaskFinish() {
        mTaskPool.finish(new TaskPool.TaskFinishListener() {
            @Override
            public void onComplete() {
                dismissProgress();
            }
        });
    }


    @Override
    protected void onClickBuy() {
        int selectedPos = mAdapter.getSelectedPos();
        if (selectedPos >= 0 && selectedPos < mAdapter.getData().size()) {
            ChildProductBean productBean = mAdapter.getData().get(selectedPos);
            if (productBean == null) return;
            tryBuy(productBean);
        } else {
            XMToast.showToast(mContext, R.string.please_select_flow);
        }
    }

    /**
     * 购买
     *
     * @param productBean
     */
    private void tryBuy(final ChildProductBean productBean) {
        FlowTrackInfo flowTrackInfo = productBean.toTrackInfo();
        flowTrackInfo.setUnUsedFlow(MainFlowFragment2.mUnUsedFlow);
        flowTrackInfo.setTotalFlow(MainFlowFragment2.mUnUsedFlow);
        ShopTrackManager.newSingleton().setBaseInfo(ResourceType.FLOW, flowTrackInfo.toString(), MainFlowFragment2.class.getName());
        XmAutoTracker.getInstance().onEvent(EventConstant.NormalClick.ACTION_BUY, flowTrackInfo.toString(), this.getClass().getName(), EventConstant.PageDesc.FRAGMENT_GROW_STORE);

        final String productName = productBean.getName();// 产品Name
        int cardCoin = productBean.getNeedScore();// 购买需要的车币
        final int productId = productBean.getId();
        PayHandler
                .getInstance()
                .carCoinPayWindow(
                        mActivity,
                        ResourceType.FLOW,
                        productId,
                        productName,
                        cardCoin,
                        false,
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

    //刷新流量获取
    protected void refreshFlow() {
        if (NetworkUtils.isConnected(mActivity)) {
            doGetFlowMargin();
            showBuySuccess();

        } else {
            showErrorToast();
        }
    }

    @Subscriber(mode = ThreadMode.MAIN)
    public void getMessage(String msg) {
        if (COINPURCHASINGFRAGMENT_TAG.equals(msg)) {
            loadData();
        }
    }

    @Override
    public void handlePay(PayInfo payInfo, boolean immediateExecute) {
        if (payInfo == null) return;
        mPayInfo = payInfo;
        if (immediateExecute || (mAdapter != null && !ListUtils.isEmpty(mAdapter.getData()))) {
            int pos = mAdapter.searchItemByProductId(payInfo.getProductId());
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

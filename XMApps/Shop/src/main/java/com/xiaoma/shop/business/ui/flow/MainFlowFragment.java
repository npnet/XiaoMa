//package com.xiaoma.shop.business.ui.flow;
//
//import android.arch.lifecycle.Observer;
//import android.arch.lifecycle.ViewModelProviders;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.graphics.Color;
//import android.graphics.drawable.BitmapDrawable;
//import android.graphics.drawable.Drawable;
//import android.os.Bundle;
//import android.support.annotation.NonNull;
//import android.support.annotation.Nullable;
//import android.support.v7.widget.GridLayoutManager;
//import android.support.v7.widget.RecyclerView;
//import android.text.Spannable;
//import android.text.SpannableString;
//import android.text.TextUtils;
//import android.text.style.AbsoluteSizeSpan;
//import android.text.style.ForegroundColorSpan;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.TextView;
//
//import com.chad.library.adapter.base.BaseQuickAdapter;
//import com.xiaoma.autotracker.XmAutoTracker;
//import com.xiaoma.autotracker.XmTracker;
//import com.xiaoma.autotracker.model.TrackerCountType;
//import com.xiaoma.config.ConfigManager;
//import com.xiaoma.login.LoginManager;
//import com.xiaoma.model.XmResource;
//import com.xiaoma.model.annotation.NormalOnClick;
//import com.xiaoma.model.annotation.PageDescComponent;
//import com.xiaoma.model.annotation.ResId;
//import com.xiaoma.shop.R;
//import com.xiaoma.shop.business.adapter.FlowShopCoinAdapter;
//import com.xiaoma.shop.business.model.FlowBean;
//import com.xiaoma.shop.business.model.ScoreProductBean;
//import com.xiaoma.shop.business.model.ScoreProductBean.ProductInfoBean.ChildProductBean;
//import com.xiaoma.shop.business.pay.PayHandler;
//import com.xiaoma.shop.business.pay.PaySuccessResultCallback;
//import com.xiaoma.shop.business.ui.ShopBaseFragment;
//import com.xiaoma.shop.business.vm.FlowVm;
//import com.xiaoma.shop.common.callback.DialogCommonCallbackImpl;
//import com.xiaoma.shop.common.constant.ResourceType;
//import com.xiaoma.shop.common.constant.ShopContract;
//import com.xiaoma.shop.common.track.EventConstant;
//import com.xiaoma.shop.common.track.ShopTrackManager;
//import com.xiaoma.shop.common.track.model.FlowTrackInfo;
//import com.xiaoma.shop.common.util.TaskPool;
//import com.xiaoma.shop.common.util.UnitConverUtils;
//import com.xiaoma.ui.StateControl.StateView;
//import com.xiaoma.ui.toast.XMToast;
//import com.xiaoma.utils.ListUtils;
//import com.xiaoma.utils.NetworkUtils;
//import com.xiaoma.utils.ResUtils;
//
//import java.util.ArrayList;
//import java.util.List;
//
///**
// * <pre>
// *     author : wutao
// *     time   : 2019/01/24
// *     desc   :
// * </pre>
// */
//@PageDescComponent(EventConstant.PageDesc.FRAGMENT_GROW_STORE)
//public class MainFlowFragment extends ShopBaseFragment implements View.OnClickListener {
//
//    /**********************UI******************************/
//    private TextView mTvAvailableFlow;
//    private TextView mTvAllFlow;
//    private RecyclerView mRecyclerView;
//    private TextView mTvBuy;
//    private FlowShopCoinAdapter mAdapter;
//    private StateView mChildStateView;
//    /**********************Data******************************/
//    private int SPAN_COUNT = 4;//
//    private TaskPool mTaskPool = new TaskPool();
//    private FlowVm mFlowVm;
//    private String vin = "";
//    private ArrayList<ChildProductBean> mFlowItemBeans;
//    private String mUnUsedFlow, mTotalFlow;
//
//    public static MainFlowFragment newInstance() {
//        return new MainFlowFragment();
//    }
//
//
//    @Nullable
//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
//                             @Nullable Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.fragment_main_flow, container, false);
//        return super.onCreateWrapView(view);
//    }
//
//    @Override
//    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//        mFlowItemBeans = new ArrayList<>();
//        initView(view);
//        initEvent();
//        initData();
//        setStateViewTips();
//    }
//
//    private void setStateViewTips() {
//        try {
//            View noNetworkView = mStateView.getNoNetworkView();
//            TextView tv = noNetworkView.findViewById(R.id.tv_tips);
//            tv.setText(R.string.network_does_not_force);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    private void initView(View view) {
//        mChildStateView = view.findViewById(R.id.state_view_flow);
//        mTvAvailableFlow = view.findViewById(R.id.tv_available_flow);
//        mTvAllFlow = view.findViewById(R.id.tv_all_flow);
//        mRecyclerView = view.findViewById(R.id.recyclerView);
//        mTvBuy = view.findViewById(R.id.tv_buy);
//        GridLayoutManager layoutManager = new GridLayoutManager(mActivity, SPAN_COUNT);
//        mRecyclerView.setLayoutManager(layoutManager);
//        ScoreProductBean bean = new ScoreProductBean();
//        mAdapter = new FlowShopCoinAdapter();
//        mRecyclerView.setAdapter(mAdapter);
//        String str = ResUtils.getString(mActivity, R.string.buy_valid_for_the_month);
//        SpannableString string = new SpannableString(str);
//        AbsoluteSizeSpan sizeSpan = new AbsoluteSizeSpan(24);
//        ForegroundColorSpan colorSpan = new ForegroundColorSpan(Color.parseColor("#8a919d"));
//        string.setSpan(sizeSpan, 2, string.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
//        string.setSpan(colorSpan, 2, str.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
//        mTvBuy.setText(string);
//        mFlowVm = ViewModelProviders.of(this).get(FlowVm.class);
//    }
//
//    private void initEvent() {
//        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
//            @Override
//            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
//                mAdapter.selected(position);
//            }
//        });
//        mTvBuy.setOnClickListener(this);
//        mTvAvailableFlow.setOnClickListener(this);
//        mTvAllFlow.setOnClickListener(this);
//        // 流量商城
//        mFlowVm.getTrafficMall().observe(this, new Observer<XmResource<List<ChildProductBean>>>() {
//            @Override
//            public void onChanged(@Nullable XmResource<List<ChildProductBean>> listXmResource) {
//                if (listXmResource == null) return;
//                listXmResource.handle(new OnCallback<List<ChildProductBean>>() {
//                    @Override
//                    public void onSuccess(List<ChildProductBean> data) {
//                        if (!ListUtils.isEmpty(data)) {
//                            mChildStateView.showContent();
//                            // 默认选择第一个
//                            data.get(0).setSelected(true);
//                            mAdapter.setSelectedPos(0);
//                            mAdapter.setNewData(data);
//                        } else {
//                            mChildStateView.showEmpty();
//                        }
//                    }
//
//                    @Override
//                    public void onFailure(String msg) {
//                        //                        super.onFailure(msg);
//                        mChildStateView.showEmpty();
//                    }
//
//                    @Override
//                    public void onError(int code, String message) {
//                        dismissProgress();
//                        if (!NetworkUtils.isConnected(mActivity)) {//没有网络
//                            showNoNetView();
//                            XMToast.toastException(mContext, mContext.getString(R.string
//                                    .network_anomaly));
//                        } else {
//                            showErrorView();
//                            XMToast.toastException(mContext, message);
//                        }
//
//                    }
//
//                    @Override
//                    public void onCompleted() {
//                        doTaskFinish();
//                    }
//                });
//            }
//        });
//        // 流量余量
//        mFlowVm.getFlowMarginBean().observe(this, new Observer<XmResource<FlowBean>>() {
//            @Override
//            public void onChanged(@Nullable XmResource<FlowBean> flowBeanXmResource) {
//                if (flowBeanXmResource == null) return;
//                flowBeanXmResource.handle(new OnCallback<FlowBean>() {
//                    @Override
//                    public void onSuccess(FlowBean data) {
//                        setFlow(data.getBalance(), data.getTotal());
//                    }
//
//                    @Override
//                    public void onError(int code, String message) {
//
//                    }
//
//                    @Override
//                    public void onCompleted() {
//                        doTaskFinish();
//                    }
//                });
//            }
//        });
//
//        mFlowVm.getScoreProductBean().observe(this, new Observer<ScoreProductBean>() {
//            @Override
//            public void onChanged(@Nullable ScoreProductBean bean) {
//                mAdapter.setScoreProductBean(bean);
//            }
//        });
//        mFlowVm.getFlowByCarCoin().observe(this, new Observer<XmResource<Object>>() {
//            @Override
//            public void onChanged(@Nullable XmResource<Object> objectXmResource) {
//                if (objectXmResource == null) return;
//                objectXmResource.handle(new OnCallback<Object>() {
//                    @Override
//                    public void onSuccess(Object data) {
//                        if (data != null && data instanceof String) {
//                            XMToast.showToast(mActivity, ResUtils.getString(mActivity, R.string.purchase_success));
//                        }
//                    }
//
//                    @Override
//                    public void onError(int code, String message) {
//
//
//                    }
//                });
//            }
//        });
//    }
//
//    private void initData() {
//        loadData();
//    }
//
//    /**
//     * 设置流量
//     *
//     * @param availFlow 可用流量
//     * @param totalFlow 总流量
//     */
//    private void setFlow(String availFlow, String totalFlow) {
//        mUnUsedFlow = availFlow;
//        mTotalFlow = totalFlow;
//        mTvAvailableFlow.setText(getString(R.string.avaliable_flow) + availFlow);
//        mTvAllFlow.setText(getString(R.string.total_flow) + totalFlow);
//    }
//
//    @Override
//    @NormalOnClick({EventConstant.NormalClick.ACTION_BUY})
//    @ResId({R.id.tv_buy})
//    public void onClick(View v) {
//        switch (v.getId()) {
//            case R.id.tv_buy:
//                int selectedPos = mAdapter.getSelectedPos();
//                if (selectedPos >= 0 && selectedPos < mAdapter.getData().size()) {
//                    ChildProductBean productBean = mAdapter.getData().get(selectedPos);
//                    if (productBean == null) return;
//                    tryBuy(productBean);
//                } else {
//                    XMToast.showToast(mContext, R.string.please_select_flow);
//                }
//                break;
//        }
//    }
//
//    /**
//     * 购买
//     *
//     * @param productBean
//     */
//    private void tryBuy(final ChildProductBean productBean) {
//        FlowTrackInfo flowTrackInfo = productBean.toTrackInfo();
//        flowTrackInfo.setUnUsedFlow(mUnUsedFlow);
//        flowTrackInfo.setTotalFlow(mTotalFlow);
//        ShopTrackManager.newSingleton().setBaseInfo(ResourceType.FLOW, flowTrackInfo.toString(), MainFlowFragment.class.getName());
//        XmAutoTracker.getInstance().onEvent(EventConstant.NormalClick.ACTION_BUY, flowTrackInfo.toString(), this.getClass().getName(), EventConstant.PageDesc.FRAGMENT_GROW_STORE);
//
//        final String productName = productBean.getName();// 产品Name
//        String scorePrice = productBean.getDiscountScorePrice();// 购买需要的车币
//        int cardCoin = (int) UnitConverUtils.exchange(scorePrice);
//        final int productId = productBean.getId();
//        final String price = productBean.getDiscountPrice();
//        ScoreProductBean scoreProductBean = mAdapter.getScoreProductBean();
//        String pid = "";
//        if (!ListUtils.isEmpty(scoreProductBean.getProductInfo())) {
//            pid = scoreProductBean.getProductInfo().get(0).getId() + "";
//        }
//
//        switch (productBean.getPayType()) {
//            case ShopContract.Pay.DEFAULT://免费
//            case ShopContract.Pay.COIN: // 仅限车币购买
//                PayHandler.getInstance().carCoinPayWindow(mActivity, ResourceType.FLOW, productId,
//                        productName, cardCoin, new PaySuccessResultCallback() {
//                            @Override
//                            public void confirm() {
//                                refreshFlow();
//                            }
//
//                            @Override
//                            public void cancel() {
//                            }
//                        });
//                break;
//            case ShopContract.Pay.COIN_AND_RMB: //车币和现金购买
//                PayHandler.getInstance().scanCodePayWindow(mActivity,
//                        ResourceType.FLOW,
//                        productId,
//                        productName,
//                        price,
//                        cardCoin,
//                        false,
//                        new PaySuccessResultCallback() {
//                            @Override
//                            public void confirm() {
//                                refreshFlow();
//                            }
//
//                            @Override
//                            public void cancel() {
//
//                            }
//                        });
//                break;
//            case ShopContract.Pay.RMB:// 现金购买
//                PayHandler.getInstance().scanCodePayWindow(mActivity,
//                        ResourceType.FLOW,
//                        productId,
//                        productName,
//                        price,
//                        cardCoin,
//                        true,
//                        new PaySuccessResultCallback() {
//                            @Override
//                            public void confirm() {
//                                refreshFlow();
//                            }
//
//                            @Override
//                            public void cancel() {
//
//                            }
//                        });
//                break;
//        }
//    }
//
//    //刷新流量获取
//    private void refreshFlow() {
//        if (NetworkUtils.isConnected(mActivity)) {
//            if (getUid() > 0) {
//                doGetFlowMargin(getUid());
//                showBuySuccess();
//            }
//        } else {
//            showErrorToast();
//        }
//    }
//
//    /**
//     * 购买成功
//     */
//    private void showBuySuccess() {
//        PayHandler.getInstance().showCommonDialog(mActivity, new DialogCommonCallbackImpl() {
//
//            @Override
//            public void prepare(View contentView) {
//                TextView coinText = contentView.findViewById(R.id.tv_pay_message);
//                TextView confirmBt = contentView.findViewById(R.id.confirm_bt);
//                TextView cancelBt = contentView.findViewById(R.id.cancel_bt);
//                View divider = contentView.findViewById(R.id.divider_line_vertical);
//                coinText.setText(R.string.purchase_success);
//                confirmBt.setText(R.string.confirm);
//                cancelBt.setVisibility(View.GONE);
//                divider.setVisibility(View.GONE);
//                XmTracker.getInstance().uploadEvent(-1, TrackerCountType.BUYFLOW.getType());
//            }
//
//            @Override
//            public void onConfirm() {
//
//            }
//        });
//    }
//
//    //网络异常Toast
//    private void showErrorToast() {
//        Bitmap errorBit = BitmapFactory.decodeResource(getResources(), R.drawable.icon_error);
//        Drawable drawable = new BitmapDrawable(errorBit);
//        XMToast.showToast(mContext, getString(R.string.network_anomaly), drawable);
//    }
//
//    @Override
//    protected void noNetworkOnRetry() {
//        loadData();
//    }
//
//    @Override
//    protected void errorOnRetry() {
//        loadData();
//    }
//
//    /**
//     * 加载数据
//     */
//    private void loadData() {
//        if (!NetworkUtils.isConnected(mContext)) {
//            showNoNetView();
//        } else {
//            showContentView();
//            if (getUid() < 0) {
//                mChildStateView.showEmpty();
//                return;
//            }
//            showProgressDialog(R.string.base_loading);
//            // 获取流量余量
//            doGetFlowMargin(getUid());
//            // 流量商城
//            doGetTrafficMall(getUid());
//        }
//    }
//
//    /**
//     * 获取流量余量
//     */
//    private void doGetFlowMargin(long uid) {
//        mTaskPool.addTask(new TaskPool.TaskMode());
//        vin = ConfigManager.DeviceConfig.getVIN(mContext);
//        mFlowVm.fetchFlowMarginBean(vin, uid);
//    }
//
//    private long getUid() {
//        String userId = LoginManager.getInstance().getLoginUserId();
//        if (TextUtils.isEmpty(userId)) {
//            XMToast.showToast(mActivity, R.string.user_does_not_exist);
//            return -1;
//        }
//        long uid = Long.parseLong(userId);
//        return uid;
//    }
//
//    /**
//     * 流量商场
//     */
//    private void doGetTrafficMall(long uid) {
//        mTaskPool.addTask(new TaskPool.TaskMode());
//        mFlowVm.fetchTrafficMall(uid);
//    }
//
//    private void doTaskFinish() {
//        mTaskPool.finish(new TaskPool.TaskFinishListener() {
//            @Override
//            public void onComplete() {
//                dismissProgress();
//            }
//        });
//    }
//
//
//}

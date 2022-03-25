package com.xiaoma.personal.order.ui.fragment;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.mapbar.xiaoma.manager.XmMapNaviManager;
import com.xiaoma.autotracker.XmAutoTracker;
import com.xiaoma.component.base.BaseFragment;
import com.xiaoma.login.LoginManager;
import com.xiaoma.model.ResultCallback;
import com.xiaoma.model.XMResult;
import com.xiaoma.model.XmResource;
import com.xiaoma.personal.R;
import com.xiaoma.personal.common.OnlyCode;
import com.xiaoma.personal.common.RequestManager;
import com.xiaoma.personal.common.util.EventConstants;
import com.xiaoma.personal.order.adapter.MineOrderAdapter;
import com.xiaoma.personal.order.constants.CallPhoneUtils;
import com.xiaoma.personal.order.constants.LoadStatus;
import com.xiaoma.personal.order.constants.OrderStatusId;
import com.xiaoma.personal.order.constants.OrderTypeMeta;
import com.xiaoma.personal.order.constants.ShopOrderConstants;
import com.xiaoma.personal.order.model.OrderInfo;
import com.xiaoma.personal.order.ui.detail.OrderDetailActivity;
import com.xiaoma.personal.order.vm.OrderVM;
import com.xiaoma.ui.dialog.ConfirmDialog;
import com.xiaoma.ui.progress.loading.XMProgress;
import com.xiaoma.ui.toast.XMToast;
import com.xiaoma.ui.view.XmScrollBar;
import com.xiaoma.utils.NetworkUtils;
import com.xiaoma.utils.log.KLog;

import java.util.List;
import java.util.Objects;

/**
 * <pre>
 *       @author Created by Gillben
 *       date: 2019/3/12 0012 10:22
 *       desc：订单基类Fragment
 * </pre>
 */

abstract class BaseOrderFragment extends BaseFragment {

    protected static final int PAY_SHOP_ORDER_REQUEST_CODE = 101;
    protected static final int PAY_SHOP_ORDER_RESULT_CODE = 102;
    private RecyclerView mRecyclerView;
    private XmScrollBar xmScrollBar;
    private MineOrderAdapter mineOrderAdapter;
    private boolean isFirst;
    private boolean isVisible;
    private OrderVM mOrderVM;

    private int pageNum = 1;
    private int pageSize = 10;


    protected final MineOrderAdapter getMineOrderAdapter() {
        return mineOrderAdapter;
    }

    protected abstract int getOrderType();

    protected abstract String getOrderTypeName();

    /**
     * 开启是否加载更多
     * 默认开启
     */
    protected boolean enableLoadMore() {
        return true;
    }

    protected void attachData(List<OrderInfo.Order> orders) {
        getMineOrderAdapter().setNewData(orders);
    }

    protected void loadMoreData(List<OrderInfo.Order> orders) {
        getMineOrderAdapter().addData(orders);
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_base_order, container, false);
        return super.onCreateWrapView(view);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        initListener();
        initLoadMore();
        mOrderVM = ViewModelProviders.of(this).get(OrderVM.class);

        pageNum = 1;
        isFirst = true;
        initData();
    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            isVisible = true;
            initData();
        } else {
            isVisible = false;
        }
    }

    private void initView(View view) {
        xmScrollBar = view.findViewById(R.id.order_scroll_bar);
        mRecyclerView = view.findViewById(R.id.rv_order_list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                outRect.set(0, 0, 0, 30);
            }
        });
        mineOrderAdapter = new MineOrderAdapter();
        mRecyclerView.setAdapter(mineOrderAdapter);
        xmScrollBar.setRecyclerView(mRecyclerView);
    }


    private void initListener() {
        mineOrderAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                BaseOrderFragment.this.onItemChildClick(adapter, view, position);
            }
        });
    }


    private void initData() {
        if (isFirst && isVisible) {
            fetchData();
            isFirst = false;
            isVisible = false;
        }
    }


    private void initLoadMore() {
        mineOrderAdapter.setEnableLoadMore(enableLoadMore());
        mineOrderAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                if (mRecyclerView.isNestedScrollingEnabled()) {
                    mRecyclerView.setNestedScrollingEnabled(false);
                }
                fetchData();
            }
        }, mRecyclerView);
    }


    @Override
    protected void noNetworkOnRetry() {
        super.noNetworkOnRetry();
        fetchData();
    }

    @Override
    protected void errorOnRetry() {
        super.errorOnRetry();
        fetchData();
    }

    protected void fetchData() {
        if (!NetworkUtils.isConnected(mContext)) {
//            dismissProgress();
            XMProgress.dismissProgressDialog(this);
            showNoNetView();
            return;
        }

        String userId = LoginManager.getInstance().getLoginUserId();
        if (TextUtils.isEmpty(userId)) {
            KLog.w("UserId is null.");
            showEmptyView();
            return;
        }

//        showLoadingView();
        XMProgress.showProgressDialog(this, mContext.getString(R.string.hard_to_loading));
        mOrderVM.getOrderInfoList(Long.parseLong(userId), getOrderType(), pageNum, pageSize)
                .observe(this, new Observer<XmResource<OrderInfo>>() {
                    @Override
                    public void onChanged(@Nullable XmResource<OrderInfo> listXmResource) {
                        assert listXmResource != null;
                        listXmResource.handle(new OnCallback<OrderInfo>() {
                            @Override
                            public void onSuccess(OrderInfo data) {
                                XMProgress.dismissProgressDialog(BaseOrderFragment.this);
                                //无数据
                                if (data.getOrders().size() == 0) {
                                    showEmptyView();
                                    return;
                                }
                                showContentView();
                                int totalPage = data.getPageInfo().getTotalPage();
                                if (pageNum <= totalPage) {
                                    if (pageNum == 1) {
                                        attachData(data.getOrders());
                                    } else {
                                        loadMoreData(data.getOrders());
                                        updateLoadStatus(LoadStatus.Complete);
                                    }
                                    pageNum++;

                                    if (pageNum > totalPage) {
                                        updateLoadStatus(LoadStatus.End);
                                    }
                                }
                            }

                            @Override
                            public void onFailure(String msg) {
                                super.onFailure(msg);
                                showNoNetView();
                                XMProgress.dismissProgressDialog(BaseOrderFragment.this);
//                                showErrorView();
                            }
                        });
                    }
                });
    }


    @Override
    public void onStop() {
        super.onStop();
        XMProgress.dismissProgressDialog(BaseOrderFragment.this);
    }

    /**
     * 刷新加载状态
     */
    protected final void updateLoadStatus(@LoadStatus int status) {
        switch (status) {
            case LoadStatus.Complete:
                mineOrderAdapter.loadMoreComplete();
                break;

            case LoadStatus.End:
                mineOrderAdapter.loadMoreEnd(true);
                break;

            case LoadStatus.Error:
                mineOrderAdapter.loadMoreFail();
                break;
        }
    }

    protected void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
        OrderInfo.Order order = getMineOrderAdapter().getData().get(position);
        String event = "";
        switch (view.getId()) {
            //删除
            case R.id.delete_text:
                deleteOrder(order);
                break;

            //内容
            case R.id.item_order_content_layout:
                navigationDetailPage(order);
                break;

            //立即支付
            case R.id.bt_pay_order:
//                XMToast.showToast(mContext,R.string.pay_immediately);
                navigationDetailPage(order);
                event = getString(R.string.pay_immediately);
                break;

            //拨打电话
            case R.id.bt_call_phone:
                callPhone(order);
                event = getString(R.string.call_phone);
                break;

            //导航前往
            case R.id.bt_navigation_go:
                navigationLocation(order);
                event = getString(R.string.navigation_go);
                break;
        }
        if (event != null && !event.isEmpty()) {
            XmAutoTracker.getInstance().onBusinessInfoEvent(event, getOrderTypeName(),
                    String.valueOf(order.getId()), this.getClass().getSimpleName(),
                    EventConstants.PageDescribe.orderFragment);
        }
    }

    private void callPhone(OrderInfo.Order order) {
        String orderType = order.getType();
        String phone = null;
        if (OrderTypeMeta.HOTEL.equals(orderType)) {
            OrderInfo.Order.Hotel hotel = order.getHotelJsonVo();
            phone = hotel.getMobile();
        } else if (OrderTypeMeta.FILM.equals(orderType)) {
            OrderInfo.Order.Film film = order.getCinemaJsonVo();
            phone = film.getMobile();
        }

        CallPhoneUtils.callBluetoothPhone(getActivity(), phone, response -> KLog.w("电话拨打失败"));
    }

    private void navigationLocation(OrderInfo.Order order) {
        if (order == null) {
            return;
        }

        String name;
        String address;
        double lat;
        double lon;

        String orderType = order.getType();
        if (OrderTypeMeta.HOTEL.equals(orderType)) {
            OrderInfo.Order.Hotel hotel = order.getHotelJsonVo();
            name = hotel.getHotelName();
            address = hotel.getAddress();
            lat = Double.parseDouble(hotel.getLat());
            lon = Double.parseDouble(hotel.getLon());
        } else if (OrderTypeMeta.FILM.equals(orderType)) {
            OrderInfo.Order.Film film = order.getCinemaJsonVo();
            name = film.getCinemaName();
            address = film.getAddress();
            lat = Double.parseDouble(film.getLat());
            lon = Double.parseDouble(film.getLon());
        } else {
            return;
        }

        XmMapNaviManager.getInstance().startNaviToPoi(name, address, lon, lat);
    }

    /**
     * 删除订单
     */
    private void deleteOrder(final OrderInfo.Order order) {
        final ConfirmDialog dialog = new ConfirmDialog(Objects.requireNonNull(getActivity()));
        dialog.setContent(mContext.getString(R.string.query_delete_order))
                .setPositiveButton(mContext.getString(R.string.confirm), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        RequestManager.deleteMineOrder(order.getId(), order.getOrderStatusId(), new ResultCallback<XMResult<OnlyCode>>() {
                            @Override
                            public void onSuccess(XMResult<OnlyCode> result) {
                                mineOrderAdapter.getData().remove(order);
                                mineOrderAdapter.notifyDataSetChanged();
                                if (mineOrderAdapter.getData().size() == 0) {
                                    showEmptyView();
                                }
                            }

                            @Override
                            public void onFailure(int code, String msg) {
                                XMToast.showToast(mContext, R.string.fail_to_delete);
                            }
                        });
                    }
                })
                .show();

//        XmDialog xmDialog = new XmDialog.Builder(Objects.requireNonNull(getActivity()))
//                .setLayoutRes(R.layout.delete_order_confirm)
//                .setWidth(500)
//                .setHeight(300)
//                .setCancelableOutside(false)
//                .addOnClickListener(R.id.cancel_delete_order, R.id.confirm_delete_order)
//                .setOnViewClickListener(new OnViewClickListener() {
//                    @Override
//                    public void onViewClick(View view, XmDialog tDialog) {
//                        tDialog.dismiss();
//                        switch (view.getId()) {
//                            case R.id.confirm_delete_order:
//                                RequestManager.deleteMineOrder(order.getId(), order.getOrderStatusId(), new ResultCallback<XMResult<OnlyCode>>() {
//                                    @Override
//                                    public void onSuccess(XMResult<OnlyCode> result) {
//                                        mineOrderAdapter.getData().remove(order);
//                                        mineOrderAdapter.notifyDataSetChanged();
//                                        if (mineOrderAdapter.getData().size() == 0) {
//                                            showEmptyView();
//                                        }
//                                    }
//
//                                    @Override
//                                    public void onFailure(int code, String msg) {
//                                        XMToast.showToast(mContext, "删除失败");
//                                    }
//                                });
//                                break;
//                        }
//                    }
//                })
//                .create();
//        xmDialog.show();
    }


    /**
     * 跳转订单详情
     *
     * @param order 订单信息
     */
    private void navigationDetailPage(OrderInfo.Order order) {
        if (!OrderTypeMeta.HOTEL.equals(order.getType()) && !OrderTypeMeta.FILM.equals(order.getType())) {
            //TODO 商城订单不展示详情，只调用支付功能
            if (order.getOrderStatusId() == OrderStatusId.WAIT_PAY) {
                handleShopOrder(order);
            }
            return;

        }
        Intent intent = new Intent(mContext, OrderDetailActivity.class);
        intent.putExtra(OrderDetailActivity.ORDER_TYPE, order);
        mContext.startActivity(intent);
    }


    private void handleShopOrder(OrderInfo.Order order) {
        //资源类型、商品id、价格
        String productId = order.getOrderId();
        String number = order.getOrderNo();
        String type = order.getType();
        String price = order.getAmount();
        String paySource = order.getPaySource();

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setAction(ShopOrderConstants.SHOP_ACTION);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setData(Uri.parse(ShopOrderConstants.SHOP_URI));

        intent.putExtra(ShopOrderConstants.ORDER_ID, productId);
        intent.putExtra(ShopOrderConstants.ORDER_NUMBER, number);
        intent.putExtra(ShopOrderConstants.PAY_TYPE, type);
        intent.putExtra(ShopOrderConstants.PRODUCT_PRICE, price);
        intent.putExtra(ShopOrderConstants.PAY_SOURCE, paySource);

        mContext.startActivity(intent);
    }


}

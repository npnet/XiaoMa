package com.xiaoma.service.order.ui;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xiaoma.component.base.VisibleFragment;
import com.xiaoma.model.XmResource;
import com.xiaoma.model.annotation.PageDescComponent;
import com.xiaoma.service.R;
import com.xiaoma.service.common.constant.EventBusTags;
import com.xiaoma.service.common.constant.EventConstants;
import com.xiaoma.service.common.constant.ServiceConstants;
import com.xiaoma.service.common.manager.CarDataManager;
import com.xiaoma.service.order.adapter.OrderListAdapter;
import com.xiaoma.service.order.model.OrderBean;
import com.xiaoma.service.order.vm.OrderListVM;
import com.xiaoma.ui.view.XmDividerDecoration;
import com.xiaoma.ui.view.XmScrollBar;
import com.xiaoma.utils.NetworkUtils;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhushi.
 * Date: 2018/12/25
 */
@PageDescComponent(EventConstants.PageDescribe.orderListActivityPagePathDesc)
public class OrderFragment extends VisibleFragment {

    public static final int TYPE_COMMITTED = 0;//已提交
    public static final int TYPE_CONFIRMED = 1;//已确认
    public static final int TYPE_CANCELED = 2;//已取消
    public static final int TYPE_ALL = 3;//全部


    public static final String TYPE_ORDER = "type_order";

    private RecyclerView mAllOrderRv;
    private XmScrollBar mOrderReceivedBar;
    private OrderListAdapter mOrderListAdapter;
    private List<OrderBean> orderList = new ArrayList<>();
    private OrderListVM mOrderListVM;
    private int mOrderType;
    /**
     * 切换刷新
     */
    protected boolean isCreated = false;

    public static OrderFragment newInstance(int type) {
        OrderFragment orderFragment = new OrderFragment();
        Bundle args = new Bundle();
        args.putInt(TYPE_ORDER, type);
        orderFragment.setArguments(args);

        return orderFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order_list, container, false);
        return super.onCreateWrapView(view);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mOrderType = getArguments().getInt(TYPE_ORDER);
        EventBus.getDefault().register(this);
        bindView(view);
        initView();
        initData();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isCreated = true;
    }


    private void bindView(View view) {
        mAllOrderRv = view.findViewById(R.id.order_list);
        mOrderReceivedBar = view.findViewById(R.id.scroll_bar);
    }

    private void initView() {
        mAllOrderRv.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        XmDividerDecoration decor = new XmDividerDecoration(mContext, DividerItemDecoration.HORIZONTAL);
        int horizontal = mContext.getResources().getDimensionPixelOffset(R.dimen.order_fragment_rv_horizontal);
        int extra = mContext.getResources().getDimensionPixelOffset(R.dimen.order_fragment_rv_extra);
        decor.setRect(horizontal, 0, horizontal, 0);
        decor.setExtraMargin(extra);
        mAllOrderRv.addItemDecoration(decor);
        mAllOrderRv.setAdapter(mOrderListAdapter = new OrderListAdapter(mOrderType));
        mOrderReceivedBar.setRecyclerView(mAllOrderRv);

    }

    private void initData() {
        mOrderListVM = ViewModelProviders.of(this).get(OrderListVM.class);
        mOrderListVM.getOrderDetailList().removeObservers(this);
        mOrderListVM.getOrderDetailList().observe(this, new Observer<XmResource<List<OrderBean>>>() {
            @Override
            public void onChanged(@Nullable XmResource<List<OrderBean>> listXmResource) {
                showContentView();
                if (listXmResource == null) {
                    mOrderListAdapter.setEmptyView(R.layout.fragment_notdata, (ViewGroup) mAllOrderRv.getParent());
                    return;
                }
                listXmResource.handle(new OnCallback<List<OrderBean>>() {
                    @Override
                    public void onSuccess(List<OrderBean> data) {
                        orderList.clear();
                        switch (mOrderType) {
                            case TYPE_COMMITTED:
                                for (int i = 0; i < data.size(); i++) {
                                    if (ServiceConstants.ORDER_RECEIVED.equals(data.get(i).getVStatus())) {
                                        orderList.add(data.get(i));
                                    }
                                }
                                mOrderListAdapter.setEmptyView(R.layout.fragment_notdata, (ViewGroup) mAllOrderRv.getParent());
                                break;

                            case TYPE_CONFIRMED:
                                for (int i = 0; i < data.size(); i++) {
                                    if (ServiceConstants.ORDER_COMPLETED.equals(data.get(i).getVStatus())) {
                                        orderList.add(data.get(i));
                                    }
                                }
                                mOrderListAdapter.setEmptyView(R.layout.fragment_notdata, (ViewGroup) mAllOrderRv.getParent());
                                break;

                            case TYPE_CANCELED:
                                for (int i = 0; i < data.size(); i++) {
                                    if (ServiceConstants.ORDER_CANCEL.equals(data.get(i).getVStatus())) {
                                        orderList.add(data.get(i));
                                    }
                                }
                                mOrderListAdapter.setEmptyView(R.layout.fragment_notdata, (ViewGroup) mAllOrderRv.getParent());
                                break;

                            case TYPE_ALL:
                                orderList.addAll(data);
                                mOrderListAdapter.setEmptyView(R.layout.fragment_notdata, (ViewGroup) mAllOrderRv.getParent());
                                break;
                        }
                        mOrderListAdapter.setNewData(orderList);
                    }

                    @Override
                    public void onError(int code, String message) {
                        showNoNetView();
                    }
                });
            }
        });

    }

    @Subscriber(tag = EventBusTags.ORDER_CANCEL_POSITION)
    public void updateOrderList(int position) {
        if (mOrderListAdapter.getItemCount() > 1) {
            mOrderListAdapter.remove(position);
            mOrderListAdapter.notifyDataSetChanged();
        } else {
            mOrderListAdapter.remove(position);
            mOrderListVM.fetchOrderDetailData(CarDataManager.getInstance().getVinInfo());
            showLoadingView();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }


    @Override
    protected void noNetworkOnRetry() {
        super.noNetworkOnRetry();
        checkNetWork();

    }

    private void checkNetWork() {
        if (NetworkUtils.isConnected(getContext()) && checkVisible()) {
            mOrderListVM.fetchOrderDetailData(CarDataManager.getInstance().getVinInfo());
            showLoadingView();
        } else {
            showNoNetView();
        }
    }

    @Override
    protected void emptyOnRetry() {
        checkNetWork();
    }

    @Override
    public void onVisibleChange(boolean realVisible) {
        super.onVisibleChange(realVisible);
        if (realVisible) {
            mOrderListVM.fetchOrderDetailData(CarDataManager.getInstance().getVinInfo());
            showLoadingView();
        }
    }

}

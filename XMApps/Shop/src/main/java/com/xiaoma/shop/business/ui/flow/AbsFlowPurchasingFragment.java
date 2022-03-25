package com.xiaoma.shop.business.ui.flow;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.xiaoma.autotracker.XmTracker;
import com.xiaoma.autotracker.model.TrackerCountType;
import com.xiaoma.model.annotation.NormalOnClick;
import com.xiaoma.model.annotation.ResId;
import com.xiaoma.shop.R;
import com.xiaoma.shop.business.model.PayInfo;
import com.xiaoma.shop.business.pay.PayHandler;
import com.xiaoma.shop.business.pay.PaySuccessResultCallback;
import com.xiaoma.shop.business.ui.ShopBaseFragment;
import com.xiaoma.shop.common.callback.DialogCommonCallbackImpl;
import com.xiaoma.shop.common.constant.ResourceType;
import com.xiaoma.shop.common.track.EventConstant;
import com.xiaoma.shop.common.util.LanUtils;
import com.xiaoma.shop.common.util.NotifyUpdateOrderInfo;
import com.xiaoma.ui.StateControl.StateView;
import com.xiaoma.ui.toast.XMToast;
import com.xiaoma.utils.ResUtils;
import com.xiaoma.utils.log.KLog;

import org.simple.eventbus.EventBus;

/**
 * Author: Ljb
 * Time  : 2019/7/19
 * Description:
 */
public abstract class AbsFlowPurchasingFragment<B, T extends BaseQuickAdapter<B, BaseViewHolder>> extends ShopBaseFragment implements View.OnClickListener {

    private RecyclerView flowRecyclerView;
    private TextView tvBuy;
    protected StateView mChildStateView;

    protected T mAdapter;
    public static final String COINPURCHASINGFRAGMENT_TAG = "CoinPurchasingFragment";
    public static final String CASHPURCHASINGFRAGMENT_TAG = "CashPurchasingFragment";
    protected PayInfo mPayInfo = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_abs_flow_purchasing, container, false);
        return onCreateWrapView(view);
    }

    @Override
    protected View onCreateWrapView(View childView) {
        return super.onCreateWrapView(childView);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        initEvent();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroyView() {
        EventBus.getDefault().unregister(this);
        super.onDestroyView();
    }

    private void initView(View view) {
        mChildStateView = view.findViewById(R.id.state_view);
        flowRecyclerView = view.findViewById(R.id.recyclerView);
        tvBuy = view.findViewById(R.id.tv_buy);

        String str = ResUtils.getString(mActivity, R.string.buy_valid_for_the_month);
//        SpannableString string = new SpannableString(str);
//        AbsoluteSizeSpan sizeSpan = new AbsoluteSizeSpan(24);
//        ForegroundColorSpan colorSpan = new ForegroundColorSpan(Color.parseColor("#8a919d"));
//        int startChange = LanUtils.isEnglish() ? 3 : 2;
//        string.setSpan(sizeSpan, startChange, string.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
//        string.setSpan(colorSpan, startChange, str.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        tvBuy.setText(str);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(mActivity, 2, GridLayoutManager.HORIZONTAL, false);
        new LinearLayoutManager(mActivity, LinearLayoutManager.HORIZONTAL, false);
        flowRecyclerView.setLayoutManager(gridLayoutManager);
        flowRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                outRect.set(0, 0, 90, 0);
            }
        });
        mAdapter = createAdapter();
        mAdapter.bindToRecyclerView(flowRecyclerView);
    }

    private void initEvent() {
        tvBuy.setOnClickListener(this);

        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                onOwnItemClick(adapter, view, position);
            }
        });
    }

    protected abstract void onOwnItemClick(BaseQuickAdapter adapter, View view, int position);

    @NormalOnClick({EventConstant.NormalClick.ACTION_BUY})
    @ResId({R.id.tv_buy})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_buy:
                onClickBuy();
                break;
        }
    }

    @Override
    protected void errorOnRetry() {
        loadData();
    }

    @Override
    protected void emptyOnRetry() {
        loadData();
    }

    @Override
    protected void showErrorView() {
        mChildStateView.showError();
    }

    @Override
    protected void showEmptyView() {
        mChildStateView.showEmpty();
    }

    @Override
    protected void showContentView() {
        mChildStateView.showContent();
    }

    @Override
    protected void showNoNetView() {
        if (getParentFragment() instanceof MainFlowFragment2) {
            ((MainFlowFragment2) getParentFragment()).showNoNetView();
        }
    }

    /**
     * 购买成功
     */
    protected void showBuySuccess() {
        PayHandler.getInstance().showCommonDialog(mActivity, new DialogCommonCallbackImpl() {

            @Override
            public void prepare(View contentView) {
                TextView coinText = contentView.findViewById(R.id.tv_pay_message);
                TextView confirmBt = contentView.findViewById(R.id.confirm_bt);
                TextView cancelBt = contentView.findViewById(R.id.cancel_bt);
                View divider = contentView.findViewById(R.id.divider_line_vertical);
                coinText.setText(R.string.purchase_success);
                confirmBt.setText(R.string.confirm);
                cancelBt.setVisibility(View.GONE);
                divider.setVisibility(View.GONE);
                XmTracker.getInstance().uploadEvent(-1, TrackerCountType.BUYFLOW.getType());
            }

            @Override
            public void onConfirm() {

            }
        });
    }

    //网络异常Toast
    protected void showErrorToast() {
        Bitmap errorBit = BitmapFactory.decodeResource(getResources(), R.drawable.icon_error);
        Drawable drawable = new BitmapDrawable(errorBit);
        XMToast.showToast(mContext, getString(R.string.network_anomaly), drawable);
    }

    //现金支付
    protected void cashPay(PayInfo payInfo) {
        PayHandler.getInstance().scanCodePayWindow(mActivity,
                payInfo.getOrderNum(),
                payInfo.getPrice(),
                payInfo.getProductId(),
                true,
                getPaySuccessCallback(payInfo.getProductId()));
    }

    // 车币支付
    protected void carCoinPay(PayInfo payInfo) {
        PayHandler.getInstance().carCoinPayWindow(mActivity,
                ResourceType.FLOW,
                payInfo.getProductId(),
                payInfo.getPrice(),
                payInfo.getOrderNum() + "",
                getPaySuccessCallback(payInfo.getProductId()));
    }

    protected PaySuccessResultCallback retInitCallback(long productId) {
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

    protected abstract void refreshFlow();

    protected abstract PaySuccessResultCallback getPaySuccessCallback(long productId);

    protected abstract T createAdapter();

    protected abstract void onClickBuy();

    protected abstract void loadData();
}

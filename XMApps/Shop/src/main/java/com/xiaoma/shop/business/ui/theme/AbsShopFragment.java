package com.xiaoma.shop.business.ui.theme;

import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.xiaoma.autotracker.XmAutoTracker;
import com.xiaoma.component.base.BaseFragment;
import com.xiaoma.model.annotation.NormalOnClick;
import com.xiaoma.model.annotation.ResId;
import com.xiaoma.shop.R;
import com.xiaoma.shop.business.model.PayInfo;
import com.xiaoma.shop.business.pay.PayHandler;
import com.xiaoma.shop.business.pay.PaySuccessResultCallback;
import com.xiaoma.shop.business.ui.view.DownloadMoreView;
import com.xiaoma.shop.common.constant.LoadMoreState;
import com.xiaoma.shop.common.constant.ResourceType;
import com.xiaoma.shop.common.constant.ThemeContract;
import com.xiaoma.shop.common.track.EventConstant;
import com.xiaoma.shop.common.util.NotifyUpdateOrderInfo;
import com.xiaoma.ui.StateControl.OnRetryClickListener;
import com.xiaoma.ui.StateControl.Type;
import com.xiaoma.ui.toast.XMToast;
import com.xiaoma.ui.view.XmScrollBar;
import com.xiaoma.utils.NetworkUtils;
import com.xiaoma.utils.ResUtils;

/**
 * <pre>
 *     author : wutao
 *     time   : 2019/01/24
 *     desc   :
 * </pre>
 */
public abstract class AbsShopFragment<T extends BaseQuickAdapter> extends BaseFragment implements View.OnClickListener, OnRetryClickListener {
    protected final String TAG = getClass().getSimpleName();
    protected ConstraintLayout rootLayout;
    protected RecyclerView mRv;
    protected T mAdapter;
    protected TextView mBtComplex;
    protected TextView mBtnSalesVolume;
    protected TextView mBtShelf;
    protected TextView mBtCoinPrice;
    protected TextView mBtCashPrice;
    protected ImageView mCarCoinSortImage;
    protected ImageView mCashPriceImage;
    protected @ThemeContract.SortRule
    String mSortRule = ThemeContract.SortRule.DEFAULT;
    private ConstraintLayout.LayoutParams mLayoutParams;
    private DownloadMoreView mLoadMoreView;

    protected PayInfo mPayInfo = null;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_abs_child_theme, container, false);
        initView(view);
        return view;
    }

    public boolean checkNet() {
        if (NetworkUtils.isConnected(mContext)) {
            return true;
        } else {
            showNoNetView();
            if (mAdapter != null) {
                mAdapter.setNewData(null);
            }
            XMToast.showToast(mContext, R.string.network_anomaly);
            return false;
        }
    }

    protected void initView(View view) {
        rootLayout = view.findViewById(R.id.root_shop_content_layout);
        mBtComplex = view.findViewById(R.id.bt_complex);
        mBtComplex.setOnClickListener(this);
        mBtnSalesVolume = view.findViewById(R.id.btn_sales_volume);
        mBtnSalesVolume.setOnClickListener(this);
        mBtShelf = view.findViewById(R.id.bt_shelf);
        mBtShelf.setOnClickListener(this);
        mBtCoinPrice = view.findViewById(R.id.bt_coin_price);
        mBtCoinPrice.setOnClickListener(this);
        mBtCashPrice = view.findViewById(R.id.bt_cash_price);
        mBtCashPrice.setOnClickListener(this);

        mStateView = view.findViewById(R.id.state_view);

        mCarCoinSortImage = view.findViewById(R.id.iv_coin_price_sort);
        mCashPriceImage = view.findViewById(R.id.iv_cash_price_sort);

        XmScrollBar xmScrollBar = view.findViewById(R.id.scroll_bar);

        mRv = view.findViewById(R.id.rv);
        mRv.setHasFixedSize(true);
        mRv.setItemAnimator(null);
        mRv.setLayoutManager(new LinearLayoutManager(mActivity, LinearLayoutManager.HORIZONTAL, false));
        mAdapter = getAdapter();
        mAdapter.setLoadMoreView(new DownloadMoreView());
        mRv.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                outRect.set(0, 0, 90, 0);
            }
        });

        mAdapter.bindToRecyclerView(mRv);
        mRv.setAdapter(mAdapter);
        xmScrollBar.setRecyclerView(mRv);
        mStateView.setOnRetryClickListener(this);
        setStateViewTips();
    }

    private void setStateViewTips() {
        try {
            View noNetworkView = mStateView.getNoNetworkView();
            TextView tv = noNetworkView.findViewById(R.id.tv_tips);
            tv.setText(R.string.network_does_not_force);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setEnableLoadMore(boolean enableLoadMore) {
        if (enableLoadMore) {
            mRv.setNestedScrollingEnabled(true);
            mAdapter.setEnableLoadMore(true);
            mAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
                @Override
                public void onLoadMoreRequested() {
                    if (mRv.isNestedScrollingEnabled()) {
                        mRv.setNestedScrollingEnabled(false);
                        loadMore();
                    }
                }
            }, mRv);
        } else {
            mAdapter.setEnableLoadMore(false);
        }
    }

    protected void tabVisible(boolean visible) {
        mBtComplex.setVisibility(visible ? View.VISIBLE : View.GONE);
        mBtnSalesVolume.setVisibility(visible ? View.VISIBLE : View.GONE);
        mBtShelf.setVisibility(visible ? View.VISIBLE : View.GONE);
        mBtCoinPrice.setVisibility(visible ? View.VISIBLE : View.GONE);
        mBtCashPrice.setVisibility(visible ? View.VISIBLE : View.GONE);
        mCarCoinSortImage.setVisibility(visible ? View.VISIBLE : View.GONE);
        mCashPriceImage.setVisibility(visible ? View.VISIBLE : View.GONE);
        if (mLayoutParams == null) {
            mLayoutParams = (ConstraintLayout.LayoutParams) mStateView.getLayoutParams();
        }
        if (visible) {
            mLayoutParams.topToBottom = R.id.ll_coin_price;
        } else {
            mLayoutParams.topToTop = ConstraintLayout.LayoutParams.PARENT_ID;
        }
    }


    private void changeSelectState(int viewId) {
        mBtComplex.setSelected(viewId == R.id.bt_complex);
        mBtComplex.setEnabled(viewId != R.id.bt_complex);
        mBtnSalesVolume.setSelected(viewId == R.id.btn_sales_volume);
        mBtnSalesVolume.setEnabled(viewId != R.id.btn_sales_volume);
        mBtShelf.setSelected(viewId == R.id.bt_shelf);
        mBtShelf.setEnabled(viewId != R.id.bt_shelf);
        mBtCoinPrice.setSelected(viewId == R.id.bt_coin_price);
        mBtCashPrice.setSelected(viewId == R.id.bt_cash_price);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        setEnableLoadMore(isLoadMoreAllowed());
        changeSelectState(R.id.bt_complex);
    }

    @Override
    @NormalOnClick({EventConstant.NormalClick.ACTION_INTEGRATED, EventConstant.NormalClick.ACTION_SALES,
            EventConstant.NormalClick.ACTION_LATEST})
    @ResId({R.id.bt_complex, R.id.btn_sales_volume, R.id.bt_shelf})
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                break;

            case R.id.bt_complex:
                clickComplex();
                break;

            case R.id.btn_sales_volume:
                clickSalesVolume();
                break;

            case R.id.bt_shelf:
                clickShelf();
                break;

            case R.id.bt_coin_price:
                if (ThemeContract.SortRule.SCORE_DESC.equals(mSortRule)) {
                    manualUpdateTrack(EventConstant.NormalClick.ACTION_COIN_UP);
                } else {
                    manualUpdateTrack(EventConstant.NormalClick.ACTION_COIN_DOWN);
                }
                clickCoinPrice();
                break;

            case R.id.bt_cash_price:
                if (ThemeContract.SortRule.RMB_DESC.equals(mSortRule)) {
                    manualUpdateTrack(EventConstant.NormalClick.ACTION_RMB_UP);
                } else {
                    manualUpdateTrack(EventConstant.NormalClick.ACTION_RMB_DOWN);
                }
                clickCashPrice();
                break;
        }
        changeSelectState(v.getId());
    }

    protected void loadMore() {
    }

    /**
     * 如果支持加载更多,每次加载之后一定要调用 告诉adapter加载的状态
     */
    protected void notifyLoadMoreState(@LoadMoreState int loadState) {
        if (loadState == LoadMoreState.COMPLETE) {
            mAdapter.loadMoreComplete();
        } else if (loadState == LoadMoreState.END) {
            mAdapter.loadMoreEnd(mAdapter.getItemCount() < 20);//表示加载到底之后 会给提示
        } else if (loadState == LoadMoreState.FAIL) {
            mAdapter.loadMoreFail();
        } else {
            Log.d(TAG, "notifyLoadMoreState-[invalid load state]-" + loadState);
        }

        mRv.setNestedScrollingEnabled(true);
    }

    @Override
    public void onRetryClick(View view, Type type) {
    }

    private void manualUpdateTrack(String eventAction) {
        if (this instanceof PersonalityThemeFragment) {
            XmAutoTracker.getInstance().onEvent(eventAction,
                    this.getClass().getName(),
                    EventConstant.PageDesc.FRAGMENT_SYSTEM_THEME);
        } else if (this instanceof VoiceToneFragment) {
            XmAutoTracker.getInstance().onEvent(eventAction,
                    this.getClass().getName(),
                    EventConstant.PageDesc.FRAGMENT_VOICE_TONE);
        }

    }




    //现金支付
    protected void cashPay(PayInfo payInfo) {
        PayHandler.getInstance().scanCodePayWindow(mActivity,
                payInfo.getOrderNum(),
                payInfo.getPrice(),
                payInfo.getProductId(),
                isFlow(),
                getPaySuccessCallback(payInfo.getProductId()));
    }



    // 车币支付
    protected void carCoinPay(PayInfo payInfo) {
        PayHandler.getInstance().carCoinPayWindow(mActivity,
              requestResourceType() ,
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
                mAdapter.notifyDataSetChanged();
                XMToast.showToast(mActivity, ResUtils.getString(mActivity, R.string.purchase_success));
            }

            @Override
            public void cancel() {

            }
        };
    }

    protected abstract PaySuccessResultCallback getPaySuccessCallback(long  productId);

    protected abstract boolean isFlow();

    protected abstract int requestResourceType();

    protected abstract T getAdapter();

    protected abstract void clickComplex();

    protected abstract void clickSalesVolume();

    protected abstract void clickShelf();

    protected abstract void clickCoinPrice();

    protected abstract void clickCashPrice();
}

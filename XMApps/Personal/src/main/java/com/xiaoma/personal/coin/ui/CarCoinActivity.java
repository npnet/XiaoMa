package com.xiaoma.personal.coin.ui;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.xiaoma.component.base.BaseActivity;
import com.xiaoma.model.XmResource;
import com.xiaoma.model.annotation.PageDescComponent;
import com.xiaoma.personal.R;
import com.xiaoma.personal.coin.adapter.CarCoinRecordAdapter;
import com.xiaoma.personal.coin.model.CoinRecord;
import com.xiaoma.personal.coin.vm.CoinRecordVM;
import com.xiaoma.personal.common.util.EventConstants;
import com.xiaoma.ui.StateControl.OnRetryClickListener;
import com.xiaoma.ui.StateControl.StateView;
import com.xiaoma.ui.StateControl.Type;
import com.xiaoma.ui.toast.XMToast;
import com.xiaoma.ui.view.XmScrollBar;
import com.xiaoma.utils.CollectionUtil;
import com.xiaoma.utils.NetworkUtils;

import java.util.List;

/**
 * @author Gillben
 * date: 2018/12/07
 * <p>
 * 我的车币
 */
@PageDescComponent(EventConstants.PageDescribe.personalCenterCarCoin)
public class CarCoinActivity extends BaseActivity {

    private TextView mCoinTotalText;
    private RecyclerView mCoinRecyclerView;
    private CarCoinRecordAdapter mCarCoinRecordAdapter;
    private CoinRecordVM mCoinRecordVM;
    private StateView mRightStateView;
    private int mRecordPage;
    private XmScrollBar mScrollBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_coin);
        initView();
        initListener();
        iniData();
    }

    private void initView() {
        mCoinTotalText = findViewById(R.id.tv_total_car_coin);
        mCoinRecyclerView = findViewById(R.id.car_coin_record_recycler_view);
        mRightStateView = findViewById(R.id.state_view);
        mScrollBar = findViewById(R.id.scroll_bar);

        LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        DividerItemDecoration itemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        itemDecoration.setDrawable(getDrawable(R.drawable.horizontal_divide_line));
        mCarCoinRecordAdapter = new CarCoinRecordAdapter();
        mCoinRecyclerView.setLayoutManager(manager);
        mCoinRecyclerView.addItemDecoration(itemDecoration);
        mCarCoinRecordAdapter.bindToRecyclerView(mCoinRecyclerView);
        mCarCoinRecordAdapter.setEnableLoadMore(true);
        mCarCoinRecordAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                mRecordPage++;
                mCoinRecordVM.fetchCoinRecords(mRecordPage);
            }
        }, mCoinRecyclerView);

        mScrollBar.setRecyclerView(mCoinRecyclerView);
    }

    private void initListener() {
        mRightStateView.setOnRetryClickListener(new OnRetryClickListener() {
            @Override
            public void onRetryClick(View view, Type type) {
                switch (type) {
                    case ERROR:
                        rightErrorOnRetry();
                        break;
                    case EEMPTY:
                        rightEmptyOnRetry();
                        break;
                }
            }
        });
    }

    private void iniData() {
        mCoinRecordVM = ViewModelProviders.of(this).get(CoinRecordVM.class);
        mCoinRecordVM.getCarCoin().observe(this, new Observer<XmResource<Integer>>() {
            @Override
            public void onChanged(@Nullable XmResource<Integer> integerXmResource) {
                integerXmResource.handle(new OnCallback<Integer>() {

                    @Override
                    public void onSuccess(Integer data) {
                        mBaseStateView.showContent();
                        mCoinTotalText.setText(String.valueOf(data));
                    }

                    @Override
                    public void onError(int code, String message) {
//                        if (NetworkUtils.isConnected(getApplication())) {
                            XMToast.toastException(getApplication(), R.string.network_error_retry);
//                        } else {
                            mBaseStateView.showNoNetwork();
//                        }
                    }
                });

            }
        });

        mCoinRecordVM.getCoinRecords().observe(this, new Observer<XmResource<List<CoinRecord>>>() {
            @Override
            public void onChanged(@Nullable XmResource<List<CoinRecord>> listXmResource) {
                listXmResource.handle(new OnCallback<List<CoinRecord>>() {

                    @Override
                    public void onLoading() {
                        if (mRecordPage == 0) {
                            super.onLoading();
                        }
                    }

                    @Override
                    public void onSuccess(List<CoinRecord> data) {
                        mCarCoinRecordAdapter.loadMoreComplete();
                        if (!CollectionUtil.isListEmpty(data)) {
                            if (mRecordPage == 0) {
                                mCarCoinRecordAdapter.setNewData(data);
                            } else {
                                mCarCoinRecordAdapter.addData(data);
                            }
                            mBaseStateView.showContent();
                            mRightStateView.showContent();
                        } else {
                            if (mRecordPage != 0) {
                                mCarCoinRecordAdapter.loadMoreEnd();
                            } else {
                                mRightStateView.showEmpty();
                            }
                        }
                    }

                    @Override
                    public void onError(int code, String message) {
                        if (mRecordPage > 0) {
                            mRecordPage--;
                        }
//                        if (NetworkUtils.isConnected(getApplication())) {
                            XMToast.toastException(getApplication(), R.string.network_error_retry);
//                            mRightStateView.showError();
//                        } else {
                            mBaseStateView.showNoNetwork();
//                        }
                    }
                });
            }
        });

        if(NetworkUtils.isConnected(getApplication())){
            fetchNetContent();
        }else {
            mBaseStateView.showNoNetwork();
        }

    }

    private void fetchNetContent() {
        mCoinRecordVM.fetchCarCoin();
        mRecordPage = 0;
        mCoinRecordVM.fetchCoinRecords(mRecordPage);
    }

    private void rightEmptyOnRetry() {
        mRecordPage = 0;
        mCoinRecordVM.fetchCoinRecords(mRecordPage);
    }

    private void rightErrorOnRetry() {
        mRecordPage = 0;
        mCoinRecordVM.fetchCoinRecords(mRecordPage);
    }

    @Override
    protected void noNetworkOnRetry() {
        if (NetworkUtils.isConnected(getApplication())) {
            fetchNetContent();
        } else {
            showNoNetView();
        }
    }
}

package com.xiaoma.shop.business.ui.theme;

import android.app.DownloadManager;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.graphics.drawable.LevelListDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;

import com.xiaoma.autotracker.XmAutoTracker;
import com.xiaoma.image.ImageLoader;
import com.xiaoma.model.ResultCallback;
import com.xiaoma.model.XMResult;
import com.xiaoma.model.XmResource;
import com.xiaoma.model.annotation.PageDescComponent;
import com.xiaoma.shop.R;
import com.xiaoma.shop.business.adapter.VoiceToneAdapter;
import com.xiaoma.shop.business.download.DownloadListener;
import com.xiaoma.shop.business.download.DownloadStatus;
import com.xiaoma.shop.business.model.PayInfo;
import com.xiaoma.shop.business.model.SkinVersionsBean;
import com.xiaoma.shop.business.model.SkusBean;
import com.xiaoma.shop.business.pay.PayHandler;
import com.xiaoma.shop.business.pay.PaySuccessResultCallback;
import com.xiaoma.shop.business.tts.TTSDownload;
import com.xiaoma.shop.business.tts.TTSUsing;
import com.xiaoma.shop.business.vm.VoiceToneVM;
import com.xiaoma.shop.common.RequestManager;
import com.xiaoma.shop.common.callback.OnPayFromPersonalCallback;
import com.xiaoma.shop.common.constant.ResourceType;
import com.xiaoma.shop.common.constant.ThemeContract;
import com.xiaoma.shop.common.track.EventConstant;
import com.xiaoma.shop.common.util.NotifyUpdateOrderInfo;
import com.xiaoma.thread.ThreadDispatcher;
import com.xiaoma.ui.StateControl.Type;
import com.xiaoma.ui.toast.XMToast;
import com.xiaoma.utils.ListUtils;
import com.xiaoma.utils.NetworkUtils;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;
import org.simple.eventbus.ThreadMode;

import java.util.List;
import java.util.Objects;


/**
 * <pre>
 *     author : wutao
 *     time   : 2019/01/24
 *     desc   :
 * </pre>
 */
@PageDescComponent(EventConstant.PageDesc.FRAGMENT_VOICE_TONE)
public class VoiceToneFragment extends AbsShopFragment<VoiceToneAdapter> implements OnPayFromPersonalCallback {
    public static final String TAG = "VoiceToneFragment";
    private boolean mNewFetchT = true;
    private VoiceToneVM mVoiceToneVM;
    private DownloadListener mDownloadListener;
    private TTSAdapterCallbackImpl mTTSAdapterCallback;
    private boolean mHasInitSuccessF = false;


    public static VoiceToneFragment newInstance() {
        return new VoiceToneFragment();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        EventBus.getDefault().register(this);
        mHasInitSuccessF = true;
        initVM();
        mSortRule = ThemeContract.SortRule.DEFAULT;
        TTSDownload.getInstance().addDownloadListener(mDownloadListener = new DownloadListener() {
            @Override
            public void onDownloadStatus(@Nullable final DownloadStatus downloadStatus) {
                ThreadDispatcher.getDispatcher().runOnMain(new Runnable() {
                    @Override
                    public void run() {
                        if (isDestroy())
                            return;
                        if (downloadStatus != null &&
                                DownloadManager.STATUS_SUCCESSFUL == downloadStatus.status) {
                            XMToast.toastSuccess(getContext(), R.string.tips_download_success_and_use);
                        }
                        if (mAdapter != null) {
                            mAdapter.notifyDataSetChanged();
                        }
                    }
                });

            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
        TTSDownload.getInstance().removeDownloadListener(mDownloadListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mTTSAdapterCallback != null)
            mTTSAdapterCallback.onStop();
    }

    private void initVM() {
        mVoiceToneVM = ViewModelProviders.of(this).get(VoiceToneVM.class);
        mVoiceToneVM.getLoadMoreStateLiveData().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer integer) {
                //                if (Objects.equals(integer, LoadMoreState.FAIL)
                //                        && ListUtils.isEmpty(mAdapter.getData())) {
                //                    showEmptyView();
                //                } else {
                //                }
                notifyLoadMoreState(integer);
            }
        });
        mVoiceToneVM.getSkuLists().observe(this, new Observer<XmResource<List<SkusBean>>>() {
            @Override
            public void onChanged(@Nullable XmResource<List<SkusBean>> skusBeans) {
                dismissProgress();
                Log.d("Jir", "onChanged: sysSkinsList " + skusBeans);
                skusBeans.handle(new OnCallback<List<SkusBean>>() {
                    @Override
                    public void onSuccess(List<SkusBean> data) {
                        showParentContentView();
                        if (mNewFetchT) {
                            if (ListUtils.isEmpty(data)) {
                                setEnableLoadMore(false);
                                showEmptyView();
                            } else {
                                setEnableLoadMore(data.size() >= 20);
                                showContentView();
                            }
                            mAdapter.setNewData(data);
                        } else {
                            mAdapter.addData(data);
                        }
                        if (mPayInfo != null) {
                            handlePay(mPayInfo,true);
                            mPayInfo=null;
                        }
                    }

                    @Override
                    public void onFailure(String msg) {
                        showParentContentView();
                        if (mNewFetchT || ListUtils.isEmpty(mAdapter.getData())) {
                            showEmptyView();
                            mAdapter.setNewData(null);
                        }
                    }

                    @Override
                    public void onError(int code, String message) {
                        if (mNewFetchT || ListUtils.isEmpty(mAdapter.getData())) {
                            mAdapter.setNewData(null);
                            if (!NetworkUtils.isConnected(mActivity) || getString(R.string.network_error).equals(message)) {
                                XMToast.toastException(mContext, mContext.getString(R.string.network_anomaly));
                                showNoNetView();
                            } else {
                                XMToast.toastException(getContext(), message);
                                showParentContentView();
                                showErrorView();
                            }
                        } else {
                            showParentContentView();
                        }
                    }
                });
            }
        });
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (mHasInitSuccessF && !hidden && ThemeContract.SortRule.DEFAULT.equals(mSortRule)) {
            clickComplex();
        }
    }

    @Override
    protected VoiceToneAdapter getAdapter() {
        return new VoiceToneAdapter(ImageLoader.with(this), mTTSAdapterCallback = new TTSAdapterCallbackImpl(this) {
            @Override
            protected void onEventReport(String content, String eventAction) {
                VoiceToneFragment.this.manualUpdateTrack(content, eventAction);
            }

            @Override
            protected void onNotifyDataSetChanged() {
                callNotifyDataSetChanged();
            }

            @Override
            protected void onAfterBuyed(SkusBean item) {
                dealAfterBuyed(item);
            }

            @Override
            public boolean onUseTTS(SkusBean item) {
                boolean result = super.onUseTTS(item);
                if (result) {
                    callNotifyDataSetChanged();
                }
                return result;
            }
        });
    }

    @Override
    protected void clickComplex() {
        fetch(ThemeContract.SortRule.INTEGRATED);
    }

    @Override
    protected void clickSalesVolume() {
        fetch(ThemeContract.SortRule.SALES_COUNT);
    }

    @Override
    protected void clickShelf() {
        fetch(ThemeContract.SortRule.LATEST);
    }

    @Override
    protected void clickCoinPrice() {
        if (mBtCoinPrice.isSelected()) {
            fetch(mCarCoinSortImage.isSelected() ? ThemeContract.SortRule.SCORE_DESC : ThemeContract.SortRule.SCORE_ASC);
            mCarCoinSortImage.setSelected(!mCarCoinSortImage.isSelected());
        } else {
            fetch(mCarCoinSortImage.isSelected() ? ThemeContract.SortRule.SCORE_ASC : ThemeContract.SortRule.SCORE_DESC);
        }
        LevelListDrawable priceUpDown = (LevelListDrawable) mCarCoinSortImage.getDrawable();
        if (mCarCoinSortImage.isSelected()) {
            priceUpDown.setLevel(2);
        } else {
            priceUpDown.setLevel(1);
        }
    }

    @Override
    protected void clickCashPrice() {
        if (mBtCashPrice.isSelected()) {
            fetch(mCashPriceImage.isSelected() ? ThemeContract.SortRule.RMB_DESC : ThemeContract.SortRule.RMB_ASC);
            mCashPriceImage.setSelected(!mCashPriceImage.isSelected());
        } else {
            fetch(mCashPriceImage.isSelected() ? ThemeContract.SortRule.RMB_ASC : ThemeContract.SortRule.RMB_DESC);
        }
        LevelListDrawable priceUpDown = (LevelListDrawable) mCashPriceImage.getDrawable();
        if (mCashPriceImage.isSelected()) {
            priceUpDown.setLevel(2);
        } else {
            priceUpDown.setLevel(1);
        }
    }

    @Override
    public void onRetryClick(View view, Type type) {
        fetch(mSortRule);
    }


    private void dealAfterBuyed(final SkusBean item) {
        item.setBuy(true);
        RequestManager.addUseNum(ResourceType.ASSISTANT, item.getId(), new ResultCallback<XMResult<String>>() {
            @Override
            public void onSuccess(XMResult<String> result) {
                item.setUsedNum(item.getUsedNum() + 1);
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(int code, String msg) {
                mAdapter.notifyDataSetChanged();
            }
        });
        PayHandler.getInstance().buySuccess(getActivity(), new PaySuccessResultCallback() {
            @Override
            public void confirm() {
                TTSUsing.useTTS(VoiceToneFragment.this, item);
                callNotifyDataSetChanged();
            }

            @Override
            public void cancel() {
                mAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    protected void loadMore() {
        mNewFetchT = false;
        mVoiceToneVM.loadMore();
    }

    private void fetch(@ThemeContract.SortRule String rule) {
        mSortRule = rule;
        mNewFetchT = true;
        if (!(Objects.equals(ThemeContract.SortRule.SCORE_ASC, rule)
                || Objects.equals(ThemeContract.SortRule.SCORE_DESC, rule))) {
            if (mCarCoinSortImage != null) {
                mCarCoinSortImage.getDrawable().setLevel(0);
                mCarCoinSortImage.setSelected(false);
            }
        }

        if (!(Objects.equals(ThemeContract.SortRule.RMB_ASC, rule)
                || Objects.equals(ThemeContract.SortRule.RMB_DESC, rule))) {
            if (mCashPriceImage != null) {
                mCashPriceImage.getDrawable().setLevel(0);
                mCashPriceImage.setSelected(false);
            }
        }
        if (checkNet()) {
            if (Objects.equals(ThemeContract.SortRule.DEFAULT, rule)) {
                mSortRule = ThemeContract.SortRule.INTEGRATED;
            }
            showProgressDialog(R.string.loading);
            if (mVoiceToneVM != null) {
                mVoiceToneVM.fetchVoiceTones(rule);
            }
        }
    }

    private void callNotifyDataSetChanged() {
        ThreadDispatcher.getDispatcher().postOnMain(new Runnable() {
            @Override
            public void run() {
                if (isDestroy())
                    return;
                if (mAdapter != null) {
                    mAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    private void manualUpdateTrack(String content, String eventAction) {
        XmAutoTracker.getInstance().onEvent(eventAction, content, this.getClass().getName(), EventConstant.PageDesc.FRAGMENT_VOICE_TONE);
    }

    @Override
    protected void showNoNetView() {// show parent no network view
        if (getParentFragment() != null) {
            Fragment fragment = getParentFragment();
            if (fragment instanceof MainThemeFragment) {
                ((MainThemeFragment) fragment).parentShowNoNetView(TAG);
            }
        }
    }

    private void showParentContentView() {
        if (getParentFragment() instanceof MainThemeFragment) {
            ((MainThemeFragment) getParentFragment()).parentShowContentView();
        }
    }

    @Subscriber(mode = ThreadMode.MAIN, tag = TAG)
    public void getMessage(String msg) {
        fetch(mSortRule);
    }

    @Override
    public void handlePay(PayInfo payInfo, boolean immediateExecute) {
        if (payInfo == null) return;
        mPayInfo = payInfo;
        if (immediateExecute || (mAdapter != null && !ListUtils.isEmpty(mAdapter.getData()))) {
            if (payInfo.isCoinPay()) {
                carCoinPay(payInfo);
            } else {
                cashPay(payInfo);
            }
        }
    }


    @Override
    protected boolean isFlow() {
        return false;
    }

    protected PaySuccessResultCallback getPaySuccessCallback(long productId) {
        if (mAdapter == null || ListUtils.isEmpty(mAdapter.getData())) return retInitCallback(productId);
        final SkusBean skusBean = mAdapter.searchItemByProductId(productId);
        if (skusBean == null) return retInitCallback(productId);
        return new PaySuccessResultCallback() {
            @Override
            public void confirm() {
                NotifyUpdateOrderInfo.sendNotifyMsg();
                dealAfterBuyed(skusBean);
            }

            @Override
            public void cancel() {

            }
        };
    }

    @Override
    protected int requestResourceType() {
        return ResourceType.ASSISTANT;
    }
}

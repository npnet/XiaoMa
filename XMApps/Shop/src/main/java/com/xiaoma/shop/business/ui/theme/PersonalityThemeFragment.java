package com.xiaoma.shop.business.ui.theme;

import android.app.Activity;
import android.app.DownloadManager;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.LevelListDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewCompat;
import android.util.Log;
import android.view.View;

import com.xiaoma.autotracker.XmAutoTracker;
import com.xiaoma.autotracker.XmTracker;
import com.xiaoma.autotracker.model.TrackerCountType;
import com.xiaoma.autotracker.model.TrackerEventType;
import com.xiaoma.guide.utils.GuideConstants;
import com.xiaoma.guide.utils.GuideDataHelper;
import com.xiaoma.guide.utils.NewGuide;
import com.xiaoma.image.ImageLoader;
import com.xiaoma.model.ResultCallback;
import com.xiaoma.model.XMResult;
import com.xiaoma.model.XmResource;
import com.xiaoma.model.annotation.PageDescComponent;
import com.xiaoma.shop.R;
import com.xiaoma.shop.business.adapter.PersonalityThemeAdapterV2;
import com.xiaoma.shop.business.download.DownloadListener;
import com.xiaoma.shop.business.download.DownloadStatus;
import com.xiaoma.shop.business.download.impl.SkinDownload;
import com.xiaoma.shop.business.model.PayInfo;
import com.xiaoma.shop.business.model.SkinVersionsBean;
import com.xiaoma.shop.business.model.SkusBean;
import com.xiaoma.shop.business.pay.PayHandler;
import com.xiaoma.shop.business.pay.PaySuccessResultCallback;
import com.xiaoma.shop.business.skin.SkinUsing;
import com.xiaoma.shop.business.ui.main.MainActivity;
import com.xiaoma.shop.business.vm.PersonalThemeVM;
import com.xiaoma.shop.common.RequestManager;
import com.xiaoma.shop.common.callback.OnPayFromPersonalCallback;
import com.xiaoma.shop.common.callback.OnRefreshCallback;
import com.xiaoma.shop.common.constant.ResourceType;
import com.xiaoma.shop.common.constant.ShopContract;
import com.xiaoma.shop.common.constant.ThemeContract;
import com.xiaoma.shop.common.track.EventConstant;
import com.xiaoma.shop.common.track.ShopTrackManager;
import com.xiaoma.shop.common.util.NotifyUpdateOrderInfo;
import com.xiaoma.shop.common.util.SkinHelper;
import com.xiaoma.skin.constant.SkinConstants;
import com.xiaoma.thread.Priority;
import com.xiaoma.thread.SeriesAsyncWorker;
import com.xiaoma.thread.ThreadDispatcher;
import com.xiaoma.thread.Work;
import com.xiaoma.ui.StateControl.Type;
import com.xiaoma.ui.toast.XMToast;
import com.xiaoma.utils.ListUtils;
import com.xiaoma.utils.NetworkUtils;
import com.xiaoma.utils.tputils.TPUtils;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;
import org.simple.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * <pre>
 *     author : wutao
 *     time   : 2019/01/24state_view
 *     desc   :
 * </pre>
 */
@PageDescComponent(EventConstant.PageDesc.FRAGMENT_SYSTEM_THEME)
public class PersonalityThemeFragment extends AbsShopFragment<PersonalityThemeAdapterV2> implements OnPayFromPersonalCallback {
    public static final String TAG = " PersonalityThemeFragment";
    private PersonalThemeVM mVM;
    private boolean mNewFetchT = true;
    private int mCurType = -1;
    private Handler handler;
    private Runnable runnable;
    private NewGuide newGuide;
    private SkinHelper skinHelper;
    private OnRefreshCallback onRefreshCallback;

    public static PersonalityThemeFragment newInstance() {
        return new PersonalityThemeFragment();
    }

    private void initVM() {
        mVM = ViewModelProviders.of(this).get(PersonalThemeVM.class);
        mVM.getSystemSkinsLiveData().observe(this, new Observer<XmResource<List<SkinVersionsBean>>>() {
            @Override
            public void onChanged(@Nullable XmResource<List<SkinVersionsBean>> listXmResource) {
                dismissProgress();
                listXmResource.handle(new OnCallback<List<SkinVersionsBean>>() {
                    @Override
                    public void onSuccess(List<SkinVersionsBean> data) {
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
                            refreshLoadingFromOut();
                        }
                        if (mPayInfo != null) {
                            handlePay(mPayInfo, true);
                            mPayInfo = null;
                        }
                    }

                    @Override
                    public void onFailure(String msg) {
                        showParentContentView();
                        if (mNewFetchT || ListUtils.isEmpty(mAdapter.getData())) {
                            showEmptyView();
                            mAdapter.setNewData(null);
                        }
                        finishGuide();
                    }

                    @Override
                    public void onError(int code, String message) {
                        if (mNewFetchT || ListUtils.isEmpty(mAdapter.getData())) {
                            mAdapter.setNewData(null);
                            if (!NetworkUtils.isConnected(mActivity) || getString(R.string.network_error).equals(message)) {
                                XMToast.toastException(mContext, mContext.getString(R.string.network_anomaly));
                                showNoNetView();
                            } else {
                                XMToast.toastException(mContext, message);
                                showParentContentView();
                                showErrorView();
                            }
                        } else {
                            showParentContentView();
                        }
                        finishGuide();
                    }
                });
            }
        });

        mVM.getLoadMoreStateLiveData().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer integer) {
                //                if (integer == LoadMoreState.FAIL && ListUtils.isEmpty(mAdapter.getData())) {
                //                    showEmptyView();
                //                } else {
                //
                //                }
                notifyLoadMoreState(integer);
            }
        });
        mSortRule = ThemeContract.SortRule.INTEGRATED;
        clickComplex();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            if (mAdapter != null) {
                mAdapter.notifyDataSetChanged();
                if (ListUtils.isEmpty(mAdapter.getData())) {
                    request(mSortRule, mCurType);
                }
            }
        }
    }

    private BroadcastReceiver mSkinChangeReceiver;
    private DownloadListener mDownloadListener;
    private SkinDownload mSkinDownload;
    private PayHandler.PayCallback mPayCallback;

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Activity act = getActivity();
        EventBus.getDefault().register(this);
        if (act != null) {
            // 监听皮肤变化
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(SkinConstants.SKIN_ACTION);
            intentFilter.addAction(SkinConstants.SKIN_ACTION_DAOMENG);
            intentFilter.addAction(SkinConstants.SKIN_ACTION_QINGSHE);
            intentFilter.addAction(SkinConstants.SKIN_ACTION_XM);
            act.registerReceiver(mSkinChangeReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    if (isDestroy())
                        return;
                    ViewCompat.postOnAnimationDelayed(mRv, new Runnable() {
                        @Override
                        public void run() {
                            if (!isDestroy() && mAdapter != null) {
                                mAdapter.notifyDataSetChanged();
                            }
                        }
                    }, 200);
                }
            }, intentFilter);
        }
        // 监听所有下载内容
        mSkinDownload = SkinDownload.getInstance();
        mSkinDownload.addDownloadListener(mDownloadListener = new DownloadListener() {
            @Override
            public void onDownloadStatus(final DownloadStatus downloadStatus) {
                ViewCompat.postOnAnimation(view, new Runnable() {
                    @Override
                    public void run() {
                        if (isDestroy() || downloadStatus == null)
                            return;
                        if (DownloadManager.STATUS_SUCCESSFUL == downloadStatus.status) {
                            XMToast.toastSuccess(getContext(), R.string.tips_download_success_and_use);
                        }
                        if (mAdapter != null) {
                            SeriesAsyncWorker.create().next(new Work(Priority.HIGH) {
                                @Override
                                public void doWork(Object lastResult) {
                                    if (isDestroy() || mAdapter == null)
                                        return;
                                    List<Integer> posList = new ArrayList<>();
                                    int itemCount = mAdapter.getItemCount();
                                    for (int i = 0; i < itemCount; i++) {
                                        SkinVersionsBean item = mAdapter.getItem(i);
                                        if (item != null && Objects.equals(item.getUrl(), downloadStatus.downUrl)) {
                                            posList.add(i);
                                        }
                                    }
                                    if (!posList.isEmpty()) {
                                        doNext(posList);
                                    }
                                }
                            }).next(new Work<List<Integer>>() {
                                @Override
                                public void doWork(List<Integer> posList) {
                                    if (isDestroy() || mAdapter == null)
                                        return;
                                    for (Integer integer : posList) {
                                        try {
                                            mAdapter.notifyItemChanged(integer);
                                        } catch (Exception ignored) {
                                        }
                                    }
                                }
                            }).start();
                        }
                    }
                });
            }
        });
        // 监听支付状态
        PayHandler.getInstance().addPayCallback(mPayCallback = new PayHandler.PayCallback() {
            @Override
            public void onPaySuccess(final boolean buyWithQRCode, final long skuId) {
                SeriesAsyncWorker.create().next(new Work(Priority.NORMAL) {
                    @Override
                    public void doWork(Object lastResult) {
                        if (isDestroy())
                            return;
                        List<SkinVersionsBean> dataList = mAdapter.getData();
                        if (dataList != null) {
                            List<SkinVersionsBean> skins = new ArrayList<>(dataList);
                            for (SkinVersionsBean skin : skins) {
                                if (skin.getId() == skuId) {
                                    skin.setIsBuy(true);
                                    if (buyWithQRCode) {
                                        XmAutoTracker.getInstance().onEventDirectUpload(TrackerEventType.EXPOSE, EventConstant.Expose.QR_CODE_BUY_SUCCESS, skin.toTrackString(), this.getClass().getName(), EventConstant.PageDesc.FRAGMENT_SYSTEM_THEME);
                                    }
                                }
                            }
                            doNext();
                        }
                    }
                }).next(new Work() {
                    @Override
                    public void doWork(Object lastResult) {
                        if (isDestroy())
                            return;
                        mAdapter.notifyDataSetChanged();
                    }
                }).start();
            }
        });
        initVM();
        shouldShowGuideWindow();
        skinHelper = new SkinHelper();
        registerRefreshCallback();
    }

    private void registerRefreshCallback() {
        SkinDownload.getInstance().addRefreshCallback(onRefreshCallback = new OnRefreshCallback() {
            @Override
            public void onSingleRefresh(long id, String filePath) {
                if (isDestroy() || mAdapter == null || ListUtils.isEmpty(mAdapter.getData()))
                    return;
                int pos = skinHelper.findPositionByUrl(mAdapter.getData(), filePath);
                if (pos >= 0) {
                    mAdapter.getData().get(pos).setUsedNum(mAdapter.getData().get(pos).getUsedNum() + 1);
                    mAdapter.notifyItemChanged(pos);
                }
            }

            @Override
            public void onRefreshAll() {

            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
        if (getActivity() != null) {
            getActivity().unregisterReceiver(mSkinChangeReceiver);
        }
        mSkinDownload.removeDownloadListener(mDownloadListener);
        PayHandler.getInstance().removePayCallback(mPayCallback);
        SkinDownload.getInstance().removeRefreshCallback(onRefreshCallback);
    }

    @Override
    public void onResume() {
        super.onResume();
        //用于刷新从我的购买过来的进度
        refreshLoadingFromOut();
    }

    @Override
    protected PersonalityThemeAdapterV2 getAdapter() {
        return new PersonalityThemeAdapterV2(ImageLoader.with(PersonalityThemeFragment.this), new PersonalityThemeAdapterV2.Callback() {
            @Override
            public void onSkinItemClick(final SkinVersionsBean item) {
                if (getActivity() == null)
                    return;
                final ThemeDetailsFragment themeDetailsFragment = ThemeDetailsFragment.newInstance(item);
                ((MainActivity) getActivity()).addHoleContainer(themeDetailsFragment);
                dismissGuideWindow();
                manualUpdateTrack(item.toTrackString(), EventConstant.NormalClick.ACTION_SKIN_SHOW);
            }

            @Override
            public void onUseSkin(SkinVersionsBean item) {
                if (getContext() == null)
                    return;
                manualUpdateTrack(item.toTrackString(), EventConstant.NormalClick.ACTION_USE);
                SkinUsing.useSkin(PersonalityThemeFragment.this, item);
            }

            @Override
            public void onDownloadSkin(SkinVersionsBean item) {
                if (!skinHelper.checkCanDownload(mSkinDownload, mActivity, true)) return;
                if (getContext() == null)
                    return;
                doDownload(item);
                manualUpdateTrack(item.toTrackString(), EventConstant.NormalClick.ACTION_DOWNLOAD);
            }

            @Override
            public void onSkinDownloading(SkinVersionsBean item) {
                showToast(R.string.tips_wait_download_complete);
            }

            @Override
            public void onBuySkin(SkinVersionsBean item) {
                payForSkins(item);
                manualUpdateTrack(item.toTrackString(), EventConstant.NormalClick.ACTION_BUY);
            }

            @Override
            public void onTrialSkin(final SkinVersionsBean item) {
                if (getContext() == null)
                    return;
                manualUpdateTrack(item.toTrackString(), EventConstant.NormalClick.ACTION_TRIAL);
                TrialUI.showTrialFirst(getActivity(), getContext().getString(R.string.tab_system_theme), item.getAppName(), item.getTrialTime(), new TrialUI.ITrialSelectListener() {
                    @Override
                    public void onLeftSelected() {
                        if (getContext() == null)
                            return;
                        manualUpdateTrack(item.toTrackString(), EventConstant.NormalClick.ACTION_TRIAL);
                        SkinUsing.trialSkin(PersonalityThemeFragment.this, item);
                    }
                });
            }
        });
    }

    private void doDownload(final SkinVersionsBean item) {
        if (!NetworkUtils.isConnected(getContext())) {
            showToastException(R.string.no_network);
            return;
        }
        RequestManager.addSkuToBuyList(item.getId(), ResourceType.SKIN, new ResultCallback<XMResult<Object>>() {
            @Override
            public void onSuccess(XMResult<Object> result) {
                if (isDestroy())
                    return;
                if (result != null && result.isSuccess()) {
                    mSkinDownload.start(item);
                } else {
                    showToastException(R.string.hint_download_error);
                }
            }

            @Override
            public void onFailure(int code, String msg) {
                if (isDestroy())
                    return;
                showToastException(R.string.hint_download_error);
            }
        });
    }

    @Override
    protected void loadMore() {
        mNewFetchT = false;
        mVM.loadMore();
    }

    @Override
    protected void clickComplex() {
        request(ThemeContract.SortRule.INTEGRATED, -1);
    }

    @Override
    protected void clickSalesVolume() {
        request(ThemeContract.SortRule.SALES_COUNT, -1);
    }

    @Override
    protected void clickShelf() {
        request(ThemeContract.SortRule.LATEST, -1);
    }

    @Override
    protected void clickCoinPrice() {
        if (mBtCoinPrice.isSelected()) {
            request(mCarCoinSortImage.isSelected() ? ThemeContract.SortRule.SCORE_DESC : ThemeContract.SortRule.SCORE_ASC, ShopContract.PriceType.SCORE);
            mCarCoinSortImage.setSelected(!mCarCoinSortImage.isSelected());
        } else {
            request(mCarCoinSortImage.isSelected() ? ThemeContract.SortRule.SCORE_ASC : ThemeContract.SortRule.SCORE_DESC, ShopContract.PriceType.SCORE);
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
            request(mCashPriceImage.isSelected() ? ThemeContract.SortRule.RMB_DESC : ThemeContract.SortRule.RMB_ASC, ShopContract.PriceType.CASH);
            mCashPriceImage.setSelected(!mCashPriceImage.isSelected());
        } else {
            request(mCashPriceImage.isSelected() ? ThemeContract.SortRule.RMB_ASC : ThemeContract.SortRule.RMB_DESC, ShopContract.PriceType.CASH);
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
        request(mSortRule, mCurType);
    }


    private void dealAfterSkinBuyed(final SkinVersionsBean bean) {
        bean.setIsBuy(true);
        XmTracker.getInstance().uploadEvent(-1, TrackerCountType.BUYTHEME.getType());
        if (mAdapter != null)
            mAdapter.notifyDataSetChanged();
        final FragmentActivity activity = getActivity();
        if (activity == null)
            return;
        PayHandler.getInstance().buySuccess(activity, new PaySuccessResultCallback() {
            @Override
            public void confirm() {
                SkinUsing.useSkin(PersonalityThemeFragment.this, bean);
                ThreadDispatcher.getDispatcher().runOnMain(new Runnable() {
                    @Override
                    public void run() {
                        bean.setIsBuy(true);
                        if (mAdapter != null) {
                            mAdapter.notifyDataSetChanged();
                        }
                    }
                });
            }

            @Override
            public void cancel() {
            }
        });
    }

    private void payForSkins(SkinVersionsBean bean) {
        ShopTrackManager.newSingleton().setBaseInfo(ResourceType.SKIN, bean.toTrackString(), this.getClass().getName());
        switch (bean.getPay()) {
            case ShopContract.Pay
                    .DEFAULT:
            case ShopContract.Pay
                    .COIN:
                PayHandler.getInstance().carCoinPayWindow(
                        getActivity(),
                        ResourceType.SKIN,
                        bean.getId(),
                        bean.getAppName(),
                        bean.getDiscountScorePrice(),
                        false,
                        getPayResultCallback(bean));
                break;
            case ShopContract.Pay
                    .COIN_AND_RMB:
                PayHandler.getInstance().scanCodePayWindow(
                        getActivity(),
                        ResourceType.SKIN,
                        bean.getId(),
                        bean.getAppName(),
                        String.valueOf(bean.getDiscountPrice()),
                        bean.getDiscountScorePrice(),
                        false,
                        getPayResultCallback(bean));
                break;
            case ShopContract.Pay
                    .RMB:
                PayHandler.getInstance().scanCodePayWindow(
                        getActivity(),
                        ResourceType.SKIN,
                        bean.getId(),
                        bean.getAppName(),
                        String.valueOf(bean.getDiscountPrice()),
                        0,
                        true,
                        getPayResultCallback(bean));
                break;
        }
    }

    private PaySuccessResultCallback getPayResultCallback(final SkinVersionsBean bean) {
        return new PaySuccessResultCallback() {
            @Override
            public void confirm() {
                manualUpdateTrack(bean.toTrackString(), EventConstant.NormalClick.ACTION_BUY_CONFIRM);
                dealAfterSkinBuyed(bean);
            }

            @Override
            public void cancel() {
                manualUpdateTrack(bean.toTrackString(), EventConstant.NormalClick.ACTION_BUY_CANCEL);
            }
        };
    }

    private void request(@ThemeContract.SortRule String sortRule, int type) {
        mSortRule = sortRule;
        mCurType = type;
        if (type != ShopContract.PriceType.SCORE) {
            mCarCoinSortImage.getDrawable().setLevel(0);
            mCarCoinSortImage.setSelected(false);
        }
        if (type != ShopContract.PriceType.CASH) {
            mCashPriceImage.getDrawable().setLevel(0);
            mCashPriceImage.setSelected(false);
        }
        if (checkNet()) {
            mNewFetchT = true;
            setEnableLoadMore(false);
            showProgressDialog(R.string.loading);
            mVM.fetchSystemSkins(sortRule, type);
        }
    }

    private void refreshLoadingFromOut() {
        // TODO: 暂时注释
        /*if (ProgressCenterControllerProxy.getInstance().isExecutingTask()) {
            List<SkinVersionsBean> data = mAdapter.getData();
            String urlCache = ProgressCenterControllerProxy.getInstance().getUrlCache();
            //避免加载更多之后,还需要重复判断是否加载了正在加载中的
            ProgressCenterControllerProxy.getInstance().release();

            if (!ListUtils.isEmpty(data)) {
                for (int i = 0, n = data.size(); i < n; i++) {
                    SkinVersionsBean skinVersionsBean = data.get(i);
                    if (skinVersionsBean.isIsBuy()
                            && Objects.equals(skinVersionsBean.getUrl(), urlCache)) {
                        // TODO:
//                        mAdapter.downloadSkinAtPosition(i, false);
                        break;
                    }
                }
            }
        }*/
    }

    private void shouldShowGuideWindow() {
        if (!GuideDataHelper.shouldShowGuide(getContext(), GuideConstants.SHOP_SHOWED, GuideConstants.SHOP_GUIDE_FIRST, true))
            return;
        showGuideWindow();
        mRv.postDelayed(new Runnable() {
            @Override
            public void run() {
                justifyGuideTargetViewEmpty();
            }
        }, 500);
    }

    private void justifyGuideTargetViewEmpty() {
        View view = mRv.getLayoutManager().findViewByPosition(0);
        if (view == null) {
            initHandlerAndRunnable();
            handler.postDelayed(runnable, 100);
            return;
        }
        startGuide(view);
    }

    private void startGuide(final View view) {
        view.post(new Runnable() {
            @Override
            public void run() {
                if (newGuide != null) {
                    newGuide.setTargetViewAndRect(view);
                    newGuide.startGuide();
                }
            }
        });
    }

    private void initHandlerAndRunnable() {
        if (handler == null)
            handler = new Handler();
        if (runnable == null)
            runnable = new Runnable() {
                @Override
                public void run() {
                    justifyGuideTargetViewEmpty();
                }
            };
    }

    private void showGuideWindow() {
        newGuide = NewGuide.with(getActivity())
                .setLebal(GuideConstants.SHOP_SHOWED)
                .setGuideLayoutId(R.layout.guide_view_skin)
                .setNeedHande(true)
                .setNeedShake(true)
                .setHandLocation(NewGuide.RIGHT_AND_BOTTOM_TOP)
                .setViewHandId(R.id.iv_gesture)
                .setViewWaveIdOne(R.id.iv_wave_one)
                .setViewWaveIdTwo(R.id.iv_wave_two)
                .setViewWaveIdThree(R.id.iv_wave_three)
                .setViewSkipId(R.id.tv_guide_skip)
                .build();
        newGuide.addGuideWindow();
        GuideDataHelper.setFirstGuideFalse(GuideConstants.SHOP_SHOWED);
    }

    private void dismissGuideWindow() {
        if (handler != null && runnable != null) {
            handler.removeCallbacks(runnable);
            handler = null;
            runnable = null;
        }
        if (newGuide != null) {
            newGuide.dismissGuideWindow();
            newGuide = null;
        }
    }

    private void finishGuide() {
        dismissGuideWindow();
        GuideDataHelper.finishGuideData(GuideConstants.SHOP_SHOWED);
        TPUtils.put(getContext(), GuideConstants.SHOP_SHOWED, true);
        TPUtils.put(getContext(), GuideConstants.SHOP_GUIDE_FIRST, false);
    }

    private void manualUpdateTrack(String content, String eventAction) {
        XmAutoTracker.getInstance().onEvent(eventAction, content, this.getClass().getName(), EventConstant.PageDesc.FRAGMENT_SYSTEM_THEME);
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
        request(mSortRule, mCurType);
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

    protected PaySuccessResultCallback getPaySuccessCallback(long productId) {
        if (mAdapter == null || ListUtils.isEmpty(mAdapter.getData())) return retInitCallback(productId);
        final SkinVersionsBean skinVersionsBean = mAdapter.searchItemByProductId(productId);
        if (skinVersionsBean == null) return retInitCallback(productId);
        return new PaySuccessResultCallback() {
            @Override
            public void confirm() {
                NotifyUpdateOrderInfo.sendNotifyMsg();
                dealAfterSkinBuyed(skinVersionsBean);
            }

            @Override
            public void cancel() {

            }
        };
    }


    @Override
    protected boolean isFlow() {
        return false;
    }
    @Override
    protected int requestResourceType() {
        return ResourceType.SKIN;
    }
}

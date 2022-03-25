package com.xiaoma.shop.business.ui.hologram;

import android.app.DownloadManager;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.LevelListDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.fsl.android.uniqueota.UniqueOtaConstants;
import com.xiaoma.autotracker.XmAutoTracker;
import com.xiaoma.autotracker.XmTracker;
import com.xiaoma.autotracker.model.TrackerCountType;
import com.xiaoma.component.AppHolder;
import com.xiaoma.config.ConfigManager;
import com.xiaoma.image.ImageLoader;
import com.xiaoma.model.ResultCallback;
import com.xiaoma.model.XMResult;
import com.xiaoma.model.XmResource;
import com.xiaoma.shop.R;
import com.xiaoma.shop.business.adapter.HologramAdapter;
import com.xiaoma.shop.business.download.DownloadListener;
import com.xiaoma.shop.business.download.DownloadStatus;
import com.xiaoma.shop.business.download.impl.HologramDownload;
import com.xiaoma.shop.business.hologram.HologramUsing;
import com.xiaoma.shop.business.model.HoloListModel;
import com.xiaoma.shop.business.model.PayInfo;
import com.xiaoma.shop.business.pay.PayHandler;
import com.xiaoma.shop.business.pay.PaySuccessResultCallback;
import com.xiaoma.shop.business.ui.theme.AbsShopFragment;
import com.xiaoma.shop.business.ui.view.ProgressButton;
import com.xiaoma.shop.business.vm.MainHoloVM;
import com.xiaoma.shop.common.RequestManager;
import com.xiaoma.shop.common.callback.OnPayFromPersonalCallback;
import com.xiaoma.shop.common.constant.Hologram3DContract;
import com.xiaoma.shop.common.constant.ResourceType;
import com.xiaoma.shop.common.constant.ShopContract;
import com.xiaoma.shop.common.constant.ThemeContract;
import com.xiaoma.shop.common.manager.update.OnUpdateCallback;
import com.xiaoma.shop.common.manager.update.UpdateOtaInfo;
import com.xiaoma.shop.common.manager.update.UpdateOtaManager;
import com.xiaoma.shop.common.track.EventConstant;
import com.xiaoma.shop.common.track.ShopTrackManager;
import com.xiaoma.shop.common.util.Hologram3DUtils;
import com.xiaoma.shop.common.util.NotifyUpdateOrderInfo;
import com.xiaoma.shop.common.util.PriceUtils;
import com.xiaoma.thread.Priority;
import com.xiaoma.thread.SeriesAsyncWorker;
import com.xiaoma.thread.Work;
import com.xiaoma.ui.StateControl.Type;
import com.xiaoma.ui.toast.XMToast;
import com.xiaoma.utils.DoubleClickUtils;
import com.xiaoma.utils.FileUtils;
import com.xiaoma.utils.ListUtils;
import com.xiaoma.utils.NetworkUtils;
import com.xiaoma.utils.ResUtils;
import com.xiaoma.utils.log.KLog;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * <pre>
 *     author : wutao
 *     time   : 2019/01/24
 *     desc   : 全息影像
 * </pre>
 */
public class MainHologramFragment extends AbsShopFragment<HologramAdapter> implements BaseQuickAdapter.OnItemChildClickListener, OnPayFromPersonalCallback {
    private boolean mNewFetchT = true;
    private MainHoloVM mVM;
    private FrameLayout.LayoutParams mLayoutParams;
    private PayHandler.PayCallback mPayCallback;
    private BroadcastReceiver mReceiver;

    public static MainHologramFragment newInstance() {
        return new MainHologramFragment();
    }

    @Override
    protected HologramAdapter getAdapter() {
        return new HologramAdapter(ImageLoader.with(this));
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mLayoutParams = (FrameLayout.LayoutParams) rootLayout.getLayoutParams();
        mLayoutParams.topMargin = 168;
        rootLayout.setLayoutParams(mLayoutParams);
        initAdapterListener();
        initVM();
        initData();
        PayHandler.getInstance().addPayCallback(mPayCallback = new PayHandler.PayCallback() {
            @Override
            public void onPaySuccess(boolean payWithQRCode, final long skuId) {
                SeriesAsyncWorker.create().next(new Work(Priority.HIGH) {
                    @Override
                    public void doWork(Object lastResult) {
                        if (isDestroy() || mAdapter == null)
                            return;
                        List<HoloListModel> holoList = new ArrayList<>(mAdapter.getData());
                        for (HoloListModel model : holoList) {
                            if (model.getId() == skuId) {
                                model.setUserBuyFlag(1);
                            }
                        }
                        doNext();
                    }
                }).next(new Work() {
                    @Override
                    public void doWork(Object lastResult) {
                        if (isDestroy() || mAdapter == null)
                            return;
                        mAdapter.notifyDataSetChanged();
                    }
                }).start();
            }
        });
        IntentFilter intentFilter = new IntentFilter(HologramUsing.ACTION_ROLE_USING);
        if (getActivity() != null) {
            getActivity().registerReceiver(mReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    if (mAdapter != null) {
                        mAdapter.notifyDataSetChanged();
                    }
                }
            }, intentFilter);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        PayHandler.getInstance().removePayCallback(mPayCallback);
        if (getActivity() != null) {
            getActivity().unregisterReceiver(mReceiver);
        }
    }

    private void initAdapterListener() {
        mAdapter.setOnItemChildClickListener(this);
        registerListener();
    }

    private void initVM() {
        mVM = ViewModelProviders.of(this).get(MainHoloVM.class);
        mVM.getHoloListLiveData().observe(this, new Observer<XmResource<List<HoloListModel>>>() {
            @Override
            public void onChanged(@Nullable XmResource<List<HoloListModel>> listXmResource) {
                dismissProgress();
                listXmResource.handle(new OnCallback<List<HoloListModel>>() {
                    @Override
                    public void onSuccess(List<HoloListModel> data) {
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
                            handlePay(mPayInfo, true);
                            mPayInfo = null;
                        }
                    }

                    @Override
                    public void onFailure(String msg) {
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
                                showErrorView();
                            }
                        }
                    }
                });
            }
        });

        mVM.getLoadMoreStateLiveData().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer integer) {
                notifyLoadMoreState(integer);
            }
        });
    }

    private void initData() {
        if (!NetworkUtils.isConnected(mContext)) {
            showNoNetView();
            return;
        }
        showContentView();
        mSortRule = ThemeContract.SortRule.INTEGRATED;
        clickComplex();

    }

    @Override
    protected void loadMore() {
        mNewFetchT = false;
        mVM.loadMore();
    }

    @Override
    protected void showNoNetView() {
        super.showNoNetView();
        mLayoutParams.topMargin = 0;
        rootLayout.setLayoutParams(mLayoutParams);
        tabVisible(false);
    }

    @Override
    protected void showContentView() {
        super.showContentView();
        mLayoutParams.topMargin = 168;
        rootLayout.setLayoutParams(mLayoutParams);
        tabVisible(true);
    }

    @Override
    public void onRetryClick(View view, Type type) {
        initData();
    }


    @Override
    protected boolean isFlow() {
        return false;
    }

    @Override
    protected int requestResourceType() {
        return ResourceType.HOLOGRAM;
    }

    @Override
    protected void noNetworkOnRetry() {
        initData();
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

    private void request(@ThemeContract.SortRule String sortRule, int type) {
        mSortRule = sortRule;
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
            showProgressDialog(R.string.loading);
            mVM.fetchHolograms(sortRule, type);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mAdapter.notifyAtMain();
    }

    @Override
    public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
        HoloListModel item = mAdapter.getItem(position);
        Context context = view.getContext();
        switch (view.getId()) {
            case R.id.pbBtnCenter:
                if (DoubleClickUtils.isFastDoubleClick(500)) return;
                if (item != null) {
                    if (item.get3DState() != null) {
                        handle3DClick(item, position);
                    } else {
                        if (item.getUserBuyFlag() == 1
                                || PriceUtils.isFree(item.getDiscountPrice(), item.getDiscountScorePrice(), true)) {
                            manualUpdateTrack(item.toTrackString(), EventConstant.NormalClick.ACTION_USE);
                            HologramUsing.useRole(context, item);
                        } else {
                            manualUpdateTrack(item.toTrackString(), EventConstant.NormalClick.ACTION_BUY);

                            ShopTrackManager.newSingleton().setBaseInfo(ResourceType.HOLOGRAM, item.toTrackString(), this.getClass().getName());
//                        ShopTrackManager.newSingleton().manualUpdateEvent(TrackerEventType.ONCLICK, EventConstant.NormalClick.ACTION_BUY);
                            int saleScorePrice = item.getDiscountScorePrice();
                            double saleRMBPrice = item.getDiscountPrice();

                            if (saleRMBPrice > 0) {
                                payByScanCode(item.getId(), item.getName(),
                                        saleScorePrice, saleRMBPrice,
                                        getPayResultCallback(position, item, (ProgressButton) view));
                            } else {
                                PayHandler.getInstance().carCoinPayWindow(getActivity(), ResourceType.HOLOGRAM, item.getId(),
                                        item.getName(), saleScorePrice, false, getPayResultCallback(position, item, (ProgressButton) view));
                            }
                        }
                    }
                }
                break;
            case R.id.iv_preview_image:
            default:
                manualUpdateTrack(item == null ? "N/A" : item.toTrackString(), EventConstant.NormalClick.ACTION_HOLOGRAM_COVER);
                if (item == null || item.getId() <= 0) {
                    return;
                } else {
                    HologramDetailActivity.newInstanceActivity(mContext, item);
                }
                break;
        }
    }

    private void handle3DClick(final HoloListModel item, final int pos) {
        Log.d(TAG, "handle3DClick: state => " + item.getState());
        switch (item.getState()) {
            case Hologram3DContract.STATE_DOWNLOAD_PROGRESS:
                XMToast.showToast(mContext, R.string.update_downloading);
                break;
            case Hologram3DContract.STATE_DOWNLOAD_SUCCESS:
            case Hologram3DContract.STATE_INSTALL_SUCCESS:
                if (mAdapter != null) {
                    mAdapter.notifyItemChanged(pos);
                }
                UpdateOtaManager.getInstance().soundUpgrade(UniqueOtaConstants.EcuId.ROBOT);
                break;
            case Hologram3DContract.STATE_DOWNLOAD:
            case Hologram3DContract.STATE_DOWNLOAD_FAIL:
                downloadSelfView(item, pos);
                break;
            case Hologram3DContract.STATE_UPDATE:
                downloadSelfView(item, pos);
                break;
        }
    }

    private void downloadSelfView(final HoloListModel item, final int pos) {
        if (mAdapter != null) {
            mAdapter.notifyItemChanged(pos);
        }
        registerListener();
        HologramDownload.newSingleton().start(item);
    }

    private void registerListener() {
        HologramDownload.newSingleton().addDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStatus(@Nullable final DownloadStatus downloadStatus) {
                if (downloadStatus == null) return;
                switch (downloadStatus.status) {
                    case DownloadManager.STATUS_RUNNING:
                        if (mAdapter != null) {
                            mAdapter.notifyAtMain();
                        }

                        break;
                    case DownloadManager.STATUS_FAILED:
                        XMToast.toastException(mContext, R.string.hint_download_error);
                        if (mAdapter != null) {
                            mAdapter.notifyAtMain();
                        }

                        HologramDownload.newSingleton().removeDownloadListener(this);
                        break;
                    case DownloadManager.STATUS_SUCCESSFUL:
                        if (!Hologram3DUtils.getUrl().equals(downloadStatus.downUrl)) {
                            File destFile = new File(ConfigManager.FileConfig.get3DFolder().getPath(), HologramDownload.FILE_HOLOGRAM_3D);
                            FileUtils.delete(destFile);
                            FileUtils.move(new File(downloadStatus.downFilePath), destFile);
                            Hologram3DUtils.saveUrl(downloadStatus.downUrl);
                        }
                        HologramDownload.newSingleton().removeDownloadListener(this);
                        if (mAdapter != null) {
                            mAdapter.notifyAtMain();
                        }
                        break;
                }
            }
        });
        UpdateOtaManager.getInstance().registerCallback(new OnUpdateCallback(UniqueOtaConstants.EcuId.ROBOT) {

            @Override
            public void notifyDataSetChange(final UpdateOtaInfo info) {
                if (info == null) return;
                if (mAdapter != null) {
                    mAdapter.notifyAtMain();
                }
            }

            @Override
            public void onSuccess(UpdateOtaInfo info) {
                if (info == null) return;
                int state = info.getInstallState();
                KLog.i("filOut| " + "[onSuccess]->InstallResult -> " + state);
                if (state == UpdateOtaInfo.InstallState.INSTALL_SUCCESSFUL) {
                    UpdateOtaManager.getInstance().unRegisterCallback(this);
                    if (mAdapter != null) {
                        mAdapter.notifyAtMain();
                    }
                }
            }

            @Override
            public void onFailure(UpdateOtaInfo info) {
                if (info == null) return;
                XMToast.showToast(AppHolder.getInstance().getAppContext(), R.string.state_error_update_3d);
                if (mAdapter != null) {
                    mAdapter.notifyAtMain();
                }
            }
        });
    }

    private void payByScanCode(int id, String name, int scorePrice, double rmbPrice, PaySuccessResultCallback callback) {
        PayHandler.getInstance().scanCodePayWindow(getActivity(),
                ResourceType.HOLOGRAM, id, name,
                String.valueOf(rmbPrice),
                scorePrice, scorePrice == 0, callback);
    }

    private PaySuccessResultCallback getPayResultCallback(final int position, final HoloListModel bean, final ProgressButton pbBtn) {
        return new PaySuccessResultCallback() {
            @Override
            public void confirm() {
                dealAfterBuy(bean, pbBtn, position);
            }

            @Override
            public void cancel() {

            }
        };
    }

    private void dealAfterBuy(final HoloListModel bean, ProgressButton pbBtn, final int position) {
        bean.setUserBuyFlag(1);
        pbBtn.updateText("使用");
        XmTracker.getInstance().uploadEvent(-1, TrackerCountType.BUYHOLOGRAM.getType());
        XMToast.showToast(mActivity, ResUtils.getString(mActivity, R.string.purchase_success));
        RequestManager.addUseNum(ResourceType.HOLOGRAM, bean.getId(), new ResultCallback<XMResult<String>>() {
            @Override
            public void onSuccess(XMResult<String> result) {
                bean.setUsedNum(bean.getUsedNum() + 1);
                mAdapter.notifyUseCount(position, bean.getUsedNum() + bean.getDefaultUsedNum());
                HologramDetailActivity.newInstanceActivity(mContext, bean);
            }

            @Override
            public void onFailure(int code, String msg) {

            }
        });
    }

    private void manualUpdateTrack(String content, String eventAction) {
        XmAutoTracker.getInstance().onEvent(eventAction, content, this.getClass().getName(), EventConstant.PageDesc.FRAGMENT_HOLOGRAM);
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
    protected PaySuccessResultCallback getPaySuccessCallback(long productId) {
        if (mAdapter == null || ListUtils.isEmpty(mAdapter.getData()))
            return retInitCallback(productId);
        final HoloListModel holoListModel = mAdapter.searchItemByProductId(productId);
        if (holoListModel == null) return retInitCallback(productId);
        return new PaySuccessResultCallback() {
            @Override
            public void confirm() {
                NotifyUpdateOrderInfo.sendNotifyMsg();
                holoListModel.setUserBuyFlag(1);
                mAdapter.notifyDataSetChanged();
                XmTracker.getInstance().uploadEvent(-1, TrackerCountType.BUYHOLOGRAM.getType());
                XMToast.showToast(mActivity, ResUtils.getString(mActivity, R.string.purchase_success));
                RequestManager.addUseNum(ResourceType.HOLOGRAM, holoListModel.getId(), new ResultCallback<XMResult<String>>() {
                    @Override
                    public void onSuccess(XMResult<String> result) {
                        holoListModel.setUsedNum(holoListModel.getUsedNum() + 1);
                        mAdapter.notifyDataSetChanged();
                        HologramDetailActivity.newInstanceActivity(mContext, holoListModel);
                    }

                    @Override
                    public void onFailure(int code, String msg) {

                    }
                });
            }

            @Override
            public void cancel() {

            }
        };
    }
}


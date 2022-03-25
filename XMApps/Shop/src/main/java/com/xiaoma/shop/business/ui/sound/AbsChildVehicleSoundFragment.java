package com.xiaoma.shop.business.ui.sound;

import android.app.DownloadManager;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.graphics.drawable.LevelListDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.xiaoma.autotracker.XmAutoTracker;
import com.xiaoma.autotracker.XmTracker;
import com.xiaoma.autotracker.model.TrackerCountType;
import com.xiaoma.model.ResultCallback;
import com.xiaoma.model.XMResult;
import com.xiaoma.model.XmResource;
import com.xiaoma.shop.R;
import com.xiaoma.shop.business.adapter.VehicleSoundAdapterV2;
import com.xiaoma.shop.business.download.DownloadListener;
import com.xiaoma.shop.business.download.DownloadStatus;
import com.xiaoma.shop.business.download.impl.HUSoundEffDownload;
import com.xiaoma.shop.business.download.impl.LCDSoundEffDownload;
import com.xiaoma.shop.business.download.impl.SoundEffDownload;
import com.xiaoma.shop.business.model.PayInfo;
import com.xiaoma.shop.business.model.VehicleSoundEntity;
import com.xiaoma.shop.business.pay.PayHandler;
import com.xiaoma.shop.business.pay.PaySuccessResultCallback;
import com.xiaoma.shop.business.ui.theme.AbsShopFragment;
import com.xiaoma.shop.business.vm.VehicleSoundVm;
import com.xiaoma.shop.common.RequestManager;
import com.xiaoma.shop.common.callback.DialogCommonCallbackImpl;
import com.xiaoma.shop.common.callback.OnPayFromPersonalCallback;
import com.xiaoma.shop.common.callback.OnRefreshCallback;
import com.xiaoma.shop.common.constant.ResourceType;
import com.xiaoma.shop.common.constant.ShopContract;
import com.xiaoma.shop.common.constant.ThemeContract;
import com.xiaoma.shop.common.constant.VehicleSoundType;
import com.xiaoma.shop.common.manager.update.OnUpdateCallback;
import com.xiaoma.shop.common.manager.update.UpdateOtaInfo;
import com.xiaoma.shop.common.manager.update.UpdateOtaManager;
import com.xiaoma.shop.common.track.EventConstant;
import com.xiaoma.shop.common.track.ShopTrackManager;
import com.xiaoma.shop.common.util.AudioAuditionHelper;
import com.xiaoma.shop.common.util.NotifyUpdateOrderInfo;
import com.xiaoma.shop.common.util.UpdateOtaUtils;
import com.xiaoma.shop.common.util.VehicleSoundUtils;
import com.xiaoma.thread.ThreadDispatcher;
import com.xiaoma.ui.StateControl.Type;
import com.xiaoma.ui.dialog.XmDialog;
import com.xiaoma.ui.toast.XMToast;
import com.xiaoma.utils.ListUtils;
import com.xiaoma.utils.NetworkUtils;
import com.xiaoma.utils.log.KLog;

import java.util.List;
import java.util.Objects;

/**
 * @Author: LiangJingBo.
 * @Date: 2019/05/29
 * @Describe:
 */

public abstract class AbsChildVehicleSoundFragment extends AbsShopFragment<VehicleSoundAdapterV2> implements OnPayFromPersonalCallback {
    protected VehicleSoundVm mVm;
    protected int mCurType = -1;
    protected boolean mNewFetchT;

    private boolean mIsHidden;
    private AudioAuditionHelper mAuditionHelper;
    private DownloadListener mDownloadListener;
    private XmDialog xmDialog;
    private OnRefreshCallback onRefreshCallback;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initVm();
        initEvent();
        mSortRule = ThemeContract.SortRule.INTEGRATED; //第一次默认使用综合排序
        initData();
        registerDownloadListener();
        registerUpdateListener();
        registerRefreshListener();
    }

    private void registerRefreshListener() {
        getSoundEffDownloader().addRefreshCallback(onRefreshCallback = new OnRefreshCallback() {
            @Override
            public void onSingleRefresh(long id, String filePath) {
                if (isDestroy() || mAdapter == null || ListUtils.isEmpty(mAdapter.getData())) return;
                int pos = VehicleSoundUtils.findPositionByUrl(mAdapter.getData(), filePath);
                if (pos >= 0) {
                    mAdapter.getData().get(pos).setDownloadNum(mAdapter.getData().get(pos).getDownloadNum() + 1);
                    mAdapter.notifyItemChanged(pos);
                }
            }

            @Override
            public void onRefreshAll() {
                if (isDestroy() || mAdapter == null || ListUtils.isEmpty(mAdapter.getData())) return;
                mAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        UpdateOtaManager.getInstance().unRegisterCallback(onUpdateCallback);
        getSoundEffDownloader().removeDownloadListener(mDownloadListener);
        getSoundEffDownloader().removeRefreshCallback(onRefreshCallback);
    }


    @Override
    protected VehicleSoundAdapterV2 getAdapter() {
        return new VehicleSoundAdapterV2(requestResourceType(), new VehicleSoundAdapterV2.Callback() {
            @Override
            public boolean isAuditionPlaying(VehicleSoundEntity.SoundEffectListBean item) {
                if (!isCurAuditionPlaying())
                    return false;
                Uri playingUri = Uri.parse(getCurAuditionUrl());
                return Objects.equals(playingUri.getQueryParameter("id"), String.valueOf(item.getId()));
//                return Objects.equals(getCurAuditionUrl(),item.getAuditionPath());
            }

            @Override
            public void startAudition(VehicleSoundEntity.SoundEffectListBean item) {
                if (!NetworkUtils.isConnected(getContext())) {
                    showToastException(R.string.no_network);
                    return;
                }
                if (URLUtil.isValidUrl(item.getAuditionPath())) {
                    manualUpdateTrack(item.toTrackString(), EventConstant.NormalClick.ACTION_PLAY_PAUSE);
                    String url = item.getAuditionPath() + "?id=" + item.getId();
                    initAuditionAndPlay(url);
                    callNotifyDataSetChanged();
                } else {
                    XMToast.toastException(mContext, R.string.invalid_audition_url);
                }

            }

            @Override
            public void stopAudition(VehicleSoundEntity.SoundEffectListBean item) {
                manualUpdateTrack(item.toTrackString(), EventConstant.NormalClick.ACTION_PLAY_PAUSE);
                releaseAudition();
                callNotifyDataSetChanged();
            }
        }, getEcu());
    }

    protected void initVm() {
        mVm = ViewModelProviders.of(this).get(VehicleSoundVm.class);
        mVm.getVehicleSoundEntity().observe(this, new Observer<XmResource<List<VehicleSoundEntity.SoundEffectListBean>>>() {
            @Override
            public void onChanged(@Nullable XmResource<List<VehicleSoundEntity.SoundEffectListBean>> listXmResource) {
                dismissProgress();
                if (listXmResource == null) return;
                listXmResource.handle(new OnCallback<List<VehicleSoundEntity.SoundEffectListBean>>() {
                    @Override
                    public void onSuccess(List<VehicleSoundEntity.SoundEffectListBean> data) {
                        showContentView();
                        showParentContent();
                        if (mNewFetchT) {// new data
                            if (ListUtils.isEmpty(data)) {
                                setEnableLoadMore(false);
                            } else {
                                setEnableLoadMore(data.size() >= 20);
                            }
                            mAdapter.setNewData(data);
                            scrollTo(0);
                            //                            mAdapter.getRecyclerView().scrollToPosition(0);
                        } else {// append data
                            mAdapter.addData(data);
                        }
                        if (mPayInfo != null) {
                            handlePay(mPayInfo,true);
                            mPayInfo=null;
                        }
                    }

                    @Override
                    public void onFailure(String msg) {
                        showParentContent();
                        if (mNewFetchT || ListUtils.isEmpty(mAdapter.getData())) {
                            showEmptyView();
                            mAdapter.setNewData(null);
                        }
                    }

                    @Override
                    public void onError(int code, String message) {
                        if (mNewFetchT || ListUtils.isEmpty(mAdapter.getData())) {
                            mAdapter.setNewData(null);
                            if (!NetworkUtils.isConnected(mActivity)|| getString(R.string.network_error).equals(message)) {//没有网络
                                showNoNetView();
                                XMToast.toastException(mContext, mContext.getString(R.string.network_anomaly));
                            } else {
                                showParentContent();
                                showErrorView();
                                XMToast.toastException(mContext, message);
                            }
                        }else{
                            showParentContent();
                        }
                    }
                });
            }
        });
        mVm.getLoadStatus().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer integer) {
                dismissProgress();
                notifyLoadMoreState(integer);
            }
        });
    }

    @Override
    protected void loadMore() {// load more
        mNewFetchT = false;
        mVm.fetchLoadMore(getProductType());
    }

    protected void initData() {
        if (!NetworkUtils.isConnected(mContext)) {
            showNoNetView();
            return;
        }
        showContentView();
        fetchVehicleSounds(getProductType(), mSortRule, mCurType);
    }


    protected void initEvent() {
        mAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, final int position) {
                switch (view.getId()) {
                    case R.id.pbBtnCenter://购买、使用
                        VehicleSoundEntity.SoundEffectListBean bean = mAdapter.getData().get(position);
                        if (bean.isBuy()) {//如果已经购买，才能到下载流程
                            //判断是否已经下载，如果已经下载了就使用，否则下载

                            SoundEffDownload download = getSoundEffDownloader();
                            int status = VehicleSoundUtils.theResIsDownloaded(bean, requestResourceType(), download);
                            if (status == VehicleSoundType.DownloadStatus.COMPLETE) {
                                manualUpdateTrack(bean.toTrackString(), EventConstant.NormalClick.ACTION_USE);
                                handleDownloadComplete(position, null,true);
                            } else {
                                if (status == VehicleSoundType.DownloadStatus.UPDATE) {
                                    manualUpdateTrack(bean.toTrackString(), EventConstant.NormalClick.ACTION_UPDATE);
                                } else {
                                    manualUpdateTrack(bean.toTrackString(), EventConstant.NormalClick.ACTION_DOWNLOAD);
                                }

                                doDownload(download, bean);
                            }
                           /* if (download.isDownloadSuccess(bean)) {
                                handleDownloadComplete(position, null);
                            } else {
                                download.start(bean);
                            }*/
                        } else {//前去购买
                            manualUpdateTrack(bean.toTrackString(), EventConstant.NormalClick.ACTION_BUY);
                            ShopTrackManager.newSingleton().setBaseInfo(ResourceType.VEHICLE_SOUND, bean.toTrackString(), this.getClass().getName());
                            VehicleSoundUtils.buyProduct(mAdapter.getData().get(position),
                                    requestResourceType(),
                                    mActivity,
                                    getPayResultCallback(position, mAdapter.getData().get(position)));
                        }
                        break;
                }
            }
        });
    }

    private void doDownload(final SoundEffDownload download, final VehicleSoundEntity.SoundEffectListBean item) {
        RequestManager.addSkuToBuyList(item.getId(), requestResourceType(), new ResultCallback<XMResult<Object>>() {
            @Override
            public void onSuccess(XMResult<Object> result) {
                if (isDestroy())
                    return;
                if (result != null && result.isSuccess()) {
                    download.start(item);
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


    private PaySuccessResultCallback getPayResultCallback(final int pos, final VehicleSoundEntity.SoundEffectListBean bean) {
        return new PaySuccessResultCallback() {
            @Override
            public void confirm() {
                paySuccess(pos, bean);
            }

            @Override
            public void cancel() {

            }
        };
    }

    //支付成功
    private void paySuccess(final int pos, final VehicleSoundEntity.SoundEffectListBean bean) {
        // 刷新条目状态
        bean.setBuy(true);
        XmTracker.getInstance().uploadEvent(-1, TrackerCountType.BUYVOICE.getType());
        mAdapter.notifyItemChanged(pos);//刷新购买状态
        // 显示购买成功弹窗
        PayHandler.getInstance().showCommonDialog(mActivity, new DialogCommonCallbackImpl() {

            @Override
            public void prepare(View contentView) {
                TextView coinText = contentView.findViewById(R.id.tv_pay_message);
                TextView confirmBt = contentView.findViewById(R.id.confirm_bt);
                TextView cancelBt = contentView.findViewById(R.id.cancel_bt);
                coinText.setText(R.string.congratulations_purchase_success);
                confirmBt.setText(R.string.now_download_resource);
                cancelBt.setText(R.string.not_download_resource);
            }

            @Override
            public void onConfirm() {
                SoundEffDownload download = getSoundEffDownloader();
                if (download.isDownloadSuccess(bean)) {
                    handleDownloadComplete(pos, null,false);
                } else {
                    doDownload(download, bean);
                }
            }
        });
    }


    @Override
    protected void showNoNetView() {// show parent no network view
        if (getParentFragment() != null) {
            Fragment fragment = getParentFragment();
            if (fragment instanceof VehicleSoundFragment) {
                ((VehicleSoundFragment) fragment).parentShowNoNetView(getProductType());
            }
        }
    }

    public void showParentContent(){
        if ( getParentFragment() instanceof VehicleSoundFragment) {
            ((VehicleSoundFragment) getParentFragment()).parentShowContentView();
        }
    }

    @Override
    public void onRetryClick(View view, Type type) {// error : try again
        fetchVehicleSounds(getProductType(), mSortRule, mCurType);
    }

    @Override
    protected void clickComplex() {//综合
        fetchVehicleSounds(getProductType(), ThemeContract.SortRule.INTEGRATED, -1);
    }

    @Override
    protected void clickSalesVolume() {//销量
        fetchVehicleSounds(getProductType(), ThemeContract.SortRule.VEHICLE_SOUND_SALES_COUNT, -1);
    }

    @Override
    protected void clickShelf() {//最近上架
        fetchVehicleSounds(getProductType(), ThemeContract.SortRule.LATEST, -1);
    }

    @Override
    protected void clickCoinPrice() {//车币价
        if (mBtCoinPrice.isSelected()) {
            fetchVehicleSounds(getProductType(), mCarCoinSortImage.isSelected() ? ThemeContract.SortRule.SCORE_DESC : ThemeContract.SortRule.SCORE_ASC, ShopContract.PriceType.SCORE);
            mCarCoinSortImage.setSelected(!mCarCoinSortImage.isSelected());
        } else {
            fetchVehicleSounds(getProductType(), mCarCoinSortImage.isSelected() ? ThemeContract.SortRule.SCORE_ASC : ThemeContract.SortRule.SCORE_DESC, ShopContract.PriceType.SCORE);
        }
        LevelListDrawable priceUpDown = (LevelListDrawable) mCarCoinSortImage.getDrawable();
        if (mCarCoinSortImage.isSelected()) {
            priceUpDown.setLevel(2);
        } else {
            priceUpDown.setLevel(1);
        }
    }

    @Override
    protected void clickCashPrice() {//现金价
        if (mBtCashPrice.isSelected()) {
            fetchVehicleSounds(getProductType(), mCashPriceImage.isSelected() ? ThemeContract.SortRule.RMB_DESC : ThemeContract.SortRule.RMB_ASC, ShopContract.PriceType.CASH);
            mCashPriceImage.setSelected(!mCashPriceImage.isSelected());
        } else {
            fetchVehicleSounds(getProductType(), mCashPriceImage.isSelected() ? ThemeContract.SortRule.RMB_ASC : ThemeContract.SortRule.RMB_DESC, ShopContract.PriceType.CASH);
        }
        LevelListDrawable priceUpDown = (LevelListDrawable) mCashPriceImage.getDrawable();
        if (mCashPriceImage.isSelected()) {
            priceUpDown.setLevel(2);
        } else {
            priceUpDown.setLevel(1);
        }
    }

    protected void fetchVehicleSounds(@VehicleSoundType.ProductType String productType, @ThemeContract.SortRule String sortRule, int type) {
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
            showProgressDialog(R.string.loading);
            mVm.fetchVehicleSoundBeans(productType, sortRule, 0, type, VehicleSoundUtils.isPro() ? 1 : 0);
        }
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

    /**
     * 处理已经下载
     *
     * @param pos pos = -1 的时候需要通过adapter中当前下载的文件路径来判断下载的position
     */
    protected void handleDownloadComplete(int pos, String url,boolean executeImmediately) {
        //高配仪表 和 音响升级不需要重启也不需要条件判断
        if (xmDialog != null && xmDialog.isAdded()) {
            xmDialog.dismiss();
        }
        if(executeImmediately && (VehicleSoundUtils.isPro() || getProductType().equals(VehicleSoundType.ProductType.AUDIO_SOUND) )){
            beginUpdateVehicleSound(pos,url);
            return;
        }
        if (VehicleSoundUtils.isPro() || getProductType().equals(VehicleSoundType.ProductType.AUDIO_SOUND)) {//高配
            xmDialog = VehicleSoundUtils.buildProUpdateDialog(mActivity, getDownloadCompleteListener(pos, url));
        } else {
            xmDialog = VehicleSoundUtils.buildUpdateDialog(mActivity, getDownloadCompleteListener(pos, url));
        }
        if (mIsHidden) return;
        xmDialog.show();
    }

    protected VehicleSoundUtils.IOnDialogClickListener getDownloadCompleteListener(final int pos, final String url) {
        return new VehicleSoundUtils.IOnDialogClickListener() {
            @Override
            public void onConfirm() {
                beginUpdateVehicleSound(pos, url);
            }

            @Override
            public void onCancel() {

            }
        };
    }

    private void beginUpdateVehicleSound(int pos, String url) {
        if (requestResourceType() == ResourceType.INSTRUMENT_SOUND
                && !VehicleSoundUtils.canUseInstrumentSound()) {
            showIneligibleError();
            return;
        }
        if (UpdateOtaUtils.isExecuting()) {
            XMToast.showToast(mActivity, getString(R.string.please_wait));
            return;
        }
        int position = pos;
        if (position < 0) {
            position = VehicleSoundUtils.findPositionByUrl(mAdapter.getData(), url);
        }
        if (position < 0) return;
        updateVehicleSound(position);
    }

    protected void scrollTo(int position) {
        mAdapter.getRecyclerView().scrollToPosition(position);
    }

    protected void showUpdateError() {
        ThreadDispatcher.getDispatcher().postOnMain(new Runnable() {
            @Override
            public void run() {
                if (mAdapter != null) {
                    mAdapter.notifyDataSetChanged();
                }
                XMToast.toastException(mActivity, R.string.update_failed);
            }
        });
    }


    protected void showIneligibleError() {
        XMToast.toastException(mActivity, R.string.condition_not_satisfied);
    }

    protected void showUseSuccess() {
        XMToast.showToast(mActivity, R.string.successful_use);
    }

    /**
     * 更新音效文件
     *
     * @param pos
     */
    protected void updateVehicleSound(int pos) {
        if (pos == -1) return;
        replaceSound(pos);

    }

    /**
     * 替换音效
     */
    private void replaceSound(int pos) {
        UpdateOtaManager.getInstance().pushSoundFile(
                mAdapter.getData().get(pos).getFilePath(),
                requestResourceType(),
                requestUpdateResourceType(),
                getEcu());
    }


    private OnUpdateCallback onUpdateCallback;

    private void registerUpdateListener() {
        UpdateOtaManager.getInstance().registerCallback(onUpdateCallback = new OnUpdateCallback(getEcu()) {
            @Override
            public void notifyDataSetChange(final UpdateOtaInfo info) {
                if (!isDestroy() && mAdapter != null && info != null) {
                    ThreadDispatcher.getDispatcher().postOnMain(new Runnable() {
                        @Override
                        public void run() {
                            int position = VehicleSoundUtils.findPositionByUrl(mAdapter.getData(), info.getFileUrl());
                            if (position >= 0) {
                                mAdapter.notifyItemChanged(position);
                            }
                        }
                    });
                }
            }

            @Override
            public void onSuccess(UpdateOtaInfo info) {
                if (info == null) return;
                int state = info.getInstallState();
                switch (state) {
                    case UpdateOtaInfo.InstallState.INSTALL_SUCCESSFUL:
                        KLog.i("filOut| "+"[onSuccess]->InstallResult");
                        installSucceed();
                        break;
                }
            }

            @Override
            public void onFailure(UpdateOtaInfo info) {
                showUpdateError();
            }
        });
    }


    private void registerDownloadListener() {
        getSoundEffDownloader().addDownloadListener(mDownloadListener = new DownloadListener() {
            @Override
            public void onDownloadStatus(@Nullable DownloadStatus downloadStatus) {
                if (isDestroy() || downloadStatus == null || mAdapter == null)
                    return;
                int position = VehicleSoundUtils.findPositionByUrl(mAdapter.getData(), downloadStatus.downUrl);
                if (position >= 0) {
                    mAdapter.notifyItemChanged(position);
                }
                if (downloadStatus.status == DownloadManager.STATUS_FAILED) {//下载失败
                    XMToast.toastException(mActivity, R.string.hint_download_error);
                } else if (downloadStatus.status == DownloadManager.STATUS_SUCCESSFUL) {//下载成功
                    handleDownloadComplete(-1, downloadStatus.downUrl,false);
                }
            }
        });
    }


    private void installSucceed() {
        ThreadDispatcher.getDispatcher().postOnMain(new Runnable() {
            @Override
            public void run() {
                if (mAdapter != null) {
                    mAdapter.notifyDataSetChanged();
                }
                showUseSuccess();
            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        mIsHidden = true;
        releaseAudition();
    }

    @Override
    public void onResume() {
        super.onResume();
        mIsHidden = false;
    }

    private SoundEffDownload getSoundEffDownloader() {
        return ResourceType.VEHICLE_SOUND == requestResourceType() ?
                HUSoundEffDownload.getInstance() : LCDSoundEffDownload.getInstance();
    }

    private void initAuditionAndPlay(String audioUrl) {
        releaseAudition();
        mAuditionHelper = new AudioAuditionHelper();
        mAuditionHelper.init(getContext(), audioUrl, new AudioAuditionHelper.PlayCallback() {
            @Override
            public void onStart() {
                callNotifyDataSetChanged();
            }

            @Override
            public void onStop() {
                callNotifyDataSetChanged();
            }

            @Override
            public void onError() {
                callNotifyDataSetChanged();
                XMToast.toastException(mActivity, R.string.play_audio_error);
            }
        });
//        mAuditionHelper.start();
    }

    private void releaseAudition() {
        if (mAuditionHelper != null)
            mAuditionHelper.release();
        mAuditionHelper = null;
    }

    private String getCurAuditionUrl() {
        if (mAuditionHelper != null)
            return mAuditionHelper.getPlayUrl();
        return null;
    }

    private boolean isCurAuditionPlaying() {
        return mAuditionHelper != null && mAuditionHelper.isPlaying();
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

    protected void manualUpdateTrack(String content, String eventAction) {
        XmAutoTracker.getInstance().onEvent(eventAction, content, this.getClass().getName(), EventConstant.PageDesc.VOICE_WHOLE_CAR);
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
       final  VehicleSoundEntity.SoundEffectListBean item = mAdapter.searchItemByProductId(productId);
        if (item == null) return retInitCallback(productId);
        return new PaySuccessResultCallback() {
            @Override
            public void confirm() {
                NotifyUpdateOrderInfo.sendNotifyMsg();
                int pos = VehicleSoundUtils.findPositionByUrl(mAdapter.getData(),item.getFilePath());
                paySuccess(pos, item);
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


    protected abstract String getProductType();



    protected abstract int requestUpdateResourceType();

    protected abstract int getEcu();

}

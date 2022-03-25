//package com.xiaoma.shop.business.adapter;
//
//import android.support.annotation.NonNull;
//import android.support.v7.widget.RecyclerView;
//import android.widget.ImageView;
//
//import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
//import com.bumptech.glide.request.RequestOptions;
//import com.chad.library.adapter.base.BaseViewHolder;
//import com.xiaoma.config.ConfigManager;
//import com.xiaoma.image.ImageLoader;
//import com.xiaoma.model.ItemEvent;
//import com.xiaoma.network.db.DownloadManager;
//import com.xiaoma.shop.R;
//import com.xiaoma.shop.business.model.VehicleSoundEntity;
//import com.xiaoma.shop.business.ui.view.ProgressButton;
//import com.xiaoma.shop.common.constant.DownloadStatus;
//import com.xiaoma.shop.common.constant.ResourceType;
//import com.xiaoma.shop.common.constant.ShopContract;
//import com.xiaoma.shop.common.constant.VehicleSoundType;
//import com.xiaoma.shop.common.download.DownloadHelper;
//import com.xiaoma.shop.common.download.DownloadResourceCallback;
//import com.xiaoma.shop.common.download.proxy.OnRegisterCallback;
//import com.xiaoma.shop.common.download.proxy.ProgressCenterControllerProxy;
//import com.xiaoma.shop.common.util.UnitConverUtils;
//import com.xiaoma.shop.common.util.VehicleSoundDbUtils;
//import com.xiaoma.shop.common.util.VehicleSoundUtils;
//import com.xiaoma.ui.toast.XMToast;
//import com.xiaoma.utils.ListUtils;
//import com.xiaoma.utils.NetworkUtils;
//import com.xiaoma.utils.ResUtils;
//import com.xiaoma.utils.log.KLog;
//
//import java.io.File;
//import java.util.List;
//
///**
// * Created by Gillben
// * date: 2019/3/5 0005
// */
//public class VehicleSoundAdapter extends AbsShopAdapter<VehicleSoundEntity.SoundEffectListBean> {
//    public static final String TAG = VehicleSoundAdapter.class.getSimpleName();
//    @ResourceType
//    private int mType;
//    private int mCurrentUseSourcePosition = -1;
//    private VehicleSoundCallTask mTask;
//
//    private String justDownloadedUrl; //刚刚下载音效文件的路径
//    private String mCurrentPlayFilePath;//刚刚播放的音效文件的路径
//
//    public String getJustDownloadedUrl() {
//        return justDownloadedUrl;
//    }
//
//    public VehicleSoundAdapter(int type) {
//        mType = type;
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position, @NonNull List<Object> payloads) {
//        if (ListUtils.isEmpty(payloads)) {
//            super.onBindViewHolder(holder, position, payloads);
//        } else {
//            for (Object obj : payloads) {
//                if (obj instanceof VehicleSoundEntity.SoundEffectListBean) {
//                    if (position == mCurrentUseSourcePosition) {
//                        updatePb(holder, false, 0, ResUtils.getString(mContext, R.string.state_using));//使用中
//                    } else {
//                        int status = VehicleSoundUtils.theResIsDownloaded((VehicleSoundEntity.SoundEffectListBean) obj, mType);
//                        if (status == VehicleSoundType.DownloadStatus.COMPLETE) {
//                            updatePb(holder, true, 0, ResUtils.getString(mContext, R.string.state_use));//未被使用
//                        }
//                    }
//                }
//            }
//        }
//    }
//
//    @Override
//    protected void convert(BaseViewHolder helper, VehicleSoundEntity.SoundEffectListBean item) {
//        helper.setVisible(R.id.iv_test_play, true);
//        registerEvent(helper);
//        adapterNormalConfig(helper, item);
//        adapterPrice(helper, item);
//        adapterCenterPb(helper, item);
//        adapterTryPlay(helper, item);
//        adapterDownloadStateInSwitch(helper, item);
//    }
//
//    private void adapterDownloadStateInSwitch(BaseViewHolder helper, VehicleSoundEntity.SoundEffectListBean item) {
//        int status = DownloadHelper.getInstance().getStatus(item.getFilePath(), mType);
//        if ((status == DownloadStatus.LOADING || status == DownloadStatus.WAITING) && mTask != null && mTask.getSoundBean().getFilePath().equals(item.getFilePath())) {
//            mTask.setSoundBean(item);
//            mTask.setViewHolder(helper);
//        }
//    }
//
//    private void adapterTryPlay(BaseViewHolder helper, VehicleSoundEntity.SoundEffectListBean item) {
//        if (item.getFilePath().equals(mCurrentPlayFilePath)) {
//            item.setPlay(true);
//        }
//        ImageView controlIV = helper.getView(R.id.iv_test_play);
//        if (item.isPlay()) {
//            controlIV.getDrawable().setLevel(1);
//        } else {
//            controlIV.getDrawable().setLevel(0);
//        }
//    }
//
//
//    //适配普通配置
//    private void adapterNormalConfig(BaseViewHolder helper, VehicleSoundEntity.SoundEffectListBean item) {
//        ImageView coverIV = helper.getView(R.id.iv_preview_image);
//        ImageView cornerMarkIV = helper.getView(R.id.iv_subscript_icon);
//
//        RequestOptions transform = new RequestOptions()
//                .placeholder(R.drawable.place_holder)
//                .centerCrop()
//                .transform(new RoundedCorners(10));
//
//        ImageLoader.with(mContext)
//                .load(item.getIcon())
//                .apply(transform)
//                .into(coverIV);
//        ImageLoader.with(mContext)
//                .load(item.getTagPath())
//                .placeholder(R.drawable.bg_tag_default)
//                .into(cornerMarkIV);
//        long useNum = item.getDownloadNum() + item.getDefaultShowNum();
//        String value = UnitConverUtils.moreThanToConvert(useNum);
//        helper.setText(R.id.tv_preview_name, item.getThemeName())
//                .setText(R.id.tv_preview_size, mContext.getString(R.string.str_buy_count, value));
//
//    }
//
//    @Override
//    public RecyclerView getRecyclerView() {
//        return super.getRecyclerView();
//    }
//
//    /**
//     * 适配金额
//     *
//     * @param helper
//     * @param item
//     */
//    private void adapterPrice(BaseViewHolder helper, VehicleSoundEntity.SoundEffectListBean item) {
//        VehicleSoundUtils.setPayType(item);
//        switch (item.getPay()) {
//            case ShopContract.Pay.DEFAULT:
//                helper.setGone(R.id.tvOriginalRMB, false)
//                        .setGone(R.id.layout_coin, false)
//                        .setVisible(R.id.layout_rmb, true)
//                        .setText(R.id.tvDiscountRMB, R.string.state_free);
//                break;
//            case ShopContract.Pay.COIN:
//                helper.setGone(R.id.layout_rmb, false)
//                        .setVisible(R.id.layout_coin, true);
//                setCoinPrice(helper, item);
//                break;
//            case ShopContract.Pay.RMB:
//                helper.setGone(R.id.layout_coin, false)
//                        .setVisible(R.id.layout_rmb, true)
//                        .setVisible(R.id.tvDiscountRMB, true);
//                setRMBPrice(helper, item);
//                break;
//            case ShopContract.Pay.COIN_AND_RMB:
//                helper.setVisible(R.id.layout_coin, true)
//                        .setVisible(R.id.layout_rmb, true);
//                setCoinPrice(helper, item);
//                setRMBPrice(helper, item);
//                break;
//        }
//    }
//
//    // 适配中间按钮
//    private void adapterCenterPb(BaseViewHolder helper, VehicleSoundEntity.SoundEffectListBean item) {
//        int status = DownloadHelper.getInstance().getStatus(item.getFilePath(), mType);
//        if (item.isBuy()) { //是否已购买
//            //已购买
//            updateDownloadStatus(status, helper, item);
//        } else {
//            //未购买
//            updatePb(helper, true, 0, ResUtils.getString(mContext, R.string.state_buy));
//        }
//    }
//
//    /**
//     * 更新 下载状态
//     *
//     * @param status 当前下载状态
//     * @param helper ViewHolder
//     * @param item   数据Bean
//     */
//    private void updateDownloadStatus(int status, BaseViewHolder helper, VehicleSoundEntity.SoundEffectListBean item) {
//        if (status == DownloadStatus.ERROR || status == DownloadStatus.PAUSE) {//下载中断(失败)  download fail
//            updatePb(helper, true, 0, ResUtils.getString(mContext, R.string.status_download));
//        } else if (status != DownloadStatus.FINISH
//                && status != DownloadStatus.NONE
//                && DownloadHelper.getInstance().checkTaskExecuting(item.getFilePath())) {//任务执行中(下载中) downloading
//            int progressByPercent = getDownloadProgress(item);
//            updatePb(helper, false, progressByPercent, progressByPercent + "%");
//        } else {//使用或未使用判断(本地存有文件) downloaded
//            int downloadStatus = VehicleSoundUtils.theResIsDownloaded(item, mType);
//            if (downloadStatus == VehicleSoundType.DownloadStatus.NONE) {
//                updatePb(helper, true, 0, ResUtils.getString(mContext, R.string.state_download));
//                return;
//            } else if (downloadStatus == VehicleSoundType.DownloadStatus.UPDATE) {
//                updatePb(helper, true, 0, ResUtils.getString(mContext, R.string.state_update));
//                return;
//            }
//            if (VehicleSoundUtils.theResIsUsing(item.getFilePath(), mType)) {
//                mCurrentUseSourcePosition = helper.getLayoutPosition();
//                updatePb(helper, false, 0, ResUtils.getString(mContext, R.string.state_using));//使用中
//            } else {
//                updatePb(helper, true, 0, ResUtils.getString(mContext, R.string.state_use));//未被使用
//            }
//        }
//    }
//
//    //设置车币
//    private void setCoinPrice(BaseViewHolder helper, VehicleSoundEntity.SoundEffectListBean item) {
//        if (item.getScorePrice() > 0) {
//            helper.setVisible(R.id.tvScoreOriginal, true)
//                    .setText(R.id.tvScoreDiscount, UnitConverUtils.moreThanToConvert(String.valueOf(item.getDiscountScorePrice())))
//                    .setText(R.id.tvScoreOriginal, UnitConverUtils.moreThanToConvert(String.valueOf(item.getScorePrice())));
//        } else {
//            helper.setGone(R.id.tvScoreOriginal, false)
//                    .setText(R.id.tvScoreDiscount, UnitConverUtils.moreThanToConvert(String.valueOf(item.getDiscountScorePrice())));
//        }
//    }
//
//    // 设置现金
//    private void setRMBPrice(BaseViewHolder helper, VehicleSoundEntity.SoundEffectListBean item) {
//        if (item.getPrice() * 100 > 0) {
//            helper.setVisible(R.id.tvOriginalRMB, true)
//                    .setText(R.id.tvDiscountRMB, mContext.getString(R.string.rmb_price, UnitConverUtils.moreThanToConvert(String.valueOf(item.getDiscountPrice()))))
//                    .setText(R.id.tvOriginalRMB, UnitConverUtils.moreThanToConvert(String.valueOf(item.getPrice())));
//        } else {
//            helper.setGone(R.id.tvOriginalRMB, false)
//                    .setText(R.id.tvDiscountRMB, mContext.getString(R.string.rmb_price, UnitConverUtils.moreThanToConvert(String.valueOf(item.getDiscountPrice()))));
//        }
//    }
//
//    /**
//     * 开始下载
//     *
//     * @param pos
//     */
//    public void startDownload(int pos, OnDownloadCompleteListener listener, @ResourceType int type) {
//        BaseViewHolder holder = getViewHolder(pos);
//        if (DownloadHelper.getInstance().isDownloading()) {
//            XMToast.showToast(holder.itemView.getContext(), holder.itemView.getContext().getString(R.string.wait_for_complete));
//            return;
//        }
//        if (holder == null) return;
//        VehicleSoundEntity.SoundEffectListBean bean = getItem(pos);
//        int downloadType = VehicleSoundUtils.theResIsDownloaded(bean, type);
//        if (downloadType != VehicleSoundType.DownloadStatus.COMPLETE) {  // 文件不存在，则开始下载
//            int status = DownloadHelper.getInstance().getStatus(bean.getFilePath(), type);
//            if (status == DownloadStatus.FINISH) {
//                DownloadManager.getInstance().delete(bean.getFilePath());
//            }
//            if (NetworkUtils.isConnected(mContext)) {// 有网络的情况下开始下载
//                mTask = new VehicleSoundCallTask(getItem(pos), holder, listener);
//                mTask.startDownload(type);
//            } else {
//                XMToast.toastException(mContext, R.string.network_unavailable);
//            }
//        }
//    }
//
//    public void startDownload(int pos, @ResourceType int type) {
//        startDownload(pos, null, type);
//    }
//
//    //获取下载进度
//    private int getDownloadProgress(VehicleSoundEntity.SoundEffectListBean item) {
//        int result = 0;
//        long progress = DownloadHelper.getInstance().getDownloadSize(item.getFilePath());
//        long totalSize = item.getSize();
//        if (totalSize == 0) {
//            return result;
//        }
//        result = (int) (progress * 1.0f / totalSize * 100f);
//        return result;
//    }
//
//    //更新ProgressButton 文案
//    public void updatePb(BaseViewHolder helper, boolean enabled, int progress, String string) {
//        ProgressButton progressButton = helper.getView(R.id.pbBtnCenter);
//        progressButton.setEnabled(enabled);
//        progressButton.updateStateAndProgress(progress, string);
//
//    }
//
//    // 注册事件
//    private void registerEvent(BaseViewHolder helper) {
//        helper.addOnClickListener(R.id.iv_test_play)
//                .addOnClickListener(R.id.pbBtnCenter);
//    }
//
//    public void useSourceAtPosition(int pos) {
//        String apkFilename = VehicleSoundUtils.getFileName(getItem(pos).getFilePath());
//        String filePath;
//        switch (mType) {
//            case ResourceType.VEHICLE_SOUND:
//                filePath = ConfigManager.FileConfig.getGlobalAudioVehicleConfigFile().getAbsolutePath();
//                break;
//            default:
//            case ResourceType.INSTRUMENT_SOUND:
//                filePath = ConfigManager.FileConfig.getGlobalInstrumentVehicleConfigFile().getAbsolutePath();
//                break;
//        }
//        VehicleSoundUtils.useThisSource(apkFilename, filePath);
//        notifyChanged(pos, getItem(pos));
//
//    }
//
//    private void notifyChanged(int pos, final VehicleSoundEntity.SoundEffectListBean item) {
//        if (mCurrentUseSourcePosition < 0) {
//            mCurrentUseSourcePosition = pos;
//            notifyItemChanged(pos, item);
//        } else {
//            int tmpUseSourcePosition = mCurrentUseSourcePosition;
//            mCurrentUseSourcePosition = pos;
//            notifyItemChanged(tmpUseSourcePosition, getItem(tmpUseSourcePosition));
//            getRecyclerView().post(new Runnable() {
//                @Override
//                public void run() {
//                    notifyItemChanged(mCurrentUseSourcePosition, item);
//                }
//            });
//        }
//    }
//
//    public void notifyPlayStatus(int position, boolean isPlay) {
//        mCurrentPlayFilePath = getData().get(position).getFilePath();
//        getData().get(position).setPlay(isPlay);
//        notifyItemChanged(position);
//        if (!isPlay) {
//            mCurrentPlayFilePath = null;
//        }
//    }
//
//    public void notifyPlayStatus(boolean isPlay) {
//        int position = findCurrentPlayPosition();
//        mCurrentPlayFilePath = getData().get(position).getFilePath();
//        getData().get(position).setPlay(isPlay);
//        notifyItemChanged(position);
//        if (!isPlay) {
//            mCurrentPlayFilePath = null;
//        }
//    }
//
//    /**
//     * 查找当前播放的位置
//     */
//    private int findCurrentPlayPosition() {
//        for (int i = 0; i < getData().size(); i++) {
//            if (getData().get(i).getFilePath().equals(mCurrentPlayFilePath)) {
//                return i;
//            }
//        }
//        return 0;
//    }
//
//    private class VehicleSoundCallTask extends DownloadResourceCallback {
//        private BaseViewHolder mViewHolder;
//        private ProgressButton mProgressButton;
//        private VehicleSoundEntity.SoundEffectListBean mSoundBean;
//        private OnDownloadCompleteListener mListener;
//
//
//        public void setViewHolder(BaseViewHolder viewHolder) {
//            mViewHolder = viewHolder;
//            mProgressButton = mViewHolder.getView(R.id.pbBtnCenter);
//        }
//
//
//        public VehicleSoundEntity.SoundEffectListBean getSoundBean() {
//            return mSoundBean;
//        }
//
//        public void setSoundBean(VehicleSoundEntity.SoundEffectListBean soundBean) {
//            mSoundBean = soundBean;
//        }
//
//        public VehicleSoundCallTask(VehicleSoundEntity.SoundEffectListBean bean, BaseViewHolder holder) {
//            mSoundBean = bean;
//            mViewHolder = holder;
//            mProgressButton = mViewHolder.getView(R.id.pbBtnCenter);
//        }
//
//        public VehicleSoundCallTask(VehicleSoundEntity.SoundEffectListBean bean, BaseViewHolder holder, OnDownloadCompleteListener listener) {
//            mSoundBean = bean;
//            mViewHolder = holder;
//            mProgressButton = mViewHolder.getView(R.id.pbBtnCenter);
//            this.mListener = listener;
//        }
//
//        @Override
//        public void start() {//开始下载
//            KLog.d(TAG, "start download");
//            int progress = getDownloadProgress(mSoundBean);
//            if(refreshEnable()){
//                mProgressButton.setEnabled(false);//设置不可点击
//                mProgressButton.updateStateAndProgress(progress, mContext.getString(R.string.str_download_progress, progress));
//            }
//        }
//
//        @Override
//        public void loading(long progress, long total) { //下载中
//            KLog.d(TAG, "downloading ->" + (int) (progress * 1.0f / total * 100));
//            if (refreshEnable()) {
//                mProgressButton.updateStateAndProgress(progress, total);
//            }
//        }
//
//
//        @Override
//        public void complete(File file) {//下载完成 --> 状态变成 使用
//            KLog.d(TAG, "download complete");
//            VehicleSoundDbUtils.save(mSoundBean, mType);
//            if (refreshEnable()) {
//                updatePb(mViewHolder, true, 0, ResUtils.getString(mContext, R.string.state_use));
//            }
//            justDownloadedUrl = mSoundBean.getFilePath();
//            if (mListener != null) {
//                mListener.complete();
//            }
//            mTask = null;
//        }
//
//        @Override
//        public void error(String msg) {//下载出错
//            KLog.d(TAG, "download error");
//            XMToast.toastException(mContext, mContext.getString(R.string.hint_download_error));
//            if (mSoundBean.isBuy() && refreshEnable()) {
//                updatePb(mViewHolder, true, 0, ResUtils.getString(mContext, R.string.status_download));
//            }
//            mTask = null;
//        }
//
//        /**
//         * 开始下载
//         */
//        public void startDownload(final @ResourceType int type) {
//            final String url = mSoundBean.getFilePath();
//            boolean isDownloading = DownloadHelper.getInstance().checkTaskExecuting(url)
//                    || DownloadHelper.getInstance().getStatus(url, type) == DownloadStatus.WAITING;
//            if (isDownloading) {
//                DownloadHelper.getInstance().registerExtensionListener(url, VehicleSoundCallTask.class.getSimpleName() + "_" + url, type, this);
//            } else {
//                //                DownloadHelper.getInstance().startDownLoad(mContext, mUrl, ResourceType.VEHICLE_SOUND, this);
//                ProgressCenterControllerProxy.getInstance().startDownLoad(mContext, url, type, new OnRegisterCallback() {
//                    @Override
//                    public void register() {
//                        DownloadHelper.getInstance().registerExtensionListener(url,
//                                VehicleSoundCallTask.class.getSimpleName() + "_" + url,
//                                type,
//                                VehicleSoundCallTask.this);
//                    }
//                });
//            }
//        }
//
//        /**
//         * 是否能刷新
//         *
//         * @return
//         */
//        private boolean refreshEnable() {
//            int layoutPosition = mViewHolder.getLayoutPosition();
//            int positionByUrl = VehicleSoundUtils.findPositionByUrl(getData(), mSoundBean.getFilePath());
//            if (layoutPosition == positionByUrl) {
//                return true;
//            }
//            return false;
//        }
//    }
//
//
//    @Override
//    public ItemEvent returnPositionEventMsg(int position) {
//        return new ItemEvent(getData().get(position).getThemeName(), position + "");
//    }
//
//    public BaseViewHolder getViewHolder(int position) {
//        RecyclerView recyclerView = getRecyclerView();
//        if (recyclerView == null) return null;
//        BaseViewHolder viewHolder = (BaseViewHolder) recyclerView.findViewHolderForLayoutPosition(position);
//        if (viewHolder == null) return null;
//        return viewHolder;
//    }
//
//    public interface OnDownloadCompleteListener {
//        void complete();
//    }
//}

package com.xiaoma.shop.business.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseViewHolder;
import com.fsl.android.uniqueota.UniqueOtaConstants;
import com.xiaoma.config.ConfigManager;
import com.xiaoma.image.ImageLoader;
import com.xiaoma.model.ItemEvent;
import com.xiaoma.shop.R;
import com.xiaoma.shop.business.download.DownloadStatus;
import com.xiaoma.shop.business.download.impl.HUSoundEffDownload;
import com.xiaoma.shop.business.download.impl.LCDSoundEffDownload;
import com.xiaoma.shop.business.download.impl.SoundEffDownload;
import com.xiaoma.shop.business.model.VehicleSoundEntity;
import com.xiaoma.shop.business.ui.view.ProgressButton;
import com.xiaoma.shop.common.constant.ResourceType;
import com.xiaoma.shop.common.constant.ShopContract;
import com.xiaoma.shop.common.constant.VehicleSoundType;
import com.xiaoma.shop.common.manager.update.UpdateOtaInfo;
import com.xiaoma.shop.common.manager.update.UpdateOtaManager;
import com.xiaoma.shop.common.util.UnitConverUtils;
import com.xiaoma.shop.common.util.VehicleSoundUtils;
import com.xiaoma.utils.ListUtils;
import com.xiaoma.utils.ResUtils;

import java.util.List;
import java.util.Objects;

/**
 * Created by Gillben
 * date: 2019/3/5 0005
 */
public class VehicleSoundAdapterV2 extends AbsShopAdapter<VehicleSoundEntity.SoundEffectListBean> {
    public static final String TAG = VehicleSoundAdapterV2.class.getSimpleName();
    @ResourceType
    private int mType;
    private Callback mCallback;
    private int ecu;

    public VehicleSoundAdapterV2(int type, Callback callback, int ecu) {
        mType = type;
        this.mCallback = callback;
        this.ecu = ecu;
        setHasStableIds(true);
    }

    @Override
    public long getItemId(int position) {
        VehicleSoundEntity.SoundEffectListBean item = getItem(position);
        return item != null ? item.getId() : RecyclerView.NO_ID;
    }

    private SoundEffDownload getSoundEffDownloader() {
        return ResourceType.VEHICLE_SOUND == mType ?
                HUSoundEffDownload.getInstance() : LCDSoundEffDownload.getInstance();
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position, @NonNull List<Object> payloads) {
        if (ListUtils.isEmpty(payloads)) {
            super.onBindViewHolder(holder, position, payloads);
        } else {
            for (Object obj : payloads) {
                if (!(obj instanceof String)) return;
                if (position < getData().size() && getData().get(position) != null) {
                    adapterCenterPb(holder, getData().get(position));
                }
            }
        }
    }

    @Override
    protected void convert(BaseViewHolder helper, VehicleSoundEntity.SoundEffectListBean item) {
        helper.setVisible(R.id.iv_test_play, true);
        registerEvent(helper);
        adapterNormalConfig(helper, item);
        adapterPrice(helper, item);
        adapterCenterPb(helper, item);
        adapterTryPlay(helper, item);
        adapterUpdateSoundStatus(helper, item);
    }

    private void adapterUpdateSoundStatus(BaseViewHolder helper, VehicleSoundEntity.SoundEffectListBean item) {
        UpdateOtaInfo info = UpdateOtaManager.getInstance().getInfos().get(ecu);
        boolean needReturn = info == null                                   // null
                || !Objects.equals(info.getFileUrl(), item.getFilePath())  // 或者不是同个路径
                || !isUpdateState(info);     // 或者当前更新状态不是progress
        if (needReturn) return;
        if(ecu== UniqueOtaConstants.EcuId.IC_L){
            updatePb(helper, false,0, helper.itemView.getContext().getString(R.string.str_installing));
        }else{
            updatePb(helper, false, info.getProgress(), info.getProgress() + "%");
        }
    }

    private boolean isUpdateState(UpdateOtaInfo info) {
        return info.getInstallState() == UpdateOtaInfo.InstallState.INSTALLING
                || info.getInstallState() == UpdateOtaInfo.InstallState.COPY_FILE
                || info.getInstallState() == UpdateOtaInfo.InstallState.INSTALL_PRE;
    }


    private void adapterTryPlay(BaseViewHolder helper, final VehicleSoundEntity.SoundEffectListBean item) {
        ImageView controlView = helper.getView(R.id.iv_test_play);
        controlView.setVisibility(View.VISIBLE);
        controlView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCallback != null) {
                    if (mCallback.isAuditionPlaying(item)) {
                        mCallback.stopAudition(item);
                    } else {
                        mCallback.startAudition(item);
                    }
                }
            }
        });
        if (mCallback != null && mCallback.isAuditionPlaying(item)) {
            controlView.getDrawable().setLevel(1);
        } else {
            controlView.getDrawable().setLevel(0);
        }
    }


    //适配普通配置
    private void adapterNormalConfig(BaseViewHolder helper, VehicleSoundEntity.SoundEffectListBean item) {
        ImageView coverIV = helper.getView(R.id.iv_preview_image);
        ImageView cornerMarkIV = helper.getView(R.id.iv_subscript_icon);

        RequestOptions transform = new RequestOptions()
                .placeholder(R.drawable.place_holder)
                .centerCrop()
                .transform(new RoundedCorners(10));

        ImageLoader.with(mContext)
                .load(item.getIcon())
                .apply(transform)
                .into(coverIV);
        ImageLoader.with(mContext)
                .load(item.getTagPath())
                .placeholder(R.drawable.bg_tag_default)
                .into(cornerMarkIV);
        long useNum = item.getDownloadNum() + item.getDefaultShowNum();
        String value = UnitConverUtils.moreThanToConvert(useNum);
        helper.setText(R.id.tv_preview_name, item.getThemeName())
                .setText(R.id.tv_preview_size, mContext.getString(R.string.str_buy_count, value));

    }

    @Override
    public RecyclerView getRecyclerView() {
        return super.getRecyclerView();
    }

    /**
     * 适配金额
     *
     * @param helper
     * @param item
     */
    private void adapterPrice(BaseViewHolder helper, VehicleSoundEntity.SoundEffectListBean item) {
        VehicleSoundUtils.setPayType(item);
        switch (item.getPay()) {
            case ShopContract.Pay.DEFAULT:
                helper.setGone(R.id.tvOriginalRMB, false)
                        .setGone(R.id.layout_coin, false)
                        .setVisible(R.id.layout_rmb, true)
                        .setText(R.id.tvDiscountRMB, R.string.state_free);
                break;
            case ShopContract.Pay.COIN:
                helper.setGone(R.id.layout_rmb, false)
                        .setVisible(R.id.layout_coin, true);
                setCoinPrice(helper, item);
                break;
            case ShopContract.Pay.RMB:
                helper.setGone(R.id.layout_coin, false)
                        .setVisible(R.id.layout_rmb, true)
                        .setVisible(R.id.tvDiscountRMB, true);
                setRMBPrice(helper, item);
                break;
            case ShopContract.Pay.COIN_AND_RMB:
                helper.setVisible(R.id.layout_coin, true)
                        .setVisible(R.id.layout_rmb, true);
                setCoinPrice(helper, item);
                setRMBPrice(helper, item);
                break;
        }
    }

    // 适配中间按钮
    private void adapterCenterPb(BaseViewHolder helper, VehicleSoundEntity.SoundEffectListBean item) {
        if (item.isBuy()) { //是否已购买
            //已购买
            updateDownloadStatus(helper, item);
        } else {
            //未购买
            updatePb(helper, true, 0, ResUtils.getString(mContext, R.string.state_buy));
        }
    }

    /**
     * 更新 下载状态
     *
     * @param helper ViewHolder
     * @param item   数据Bean
     */
    private void updateDownloadStatus(BaseViewHolder helper, VehicleSoundEntity.SoundEffectListBean item) {
        SoundEffDownload download = getSoundEffDownloader();
        if (VehicleSoundUtils.theResIsUsing(item.getFilePath(), mType)) {
            int status = VehicleSoundUtils.theResIsDownloaded(item, mType, download);
            if (status == VehicleSoundType.DownloadStatus.UPDATE) {
                updatePb(helper, true, 0, ResUtils.getString(mContext, R.string.state_update));//更新
            } else {
                updatePb(helper, false, 0, ResUtils.getString(mContext, R.string.state_using));//使用中..
            }

        } else {
            if (download.isDownloading(item)) {
                int progressByPercent = getDownloadProgress(item);
                updatePb(helper, false, progressByPercent, progressByPercent + "%");
            } else {
                int status = VehicleSoundUtils.theResIsDownloaded(item, mType, download);
                if (status == VehicleSoundType.DownloadStatus.UPDATE) {
                    updatePb(helper, true, 0, ResUtils.getString(mContext, R.string.state_update));//更新
                } else if (status == VehicleSoundType.DownloadStatus.COMPLETE) {
                    updatePb(helper, true, 0, ResUtils.getString(mContext, R.string.state_use));//未被使用
                } else {
                    updatePb(helper, true, 0, ResUtils.getString(mContext, R.string.state_download));
                }
            }

            /*else if (download.isDownloadSuccess(item)) {
                updatePb(helper, true, 0, ResUtils.getString(mContext, R.string.state_use));//未被使用
            } else {
                updatePb(helper, true, 0, ResUtils.getString(mContext, R.string.state_download));
            }*/
        }
    }

    //设置车币
    private void setCoinPrice(BaseViewHolder helper, VehicleSoundEntity.SoundEffectListBean item) {
        if (item.getScorePrice() > 0 && item.getScorePrice() != item.getDiscountScorePrice()) {
            helper.setVisible(R.id.tvScoreOriginal, true)
                    .setText(R.id.tvScoreDiscount, UnitConverUtils.moreThanToConvert(String.valueOf(item.getDiscountScorePrice())))
                    .setText(R.id.tvScoreOriginal, UnitConverUtils.moreThanToConvert(String.valueOf(item.getScorePrice())));
        } else {
            helper.setGone(R.id.tvScoreOriginal, false)
                    .setText(R.id.tvScoreDiscount, UnitConverUtils.moreThanToConvert(String.valueOf(item.getDiscountScorePrice())));
        }
    }

    // 设置现金
    private void setRMBPrice(BaseViewHolder helper, VehicleSoundEntity.SoundEffectListBean item) {
        if (item.getPrice() * 100 > 0  && item.getPrice() != item.getDiscountPrice()) {
            helper.setVisible(R.id.tvOriginalRMB, true)
                    .setText(R.id.tvDiscountRMB, mContext.getString(R.string.rmb_price, UnitConverUtils.moreThanToConvert(String.valueOf(item.getDiscountPrice()))))
                    .setText(R.id.tvOriginalRMB, UnitConverUtils.moreThanToConvert(String.valueOf(item.getPrice())));
        } else {
            helper.setGone(R.id.tvOriginalRMB, false)
                    .setText(R.id.tvDiscountRMB, mContext.getString(R.string.rmb_price, UnitConverUtils.moreThanToConvert(String.valueOf(item.getDiscountPrice()))));
        }
    }

    //获取下载进度
    private int getDownloadProgress(VehicleSoundEntity.SoundEffectListBean item) {
        DownloadStatus downloadStatus = getSoundEffDownloader().getDownloadStatus(item);
        if (downloadStatus == null)
            return 0;
        return (int) (downloadStatus.currentLength * 100 / downloadStatus.totalLength);
    }

    //更新ProgressButton 文案
    public void updatePb(BaseViewHolder helper, boolean enabled, int progress, String string) {
        ProgressButton progressButton = helper.getView(R.id.pbBtnCenter);
        progressButton.setEnabled(enabled);
        progressButton.updateStateAndProgress(progress, string);

    }

    // 注册事件
    private void registerEvent(BaseViewHolder helper) {
        helper.addOnClickListener(R.id.iv_test_play)
                .addOnClickListener(R.id.pbBtnCenter);
    }

    public void useSourceAtPosition(int pos) {
        String apkFilename = VehicleSoundUtils.getFileName(getItem(pos).getFilePath());
        String filePath;
        switch (mType) {
            case ResourceType.VEHICLE_SOUND:
                filePath = ConfigManager.FileConfig.getGlobalAudioVehicleConfigFile().getAbsolutePath();
                break;
            default:
            case ResourceType.INSTRUMENT_SOUND:
                filePath = ConfigManager.FileConfig.getGlobalInstrumentVehicleConfigFile().getAbsolutePath();
                break;
        }
        VehicleSoundUtils.useThisSource(apkFilename, filePath);
        notifyDataSetChanged();
    }


    @Override
    public ItemEvent returnPositionEventMsg(int position) {
        return new ItemEvent(getData().get(position).getThemeName(), position + "");
    }

    public BaseViewHolder getViewHolder(int position) {
        RecyclerView recyclerView = getRecyclerView();
        if (recyclerView == null) return null;
        BaseViewHolder viewHolder = (BaseViewHolder) recyclerView.findViewHolderForLayoutPosition(position);
        if (viewHolder == null) return null;
        return viewHolder;
    }

    @Override
    public VehicleSoundEntity.SoundEffectListBean searchItemByProductId(long productId) {
        if (ListUtils.isEmpty(getData())) return null;
        for (int i = 0; i < getData().size(); i++) {
            if (productId == getData().get(i).getId()) {
                return getData().get(i);
            }
        }
        return null;
    }


    public interface Callback {
        /**
         * @return 当前item是否正在试听
         */
        boolean isAuditionPlaying(VehicleSoundEntity.SoundEffectListBean item);

        /**
         * 开始试听
         */
        void startAudition(VehicleSoundEntity.SoundEffectListBean item);

        /**
         * 停止试听
         */
        void stopAudition(VehicleSoundEntity.SoundEffectListBean item);

        //        /**
        //         * 购买
        //         */
        //        void onBuy(SkusBean item);
        //
        //        /**
        //         * 使用
        //         */
        //        void onUse(SkusBean item);
    }
}

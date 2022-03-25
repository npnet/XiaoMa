package com.xiaoma.shop.business.adapter.bought;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseViewHolder;
import com.fsl.android.uniqueota.UniqueOtaConstants;
import com.xiaoma.config.ConfigManager;
import com.xiaoma.image.ImageLoader;
import com.xiaoma.shop.R;
import com.xiaoma.shop.business.download.DownloadStatus;
import com.xiaoma.shop.business.download.impl.HUSoundEffDownload;
import com.xiaoma.shop.business.download.impl.LCDSoundEffDownload;
import com.xiaoma.shop.business.model.VehicleSoundEntity;
import com.xiaoma.shop.business.ui.view.ProgressButton;
import com.xiaoma.shop.common.constant.ResourceType;
import com.xiaoma.shop.business.download.impl.SoundEffDownload;
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
 * <p>
 * 已购买整车音效
 */
public class BoughtVehicleSoundAdapter extends BaseBoughtAdapter<VehicleSoundEntity.SoundEffectListBean> {

    @ResourceType
    private int mType;
    private Callback mCallback;
    private int ecu;

    public BoughtVehicleSoundAdapter(RequestManager requestManager, int type, int ecu, Callback callback) {
        super(requestManager);
        mType = type;
        setHasStableIds(true);
        this.ecu = ecu;
        mCallback = callback;
    }

    @Override
    public long getItemId(int position) {
        VehicleSoundEntity.SoundEffectListBean item = getItem(position);
        return item != null ? item.getId() : RecyclerView.NO_ID;
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position, @NonNull List<Object> payloads) {
        if (ListUtils.isEmpty(payloads)) {
            super.onBindViewHolder(holder, position, payloads);
        } else {
            for (Object obj : payloads) {
                if (obj instanceof String) {
                    if (position < getData().size() && getData().get(position) != null) {
                        adapterCenterPb(holder, getData().get(position));
                    }
                }
            }
        }
    }


    @Override
    protected void convert(BaseViewHolder helper, VehicleSoundEntity.SoundEffectListBean item) {
        super.convert(helper, item);
        helper.setGone(R.id.iv_bought_test_play, true)
                .setGone(R.id.tv_preview_size, true);
        registerEvent(helper);
        adapterNormalConfig(helper, item);
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

        ImageView controlView = helper.getView(R.id.iv_bought_test_play);
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

    // 注册事件
    private void registerEvent(BaseViewHolder helper) {
        helper.addOnClickListener(R.id.bought_operation_bt);
    }

    //适配普通配置
    private void adapterNormalConfig(BaseViewHolder helper, VehicleSoundEntity.SoundEffectListBean item) {
        ImageView coverIV = helper.getView(R.id.iv_bought_icon);
        ImageView cornerMarkIV = helper.getView(R.id.iv_bought_subscript_icon);

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

        helper.setText(R.id.tv_bought_name, item.getThemeName())
                .setText(R.id.tv_bought_number, mContext.getString(R.string.str_buy_count, value))
                .setText(R.id.tv_preview_size,item.getSizeFomat());

    }

    // 适配中间按钮
    private void adapterCenterPb(BaseViewHolder helper, VehicleSoundEntity.SoundEffectListBean item) {
        updateDownloadStatus(helper, item);
    }

    private SoundEffDownload getSoundEffDownloader() {
        return ResourceType.VEHICLE_SOUND == mType ?
                HUSoundEffDownload.getInstance() : LCDSoundEffDownload.getInstance();
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
                updatePb(helper, false, 0, ResUtils.getString(mContext, R.string.state_using));//使用中
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
                updatePb(helper, true, 0, ResUtils.getString(mContext, R.string.state_use));
            } else {
                updatePb(helper, true, 0, ResUtils.getString(mContext, R.string.state_download));
            }*/
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
        ProgressButton progressButton = helper.getView(R.id.bought_operation_bt);
        progressButton.setEnabled(enabled);
        progressButton.updateStateAndProgress(progress, string);

    }

    public void useSourceAtPosition(int pos) {
        String apkFilename = getFileName(getItem(pos).getFilePath());
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


    private String getFileName(String url) {
        String[] split = url.split("\\/");
        return split[split.length - 1];
    }

    public BaseViewHolder getViewHolder(int position) {
        RecyclerView recyclerView = getRecyclerView();
        if (recyclerView == null) return null;
        BaseViewHolder viewHolder = (BaseViewHolder) recyclerView.findViewHolderForLayoutPosition(position);
        if (viewHolder == null) return null;
        return viewHolder;
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
        //        void onBuy(VehicleSoundEntity.SoundEffectListBean item);
        //
        //        /**
        //         * 使用
        //         */
        //        void onUse(VehicleSoundEntity.SoundEffectListBean item);
    }
}

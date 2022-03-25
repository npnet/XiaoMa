package com.xiaoma.shop.business.ui.bought;

import android.app.DownloadManager;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.fsl.android.uniqueota.UniqueOtaConstants;
import com.xiaoma.autotracker.XmAutoTracker;
import com.xiaoma.component.AppHolder;
import com.xiaoma.config.ConfigManager;
import com.xiaoma.image.ImageLoader;
import com.xiaoma.model.annotation.PageDescComponent;
import com.xiaoma.shop.R;
import com.xiaoma.shop.business.adapter.bought.BoughtHologramAdapter;
import com.xiaoma.shop.business.download.DownloadListener;
import com.xiaoma.shop.business.download.DownloadStatus;
import com.xiaoma.shop.business.download.impl.HologramDownload;
import com.xiaoma.shop.business.hologram.HologramUsing;
import com.xiaoma.shop.business.model.HoloListModel;
import com.xiaoma.shop.business.model.MineBought;
import com.xiaoma.shop.business.ui.hologram.HologramDetailActivity;
import com.xiaoma.shop.common.constant.CacheBindStatus;
import com.xiaoma.shop.common.constant.Hologram3DContract;
import com.xiaoma.shop.common.constant.ResourceType;
import com.xiaoma.shop.common.manager.update.OnUpdateCallback;
import com.xiaoma.shop.common.manager.update.UpdateOtaInfo;
import com.xiaoma.shop.common.manager.update.UpdateOtaManager;
import com.xiaoma.shop.common.track.EventConstant;
import com.xiaoma.shop.common.util.Hologram3DUtils;
import com.xiaoma.shop.common.util.PriceUtils;
import com.xiaoma.ui.toast.XMToast;
import com.xiaoma.utils.DoubleClickUtils;
import com.xiaoma.utils.FileUtils;
import com.xiaoma.utils.ListUtils;
import com.xiaoma.utils.log.KLog;

import java.io.File;

/**
 * <pre>
 *     author : wutao
 *     time   : 2019/01/29
 *     desc   : 已购买全息影像
 * </pre>
 */
@PageDescComponent(EventConstant.PageDesc.FRAGMENT_BUYED_HOLOGRAM)
public class BoughtHologramFragment extends AbsBoughtFragment<HoloListModel, BoughtHologramAdapter> {


    public static BoughtHologramFragment newInstance() {
        return new BoughtHologramFragment();
    }

    @Override
    protected BoughtHologramAdapter createAdapter() {
        return new BoughtHologramAdapter(ImageLoader.with(this));
    }

    @Override
    protected int requestResourceType() {
        return ResourceType.HOLOGRAM;
    }

    @Override
    protected int cacheBindStatus() {
        return CacheBindStatus.NONE;
    }

    @Override
    protected String getItemDownloadUrl(HoloListModel model) {
        return model != null ? model.getCustomImageResourceUrl() : "";
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        registerListener();
    }

    @Override
    protected void obtainActuallyData(boolean more, MineBought data) {
        if (ListUtils.isEmpty(data.getHolos())) {
            showEmptyView();
            return;
        }
        if (more) {
            getAdapter().addData(data.getHolos());
        } else {
            getAdapter().setNewData(data.getHolos());
        }
    }


    @Override
    protected void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        //        HologramDetailActivity.newInstanceActivity(mContext, (HoloListModel) adapter.getItem(position));
    }

    @Override
    protected void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
        HoloListModel item = getAdapter().getItem(position);
        Context context = view.getContext();
        switch (view.getId()) {
            case R.id.bought_operation_bt:
                if (DoubleClickUtils.isFastDoubleClick(500)) return;
                if (item != null) {
                    if (item.get3DState() == null) {
                        if (item.getUserBuyFlag() == 1
                                || PriceUtils.isFree(item.getDiscountPrice(), item.getDiscountScorePrice(), true)) {
                            manualUpdateTrack(item.toTrackString(), EventConstant.NormalClick.ACTION_USE);
                            HologramUsing.useRole(context, item);
                            adapter.notifyDataSetChanged();
                        }
                    } else {
                        handle3DClick(item, position);
                    }
                }
                break;
            case R.id.iv_bought_icon:
            default:
                manualUpdateTrack(item == null ? "N/A" : item.toTrackString(), EventConstant.NormalClick.ACTION_HOLOGRAM_COVER);
                HologramDetailActivity.newInstanceActivity(mContext, item);
                break;
        }
    }

    private void manualUpdateTrack(String content, String eventAction) {
        XmAutoTracker.getInstance().onEvent(eventAction, content, this.getClass().getName(), EventConstant.PageDesc.FRAGMENT_HOLOGRAM);
    }

    private void handle3DClick(final HoloListModel item, final int pos) {
        switch (item.getState()) {
            case Hologram3DContract.STATE_DOWNLOAD_PROGRESS:
                XMToast.showToast(mContext, R.string.update_downloading);
                break;
            case Hologram3DContract.STATE_DOWNLOAD_SUCCESS:
            case Hologram3DContract.STATE_INSTALL_SUCCESS:
                if (mBaseQuickAdapter != null) {
                    mBaseQuickAdapter.notifyItemChanged(pos);
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
        if (mBaseQuickAdapter != null) {
            mBaseQuickAdapter.notifyItemChanged(pos);
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
                        if (mBaseQuickAdapter != null) {
                            mBaseQuickAdapter.notifyAtMain();
                        }

                        break;
                    case DownloadManager.STATUS_FAILED:
                        XMToast.toastException(mContext, R.string.hint_download_error);
                        if (mBaseQuickAdapter != null) {
                            mBaseQuickAdapter.notifyAtMain();
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
                        if (mBaseQuickAdapter != null) {
                            mBaseQuickAdapter.notifyAtMain();
                        }
                        break;
                }
            }
        });
        UpdateOtaManager.getInstance().registerCallback(new OnUpdateCallback(UniqueOtaConstants.EcuId.ROBOT) {

            @Override
            public void notifyDataSetChange(final UpdateOtaInfo info) {
                if (info == null) return;
                if (mBaseQuickAdapter != null) {
                    mBaseQuickAdapter.notifyAtMain();
                }
            }

            @Override
            public void onSuccess(UpdateOtaInfo info) {
                if (info == null) return;
                int state = info.getInstallState();
                KLog.i("filOut| " + "[onSuccess]->InstallResult -> " + state);
                if (state == UpdateOtaInfo.InstallState.INSTALL_SUCCESSFUL) {
                    UpdateOtaManager.getInstance().unRegisterCallback(this);
                    if (mBaseQuickAdapter != null) {
                        mBaseQuickAdapter.notifyAtMain();
                    }
                }
            }

            @Override
            public void onFailure(UpdateOtaInfo info) {
                if (info == null) return;
                XMToast.showToast(AppHolder.getInstance().getAppContext(), R.string.state_error_update_3d);
                if (mBaseQuickAdapter != null) {
                    mBaseQuickAdapter.notifyAtMain();
                }
            }
        });
    }
}

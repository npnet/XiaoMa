package com.xiaoma.shop.business.ui.bought;

import android.app.DownloadManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.webkit.URLUtil;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.xiaoma.image.ImageLoader;
import com.xiaoma.shop.R;
import com.xiaoma.shop.business.adapter.bought.BoughtVehicleSoundAdapter;
import com.xiaoma.shop.business.download.DownloadListener;
import com.xiaoma.shop.business.download.DownloadStatus;
import com.xiaoma.shop.business.download.impl.HUSoundEffDownload;
import com.xiaoma.shop.business.download.impl.LCDSoundEffDownload;
import com.xiaoma.shop.business.download.impl.SoundEffDownload;
import com.xiaoma.shop.business.model.MineBought;
import com.xiaoma.shop.business.model.VehicleSoundEntity;
import com.xiaoma.shop.common.callback.OnRefreshCallback;
import com.xiaoma.shop.common.constant.CacheBindStatus;
import com.xiaoma.shop.common.constant.ResourceType;
import com.xiaoma.shop.common.constant.VehicleSoundType;
import com.xiaoma.shop.common.manager.update.OnUpdateCallback;
import com.xiaoma.shop.common.manager.update.UpdateOtaInfo;
import com.xiaoma.shop.common.manager.update.UpdateOtaManager;
import com.xiaoma.shop.common.util.AudioAuditionHelper;
import com.xiaoma.shop.common.util.UpdateOtaUtils;
import com.xiaoma.shop.common.util.VehicleSoundUtils;
import com.xiaoma.thread.ThreadDispatcher;
import com.xiaoma.ui.dialog.XmDialog;
import com.xiaoma.ui.toast.XMToast;
import com.xiaoma.utils.ListUtils;
import com.xiaoma.utils.NetworkUtils;
import com.xiaoma.utils.log.KLog;

import java.util.List;
import java.util.Objects;

/**
 * @Author: LiangJingBo.
 * @Date: 2019/06/05
 * @Describe:
 */

public abstract class AbsBoughtVehicleSoundFragment extends AbsBoughtFragment<VehicleSoundEntity.SoundEffectListBean, BoughtVehicleSoundAdapter> {


    private DownloadListener mDownloadListener;
    private boolean mIsHidden;
    private AudioAuditionHelper mAuditionHelper;
    private OnUpdateCallback onUpdateCallback;
    private XmDialog xmDialog;
    private OnRefreshCallback onRefreshCallback;

    @Override
    final protected String getItemDownloadUrl(VehicleSoundEntity.SoundEffectListBean bean) {
        return bean != null ? bean.getFilePath() : "";
    }

    @Override
    protected BoughtVehicleSoundAdapter createAdapter() {
        return new BoughtVehicleSoundAdapter(ImageLoader.with(this), requestResourceType(), getEcu(), new BoughtVehicleSoundAdapter.Callback() {
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
                    String url = item.getAuditionPath() + "?id=" + item.getId();
                    initAuditionAndPlay(url);
                    callNotifyDataSetChanged();
                } else {
                    XMToast.toastException(mContext, R.string.invalid_audition_url);
                }
            }

            @Override
            public void stopAudition(VehicleSoundEntity.SoundEffectListBean item) {
                releaseAudition();
                callNotifyDataSetChanged();
            }
        });
    }


    @Override
    protected int cacheBindStatus() {
        return CacheBindStatus.NONE;
    }

    @Override
    protected void obtainActuallyData(boolean more, MineBought data) {
        if (ListUtils.isEmpty(getData(data))) {
            showEmptyView();
            return;
        }
        if (more) {
            getAdapter().addData(getData(data));
        } else {
            getAdapter().setNewData(getData(data));
        }
    }


    @Override
    protected void onItemChildClick(BaseQuickAdapter adapter, View view, final int position) {
        switch (view.getId()) {
            case R.id.bought_operation_bt:
                VehicleSoundEntity.SoundEffectListBean bean = getAdapter().getData().get(position);
                //判断是否已经下载，如果已经下载了就使用，否则下载
                int status = VehicleSoundUtils.theResIsDownloaded(bean, requestResourceType(), getSoundEffDownloader());
                if (status == VehicleSoundType.DownloadStatus.COMPLETE) {
                    handleDownloadComplete(position, null,true);
                } else {
                    getSoundEffDownloader().start(bean);
                }
                /*
                if (getSoundEffDownloader().isDownloadSuccess(bean)) {
                    handleDownloadComplete(position, null);
                } else {
                    getSoundEffDownloader().start(bean);
                }*/
                break;
        }
    }

    protected void showUseSuccess() {
        XMToast.showToast(mActivity, R.string.successful_use);
    }

    protected void handleDownloadComplete(int pos, String url,boolean executeImmediately) {
        if (xmDialog != null && xmDialog.isAdded()) {
            xmDialog.dismiss();
        }
        if(executeImmediately && (VehicleSoundUtils.isPro() || getProductType().equals(VehicleSoundType.ProductType.AUDIO_SOUND) )){
            beginUpdateVehicleSound(pos,url);
            return;
        }
        if (VehicleSoundUtils.isPro() || requestResourceType() == ResourceType.VEHICLE_SOUND) {//高配
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
            position = VehicleSoundUtils.findPositionByUrl(getAdapter().getData(), url);
        }
        if (position < 0) return;
        updateVehicleSound(position);
    }


    //更新
    protected void updateVehicleSound(int pos) {
        if (pos == -1) return;
        replaceSound(pos);
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registerDownloadListener();
        registerUpdateListener();
        registerRefreshListener();
    }

    private void registerRefreshListener() {
        getSoundEffDownloader().addRefreshCallback(onRefreshCallback = new OnRefreshCallback() {
            @Override
            public void onSingleRefresh(long id, String filePath) {
                if (isDestroy() || getAdapter() == null || ListUtils.isEmpty(getAdapter().getData()))
                    return;
                int pos = VehicleSoundUtils.findPositionByUrl(getAdapter().getData(), filePath);
                if (pos >= 0) {
                    getAdapter().getData().get(pos).setDownloadNum(getAdapter().getData().get(pos).getDownloadNum() + 1);
                    getAdapter().notifyItemChanged(pos);
                }
            }

            @Override
            public void onRefreshAll() {
                if (isDestroy() || getAdapter() == null || ListUtils.isEmpty(getAdapter().getData()))
                    return;
                getAdapter().notifyDataSetChanged();
            }
        });
    }

    private void registerUpdateListener() {
        UpdateOtaManager.getInstance().registerCallback(onUpdateCallback = new OnUpdateCallback(getEcu()) {
            @Override
            public void notifyDataSetChange(final UpdateOtaInfo info) {
                if (!isDestroy() && getAdapter() != null && info != null) {
                    ThreadDispatcher.getDispatcher().postOnMain(new Runnable() {
                        @Override
                        public void run() {
                            int position = VehicleSoundUtils.findPositionByUrl(getAdapter().getData(), info.getFileUrl());
                            if (position >= 0) {
                                getAdapter().notifyItemChanged(position);
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

    @Override
    public void onDestroy() {
        UpdateOtaManager.getInstance().unRegisterCallback(onUpdateCallback);
        getSoundEffDownloader().removeDownloadListener(mDownloadListener);
        getSoundEffDownloader().removeRefreshCallback(onRefreshCallback);
        super.onDestroy();
    }

    private void registerDownloadListener() {
        getSoundEffDownloader().addDownloadListener(mDownloadListener = new DownloadListener() {
            @Override
            public void onDownloadStatus(@Nullable DownloadStatus downloadStatus) {
                if (isDestroy() || downloadStatus == null || getAdapter() == null)
                    return;
                int position = VehicleSoundUtils.findPositionByUrl(getAdapter().getData(), downloadStatus.downUrl);
                if (position >= 0) {
                    getAdapter().notifyItemChanged(position);
                }
                if (downloadStatus.status == DownloadManager.STATUS_SUCCESSFUL) {
                    handleDownloadComplete(-1, downloadStatus.downUrl,false);
                }
                if (downloadStatus.status == DownloadManager.STATUS_FAILED) {
                    XMToast.toastException(mActivity, R.string.hint_download_error);
                }
            }
        });
    }


    private void installSucceed() {
        ThreadDispatcher.getDispatcher().postOnMain(new Runnable() {
            @Override
            public void run() {
                if (getAdapter() != null) {
                    getAdapter().notifyDataSetChanged();
                }
                showUseSuccess();
            }
        });

    }

    protected void showUpdateError() {
        ThreadDispatcher.getDispatcher().postOnMain(new Runnable() {
            @Override
            public void run() {
                if (getAdapter() != null) {
                    getAdapter().notifyDataSetChanged();
                }
                XMToast.toastException(mActivity, R.string.update_failed);
            }
        });
    }


    @Override
    public void onResume() {
        super.onResume();
        mIsHidden = false;
    }

    @Override
    public void onStop() {
        super.onStop();
        releaseAudition();
        mIsHidden = true;
    }


    protected void showIneligibleError() {
        XMToast.toastException(mActivity, R.string.condition_not_satisfied);
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
                if (getAdapter() != null) {
                    getAdapter().notifyDataSetChanged();
                }
            }
        });
    }


    /**
     * 替换音效
     */
    private void replaceSound(int pos) {
        UpdateOtaManager.getInstance().pushSoundFile(
                getAdapter().getData().get(pos).getFilePath(),
                requestResourceType(),
                requestUpdateResourceType(),
                getEcu());
    }

    protected abstract int getEcu();

    protected abstract String getKeyTag();

    protected abstract List<VehicleSoundEntity.SoundEffectListBean> getData(MineBought data);

    protected abstract int requestUpdateResourceType();

    protected abstract String getProductType();

    protected abstract Object getEvent();

}

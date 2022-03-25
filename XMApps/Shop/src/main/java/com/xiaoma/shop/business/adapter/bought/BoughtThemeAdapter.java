package com.xiaoma.shop.business.adapter.bought;

import android.app.DownloadManager;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.RequestManager;
import com.chad.library.adapter.base.BaseViewHolder;
import com.xiaoma.shop.R;
import com.xiaoma.shop.business.download.DownloadStatus;
import com.xiaoma.shop.business.download.impl.SkinDownload;
import com.xiaoma.shop.business.model.SkinVersionsBean;
import com.xiaoma.shop.business.skin.SkinUsing;
import com.xiaoma.shop.business.ui.bought.BoughtThemeFragment;
import com.xiaoma.shop.business.ui.view.ProgressButton;
import com.xiaoma.shop.common.track.EventConstant;
import com.xiaoma.shop.common.util.ResourceCounter;
import com.xiaoma.ui.toast.XMToast;

import java.io.File;
import java.text.DecimalFormat;

/**
 * Created by Gillben
 * date: 2019/3/5 0005
 * <p>
 * 已购买主题
 */
public class BoughtThemeAdapter extends BaseBoughtAdapter<SkinVersionsBean> {
    public BoughtThemeAdapter(RequestManager requestManager, Callback callback) {
        super(requestManager);
        mCallback = callback;
    }

    @Override
    protected void convert(BaseViewHolder helper, SkinVersionsBean item) {
        super.convert(helper, item);

        loadRoundCircleUrl(helper, item.getLogoUrl());
        loadTagUrl(helper, item.getTagPath());

        helper.setText(R.id.tv_preview_size, mContext.getString(R.string.down_load_size, item.getSizeFomat()));
        helper.setText(R.id.tv_bought_name, item.getAppName());

        String showCount;
        int showUseCountNum = item.getUsedNum() + item.getDefaultShowNum();
        if (showUseCountNum >= 10000) {
            showCount = (new DecimalFormat("#0.0").format(showUseCountNum * 1.0f / 10000)) + "W";
        } else {
            showCount = String.valueOf(showUseCountNum);
        }

        helper.setText(R.id.tv_bought_number, mContext.getString(R.string.down_load_number, showCount));


        //本地文件没有
        updateProgressButton(helper, item);

        //一键清理缓存
        ImageView selectCacheImage = helper.getView(R.id.iv_select_cache);
        if (isCleanCache()
                && !SkinUsing.isUsing(item)
                && SkinDownload.getInstance().isDownloadSuccess(item)) {
            selectCacheImage.setVisibility(View.VISIBLE);
            //开启选择
            if (item.isSelect()) {
                selectCacheImage.setImageLevel(1);
            } else {
                selectCacheImage.setImageLevel(0);
            }
        } else {
            selectCacheImage.setVisibility(View.GONE);
            item.setSelect(false);
            selectCacheImage.setImageLevel(0);
        }


    }


    @Override
    public boolean selectOnlyOne(int position) {
        SkinVersionsBean skinVersionsBean = getData().get(position);
        if (skinVersionsBean.isSelect()) {
            skinVersionsBean.setSelect(false);
        } else {
            skinVersionsBean.setSelect(true);
        }
        notifyItemChanged(position, skinVersionsBean);
        return skinVersionsBean.isSelect();
    }

    @Override
    public String selectAll(boolean select) {
        String cacheSize = "0";
        for (SkinVersionsBean bean : getData()) {
            if (SkinUsing.isUsing(bean)) {
                continue;
            }
            DownloadStatus downloadStatus = SkinDownload.getInstance().getDownloadStatus(bean);
            File skinZipFile;
            if (downloadStatus != null
                    && DownloadManager.STATUS_SUCCESSFUL == downloadStatus.status
                    && (skinZipFile = new File(downloadStatus.downFilePath)).exists()) {
                bean.setSelect(select);
                cacheSize = ResourceCounter.getInstance().calculationSelectedFileCache(select, skinZipFile);
            }
        }
        notifyDataSetChanged();
        return cacheSize;
    }

    private void updateProgressButton(final BaseViewHolder helper, final SkinVersionsBean item) {
        final SkinDownload skinDownload = SkinDownload.getInstance();
        DownloadStatus downloadStatus = skinDownload.getDownloadStatus(item);

        final ProgressButton progressButton = helper.getView(R.id.bought_operation_bt);
        progressButton.setEnabled(true);
        if (SkinUsing.isUsing(item)) {
            progressButton.setEnabled(false);
            progressButton.updateStateAndProgress(0, mContext.getString(R.string.state_using));
        } else if (skinDownload.isDownloading(item)) {
            int downProgress = 0;
            if (downloadStatus != null) {
                downProgress = (int) (downloadStatus.currentLength * 100 / downloadStatus.totalLength);
            }
            progressButton.updateStateAndProgress(downProgress, downProgress + "%");
            progressButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    XMToast.showToast(v.getContext(), R.string.tips_wait_download_complete);
                }
            });
        } else {
            if (skinDownload.isDownloadSuccess(item)) {
                progressButton.updateStateAndProgress(0, mContext.getString(R.string.use_text));
                progressButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mCallback != null) {
                            mCallback.onUseSkin(item);
                        }
                        manualUpdateTrack(EventConstant.NormalClick.ACTION_USE, item.toTrackString(), BoughtThemeFragment.class.getName());
                    }
                });
            } else {
                progressButton.updateStateAndProgress(0, mContext.getString(R.string.load_text));
                progressButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mCallback != null) {
                            mCallback.onDownloadSkin(item);
                        }
                        manualUpdateTrack(EventConstant.NormalClick.ACTION_DOWNLOAD, item.toTrackString(), BoughtThemeFragment.class.getName());
//                        XmAutoTracker.getInstance().onEvent(EventConstant.NormalClick.ACTION_DOWNLOAD,
//                                getClass().getName(),
//                                EventConstant.PageDesc.FRAGMENT_BUYED_SKIN);
                    }
                });
            }
        }
    }

    private Callback mCallback;

    public interface Callback {
        void onDownloadSkin(SkinVersionsBean skin);

        void onUseSkin(SkinVersionsBean skin);
    }

}

package com.xiaoma.shop.business.adapter.bought;

import android.app.DownloadManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import com.bumptech.glide.RequestManager;
import com.chad.library.adapter.base.BaseViewHolder;
import com.xiaoma.shop.R;
import com.xiaoma.shop.business.adapter.TTSAdapterCallback;
import com.xiaoma.shop.business.download.DownloadStatus;
import com.xiaoma.shop.business.model.SkusBean;
import com.xiaoma.shop.business.tts.TTSDownload;
import com.xiaoma.shop.business.tts.TTSUsing;
import com.xiaoma.shop.business.ui.view.ProgressButton;
import com.xiaoma.shop.common.util.ResourceCounter;

import java.io.File;

/**
 * Created by Gillben
 * date: 2019/3/5 0005
 * <p>
 * 已购买语音音色
 */
public class BoughtVoiceToneAdapter extends BaseBoughtAdapter<SkusBean> {
    private TTSAdapterCallback mCallback;

    public BoughtVoiceToneAdapter(RequestManager requestManager, TTSAdapterCallback callback) {
        super(requestManager);
        mCallback = callback;
    }

    @Override
    public long getItemId(int position) {
        SkusBean tts = getItem(position);
        return tts != null ? tts.getId() : RecyclerView.NO_ID;
    }

    @Override
    protected void convert(BaseViewHolder helper, final SkusBean item) {
        super.convert(helper, item);

        loadRoundCircleUrl(helper, item.getLogoUrl());
        loadTagUrl(helper, item.getTypeIcon());

        helper.setGone(R.id.iv_bought_test_play, true);
        helper.setGone(R.id.tv_preview_size, false);
        helper.setText(R.id.tv_bought_name, item.getThemeName());
        helper.setText(R.id.tv_bought_number, mContext.getString(R.string.down_load_number, item.getOrderList()+""));

        //一键清理缓存
        ImageView selectCacheImage = helper.getView(R.id.iv_select_cache);
        if (isCleanCache()
                && !TTSUsing.isTTSUsing(item)
                && TTSDownload.getInstance().isDownloadSuccess(item)) {
            selectCacheImage.setVisibility(View.VISIBLE);
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
        // 试听
        ImageView testPlayView = helper.getView(R.id.iv_bought_test_play);
        testPlayView.setOnClickListener(new View.OnClickListener() {
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
            testPlayView.getDrawable().setLevel(1);
        } else {
            testPlayView.getDrawable().setLevel(0);
        }
        // 按钮状态
        ProgressButton pBtn = helper.getView(R.id.bought_operation_bt);
        pBtn.setEnabled(false);
        if (TTSUsing.isTTSUsing(item)) {
            pBtn.updateStateAndProgress(0, mContext.getString(R.string.state_using));
            pBtn.setOnClickListener(null);
            pBtn.setEnabled(false);
        } else {
            pBtn.setEnabled(true);
            if (TextUtils.isEmpty(item.getTtsResDownloadUrl())) {
                pBtn.updateStateAndProgress(0, mContext.getString(R.string.state_use));
                pBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mCallback != null) {
                            mCallback.onUseTTS(item);
                        }
                    }
                });
            } else {
                TTSDownload download = TTSDownload.getInstance();
                if (download.isDownloading(item)) {
                    long curLen = 0, totalLen = -1;
                    DownloadStatus status = download.getDownloadStatus(item);
                    if (status != null) {
                        curLen = status.currentLength;
                        totalLen = status.totalLength;
                    }
                    pBtn.updateStateAndProgress(curLen, totalLen);
                    pBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (mCallback != null) {
                                mCallback.onDownloading(item);
                            }
                        }
                    });
                } else {
                    File resFile = download.getDownloadFile(item);
                    if (resFile != null && resFile.exists()) {
                        pBtn.updateStateAndProgress(0, mContext.getString(R.string.state_use));
                        pBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (mCallback != null) {
                                    mCallback.onUseTTS(item);
                                }
                            }
                        });
                    } else {
                        pBtn.updateStateAndProgress(0, mContext.getString(R.string.state_download));
                        pBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (mCallback != null) {
                                    mCallback.onDownload(item);
                                }
                            }
                        });
                    }
                }
            }
        }
    }

    @Override
    public boolean selectOnlyOne(int position) {
        SkusBean skusBean = getData().get(position);
        if (skusBean.isSelect()) {
            skusBean.setSelect(false);
        } else {
            skusBean.setSelect(true);
        }
        notifyItemChanged(position, skusBean);
        return skusBean.isSelect();
    }


    @Override
    public String selectAll(boolean select) {
        String cacheSize = "0";
        for (SkusBean bean : getData()) {
            if (TTSUsing.isTTSUsing(bean)) {
                continue;
            }
            DownloadStatus downloadStatus = TTSDownload.getInstance().getDownloadStatus(bean);
            File skusZipFile;
            if (downloadStatus != null
                    && DownloadManager.STATUS_SUCCESSFUL == downloadStatus.status
                    && (skusZipFile = new File(downloadStatus.downFilePath)).exists()) {
                bean.setSelect(select);
                cacheSize = ResourceCounter.getInstance().calculationSelectedFileCache(select, skusZipFile);
            }
        }
        notifyDataSetChanged();
        return cacheSize;
    }
}

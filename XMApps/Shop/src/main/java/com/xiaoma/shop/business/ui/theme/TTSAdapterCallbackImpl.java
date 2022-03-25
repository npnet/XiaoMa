package com.xiaoma.shop.business.ui.theme;

import android.content.Context;
import android.webkit.URLUtil;

import com.xiaoma.component.base.BaseFragment;
import com.xiaoma.model.ResultCallback;
import com.xiaoma.model.XMResult;
import com.xiaoma.shop.R;
import com.xiaoma.shop.business.adapter.TTSAdapterCallback;
import com.xiaoma.shop.business.model.SkusBean;
import com.xiaoma.shop.business.pay.PayHandler;
import com.xiaoma.shop.business.pay.PaySuccessResultCallback;
import com.xiaoma.shop.business.tts.TTSDownload;
import com.xiaoma.shop.business.tts.TTSUsing;
import com.xiaoma.shop.common.RequestManager;
import com.xiaoma.shop.common.constant.ResourceType;
import com.xiaoma.shop.common.track.EventConstant;
import com.xiaoma.shop.common.util.AudioAuditionHelper;
import com.xiaoma.thread.SeriesAsyncWorker;
import com.xiaoma.thread.Work;
import com.xiaoma.ui.toast.XMToast;
import com.xiaoma.utils.NetworkUtils;

import java.util.Objects;

/**
 * Created by LKF on 2019-7-1 0001.
 */
public abstract class TTSAdapterCallbackImpl implements TTSAdapterCallback {
    private AudioAuditionHelper mAuditionHelper;
    private BaseFragment mFragment;
    protected Context mContext;

    public TTSAdapterCallbackImpl(BaseFragment fragment) {
        mFragment = fragment;
        mContext = mFragment.getContext();
    }

    protected abstract void onEventReport(String content, String eventAction);

    protected abstract void onNotifyDataSetChanged();

    protected abstract void onAfterBuyed(SkusBean item);

    @Override
    public boolean isAuditionPlaying(SkusBean item) {
        if (!isCurAuditionPlaying())
            return false;
        return Objects.equals(getCurAuditionUrl(), item.getAuditionUrl());
    }

    @Override
    public void startAudition(SkusBean item) {
        onEventReport(item.toTrackString(), EventConstant.NormalClick.ACTION_PLAY_PAUSE);
        if (!NetworkUtils.isConnected(mContext)) {
            XMToast.toastException(mContext, R.string.no_network);
            return;
        }
        String auditionUrl = item.getAuditionUrl();
        if (URLUtil.isValidUrl(auditionUrl)) {
            initAuditionAndPlay(auditionUrl);
            onNotifyDataSetChanged();
        } else {
            XMToast.toastException(mContext, R.string.invalid_audition_url);
        }
    }

    @Override
    public void stopAudition(SkusBean item) {
        releaseAudition();
        onNotifyDataSetChanged();
    }

    @Override
    public void onBuy(final SkusBean item) {
        onEventReport(item.toTrackString(), EventConstant.NormalClick.ACTION_BUY);
        if (!NetworkUtils.isConnected(mContext)) {
            XMToast.toastException(mContext, R.string.no_network);
            return;
        }
        double rmbPrice = item.getDiscountPrice();
        int scorePrice = item.getDiscountScorePrice();
        if (scorePrice > 0) {
            if (rmbPrice > 0) {
                PayHandler.getInstance().scanCodePayWindow(
                        mFragment.getActivity(),
                        ResourceType.ASSISTANT,
                        item.getId(),
                        item.getThemeName(),
                        String.valueOf(rmbPrice),
                        scorePrice,
                        false,
                        new PaySuccessResultCallback() {
                            @Override
                            public void confirm() {
                                onAfterBuyed(item);
                            }

                            @Override
                            public void cancel() {
                            }
                        });
            } else {
                PayHandler.getInstance().carCoinPayWindow(
                        mFragment.getActivity(),
                        ResourceType.ASSISTANT,
                        item.getId(),
                        item.getThemeName(),
                        scorePrice,
                        false,
                        new PaySuccessResultCallback() {
                            @Override
                            public void confirm() {
                                onAfterBuyed(item);
                            }

                            @Override
                            public void cancel() {
                            }
                        });
            }
        } else {
            if (rmbPrice > 0) {
                PayHandler.getInstance().scanCodePayWindow(
                        mFragment.getActivity(),
                        ResourceType.ASSISTANT,
                        item.getId(),
                        item.getThemeName(),
                        String.valueOf(rmbPrice),
                        scorePrice,
                        true,
                        new PaySuccessResultCallback() {
                            @Override
                            public void confirm() {
                                onAfterBuyed(item);
                            }

                            @Override
                            public void cancel() {
                            }
                        });
            }
        }

    }

    @Override
    public boolean onUseTTS(SkusBean item) {
        return TTSUsing.useTTS(mFragment, item);
    }

    @Override
    public void onDownloading(SkusBean item) {
        XMToast.showToast(mContext, R.string.tips_wait_download_complete);
    }

    @Override
    public void onDownload(final SkusBean item) {
        if (!NetworkUtils.isConnected(mContext)) {
            XMToast.toastException(mContext, R.string.no_network);
            return;
        }
        onEventReport(item.toTrackString(), EventConstant.NormalClick.ACTION_DOWNLOAD);
        String url = item.getTtsResDownloadUrl();
        if (URLUtil.isValidUrl(url)) {
            SeriesAsyncWorker.create()
                    .next(new Work() {
                        @Override
                        public void doWork(Object lastResult) {
                            RequestManager.addSkuToBuyList(item.getId(), ResourceType.ASSISTANT, new ResultCallback<XMResult<Object>>() {
                                @Override
                                public void onSuccess(XMResult<Object> result) {
                                    if (result != null && result.isSuccess()) {
                                        doNext();
                                    } else {
                                        XMToast.toastException(mContext, R.string.hint_download_error);
                                    }
                                }

                                @Override
                                public void onFailure(int code, String msg) {
                                    XMToast.toastException(mContext, R.string.hint_download_error);
                                }
                            });
                        }
                    })
                    .next(new Work() {
                        @Override
                        public void doWork(Object lastResult) {
                            TTSDownload.getInstance().start(item);
                        }
                    })
                    .start();
        } else {
            XMToast.toastException(mContext, R.string.invalid_download_url);
        }
    }

    public void onStop() {
        releaseAudition();
    }

    private void initAuditionAndPlay(String audioUrl) {
        releaseAudition();
        mAuditionHelper = new AudioAuditionHelper();
        mAuditionHelper.init(mContext, audioUrl, new AudioAuditionHelper.PlayCallback() {
            @Override
            public void onStart() {
                onNotifyDataSetChanged();
            }

            @Override
            public void onStop() {
                onNotifyDataSetChanged();
            }

            @Override
            public void onError() {
                XMToast.toastException(mContext, R.string.play_audio_error);
                onNotifyDataSetChanged();
            }
        });
        //        mAuditionHelper.start();
    }

    protected void releaseAudition() {
        if (mAuditionHelper != null)
            mAuditionHelper.release();
        mAuditionHelper = null;
    }

    private boolean isCurAuditionPlaying() {
        return mAuditionHelper != null && mAuditionHelper.isPlaying();
    }

    private String getCurAuditionUrl() {
        if (mAuditionHelper != null)
            return mAuditionHelper.getPlayUrl();
        return null;
    }

}

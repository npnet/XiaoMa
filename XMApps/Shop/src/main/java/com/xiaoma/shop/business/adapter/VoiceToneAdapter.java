package com.xiaoma.shop.business.adapter;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.chad.library.adapter.base.BaseViewHolder;
import com.xiaoma.model.ItemEvent;
import com.xiaoma.shop.R;
import com.xiaoma.shop.business.download.DownloadStatus;
import com.xiaoma.shop.business.model.SkusBean;
import com.xiaoma.shop.business.tts.TTSDownload;
import com.xiaoma.shop.business.tts.TTSUsing;
import com.xiaoma.shop.business.ui.theme.VoiceToneFragment;
import com.xiaoma.shop.business.ui.view.ProgressButton;
import com.xiaoma.shop.common.constant.ResourceType;
import com.xiaoma.shop.common.track.ShopTrackManager;
import com.xiaoma.shop.common.util.PriceUtils;
import com.xiaoma.shop.common.util.UnitConverUtils;
import com.xiaoma.utils.ListUtils;

import java.util.List;

/**
 * Created by Gillben
 * date: 2019/3/5 0005
 * <p>
 * 语音音色
 */
public class VoiceToneAdapter extends AbsShopAdapter<SkusBean> {
    private RequestManager mRequestManager;
    private TTSAdapterCallback mCallback;

    public VoiceToneAdapter(RequestManager requestManager, TTSAdapterCallback callback) {
        mRequestManager = requestManager;
        mCallback = callback;
        setHasStableIds(true);
    }

    public static int findPositionByUrl(List<SkusBean> data, String downUrl) {
        if (TextUtils.isEmpty(downUrl)) return -1;
        for (int i = 0; i < data.size(); i++) {
            if (downUrl.equals(data.get(i).getTtsResDownloadUrl())) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public long getItemId(int position) {
        SkusBean bean = getItem(position);
        return bean != null ? bean.getId() : RecyclerView.NO_ID;
    }

    @Override
    protected void convert(BaseViewHolder helper, final SkusBean item) {
        ImageView coverIV = helper.getView(R.id.iv_preview_image);
        mRequestManager.load(item.getIcon())
                .placeholder(R.drawable.place_holder)
                .centerInside()
                .transform(new RoundedCorners(10))
                .into(coverIV);

        ImageView cornerMarkIV = helper.getView(R.id.iv_subscript_icon);
        mRequestManager.load(item.getTypeIcon())
                .placeholder(R.drawable.bg_tag_default)
                .into(cornerMarkIV);

        helper.setText(R.id.tv_preview_name, item.getThemeName())
                .setText(R.id.tv_preview_size,
                        mContext.getString(R.string.str_buy_count,
                                UnitConverUtils.moreThanToConvert(item.getUsedNum() + item.getDefaultShowNum())))
                .setGone(R.id.group_purchase_trial, false);


        handlePrice(helper, item);
        handleBtn(helper, item);
        adapterTryPlay(helper, item);
    }

    private void adapterTryPlay(BaseViewHolder helper, final SkusBean item) {
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

    private void handlePrice(BaseViewHolder helper, SkusBean item) {
        if (PriceUtils.isFree(item.getDiscountScorePrice(), item.getDiscountPrice(), true)) {
            helper.setText(R.id.tvDiscountRMB, R.string.state_free)
                    .setVisible(R.id.layout_rmb, true)
                    .setGone(R.id.tvOriginalRMB, false)
                    .setGone(R.id.layout_coin, false);
        } else {
            setCoinPrice(helper, item);
            setRMBPrice(helper, item);
        }
    }

    private void handleBtn(BaseViewHolder helper, final SkusBean item) {
        ProgressButton centerBtn = helper.getView(R.id.pbBtnCenter);
        TTSDownload download = TTSDownload.getInstance();
        if (TTSUsing.isTTSUsing(item)) {
            // 使用中
            centerBtn.setEnabled(false);
            centerBtn.updateStateAndProgress(0, mContext.getString(R.string.state_using));
            centerBtn.setOnClickListener(null);
        } else if (download.isDownloading(item)) {
            // 下载中
            long curLen = 0, totalLen = -1;
            DownloadStatus status = download.getDownloadStatus(item);
            if (status != null) {
                curLen = status.currentLength;
                totalLen = status.totalLength;
            }
            centerBtn.setEnabled(true);
            centerBtn.updateStateAndProgress(curLen, totalLen);
            centerBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mCallback != null) {
                        mCallback.onDownloading(item);
                    }
                }
            });
        } else {
            if (item.isBuy()
                    || PriceUtils.isFree(item.getDiscountPrice(), item.getDiscountScorePrice(), true)) {
                // 已购买或者免费
                String downUrl = item.getTtsResDownloadUrl();
                if (TextUtils.isEmpty(downUrl)) {
                    //下载url为空,判断为内置音色
                    centerBtn.setEnabled(true);
                    centerBtn.updateStateAndProgress(0, mContext.getString(R.string.state_use));
                    centerBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (mCallback != null) {
                                mCallback.onUseTTS(item);
                            }
                        }
                    });
                } else {
                    if (download.isDownloadSuccess(item)) {
                        //下载url为空,判断为内置音色
                        centerBtn.setEnabled(true);
                        centerBtn.updateStateAndProgress(0, mContext.getString(R.string.state_use));
                        centerBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (mCallback != null) {
                                    mCallback.onUseTTS(item);
                                }
                            }
                        });
                    } else {
                        // 需要下载
                        centerBtn.setEnabled(true);
                        centerBtn.updateStateAndProgress(0, mContext.getString(R.string.state_download));
                        centerBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (mCallback != null) {
                                    mCallback.onDownload(item);
                                }
                            }
                        });
                    }
                }
            } else {
                // 未购买
                centerBtn.setEnabled(true);
                centerBtn.updateStateAndProgress(0, mContext.getString(R.string.state_buy));
                centerBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mCallback != null) {
                            ShopTrackManager.newSingleton().setBaseInfo(ResourceType.ASSISTANT, item.toTrackString(), VoiceToneFragment.class.getName());
                            mCallback.onBuy(item);
                        }
                    }
                });
            }
        }
    }

    private void setCoinPrice(BaseViewHolder helper, SkusBean item) {
        int scorePrice = item.getScorePrice();
        int discountScorePrice = item.getDiscountScorePrice();
        if (PriceUtils.isFree(scorePrice, discountScorePrice, false)) {
            helper.setGone(R.id.layout_coin, false);
        } else {
            helper.setVisible(R.id.layout_coin, true);
            if (scorePrice == discountScorePrice) {
                helper.setText(R.id.tvScoreDiscount, UnitConverUtils.formatIntValue(scorePrice))
                        .setGone(R.id.tvScoreOriginal, false)
                        .setVisible(R.id.layout_coin, true);
            } else {
                helper.setText(R.id.tvScoreDiscount, UnitConverUtils.formatIntValue(discountScorePrice))
                        .setText(R.id.tvScoreOriginal, UnitConverUtils.formatIntValue(scorePrice))
                        .setVisible(R.id.tvScoreOriginal, true)
                        .setVisible(R.id.layout_coin, true);
            }
        }
    }

    private void setRMBPrice(BaseViewHolder helper, SkusBean item) {
        double originalPrice = item.getPrice();
        double discountPrice = item.getDiscountPrice();

        if (PriceUtils.isFree(originalPrice, discountPrice, false)) {
            helper.setGone(R.id.layout_rmb, false);
        } else {
            if (originalPrice == discountPrice) {
                helper.setText(R.id.tvDiscountRMB, mContext.getString(R.string.rmb_price, PriceUtils.formatPrice(discountPrice)))
                        .setVisible(R.id.tvDiscountRMB, true)
                        .setGone(R.id.tvOriginalRMB, false);
            } else {
                helper.setText(R.id.tvDiscountRMB, mContext.getString(R.string.rmb_price, PriceUtils.formatPrice(discountPrice)))
                        .setText(R.id.tvOriginalRMB, PriceUtils.formatPrice(originalPrice))
                        .setVisible(R.id.tvDiscountRMB, true)
                        .setVisible(R.id.tvOriginalRMB, true);
            }
        }
    }

    @Override
    public ItemEvent returnPositionEventMsg(int position) {
        return new ItemEvent(getData().get(position).getThemeName(), getData().get(position).getId() + "");
    }

    @Override
    public SkusBean searchItemByProductId(long productId) {
        if (ListUtils.isEmpty(getData())) return null;
        for (int i = 0; i < getData().size(); i++) {
            if (productId == getData().get(i).getId()) {
                return getData().get(i);
            }
        }
        return null;
    }
}

package com.xiaoma.shop.business.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.chad.library.adapter.base.BaseViewHolder;
import com.xiaoma.model.ItemEvent;
import com.xiaoma.shop.R;
import com.xiaoma.shop.business.download.DownloadStatus;
import com.xiaoma.shop.business.download.impl.SkinDownload;
import com.xiaoma.shop.business.model.SkinVersionsBean;
import com.xiaoma.shop.business.skin.SkinUsing;
import com.xiaoma.shop.business.ui.view.ProgressButton;
import com.xiaoma.shop.common.constant.ShopContract;
import com.xiaoma.shop.common.util.PriceUtils;
import com.xiaoma.utils.ListUtils;
import com.xiaoma.utils.StringUtil;

import java.text.DecimalFormat;

/**
 * Created by Gillben
 * date: 2019/3/5 0005
 * <p>
 * 个性主题
 */
public class PersonalityThemeAdapterV2 extends AbsShopAdapter<SkinVersionsBean> {
    private static final String TAG = PersonalityThemeAdapterV2.class.getSimpleName();

    private RequestManager mRequestManager;

    public PersonalityThemeAdapterV2(RequestManager requestManager, Callback callback) {
        super(R.layout.item_personal_skin);
        mRequestManager = requestManager;
        mCallback = callback;
    }

    @Override
    protected BaseViewHolder onCreateDefViewHolder(ViewGroup parent, int viewType) {
        return new SkinHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_abs_shop, parent, false));
    }

    @Override
    protected void convert(BaseViewHolder holder, SkinVersionsBean skin) {
        if (skin == null)
            return;
        if (holder instanceof SkinHolder) {
            SkinHolder skinHolder = (SkinHolder) holder;
            // 皮肤信息
            bindSkinInfo(skinHolder, skin);
            // 价格
            bindSkinPrice(skinHolder, skin);
            // 按钮
            bindBottomBtn(skinHolder, skin);
        }
    }

    private void bindSkinInfo(PersonalityThemeAdapterV2.SkinHolder holder, final SkinVersionsBean item) {
        Context context = holder.itemView.getContext();

        mRequestManager.load(item.getIconPathUrl())
                .placeholder(R.drawable.place_holder)
                .error(R.drawable.place_holder)
                .centerCrop()
                .transform(new RoundedCorners(10))
                .into((ImageView) holder.getView(R.id.iv_preview_image));

        mRequestManager.load(item.getTagPath())
                .placeholder(R.drawable.bg_tag_default)
                .into((ImageView) holder.getView(R.id.iv_subscript_icon));

        holder.setText(R.id.tv_preview_name, StringUtil.optString(item.getAppName()));

        String showCount;
        int showUseCountNum = item.getUsedNum() + item.getDefaultShowNum();
        if (showUseCountNum >= 10000) {
            showCount = (new DecimalFormat("#0.0").format(showUseCountNum * 1.0f / 10000)) + "W";
        } else {
            showCount = String.valueOf(showUseCountNum);
        }
        holder.setText(R.id.tv_preview_size, context.getString(R.string.str_buy_count, showCount));

        holder.getView(R.id.iv_preview_image).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCallback != null) {
                    mCallback.onSkinItemClick(item);
                }
            }
        });
    }

    private void bindSkinPrice(PersonalityThemeAdapterV2.SkinHolder holder, SkinVersionsBean item) {
        // 价格
        int pay = ShopContract.Pay.DEFAULT;
        if (item.getDiscountScorePrice() > 0) { // 购买金额计算只用折扣价，原价不参与购买逻辑
            pay += ShopContract.Pay.COIN;
        }
        if (item.getDiscountPrice() > 0) {
            pay += ShopContract.Pay.RMB;
        }
        item.setPay(pay);
        switch (pay) {
            case ShopContract.Pay.DEFAULT:
            default:
                holder.setGone(R.id.tvOriginalRMB, false)
                        .setGone(R.id.layout_coin, false)
                        .setVisible(R.id.layout_rmb, true)
                        .setText(R.id.tvDiscountRMB, R.string.state_free);
                break;
            case ShopContract.Pay.COIN:
                holder.setGone(R.id.layout_rmb, false)
                        .setVisible(R.id.layout_coin, true);
                updateCoinPrice(holder, item);
                break;
            case ShopContract.Pay.RMB:
                holder.setGone(R.id.layout_coin, false)
                        .setVisible(R.id.layout_rmb, true)
                        .setVisible(R.id.tvDiscountRMB, true);
                updateRMBPrice(holder, item);
                break;
            case ShopContract.Pay.COIN_AND_RMB:
                holder.setVisible(R.id.layout_coin, true)
                        .setVisible(R.id.layout_rmb, true);
                updateCoinPrice(holder, item);
                updateRMBPrice(holder, item);
                break;
        }
    }

    private void updateCoinPrice(BaseViewHolder holder, SkinVersionsBean item) {
        int discountScorePrice = item.getDiscountScorePrice();
        int scorePrice = item.getScorePrice();
        if (discountScorePrice == scorePrice) {
            holder.setGone(R.id.tvScoreOriginal, false)
                    .setText(R.id.tvScoreDiscount, PriceUtils.formatPrice(discountScorePrice));
        } else {
            holder.setVisible(R.id.tvScoreOriginal, true)
                    .setText(R.id.tvScoreDiscount, PriceUtils.formatPrice(discountScorePrice))
                    .setText(R.id.tvScoreOriginal, PriceUtils.formatPrice(scorePrice));
        }
    }

    private void updateRMBPrice(BaseViewHolder holder, SkinVersionsBean item) {
        double discountPrice = item.getDiscountPrice();
        double price = item.getPrice();
        if (discountPrice == price) {
            holder.setGone(R.id.tvOriginalRMB, false)
                    .setText(R.id.tvDiscountRMB, mContext.getString(R.string.rmb_price, PriceUtils.formatPrice(discountPrice)));
        } else {
            holder.setVisible(R.id.tvOriginalRMB, true)
                    .setText(R.id.tvDiscountRMB, mContext.getString(R.string.rmb_price, PriceUtils.formatPrice(discountPrice)))
                    .setText(R.id.tvOriginalRMB, PriceUtils.formatPrice(price));
        }
    }

    private void bindBottomBtn(PersonalityThemeAdapterV2.SkinHolder holder, final SkinVersionsBean item) {
        Context context = holder.itemView.getContext();
        final SkinDownload skinDownload = SkinDownload.getInstance();

        ProgressButton pBtnLeft = holder.getView(R.id.pbBtnLeft);
        ProgressButton pBtnCenter = holder.getView(R.id.pbBtnCenter);
        ProgressButton pBtnRight = holder.getView(R.id.pbBtnRight);
        // 已购买或免费使用
        final boolean hasBuy = item.isIsBuy()
                || (item.getDiscountScorePrice() <= 0 && item.getDiscountPrice() <= 0);
        if (SkinUsing.isUsing(item)) {
            if (hasBuy) {
                holder.setGone(R.id.group_purchase_trial, false);
                pBtnLeft.setVisibility(View.GONE);
                pBtnRight.setVisibility(View.GONE);
                // 正在使用
                pBtnCenter.setVisibility(View.VISIBLE);
                pBtnCenter.updateStateAndProgress(0, context.getString(R.string.state_using));
                pBtnCenter.setEnabled(false);
            } else {
                holder.setGone(R.id.group_purchase_trial, true);
                pBtnCenter.setVisibility(View.GONE);
                // 试用中
                pBtnLeft.setVisibility(View.VISIBLE);
                pBtnLeft.updateStateAndProgress(0, context.getString(R.string.state_buy));
                pBtnLeft.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mCallback != null) {
                            mCallback.onBuySkin(item);
                        }
                    }
                });
                pBtnLeft.setEnabled(true);

                pBtnRight.setVisibility(View.VISIBLE);
                pBtnRight.updateStateAndProgress(0, context.getString(R.string.state_trying));
                pBtnRight.setEnabled(false);
            }
        } else if (skinDownload.isDownloading(item)) {
            holder.setGone(R.id.group_purchase_trial, false);
            pBtnLeft.setVisibility(View.GONE);
            pBtnRight.setVisibility(View.GONE);

            pBtnCenter.setVisibility(View.VISIBLE);
            long curDownloadLen = 0, totalDownloadLen = -1;
            DownloadStatus status = skinDownload.getDownloadStatus(item);
            if (status != null) {
                curDownloadLen = status.currentLength;
                totalDownloadLen = status.totalLength;
            }
            pBtnCenter.updateStateAndProgress(curDownloadLen, totalDownloadLen);
            pBtnCenter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mCallback != null) {
                        mCallback.onSkinDownloading(item);
                    }
                }
            });
            pBtnCenter.setEnabled(true);
        } else {
            if (hasBuy) {
                holder.setGone(R.id.group_purchase_trial, false);
                pBtnLeft.setVisibility(View.GONE);
                pBtnRight.setVisibility(View.GONE);

                pBtnCenter.setVisibility(View.VISIBLE);
                if(skinDownload.isDownloadSuccess(item)){
                    pBtnCenter.updateStateAndProgress(0, context.getString(R.string.state_use));
                }else{
                    pBtnCenter.updateStateAndProgress(0, context.getString(R.string.load_text));
                }

                pBtnCenter.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mCallback != null) {
                            if (skinDownload.isDownloadSuccess(item)) {
                                mCallback.onUseSkin(item);
                            } else {
                                mCallback.onDownloadSkin(item);
                            }
                        }
                    }
                });
                pBtnCenter.setEnabled(true);
            } else {
                // 未购买
                if (item.getCanTry() >= 0
                        && item.getTrialTime() > 0) {
                    holder.setGone(R.id.group_purchase_trial, true);
                    pBtnCenter.setVisibility(View.GONE);
                    // 可试用
                    pBtnLeft.setVisibility(View.VISIBLE);
                    pBtnLeft.updateStateAndProgress(0, context.getString(R.string.state_buy));
                    pBtnLeft.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (mCallback != null) {
                                mCallback.onBuySkin(item);
                            }
                        }
                    });
                    pBtnLeft.setEnabled(true);

                    pBtnRight.setVisibility(View.VISIBLE);
                    pBtnRight.updateStateAndProgress(0, context.getString(R.string.state_trial));
                    pBtnRight.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (mCallback != null) {
                                mCallback.onTrialSkin(item);
                            }
                        }
                    });
                    pBtnRight.setEnabled(true);
                } else {
                    holder.setGone(R.id.group_purchase_trial, false);
                    pBtnLeft.setVisibility(View.GONE);
                    pBtnRight.setVisibility(View.GONE);
                    // 不能试用
                    pBtnCenter.setVisibility(View.VISIBLE);
                    pBtnCenter.updateStateAndProgress(0, context.getString(R.string.state_buy));
                    pBtnCenter.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (mCallback != null) {
                                mCallback.onBuySkin(item);
                            }
                        }
                    });
                    pBtnCenter.setEnabled(true);
                }
            }
        }
    }

    @Override
    public ItemEvent returnPositionEventMsg(int position) {
        SkinVersionsBean skin = getItem(position);
        if (skin != null) {
            return new ItemEvent(skin.getAppName(), skin.getId() + "");
        }
        return null;
    }

    @Override
    public SkinVersionsBean searchItemByProductId(long productId) {
        if (ListUtils.isEmpty(getData())) return null;
        for (int i = 0; i < getData().size(); i++) {
            if (productId == getData().get(i).getId()) {
                return getData().get(i);
            }
        }
        return null;
    }

    static class SkinHolder extends BaseViewHolder {
        SkinHolder(View itemView) {
            super(itemView);
            //R.layout.item_abs_shop
        }
    }

    private Callback mCallback;

    public interface Callback {
        void onSkinItemClick(SkinVersionsBean item);

        void onUseSkin(SkinVersionsBean item);

        void onDownloadSkin(SkinVersionsBean item);

        void onSkinDownloading(SkinVersionsBean item);

        void onBuySkin(SkinVersionsBean item);

        void onTrialSkin(SkinVersionsBean item);
    }
}

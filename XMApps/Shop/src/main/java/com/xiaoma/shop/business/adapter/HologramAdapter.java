package com.xiaoma.shop.business.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;

import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseViewHolder;
import com.xiaoma.carlib.store.HologramRepo;
import com.xiaoma.component.AppHolder;
import com.xiaoma.model.ItemEvent;
import com.xiaoma.shop.R;
import com.xiaoma.shop.business.hologram.HologramUsing;
import com.xiaoma.shop.business.model.HoloListModel;
import com.xiaoma.shop.business.ui.view.ProgressButton;
import com.xiaoma.shop.common.constant.Hologram3DContract;
import com.xiaoma.shop.common.util.Hologram3DUtils;
import com.xiaoma.shop.common.util.PriceUtils;
import com.xiaoma.shop.common.util.UnitConverUtils;
import com.xiaoma.thread.ThreadDispatcher;
import com.xiaoma.utils.ListUtils;

import java.util.List;
import java.util.Objects;

/**
 * <pre>
 *     author : wutao
 *     time   : 2019/01/24
 *     desc   : 全息影像
 * </pre>
 */
public class HologramAdapter extends AbsShopAdapter<HoloListModel> {
    private RequestManager mRequestManager;
    private int mSelfHologramIndex = -1;

    public HologramAdapter(RequestManager requestManager) {
        mRequestManager = requestManager;
        setHasStableIds(true);
    }

    public void notifyAtMain() {
        ThreadDispatcher.getDispatcher().postOnMain(new Runnable() {
            @Override
            public void run() {
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public long getItemId(int position) {
        HoloListModel item = getItem(position);
        return item != null ? item.getId() : RecyclerView.NO_ID;
    }

    public void notifyUseCount(int position, int showUseCount) {
        notifyItemChanged(position, showUseCount);
    }

    @Override
    public ItemEvent returnPositionEventMsg(int position) {
        return new ItemEvent(getData().get(position).getName(), position + "");
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position, @NonNull List<Object> payloads) {
        if (ListUtils.isEmpty(payloads)) {
            super.onBindViewHolder(holder, position, payloads);
        } else {
            Object o = payloads.get(0);
            if (o instanceof Integer) {
                holder.setText(R.id.tv_preview_size, mContext.getString(R.string.str_buy_count, UnitConverUtils.moreThanToConvert((Integer) o)));
            }
        }
    }

    @Override
    protected void convert(BaseViewHolder helper, HoloListModel item) {
        ImageView coverIV = helper.getView(R.id.iv_preview_image);
        ImageView cornerMarkIV = helper.getView(R.id.iv_subscript_icon);

        Boolean holoState = item.get3DState();
        RequestOptions transform = new RequestOptions()
                .placeholder((holoState != null) ? R.drawable.def_3d_self : R.drawable.place_holder)
                .centerCrop()
                .transform(new RoundedCorners(10));

        mRequestManager.load(item.getPicUrl())
                .apply(transform)
                .into(coverIV);
        mRequestManager.load(item.getIconUrl())
                .placeholder(R.drawable.bg_tag_default)
                .into(cornerMarkIV);

        helper.setGone(R.id.pbBtnLeft, false)
                .setGone(R.id.pbBtnRight, false);

        if (holoState == null) {
            helper.setText(R.id.tv_preview_name, item.getName())
                    .setText(R.id.tv_preview_size, mContext.getString(R.string.str_buy_count, UnitConverUtils.moreThanToConvert(item.getUsedNum() + item.getDefaultUsedNum())))
                    .setGone(R.id.tv_show, false)
                    .addOnClickListener(R.id.iv_preview_image)
                    .setVisible(R.id.pbBtnCenter, true)
                    .addOnClickListener(R.id.iv_preview_image)
                    .addOnClickListener(R.id.pbBtnCenter);

            handlePrice(helper, item);
            handleBtn(helper, item);
        } else {
            helper.setGone(R.id.layout_coin, false)
                    .setVisible(R.id.layout_rmb, false)
                    .setGone(R.id.tv_preview_size, false)
                    .setGone(R.id.tv_preview_name, false)
                    .setGone(R.id.pbBtnCenter, holoState)
                    .setGone(R.id.tv_preview_name, false)
                    .setVisible(R.id.tv_show, true);
            if (holoState) {
                helper.addOnClickListener(R.id.pbBtnCenter);
                handleSelf3D(helper, item);
            }
        }
    }

    private void handlePrice(BaseViewHolder helper, HoloListModel item) {
        if (PriceUtils.isFree(item.getDiscountPrice(), item.getDiscountScorePrice(), false)) {
            helper.setGone(R.id.tvOriginalRMB, false)
                    .setGone(R.id.layout_coin, false)
                    .setVisible(R.id.layout_rmb, true)
                    .setText(R.id.tvDiscountRMB, R.string.state_free);
        } else {
            setCoinPrice(helper, item);
            setRMBPrice(helper, item);
        }
    }

    private void handleBtn(BaseViewHolder helper, final HoloListModel item) {
        final ProgressButton centerPBtn = helper.getView(R.id.pbBtnCenter);
        centerPBtn.setEnabled(true);
        Context context = centerPBtn.getContext();
        int usingRole = HologramRepo.getUsingRoleId(context);

        if (Objects.equals(item.getCode(), String.valueOf(usingRole))) {
            centerPBtn.updateStateAndProgress(0, context.getString(R.string.state_using));
//                centerPBtn.updateText(context.getString(R.string.state_using));
            setState(false, centerPBtn);
        } else if (PriceUtils.isFree(item.getDiscountPrice(), item.getDiscountScorePrice(), true)) {
            centerPBtn.updateText(context.getString(R.string.state_use));
            setState(true, centerPBtn);
        } else {
            setState(true, centerPBtn);
            centerPBtn.updateText(context.getString(item.getUserBuyFlag() == 1 ? R.string.state_use : R.string.state_buy));
        }
    }

    private void handleSelf3D(BaseViewHolder helper, final HoloListModel item) {
        final ProgressButton centerPBtn = helper.getView(R.id.pbBtnCenter);
        int[] hologramState = Hologram3DUtils.checkItemState(item);
        item.setState(hologramState[0]);
        setState(hologramState[2] == Hologram3DUtils.ENABL, centerPBtn);
        if (hologramState[0] == Hologram3DContract.STATE_DOWNLOAD_PROGRESS
                || hologramState[1] == Hologram3DContract.STATE_INSTALL_PROGRESS) {
            centerPBtn.updateStateAndProgress(hologramState[1], 100);
        } else {
            centerPBtn.updateStateAndProgress(0, 100);
            centerPBtn.updateText(mContext.getString(hologramState[1]));
            if (hologramState[0] == Hologram3DContract.STATE_USING) {
                if (!HologramUsing.isRoleUsing(mContext, item)) {
                    HologramUsing.useRole(AppHolder.getInstance().getAppContext(), item);
                }
            }
        }
    }

    public void setState(boolean enable, ProgressButton progressBtn) {
        progressBtn.setEnabled(enable);
//        progressBtn.updateTextColor(enable ? Color.WHITE : Color.GRAY);
        progressBtn.updateTextColorSkin(enable ? R.style.text_nousing : R.style.text_using);
    }

    private void setCoinPrice(BaseViewHolder helper, HoloListModel item) {
        int originalPrice = item.getNeedScore();
        int discountPrice = item.getDiscountScorePrice();

        if (PriceUtils.isFree(originalPrice, discountPrice, false)) {
            helper.setGone(R.id.layout_coin, false);
        } else {
            helper.setVisible(R.id.layout_coin, true);
            if (discountPrice == originalPrice) {
                helper.setGone(R.id.tvScoreOriginal, false)
                        .setText(R.id.tvScoreDiscount, PriceUtils.formatPrice(discountPrice));
            } else {
                helper.setVisible(R.id.tvScoreOriginal, true)
                        .setText(R.id.tvScoreDiscount, PriceUtils.formatPrice(discountPrice))
                        .setText(R.id.tvScoreOriginal, PriceUtils.formatPrice(originalPrice));
            }
        }
    }

    private void setRMBPrice(BaseViewHolder helper, HoloListModel item) {
        double originalPrice = item.getPrice();
        double discountPrice = item.getDiscountPrice();
        if (PriceUtils.isFree(originalPrice, discountPrice, false)) {
            helper.setGone(R.id.layout_rmb, false);
        } else {
            helper.setVisible(R.id.layout_rmb, true);
            if (originalPrice == discountPrice) {
                helper.setGone(R.id.tvOriginalRMB, false)
                        .setText(R.id.tvDiscountRMB, mContext.getString(R.string.rmb_price, PriceUtils.formatPrice(discountPrice)));
            } else {
                helper.setVisible(R.id.tvOriginalRMB, true)
                        .setText(R.id.tvDiscountRMB, mContext.getString(R.string.rmb_price, PriceUtils.formatPrice(discountPrice)))
                        .setText(R.id.tvOriginalRMB, PriceUtils.formatPrice(originalPrice));
            }
        }
    }

    @Override
    public HoloListModel searchItemByProductId(long productId) {
        if (ListUtils.isEmpty(getData())) return null;
        for (int i = 0; i < getData().size(); i++) {
            if (productId == getData().get(i).getId()) {
                return getData().get(i);
            }
        }
        return null;
    }
}

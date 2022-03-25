package com.xiaoma.shop.business.adapter.bought;

import android.content.Context;

import com.bumptech.glide.RequestManager;
import com.chad.library.adapter.base.BaseViewHolder;
import com.xiaoma.carlib.store.HologramRepo;
import com.xiaoma.component.AppHolder;
import com.xiaoma.shop.R;
import com.xiaoma.shop.business.hologram.HologramUsing;
import com.xiaoma.shop.business.model.HoloListModel;
import com.xiaoma.shop.business.ui.view.ProgressButton;
import com.xiaoma.shop.common.constant.Hologram3DContract;
import com.xiaoma.shop.common.util.Hologram3DUtils;
import com.xiaoma.shop.common.util.PriceUtils;
import com.xiaoma.thread.ThreadDispatcher;

import java.util.Objects;

/**
 * Created by Gillben
 * date: 2019/3/5 0005
 * <p>
 * 已购买全息影像
 */
public class BoughtHologramAdapter extends BaseBoughtAdapter<HoloListModel> {
    private int mSelfHologramIndex = -1;

    public BoughtHologramAdapter(RequestManager requestManager) {
        super(requestManager);
    }

    @Override
    protected void convert(BaseViewHolder helper, HoloListModel item) {
        super.convert(helper, item);
        loadRoundCircleUrl(helper, item.getPicUrl());
        loadTagUrl(helper, item.getIconUrl());

        helper.setGone(R.id.iv_bought_test_play, false)
        ;

        //一键清理缓存
        //        ImageView selectCacheImage = helper.getView(R.id.iv_select_cache);
        //        if (isCleanCache()) {
        //            selectCacheImage.setVisibility(View.VISIBLE);
        //            if (item.isSelect()) {
        //                selectCacheImage.setImageLevel(1);
        //            } else {
        //                selectCacheImage.setImageLevel(0);
        //            }
        //        } else {
        //            selectCacheImage.setVisibility(View.GONE);
        //            item.setSelect(false);
        //            selectCacheImage.setImageLevel(0);
        //        }
        Boolean state = item.get3DState();
        if (state == null) {
            handleBtn(helper, item);
            helper.setVisible(R.id.tv_preview_size, true)
                    .setVisible(R.id.tv_bought_name, true)
                    .setVisible(R.id.tv_bought_number, true)
                    .setGone(R.id.tvSelfDefine, false)
                    .setText(R.id.tv_bought_name, item.getName())
                    .setText(R.id.tv_bought_number, mContext.getString(R.string.down_load_number, String.valueOf(item.getUsedNum() + item.getDefaultUsedNum())))
                    .addOnClickListener(R.id.iv_bought_icon);
        } else {
            helper.setVisible(R.id.tvSelfDefine, true)
                    .setGone(R.id.tv_preview_size, false)
                    .setGone(R.id.tv_bought_number, false)
                    .setGone(R.id.tv_bought_name, false);
            if (state) {
                handleSelf3D(helper, item);
            }
        }
    }

    private void handleBtn(BaseViewHolder helper, HoloListModel item) {
        ProgressButton centerPBtn = helper.getView(R.id.bought_operation_bt);
        centerPBtn.setEnabled(true);
        Context context = centerPBtn.getContext();
        int usingRole = HologramRepo.getUsingRoleId(context);
        if (Objects.equals(item.getCode(), String.valueOf(usingRole))) {
            centerPBtn.updateText(context.getString(R.string.state_using));
            setState(false, centerPBtn);
        } else if (PriceUtils.isFree(item.getDiscountPrice(), item.getDiscountScorePrice(), true)) {
            centerPBtn.updateText(context.getString(R.string.state_use));
            setState(true, centerPBtn);
        } else {
            setState(true, centerPBtn);
            centerPBtn.updateText(context.getString(item.getUserBuyFlag() == 1 ? R.string.state_use : R.string.state_buy));
        }
    }

    private void setState(boolean enable, ProgressButton progressBtn) {
        progressBtn.setEnabled(enable);
        //        progressBtn.updateTextColor(enable ? Color.WHITE : Color.GRAY);
        progressBtn.updateTextColorSkin(enable ? R.style.text_nousing : R.style.text_using);
    }

    public void notifyAtMain() {
        ThreadDispatcher.getDispatcher().postOnMain(new Runnable() {
            @Override
            public void run() {
                int itemCount = getItemCount();
                if (itemCount <= mSelfHologramIndex || mSelfHologramIndex < 0) {
                    notifyDataSetChanged();
                } else {
                    HoloListModel item = getItem(mSelfHologramIndex);
                    Boolean state = item.get3DState();
                    if (state != null && state) {
                        notifyItemChanged(mSelfHologramIndex);
                    } else {
                        notifyDataSetChanged();
                    }
                }
            }
        });
    }

    private void handleSelf3D(BaseViewHolder helper, final HoloListModel item) {
        final ProgressButton centerPBtn = helper.getView(R.id.bought_operation_bt);
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
}

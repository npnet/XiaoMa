package com.xiaoma.pet.adapter;

import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.xiaoma.image.ImageLoader;
import com.xiaoma.pet.R;
import com.xiaoma.pet.model.RewardDetails;

/**
 * <pre>
 *   @author Create by on Gillben
 *   date:   2019/5/24 0024 17:35
 *   desc:   升级奖励适配器
 * </pre>
 */
public class UpgradeRewardAdapter extends BaseQuickAdapter<RewardDetails, BaseViewHolder> {


    public UpgradeRewardAdapter() {
        super(R.layout.pet_upgrade_reward_item);
    }

    @Override
    protected void convert(BaseViewHolder helper, RewardDetails item) {
        ImageLoader.with(mContext)
                .load(item.getGoodsIcon())
                .into((ImageView) helper.getView(R.id.iv_reward_item));
    }
}

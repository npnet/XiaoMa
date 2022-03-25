package com.xiaoma.shop.business.adapter;

import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.xiaoma.shop.R;
import com.xiaoma.shop.business.model.HologramSkill;

/**
 * Created by Gillben
 * date: 2019/3/5 0005
 * <p>
 * 全息影像 技能
 */
public class HologramSkillAdapter extends BaseQuickAdapter<HologramSkill, BaseViewHolder> {
    private int mCurSelectIndex = -1;

    public HologramSkillAdapter() {
        super(R.layout.item_hologram_skill);
    }

    public void notifyItemSelected(int position) {
        if (position == mCurSelectIndex) return;
        if (mCurSelectIndex == -1) {
            mCurSelectIndex = position;
        } else {
            int temp = mCurSelectIndex;
            mCurSelectIndex = position;
            notifyItemChanged(temp);

        }
        notifyItemChanged(position);
    }


    @Override
    protected void convert(BaseViewHolder helper, HologramSkill item) {
        helper.setText(R.id.tv_hologram_skill_name, item.getName())
                .setText(R.id.tv_hologram_skill_desc, item.getDescription());

        TextView tvSkillName = helper.getView(R.id.tv_hologram_skill_name);
        tvSkillName.setSelected(helper.getAdapterPosition() == mCurSelectIndex);
    }
}

package com.xiaoma.vrpractice.adapter;


/*  @project_name：  XMAgateOS
 *  @package_name：  com.xiaoma.vrpractice.adapter
 *  @file_name:      SkillAdapter
 *  @author:         Rookie
 *  @create_time:    2019/6/3 15:41
 *  @description：   TODO             */

import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseViewHolder;
import com.xiaoma.image.ImageLoader;
import com.xiaoma.model.ItemEvent;
import com.xiaoma.ui.adapter.XMBaseAbstractBQAdapter;
import com.xiaoma.utils.ListUtils;
import com.xiaoma.vrpractice.R;
import com.xiaoma.model.pratice.SkillBean;
import com.xiaoma.model.pratice.UserSkillItemsBean;

import java.util.List;

public class SkillAdapter extends XMBaseAbstractBQAdapter<SkillBean, BaseViewHolder> {


    private ImageView[] ivActions;

    public SkillAdapter(@Nullable List<SkillBean> data) {
        super(R.layout.item_skill, data);

    }

    @Override
    protected void convert(BaseViewHolder helper, SkillBean item) {
        helper.setText(R.id.tv_title, mContext.getString(R.string.skill_title, (helper.getAdapterPosition() + 1)));
        helper.setText(R.id.tv_voice, item.getWord());
        ImageView ivAction1 = helper.getView(R.id.iv_action1);
        ImageView ivAction2 = helper.getView(R.id.iv_action2);
        ImageView ivAction3 = helper.getView(R.id.iv_action3);
        ImageView ivAction4 = helper.getView(R.id.iv_action4);
        ImageView ivAction5 = helper.getView(R.id.iv_action5);
        ImageView ivAction6 = helper.getView(R.id.iv_action6);
        ImageView ivAction7 = helper.getView(R.id.iv_action7);
        ImageView ivAction8 = helper.getView(R.id.iv_action8);
        ImageView ivAction9 = helper.getView(R.id.iv_action9);
        ivActions = new ImageView[]{ivAction1, ivAction2, ivAction3, ivAction4, ivAction5, ivAction6, ivAction7, ivAction8, ivAction9};
        List<UserSkillItemsBean> yomiActions = item.getUserSkillItems();
        if (ListUtils.isEmpty(yomiActions)) {
            return;
        }
        //先隐藏所有view
        for (ImageView ivAction : ivActions) {
            if (ivAction != null) {
                ivAction.setVisibility(View.GONE);
            }
        }

        for (int i = 0; i < yomiActions.size(); i++) {
            if (ivActions[i] != null) {
                ivActions[i].setVisibility(View.VISIBLE);
                ImageLoader.with(mContext).load(yomiActions.get(i).getSkillItem().getIcon()).into(ivActions[i]);
            }
        }
    }

    @Override
    public ItemEvent returnPositionEventMsg(int position) {
        return new ItemEvent(getData().get(position).getWord(), "");
    }
}

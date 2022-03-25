package com.xiaoma.vrpractice.adapter;


/*  @project_name：  XMAgateOS
 *  @package_name：  com.xiaoma.vrpractice.adapter
 *  @file_name:      AddSkillAdapter
 *  @author:         Rookie
 *  @create_time:    2019/6/4 10:32
 *  @description：   TODO             */

import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.xiaoma.model.pratice.UserSkillItemsBean;
import com.xiaoma.vrpractice.R;
import com.xiaoma.vrpractice.common.util.ActionsUtils;

import java.util.List;

public class AddSkillAdapter extends BaseMultiItemQuickAdapter<UserSkillItemsBean, BaseViewHolder> {

    public AddSkillAdapter(@Nullable List<UserSkillItemsBean> data) {
        super(data);
        //添加技能
        addItemType(UserSkillItemsBean.TYPE_ADD_SKILL, R.layout.item_add_skill);
        //显示技能
        addItemType(UserSkillItemsBean.TYPE_ACTION, R.layout.item_skill_action);
    }

    @Override
    protected void convert(BaseViewHolder helper, UserSkillItemsBean item) {
        int itemType = item.getItemType();
        if (itemType == UserSkillItemsBean.TYPE_ACTION) {
            helper.setText(R.id.tv_skill_name, item.getSkillItem().getText());
            helper.setText(R.id.tv_skill_action, ActionsUtils.getSkillTip(item));
            helper.addOnClickListener(R.id.iv_del);
        }
    }

}

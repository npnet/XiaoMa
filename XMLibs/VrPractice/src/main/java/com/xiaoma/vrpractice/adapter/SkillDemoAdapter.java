package com.xiaoma.vrpractice.adapter;


/*  @project_name：  XMAgateOS
 *  @package_name：  com.xiaoma.vrpractice.adapter
 *  @file_name:      SkillDemoAdapter
 *  @author:         Rookie
 *  @create_time:    2019/6/5 14:23
 *  @description：   TODO             */

import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseViewHolder;
import com.xiaoma.model.ItemEvent;
import com.xiaoma.ui.adapter.XMBaseAbstractBQAdapter;
import com.xiaoma.vrpractice.R;
import com.xiaoma.vrpractice.common.util.ActionsUtils;
import com.xiaoma.model.pratice.UserSkillItemsBean;

import java.util.List;

public class SkillDemoAdapter extends XMBaseAbstractBQAdapter<UserSkillItemsBean, BaseViewHolder> {

    public SkillDemoAdapter(@Nullable List<UserSkillItemsBean> data) {
        super(R.layout.item_skill_demo, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, UserSkillItemsBean item) {
        helper.setText(R.id.tv_action, item.getSkillItem().getText() + ":" + ActionsUtils.getSkillTip(item));
    }

    @Override
    public ItemEvent returnPositionEventMsg(int position) {
        return null;
    }
}

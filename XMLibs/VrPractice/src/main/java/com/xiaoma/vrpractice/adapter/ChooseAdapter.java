package com.xiaoma.vrpractice.adapter;


/*  @project_name：  XMAgateOS
 *  @package_name：  com.xiaoma.vrpractice.adapter
 *  @file_name:      ChooseAdapter
 *  @author:         Rookie
 *  @create_time:    2019/6/4 14:28
 *  @description：   TODO             */

import android.content.Context;
import android.widget.ImageView;
import android.widget.TextView;

import com.xiaoma.image.ImageLoader;
import com.xiaoma.model.ItemEvent;
import com.xiaoma.model.pratice.SkillItemBean;
import com.xiaoma.ui.adapter.XMBaseAbstractLvGvAdapter;
import com.xiaoma.ui.vh.XMLvViewHolder;
import com.xiaoma.vrpractice.R;

import java.util.List;

public class ChooseAdapter extends XMBaseAbstractLvGvAdapter<SkillItemBean> {

    public ChooseAdapter(Context context, List<SkillItemBean> datas) {
        super(context, datas, R.layout.item_choose);
    }

    @Override
    protected void convert(XMLvViewHolder viewHolder, SkillItemBean skillItemBean, int position) {
        ImageLoader.with(mContext).load(skillItemBean.getIcon()).into((ImageView) viewHolder.getView(R.id.iv_skill));
        TextView tvSkill = viewHolder.getView(R.id.tv_skill);
        tvSkill.setText(skillItemBean.getText());
        if (skillItemBean.isDark()) {
            tvSkill.setTextColor(mContext.getResources().getColor(R.color.tab_tv_color_normal));
        } else {
            tvSkill.setTextColor(mContext.getResources().getColor(R.color.white));
        }
    }

    @Override
    public ItemEvent returnPositionEventMsg(int position) {
        return null;
    }

}

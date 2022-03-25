package com.xiaoma.xting.practice.adapter;


/*  @project_name：  XMAgateOS
 *  @package_name：  com.xiaoma.xting.practice.adapter
 *  @file_name:      RadioHistoryAdapter
 *  @author:         Rookie
 *  @create_time:    2019/7/8 17:04
 *  @description：   TODO             */

import com.chad.library.adapter.base.BaseViewHolder;
import com.xiaoma.model.ItemEvent;
import com.xiaoma.ui.adapter.XMBaseAbstractBQAdapter;
import com.xiaoma.xting.R;
import com.xiaoma.xting.practice.model.RadioHistoryBean;

import java.util.List;

public class RadioHistoryAdapter extends XMBaseAbstractBQAdapter<RadioHistoryBean, BaseViewHolder> {

    public RadioHistoryAdapter(List<RadioHistoryBean> data) {
        super(R.layout.item_play_history, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, RadioHistoryBean item) {
        helper.setText(R.id.tv_history_name, item.getName());
        helper.addOnClickListener(R.id.btn_history_delete);
    }

    @Override
    public ItemEvent returnPositionEventMsg(int position) {
        return null;
    }
}

package com.xiaoma.music.practice;


/*  @project_name：  XMAgateOS
 *  @package_name：  com.xiaoma.music.practice
 *  @file_name:      MusicHistoryAdapter
 *  @author:         Rookie
 *  @create_time:    2019/7/4 19:22
 *  @description：   TODO             */

import com.chad.library.adapter.base.BaseViewHolder;
import com.xiaoma.model.ItemEvent;
import com.xiaoma.music.R;
import com.xiaoma.ui.adapter.XMBaseAbstractBQAdapter;

import java.util.List;

public class MusicHistoryAdapter extends XMBaseAbstractBQAdapter<MusicHistoryBean, BaseViewHolder> {


    public MusicHistoryAdapter(List<MusicHistoryBean> data) {
        super(R.layout.item_play_history,data);
    }

    @Override
    protected void convert(BaseViewHolder helper, MusicHistoryBean item) {
        helper.setText(R.id.tv_history_name, item.getName());
        helper.addOnClickListener(R.id.btn_history_delete);
    }

    @Override
    public ItemEvent returnPositionEventMsg(int position) {
        return null;
    }
}
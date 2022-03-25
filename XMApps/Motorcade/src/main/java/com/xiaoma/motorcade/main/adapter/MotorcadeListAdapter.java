package com.xiaoma.motorcade.main.adapter;

import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseViewHolder;
import com.xiaoma.model.ItemEvent;
import com.xiaoma.motorcade.R;
import com.xiaoma.motorcade.common.model.GroupCardInfo;
import com.xiaoma.motorcade.common.view.UserHeadView;
import com.xiaoma.ui.adapter.XMBaseAbstractBQAdapter;

import java.util.List;

/**
 * Created by ZYao.
 * Date ：2019/1/16 0016
 */
public class MotorcadeListAdapter extends XMBaseAbstractBQAdapter<GroupCardInfo, BaseViewHolder> {
    private List<GroupCardInfo> infos;

    public MotorcadeListAdapter(@Nullable List<GroupCardInfo> data) {
        super(R.layout.item_motorcade, data);
        infos = data;
    }

    @Override
    protected void convert(BaseViewHolder helper, GroupCardInfo item) {
        helper.addOnClickListener(R.id.btn_exit_motorcade);
        helper.addOnClickListener(R.id.btn_motorcade_setting);
        helper.addOnClickListener(R.id.motorcade_info_ll);
        helper.setText(R.id.tv_motorcade_name, item.getNick());
        helper.setText(R.id.tv_motorcade_id, mContext.getString(R.string.command) + item.getHxGroupId());
        helper.setText(R.id.tv_online_num, item.getOnlineUserCount() + "/" + item.getCount());
        UserHeadView userHeadView = helper.getConvertView().findViewById(R.id.img_motorcade_icon);
        userHeadView.setMotorcadeHeadMsg(item.getPicPath());
    }

    @Override
    public ItemEvent returnPositionEventMsg(int position) {
        return null;
    }
}

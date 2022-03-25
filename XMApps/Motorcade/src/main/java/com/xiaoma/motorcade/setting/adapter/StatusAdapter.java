package com.xiaoma.motorcade.setting.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.xiaoma.motorcade.R;
import com.xiaoma.motorcade.common.model.GroupMemberInfo;
import com.xiaoma.motorcade.common.view.UserHeadView;

/**
 * @author zs
 * @date 2019/1/18 0018.
 */
public class StatusAdapter extends BaseQuickAdapter<GroupMemberInfo, BaseViewHolder> {

    public StatusAdapter() {
        super(R.layout.item_owner_status);
    }

    @Override
    protected void convert(BaseViewHolder helper, GroupMemberInfo item) {
        UserHeadView userHeadView = helper.getConvertView().findViewById(R.id.avatar_iv);
        userHeadView.setUserHeadMsg(item.getHeader());
        helper.setText(R.id.owner_nickname_tv, item.getNickName());
        String status = item.getOnline() == 0 ? mContext.getString(R.string.offline) : mContext.getString(R.string.online);
        helper.setText(R.id.owner_status_tv, status);
    }
}

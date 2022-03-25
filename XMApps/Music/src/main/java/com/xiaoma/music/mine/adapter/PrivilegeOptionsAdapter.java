package com.xiaoma.music.mine.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.xiaoma.music.R;
import com.xiaoma.music.mine.model.PrivilegeBean;

/**
 * <pre>
 *   @author Create by on Gillben
 *   date:   2019/6/28 0028 15:48
 *   desc:   优享特权栏目
 * </pre>
 */
public class PrivilegeOptionsAdapter extends BaseQuickAdapter<PrivilegeBean, BaseViewHolder> {


    public PrivilegeOptionsAdapter() {
        super(R.layout.item_privilege_options);
    }

    @Override
    protected void convert(BaseViewHolder helper, PrivilegeBean item) {
        helper.setText(R.id.tv_item_vip_privilege, item.getValue());
    }
}

package com.xiaoma.mapadapter.ui;

import android.support.annotation.Nullable;
import android.view.View;

import com.chad.library.adapter.base.BaseViewHolder;
import com.xiaoma.mapadapter.R;
import com.xiaoma.mapadapter.constant.EventConstants;
import com.xiaoma.mapadapter.model.SearchAddressInfo;
import com.xiaoma.model.ItemEvent;
import com.xiaoma.ui.adapter.XMBaseAbstractBQAdapter;

import java.util.List;

/**
 * @author taojin
 * @date 2019/1/9
 */
public class MapAdapter extends XMBaseAbstractBQAdapter<SearchAddressInfo, BaseViewHolder> {
    public MapAdapter(int layoutResId, @Nullable List<SearchAddressInfo> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, SearchAddressInfo item) {

        helper.setText(R.id.tv_name, item.title);
        helper.setText(R.id.tv_address, item.addressName);
        if (item.isChoose) {
            helper.getView(R.id.iv_location).setVisibility(View.VISIBLE);
        } else {
            helper.getView(R.id.iv_location).setVisibility(View.INVISIBLE);
        }

    }

    @Override
    public ItemEvent returnPositionEventMsg(int position) {
        return new ItemEvent(EventConstants.NormalClick.MAP_ITEM,""+position);
    }
}

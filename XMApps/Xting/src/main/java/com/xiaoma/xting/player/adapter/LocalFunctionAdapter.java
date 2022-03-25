package com.xiaoma.xting.player.adapter;

import android.support.v4.app.Fragment;

import com.xiaoma.model.ItemEvent;
import com.xiaoma.xting.common.adapter.GalleryAdapter;
import com.xiaoma.xting.local.model.BaseChannelBean;

/**
 * <des>
 *
 * @author wutao
 * @date 2018/12/5
 */
public class LocalFunctionAdapter extends GalleryAdapter<BaseChannelBean> {
    public LocalFunctionAdapter(Fragment fragment) {
        super(fragment);
        mAntiDoubleClickT = false;
    }

    @Override
    public ItemEvent returnPositionEventMsg(int position) {
        return new ItemEvent(getData().get(position).getChannelName(), String.valueOf(position));
    }
}
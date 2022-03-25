package com.xiaoma.instructiondistribute.xkan.common.base;


/*  @project_name：  XMAgateOS
 *  @package_name：  com.xiaoma.xkan.common
 *  @file_name:      XMBaseFilterAdapter
 *  @author:         Rookie
 *  @create_time:    2018/12/20 17:43
 *  @description：   TODO             */

import android.content.Context;

import com.xiaoma.instructiondistribute.xkan.common.model.UsbMediaInfo;
import com.xiaoma.model.ItemEvent;
import com.xiaoma.ui.adapter.XMBaseAbstractRyAdapter;

import java.util.List;

public abstract class XMBaseFilterAdapter extends XMBaseAbstractRyAdapter<UsbMediaInfo> {
    public XMBaseFilterAdapter(Context context, List<UsbMediaInfo> datas, int layoutId) {
        super(context, datas, layoutId);
    }

    @Override
    public ItemEvent returnPositionEventMsg(int position) {
        return new ItemEvent(getDatas().get(position).getMediaName(), "");
    }

}

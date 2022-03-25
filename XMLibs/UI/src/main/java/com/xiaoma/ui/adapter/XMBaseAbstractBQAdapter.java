package com.xiaoma.ui.adapter;

import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.xiaoma.ui.AdapterViewItemTrackProperties;

import java.util.List;


/*  @project_name：  XMAgateOS
 *  @package_name：  com.xiaoma.ui.adapter
 *  @file_name:      XMBaseAbstractBQAdapter
 *  @author:         Rookie
 *  @create_time:    2018/12/10 15:17
 *  @description：   带接口的BaseQuickAdapter              */

public abstract class XMBaseAbstractBQAdapter<T,V extends BaseViewHolder> extends BaseQuickAdapter<T,V> implements AdapterViewItemTrackProperties {

    public XMBaseAbstractBQAdapter(int layoutResId, @Nullable List<T> data) {
        super(layoutResId, data);
    }

    public XMBaseAbstractBQAdapter(@Nullable List<T> data) {
        super(data);
    }

    public XMBaseAbstractBQAdapter(int layoutResId) {
        super(layoutResId);
    }


}

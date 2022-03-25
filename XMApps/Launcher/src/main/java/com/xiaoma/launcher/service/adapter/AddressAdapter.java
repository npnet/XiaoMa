package com.xiaoma.launcher.service.adapter;


/*  @project_name：  XMAgateOS
 *  @package_name：  com.xiaoma.launcher.service.adapter
 *  @file_name:      AddressAdapter
 *  @author:         Rookie
 *  @create_time:    2019/2/18 16:54
 *  @description：   TODO             */

import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.xiaoma.mapadapter.model.SearchAddressInfo;

import java.util.List;

public class AddressAdapter extends BaseQuickAdapter<SearchAddressInfo,BaseViewHolder> {


    public AddressAdapter(int layoutResId, @Nullable List<SearchAddressInfo> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, SearchAddressInfo item) {

    }

}

package com.xiaoma.launcher.service.ui;


/*  @project_name：  XMAgateOS
 *  @package_name：  com.xiaoma.launcher.service.ui
 *  @file_name:      UsedAddressActivity
 *  @author:         Rookie
 *  @create_time:    2019/2/18 16:32
 *  @description：   服务常用目的地             */

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.xiaoma.component.base.BaseActivity;
import com.xiaoma.launcher.R;
import com.xiaoma.launcher.service.adapter.AddressAdapter;
import com.xiaoma.mapadapter.model.SearchAddressInfo;
import com.xiaoma.ui.view.XmScrollBar;

import java.util.ArrayList;
import java.util.List;

public class UsedAddressActivity extends BaseActivity {

    private RecyclerView rvAddress;
    private XmScrollBar scrollBar;
    private AddressAdapter mAddressAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_used_address);
        initView();
        initData();
    }

    private void initView() {

        rvAddress = findViewById(R.id.rv_address);
        scrollBar = findViewById(R.id.scroll_bar);
        rvAddress.setLayoutManager(new GridLayoutManager(this, 2, LinearLayoutManager.HORIZONTAL, false));
        List<SearchAddressInfo> addressInfoList=new ArrayList<>();
        addressInfoList.add(new SearchAddressInfo("","","","",false,null));
        addressInfoList.add(new SearchAddressInfo("","","","",false,null));
        addressInfoList.add(new SearchAddressInfo("","","","",false,null));
        addressInfoList.add(new SearchAddressInfo("","","","",false,null));
        addressInfoList.add(new SearchAddressInfo("","","","",false,null));
        addressInfoList.add(new SearchAddressInfo("","","","",false,null));
        mAddressAdapter = new AddressAdapter(R.layout.item_address,addressInfoList);
        rvAddress.setAdapter(mAddressAdapter);
        scrollBar.setRecyclerView(rvAddress);

    }

    private void initData() {

    }
}

package com.xiaoma.service.order.ui;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xiaoma.component.base.BaseActivity;
import com.xiaoma.mapadapter.manager.LocationManager;
import com.xiaoma.mapadapter.model.LocationInfo;
import com.xiaoma.model.XmResource;
import com.xiaoma.model.annotation.PageDescComponent;
import com.xiaoma.service.R;
import com.xiaoma.service.common.constant.EventConstants;
import com.xiaoma.service.order.adapter.CityAdapter;
import com.xiaoma.service.order.model.CityBean;
import com.xiaoma.service.order.view.SideBar;
import com.xiaoma.service.order.vm.OrderVM;
import com.xiaoma.utils.StringUtil;

import java.util.List;

/**
 * 选择城市dialog
 * Created by zhushi.
 * Date: 2018/11/13
 */
@PageDescComponent(EventConstants.PageDescribe.chooseCityPagePathDesc)
public class ChooseCityDialog extends BaseActivity {

    private OrderVM mOrderVM;
    private RecyclerView mRecyclerView;
    private SideBar sideBar;
    private TextView dialog;
    private CityAdapter adapter;
    private LinearLayoutManager manager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_choose_city);
        getNaviBar().showBackNavi();
        getWindow().setLayout(890, ViewGroup.LayoutParams.MATCH_PARENT);
        getWindow().setGravity(Gravity.START);
        initViews();
        fetchData();
    }

    private void initViews() {
        sideBar = findViewById(R.id.sideBar);
        dialog = findViewById(R.id.dialog);
        mRecyclerView = findViewById(R.id.recyclerView);
        sideBar.setTextView(dialog);
        //设置右侧SideBar触摸监听
        sideBar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {

            @Override
            public void onTouchingLetterChanged(String s) {
                //该字母首次出现的位置
                int position = adapter.getPositionForSection(s.charAt(0));
                if (position != -1) {
                    manager.scrollToPositionWithOffset(position, 0);
                }
            }
        });
        manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(manager);
        adapter = new CityAdapter(this);
        mRecyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new CityAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                CityBean cityBean = adapter.getItem(position);
                Intent intent = new Intent();
                intent.putExtra(Choose4sShopActivity.INTENT_CITY, cityBean);
                setResult(RESULT_OK, intent);
                finish();
            }

            @Override
            public void onLocateClick() {
                LocationManager.getInstance().setLocationChangedListener(new LocationManager.ILocationChangedListener() {
                    @Override
                    public void onLocationChange(LocationInfo locationInfo) {
                        String currentCity = locationInfo.getCity();
                        int locationState = "".equals(currentCity) ? CityAdapter.LOCATING : CityAdapter.SUCCESS;
                        adapter.getItem(0).setName(currentCity);
                        adapter.updateLocateState(locationState, locationState == CityAdapter.SUCCESS ? currentCity : getString(R.string.location_ing));
                    }
                });
            }
        });
    }

    private void fetchData() {
        mOrderVM = ViewModelProviders.of(this).get(OrderVM.class);
        mOrderVM.getCityListDates().observe(this, new Observer<XmResource<List<CityBean>>>() {
            @Override
            public void onChanged(@Nullable XmResource<List<CityBean>> listXmResource) {
                if (listXmResource == null) {
                    return;
                }
                listXmResource.handle(new OnCallback<List<CityBean>>() {
                    @Override
                    public void onSuccess(List<CityBean> data) {
                        //返回的数据里的第一项加入定位城市
                        CityBean cityBean = new CityBean();
                        String cityName = LocationManager.getInstance().getCurrentCity();
                        if (!StringUtil.isEmpty(cityName)) {
                            adapter.updateLocateState(CityAdapter.SUCCESS, cityName);
                        }
                        cityBean.setName("".equals(cityName) ? getString(R.string.location_ing) : cityName);
                        data.add(0, cityBean);
                        adapter.updateList(data);
                    }
                });
            }
        });
        mOrderVM.fetchCityList();
    }

    @Override
    protected void noNetworkOnRetry() {
        super.noNetworkOnRetry();
        mOrderVM.fetchCityList();
    }

    @Override
    public void finish() {
        super.finish();
        //注释掉activity本身的过渡动画
        overridePendingTransition(0, 0);
        LocationManager.getInstance().setLocationChangedListener(null);
    }
}

package com.xiaoma.assistant.practice;


/*  @project_name：  XMAgateOS
 *  @package_name：  com.xiaoma.vrpractice.ui
 *  @file_name:      TtsWeatherActivity
 *  @author:         Rookie
 *  @create_time:    2019/6/4 16:43
 *  @description：   TODO             */

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.xiaoma.assistant.R;
import com.xiaoma.assistant.practice.adapter.CityAdapter;
import com.xiaoma.assistant.practice.view.SlideBar;
import com.xiaoma.assistant.practice.vm.WeatherVM;
import com.xiaoma.component.base.BaseActivity;
import com.xiaoma.mapadapter.manager.LocationManager;
import com.xiaoma.mapadapter.model.LocationInfo;
import com.xiaoma.model.XmResource;
import com.xiaoma.model.pratice.CityBean;
import com.xiaoma.model.pratice.VrPracticeConstants;
import com.xiaoma.ui.view.XmScrollBar;
import com.xiaoma.utils.LaunchUtils;
import com.xiaoma.utils.StringUtil;
import com.xiaoma.utils.ViewUtils;

import java.util.List;

public class TtsWeatherActivity extends BaseActivity {

    private RecyclerView mRecyclerView;
    private SlideBar slideBar;
    private TextView dialog;
    private CityAdapter adapter;
    private LinearLayoutManager manager;
    private WeatherVM mWeatherVM;
    private TextView mTv_choose;
    private int actionPosition = -1;
    private int mRequestCode;
    private String mCurrentCity;
    private BroadcastReceiver mBroadcastReceiver;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ttsweather);
        initView();
        initData();
        initLocate();
        registerExit();
    }

    private LocationManager.ILocationChangedListener mILocationChangedListener = new LocationManager.ILocationChangedListener() {
        @Override
        public void onLocationChange(LocationInfo locationInfo) {
            mCurrentCity = locationInfo.getCity();
            int locationState = TextUtils.isEmpty(mCurrentCity) ? CityAdapter.FAILED : CityAdapter.SUCCESS;
            adapter.getItem(0).setName(mCurrentCity);
            adapter.updateLocateState(locationState, locationState == CityAdapter.SUCCESS ? mCurrentCity : getString(R.string.location_ing));
        }
    };

    private void initLocate() {
        LocationManager.getInstance().setLocationChangedListener(mILocationChangedListener);
    }

    private void registerExit() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("close_app_VR_PRACTICE");
        registerReceiver(mBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                exit();
            }
        }, intentFilter);
    }
    private void unRegisterExit() {
        if (mBroadcastReceiver == null) return;
        unregisterReceiver(mBroadcastReceiver);
    }
    private void exit() {
        finish();
    }

    private void initView() {
        slideBar = findViewById(R.id.sideBar);
        dialog = findViewById(R.id.dialog);
        mRecyclerView = findViewById(R.id.recyclerView);
        mTv_choose = findViewById(R.id.tv_choose);
        XmScrollBar verticalScrollBar = findViewById(R.id.scrollbar);

        slideBar.setTextView(dialog);
        //设置右侧SideBar触摸监听
        slideBar.setOnTouchingLetterChangedListener(new SlideBar.OnTouchingLetterChangedListener() {

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
        verticalScrollBar.setRecyclerView(mRecyclerView);
        adapter.setOnItemClickListener(new CityAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (ViewUtils.isFastClick()){
                    return;
                }
                CityBean cityBean = adapter.getItem(position);
                mTv_choose.setText(getString(R.string.already_select_city, cityBean.getName()));
                Bundle bundle = new Bundle();
                bundle.putString(VrPracticeConstants.ACTION_JSON, cityBean.getName());
                bundle.putInt(VrPracticeConstants.ACTION_POSITION, actionPosition);
                bundle.putInt(VrPracticeConstants.SKILL_REQUEST_CODE, mRequestCode);
                LaunchUtils.launchAppWithData(TtsWeatherActivity.this, VrPracticeConstants.PACKAGE_NAME, VrPracticeConstants.SKILL_CLASS_NAME, bundle);
                finish();
            }

            @Override
            public void onLocateClick() {
                mCurrentCity = LocationManager.getInstance().getCurrentCity();
                int locationState = TextUtils.isEmpty(mCurrentCity) ? CityAdapter.FAILED : CityAdapter.SUCCESS;
                adapter.getItem(0).setName(mCurrentCity);
                adapter.updateLocateState(locationState, locationState == CityAdapter.SUCCESS ? mCurrentCity : getString(R.string.location_ing));
            }
        });
    }

    private void initData() {
        mTv_choose.setText(getString(R.string.already_select_city, "无"));
        mWeatherVM = ViewModelProviders.of(this).get(WeatherVM.class);
        mWeatherVM.getCityListDatas().observe(this, new Observer<XmResource<List<CityBean>>>() {
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
        mWeatherVM.fetchCityList();

        Intent intent = getIntent();
        Bundle bundleExtra = intent.getBundleExtra(LaunchUtils.EXTRA_BUNDLE);
        if (bundleExtra == null) {
            return;
        }
        String cityName = bundleExtra.getString(VrPracticeConstants.ACTION_JSON);
        if (!TextUtils.isEmpty(cityName)) {
            mTv_choose.setText(getString(R.string.already_select_city, cityName));
        } else {
            mTv_choose.setText(getString(R.string.already_select_city, "无"));
        }
        actionPosition = bundleExtra.getInt(VrPracticeConstants.ACTION_POSITION, 0);
        mRequestCode = bundleExtra.getInt(VrPracticeConstants.SKILL_REQUEST_CODE, 0);
    }

    @Override
    protected void onDestroy() {
        unRegisterExit();
        LocationManager.getInstance().removeLocationListener(mILocationChangedListener);
        super.onDestroy();

    }
}

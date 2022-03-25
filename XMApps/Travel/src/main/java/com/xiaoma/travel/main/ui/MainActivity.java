package com.xiaoma.travel.main.ui;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.xiaoma.component.base.BaseActivity;
import com.xiaoma.travel.R;
import com.xiaoma.travel.main.model.MainTabBean;
import com.xiaoma.travel.main.vm.MainVm;
import com.xiaoma.travel.movie.SeatTableActivity;
import com.xiaoma.travel.view.citypick.adapter.CityRecyclerAdapter;
import com.xiaoma.travel.view.citypick.view.SideBar;
import com.xiaoma.update.manager.AppUpdateCheck;
import com.xiaoma.utils.log.KLog;

/**
 * @author wutao
 * @date 2018/11/6
 * @desc 奔腾出行
 */
public class MainActivity extends BaseActivity implements View.OnClickListener {

    private static final int DIVIDER_ITEM = 100;
    private MainAdapter mAdapter;
    private DrawerLayout mDraw;
    private RecyclerView mLeftCityRecyclerView;
    private SideBar mLeftContactSidebar;
    private CityRecyclerAdapter mCityRecyclerAdapter;
    private LinearLayoutManager linearLayoutManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        AppUpdateCheck.checkAppUpdate(getPackageName(), getApplication());
    }

    private void initView() {
        TextView tvOrder = (TextView) findViewById(R.id.tv_order);
        TextView tvLocation = (TextView) findViewById(R.id.tv_location);
        tvOrder.setOnClickListener(this);
        tvLocation.setOnClickListener(this);
        mDraw = (DrawerLayout) findViewById(R.id.draw);
        TextView leftContactDialog = (TextView) findViewById(R.id.left_contact_dialog);
        mLeftContactSidebar = (SideBar) findViewById(R.id.left_contact_sidebar);
        mLeftContactSidebar.setTextView(leftContactDialog);
        mLeftCityRecyclerView = (RecyclerView) findViewById(R.id.left_recycler_view);
        initMainRecyclerView();
    }

    private void initData() {
        MainVm vm = ViewModelProviders.of(this).get(MainVm.class);
        mAdapter.setNewData(vm.getTabs());
        mCityRecyclerAdapter = new CityRecyclerAdapter(MainActivity.this, vm.getCityList());
        linearLayoutManager = new LinearLayoutManager(MainActivity.this);
        mLeftCityRecyclerView.setLayoutManager(linearLayoutManager);
        mLeftCityRecyclerView.setAdapter(mCityRecyclerAdapter);
        mCityRecyclerAdapter.setOnCityClickListener(new CityRecyclerAdapter.OnCityClickListener() {
            @Override
            public void onCityClick(String name) {
                showToast(name);
                mDraw.closeDrawer(Gravity.LEFT);
            }

            @Override
            public void onLocateClick() {
                //重新定位
                KLog.i("onLocateClick");
                mCityRecyclerAdapter.updateLocateState(CityRecyclerAdapter.LOCATING, null);
//                mCityRecyclerAdapter.updateLocateState(CityRecyclerAdapter.SUCCESS, "长沙");
//                mCityRecyclerAdapter.updateLocateState(CityRecyclerAdapter.FAILED, null);
            }
        });

        mLeftContactSidebar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {

            @Override
            public void onTouchingLetterChanged(String s) {
                int position = mCityRecyclerAdapter.getPositionForSection(s);
                if (position != -1) {
                    linearLayoutManager.scrollToPositionWithOffset(position, 0);
                }

            }
        });
    }

    private void initMainRecyclerView() {
        RecyclerView recycleView = (RecyclerView) findViewById(R.id.recycle_view);
        recycleView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        DividerItemDecoration decor = new DividerItemDecoration(this, DividerItemDecoration.HORIZONTAL) {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                outRect.set(0, 0, DIVIDER_ITEM, 0);
            }
        };
        decor.setDrawable(new ColorDrawable());
        recycleView.addItemDecoration(decor);
        mAdapter = new MainAdapter();
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                switch (position) {
                    case 0://酒店
                        startActivity(new Intent(MainActivity.this, HotelActivity.class));
                        break;
                    case 1://美食
                        startActivity(new Intent(MainActivity.this, FoodActivity.class));
                        break;
                    case 2://景点
                        startActivity(new Intent(MainActivity.this, AttractionsActivity.class));
                        break;
                    case 3://电影
                        startActivity(new Intent(MainActivity.this, MovieActivity.class));
                        break;
                    case 4://停车
                        startActivity(new Intent(MainActivity.this, ParkingActivity.class));
                }
            }
        });
        recycleView.setAdapter(mAdapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_order:
                startActivity(new Intent(this, SeatTableActivity.class));
//                startActivity(new Intent(this, OrderActivity.class));
                break;
            case R.id.tv_location:
                mDraw.openDrawer(Gravity.LEFT);
                break;
        }
    }

    class MainAdapter extends BaseQuickAdapter<MainTabBean, BaseViewHolder> {

        MainAdapter() {
            super(R.layout.item_main_tab);
        }

        @Override
        protected void convert(BaseViewHolder helper, MainTabBean item) {
            helper.setImageResource(R.id.iv, item.getImageId());
            helper.setText(R.id.tv, item.getName());
        }
    }

}


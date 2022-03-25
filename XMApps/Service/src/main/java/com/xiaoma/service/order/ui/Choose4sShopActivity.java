package com.xiaoma.service.order.ui;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.xiaoma.component.base.BaseActivity;
import com.xiaoma.mapadapter.manager.LocationManager;
import com.xiaoma.mapadapter.model.LocationInfo;
import com.xiaoma.model.XmResource;
import com.xiaoma.model.annotation.NormalOnClick;
import com.xiaoma.model.annotation.PageDescComponent;
import com.xiaoma.model.annotation.ResId;
import com.xiaoma.service.R;
import com.xiaoma.service.common.constant.EventConstants;
import com.xiaoma.service.order.adapter.Shop4sAdapter;
import com.xiaoma.service.order.model.CityBean;
import com.xiaoma.service.order.model.ShopBean;
import com.xiaoma.service.order.vm.OrderVM;
import com.xiaoma.ui.toast.XMToast;
import com.xiaoma.ui.view.XmDividerDecoration;
import com.xiaoma.ui.view.XmScrollBar;

import java.util.List;

/**
 * 选择门店页面
 * Created by zhushi.
 * Date: 2018/11/13
 */
@PageDescComponent(EventConstants.PageDescribe.choose4sActivityPagePathDesc)
public class Choose4sShopActivity extends BaseActivity implements View.OnClickListener, LocationManager.ILocationChangedListener {

    public static final int CHOOSE_CITY_REQUEST_CODE = 100;
    public static String INTENT_CITY = "intent_city";
    public static String INTENT_SHOP = "intent_shop";
    private CityBean mCityBean;
    private Button btnSure;
    private TextView cityNameTv;
    private XmScrollBar xmScrollBar;
    private RecyclerView mShopRecyclerView;
    private Shop4sAdapter mAdapter;
    private OrderVM mOrderVM;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_shop);
        initView();
        fetchData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocationManager.getInstance().setLocationChangedListener(this);
    }

    private void initView() {
        int pos = getIntent().getIntExtra(OrderActivity.EXTRA_POSITION, -1);
        ShopBean intentShopBean = (ShopBean) getIntent().getSerializableExtra(INTENT_SHOP);
        cityNameTv = findViewById(R.id.city_name_tv);
        btnSure = findViewById(R.id.button);
        mShopRecyclerView = findViewById(R.id.shop_recycler);
        xmScrollBar = findViewById(R.id.scroll_bar);
        mShopRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        XmDividerDecoration decoration = new XmDividerDecoration(this, DividerItemDecoration.HORIZONTAL);
        decoration.setRect(0, 0, 50, 0);
        mShopRecyclerView.addItemDecoration(decoration);
        mAdapter = new Shop4sAdapter();

        mAdapter.setSelectShopListener(new Shop4sAdapter.SelectShopListener() {
            @Override
            public void setSelectShop(boolean select) {
                if (select){
                    btnSure.setEnabled(true);
                }else {
                    btnSure.setEnabled(false);
                }
            }
        });
        mAdapter.setSelectedPos(pos);

        mShopRecyclerView.setAdapter(mAdapter);
        xmScrollBar.setRecyclerView(mShopRecyclerView);
        cityNameTv.setOnClickListener(this);
        btnSure.setOnClickListener(this);
        btnSure.setEnabled(pos != -1);

        if (intentShopBean != null) {
            cityNameTv.setText(intentShopBean.getVCITY());

        } else {
            cityNameTv.setText("".equals(LocationManager.getInstance().getCurrentCity()) ? getResources().getString(R.string.location_ing) :
                    LocationManager.getInstance().getCurrentCity());
        }
    }

    private void fetchData() {
        mOrderVM = ViewModelProviders.of(this).get(OrderVM.class);
        mOrderVM.getShopListDates().observe(this, new Observer<XmResource<List<ShopBean>>>() {
            @Override
            public void onChanged(@Nullable XmResource<List<ShopBean>> listXmResource) {
                if (listXmResource == null) {
                    return;
                }
                listXmResource.handle(new OnCallback<List<ShopBean>>() {
                    @Override
                    public void onSuccess(List<ShopBean> data) {
                        if (data == null || data.size() == 0) {
                            btnSure.setVisibility(View.GONE);

                        } else {
                            btnSure.setVisibility(View.VISIBLE);
                        }
                        mAdapter.setNewData(data);
                        mAdapter.setEmptyView(R.layout.shop_list_notdata, (ViewGroup) mShopRecyclerView.getParent());
                    }
                });
            }
        });
        mOrderVM.fetchShopList(String.valueOf(cityNameTv.getText()));
    }

    @Override
    protected void noNetworkOnRetry() {
        super.noNetworkOnRetry();
        mOrderVM.fetchShopList(String.valueOf(cityNameTv.getText()));
    }

    /**
     * 城市选择dialog
     */
    public void showChooseCityDialog() {
        Intent intent = new Intent(this, ChooseCityDialog.class);
        startActivityForResult(intent, CHOOSE_CITY_REQUEST_CODE);
    }

    /**
     * 确定
     */
    public void chooseShopCommit() {
        int position = mAdapter.getSelectedPos();
        if (position == -1) {
            XMToast.toastException(this, getString(R.string.choose_shop_tips), false);
            return;
        }
        ShopBean shopBean = mAdapter.getData().get(position);
        shopBean.setLocationDistance(mAdapter.getLocationDistance());
        Intent intent = new Intent();
        intent.putExtra(OrderActivity.POSITION, position);
        intent.putExtra(Choose4sShopActivity.INTENT_SHOP, shopBean);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case CHOOSE_CITY_REQUEST_CODE:
                if (data == null) {
                    return;
                }
                mCityBean = (CityBean) data.getSerializableExtra(INTENT_CITY);
                //选择的城市没有改变
                if (cityNameTv.getText().toString().equals(mCityBean.getName())) {
                    return;
                }
                cityNameTv.setText(mCityBean.getName());
                //根据城市获取4s店数据
                mOrderVM.fetchShopList(String.valueOf(cityNameTv.getText()));
                mAdapter.setSelectedPos(-1);
                btnSure.setEnabled(false);
                break;
        }
    }

    @Override
    @NormalOnClick({EventConstants.NormalClick.affirm4S, EventConstants.NormalClick.selectCity})
    @ResId({R.id.button, R.id.city_name_tv})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button:
                chooseShopCommit();
                break;

            case R.id.city_name_tv:

                showChooseCityDialog();
                break;
        }
    }


    @Override
    public void finish() {
        super.finish();
        LocationManager.getInstance().setLocationChangedListener(null);
    }

    @Override
    public void onLocationChange(LocationInfo locationInfo) {
        if ((getResources().getString(R.string.location_ing)).equals(String.valueOf(cityNameTv.getText()))) {
            cityNameTv.setText("".equals(locationInfo.getCity()) ? getResources().getString(R.string.location_ing) : locationInfo.getCity());
            if (!("").equals(locationInfo.getCity())) {
                mOrderVM.fetchShopList(String.valueOf(cityNameTv.getText()));
            }
        }
        if (mAdapter!=null){
            mAdapter.notifyDataSetChanged();
        }
    }


}

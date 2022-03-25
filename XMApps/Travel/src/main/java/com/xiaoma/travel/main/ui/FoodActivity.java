package com.xiaoma.travel.main.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;

import com.xiaoma.component.base.BaseActivity;
import com.xiaoma.model.ResultCallback;
import com.xiaoma.model.XMResult;
import com.xiaoma.travel.R;
import com.xiaoma.trip.category.request.RequestSearchStoreParm;
import com.xiaoma.trip.category.response.CategoryBean;
import com.xiaoma.trip.category.response.SearchStoreBean;
import com.xiaoma.trip.category.response.StoreInfoBean;
import com.xiaoma.trip.common.RequestManager;
import com.xiaoma.trip.place.response.CityBean;
import com.xiaoma.trip.place.response.DistrictBean;

import java.util.List;

public class FoodActivity extends BaseActivity{

    int cityId = 30;
    String usernName = "123456";
    String password = "123456";
    String city = "深圳";
    String query = "粤菜";
    String storid = "93653388";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food);
    }


    public void queryCity(View view) {
        RequestManager.getInstance().queryCity(new ResultCallback<XMResult<List<CityBean>>>() {

                    @Override
                    public void onSuccess(XMResult<List<CityBean>> result) {
                        Log.d("result", "result:" + result.getData().get(0).getName());
                    }

                    @Override
                    public void onFailure(int code, String msg) {

                    }
                });
    }

    public void queryDistricy(View view) {
        RequestManager.getInstance().queryCityDistrict(cityId,new ResultCallback<XMResult<List<DistrictBean>>>() {

            @Override
            public void onSuccess(XMResult<List<DistrictBean>> result) {
                Log.d("result", "result:" + result.getData());
            }

            @Override
            public void onFailure(int code, String msg) {

            }
        });
    }

    public void queryCategory(View view) {
        RequestManager.getInstance().queryCategory(cityId,new ResultCallback<XMResult<List<CategoryBean>>>() {

            @Override
            public void onSuccess(XMResult<List<CategoryBean>> result) {
                Log.d("result", "result:" + result.getData());
            }

            @Override
            public void onFailure(int code, String msg) {

            }
        });
    }

    public void querySearchStore(View view) {
        RequestSearchStoreParm requestSearchStoreParm = new RequestSearchStoreParm();
        requestSearchStoreParm.query = query;
        requestSearchStoreParm.pos = "";
        requestSearchStoreParm.city = city ;
        requestSearchStoreParm.districtid = "";
        requestSearchStoreParm.cateid = "";
        requestSearchStoreParm.dist = "";
        requestSearchStoreParm.orderType = "";
        requestSearchStoreParm.orderStandard = "";
        requestSearchStoreParm.limit = "";
        RequestManager.getInstance().conditionSearchStore(requestSearchStoreParm,new ResultCallback<XMResult<List<SearchStoreBean>>>() {
            @Override
            public void onSuccess(XMResult<List<SearchStoreBean>> result) {
                Log.d("result", "result:" + result.getData());
            }

            @Override
            public void onFailure(int code, String msg) {

            }
        });
    }

    public void queryStoreInfo(View view) {

        RequestManager.getInstance().queryStoreInfo(usernName,password,"",storid,new ResultCallback<XMResult<StoreInfoBean>>() {

            @Override
            public void onSuccess(XMResult<StoreInfoBean> result) {

                Log.d("result", "result:" + result.getData().getCityname());
            }

            @Override
            public void onFailure(int code, String msg) {

            }
        });
    }
}

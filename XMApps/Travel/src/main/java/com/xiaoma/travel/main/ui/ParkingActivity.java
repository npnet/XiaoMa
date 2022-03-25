package com.xiaoma.travel.main.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.xiaoma.component.base.BaseActivity;
import com.xiaoma.model.ResultCallback;
import com.xiaoma.model.XMResult;
import com.xiaoma.travel.R;
import com.xiaoma.trip.common.RequestManager;
import com.xiaoma.trip.parking.response.ParkingFeeBudgetBean;
import com.xiaoma.trip.parking.response.ParkingInfoBean;
import com.xiaoma.trip.parking.response.ParkingSpotFeeStandardBean;
import com.xiaoma.utils.log.KLog;

import java.util.List;

public class ParkingActivity extends BaseActivity {

    private String id = "2V0ocGFe"; //停车场id
    private ImageView mImageView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parking);
        mImageView = findViewById(R.id.user_img);
    }

    public void parkinInfo(View view) {
        RequestManager.getInstance().queryParkingInfo("深圳市", "", "", "", null, "", "", new ResultCallback<XMResult<List<ParkingInfoBean>>>() {
            @Override
            public void onSuccess(XMResult<List<ParkingInfoBean>> result) {
                Log.d("result", "result: onSuccess" + result.getData());
            }

            @Override
            public void onFailure(int code, String msg) {
                Log.d("result", "result: onFailure" + msg);
            }
        });
    }

    public void parkingToll(View view) {
        RequestManager.getInstance().queryParkingToll(id, new ResultCallback<XMResult<List<ParkingSpotFeeStandardBean>>>() {
            @Override
            public void onSuccess(XMResult<List<ParkingSpotFeeStandardBean>> result) {
                Log.d("result", "result: onSuccess" + result.getData());
            }

            @Override
            public void onFailure(int code, String msg) {
                Log.d("result", "result: onFailure" + msg);
            }
        });
    }

    public void parkingBudget(View view) {
        RequestManager.getInstance().parkingCostBuget(id, "", "30", "", new ResultCallback<XMResult<ParkingFeeBudgetBean>>() {
            @Override
            public void onSuccess(XMResult<ParkingFeeBudgetBean> result) {
                Log.d("result", "result: onSuccess" + result.getData());
            }

            @Override
            public void onFailure(int code, String msg) {
                Log.d("result", "result: onFailure" + msg);
            }
        });
    }

    public void parkingImg(View view) {
        //Glide.with(this).load("https://pic.ezparking.com.cn/rtpi-service/parking?key=ehl4czrksvct7qnbhdpb6ewbks3qklz0&type=photo&id=2V0ocGFe&file=entrance_nel5mAqc.jpg").into(mImageView);
        RequestManager.getInstance().getParkingImg(id, "entrance_nel5mAqc.jpg", new ResultCallback<XMResult<String>>() {
            @Override
            public void onSuccess(XMResult<String> result) {
              Glide.with(ParkingActivity.this).load(result.getData()).into(mImageView);
            }

            @Override
            public void onFailure(int code, String msg) {
                Log.d("result", "result: onFailure" + msg);
            }
        });
    }
}

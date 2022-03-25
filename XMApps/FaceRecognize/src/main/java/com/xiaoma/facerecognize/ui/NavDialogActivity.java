package com.xiaoma.facerecognize.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.xiaoma.center.logic.CenterConstants;
import com.xiaoma.facerecognize.R;
import com.xiaoma.facerecognize.common.EventConstants;
import com.xiaoma.model.annotation.NormalOnClick;
import com.xiaoma.model.annotation.PageDescComponent;
import com.xiaoma.model.annotation.ResId;

/**
 * Created by kaka
 * on 19-4-11 下午6:04
 * <p>
 * TODO:暂时不确定导航的后续操作
 * </p>
 */
@PageDescComponent(EventConstants.PageDescClick.HeavyFatigueDriving)
public class NavDialogActivity extends AbsRecognizeDialog implements View.OnClickListener {

    private ImageView mClose;
    private TextView mCountDown;
    private ImageView mRestArea;
    private ImageView mCoffeeShop;
    private ImageView mServiceArea;
    private ImageView mParkingLot;

    public static void newRecognize(Context context) {
        Intent intent = new Intent(context, NavDialogActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getNaviBar().hideNavi();
        setContentView(R.layout.activity_nav_dialog);
        initView();
        initListener();
        playTTs(R.string.heavy_fatigue_driving_tip);
    }

    private void initView() {
        mClose = findViewById(R.id.close);
        mCountDown = findViewById(R.id.count_down);
        mRestArea = findViewById(R.id.rest_area);
        mCoffeeShop = findViewById(R.id.coffee_shop);
        mServiceArea = findViewById(R.id.service_area);
        mParkingLot = findViewById(R.id.parking_lot);
    }

    private void initListener() {
        mClose.setOnClickListener(this);
        mRestArea.setOnClickListener(this);
        mCoffeeShop.setOnClickListener(this);
        mServiceArea.setOnClickListener(this);
        mParkingLot.setOnClickListener(this);
    }

    @Override
    @NormalOnClick({EventConstants.NormalClick.close, EventConstants.NormalClick.restArea, EventConstants.NormalClick.coffeeShop,
            EventConstants.NormalClick.serviceArea, EventConstants.NormalClick.parkingLot})
    @ResId({R.id.close, R.id.rest_area, R.id.coffee_shop, R.id.service_area, R.id.parking_lot})
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.close:
                finish();
                break;
            case R.id.rest_area:
                sendBroadcast(new Intent(CenterConstants.NAVIGATE_TO_RESTING_AREA));
                finish();
                break;
            case R.id.coffee_shop:
                sendBroadcast(new Intent(CenterConstants.NAVIGATE_TO_COFFEE_HOUSE));
                finish();
                break;
            case R.id.service_area:
                sendBroadcast(new Intent(CenterConstants.NAVIGATE_TO_SERVICE_AREA));
                finish();
                break;
            case R.id.parking_lot:
                sendBroadcast(new Intent(CenterConstants.NAVIGATE_TO_GAS_STATION));
                finish();
                break;
        }
    }

    @Override
    protected int getSecondsInFuture() {
        return 10;
    }

    @Override
    protected void onTick(int seconds) {
        mCountDown.setVisibility(View.VISIBLE);
        mCountDown.setText(getString(R.string.count_down_close,seconds));
    }

    @Override
    protected void onChose(String recognizerText) {
        if(TextUtils.isEmpty(recognizerText)) return;
        if(recognizerText.equals(getString(R.string.rest_area))){
            mRestArea.performClick();
        }else if(recognizerText.equals(getString(R.string.coffee_shop))){
            mCoffeeShop.performClick();
        }else if(recognizerText.equals(getString(R.string.service_area))){
            mServiceArea.performClick();
        }else if(recognizerText.equals(getString(R.string.parking_lot))){
            mParkingLot.performClick();
        }
    }
}

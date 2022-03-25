package com.xiaoma.launcher.travel.hotel.ui;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.TextView;

import com.xiaoma.component.base.BaseActivity;
import com.xiaoma.launcher.R;
import com.xiaoma.launcher.common.constant.EventConstants;
import com.xiaoma.launcher.travel.hotel.constants.HotelConstants;
import com.xiaoma.launcher.travel.hotel.vm.HotelPolicyVM;
import com.xiaoma.model.XmResource;
import com.xiaoma.model.annotation.PageDescComponent;
import com.xiaoma.trip.hotel.response.HotelPolicyBean;
import com.xiaoma.utils.NetworkUtils;
import com.xiaoma.utils.StringUtil;

/**
 * @author taojin
 * @date 2019/2/25
 */
@PageDescComponent(EventConstants.PageDescribe.BookHotelPolicyActivityPagePathDesc)
public class BookHotelPolicyActivity extends BaseActivity {

    private HotelPolicyVM hotelPolicyVM;
    private TextView tvTimePolicy;
    private TextView tvCancelPolicy;
    private TextView tvAddBedPolicy;
    private TextView tvPetPolicy;

    private String hotelId;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hotel_policy);
        initView();
        initData();
    }

    private void initView() {
        tvTimePolicy = findViewById(R.id.tv_time_policy);
        tvCancelPolicy = findViewById(R.id.tv_cancel_policy);
        tvAddBedPolicy = findViewById(R.id.tv_add_bed_policy);
        tvPetPolicy = findViewById(R.id.tv_pet_policy);
    }

    private void initData() {

        hotelId = getIntent().getStringExtra(HotelConstants.HOTEL_ID);

        hotelPolicyVM = ViewModelProviders.of(this).get(HotelPolicyVM.class);

        hotelPolicyVM.getHotelPolicyData().observe(this, new Observer<XmResource<HotelPolicyBean>>() {
            @Override
            public void onChanged(@Nullable XmResource<HotelPolicyBean> hotelPolicyBeanXmResource) {
                hotelPolicyBeanXmResource.handle(new OnCallback<HotelPolicyBean>() {
                    @Override
                    public void onSuccess(HotelPolicyBean data) {

                        if (data != null) {
                            tvTimePolicy.setText(!StringUtil.isEmpty(data.getTime()) ? data.getTime() : "");
                            tvCancelPolicy.setText(!StringUtil.isEmpty(data.getCancel()) ? data.getCancel() : "");
                            tvAddBedPolicy.setText(!StringUtil.isEmpty(data.getChild()) ? data.getChild() : "");
                            tvPetPolicy.setText(!StringUtil.isEmpty(data.getPet()) ? data.getPet() : "");
                        } else {
                            onError(1026,"查询结果数据列表为空");
                        }

                    }

                    @Override
                    public void onFailure(String msg) {
                        super.onFailure(msg);
                    }
                });
            }
        });

        if (!NetworkUtils.isConnected(this)) {
            showNoNetView();
            return;
        }

        hotelPolicyVM.fetchHotelPolicy(hotelId);
    }

    @Override
    protected void noNetworkOnRetry() {
        super.noNetworkOnRetry();
        if (NetworkUtils.isConnected(this)) {
            hotelPolicyVM.fetchHotelPolicy(hotelId);
        }
    }
}

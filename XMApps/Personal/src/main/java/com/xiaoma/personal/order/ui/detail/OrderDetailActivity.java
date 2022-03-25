package com.xiaoma.personal.order.ui.detail;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;

import com.xiaoma.component.base.BaseActivity;
import com.xiaoma.model.annotation.NormalOnClick;
import com.xiaoma.model.annotation.PageDescComponent;
import com.xiaoma.model.annotation.ResId;
import com.xiaoma.personal.R;
import com.xiaoma.personal.common.util.EventConstants;
import com.xiaoma.personal.order.constants.OrderTypeMeta;
import com.xiaoma.personal.order.model.OrderInfo;
import com.xiaoma.utils.FragmentUtils;
import com.xiaoma.utils.KeyEventUtils;

/**
 * <pre>
 *       @author Created by Gillben
 *       date: 2019/3/13 0013 10:29
 *       desc：订单详情页
 * </pre>
 */
@PageDescComponent(EventConstants.PageDescribe.orderDetailActivity)
public class OrderDetailActivity extends BaseActivity implements View.OnClickListener {

    public static final String ORDER_TYPE = "ORDER_TYPE";
    private OrderInfo.Order order;

    private ImageView mBackImage;
    private ImageView mPredestinePrompt;
    private ImageView mHomeImage;

    private boolean isHotelPolicy = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getNaviBar().hideNavi();
        setContentView(R.layout.activity_order_detail);
        initView();


        Intent intent = getIntent();
        if (intent != null) {
            order = intent.getParcelableExtra(ORDER_TYPE);
        }
        if (order != null && !TextUtils.isEmpty(order.getType())) {
            navigationSpecificDetail(order, order.getType());
        }

    }

    private void initView() {
        mBackImage = findViewById(R.id.order_detail_back);
        mPredestinePrompt = findViewById(R.id.order_detail_prompt);
        mHomeImage = findViewById(R.id.order_detail_home);

        mBackImage.setOnClickListener(this);
        mPredestinePrompt.setOnClickListener(this);
        mHomeImage.setOnClickListener(this);
    }


    @Override
    @NormalOnClick({EventConstants.NormalClick.orderDetailBack,
            EventConstants.NormalClick.orderDetailPrompt,
            EventConstants.NormalClick.orderDetailHome})
    @ResId({R.id.order_detail_back, R.id.order_detail_prompt, R.id.order_detail_home})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.order_detail_back:
                if (isHotelPolicy) {
                    isHotelPolicy = false;
                    navigationSpecificDetail(order, order.getType());
                } else {
                    finish();
                }
                break;

            case R.id.order_detail_prompt:
                if (order != null && OrderTypeMeta.HOTEL.equals(order.getType())) {
                    isHotelPolicy = true;
                    mPredestinePrompt.setVisibility(View.INVISIBLE);
                    attachContent(HotelPolicyFragment.newInstance(order));
                }
                break;

            case R.id.order_detail_home:
                KeyEventUtils.sendKeyEvent(this, KeyEvent.KEYCODE_HOME);
                break;
        }
    }


    /**
     * 进入具体订单详情
     *
     * @param orderType 订单类型
     */
    private void navigationSpecificDetail(OrderInfo.Order order, String orderType) {
        if (OrderTypeMeta.HOTEL.equals(orderType)) {
            mPredestinePrompt.setVisibility(View.VISIBLE);
            attachContent(HotelDetailFragment.newInstance(order.getId()));
        } else if (OrderTypeMeta.FILM.equals(orderType)) {
            mPredestinePrompt.setVisibility(View.INVISIBLE);
            attachContent(MovieDetailFragment.newInstance(order.getId()));
        }
    }


    private void attachContent(Fragment fragment) {
        FragmentUtils.replace(getSupportFragmentManager(), fragment, R.id.order_detail_content);
    }


}

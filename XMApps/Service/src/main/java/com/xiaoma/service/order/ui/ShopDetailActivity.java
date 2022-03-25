package com.xiaoma.service.order.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.xiaoma.component.base.BaseActivity;
import com.xiaoma.model.annotation.PageDescComponent;
import com.xiaoma.service.R;
import com.xiaoma.service.common.constant.EventConstants;
import com.xiaoma.service.order.model.ShopBean;
import com.xiaoma.ui.dialog.XmDialog;

/**
 * 门店介绍页面
 * Created by zhushi.
 * Date: 2018/11/13
 */
@PageDescComponent(EventConstants.PageDescribe.shopDetailActivityPagePathDesc)
public class ShopDetailActivity extends BaseActivity {
    private ShopBean shopBean;
    private TextView tvShopName, tvShopPhone;
    private TextView mShopInfo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_detail);
        initView();
        initData();
    }

    private void initView() {
        tvShopName = findViewById(R.id.detail_shop_name_tv);
        tvShopPhone = findViewById(R.id.detail_shop_phone_tv);
        mShopInfo = findViewById(R.id.shop_info);
    }

    private void initData() {
        shopBean = (ShopBean) getIntent().getSerializableExtra(Choose4sShopActivity.INTENT_SHOP);
        tvShopName.setText(shopBean.getVDEALERNAME());
        tvShopPhone.setText(shopBean.getVTEL());
        mShopInfo.setText(shopBean.getVADDRESS());

    }
}

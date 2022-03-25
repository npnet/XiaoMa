package com.xiaoma.service.order.ui;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.mapbar.xiaoma.manager.XmMapNaviManager;
import com.xiaoma.component.base.BaseActivity;
import com.xiaoma.model.XmResource;
import com.xiaoma.model.annotation.NormalOnClick;
import com.xiaoma.model.annotation.PageDescComponent;
import com.xiaoma.model.annotation.ResId;
import com.xiaoma.service.R;
import com.xiaoma.service.common.constant.EventBusTags;
import com.xiaoma.service.common.constant.EventConstants;
import com.xiaoma.service.common.constant.ServiceConstants;
import com.xiaoma.service.common.manager.ServiceBlueToothPhoneManager;
import com.xiaoma.service.order.model.OrderBean;
import com.xiaoma.service.order.vm.OrderListVM;
import com.xiaoma.ui.dialog.ConfirmDialog;
import com.xiaoma.ui.toast.XMToast;
import com.xiaoma.utils.NetworkUtils;
import com.xiaoma.utils.TimeUtils;

import org.simple.eventbus.EventBus;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

@PageDescComponent(EventConstants.PageDescribe.maintainDetailActivityPagePathDesc)
public class OrderDetailDialog extends BaseActivity implements View.OnClickListener {

    private TextView mStoreOrderStatus, mStoreAddress, mStorePhone, mGotoStoreTime, mOrderNumbering, mOrderTime, mOrderProject, tvStatus;
    private ImageView mNavigation, mPhoneImg;
    private Button mCancelOrderBtn;
    private OrderBean mOrderBean;
    private int orderPosition;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private Calendar calendar;
    private OrderListVM orderListVM;

    private final int NAVI_DIALOG = 1;
    private final int PHONE_DIALOG = 2;
    private final int CANCEL_ORDER_DIALOG = 3;
    public static final String ORDER_DATA = "order_data";
    public static final String ORDER_POSITION = "order_position";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_details_popwindow);
        DisplayMetrics screenSize = getResources().getDisplayMetrics();
        getWindow().setLayout(screenSize.widthPixels * 2 / 3, ViewGroup.LayoutParams.MATCH_PARENT);
        getWindow().setGravity(Gravity.START);
        bindView();
        initEvent();
        initData();
    }

    private void bindView() {
        mCancelOrderBtn = findViewById(R.id.cancel_order_btn);
        mStoreOrderStatus = findViewById(R.id.store_order_status_txt);
        tvStatus = findViewById(R.id.tv_status);
        mStoreAddress = findViewById(R.id.store_address_txt);
        mStorePhone = findViewById(R.id.store_phone_txt);
        mGotoStoreTime = findViewById(R.id.goto_store_time_txt);
        mOrderNumbering = findViewById(R.id.order_numbering_txt);
        mOrderTime = findViewById(R.id.order_time_txt);
        mOrderProject = findViewById(R.id.order_project);
        mNavigation = findViewById(R.id.navigation_img);
        mPhoneImg = findViewById(R.id.phone_img);
    }

    private void initEvent() {
        mNavigation.setOnClickListener(this);
        mPhoneImg.setOnClickListener(this);
        mCancelOrderBtn.setOnClickListener(this);
    }

    private void initData() {
        mOrderBean = (OrderBean) getIntent().getSerializableExtra(ORDER_DATA);
        orderPosition = getIntent().getIntExtra(ORDER_POSITION, -1);
        calendar = Calendar.getInstance();
        try {
            calendar.setTime(simpleDateFormat.parse(mOrderBean.getAppointmentDate()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (ServiceConstants.ORDER_RECEIVED.equals(mOrderBean.getVStatus())) {
            mCancelOrderBtn.setVisibility(View.VISIBLE);
        } else {
            mCancelOrderBtn.setVisibility(View.INVISIBLE);
        }
        orderListVM = ViewModelProviders.of(this).get(OrderListVM.class);
        orderListVM.getmCancelOrderFeedback().observe(this, new Observer<XmResource<String>>() {
            @Override
            public void onChanged(@Nullable XmResource<String> resource) {
                if (resource == null) {
                    return;
                }
                resource.handle(new OnCallback<String>() {
                    @Override
                    public void onSuccess(String data) {
                        EventBus.getDefault().post(orderPosition, EventBusTags.ORDER_CANCEL_POSITION);
                        XMToast.toastSuccess(OrderDetailDialog.this, getResources().getString(R.string.cancel_success), false);
                        finish();
                    }

                    @Override
                    public void onError(int code, String message) {
                        XMToast.toastException(OrderDetailDialog.this, getResources().getString(R.string.cancel_failed), false);
                    }
                });
            }
        });

        mStoreOrderStatus.setText(mOrderBean.getCompanyName());
        tvStatus.setText("(" + mOrderBean.getVStatus() + ")");
        mStoreAddress.setText(getString(R.string.store_address) + mOrderBean.getAddress());
        mStorePhone.setText(getString(R.string.store_info) + mOrderBean.getVMoblie());
        mGotoStoreTime.setText(getString(R.string.time_to_land) + (simpleDateFormat.format(calendar.getTime()) + TimeUtils.getWeek(this, calendar)
                + "\u3000" + mOrderBean.getAppointmentTime()));
        mOrderNumbering.setText(getText(R.string.order_numbering) + mOrderBean.getVbillno());
        mOrderTime.setText(getText(R.string.generate_order_time) + dateFormat.format(mOrderBean.getCreateDate()));
        mOrderProject.setText(getText(R.string.order_detect_porject) + mOrderBean.getOrderType());
    }

    @Override
    @NormalOnClick({EventConstants.NormalClick.navi, EventConstants.NormalClick.phone,
            EventConstants.NormalClick.cancelOrder})
    @ResId({R.id.navigation_img, R.id.phone_img, R.id.cancel_order_btn})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.navigation_img:
                showTypeDialog(NAVI_DIALOG, getString(R.string.prompt), getString(R.string.open_navigation), getString(R.string.dialog_sure));
                break;

            case R.id.phone_img:
                showTypeDialog(PHONE_DIALOG, getString(R.string.store_phone), mOrderBean.getTel(), getString(R.string.dialog_call));
                break;

            case R.id.cancel_order_btn:
                showTypeDialog(CANCEL_ORDER_DIALOG, getString(R.string.prompt), getString(R.string.whether_cancel_order), getString(R.string.dialog_sure));
                break;
        }
    }

    private void showTypeDialog(final int type, String title, final String message, String leftText) {
        final ConfirmDialog dialog = new ConfirmDialog(this);
        dialog.setTitle(title)
                .setContent(message)
                .setPositiveButton(leftText,new View.OnClickListener() {
                    @Override
                    @NormalOnClick({EventConstants.NormalClick.phoneSure})
                    public void onClick(View v) {
                        switch (type) {
                            case NAVI_DIALOG:
                                int ret= XmMapNaviManager.getInstance().startNaviToPoi(mOrderBean.getCompanyName(),mOrderBean.getAddress(), mOrderBean.getLng(),mOrderBean.getLat());
                                if(ret==-1){
                                    XMToast.showToast(OrderDetailDialog.this, "打开导航");
                                }

                                break;
                            case PHONE_DIALOG:
                                ServiceBlueToothPhoneManager.getInstance().callPhone(message);
                                break;
                            case CANCEL_ORDER_DIALOG:
                                if (!NetworkUtils.isConnected(OrderDetailDialog.this)) {
                                    XMToast.toastException(OrderDetailDialog.this, getResources().getString(R.string.net_work_error), false);
                                    return;
                                }
                                orderListVM.cancelAppointment(mOrderBean.getVbillno());
                                break;
                        }
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(getString(R.string.dialog_cancel),new View.OnClickListener() {
                    @Override
                    @NormalOnClick({EventConstants.NormalClick.phoneCancel})
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                })
                .show();
//
//        View view = View.inflate(this, R.layout.dialog_order_address_or_phone, null);
//        TextView dialogTitle = view.findViewById(R.id.dialog_title);
//        TextView dialogMessage = view.findViewById(R.id.dialog_message);
//        TextView sureBtn = view.findViewById(R.id.btn_sure);
//        TextView cancelBtn = view.findViewById(R.id.btn_cancel);
//        dialogTitle.setText(title);
//        dialogMessage.setText(message);
//        final XmDialog builder = new XmDialog.Builder(this)
//                .setView(view)
//                .setWidth(this.getResources().getDimensionPixelOffset(R.dimen.dialog_activity_dialog_width))
//                .setHeight(this.getResources().getDimensionPixelOffset(R.dimen.dialog_activity_dialog_height))
//                .create();
//        builder.setCancelable(false);
//        sureBtn.setText(leftText);
//        sureBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            @NormalOnClick({EventConstants.NormalClick.phoneSure})
//            public void onClick(View v) {
//                switch (type) {
//                    case NAVI_DIALOG:
//                        XMToast.showToast(OrderDetailDialog.this,"打开导航");
//                        NaviUtils.startNavi(OrderDetailDialog.this, mOrderBean.getLat(), mOrderBean.getLng(), mOrderBean.getCompanyName());
//                        break;
//                    case PHONE_DIALOG:
//                        //todo  待接口拨打蓝牙电话  phone = message
//                        break;
//                    case CANCEL_ORDER_DIALOG:
//                        if (!NetworkUtils.isConnected(OrderDetailDialog.this)) {
//                            XMToast.toastException(OrderDetailDialog.this, getResources().getString(R.string.net_work_error), false);
//                            return;
//                        }
//                        orderListVM.cancelAppointment(mOrderBean.getVbillno());
//                        break;
//                }
//                builder.dismiss();
//            }
//        });
//        cancelBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            @NormalOnClick({EventConstants.NormalClick.phoneCancel})
//            public void onClick(View v) {
//                builder.dismiss();
//            }
//        });
//        builder.show();
    }

    @Override
    public void finish() {
        super.finish();
        //注释掉activity本身的过渡动画
        overridePendingTransition(0, 0);
    }
}

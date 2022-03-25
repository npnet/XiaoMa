package com.xiaoma.personal.order.ui.detail;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.mapbar.xiaoma.manager.XmMapNaviManager;
import com.xiaoma.component.base.BaseFragment;
import com.xiaoma.model.ResultCallback;
import com.xiaoma.model.XMResult;
import com.xiaoma.model.XmResource;
import com.xiaoma.model.annotation.NormalOnClick;
import com.xiaoma.model.annotation.PageDescComponent;
import com.xiaoma.model.annotation.ResId;
import com.xiaoma.personal.R;
import com.xiaoma.personal.common.OnlyCode;
import com.xiaoma.personal.common.RequestManager;
import com.xiaoma.personal.common.util.EventConstants;
import com.xiaoma.personal.order.constants.CallPhoneUtils;
import com.xiaoma.personal.order.constants.Constants;
import com.xiaoma.personal.order.constants.DialogHandlerCallback;
import com.xiaoma.personal.order.constants.OrderDialogFactory;
import com.xiaoma.personal.order.constants.OrderStatusId;
import com.xiaoma.personal.order.constants.timer.OnTimerCallback;
import com.xiaoma.personal.order.constants.timer.OrderTimer;
import com.xiaoma.personal.order.model.OrderInfo;
import com.xiaoma.personal.order.vm.OrderVM;
import com.xiaoma.ui.progress.loading.XMProgress;
import com.xiaoma.ui.toast.XMToast;
import com.xiaoma.utils.LaunchUtils;
import com.xiaoma.utils.NetworkUtils;
import com.xiaoma.utils.log.KLog;

import java.util.Locale;
import java.util.Objects;

/**
 * <pre>
 *       @author Created by Gillben
 *       date: 2019/3/13 0013 16:25
 *       desc：酒店详情
 * </pre>
 */
@PageDescComponent(EventConstants.PageDescribe.hotelDetailFragment)
public class HotelDetailFragment extends BaseFragment implements View.OnClickListener {

    private static final String TAG = HotelDetailFragment.class.getSimpleName();
    private ConstraintLayout rootLayout;
    private ImageView orderStatusImage;
    private TextView orderStatusDesc;
    //酒店相关信息
    private TextView mRoomName;
    private TextView mRoomPrice;
    private TextView mRoomService;
    private TextView mHotelName;

    //user相关信息
    private RelativeLayout mFourthRelative;
    private TextView mFirstInfoTitle;
    private TextView mFirstInfo;
    private TextView mSecondInfoTitle;
    private TextView mSecondInfo;
    private TextView mThirdInfoTitle;
    private TextView mThirdInfo;
    private TextView mFourthInfoTitle;
    private TextView mFourthInfo;

    //扫码区
    private ConstraintLayout scanCodeLayout;
    private ImageView mQRCodeImage;
    private TextView mCodePreviewAmount;

    //底部操作区
    private LinearLayout mPayLinear;
    private TextView mTimerText;
    private ConstraintLayout mPaidLayout;
    private Button mNavButton;
    private Button mPhoneButton;
    private Button mPredestineButton;


    private long orderId;
    private OrderVM orderVM;
    private int totalInterval;
    private boolean startTimer = false;
    private int currentPayStatus = -1;          //当前支付状态
    private boolean isCanCancel = true;         //是否可取消操作，默认true
    private OrderInfo.Order mOrderDetailInfo;   //保存一个全局订单详情
    private static final int OBTAIN_ORDER_CONFIRM_STATUS_INTERVAL = 5000;
    private boolean refreshFlag;

    public static HotelDetailFragment newInstance(long id) {
        Bundle args = new Bundle();
        args.putLong(Constants.HOTEL_ORDER_ID, id);
        HotelDetailFragment fragment = new HotelDetailFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_hotel_detail, container, false);
        return super.onCreateWrapView(view);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);

        Bundle bundle = getArguments();
        assert bundle != null;
        orderId = bundle.getLong(Constants.HOTEL_ORDER_ID);

        if (NetworkUtils.isConnected(mContext)) {
            initData();
        } else {
            showNoNetView();
        }
    }


    @Override
    protected void noNetworkOnRetry() {
        super.noNetworkOnRetry();
        initData();
    }


    private void initView(View view) {
        rootLayout = view.findViewById(R.id.hotel_detail_root);
        rootLayout.setVisibility(View.GONE);
        orderStatusImage = view.findViewById(R.id.iv_order_status_icon);
        orderStatusDesc = view.findViewById(R.id.tv_order_status_desc);
        mRoomName = view.findViewById(R.id.tv_order_content_name);
        mRoomPrice = view.findViewById(R.id.tv_order_content_price);
        mRoomService = view.findViewById(R.id.tv_order_room_service);
        mHotelName = view.findViewById(R.id.tv_order_content_author);
        mFourthRelative = view.findViewById(R.id.rl_info_fourth);
        mFirstInfoTitle = view.findViewById(R.id.tv_info_first_title);
        mFirstInfo = view.findViewById(R.id.tv_info_first_content);
        mSecondInfoTitle = view.findViewById(R.id.tv_info_second_title);
        mSecondInfo = view.findViewById(R.id.tv_info_second_content);
        mThirdInfoTitle = view.findViewById(R.id.tv_info_third_title);
        mThirdInfo = view.findViewById(R.id.tv_info_third_content);
        mFourthInfoTitle = view.findViewById(R.id.tv_info_fourth_title);
        mFourthInfo = view.findViewById(R.id.tv_info_fourth_content);
        scanCodeLayout = view.findViewById(R.id.scan_qr_code_layout);
        mQRCodeImage = view.findViewById(R.id.iv_order_qr_code);
        mCodePreviewAmount = view.findViewById(R.id.tv_scan_code_amount);
        mPayLinear = view.findViewById(R.id.item_order_pay_operation_layout);
        mTimerText = view.findViewById(R.id.tv_pay_timer);
        mPaidLayout = view.findViewById(R.id.order_detail_paid_layout);
        mNavButton = view.findViewById(R.id.bt_paid_navigation);
        mPhoneButton = view.findViewById(R.id.bt_paid_phone);
        mPredestineButton = view.findViewById(R.id.bt_paid_predestine);

        mNavButton.setOnClickListener(this);
        mPhoneButton.setOnClickListener(this);
        mPredestineButton.setOnClickListener(this);
        mPayLinear.setOnClickListener(this);
    }


    private void initData() {
        orderVM = ViewModelProviders.of(this).get(OrderVM.class);
        XMProgress.showProgressDialog(this, getResources().getString(R.string.progress_loading));
        fetchData();
    }


    @Override
    @NormalOnClick({EventConstants.NormalClick.orderDetailUpdatePayStatus,
            EventConstants.NormalClick.orderDetailNavigation,
            EventConstants.NormalClick.orderDetailCallPhone,
            EventConstants.NormalClick.orderDetailPredestine})
    @ResId({R.id.item_order_pay_operation_layout,
            R.id.bt_paid_navigation,
            R.id.bt_paid_phone,
            R.id.bt_paid_predestine})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.item_order_pay_operation_layout:
                //TODO 立即更新支付状态
                XMProgress.showProgressDialog(this, getResources().getString(R.string.progress_loading));
                refreshFlag = true;
                fetchData();
                break;

            case R.id.bt_paid_navigation:
                //TODO 导航
                OrderInfo.Order.Hotel hotel = mOrderDetailInfo.getHotelJsonVo();
                if (hotel != null) {
                    double lon = Double.parseDouble(hotel.getLon());
                    double lat = Double.parseDouble(hotel.getLat());
                    XmMapNaviManager.getInstance().startNaviToPoi(hotel.getHotelName(), hotel.getAddress(), lon, lat);
                } else {
                    KLog.d(TAG, "Hotel instance is null.");
                }
                break;

            case R.id.bt_paid_phone:
                //TODO 蓝牙电话
                CallPhoneUtils.callBluetoothPhone(getActivity(), mOrderDetailInfo.getHotelJsonVo().getMobile(), response -> KLog.w("电话拨打失败"));
                break;

            case R.id.bt_paid_predestine:
                //3：已付款完成
                KLog.i("orderStatusId: " + mOrderDetailInfo.getOrderStatusId());
                int id = mOrderDetailInfo.getOrderStatusId();
                if (id == OrderStatusId.COMPLETE || id == OrderStatusId.WAIT_CONFIRM) {
                    if (isCanCancel) {
                        cancelPredestineOrder(mOrderDetailInfo);
                    } else {
                        XMToast.showToast(mContext, getResources().getString(R.string.not_can_cancel_toast));
                    }
                } else {
                    renewPredestineHotel();
                }
                break;
        }
    }

    private void renewPredestineHotel() {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.HOTEL_ID_TAG, mOrderDetailInfo.getHotelJsonVo().getId());
        bundle.putString(Constants.HOTEL_NAME_TAG, mOrderDetailInfo.getHotelJsonVo().getHotelName());
        bundle.putString(Constants.HOTEL_ADDRESS_TAG, mOrderDetailInfo.getHotelJsonVo().getAddress());
        bundle.putString(Constants.HOTEL_LAT_TAG, mOrderDetailInfo.getHotelJsonVo().getLat());
        bundle.putString(Constants.HOTEL_LON_TAG, mOrderDetailInfo.getHotelJsonVo().getLon());
        bundle.putString(Constants.HOTEL_PHONE_TAG, mOrderDetailInfo.getHotelJsonVo().getMobile());
        bundle.putString(Constants.HOTEL_ICON_URL_TAG, mOrderDetailInfo.getHotelJsonVo().getIconUrl());
        boolean result = LaunchUtils.launchAppWithData(mContext, Constants.LAUNCHER_PKG, Constants.RENEW_PREDESTINE_HOTEL, bundle);

        KLog.w("Hotel predestine result: " + result);
        if (result) {
            Objects.requireNonNull(getActivity()).finish();
        }
    }


    private void fetchData() {
        orderVM.getOrderDetailInfo(orderId).observe(this, new Observer<XmResource<OrderInfo.Order>>() {
            @Override
            public void onChanged(@Nullable XmResource<OrderInfo.Order> orderDetailInfo) {
                assert orderDetailInfo != null;
                orderDetailInfo.handle(new OnCallback<OrderInfo.Order>() {
                    @Override
                    public void onSuccess(OrderInfo.Order data) {
                        XMProgress.dismissProgressDialog(HotelDetailFragment.this);
                        if (refreshFlag && data.getOrderStatusId() != OrderStatusId.COMPLETE) {
                            refreshFlag = false;
                            XMToast.showToast(mContext, R.string.not_pay_finish);
                        }

                        showOrderDetailInfo(data);
                        rootLayout.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onFailure(String msg) {
                        super.onFailure(msg);
                        if (refreshFlag) {
                            refreshFlag = false;
                            XMProgress.dismissProgressDialog(HotelDetailFragment.this);
                            XMToast.toastException(mContext, R.string.no_network);
                        }
                    }
                });
            }
        });
    }


    private void showOrderDetailInfo(OrderInfo.Order data) {
        if (currentPayStatus == data.getOrderStatusId()) {
            return;
        }
        mOrderDetailInfo = data;
        currentPayStatus = data.getOrderStatusId();

        mRoomName.setText(data.getHotelJsonVo().getRoomType());
        String retainOneDecimal = String.format(Locale.CHINA, "%.2f", Float.parseFloat(data.getAmount()));
        mRoomPrice.setText(getResources().getString(R.string.hotel_room_unit_price, retainOneDecimal));
        mRoomService.setText(data.getHotelJsonVo().getRoomMsg());
        mHotelName.setText(data.getHotelJsonVo().getHotelName());
        mFirstInfoTitle.setText(R.string.predestine_hotel_room);
        mFirstInfo.setText(getResources().getString(R.string.hotel_room, data.getTicketNum()));
        mSecondInfoTitle.setText(R.string.check_in_check_out_room);
        String inOutDate = checkInOutHotel(data);
        mSecondInfo.setText(inOutDate);
        mFourthInfoTitle.setText(R.string.contact_way);
        mFourthInfo.setText(data.getBookPhone());

        if ("false".equals(data.getHotelJsonVo().getCanCancel())) {
            mThirdInfo.setText(R.string.not_can_cancel);
            isCanCancel = false;
        } else {
            String cancelDate = inOutDate.substring(inOutDate.indexOf("-") + 1);
            mThirdInfo.setText(getResources().getString(R.string.can_cancel_predestine, cancelDate));
        }

        switch (data.getOrderStatusId()) {
            case OrderStatusId.WAIT_PAY:
                orderWaitPay(data);
                break;

            case OrderStatusId.WAIT_REFUND:
            case OrderStatusId.WAIT_CONFIRM:
                orderWaitConfirm(data);
                break;

            case OrderStatusId.COMPLETE:
                orderComplete(data);
                break;

            case OrderStatusId.CANCEL:
            case OrderStatusId.EXPIRED:
            case OrderStatusId.CLOSED:
                orderClosed(data);
                break;
        }
    }


    private void orderWaitPay(OrderInfo.Order data) {
        scanCodeLayout.setVisibility(View.VISIBLE);
        mPayLinear.setVisibility(View.VISIBLE);
        mPaidLayout.setVisibility(View.GONE);
        mFourthRelative.setGravity(View.GONE);

        orderStatusDesc.setText(getResources().getString(R.string.order_status_wait_pay));
        Glide.with(this).load(data.getPayQrcode()).into(mQRCodeImage);
        String retainOneDecimal = String.format(Locale.CHINA, "%.2f", Float.parseFloat(data.getAmount()));
        mCodePreviewAmount.setText(getResources().getString(R.string.preview_amount, retainOneDecimal));

        //开启订单计时
        timerLoop(data);
    }


    private void orderWaitConfirm(OrderInfo.Order data) {
        scanCodeLayout.setVisibility(View.GONE);
        mPayLinear.setVisibility(View.GONE);
        mPaidLayout.setVisibility(View.VISIBLE);
        mFourthRelative.setVisibility(View.VISIBLE);
        mPredestineButton.setEnabled(true);
        if (data.getOrderStatusId() == OrderStatusId.WAIT_REFUND) {
            mPredestineButton.setText(getResources().getString(R.string.re_predestine));
            orderStatusDesc.setText(getResources().getString(R.string.order_status_failed));
            orderStatusImage.setImageResource(R.drawable.order_exception);
        } else if (data.getOrderStatusId() == OrderStatusId.WAIT_CONFIRM) {
            mPredestineButton.setText(getResources().getString(R.string.cancel_predestine));
            orderStatusDesc.setText(R.string.order_status_wait_feedback);
            orderStatusImage.setImageResource(R.drawable.wait_confirm);
            OrderTimer.endTimer();
            //重新开启轮询拉取商家反馈信息
            OrderTimer.startTimer(OBTAIN_ORDER_CONFIRM_STATUS_INTERVAL, this::fetchData);
        }
    }


    private void orderComplete(OrderInfo.Order data) {
        scanCodeLayout.setVisibility(View.GONE);
        mPayLinear.setVisibility(View.GONE);
        mPaidLayout.setVisibility(View.VISIBLE);
        mFourthRelative.setVisibility(View.VISIBLE);

        mPredestineButton.setEnabled(true);
        mPredestineButton.setText(getResources().getString(R.string.cancel_predestine));
        orderStatusDesc.setText(getResources().getString(R.string.order_status_predestine_success));
        orderStatusImage.setImageResource(R.drawable.predestine_success);
        OrderTimer.endTimer();
    }

    private void orderClosed(OrderInfo.Order data) {
        scanCodeLayout.setVisibility(View.GONE);
        mPayLinear.setVisibility(View.GONE);
        mPaidLayout.setVisibility(View.VISIBLE);
        mFourthRelative.setVisibility(View.VISIBLE);
        mPredestineButton.setVisibility(View.VISIBLE);
        mPredestineButton.setText(getResources().getString(R.string.re_predestine));
        orderStatusDesc.setText(getResources().getString(R.string.order_status_expired));

        OrderTimer.endTimer();
    }


    /**
     * 酒店入住和退房日期转换
     */
    private String checkInOutHotel(OrderInfo.Order orderDetailInfo) {
        String checkIn = orderDetailInfo.getCheckIn();
        String checkOut = orderDetailInfo.getCheckOut();
        String[] startDay = checkIn.split("-");
        String[] endDay = checkOut.split("-");
        String tempStart = getResources().getString(R.string.item_Film_date, startDay[1], startDay[2]);
        String tempEnd = getResources().getString(R.string.item_Film_date, endDay[1], endDay[2]);
        return tempStart + "-" + tempEnd;
    }


    private void timerLoop(final OrderInfo.Order orderDetailInfo) {
        OrderTimer.startTimer(1000, new OnTimerCallback() {
            @Override
            public void onTimer() {
                calculationTime(orderDetailInfo);
            }
        });
    }


    private void calculationTime(OrderInfo.Order orderDetailInfo) {
        if (!startTimer) {
            totalInterval = OrderTimer.calculationInterval(orderDetailInfo.getCurrentDate(), orderDetailInfo.getLastpayDate());
        }

        //订单保持待支付状态
        if (totalInterval > 0) {
            startTimer = true;
            --totalInterval;
            int min = totalInterval / 60;
            int seconds = totalInterval % 60;
            String curMin = String.format(Locale.CHINA, "%02d", min);
            String curSeconds = String.format(Locale.CHINA, "%02d", seconds);
            mTimerText.setText(getResources().getString(R.string.pay_timer_interval, curMin, curSeconds));

            if (totalInterval % 5 == 0) {
                fetchData();
            }
        } else {
            startTimer = false;
            currentPayStatus = -1;
            mOrderDetailInfo.setOrderStatusId(7);
            orderClosed(orderDetailInfo);
        }

    }


    private void cancelPredestineOrder(final OrderInfo.Order orderDetailInfo) {
        OrderDialogFactory.createCallPhoneOrCancelPredestineDialog(Objects.requireNonNull(getActivity()),
                getString(R.string.order_dialog_cancel_predestine_desc),
                getString(R.string.order_dialog_cancel_predestine_content),
                getString(R.string.cancel_predestine),
                new DialogHandlerCallback() {
                    @Override
                    public void handle() {
                        RequestManager.cancelMineOrder(
                                orderDetailInfo.getId(),
                                orderDetailInfo.getOrderStatusId(),
                                new ResultCallback<XMResult<OnlyCode>>() {
                                    @Override
                                    public void onSuccess(XMResult<OnlyCode> result) {
                                        handleCancelPredestine();
                                    }

                                    @Override
                                    public void onFailure(int code, String msg) {
                                        XMToast.showToast(mContext, R.string.hint_fail_to_cancel_booked);
                                    }
                                });
                    }
                });
    }


    private void handleCancelPredestine() {
        orderStatusDesc.setText(R.string.order_status_predestine_cancel);
        mPredestineButton.setVisibility(View.GONE);
        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) mNavButton.getLayoutParams();
        params.setMarginStart(getResources().getDimensionPixelSize(R.dimen.size_order_detail_operation_button_left_long));
        mNavButton.setLayoutParams(params);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        startTimer = false;
        OrderTimer.endTimer();
    }
}

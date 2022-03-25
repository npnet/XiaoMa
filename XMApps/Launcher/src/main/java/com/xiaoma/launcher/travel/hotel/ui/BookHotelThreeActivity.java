package com.xiaoma.launcher.travel.hotel.ui;


/*  @project_name：  XMAgateOS
 *  @package_name：  com.xiaoma.launcher.travel.hotel.ui
 *  @file_name:      BookHotelActivity
 *  @author:         Rookie
 *  @create_time:    2019/1/4 16:21
 *  @description：   预订酒店房间             */

import android.annotation.SuppressLint;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xiaoma.autotracker.XmTracker;
import com.xiaoma.autotracker.listener.XMAutoTrackerEventOnClickListener;
import com.xiaoma.autotracker.model.TrackerCountType;
import com.xiaoma.component.base.BaseActivity;
import com.xiaoma.image.ImageLoader;
import com.xiaoma.launcher.R;
import com.xiaoma.launcher.common.constant.EventConstants;
import com.xiaoma.launcher.common.constant.LauncherConstants;
import com.xiaoma.launcher.travel.hotel.constants.HotelConstants;
import com.xiaoma.launcher.travel.hotel.vm.HotelRoomVM;
import com.xiaoma.model.ItemEvent;
import com.xiaoma.model.XMResult;
import com.xiaoma.model.XmResource;
import com.xiaoma.model.annotation.BusinessOnClick;
import com.xiaoma.model.annotation.NormalOnClick;
import com.xiaoma.model.annotation.PageDescComponent;
import com.xiaoma.model.annotation.ResId;
import com.xiaoma.thread.ThreadDispatcher;
import com.xiaoma.trip.orders.response.OrdersBean;
import com.xiaoma.ui.dialog.ConfirmDialog;
import com.xiaoma.ui.dialog.XmDialog;
import com.xiaoma.ui.toast.XMToast;
import com.xiaoma.utils.LaunchUtils;
import com.xiaoma.utils.NetworkUtils;
import com.xiaoma.utils.StringUtil;
import com.xiaoma.utils.TimeUtils;

import java.text.SimpleDateFormat;
import java.util.Locale;

@PageDescComponent(EventConstants.PageDescribe.BookHotelThreeActivityPagePathDesc)
public class BookHotelThreeActivity extends BaseActivity implements View.OnClickListener {


    private TextView tvSecond;
    private TextView tvThree;
    private TextView tvSecondInfo;
    private TextView tvThreeInfo;
    private TextView tvRoomTypePrice;
    private TextView tvRoomMsg;
    private TextView tvHotelName;
    private TextView tvRoomCount;
    private TextView tvGuestDate;
    private TextView tvCancelDate;
    private TextView tvBookingPhone;
    private ImageView ivQrCode;
    private TextView tvPayPrice;
    private TextView tvOrderTime;
    private TextView tvBookTip;
    private Button btnGuide;
    private Button btnCancel;
    private Button btnCall;

    private View stepView;
    private RelativeLayout rlPay;
    private LinearLayout llPay;
    private LinearLayout llPhone;
    private LinearLayout llPaySuccess;
    private LinearLayout llComplete;
    private ImageView iconBookTip;

    private String mHotelId;
    private String mRoomType;
    private String mRoomPrice;
    private String mHotelName;
    private String mCheckIn;
    private String mCheckOut;
    private String mBookPhone;
    private int mRoomCount;
    private String mRoomMsg;
    private String mOrderAmount;
    private String orderQrCode;
    private String orderId;
    private boolean isCancel;
    private String lastCancelTime;
    private long lastPayDate;
    private long createDate;
    private String address;
    private String lat;
    private String lon;
    private String storePhone;
    private String imageUrl;
    private CountDownTimer countDownTimer;
    private long POLLING_TIME = 5 * 1000;
    private long countDownTime;
    private static final int PAY_SUCCESS_CODE = 3; //已完成
    private static final int PAY_CONFIRMING_CODE = 1; //待确认
    private static final int PAY_OUT_DATE_CODE = 7;   //已过期
    private static final int PAY_ABNORMAL_CODE = -1;//异常
    private static final int PAY_CLOSED_CODE = 6;   //已关闭
    private static final int PAY_REFUND_CODE = 8;  //待退款
    private boolean isClickQuery = false;
    private boolean isWaitPay = true;
    private HotelRoomVM hotelRoomVM;
    private XmDialog backDialog;
    private XmDialog cancelDialog;


    private Runnable pollingRunnable = new Runnable() {
        @Override
        public void run() {
            hotelRoomVM.fetchOrder(orderId);
            ThreadDispatcher.getDispatcher().postDelayed(this, POLLING_TIME);
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_hotel_three);
        bindView();
        getDataFromIntent();
        setViewData();
        initData();
        pollingPayStatus();
    }


    private void bindView() {
        tvSecond = findViewById(R.id.tap_two_hotel);
        tvSecondInfo = findViewById(R.id.tap_two_select_session);
        tvThree = findViewById(R.id.tap_three_hotel);
        tvThreeInfo = findViewById(R.id.tap_three_confirm_pay);
        tvRoomTypePrice = findViewById(R.id.tv_room_type_price);
        tvRoomMsg = findViewById(R.id.tv_room_msg);
        tvHotelName = findViewById(R.id.tv_hotel_name);
        tvRoomCount = findViewById(R.id.tv_room_num);
        tvGuestDate = findViewById(R.id.tv_guest_date);
        tvCancelDate = findViewById(R.id.tv_cancel_date);
        tvBookingPhone = findViewById(R.id.tv_booking_phone);
        ivQrCode = findViewById(R.id.iv_pay_code);
        tvPayPrice = findViewById(R.id.tv_pay_price);
        tvOrderTime = findViewById(R.id.tv_order_time);
        tvBookTip = findViewById(R.id.tv_book_tip);
        btnCall = findViewById(R.id.btn_call);
        btnGuide = findViewById(R.id.btn_guide);
        btnCancel = findViewById(R.id.btn_cancel);

        llPay = findViewById(R.id.ll_pay);
        llPhone = findViewById(R.id.ll_phone);
        llPaySuccess = findViewById(R.id.ll_pay_success);
        stepView = findViewById(R.id.view_step);
        rlPay = findViewById(R.id.rl_pay);
        llComplete = findViewById(R.id.ll_complete);
        iconBookTip = findViewById(R.id.icon_book_three);


        tvSecond.setBackground(getDrawable(R.drawable.round_back_yellow));
        tvSecondInfo.setTextColor(getColor(R.color.white));
        tvThree.setBackground(getDrawable(R.drawable.round_back_yellow));
        tvThreeInfo.setTextColor(getColor(R.color.white));

        llPay.setOnClickListener(this);
        btnCall.setOnClickListener(this);
        btnGuide.setOnClickListener(this);
        btnCancel.setOnClickListener(new XMAutoTrackerEventOnClickListener() {
            @Override
            public ItemEvent returnPositionEventMsg(View view) {
                if (getString(R.string.cancel_book).equals(btnCancel.getText())) {
                    return new ItemEvent(EventConstants.NormalClick.HOTEL_HOME_PAY_CANCE, "");
                } else if (getString(R.string.re_booking).equals(btnCancel.getText())) {
                    return new ItemEvent(EventConstants.NormalClick.HOTEL_HOME_PAY_RETRY, "");
                }
                return new ItemEvent();
            }

            @Override
            @BusinessOnClick
            public void onClick(View v) {
                if (getString(R.string.cancel_book).equals(btnCancel.getText())) {
                    showCancelDialog();
                } else if (getString(R.string.re_booking).equals(btnCancel.getText())) {

                    Bundle bundle = new Bundle();
                    bundle.putString(HotelConstants.HOTEL_ID_TAG, mHotelId);
                    bundle.putString(HotelConstants.HOTEL_NAME_TAG, mHotelName);
                    bundle.putString(HotelConstants.HOTEL_ADDRESS_TAG, address);
                    bundle.putString(HotelConstants.HOTEL_LAT_TAG, lat);
                    bundle.putString(HotelConstants.HOTEL_LON_TAG, lon);
                    bundle.putString(HotelConstants.HOTEL_PHONE_TAG, storePhone);
                    bundle.putString(HotelConstants.HOTEL_ICON_URL_TAG, imageUrl);

                    Intent intent = new Intent(BookHotelThreeActivity.this, SelectDateActivity.class);
                    intent.putExtras(bundle);
                    startActivity(intent);

                    finish();
                }
            }
        });

        getNaviBar().getMiddleView().setImageDrawable(getDrawable(R.drawable.selector_iv_hotel_policy));
        getNaviBar().getMiddleView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (StringUtil.isEmpty(mHotelId)) {
                    showToast(R.string.error_msg_hotel_id);
                    return;
                }

                Intent intent = new Intent(BookHotelThreeActivity.this, BookHotelPolicyActivity.class);
                intent.putExtra(HotelConstants.HOTEL_ID, mHotelId);
                startActivity(intent);
            }
        });
    }


    private void getDataFromIntent() {
        mHotelId = getIntent().getStringExtra(HotelConstants.HOTEL_ID);
        mRoomType = getIntent().getStringExtra(HotelConstants.HOTEL_ROOM_TYPE);
        mRoomPrice = getIntent().getStringExtra(HotelConstants.HOTEL_ROOM_PRICE);
        mHotelName = getIntent().getStringExtra(HotelConstants.HOTEL_NAME);
        mCheckIn = getIntent().getStringExtra(HotelConstants.HOTEL_CHECK_IN);
        mCheckOut = getIntent().getStringExtra(HotelConstants.HOTEL_CHECK_OUT);
        mBookPhone = getIntent().getStringExtra(HotelConstants.HOTEL_BOOK_PHONE);
        mRoomCount = getIntent().getIntExtra(HotelConstants.HOTEL_ROOM_COUNT, 1);
        mRoomMsg = getIntent().getStringExtra(HotelConstants.HOTEL_ROOM_MSG);
        mOrderAmount = getIntent().getStringExtra(HotelConstants.HOTEL_ORDER_AMOUNT);
        orderQrCode = getIntent().getStringExtra(HotelConstants.HOTEL_ORDER_QRCODE);
        orderId = getIntent().getStringExtra(HotelConstants.HOTEL_ORDER_ID);
        isCancel = getIntent().getBooleanExtra(HotelConstants.HOTEL_ORDER_IS_CANCEL, false);
        lastCancelTime = getIntent().getStringExtra(HotelConstants.HOTEL_CANCEL_TIME);
        lastPayDate = getIntent().getLongExtra(HotelConstants.ORDER_LAST_PAY_DATE, -1);
        createDate = getIntent().getLongExtra(HotelConstants.ORDER_CREATE_DATE, -1);
        address = getIntent().getStringExtra(HotelConstants.HOTEL_ADDRESS);
        lat = getIntent().getStringExtra(HotelConstants.HOTEL_LAT);
        lon = getIntent().getStringExtra(HotelConstants.HOTEL_LON);
        storePhone = getIntent().getStringExtra(HotelConstants.HOTEL_PHONE);
        imageUrl = getIntent().getStringExtra(HotelConstants.HOTEL_IMAGE);

        countDownTime = lastPayDate - createDate;
    }

    @SuppressLint("StringFormatInvalid")
    private void setViewData() {
        String roomTypePrice = mRoomType + "  " + "<font color='#fbd3a4'>" + "￥" + mRoomPrice + " / 间" + "</font>";
        tvRoomTypePrice.setText(Html.fromHtml(roomTypePrice));
        tvRoomMsg.setText(mRoomMsg);
        tvHotelName.setText(mHotelName);
        tvRoomCount.setText(String.format(getString(R.string.booking_three_room_count), mRoomCount));
        tvGuestDate.setText(String.format(getString(R.string.booking_three_room_date), mCheckIn, mCheckOut));
        tvBookingPhone.setText(String.format(getString(R.string.booking_three_phone), mBookPhone));
        tvPayPrice.setText("￥" + mOrderAmount);
        if (isCancel) {
            tvCancelDate.setText(String.format(getString(R.string.booking_three_room_cancel_date), lastCancelTime));
        } else {
            tvCancelDate.setText(getString(R.string.booking_three_no_cancel));
        }
        try {
            ImageLoader.with(this).load(orderQrCode).into(ivQrCode);
        } catch (Exception e) {
            e.printStackTrace();
        }

        XMToast.showToast(this, getString(R.string.please_scan_qrcode_pay), getDrawable(R.drawable.toast_success));

    }

    private void initData() {

        countDownTimer = new CountDownTimer(countDownTime, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                tvOrderTime.setText(TimeUtils.millis2String(millisUntilFinished, new SimpleDateFormat("mm:ss", Locale.getDefault())));
            }

            @Override
            public void onFinish() {
                ThreadDispatcher.getDispatcher().remove(pollingRunnable);
                hotelRoomVM.fetchOrder(orderId);

            }
        }.start();

        hotelRoomVM = ViewModelProviders.of(this).get(HotelRoomVM.class);

        //取消订单结果
        hotelRoomVM.getCancelResult().observe(this, new Observer<XmResource<XMResult>>() {
            @Override
            public void onChanged(@Nullable XmResource<XMResult> result) {
                if (result == null) {
                    return;
                }
                result.handle(new OnCallback<XMResult>() {
                    @Override
                    public void onSuccess(XMResult data) {
                        if (data.isSuccess()) {
                            showToast(R.string.cancel_order_success);
                            btnCancel.setVisibility(View.GONE);
                            iconBookTip.setImageDrawable(getDrawable(R.drawable.list_icon_right));
                            tvBookTip.setText(getString(R.string.cancel_order_success));
                        }

                    }
                });
            }
        });
        //获取订单数据,是否支付成功
        hotelRoomVM.getOrdersData().observe(this, new Observer<XmResource<OrdersBean>>() {
            @Override
            public void onChanged(@Nullable XmResource<OrdersBean> listXmResource) {
                if (listXmResource == null) {
                    return;
                }
                listXmResource.handle(new OnCallback<OrdersBean>() {
                    @Override
                    public void onSuccess(OrdersBean data) {
                        if (data != null) {
                            notifyPayStatus(data);
                        }

                    }

                });

            }
        });
    }

    /**
     * 支付成功
     */
    private void onPaySuccess(String statusMessage, int code) {
        isWaitPay = false;
        stepView.setVisibility(View.INVISIBLE);
        rlPay.setVisibility(View.INVISIBLE);
        llPay.setVisibility(View.INVISIBLE);
        llPaySuccess.setVisibility(View.VISIBLE);
        llPhone.setVisibility(View.VISIBLE);
        llComplete.setVisibility(View.VISIBLE);
        tvBookTip.setText(statusMessage);
        if (code == PAY_OUT_DATE_CODE || isFailed(code)) {
            btnCancel.setVisibility(View.VISIBLE);
            btnCancel.setText(getString(R.string.re_booking));
        } else {
            if (!isCancel) {
                btnCancel.setVisibility(View.GONE);
            }
        }


    }

    /**
     * orderStatusId
     * 0	已取消
     * 1	待确认
     * 2	待支付
     * 3	已完成
     * 4	已删除
     * 订单状态刷新
     *
     * @param data
     */
    private synchronized void notifyPayStatus(OrdersBean data) {
        if (PAY_SUCCESS_CODE == data.getOrderStatusId()) {
            dismissBackDialog();
            iconBookTip.setImageDrawable(getDrawable(R.drawable.list_icon_right));
            onPaySuccess(getString(R.string.order_success), data.getOrderStatusId());
            ThreadDispatcher.getDispatcher().remove(pollingRunnable);
            XmTracker.getInstance().uploadEvent(-1,
                    TrackerCountType.BOOKHOTEL.getType());
        } else if (PAY_CONFIRMING_CODE == data.getOrderStatusId()) {
            dismissBackDialog();
            iconBookTip.setImageDrawable(getDrawable(R.drawable.list_icon_right));
            onPaySuccess(getString(R.string.order_loading), data.getOrderStatusId());
            ThreadDispatcher.getDispatcher().remove(pollingRunnable);
        } else if (PAY_OUT_DATE_CODE == data.getOrderStatusId()) {
            dismissBackDialog();
            iconBookTip.setImageDrawable(getDrawable(R.drawable.list_icon_wrong));
            onPaySuccess(getString(R.string.order_out_date), data.getOrderStatusId());
            ThreadDispatcher.getDispatcher().remove(pollingRunnable);
        } else if (isFailed(data.getOrderStatusId())) {
            dismissBackDialog();
            iconBookTip.setImageDrawable(getDrawable(R.drawable.list_icon_wrong));
            onPaySuccess(getString(R.string.order_failed), data.getOrderStatusId());
            ThreadDispatcher.getDispatcher().remove(pollingRunnable);
        } else if (isClickQuery) {
            showToast(R.string.rescan_to_pay);
            isClickQuery = false;
        }
    }

    /**
     * 轮询获取支付状态
     */
    private void pollingPayStatus() {
        ThreadDispatcher.getDispatcher().postLowPriority(pollingRunnable);
    }

    @Override
    @NormalOnClick({EventConstants.NormalClick.HOTEL_HOME_PAY_STATUS, EventConstants.NormalClick.HOTEL_HOME_PAY_PHONE, EventConstants.NormalClick.HOTEL_HOME_PAY_NAVI})//按钮对应的名称
    @ResId({R.id.ll_pay, R.id.btn_call, R.id.btn_guide})//按钮对应的R文件id
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_pay:

                if (!NetworkUtils.isConnected(this)) {
                    showToastException(R.string.net_work_error);
                    return;
                }

                isClickQuery = true;
                hotelRoomVM.fetchOrder(orderId);
                break;

            case R.id.btn_call:
                showToast(getString(R.string.btn_phone));
                break;
            case R.id.btn_guide:
                showToast(getString(R.string.btn_navi));
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (isWaitPay) {
            showBackDialog();
            return;
        }
        super.onBackPressed();
    }

    private void showCancelDialog() {
        SpannableString s1 = new SpannableString(getString(R.string.travel_booking_three_cancel_message));
        s1.setSpan(new AbsoluteSizeSpan(32, true), 0, s1.length() - 17, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        s1.setSpan(new AbsoluteSizeSpan(28, true), s1.length() - 17, s1.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ConfirmDialog dialog = new ConfirmDialog(this);
        dialog.setContent(s1)
                .setPositiveButton(getString(R.string.travel_left_btn_cancel_book), new View.OnClickListener() {
                    @NormalOnClick(EventConstants.NormalClick.HOTEL_PAY_SURE)
                    public void onClick(View v) {
                        dialog.dismiss();

                        if (!NetworkUtils.isConnected(BookHotelThreeActivity.this)) {
                            showToastException(R.string.net_work_error);
                            return;
                        }
                        hotelRoomVM.cancelOrder(Long.valueOf(orderId));
                    }
                })
                .setNegativeButton(getString(R.string.travel_right_btn), new View.OnClickListener() {
                    @NormalOnClick(EventConstants.NormalClick.HOTEL_PAY_CANCE)
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                })
                .show();

//        View view = View.inflate(this, R.layout.dialog_travel, null);
//        TextView dialogTitle = view.findViewById(R.id.dialog_title);
//        TextView dialogMessage = view.findViewById(R.id.dialog_message);
//        TextView sureBtn = view.findViewById(R.id.btn_sure);
//        TextView cancelBtn = view.findViewById(R.id.btn_cancel);
//        dialogTitle.setText(getString(R.string.travel_tip_str));
//
//        SpannableString s1 = new SpannableString(getString(R.string.travel_booking_three_cancel_message));
//        s1.setSpan(new AbsoluteSizeSpan(32, true), 0, s1.length() - 17, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//        s1.setSpan(new AbsoluteSizeSpan(28, true), s1.length() - 17, s1.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//        dialogMessage.setText(s1);
//        sureBtn.setText(getString(R.string.travel_left_btn_cancel_book));
//        cancelBtn.setText(getString(R.string.travel_right_btn));
//        cancelDialog = new XmDialog.Builder(this)
//                .setView(view)
//                .setWidth(this.getResources().getDimensionPixelOffset(R.dimen.dialog_travel_width))
//                .setHeight(this.getResources().getDimensionPixelOffset(R.dimen.dialog_travel_height_350))
//                .create();
//        sureBtn.setOnClickListener(new View.OnClickListener() {
//            @NormalOnClick(EventConstants.NormalClick.HOTEL_PAY_SURE)
//            public void onClick(View v) {
//                cancelDialog.dismiss();
//
//                if (!NetworkUtils.isConnected(BookHotelThreeActivity.this)) {
//                    showToastException(R.string.net_work_error);
//                    return;
//                }
//                hotelRoomVM.cancelOrder(Long.valueOf(orderId));
//            }
//        });
//        cancelBtn.setOnClickListener(new View.OnClickListener() {
//            @NormalOnClick(EventConstants.NormalClick.HOTEL_PAY_CANCE)
//            public void onClick(View v) {
//                cancelDialog.dismiss();
//            }
//        });
//        cancelDialog.show();
    }


    private void showBackDialog() {
        ConfirmDialog dialog = new ConfirmDialog(this);
        dialog.setContent(getString(R.string.travel_booking_three_message))
                .setPositiveButton(getString(R.string.travel_left_btn_back), new View.OnClickListener() {
                    @NormalOnClick(EventConstants.NormalClick.HOTEL_PAY_BACK_SURE)
                    public void onClick(View v) {
                        //检测到订单未支付跳转到个人中心我的订单页面
                        dialog.dismiss();
                        LaunchUtils.launchApp(BookHotelThreeActivity.this, LauncherConstants.LauncherApp.LAUNCHER_PERSONAL_PACKAGE, LauncherConstants.LauncherApp.LAUNCHER_PERSONAL_CLASS, null, true);
                        finish();

                    }
                })
                .setNegativeButton(getString(R.string.travel_right_btn), new View.OnClickListener() {
                    @NormalOnClick(EventConstants.NormalClick.HOTEL_PAY_BACK_CANCE)
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                })
                .show();

//        View view = View.inflate(this, R.layout.dialog_travel, null);
//        TextView dialogTitle = view.findViewById(R.id.dialog_title);
//        TextView dialogMessage = view.findViewById(R.id.dialog_message);
//        TextView sureBtn = view.findViewById(R.id.btn_sure);
//        TextView cancelBtn = view.findViewById(R.id.btn_cancel);
//        dialogTitle.setText(getString(R.string.travel_tip_str));
//        dialogMessage.setText(getString(R.string.travel_booking_three_message));
//        sureBtn.setText(getString(R.string.travel_left_btn_back));
//        cancelBtn.setText(getString(R.string.travel_right_btn));
//        backDialog = new XmDialog.Builder(this)
//                .setView(view)
//                .setWidth(this.getResources().getDimensionPixelOffset(R.dimen.dialog_travel_width))
//                .setHeight(this.getResources().getDimensionPixelOffset(R.dimen.dialog_travel_height_320))
//                .create();
//        sureBtn.setOnClickListener(new View.OnClickListener() {
//            @NormalOnClick(EventConstants.NormalClick.HOTEL_PAY_BACK_SURE)
//            public void onClick(View v) {
//                //检测到订单未支付跳转到个人中心我的订单页面
//                backDialog.dismiss();
//                LaunchUtils.launchApp(BookHotelThreeActivity.this, LauncherConstants.LauncherApp.LAUNCHER_PERSONAL_PACKAGE, LauncherConstants.LauncherApp.LAUNCHER_PERSONAL_CLASS,null,true);
//                finish();
//
//            }
//        });
//        cancelBtn.setOnClickListener(new View.OnClickListener() {
//            @NormalOnClick(EventConstants.NormalClick.HOTEL_PAY_BACK_CANCE)
//            public void onClick(View v) {
//                backDialog.dismiss();
//            }
//        });
//        backDialog.show();
    }

    private void dismissBackDialog() {
        if (backDialog != null && backDialog.getDialog() != null && backDialog.getDialog().isShowing()) {
            backDialog.dismiss();
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
        }
        ThreadDispatcher.getDispatcher().remove(pollingRunnable);
    }

    private static boolean isFailed(int code) {
        if (code == PAY_REFUND_CODE || code == PAY_CLOSED_CODE || code == PAY_ABNORMAL_CODE) {
            return true;
        }

        return false;
    }
}

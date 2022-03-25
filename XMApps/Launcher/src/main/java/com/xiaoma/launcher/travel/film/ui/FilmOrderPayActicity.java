package com.xiaoma.launcher.travel.film.ui;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.xiaoma.autotracker.XmAutoTracker;
import com.xiaoma.autotracker.XmTracker;
import com.xiaoma.autotracker.model.TrackerCountType;
import com.xiaoma.component.base.BaseActivity;
import com.xiaoma.image.ImageLoader;
import com.xiaoma.launcher.R;
import com.xiaoma.launcher.common.constant.EventConstants;
import com.xiaoma.launcher.common.constant.LauncherConstants;
import com.xiaoma.launcher.common.model.TrackFilmModel;
import com.xiaoma.launcher.travel.film.time.OnTimerCallback;
import com.xiaoma.launcher.travel.film.time.OrderTimer;
import com.xiaoma.launcher.travel.film.vm.FilmVM;
import com.xiaoma.model.XmResource;
import com.xiaoma.model.annotation.NormalOnClick;
import com.xiaoma.model.annotation.PageDescComponent;
import com.xiaoma.model.annotation.ResId;
import com.xiaoma.trip.movie.request.CinemaJsonBean;
import com.xiaoma.trip.movie.response.LockSeatResponseBean;
import com.xiaoma.trip.movie.response.OrderDetailBean;
import com.xiaoma.ui.dialog.ConfirmDialog;
import com.xiaoma.ui.toast.XMToast;
import com.xiaoma.utils.GsonHelper;
import com.xiaoma.utils.LaunchUtils;
import com.xiaoma.utils.NetworkUtils;
import com.xiaoma.utils.StringUtil;
import com.xiaoma.utils.tputils.TPUtils;

import java.util.Locale;

@PageDescComponent(EventConstants.PageDescribe.FilmOrderPayActicityPagePathDesc)
public class FilmOrderPayActicity extends BaseActivity implements View.OnClickListener {
    private static final String NEW_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private static final String NEW__MS_FORMAT = "mm:ss";
    private static final int PAY_SUCCESS = 3;
    private boolean INSPECTION_PAY_TYPE = true;
    private TextView mAlreadyPay;
    private TextView mFilmName;
    private TextView mFilmType;
    private TextView mFilmUnitPrice;
    private TextView mCinemaName;
    private TextView mCinemaSeat;
    private TextView mTicketNumber;
    private TextView mFilmTime;
    private TextView mUserPhone;
    private ImageView mPayQrCode;
    private TextView mPaySum;
    private FilmVM mFilmVM;
    private TextView mCountDown;
    private TextView mTwoImg;
    private TextView mThreeImg;
    private TextView mConfirmPay;
    private LockSeatResponseBean mLockSeatResponseBean;
    private TextView tapOneText;
    private OrderDetailBean mOrderDetailBean;
    public static final String DAY_TEXT = "日 ";
    public static final String MONTH_TEXT = "月";
    private boolean DELAY_PAY_TYPE = true;
    private CinemaJsonBean mCinemaJsonBean;

    private boolean startTimer = false;
    private int totalInterval;
    private int currentPayStatus = -1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.film_order_pay);
        bindview();
        initView();
        initData();
        initVM();
    }

    private void bindview() {
        mAlreadyPay = findViewById(R.id.already_pay);
        tapOneText = findViewById(R.id.tap_one_text);
        mFilmName = findViewById(R.id.film_name);
        mFilmType = findViewById(R.id.film_type);
        mFilmUnitPrice = findViewById(R.id.film_unit_price);
        mCinemaName = findViewById(R.id.cinema_name);
        mCinemaSeat = findViewById(R.id.cinema_seat);
        mTicketNumber = findViewById(R.id.ticket_number);
        mFilmTime = findViewById(R.id.film_time);
        mUserPhone = findViewById(R.id.user_phone);
        mPayQrCode = findViewById(R.id.pay_qr_code);
        mPaySum = findViewById(R.id.pay_sum);
        mCountDown = findViewById(R.id.pay_countdown);

        //tap相关
        mTwoImg = findViewById(R.id.tap_two_img);
        mThreeImg = findViewById(R.id.tap_three_img);
        mConfirmPay = findViewById(R.id.tap_three_confirm_pay);
    }


    private void initView() {
        mTwoImg.setBackgroundResource(R.drawable.round_back_yellow);
        mThreeImg.setBackgroundResource(R.drawable.round_back_yellow);
        mConfirmPay.setText(R.string.scan_code_pay);
        mAlreadyPay.setOnClickListener(this);
    }

    private void initData() {
        tapOneText.setText(TPUtils.get(this, LauncherConstants.PATH_TYPE, getString(R.string.select_cinema)));
        mLockSeatResponseBean = (LockSeatResponseBean) getIntent().getSerializableExtra(LauncherConstants.ActionExtras.LOCK_SEAT_RESPONSE_BEAN);
        if (mLockSeatResponseBean != null) {
            float price = Float.parseFloat(mLockSeatResponseBean.getPrice()) / mLockSeatResponseBean.getTickets().size();
            mFilmUnitPrice.setText(String.format(getString(R.string.price_people), StringUtil.keep2Decimal(price)));
            mTicketNumber.setText(mLockSeatResponseBean.getTickets().size() + getString(R.string.ticket_sheet));
            mPaySum.setText(String.format(getString(R.string.price), StringUtil.keep2Decimal(Float.parseFloat(mLockSeatResponseBean.getPrice()))));
            ImageLoader.with(FilmOrderPayActicity.this).load(mLockSeatResponseBean.getQrCode()).into(mPayQrCode);
            setSeatList();
        }
    }

    private void initVM() {
        mFilmVM = ViewModelProviders.of(this).get(FilmVM.class);
        mFilmVM.getOrderDeatil().observe(this, new Observer<XmResource<OrderDetailBean>>() {
            @Override
            public void onChanged(@Nullable XmResource<OrderDetailBean> listXmResource) {
                if (listXmResource == null) {
                    return;
                }

                listXmResource.handle(new OnCallback<OrderDetailBean>() {
                    @Override
                    public void onSuccess(OrderDetailBean data) {
                        if (data != null) {
                            if (INSPECTION_PAY_TYPE) {
                                INSPECTION_PAY_TYPE = false;
                                mOrderDetailBean = data;
                                mCinemaJsonBean = GsonHelper.fromJson(mOrderDetailBean.getCinemaJson(), CinemaJsonBean.class);
                                setView(data);
                                XMToast.showToast(FilmOrderPayActicity.this, R.string.order_generate);
                                timerLoop(data);
                            } else {
                                if (DELAY_PAY_TYPE) {
                                    if (data.getOrderStatusId() == PAY_SUCCESS) {
                                        orderPaySuccess(data);
                                    }
                                } else {
                                    if (data.getOrderStatusId() == PAY_SUCCESS) {
                                        orderPaySuccess(data);
                                    } else {
                                        DELAY_PAY_TYPE = true;
                                        XMToast.showToast(FilmOrderPayActicity.this, R.string.rescan_to_pay);
                                    }
                                }
                            }
                        }
                    }

                    @Override
                    public void onFailure(String msg) {
                        super.onFailure(msg);

                    }
                });
            }
        });
        if (mLockSeatResponseBean != null) {
            mFilmVM.queryOrderDeatil(mLockSeatResponseBean.getOrderNo());
        }
    }

    private void orderPaySuccess(OrderDetailBean data) {
        OrderTimer.endTimer();
        Intent intent = new Intent(FilmOrderPayActicity.this, FilmOrderSuccessActicity.class);
        intent.putExtra(LauncherConstants.ActionExtras.ORDER_DETAIL_BEAN, data);
        startActivity(intent);
        // 上报电影预订成功和推荐电影预订成功次数
        XmTracker.getInstance().uploadEvent(-1,
                TrackerCountType.BOOKMOVIE.getType());
        if (FilmActivity.getmIsRecommend()) {
            XmTracker.getInstance().uploadEvent(-1,
                    TrackerCountType.BOOKRECOMMANDMOVIE.getType());
        }
    }

    private void setView(OrderDetailBean data) {
        mFilmName.setText(String.format(getString(R.string.film_name), data.getOrderName()));
        mFilmType.setText(mCinemaJsonBean.getFilmType());
        mCinemaName.setText(mCinemaJsonBean.getCinemaName());
        mUserPhone.setText(data.getBookPhone());
        StringBuffer showTime = new StringBuffer(mOrderDetailBean.getOrderDate());
        showTime.insert(13, ":");
        showTime.insert(10, DAY_TEXT);
        showTime.replace(7, 8, MONTH_TEXT);
        showTime.replace(0, 5, "");
        mFilmTime.setText(showTime.toString());
        TrackFilmModel model = new TrackFilmModel(mCinemaSeat.getText().toString(),
                mFilmName.getText().toString(), mPaySum.getText().toString(), mFilmTime.getText().toString());
        XmAutoTracker.getInstance().onEvent(EventConstants.NormalClick.BOOK_MOVIE, GsonHelper.toJson(model),
                "FilmOrderPayActicity", EventConstants.PageDescribe.FilmOrderPayActicityPagePathDesc);
    }


    /**
     * 设置已选座位列表
     */
    private void setSeatList() {
        StringBuffer seatList = new StringBuffer();
        float price = Float.parseFloat(mLockSeatResponseBean.getPrice()) / mLockSeatResponseBean.getTickets().size();
        for (LockSeatResponseBean.TicketsBean item : mLockSeatResponseBean.getTickets()) {
            seatList.append(String.format(getString(R.string.row_col_price), item.getSeatRow(), item.getSeatCol(), StringUtil.keep2Decimal(price)));
        }
        mCinemaSeat.setText(seatList.toString());
    }

    @NormalOnClick({EventConstants.NormalClick.FILM_ORDER_PAY_SURE})//按钮对应的名称
    @ResId({R.id.already_pay})//按钮对应的R文件id
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.already_pay:

                if (!NetworkUtils.isConnected(this)) {
                    showToastException(R.string.net_work_error);
                    return;
                }

                if (mLockSeatResponseBean != null) {
                    DELAY_PAY_TYPE = false;
                    mFilmVM.queryOrderDeatil(mLockSeatResponseBean.getOrderNo());
                }
                break;
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            startTimer = false;
            OrderTimer.endTimer();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                // break;
                showBackDialog();
                return false;//拦截事件
            default:
                break;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 电话dialog
     */
    private void showBackDialog() {
        ConfirmDialog dialog = new ConfirmDialog(this);
        dialog.setContent(getString(R.string.travel_booking_three_message))
                .setPositiveButton(getString(R.string.surce_goaway), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                XMToast.showToast(FilmOrderPayActicity.this, "订单保存至个人中心，您可前往支付", getDrawable(R.drawable.toast_error));
                        dialog.dismiss();
                        //检测到订单未支付跳转到个人中心我的订单页面
                        LaunchUtils.launchApp(FilmOrderPayActicity.this, "com.xiaoma.personal", "com.xiaoma.personal.order.ui.MineOrderActivity");
                        finish();
                    }
                })
                .setNegativeButton(getString(R.string.forget_it), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    //---------------------------------------------------------------------------------

    /**
     * 倒计时相关
     *
     * @param orderDetailInfo
     */
    private void timerLoop(final OrderDetailBean orderDetailInfo) {
        OrderTimer.startTimer(1000, new OnTimerCallback() {
            @Override
            public void onTimer() {
                calculationTime(orderDetailInfo);
            }
        });
    }

    private void calculationTime(OrderDetailBean data) {
        if (!startTimer) {
            totalInterval = OrderTimer.calculationInterval(data.getCreateDate(), data.getLastpayDate());
        }

        //订单保持待支付状态
        if (totalInterval > 0) {
            startTimer = true;
            --totalInterval;
            int min = totalInterval / 60;
            int seconds = totalInterval % 60;
            String curMin = String.format(Locale.CHINA, "%02d", min);
            String curSeconds = String.format(Locale.CHINA, "%02d", seconds);
            mCountDown.setText(getResources().getString(R.string.pay_timer_interval, curMin, curSeconds));

            if (totalInterval % 5 == 0) {
                mFilmVM.queryOrderDeatil(mLockSeatResponseBean.getOrderNo());
            }
        } else {
            startTimer = false;
            currentPayStatus = -1;
            if (mOrderDetailBean != null) {
                OrderTimer.endTimer();
                Intent intent = new Intent(FilmOrderPayActicity.this, FilmOrderCancelActicity.class);
                intent.putExtra(LauncherConstants.ActionExtras.ORDER_DETAIL_BEAN, mOrderDetailBean);
                startActivity(intent);
                finish();
            }
        }
    }
    //---------------------------------------------------------------------------------

}

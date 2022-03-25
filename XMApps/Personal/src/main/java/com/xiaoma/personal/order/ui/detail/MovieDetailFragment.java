package com.xiaoma.personal.order.ui.detail;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.mapbar.xiaoma.manager.XmMapNaviManager;
import com.xiaoma.component.base.BaseFragment;
import com.xiaoma.image.ImageLoader;
import com.xiaoma.model.XmResource;
import com.xiaoma.model.annotation.NormalOnClick;
import com.xiaoma.model.annotation.PageDescComponent;
import com.xiaoma.model.annotation.ResId;
import com.xiaoma.personal.R;
import com.xiaoma.personal.common.util.EventConstants;
import com.xiaoma.personal.order.constants.CallPhoneUtils;
import com.xiaoma.personal.order.constants.Constants;
import com.xiaoma.personal.order.constants.OrderStatusId;
import com.xiaoma.personal.order.constants.OrderStatusMeta;
import com.xiaoma.personal.order.constants.timer.OnTimerCallback;
import com.xiaoma.personal.order.constants.timer.OrderTimer;
import com.xiaoma.personal.order.model.OrderInfo;
import com.xiaoma.personal.order.vm.OrderVM;
import com.xiaoma.ui.progress.loading.XMProgress;
import com.xiaoma.ui.toast.XMToast;
import com.xiaoma.utils.LaunchUtils;
import com.xiaoma.utils.NetworkUtils;
import com.xiaoma.utils.log.KLog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/**
 * <pre>
 *       @author Created by Gillben
 *       date: 2019/3/13 0013 16:26
 *       desc：电影详情
 * </pre>
 */
@PageDescComponent(EventConstants.PageDescribe.movieDetailFragment)
public class MovieDetailFragment extends BaseFragment implements View.OnClickListener {

    private static final String TAG = MovieDetailFragment.class.getSimpleName();
    private ConstraintLayout rootLayout;
    private ImageView orderStatusImage;
    private TextView orderStatusDesc;
    //电影相关信息
    private TextView mMovieName;
    private TextView mMovieType;
    private TextView mMoviePrice;
    private TextView mCinema;
    private TextView mCinemaSeat;

    //user相关信息
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

    //验证码、序列号加载区
    private ConstraintLayout attachSerialNumberLayout;
    private LinearLayout firstSerialLayout;
    private TextView firstSerialTitle;
    private TextView firstSerial;
    private LinearLayout thirdSerialLayout;
    private TextView thirdSerialTitle;
    private TextView thirdSerial;
//    private LinearLayout secondSerialLayout;
//    private TextView secondSerialTitle;
//    private TextView secondSerial;

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
    private int currentPayStatus = -1;
    private OrderInfo.Order mOrderDetailInfo;
    private boolean refreshFlag;

    public static MovieDetailFragment newInstance(long id) {
        Bundle args = new Bundle();
        args.putLong(Constants.MOVIE_ORDER_ID, id);
        MovieDetailFragment fragment = new MovieDetailFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_movie_detail, container, false);
        return super.onCreateWrapView(view);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);

        Bundle bundle = getArguments();
        assert bundle != null;
        orderId = bundle.getLong(Constants.MOVIE_ORDER_ID);

        if (NetworkUtils.isConnected(mContext)) {
            initData();
        } else {
            showNoNetView();
        }
    }

    private void initView(View view) {
        rootLayout = view.findViewById(R.id.movie_detail_root);
        rootLayout.setVisibility(View.GONE);
        orderStatusImage = view.findViewById(R.id.iv_order_status_icon);
        orderStatusDesc = view.findViewById(R.id.tv_order_status_desc);
        mMovieName = view.findViewById(R.id.tv_movie_name);
        mMovieType = view.findViewById(R.id.tv_movie_type);
        mMoviePrice = view.findViewById(R.id.tv_order_content_price);
        mCinema = view.findViewById(R.id.tv_order_content_author);
        mCinemaSeat = view.findViewById(R.id.movie_seat_text);
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

        attachSerialNumberLayout = view.findViewById(R.id.attach_movie_serial_number_layout);
        firstSerialLayout = view.findViewById(R.id.ll_ticket_code_first);
        firstSerialTitle = view.findViewById(R.id.ticket_code_first_title);
        firstSerial = view.findViewById(R.id.ticket_code_first);

//        secondSerialLayout = view.findViewById(R.id.ll_ticket_code_second);
//        secondSerialTitle = view.findViewById(R.id.ticket_code_second_title);
//        secondSerial = view.findViewById(R.id.ticket_code_second);

        thirdSerialLayout = view.findViewById(R.id.ll_ticket_code_third);
        thirdSerialTitle = view.findViewById(R.id.ticket_code_third_title);
        thirdSerial = view.findViewById(R.id.ticket_code_third);

        mNavButton.setOnClickListener(this);
        mPhoneButton.setOnClickListener(this);
        mPredestineButton.setOnClickListener(this);
        mPayLinear.setOnClickListener(this);
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
                //TODO 处理支付计时
                XMProgress.showProgressDialog(this, getResources().getString(R.string.progress_loading));
                refreshFlag = true;
                fetchData();
                break;

            case R.id.bt_paid_navigation:
                //TODO 导航
                OrderInfo.Order.Film film = mOrderDetailInfo.getCinemaJsonVo();
                if (film != null) {
                    double lon = Double.parseDouble(film.getLon());
                    double lat = Double.parseDouble(film.getLat());
                    XmMapNaviManager.getInstance().startNaviToPoi(film.getCinemaName(), film.getAddress(), lon, lat);
                } else {
                    KLog.d(TAG, "Film instance is null.");
                }
                break;

            case R.id.bt_paid_phone:
                //TODO 蓝牙电话
                CallPhoneUtils.callBluetoothPhone(getActivity(), mOrderDetailInfo.getCinemaJsonVo().getMobile(), response -> KLog.w("电话拨打失败"));
                break;

            case R.id.bt_paid_predestine:
                //TODO 重新预定
                if (mOrderDetailInfo == null || mOrderDetailInfo.getCinemaJsonVo() == null) {
                    KLog.w("OrderDetailInfo or CinemaJsonVo is null.");
                    return;
                }
                Bundle bundle = new Bundle();
                bundle.putString(Constants.MOVIE_TAG, mOrderDetailInfo.getCinemaJsonVo().getId());
                boolean result = LaunchUtils.launchAppWithData(mContext, Constants.LAUNCHER_PKG, Constants.RENEW_PREDESTINE_MOVIE, bundle);
                KLog.w("result: " + result);
                if (result) {
                    Objects.requireNonNull(getActivity()).finish();
                }
                break;
        }
    }


    @Override
    protected void noNetworkOnRetry() {
        super.noNetworkOnRetry();
        initData();
    }

    private void initData() {
        orderVM = ViewModelProviders.of(this).get(OrderVM.class);
        XMProgress.showProgressDialog(this, getResources().getString(R.string.progress_loading));
        fetchData();
    }


    private void fetchData() {
        orderVM.getOrderDetailInfo(orderId).observe(this, new Observer<XmResource<OrderInfo.Order>>() {
            @Override
            public void onChanged(@Nullable XmResource<OrderInfo.Order> orderDetailInfo) {
                assert orderDetailInfo != null;
                orderDetailInfo.handle(new OnCallback<OrderInfo.Order>() {
                    @Override
                    public void onSuccess(OrderInfo.Order data) {
                        XMProgress.dismissProgressDialog(MovieDetailFragment.this);
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
                            XMProgress.dismissProgressDialog(MovieDetailFragment.this);
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
        mMovieName.setText(data.getOrderName());
        mMovieType.setText(data.getCinemaJsonVo().getFilmType());
        mCinema.setText(data.getCinemaJsonVo().getCinemaName());
        String unit = calculationUnitPrice(data);
        mMoviePrice.setText(getResources().getString(R.string.movie_ticket_unit_price, unit));
        mCinemaSeat.setText(convertCinemaSeat(unit, data));

        switch (data.getOrderStatusId()) {
            case OrderStatusId.WAIT_PAY:
                orderWaitPay(data);
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
        attachSerialNumberLayout.setVisibility(View.GONE);
        mPayLinear.setVisibility(View.VISIBLE);
        mPaidLayout.setVisibility(View.GONE);

        mFirstInfoTitle.setText(R.string.ticket_number);
        mFirstInfo.setText(getResources().getString(R.string.movie_ticket_number, data.getTicketNum()));
        mSecondInfoTitle.setText(R.string.watch_movie_time);
        mSecondInfo.setText(convertDate(data.getOrderDate()));
        mThirdInfoTitle.setText(R.string.phone_certificate);
        mThirdInfo.setText(data.getBookPhone());

        Glide.with(this).load(data.getPayQrcode()).into(mQRCodeImage);
        String retainOneDecimal = String.format(Locale.CHINA, "%.2f", Float.parseFloat(data.getAmount()));
        mCodePreviewAmount.setText(getResources().getString(R.string.preview_amount, retainOneDecimal));

        //开启订单计时
        timerLoop(data);
    }


    private void orderComplete(OrderInfo.Order data) {
        scanCodeLayout.setVisibility(View.GONE);
        attachSerialNumberLayout.setVisibility(View.VISIBLE);
        mPayLinear.setVisibility(View.GONE);
        mPaidLayout.setVisibility(View.VISIBLE);
        mPredestineButton.setVisibility(View.GONE);

        ImageLoader.with(this).load(R.drawable.predestine_success).into(orderStatusImage);
        orderStatusDesc.setText(getResources().getString(R.string.order_status_film_pay_success));
        mFirstInfoTitle.setText(R.string.phone_certificate);
        mFirstInfo.setText(data.getBookPhone());
        mSecondInfoTitle.setText(R.string.total_amount);
        String retainOneDecimal = String.format(Locale.CHINA, "%.2f", Float.parseFloat(data.getAmount()));
        mSecondInfo.setText(retainOneDecimal);
        mThirdInfoTitle.setText(R.string.watch_movie_time);
        mThirdInfo.setText(convertDate(data.getOrderDate()));

        handleTicketVoucher(data);
        dynamicAdjustMarginValue(OrderStatusMeta.COMPLETED);
        OrderTimer.endTimer();
    }


    private void orderClosed(OrderInfo.Order data) {
        scanCodeLayout.setVisibility(View.GONE);
        mPayLinear.setVisibility(View.GONE);
        mPaidLayout.setVisibility(View.VISIBLE);
        mPredestineButton.setVisibility(View.VISIBLE);
        mPredestineButton.setText(getResources().getString(R.string.re_predestine));

        orderStatusDesc.setText(getResources().getString(R.string.order_status_expired));
        mFirstInfoTitle.setText(R.string.phone_certificate);
        mFirstInfo.setText(data.getBookPhone());
        mSecondInfoTitle.setText(R.string.total_amount);
        String retainOneDecimal = String.format(Locale.CHINA, "%.2f", Float.parseFloat(data.getAmount()));
        mSecondInfo.setText(retainOneDecimal);
        mThirdInfoTitle.setText(R.string.watch_movie_time);
        mThirdInfo.setText(convertDate(data.getOrderDate()));

        dynamicAdjustMarginValue(OrderStatusMeta.CLOSED);
        OrderTimer.endTimer();
    }

    private void handleTicketVoucher(OrderInfo.Order data) {
        //TODO 处理票据序列码
        String voucher = data.getVoucherValue();
        if (!TextUtils.isEmpty(voucher)) {
            String[] voucherArray;
            if (voucher.contains("|")) {
                voucherArray = voucher.split("\\|");
            } else {
                voucherArray = new String[]{voucher};
            }

            if (voucherArray.length == 1) {
                firstSerialLayout.setVisibility(View.GONE);

                int splitIndex = findFirstIndex(voucherArray[0]);
                thirdSerialTitle.setText(voucherArray[0].substring(0, splitIndex));
                thirdSerial.setText(voucherArray[0].substring(splitIndex + 1));
            } else if (voucherArray.length == 2) {
                firstSerialLayout.setVisibility(View.VISIBLE);

                int splitIndexFirst = findFirstIndex(voucherArray[0]);
                firstSerialTitle.setText(voucherArray[0].substring(0, splitIndexFirst));
                firstSerial.setText(voucherArray[0].substring(splitIndexFirst + 1));

                int splitIndexSecond = findFirstIndex(voucherArray[1]);
                thirdSerialTitle.setText(voucherArray[1].substring(0, splitIndexSecond));
                thirdSerial.setText(voucherArray[1].substring(splitIndexSecond + 1));
            }
//            else if (voucherArray.length == 3) {
//                int splitIndexFirst = findFirstIndex(voucherArray[0]);
//                firstSerialTitle.setText(voucherArray[0].substring(0, splitIndexFirst));
//                firstSerial.setText(voucherArray[0].substring(splitIndexFirst + 1));
//
//                int splitIndexSecond = findFirstIndex(voucherArray[1]);
//                secondSerialTitle.setText(voucherArray[1].substring(0, splitIndexSecond));
//                secondSerial.setText(voucherArray[1].substring(splitIndexSecond + 1));
//
//                int splitIndexThird = findFirstIndex(voucherArray[2]);
//                thirdSerialTitle.setText(voucherArray[2].substring(0, splitIndexThird));
//                thirdSerialTitle.setText(voucherArray[2].substring(splitIndexThird + 1));
//            }
        }
    }


    private int findFirstIndex(String content) {
        return !TextUtils.isEmpty(content) ? content.indexOf(":") : 0;
    }


    private void dynamicAdjustMarginValue(@OrderStatusMeta int status) {
        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) mNavButton.getLayoutParams();
        if (status == OrderStatusMeta.COMPLETED) {
            mPredestineButton.setVisibility(View.GONE);
            params.setMarginStart(getResources().getDimensionPixelSize(R.dimen.size_order_detail_operation_button_left_long));
        } else {
            params.setMarginStart(0);
        }
        mNavButton.setLayoutParams(params);
    }


    private String convertDate(String date) {
        StringBuilder builder = new StringBuilder();
        String[] splitDate = date.split("-");
        String month = splitDate[1];
        String day = splitDate[2].substring(0, 2);
        String temp = mContext.getResources().getString(R.string.item_Film_date, month, day);
        builder.append(temp);
        builder.append(" ");

        String time = date.substring(date.length() - 4);
        String hour = time.substring(0, 2);
        builder.append(hour);
        builder.append(":");
        String min = time.substring(2, 4);
        builder.append(min);
        Log.d(MovieDetailFragment.class.getSimpleName(),
                "hour: " + hour + "   min: " + min);
        return builder.toString();
    }

    private String calculationUnitPrice(OrderInfo.Order orderDetailInfo) {
        float amount = Float.parseFloat(orderDetailInfo.getAmount());
        int ticket = Integer.parseInt(orderDetailInfo.getTicketNum());
        return String.format(Locale.CHINA, "%.2f", amount / ticket);
    }


    private String convertCinemaSeat(String unit, OrderInfo.Order orderDetailInfo) {
        StringBuilder builder = new StringBuilder();

        String seat = orderDetailInfo.getCinemaJsonVo().getSeat();
        if (!TextUtils.isEmpty(seat)) {
            List<String> seatList = new ArrayList<>();
            if (seat.contains("|")) {
                seatList = Arrays.asList(seat.split("\\|"));
            } else {
                seatList.add(seat);
            }

            for (int i = 0; i < seatList.size(); i++) {
                String temp = seatList.get(i);
                int splitIndex = temp.indexOf(":");
                String number1 = temp.substring(0, splitIndex);
                String number2 = temp.substring(splitIndex + 1);
                builder.append(number1);
                builder.append(mContext.getString(R.string.unit_row));
                builder.append(number2);
                builder.append(mContext.getString(R.string.unit_seat));
                builder.append("  ");
                builder.append(unit);
                builder.append(mContext.getString(R.string.unit_yuan));

                if (i != seatList.size() - 1) {
                    builder.append("    ");
                }
            }
        }
        return builder.toString();
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
            orderClosed(orderDetailInfo);
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        startTimer = false;
        OrderTimer.endTimer();
    }
}

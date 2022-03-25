package com.xiaoma.launcher.travel.film.ui;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xiaoma.component.base.BaseActivity;
import com.xiaoma.launcher.R;
import com.xiaoma.launcher.common.constant.EventConstants;
import com.xiaoma.launcher.common.constant.LauncherConstants;
import com.xiaoma.launcher.common.manager.LauncherBlueToothPhoneManager;
import com.xiaoma.launcher.travel.film.vm.FilmVM;
import com.xiaoma.model.XmResource;
import com.xiaoma.model.annotation.NormalOnClick;
import com.xiaoma.model.annotation.PageDescComponent;
import com.xiaoma.model.annotation.ResId;
import com.xiaoma.thread.ThreadDispatcher;
import com.xiaoma.trip.movie.request.CinemaJsonBean;
import com.xiaoma.trip.movie.response.ConfirmOrderBean;
import com.xiaoma.trip.movie.response.OrderDetailBean;
import com.xiaoma.ui.toast.XMToast;
import com.xiaoma.utils.ConvertUtils;
import com.xiaoma.utils.GsonHelper;
import com.xiaoma.utils.StringUtil;
@PageDescComponent(EventConstants.PageDescribe.FilmOrderSuccessActicityPagePathDesc)
public class FilmOrderSuccessActicity extends BaseActivity implements View.OnClickListener {

    private Button mNavigation;
    private Button mFilmPhone;
    private FilmVM mFilmVM;
    private Runnable checkVoucherValue = new Runnable() {
        @Override
        public void run() {
            queryVoucherValue();
        }
    };
    private long CHECK_INTERVAL = 6000;
    private OrderDetailBean mOrderDetailBean;
    private TextView mFilmName;
    private TextView mFilmType;
    private TextView mFilmUnitPrice;
    private TextView mCinemaName;
    private TextView mCinemaSeat;
    private TextView mUserPhone;
    private TextView mTicketTotalNumber;
    private TextView mFilmTime;
    public static final String DAY_TEXT = "日 ";
    public static final String MONTH_TEXT = "月";
    private LinearLayout mLlTicketFirst;
    private LinearLayout mLlTicketSecond;
    private LinearLayout mLlTicketThird;
    private TextView mTicketFirstTitle;
    private TextView mTicketFirst;
    private TextView mTicketSecondTitle;
    private TextView mTicketSecond;
    private TextView mTicketThirdTitle;
    private TextView mTicketThird;
    private LinearLayout mLoadingTicketCode;
    private RelativeLayout mMovieCodeNumberLayout;
    private CinemaJsonBean mCinemaJsonBean;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.film_order_success);
        bindview();
        initView();
        initData();
    }

    private void bindview() {
        mFilmName = findViewById(R.id.film_name);
        mFilmType = findViewById(R.id.film_type);
        mFilmUnitPrice = findViewById(R.id.film_unit_price);
        mCinemaName = findViewById(R.id.cinema_name);
        mCinemaSeat = findViewById(R.id.cinema_seat);
        mUserPhone = findViewById(R.id.user_phone);
        mTicketTotalNumber = findViewById(R.id.ticket_total_number);
        mFilmTime = findViewById(R.id.film_time);
        mNavigation = findViewById(R.id.film_navigation);
        mFilmPhone = findViewById(R.id.film_phone);

        mLoadingTicketCode = findViewById(R.id.loading_ticket_code);
        mMovieCodeNumberLayout = findViewById(R.id.movie_code_number_layout);
        mLlTicketFirst = findViewById(R.id.ll_ticket_first);
        mLlTicketSecond = findViewById(R.id.ll_ticket_second);
        mLlTicketThird = findViewById(R.id.ll_ticket_third);

        mTicketFirstTitle = findViewById(R.id.ticket_first_title);
        mTicketFirst = findViewById(R.id.ticket_first);
        mTicketSecondTitle = findViewById(R.id.ticket_second_title);
        mTicketSecond = findViewById(R.id.ticket_second);
        mTicketThirdTitle = findViewById(R.id.ticket_third_title);
        mTicketThird = findViewById(R.id.ticket_third);
    }

    private void initView() {
        mOrderDetailBean = (OrderDetailBean) getIntent().getSerializableExtra(LauncherConstants.ActionExtras.ORDER_DETAIL_BEAN);
        mCinemaJsonBean = GsonHelper.fromJson(mOrderDetailBean.getCinemaJson(), CinemaJsonBean.class);
        if (mOrderDetailBean != null) {
            mFilmName.setText(String.format(getString(R.string.film_name), mOrderDetailBean.getOrderName()));
            mFilmType.setText(mCinemaJsonBean.getFilmType());

            float price = Float.parseFloat(mOrderDetailBean.getAmount()) / ConvertUtils.stringToInt(mOrderDetailBean.getTicketNum());
            mFilmUnitPrice.setText(String.format(getString(R.string.price_people), StringUtil.keep2Decimal(price)));
            mCinemaName.setText(mCinemaJsonBean.getCinemaName());
            mUserPhone.setText(mOrderDetailBean.getBookPhone());
            mTicketTotalNumber.setText(mOrderDetailBean.getTicketNum());
            setFilmTime();
            setSeatList(price);
            mNavigation.setOnClickListener(this);
            mFilmPhone.setOnClickListener(this);
        }
    }

    /**
     * 设置观影时间
     */
    private void setFilmTime() {
        StringBuffer showTime = new StringBuffer(mOrderDetailBean.getOrderDate());
        showTime.insert(12, ":");
        showTime.insert(10, DAY_TEXT);
        showTime.replace(7, 8, MONTH_TEXT);
        showTime.replace(0, 5, "");
        mFilmTime.setText(showTime.toString());
    }

    /**
     * 设置已选座位列表
     */
    private void setSeatList(float price) {
        StringBuffer seatList = new StringBuffer();
        String seat = mCinemaJsonBean.getSeat();
        String[] seatArr = seat.split("\\|");
        for (String item : seatArr) {
            String[] seatitem = item.split("\\:");
            seatList.append(String.format(getString(R.string.row_col_price), seatitem[0], seatitem[1], StringUtil.keep2Decimal(price)));
        }
        mCinemaSeat.setText(seatList.toString());
    }

    private void initData() {
        mFilmVM = ViewModelProviders.of(this).get(FilmVM.class);
        mFilmVM.getConfirmOrder().observe(this, new Observer<XmResource<ConfirmOrderBean>>() {
            @Override
            public void onChanged(@Nullable XmResource<ConfirmOrderBean> confirmOrderBeanXmResource) {
                if (confirmOrderBeanXmResource == null) {
                    return;
                }
                confirmOrderBeanXmResource.handle(new OnCallback<ConfirmOrderBean>() {
                    @Override
                    public void onSuccess(ConfirmOrderBean data) {
                        if (StringUtil.isNotEmpty(data.getVoucherValue())) {
                            mLoadingTicketCode.setVisibility(View.GONE);
                            mMovieCodeNumberLayout.setVerticalGravity(View.VISIBLE);
                            handleTicketVoucher(data.getVoucherValue());
                        } else {
                            mLoadingTicketCode.setVisibility(View.VISIBLE);
                            ThreadDispatcher.getDispatcher().postDelayed(checkVoucherValue, CHECK_INTERVAL);
                        }
                    }
                });

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mFilmVM.queryConfirmOrder(mOrderDetailBean.getOrderId());
    }

    private void handleTicketVoucher(String data) {
        //TODO 处理票据序列码
        if (!TextUtils.isEmpty(data)) {
            String[] voucherArray;
            if (data.contains("|")) {
                voucherArray = data.split("\\|");
            } else {
                voucherArray = new String[]{data};
            }

            if (voucherArray.length == 1) {
                mLlTicketFirst.setVisibility(View.GONE);
                mLlTicketSecond.setVisibility(View.GONE);
                int splitIndex = findFirstIndex(voucherArray[0]);
                mTicketThirdTitle.setText(voucherArray[0].substring(0, splitIndex));
                mTicketThird.setText(voucherArray[0].substring(splitIndex + 1));
            } else if (voucherArray.length == 2) {
                mLlTicketFirst.setVisibility(View.VISIBLE);
                mLlTicketSecond.setVisibility(View.GONE);
                int splitIndexFirst = findFirstIndex(voucherArray[0]);
                mTicketFirstTitle.setText(voucherArray[0].substring(0, splitIndexFirst));
                mTicketFirst.setText(voucherArray[0].substring(splitIndexFirst + 1));

                int splitIndexSecond = findFirstIndex(voucherArray[1]);
                mTicketSecondTitle.setText(voucherArray[1].substring(0, splitIndexSecond));
                mTicketSecond.setText(voucherArray[1].substring(splitIndexSecond + 1));
            } else if (voucherArray.length == 3) {
                int splitIndexFirst = findFirstIndex(voucherArray[0]);
                mTicketFirstTitle.setText(voucherArray[0].substring(0, splitIndexFirst));
                mTicketFirst.setText(voucherArray[0].substring(splitIndexFirst + 1));

                int splitIndexSecond = findFirstIndex(voucherArray[1]);
                mTicketSecondTitle.setText(voucherArray[1].substring(0, splitIndexSecond));
                mTicketSecond.setText(voucherArray[1].substring(splitIndexSecond + 1));

                int splitIndexThird = findFirstIndex(voucherArray[2]);
                mTicketThirdTitle.setText(voucherArray[2].substring(0, splitIndexThird));
                mTicketThird.setText(voucherArray[2].substring(splitIndexThird + 1));
            }
        }
    }

    private int findFirstIndex(String content) {
        return !TextUtils.isEmpty(content) ? content.indexOf(":") : 0;
    }

    private void queryVoucherValue() {
        mFilmVM.queryConfirmOrder(mOrderDetailBean.getOrderId());
        ThreadDispatcher.getDispatcher().postDelayed(checkVoucherValue, CHECK_INTERVAL);
    }
    @NormalOnClick({EventConstants.NormalClick.FILM_ORDER_SUCCESS_NAVI, EventConstants.NormalClick.FILM_ORDER_SUCCESS_PHONE})//按钮对应的名称
    @ResId({R.id.film_navigation, R.id.film_phone})//按钮对应的R文件id
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.film_navigation:
                XMToast.showToast(FilmOrderSuccessActicity.this, R.string.open_nvi);
                break;
            case R.id.film_phone:
                if (StringUtil.isNotEmpty(mCinemaJsonBean.getMobile())){
                    LauncherBlueToothPhoneManager.getInstance().callPhone(mCinemaJsonBean.getMobile());
                }else {
                    XMToast.showToast(this, R.string.not_have_phono);
                }
                break;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (checkVoucherValue!=null){
            ThreadDispatcher.getDispatcher().remove(checkVoucherValue);
        }
    }
}

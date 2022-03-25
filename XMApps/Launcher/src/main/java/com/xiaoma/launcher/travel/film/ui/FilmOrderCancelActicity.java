package com.xiaoma.launcher.travel.film.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.xiaoma.component.base.BaseActivity;
import com.xiaoma.launcher.R;
import com.xiaoma.launcher.common.constant.EventConstants;
import com.xiaoma.launcher.common.constant.LauncherConstants;
import com.xiaoma.launcher.common.manager.LauncherBlueToothPhoneManager;
import com.xiaoma.model.annotation.NormalOnClick;
import com.xiaoma.model.annotation.PageDescComponent;
import com.xiaoma.model.annotation.ResId;
import com.xiaoma.trip.movie.request.CinemaJsonBean;
import com.xiaoma.trip.movie.response.OrderDetailBean;
import com.xiaoma.ui.toast.XMToast;
import com.xiaoma.utils.ConvertUtils;
import com.xiaoma.utils.GsonHelper;
import com.xiaoma.utils.KeyEventUtils;
import com.xiaoma.utils.StringUtil;

@PageDescComponent(EventConstants.PageDescribe.FilmOrderCancelActicityPagePathDesc)
public class FilmOrderCancelActicity extends BaseActivity implements View.OnClickListener {

    private Button mAgainSet;
    private TextView filmName;
    private TextView filmType;
    private TextView filmUnitPrice;
    private TextView cinemaName;
    private TextView cinemaSeat;
    private TextView userPhone;
    private TextView ticketTotalPrice;
    private TextView filmTime;
    private Button againSet;
    private Button filmNavigation;
    private Button filmPhone;
    private OrderDetailBean mOrderDetailBean;
    private CinemaJsonBean mCinemaJsonBean;
    public static final String DAY_TEXT = "日 ";
    public static final String MONTH_TEXT = "月";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.film_order_cancel);
        bindview();
        initView();
    }

    private void bindview() {
        filmName = findViewById(R.id.film_name);
        filmType = findViewById(R.id.film_type);
        filmUnitPrice = findViewById(R.id.film_unit_price);
        cinemaName = findViewById(R.id.cinema_name);
        cinemaSeat = findViewById(R.id.cinema_seat);
        userPhone = findViewById(R.id.user_phone);
        ticketTotalPrice = findViewById(R.id.ticket_total_price);
        filmTime = findViewById(R.id.film_time);

        mAgainSet = findViewById(R.id.again_set);
        filmNavigation = findViewById(R.id.film_navigation);
        filmPhone = findViewById(R.id.film_phone);


        mAgainSet = findViewById(R.id.again_set);
    }

    private void initView() {
        mOrderDetailBean = (OrderDetailBean) getIntent().getSerializableExtra(LauncherConstants.ActionExtras.ORDER_DETAIL_BEAN);
        mCinemaJsonBean = GsonHelper.fromJson(mOrderDetailBean.getCinemaJson(), CinemaJsonBean.class);
        if (mOrderDetailBean != null) {
            filmName.setText(String.format(getString(R.string.film_name), mOrderDetailBean.getOrderName()));
            filmType.setText(mCinemaJsonBean.getFilmType());

            String price = StringUtil.keep2Decimal(Float.parseFloat(mOrderDetailBean.getAmount()) / ConvertUtils.stringToInt(mOrderDetailBean.getTicketNum()));
            filmUnitPrice.setText(String.format(getString(R.string.price_people), price));

            cinemaName.setText(mCinemaJsonBean.getCinemaName());
            userPhone.setText(mOrderDetailBean.getBookPhone());
            ticketTotalPrice.setText(String.format(getString(R.string.seat_list), mOrderDetailBean.getAmount()));
            setFilmTime();
            setSeatList(price);
            mAgainSet.setOnClickListener(this);
            filmPhone.setOnClickListener(this);
            filmNavigation.setOnClickListener(this);
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
        filmTime.setText(showTime.toString());
    }

    /**
     * 设置已选座位列表
     */
    private void setSeatList(String price) {
        StringBuffer seatList = new StringBuffer();
        String seat = mCinemaJsonBean.getSeat();
        String[] seatArr = seat.split("\\|");
        for (String item : seatArr) {
            String[] seatitem = item.split("\\:");
            seatList.append(String.format(getString(R.string.row_col_price), seatitem[0], seatitem[1], price));
        }
        cinemaSeat.setText(seatList.toString());
    }

    @NormalOnClick({EventConstants.NormalClick.FILM_ORDER_CANCEL_RESCHEDULE, EventConstants.NormalClick.FILM_ORDER_CANCEL_PHONE, EventConstants.NormalClick.FILM_ORDER_CANCEL_NAVI})
//按钮对应的名称
    @ResId({R.id.again_set, R.id.film_phone, R.id.film_navigation})//按钮对应的R文件id
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.again_set:
                KeyEventUtils.sendKeyEvent(this, KeyEvent.KEYCODE_BACK);
                break;
            case R.id.film_phone:
                if (StringUtil.isNotEmpty(mCinemaJsonBean.getMobile())) {
                    LauncherBlueToothPhoneManager.getInstance().callPhone(mCinemaJsonBean.getMobile());
                } else {
                    XMToast.showToast(this, R.string.not_have_phono);
                }

                break;
            case R.id.film_navigation:
                XMToast.showToast(this, R.string.open_nvi);
                break;

        }
    }
}

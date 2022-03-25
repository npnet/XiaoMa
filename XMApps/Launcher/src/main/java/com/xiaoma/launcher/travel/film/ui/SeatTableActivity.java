package com.xiaoma.launcher.travel.film.ui;

import android.annotation.SuppressLint;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xiaoma.autotracker.listener.XMAutoTrackerEventOnClickListener;
import com.xiaoma.component.base.BaseActivity;
import com.xiaoma.launcher.R;
import com.xiaoma.launcher.common.constant.EventConstants;
import com.xiaoma.launcher.common.constant.LauncherConstants;
import com.xiaoma.launcher.common.views.EditMsgDialog;
import com.xiaoma.launcher.travel.film.adapter.SeatTableAdapter;
import com.xiaoma.launcher.travel.film.view.SeatTable;
import com.xiaoma.launcher.travel.film.vm.FilmVM;
import com.xiaoma.login.LoginManager;
import com.xiaoma.mapadapter.manager.LocationManager;
import com.xiaoma.mapadapter.model.LocationInfo;
import com.xiaoma.model.ItemEvent;
import com.xiaoma.model.User;
import com.xiaoma.model.XmResource;
import com.xiaoma.model.annotation.BusinessOnClick;
import com.xiaoma.model.annotation.NormalOnClick;
import com.xiaoma.model.annotation.PageDescComponent;
import com.xiaoma.model.annotation.ResId;
import com.xiaoma.trip.movie.request.CinemaJsonBean;
import com.xiaoma.trip.movie.request.RequestLockSeatParm;
import com.xiaoma.trip.movie.response.CinemasShowBean;
import com.xiaoma.trip.movie.response.HallSeatsInfoBean;
import com.xiaoma.trip.movie.response.LockSeatResponseBean;
import com.xiaoma.trip.movie.response.ShowBean;
import com.xiaoma.ui.dialog.ConfirmDialog;
import com.xiaoma.ui.toast.XMToast;
import com.xiaoma.utils.ConvertUtils;
import com.xiaoma.utils.GsonHelper;
import com.xiaoma.utils.ListUtils;
import com.xiaoma.utils.NetworkUtils;
import com.xiaoma.utils.RegexUtils;
import com.xiaoma.utils.StringUtil;
import com.xiaoma.utils.logintype.constant.LoginCfgConstant;
import com.xiaoma.utils.logintype.manager.LoginTypeManager;
import com.xiaoma.utils.tputils.TPUtils;

import java.util.ArrayList;
import java.util.List;
@PageDescComponent(EventConstants.PageDescribe.SeatTableActivityPagePathDesc)
public class SeatTableActivity extends BaseActivity implements View.OnClickListener {
    public SeatTable seatTableView;
    private FilmVM mFilmVM;
    private ShowBean mShowBean;
    private CinemasShowBean mCinemasBean;
    private TextView mConfirmSeat;
    private TextView mConfirmPrice;
    private TextView mUserPhoneNumber;
    private RecyclerView mSeatRv;
    private LinearLayout mSeatLayout;
    private List<HallSeatsInfoBean.SeatsBean> mSeatsBeans = new ArrayList<>();
    private SeatTableAdapter mSeatTableAdapter;
    public static final String NOT_SEAT = "C区";
    public static final String NOT_TYPE = "无类型";
    public static final String PEICE_TEST = "价格效验";
    private TextView mTwoImg;
    private TextView mSelectSession;
    private TextView mConfirmPay;
    private User mUser;

    private LocationInfo mLocationInfo;
    private HallSeatsInfoBean hallSeatsInfoBean;
    private TextView tapOneText;
    private EditMsgDialog mPhoneDilaog;
    private TextView mPhoneSure;
    private LinearLayout mPhoneLayout;

    private int disposeColor;
    private ConfirmDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seat_table);
        bindView();
        initView();
        initData();
        initDialog();
    }

    private void bindView() {
        seatTableView = findViewById(R.id.seatView);
        tapOneText = findViewById(R.id.tap_one_text);
        mConfirmSeat = findViewById(R.id.confirm_seat);
        mConfirmPrice = findViewById(R.id.confirm_price);
        mConfirmPrice.setText(String.format(getString(R.string.seat_list), "0.00"));
        mUserPhoneNumber = findViewById(R.id.user_phone_number);
        mSeatLayout = findViewById(R.id.seat_layout);
        mPhoneLayout = findViewById(R.id.phone_layout);
        mSeatRv = findViewById(R.id.seat_rv);

        mTwoImg = findViewById(R.id.tap_two_img);
        mSelectSession = findViewById(R.id.tap_two_select_session);
        mConfirmPay = findViewById(R.id.tap_three_confirm_pay);
    }

    private void initView() {
        mTwoImg.setBackgroundResource(R.drawable.round_back_yellow);
        disposeColor = Color.parseColor("#8a919d");
        mSelectSession.setTextColor(disposeColor);
        mConfirmPay.setTextColor(disposeColor);
        tapOneText.setText(TPUtils.get(this, LauncherConstants.PATH_TYPE, getString(R.string.select_cinema)));
        mSeatsBeans.clear();
        mSeatRv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mSeatTableAdapter = new SeatTableAdapter();
        mSeatRv.setAdapter(mSeatTableAdapter);
        mSeatTableAdapter.setEmptyView(R.layout.not_seat_data, (ViewGroup) mSeatRv.getParent());
        mConfirmSeat.setOnClickListener(new XMAutoTrackerEventOnClickListener() {
            @Override
            public ItemEvent returnPositionEventMsg(View view) {
                return new ItemEvent(EventConstants.NormalClick.SEAT_TABLE_SUBMIT,getSeatIdList());
            }

            @Override
            @BusinessOnClick
            public void onClick(View v) {
                if (!LoginTypeManager.getInstance().judgeUse(LoginCfgConstant.MOVIE_TICKETS)) {
                    XMToast.showToast(SeatTableActivity.this, LoginTypeManager.getPrompt(SeatTableActivity.this));
                    return;
                }

                if (!NetworkUtils.isConnected(SeatTableActivity.this)) {
                    showToastException(R.string.net_work_error);
                    return;
                }

                if (mSeatsBeans.size() < 1) {
                    XMToast.showToast(SeatTableActivity.this, R.string.not_slete_seat);
                } else {
                    showAlterDialog();
                }
            }
        });
        mPhoneLayout.setOnClickListener(this);
    }

    /**
     * 电话确认dialog
     */
    private void showAlterDialog() {
        String content = getString(R.string.confirm_number_correct)+"？\n\n"+
                String.format(getString(R.string.phone_sure), mUserPhoneNumber.getText())+
                "\n"+getString(R.string.ticket_accepting);
        mDialog = new ConfirmDialog(this);
        mDialog.setContent(content)
                .setPositiveButton(getString(R.string.sure),new View.OnClickListener() {
                    @Override
                    @NormalOnClick(EventConstants.NormalClick.SEAT_TABLE_SUBMIT_SURE)
                    public void onClick(View v) {
                        detecLockingSeat(String.format(getString(R.string.phone_sure), mUserPhoneNumber.getText()));
                        mDialog.dismiss();
                    }
                })
                .setNegativeButton(getString(R.string.dialog_modify),new View.OnClickListener() {
                    @Override
                    @NormalOnClick(EventConstants.NormalClick.SEAT_TABLE_SUBMIT_CANCEL)
                    public void onClick(View v) {
                        updatePhone();
                    }
                })
                .show();

//        View view = View.inflate(this, R.layout.dialog_confirm_film_order, null);
//        mPhoneSure = view.findViewById(R.id.phone_sure);
//        ImageView cancelBtn = view.findViewById(R.id.cancel_img);
//        TextView sureBtn = view.findViewById(R.id.btn_sure);
//        TextView modifyBtn = view.findViewById(R.id.btn_modify);
//        mPhoneSure.setText(String.format(getString(R.string.phone_sure), mUserPhoneNumber.getText()));
//        final XmDialog builder = new XmDialog.Builder(this)
//                .setView(view)
//                .setWidth(700)
//                .setHeight(400)
//                .create();
//        sureBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            @NormalOnClick(EventConstants.NormalClick.SEAT_TABLE_SUBMIT_SURE)
//            public void onClick(View v) {
//                detecLockingSeat(mPhoneSure.getText().toString());
//                builder.dismiss();
//            }
//        });
//        modifyBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            @NormalOnClick(EventConstants.NormalClick.SEAT_TABLE_SUBMIT_CANCEL)
//            public void onClick(View v) {
//                updatePhone();
//            }
//        });
//        cancelBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            @NormalOnClick(EventConstants.NormalClick.SEAT_TABLE_SUBMIT_CLOSE)
//            public void onClick(View v) {
//                builder.dismiss();
//            }
//        });
//        builder.show();
    }

    /**
     * 检测锁座状态
     */
    private void detecLockingSeat(String phone) {
        if (mLocationInfo == null) {
            XMToast.showToast(this, R.string.not_nvi_info);
            return;
        }
        if (mUser == null) {
            XMToast.showToast(this, R.string.user_not_load);
            return;
        }
        RequestLockSeatParm requestLockSeatParm = new RequestLockSeatParm();
        requestLockSeatParm.cinemaId = mCinemasBean.getCinemaId();
        requestLockSeatParm.ticketCount = mSeatsBeans.size() + "";
        requestLockSeatParm.filmId = mCinemasBean.getFilmId();
        requestLockSeatParm.userPhone = mUserPhoneNumber.getText().toString();
        requestLockSeatParm.sectionId = mSeatsBeans.get(0).getSectionId();
        requestLockSeatParm.hallCode = mShowBean.getHallCode();
        requestLockSeatParm.showDate = mShowBean.getShowDate();
        requestLockSeatParm.showTime = mShowBean.getShowTime();
        requestLockSeatParm.filmName = mCinemasBean.getFilmName();
        requestLockSeatParm.cinemaName = mCinemasBean.getCinemaName();
        requestLockSeatParm.showCode = mShowBean.getShowCode();
        requestLockSeatParm.hallName = mShowBean.getHallName();
        requestLockSeatParm.seatIds = getSeatIdList();
        requestLockSeatParm.seatCross = getseatCrossList();
        requestLockSeatParm.prices = getseatPriceList();
        requestLockSeatParm.lat = mLocationInfo.getLatitude() + "";
        requestLockSeatParm.lon = mLocationInfo.getLongitude() + "";
        requestLockSeatParm.address = mCinemasBean.getAddress();
        requestLockSeatParm.cinemaPhone = mCinemasBean.getMobile();
        requestLockSeatParm.cinemaJson = getCinemaJson();

        requestLockSeatParm.filmType = TPUtils.get(SeatTableActivity.this, LauncherConstants.FILM_TYPE, NOT_TYPE);

        mFilmVM.queryLockSeat(requestLockSeatParm);
    }

    /**
     * 返回CinemaJson
     *
     * @return
     */
    private String getCinemaJson() {
        CinemaJsonBean cinemaJsonBean = new CinemaJsonBean();
        cinemaJsonBean.id = mCinemasBean.getFilmId();
        cinemaJsonBean.address = mCinemasBean.getAddress();
        cinemaJsonBean.lat = mCinemasBean.getLat();
        cinemaJsonBean.lon = mCinemasBean.getLon();
        cinemaJsonBean.filmType = TPUtils.get(SeatTableActivity.this, LauncherConstants.FILM_TYPE, NOT_TYPE);
        cinemaJsonBean.seat = getseatCrossList();
        cinemaJsonBean.mobile = mCinemasBean.getMobile();
        cinemaJsonBean.cinemaName = mCinemasBean.getCinemaName();
        cinemaJsonBean.iconUrl = mCinemasBean.getIconUrl();
        return GsonHelper.toJson(cinemaJsonBean);
    }

    /**
     * 返回多座位id
     *
     * @return
     */
    public String getSeatIdList() {
        StringBuffer seatList = new StringBuffer();
        for (HallSeatsInfoBean.SeatsBean item : mSeatsBeans) {
            seatList.append(item.getSeatId() + "|");
        }
        return seatList.toString().substring(0, seatList.length() - 1);
    }

    /**
     * 返回多座位行列号
     *
     * @return
     */
    public String getseatCrossList() {
        StringBuffer seatRowColList = new StringBuffer();
        for (HallSeatsInfoBean.SeatsBean item : mSeatsBeans) {
            seatRowColList.append(item.getSeatRow() + ":" + item.getSeatCol() + "|");
        }
        return seatRowColList.toString().substring(0, seatRowColList.length() - 1);
    }

    /**
     * 返回多座位价格
     *
     * @return
     */
    public String getseatPriceList() {
        StringBuffer seatPriceList = new StringBuffer();
        for (HallSeatsInfoBean.SeatsBean item : mSeatsBeans) {
            seatPriceList.append(StringUtil.keep2Decimal(Float.parseFloat(mShowBean.getPrice())) + "|");
        }
        return seatPriceList.toString().substring(0, seatPriceList.length() - 1);
    }

    private void initData() {
        mLocationInfo = LocationManager.getInstance().getCurrentLocation();
        mUser = com.xiaoma.login.UserManager.getInstance().getUser(LoginManager.getInstance().getLoginUserId());
        mShowBean = (ShowBean) getIntent().getSerializableExtra(LauncherConstants.ActionExtras.SHOW_BEAN);
        mCinemasBean = (CinemasShowBean) getIntent().getSerializableExtra(LauncherConstants.ActionExtras.CINEMAS_SHOW_BEAN);
        hallSeatsInfoBean = (HallSeatsInfoBean) getIntent().getSerializableExtra(LauncherConstants.ActionExtras.HALL_SEATS_INFO_BEAN);
        mSeatTableAdapter.setPrice(StringUtil.keep2Decimal(Float.parseFloat(mShowBean.getPrice())));
        if (mUser == null) {
            mUserPhoneNumber.setText(R.string.not_user_info);
        } else {
            String phoneString=mUser.getPhone();
            mUserPhoneNumber.setText(phoneString==null?"":(phoneString.length()<=11?phoneString:(phoneString.substring(0,11)+"...")));
//            mUserPhoneNumber.setText(mUser.getPhone());
        }
        if (hallSeatsInfoBean != null) {
            setSeat(hallSeatsInfoBean);
        }
        mFilmVM = ViewModelProviders.of(this).get(FilmVM.class);
        mFilmVM.getLockSeat().observe(this, new android.arch.lifecycle.Observer<XmResource<LockSeatResponseBean>>() {
            @Override
            public void onChanged(@Nullable XmResource<LockSeatResponseBean> lockSeatResponseBeanXmResource) {
                lockSeatResponseBeanXmResource.handle(new OnCallback<LockSeatResponseBean>() {
                    @Override
                    public void onSuccess(LockSeatResponseBean data) {
                        Intent intent = new Intent(SeatTableActivity.this, FilmOrderPayActicity.class);
                        intent.putExtra(LauncherConstants.ActionExtras.LOCK_SEAT_RESPONSE_BEAN, data);
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onFailure(String msg) {
                        if (StringUtil.isNotEmpty(msg)) {
                            if (msg.contains(PEICE_TEST)) {
                                XMToast.showToast(SeatTableActivity.this, R.string.price_test_failure);
                            } else {
                                XMToast.showToast(SeatTableActivity.this, msg);
                            }
                        }

                    }
                });
            }
        });
    }

    /**
     * 座位列表设置
     *
     * @param data
     */
    private void setSeat(final HallSeatsInfoBean data) {
        seatTableView.setScreenName(mShowBean.getHallName());//设置屏幕名称
        seatTableView.setMaxSelected(LauncherConstants.FILM_SEAT_MAX_SELECTED);//设置最多选中

        seatTableView.setSeatChecker(new SeatTable.SeatChecker() {
            @Override
            public boolean isValidSeat(int row, int column) {
                if (!ListUtils.isEmpty(data.getSeats())) {
                    for (HallSeatsInfoBean.SeatsBean item : data.getSeats()) {
                        int seatRow = Integer.parseInt(item.getGraphRow());
                        int seatCol = Integer.parseInt(item.getGraphCol());
                        if (seatRow - 1 == row && seatCol - 1 == column && !NOT_SEAT.equals(item.getSectionName())) {
                            return true;
                        }
                    }
                }
                return false;
            }

            @Override
            public boolean isSold(int row, int column) {
                if (!ListUtils.isEmpty(data.getSeats())) {
                    for (HallSeatsInfoBean.SeatsBean item : data.getSeats()) {
                        if (ConvertUtils.stringToInt(item.getGraphRow()) - 1 == row && ConvertUtils.stringToInt(item.getGraphCol()) - 1 == column && item.getState().equals("1")) {
                            return false;
                        }
                    }
                }
                return true;
            }

            @SuppressLint("StringFormatInvalid")
            @Override
            public void checked(int row, int column) {
                if (!ListUtils.isEmpty(data.getSeats())) {
                    for (HallSeatsInfoBean.SeatsBean item : data.getSeats()) {
                        if (ConvertUtils.stringToInt(item.getGraphRow()) - 1 == row && ConvertUtils.stringToInt(item.getGraphCol()) - 1 == column && !item.getSectionName().equals("C区")) {
                            mSeatsBeans.add(item);
                            float price = Float.parseFloat(mShowBean.getPrice()) * mSeatsBeans.size();
                            mConfirmPrice.setText(String.format(getString(R.string.seat_list), StringUtil.keep2Decimal(price)));
                            //mSeatNumber.setText(String.format(getString(R.string.seat_list), mSeatsBeans.size()));
                            mSeatTableAdapter.setNewData(mSeatsBeans);
                        }
                    }
                }
            }

            @SuppressLint("StringFormatInvalid")
            @Override
            public void unCheck(int row, int column) {
                if (!ListUtils.isEmpty(data.getSeats())) {
                    for (HallSeatsInfoBean.SeatsBean item : data.getSeats()) {
                        if (ConvertUtils.stringToInt(item.getGraphRow()) - 1 == row && ConvertUtils.stringToInt(item.getGraphCol()) - 1 == column) {
                            mSeatsBeans.remove(item);
                            float price = Float.parseFloat(mShowBean.getPrice()) * mSeatsBeans.size();
                            mConfirmPrice.setText(String.format(getString(R.string.seat_list), StringUtil.keep2Decimal(price)));
                            mSeatTableAdapter.setNewData(mSeatsBeans);
                        }
                    }
                }
                if (mSeatsBeans.size() < 1) {
                    mConfirmPrice.setText(String.format(getString(R.string.seat_list), "0.00"));
                }
            }

            @Override
            public String[] checkedSeatTxt(int row, int column) {
                return null;
            }

        });
        seatTableView.setData(data.getMax_Row_Num(), data.getMax_Col_Num());
    }


    @Override
    @NormalOnClick({ EventConstants.NormalClick.SEAT_TABLE_PHONE})//按钮对应的名称
    @ResId({ R.id.phone_layout})//按钮对应的R文件id
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.phone_layout:
                updatePhone();
                break;
        }
    }

    /**
     * 初始化电话修改的dialog
     */
    private void initDialog() {
        mPhoneDilaog = new EditMsgDialog(this, R.style.dialog_fullscreen_hotel);
        mPhoneDilaog.getEditText().setFilters(new InputFilter[]{new InputFilter.LengthFilter(13)});
        mPhoneDilaog.getEditText().setHint(R.string.film_user_phone_hint);
        mPhoneDilaog.getEditText().setInputType(InputType.TYPE_CLASS_NUMBER);
        mPhoneDilaog.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s == null || s.length() == 0)
                    return;
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < s.length(); i++) {
                    if (i != 3 && i != 8 && s.charAt(i) == '-') {
                        continue;
                    } else {
                        sb.append(s.charAt(i));
                        if ((sb.length() == 4 || sb.length() == 9) && sb.charAt(sb.length() - 1) != '-') {
                            sb.insert(sb.length() - 1, '-');
                        }
                    }
                }
                if (!sb.toString().equals(s.toString())) {
                    int index = start + 1;
                    if (sb.charAt(start) == '-') {
                        if (before == 0) {
                            index++;
                        } else {
                            index--;
                        }
                    } else {
                        if (before == 1) {
                            index--;
                        }
                    }
                    mPhoneDilaog.getEditText().setText(sb.toString());
                    mPhoneDilaog.getEditText().setSelection(index);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    /**
     * 更新电话号码
     */
    private void updatePhone() {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
        mPhoneDilaog.setOnClickSureListener(new EditMsgDialog.OnClickSureListener() {
            @Override
            public void onClickSure(String name) {
                if (!StringUtil.isEmpty(name)) {
                    String phone = getPhoneText(name, '-');

                    if (RegexUtils.isMobileExact(phone)) {
                        mPhoneDilaog.dismiss();
                        XMToast.showToast(SeatTableActivity.this, getString(R.string.phone_number_revise_success), getDrawable(R.drawable.toast_success));
                        mUserPhoneNumber.setText(phone);
                        if (mDialog != null) {
                            String content = getString(R.string.confirm_number_correct)+"？\n\n"+
                                    String.format(getString(R.string.phone_sure), phone+"")+
                                    "\n"+getString(R.string.ticket_accepting);
                            mDialog.setContent(content);
                        }
                    } else {
                        XMToast.showToast(SeatTableActivity.this, getString(R.string.input_right_phone_number), getDrawable(R.drawable.toast_error));
                    }
                } else {
                    XMToast.showToast(SeatTableActivity.this, getString(R.string.input_right_phone_number), getDrawable(R.drawable.toast_error));
                }
            }
        });

        mPhoneDilaog.getEditText().setText("");
        mPhoneDilaog.show();
    }

    private String getPhoneText(String str, char delChar) {
        String delStr = "";
        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) != delChar) {
                delStr += str.charAt(i);
            }
        }
        return delStr;
    }

}

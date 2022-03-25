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
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.Html;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xiaoma.ad.utils.GsonUtil;
import com.xiaoma.autotracker.listener.XMAutoTrackerEventOnClickListener;
import com.xiaoma.component.base.BaseActivity;
import com.xiaoma.launcher.R;
import com.xiaoma.launcher.common.constant.EventConstants;
import com.xiaoma.launcher.common.views.EditMsgDialog;
import com.xiaoma.launcher.travel.hotel.constants.HotelConstants;
import com.xiaoma.launcher.travel.hotel.vm.HotelRoomVM;
import com.xiaoma.launcher.travel.itemevent.HotelPAYTrackerBean;
import com.xiaoma.login.LoginManager;
import com.xiaoma.login.UserManager;
import com.xiaoma.model.ItemEvent;
import com.xiaoma.model.User;
import com.xiaoma.model.XmResource;
import com.xiaoma.model.annotation.BusinessOnClick;
import com.xiaoma.model.annotation.NormalOnClick;
import com.xiaoma.model.annotation.PageDescComponent;
import com.xiaoma.model.annotation.ResId;
import com.xiaoma.trip.hotel.response.BookOrderResult;
import com.xiaoma.ui.toast.XMToast;
import com.xiaoma.utils.NetworkUtils;
import com.xiaoma.utils.RegexUtils;
import com.xiaoma.utils.StringUtil;
import com.xiaoma.utils.TimeUtils;
import com.xiaoma.utils.logintype.callback.OnBlockCallback;
import com.xiaoma.utils.logintype.constant.LoginCfgConstant;
import com.xiaoma.utils.logintype.manager.LoginType;
import com.xiaoma.utils.logintype.manager.LoginTypeManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

@PageDescComponent(EventConstants.PageDescribe.BookHotelTwoActivityPagePathDesc)
public class BookHotelTwoActivity extends BaseActivity implements View.OnClickListener {

    private TextView tvSecond;
    private TextView tvSecondInfo;
    private TextView tvHotelName;
    private TextView tvRoomType;
    private TextView tvHotelDate;
    private TextView tvRoomNum;
    private TextView tvPhone;
    private TextView tvMoney;
    private LinearLayout llPay;

    private RelativeLayout rlRoomNum;
    private RelativeLayout rlPhone;

    private String mCheckIn;
    private String mCheckOut;
    private String mHotelName;
    private String mRoomPrice;
    private String mRoomType;
    private String orderAmount;
    private String mHotelId;
    private String mRoomMsg;
    private String mRoomId;
    private String rateplanId;
    private int roomCount;
    //入住人名字，多个，用,号拼接
    private String guestName;
    private String bookPhone;
    private String address;
    private String lat;
    private String lon;
    private boolean isCancel;
    private String lastCancelDate;
    private String storePhone;
    private String imageUrl;

    private final int ROOM_REQUEST_CODE = 1001;
    private final int PHONE_REQUEST_CODE = 1002;


    private ArrayList<String> mNames = new ArrayList<>();
    private HotelRoomVM hotelRoomVM;

    private EditMsgDialog mPhoneDilaog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_hotel_two);
        initView();
        initViewListener();
        getDataFromIntent();
        setViewData();
        initData();
        initDialog();
    }

    private void initView() {
        tvSecond = findViewById(R.id.tap_two_hotel);
        tvSecondInfo = findViewById(R.id.tap_two_select_session);
        tvHotelName = findViewById(R.id.tv_hotel_name);
        tvRoomType = findViewById(R.id.tv_room_type);
        tvHotelDate = findViewById(R.id.tv_room_date);
        tvRoomNum = findViewById(R.id.tv_room_num);
        tvPhone = findViewById(R.id.tv_booking_phone);
        tvMoney = findViewById(R.id.tv_hotel_money);
        llPay = findViewById(R.id.ll_pay);
        rlRoomNum = findViewById(R.id.rl_room_num);
        rlPhone = findViewById(R.id.rl_booking_phone);

        tvSecond.setBackground(getDrawable(R.drawable.round_back_yellow));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            tvSecondInfo.setTextColor(getColor(R.color.white));
        }

        getNaviBar().getMiddleView().setImageDrawable(getDrawable(R.drawable.selector_iv_hotel_policy));
        getNaviBar().getMiddleView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (StringUtil.isEmpty(mHotelId)) {
                    showToast(R.string.error_msg_hotel_id);
                    return;
                }
                Intent intent = new Intent(BookHotelTwoActivity.this, BookHotelPolicyActivity.class);
                intent.putExtra(HotelConstants.HOTEL_ID, mHotelId);
                startActivity(intent);
            }
        });


    }

    private void initViewListener() {
        llPay.setOnClickListener(new XMAutoTrackerEventOnClickListener() {
            @Override
            public ItemEvent returnPositionEventMsg(View view) {
                return new ItemEvent(EventConstants.NormalClick.HOTEL_HOME_GOTO_PAY, GsonUtil.toJson(setHotelBookTracker()));
            }

            @Override
            @BusinessOnClick
            public void onClick(View v) {
                //预定订单
                if (!LoginTypeManager.getInstance().judgeUse(LoginCfgConstant.ORDER_HOTEL, new OnBlockCallback() {
                    @Override
                    public boolean onShowToast(LoginType loginType) {
                        XMToast.showToast(BookHotelTwoActivity.this, loginType.getPrompt(BookHotelTwoActivity.this));
                        return true;
                    }
                })) {
                    return;
                }

                if (!NetworkUtils.isConnected(BookHotelTwoActivity.this)) {
                    showToastException(R.string.net_work_error);
                    return;
                }

                hotelRoomVM.bookRoom(mHotelId, mHotelName, mRoomId, rateplanId, mCheckIn, mCheckOut, orderAmount, roomCount, guestName, tvPhone.getText().toString().trim(), bookPhone, address, lat, lon, mRoomType, mRoomMsg, isCancel, lastCancelDate, storePhone, imageUrl);
            }
        });
        rlRoomNum.setOnClickListener(this);
        rlPhone.setOnClickListener(this);
    }

    private void getDataFromIntent() {
        mCheckIn = getIntent().getStringExtra(HotelConstants.HOTEL_CHECK_IN);
        mCheckOut = getIntent().getStringExtra(HotelConstants.HOTEL_CHECK_OUT);
        mHotelName = getIntent().getStringExtra(HotelConstants.HOTEL_NAME);
        mRoomPrice = getIntent().getStringExtra(HotelConstants.HOTEL_ROOM_PRICE);
        mRoomType = getIntent().getStringExtra(HotelConstants.HOTEL_ROOM_TYPE);
        mRoomMsg = getIntent().getStringExtra(HotelConstants.HOTEL_ROOM_MSG);
        mHotelId = getIntent().getStringExtra(HotelConstants.HOTEL_ID);
        mRoomId = getIntent().getStringExtra(HotelConstants.HOTEL_ROOM_ID);
        rateplanId = getIntent().getStringExtra(HotelConstants.HOTEL_RATEPLAN_ID);
        roomCount = getIntent().getIntExtra(HotelConstants.HOTEL_ROOM_COUNT, 1);
        guestName = getIntent().getStringExtra(HotelConstants.HOTEL_GUEST_NAME);
        bookPhone = getIntent().getStringExtra(HotelConstants.HOTEL_BOOK_PHONE);
        address = getIntent().getStringExtra(HotelConstants.HOTEL_ADDRESS);
        lat = getIntent().getStringExtra(HotelConstants.HOTEL_LAT);
        lon = getIntent().getStringExtra(HotelConstants.HOTEL_LON);
        isCancel = getIntent().getBooleanExtra(HotelConstants.HOTEL_ORDER_IS_CANCEL, false);
        lastCancelDate = getIntent().getStringExtra(HotelConstants.HOTEL_CANCEL_TIME);
        storePhone = getIntent().getStringExtra(HotelConstants.HOTEL_PHONE);
        imageUrl = getIntent().getStringExtra(HotelConstants.HOTEL_IMAGE);
    }

    @SuppressLint("StringFormatInvalid")
    private void setViewData() {

        mNames.add(guestName);

        tvHotelName.setText(mHotelName);
        orderAmount = mRoomPrice;
        String roomType = mRoomType + "  /  " + "<font color='#fcd3a4'>" + "￥" + orderAmount + "</font>";
        tvRoomType.setText(Html.fromHtml(roomType));

        tvHotelDate.setText(covertDate(mCheckIn) + "-" + covertDate(mCheckOut));

        if (roomCount == 1) {
            User user = UserManager.getInstance().getUser(LoginManager.getInstance().getLoginUserId());
//            tvRoomNum.setText(roomCount + "间" + "  (" + user.getName() + ")");
            tvRoomNum.setText(roomCount + "间" + "  (" + limitLength(user.getName(), 10) + ")");
        } else {
            tvRoomNum.setText(roomCount + "间");
        }

        tvPhone.setText(bookPhone);
        tvMoney.setText("￥" + orderAmount);

    }

    private String limitLength(String data, int length) {
        if (data == null || data.length() == 0) {
            return "";
        }
        StringBuilder str = new StringBuilder();
        if (data.length() > length) {
            str.append(data, 0, length).append("...");
        } else {
            str.append(data);
        }
        return str.toString();
    }

    private void initData() {


        hotelRoomVM = ViewModelProviders.of(this).get(HotelRoomVM.class);

        //下订单结果
        hotelRoomVM.getBookResult().observe(this, new Observer<XmResource<BookOrderResult>>() {
            @Override
            public void onChanged(@Nullable XmResource<BookOrderResult> stringXmResource) {
                stringXmResource.handle(new OnCallback<BookOrderResult>() {
                    @Override
                    public void onSuccess(BookOrderResult data) {

                        if (data != null && !StringUtil.isEmpty(data.getQrCode())) {
                            toBookingThreeStep(data);
                            finish();
                        } else {
                            showToast(R.string.no_room_left);
                        }
                    }

                    @Override
                    public void onError(int code, String message) {
//                        super.onError(code, message);
                        showToast(message);
                    }
                });
            }
        });

    }

    private void initDialog() {
        mPhoneDilaog = new EditMsgDialog(this, R.style.dialog_fullscreen_hotel);
        mPhoneDilaog.getEditText().setFilters(new InputFilter[]{new InputFilter.LengthFilter(13)});
        mPhoneDilaog.getEditText().setHint(R.string.book_user_phone_hint);
        mPhoneDilaog.getEditText().setInputType(InputType.TYPE_CLASS_NUMBER);
        mPhoneDilaog.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s == null || s.length() == 0) return;
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
                        XMToast.showToast(BookHotelTwoActivity.this, getString(R.string.phone_number_revise_success), getDrawable(R.drawable.toast_success));
                        tvPhone.setText(phone);
                    } else {
                        XMToast.showToast(BookHotelTwoActivity.this, getString(R.string.input_right_phone_number), getDrawable(R.drawable.toast_error));
                    }
                } else {
                    XMToast.showToast(BookHotelTwoActivity.this, getString(R.string.input_right_phone_number), getDrawable(R.drawable.toast_error));
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


    @Override
    @NormalOnClick({EventConstants.NormalClick.HOTEL_HOME_NUMBER, EventConstants.NormalClick.HOTEL_HOME_PHONE})
//按钮对应的名称
    @ResId({R.id.rl_room_num, R.id.rl_booking_phone})//按钮对应的R文件id
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_room_num:

                Intent intent = new Intent(this, BookUserMsgActivity.class);
                intent.putStringArrayListExtra(HotelConstants.HOTEL_GUEST_NAME, mNames);
                intent.putExtra(HotelConstants.HOTEL_ID, mHotelId);
                intent.putExtra(HotelConstants.HOTEL_ROOM_ID, mRoomId);
                intent.putExtra(HotelConstants.HOTEL_RATEPLAN_ID, rateplanId);
                intent.putExtra(HotelConstants.HOTEL_CHECK_IN, mCheckIn);
                intent.putExtra(HotelConstants.HOTEL_CHECK_OUT, mCheckOut);
                startActivityForResult(intent, ROOM_REQUEST_CODE);
                break;
            case R.id.rl_booking_phone:
                updatePhone();
                break;

        }
    }

    /**
     * 更新房间数量和价格
     *
     * @param data
     */
    @SuppressLint("StringFormatInvalid")
    private void updateRoomCountAndPrice(Intent data) {
        ArrayList<String> tempNewNames = data.getStringArrayListExtra(HotelConstants.HOTEL_NEW_GUEST_NAME);
        mNames.clear();
        mNames.addAll(tempNewNames);
        guestName = getGuestNames();
        roomCount = tempNewNames.size();
        orderAmount = StringUtil.keep2Decimal(Float.valueOf(mRoomPrice) * roomCount);
        if (roomCount == 1) {
//            tvRoomNum.setText(roomCount + "间" + "  (" + tempNewNames.get(0) + ")");
            tvRoomNum.setText(roomCount + "间" + "  (" + limitLength(tempNewNames.get(0), 10) + ")");
        } else {
            tvRoomNum.setText(roomCount + "间");
        }
        String roomType = mRoomType + "  /  " + "<font color='#fcd3a4'>" + "￥" + orderAmount + "</font>";
        tvRoomType.setText(Html.fromHtml(roomType));
        tvMoney.setText("￥" + orderAmount);
    }

    private String getGuestNames() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < mNames.size(); i++) {
            if (mNames.size() - 1 == i) {
                sb.append(mNames.get(i));
            } else {
                sb.append(mNames.get(i)).append(",");
            }
        }

        return sb.toString();
    }

    private void toBookingThreeStep(BookOrderResult bookOrderResult) {

        Intent intent = new Intent(this, BookHotelThreeActivity.class);

        intent.putExtra(HotelConstants.HOTEL_NAME, mHotelName);
        intent.putExtra(HotelConstants.HOTEL_ID, mHotelId);
        intent.putExtra(HotelConstants.HOTEL_CHECK_IN, mCheckIn);
        intent.putExtra(HotelConstants.HOTEL_CHECK_OUT, mCheckOut);
        intent.putExtra(HotelConstants.HOTEL_ROOM_COUNT, roomCount);
        intent.putExtra(HotelConstants.HOTEL_ROOM_TYPE, mRoomType);
        intent.putExtra(HotelConstants.HOTEL_ROOM_PRICE, mRoomPrice);
        intent.putExtra(HotelConstants.HOTEL_BOOK_PHONE, bookPhone);
        intent.putExtra(HotelConstants.HOTEL_ROOM_MSG, mRoomMsg);
        intent.putExtra(HotelConstants.HOTEL_ORDER_AMOUNT, orderAmount);
        intent.putExtra(HotelConstants.HOTEL_ORDER_IS_CANCEL, isCancel);
        intent.putExtra(HotelConstants.HOTEL_CANCEL_TIME, lastCancelDate);
        intent.putExtra(HotelConstants.HOTEL_ORDER_ID, bookOrderResult.getId());
        intent.putExtra(HotelConstants.HOTEL_ORDER_QRCODE, bookOrderResult.getQrCode());
        intent.putExtra(HotelConstants.ORDER_LAST_PAY_DATE, bookOrderResult.getLastpayDate());
        intent.putExtra(HotelConstants.ORDER_CREATE_DATE, bookOrderResult.getCreateDate());
        intent.putExtra(HotelConstants.HOTEL_ADDRESS, address);
        intent.putExtra(HotelConstants.HOTEL_LAT, lat);
        intent.putExtra(HotelConstants.HOTEL_LON, lon);
        intent.putExtra(HotelConstants.HOTEL_PHONE, storePhone);
        intent.putExtra(HotelConstants.HOTEL_IMAGE, imageUrl);

        startActivity(intent);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case ROOM_REQUEST_CODE:
                    updateRoomCountAndPrice(data);
                    break;
                case PHONE_REQUEST_CODE:
                    break;
            }
        }
    }

    private String covertDate(String date) {
        Date tempDate = TimeUtils.string2Date(date, new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()));

        return TimeUtils.date2String(tempDate, new SimpleDateFormat("MM月dd日", Locale.getDefault()));
    }

    private HotelPAYTrackerBean setHotelBookTracker() {
        HotelPAYTrackerBean hotelPAYTrackerBean = new HotelPAYTrackerBean();
        hotelPAYTrackerBean.id = mCheckIn;
        hotelPAYTrackerBean.value = mCheckOut;
        hotelPAYTrackerBean.h = mRoomType;  //酒店星级
        hotelPAYTrackerBean.i = mRoomPrice;
        hotelPAYTrackerBean.j = mHotelName;
        return hotelPAYTrackerBean;
    }
}

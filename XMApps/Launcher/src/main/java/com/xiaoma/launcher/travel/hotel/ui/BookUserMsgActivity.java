package com.xiaoma.launcher.travel.hotel.ui;


/*  @project_name：  XMAgateOS
 *  @package_name：  com.xiaoma.launcher.travel.hotel.ui
 *  @file_name:      BookUserMsgActivity
 *  @author:         Rookie
 *  @create_time:    2019/1/15 15:13
 *  @description：   TODO             */

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;

import com.xiaoma.component.base.BaseActivity;
import com.xiaoma.launcher.R;
import com.xiaoma.launcher.common.constant.EventConstants;
import com.xiaoma.launcher.common.views.EditMsgDialog;
import com.xiaoma.launcher.travel.hotel.adapter.RoomUserAdapter;
import com.xiaoma.launcher.travel.hotel.constants.HotelConstants;
import com.xiaoma.launcher.travel.hotel.vm.HotelRoomVM;
import com.xiaoma.model.XmResource;
import com.xiaoma.model.annotation.NormalOnClick;
import com.xiaoma.model.annotation.PageDescComponent;
import com.xiaoma.model.annotation.ResId;
import com.xiaoma.trip.hotel.response.RatePlanStatusBean;
import com.xiaoma.ui.toast.XMToast;
import com.xiaoma.utils.NetworkUtils;
import com.xiaoma.utils.StringUtil;

import java.util.ArrayList;

@PageDescComponent(EventConstants.PageDescribe.BookUserMsgActivityPagePathDesc)
public class BookUserMsgActivity extends BaseActivity {

    private GridView mGvRoom;
    private Button mBtnAddRoom;
    private RoomUserAdapter mAdapter;
    private ArrayList<String> mNameList = new ArrayList<>();
    private EditMsgDialog mDialog;
    private EditText mEtName;

    private HotelRoomVM mHotelRoomVM;

    private String mCheckIn;
    private String mCheckOut;
    private String mHotelId;
    private String mRoomId;
    private String rateplanId;
    private int maxRoomCount = -1;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getNaviBar().showBackNavi();
        setContentView(R.layout.activity_book_user_msg);
        initView();
        initData();
        initDialog();
    }


    private void initView() {

        mGvRoom = findViewById(R.id.gv_room);

        mBtnAddRoom = findViewById(R.id.btn_add_room);
        mAdapter = new RoomUserAdapter(this, mNameList, R.layout.item_book_user);
        mAdapter.setOnItemEdit(new RoomUserAdapter.OnItemEdit() {
            @Override
            public void itemEdit(String name, int position) {
                setItemEdit(name, position);
            }
        });
        mGvRoom.setAdapter(mAdapter);

        mBtnAddRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            @NormalOnClick(EventConstants.NormalClick.HOTEL_HOME_INCREASE)
            @ResId(R.id.btn_add_room)
            public void onClick(View v) {
                if (!NetworkUtils.isConnected(BookUserMsgActivity.this)) {
                    showToastException(R.string.net_work_error);
                    return;
                }
                mHotelRoomVM.queryHasRoom(mHotelId, mRoomId, rateplanId, mCheckIn, mCheckOut);
            }
        });
    }

    private void addRoomMsg() {

        mDialog.setOnClickSureListener(new EditMsgDialog.OnClickSureListener() {
            @Override
            public void onClickSure(String name) {
                if (!StringUtil.isEmpty(name)) {
                    mNameList.add(name);
                    mAdapter.setDatas(mNameList);
                    mDialog.dismiss();
                } else {
                    showToast(R.string.please_input_name);
                }
            }
        });

        mEtName.setText("");
        mEtName.setHint(R.string.book_user_hint);
        mDialog.show();
    }

    private void setItemEdit(final String name, final int position) {
        if (mDialog == null) {
            initDialog();
        }

        mDialog.setOnClickSureListener(new EditMsgDialog.OnClickSureListener() {
            @Override
            public void onClickSure(String str) {
                if (!StringUtil.isEmpty(str)) {
                    mNameList.set(position, str);
                    mAdapter.setDatas(mNameList);
                    mDialog.dismiss();
                } else {
                    showToast(R.string.please_input_name);
                }
            }
        });

        mEtName.setText(name);
        mEtName.setSelection(name.length());
        mDialog.show();
    }

    private void initData() {
        ArrayList<String> tempNameList = getIntent().getStringArrayListExtra(HotelConstants.HOTEL_GUEST_NAME);

        mCheckIn = getIntent().getStringExtra(HotelConstants.HOTEL_CHECK_IN);
        mCheckOut = getIntent().getStringExtra(HotelConstants.HOTEL_CHECK_OUT);
        mHotelId = getIntent().getStringExtra(HotelConstants.HOTEL_ID);
        mRoomId = getIntent().getStringExtra(HotelConstants.HOTEL_ROOM_ID);
        rateplanId = getIntent().getStringExtra(HotelConstants.HOTEL_RATEPLAN_ID);

        mNameList.clear();
        mNameList.addAll(tempNameList);

        mAdapter.setDatas(mNameList);

        mHotelRoomVM = ViewModelProviders.of(this).get(HotelRoomVM.class);

        mHotelRoomVM.getRatePlanStatusData().observe(this, new Observer<XmResource<RatePlanStatusBean>>() {
            @Override
            public void onChanged(@Nullable XmResource<RatePlanStatusBean> ratePlanStatusBeanXmResource) {

                if (ratePlanStatusBeanXmResource == null) {
                    return;
                }
                ratePlanStatusBeanXmResource.handle(new OnCallback<RatePlanStatusBean>() {
                    @Override
                    public void onSuccess(RatePlanStatusBean data) {

                        maxRoomCount = data.getMaxRoomCount();

                        //先判断房间库存是否大于目前房间数量
                        if (maxRoomCount != -1 && mNameList.size() >= maxRoomCount) {
                            showToast(R.string.hotel_no_room_add);
                            return;
                        }

                        if (data.isStatus()) {
                            addRoomMsg();
                        } else {
                            XMToast.showToast(BookUserMsgActivity.this, getString(R.string.hotel_no_room_add), getDrawable(R.drawable.toast_error));
                        }
                    }
                });

            }
        });
    }


    private void initDialog() {
        mDialog = new EditMsgDialog(this, R.style.dialog_fullscreen_hotel);
        mEtName = mDialog.getEditText();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putStringArrayListExtra(HotelConstants.HOTEL_NEW_GUEST_NAME, (ArrayList<String>) mAdapter.getDatas());
        setResult(RESULT_OK, intent);
        super.onBackPressed();

    }
}

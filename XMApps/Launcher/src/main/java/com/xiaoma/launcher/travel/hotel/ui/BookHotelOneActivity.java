package com.xiaoma.launcher.travel.hotel.ui;


/*  @project_name：  XMAgateOS
 *  @package_name：  com.xiaoma.launcher.travel.hotel.ui
 *  @file_name:      BookHotelActivity
 *  @author:         Rookie
 *  @create_time:    2019/1/4 16:21
 *  @description：   预订酒店房间             */

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.xiaoma.component.base.BaseActivity;
import com.xiaoma.launcher.R;
import com.xiaoma.launcher.common.constant.EventConstants;
import com.xiaoma.launcher.travel.hotel.adapter.HotelDetailAdapter;
import com.xiaoma.launcher.travel.hotel.constants.HotelConstants;
import com.xiaoma.launcher.travel.hotel.vm.HotelRoomVM;
import com.xiaoma.login.LoginManager;
import com.xiaoma.login.UserManager;
import com.xiaoma.model.XmResource;
import com.xiaoma.model.annotation.PageDescComponent;
import com.xiaoma.trip.hotel.response.HotelBean;
import com.xiaoma.trip.hotel.response.ImageBean;
import com.xiaoma.trip.hotel.response.RoomRatePlanBean;
import com.xiaoma.ui.toast.XMToast;
import com.xiaoma.ui.view.XmDividerDecoration;
import com.xiaoma.ui.view.XmScrollBar;
import com.xiaoma.utils.ListUtils;
import com.xiaoma.utils.StringUtil;

import java.util.ArrayList;
import java.util.List;
@PageDescComponent(EventConstants.PageDescribe.BookHotelOneActivityPagePathDesc)
public class BookHotelOneActivity extends BaseActivity {
    private RecyclerView rvHotelDetail;
    private XmScrollBar scrollBar;
    private HotelRoomVM mHotelRoomVM;
    private HotelDetailAdapter mHotelDetailAdapter;

    private static final String HOTEL_BEAN = "hotelBean";
    private static final String CHECK_IN = "checkIn";
    private static final String CHECK_OUT = "checkOut";
    private List<RoomRatePlanBean> mRoomRatePlanBeans;
    private RoomRatePlanBean mRoomRatePlanBean;
    private String mCheckIn;
    private String mCheckOut;


    private HotelBean mHotelBean;

    private LinearLayoutManager mLayoutManager;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_hotel_one);
        initView();
        initData();
    }


    private void initView() {


        rvHotelDetail = findViewById(R.id.rv_hotel_detail);
        scrollBar = findViewById(R.id.scroll_bar);
        mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rvHotelDetail.setLayoutManager(mLayoutManager);
        XmDividerDecoration decor = new XmDividerDecoration(this, DividerItemDecoration.HORIZONTAL);
        int horizontal = 60;
        int extra = 40;
        decor.setRect(horizontal, 0, horizontal, 0);
        decor.setExtraMargin(extra);
        rvHotelDetail.addItemDecoration(decor);

        mRoomRatePlanBeans = new ArrayList<>();
        mHotelDetailAdapter = new HotelDetailAdapter(R.layout.item_room_detail, mRoomRatePlanBeans);

        mHotelDetailAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                mRoomRatePlanBean = mRoomRatePlanBeans.get(position);
                if (mRoomRatePlanBean.isHasRoom()) {
                    toBookingSecondStep(mRoomRatePlanBean);
                } else {
                    XMToast.showToast(BookHotelOneActivity.this, getString(R.string.no_room_left), getDrawable(R.drawable.toast_error));
                }
            }
        });

        rvHotelDetail.setAdapter(mHotelDetailAdapter);
        scrollBar.setRecyclerView(rvHotelDetail);

        getNaviBar().getMiddleView().setImageDrawable(getDrawable(R.drawable.selector_iv_hotel_policy));
        getNaviBar().getMiddleView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mHotelBean == null || StringUtil.isEmpty(mHotelBean.getHotelId())) {
                    showToast(R.string.error_msg_hotel_id);
                    return;
                }
                Intent intent = new Intent(BookHotelOneActivity.this, BookHotelPolicyActivity.class);
                intent.putExtra(HotelConstants.HOTEL_ID, mHotelBean.getHotelId());
                startActivity(intent);
            }
        });
    }


    private void initData() {
        Intent intent = getIntent();
        mHotelBean = intent.getParcelableExtra(HOTEL_BEAN);
        mCheckIn = intent.getStringExtra(CHECK_IN);
        mCheckOut = intent.getStringExtra(CHECK_OUT);
        mHotelRoomVM = ViewModelProviders.of(this).get(HotelRoomVM.class);

        initVMObservers();

        //查询房间
        mHotelRoomVM.fetchRooms(mHotelBean.getHotelId(), mCheckIn, mCheckOut);
    }

    private void initVMObservers() {
        //查询剩余房间信息
        mHotelRoomVM.getRoomlData().observe(this, new Observer<XmResource<List<RoomRatePlanBean>>>() {

            @Override
            public void onChanged(@Nullable XmResource<List<RoomRatePlanBean>> listXmResource) {
                if (listXmResource == null) {
                    return;
                }
                listXmResource.handle(new OnCallback<List<RoomRatePlanBean>>() {
                    @Override
                    public void onSuccess(List<RoomRatePlanBean> data) {
                        mRoomRatePlanBeans.addAll(data);
                        mHotelDetailAdapter.setNewData(mRoomRatePlanBeans);
                    }

                });
            }
        });
    }


    public static void startBookHotel(Context context, HotelBean hotelBean, String checkIn, String checkOut) {
        Intent intent = new Intent(context, BookHotelOneActivity.class);
        intent.putExtra(HOTEL_BEAN, hotelBean);
        intent.putExtra(CHECK_IN, checkIn);
        intent.putExtra(CHECK_OUT, checkOut);
        context.startActivity(intent);
    }

    private void toBookingSecondStep(RoomRatePlanBean mRoomRatePlanBean) {
        Intent intent = new Intent(BookHotelOneActivity.this, BookHotelTwoActivity.class);

        intent.putExtra(HotelConstants.HOTEL_ID, mHotelBean.getHotelId());
        intent.putExtra(HotelConstants.HOTEL_NAME, mHotelBean.getHotelName());
        intent.putExtra(HotelConstants.HOTEL_CHECK_IN, mCheckIn);
        intent.putExtra(HotelConstants.HOTEL_CHECK_OUT, mCheckOut);
        intent.putExtra(HotelConstants.HOTEL_ADDRESS, mHotelBean.getAddress());
        intent.putExtra(HotelConstants.HOTEL_LAT, mHotelBean.getLat());
        intent.putExtra(HotelConstants.HOTEL_LON, mHotelBean.getLon());
        //默认预定房间数量为1
        intent.putExtra(HotelConstants.HOTEL_ROOM_COUNT, 1);
        intent.putExtra(HotelConstants.HOTEL_GUEST_NAME, UserManager.getInstance().getUser(LoginManager.getInstance().getLoginUserId()).getName());
        intent.putExtra(HotelConstants.HOTEL_BOOK_PHONE, UserManager.getInstance().getUser(LoginManager.getInstance().getLoginUserId()).getPhone());

        intent.putExtra(HotelConstants.HOTEL_ROOM_ID, mRoomRatePlanBean.getRoom().getRoomId());
        intent.putExtra(HotelConstants.HOTEL_ROOM_TYPE, mRoomRatePlanBean.getRoom().getRoomType());
        intent.putExtra(HotelConstants.HOTEL_RATEPLAN_ID, mRoomRatePlanBean.getRatePlan().getRatePlanId());
        intent.putExtra(HotelConstants.HOTEL_ORDER_IS_CANCEL, mRoomRatePlanBean.getRatePlan().isCancel());
        intent.putExtra(HotelConstants.HOTEL_CANCEL_TIME, mRoomRatePlanBean.getRatePlan().getLastCancelTime());
        intent.putExtra(HotelConstants.HOTEL_ROOM_PRICE, calculateRoomPrice(mRoomRatePlanBean));
        intent.putExtra(HotelConstants.HOTEL_ROOM_MSG, mRoomRatePlanBean.getRoomMsg());
        intent.putExtra(HotelConstants.HOTEL_PHONE, mHotelBean.getTelephone());
        intent.putExtra(HotelConstants.HOTEL_IMAGE, getHotelImageUrl(mHotelBean.getImages()));

        startActivity(intent);
    }

    /**
     * 计算房间总价
     *
     * @param mRoomRatePlanBean
     * @return
     */
    private String calculateRoomPrice(RoomRatePlanBean mRoomRatePlanBean) {

        //计算房间总价
        List<RoomRatePlanBean.PriceAndStatuListBean> priceAndStatuList = mRoomRatePlanBean.getPriceAndStatuList();
        float price = 0;

        if (!ListUtils.isEmpty(priceAndStatuList)) {
            for (RoomRatePlanBean.PriceAndStatuListBean priceAndStatuListBean : priceAndStatuList) {
                price += Float.valueOf(priceAndStatuListBean.getPrice());
            }

        }
        return StringUtil.keep2Decimal(price);

    }


    private String getHotelImageUrl(List<ImageBean> imageBeans) {

        if (!StringUtil.isEmpty(mHotelBean.getIconUrl())) {
            return mHotelBean.getIconUrl();
        }

        if (imageBeans == null || imageBeans.size() <= 0) {
            return "";
        }

        String hotelImageUrl = "";

        for (int i = 0; i < imageBeans.size(); i++) {

            if (getString(R.string.hotel_image).equals(imageBeans.get(i).getImageName())) {

                hotelImageUrl = imageBeans.get(i).getImageUrl();

                break;
            }
        }


        if (StringUtil.isEmpty(hotelImageUrl)) {
            hotelImageUrl = imageBeans.get(0).getImageUrl();
        }

        return hotelImageUrl;

    }
}

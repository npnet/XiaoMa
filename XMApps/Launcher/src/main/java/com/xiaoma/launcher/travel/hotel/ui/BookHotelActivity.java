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
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewStub;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.xiaoma.autotracker.XmTracker;
import com.xiaoma.autotracker.listener.XMAutoTrackerEventOnClickListener;
import com.xiaoma.autotracker.model.TrackerCountType;
import com.xiaoma.component.base.BaseActivity;
import com.xiaoma.image.ImageLoader;
import com.xiaoma.launcher.R;
import com.xiaoma.launcher.common.constant.EventConstants;
import com.xiaoma.launcher.common.manager.LauncherBlueToothPhoneManager;
import com.xiaoma.launcher.common.views.EditMsgDialog;
import com.xiaoma.launcher.travel.hotel.adapter.HotelDetailAdapter;
import com.xiaoma.launcher.travel.hotel.vm.HotelRoomVM;
import com.xiaoma.model.ItemEvent;
import com.xiaoma.model.XmResource;
import com.xiaoma.model.annotation.PageDescComponent;
import com.xiaoma.trip.hotel.response.BookOrderResult;
import com.xiaoma.trip.hotel.response.HotelBean;
import com.xiaoma.trip.hotel.response.RoomRatePlanBean;
import com.xiaoma.trip.orders.response.OrdersBean;
import com.xiaoma.ui.view.XmScrollBar;
import com.xiaoma.utils.ListUtils;
import com.xiaoma.utils.StringUtil;

import java.util.ArrayList;
import java.util.List;

@PageDescComponent(EventConstants.PageDescribe.BookHotelPolicyActivityPagePathDesc)

public class BookHotelActivity extends BaseActivity {

    private TextView tvStepFirst;
    private TextView tvStepSecond;
    private TextView tvStepThird;
    private LinearLayout llStepFirst;
    private RecyclerView rvHotelDetail;
    private XmScrollBar scrollBar;
    private ViewStub vsStepSecond;
    private ViewStub vsStepThird;
    private HotelRoomVM mHotelRoomVM;
    private HotelDetailAdapter mHotelDetailAdapter;
    private View mVsSecond;
    private View mVsThrid;

    //第三步view
    private TextView tvRoomName;
    private TextView tvRoomMsg;
    private TextView tvHotelName;
    private TextView tvBookCount;
    private TextView tvBookDate;
    private TextView tvBookCancel;
    private TextView tvBookCall;
    private ImageView ivPay;
    private Button btnPay;
    private Button btnCall;
    private Button btnGuide;
    private Button btnCancel;
    private TextView mTvBookTip;
    private View mBottomView;

    //第二步view
    private TextView tvHotelName2;
    private TextView tvRoomType;
    private TextView tvBookDate2;
    private TextView tvCall;
    private TextView tvRoomCount;
    private Button btnBook;

    private static final String HOTEL_BEAN = "hotelBean";
    private static final String CHECK_IN = "checkIn";
    private static final String CHECK_OUT = "checkOut";
    private List<RoomRatePlanBean> mRoomRatePlanBeans;
    private RoomRatePlanBean mRoomRatePlanBean;
    private String mHotelName;
    private String mCheckIn;
    private String mCheckOut;


    private HotelBean mHotelBean;
    private BookOrderResult mBookOrderResult;
    private OrdersBean mOrdersBean;

    private static final int REQUEST_CODE = 200;
    public static final String NAME_LIST = "name_list";
    public static final String ROOM_COUNT_MAX = "room_count_max";
    private ArrayList<String> mNameList;

    private String bookPhone = "";
    private EditMsgDialog mEditMsgDialog;
    private TextView mTvTotalPrice;

    private static final int PAY_SUCCESS_CODE = 3;

    private int pageNo = 1;
    private static final int PAGE_SIZE = 10;

    private int lastVisibleItem;
    private LinearLayoutManager mLayoutManager;

    private boolean hasMore;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_hotel);
        initView();
        initData();
    }


    private void initView() {

        tvStepFirst = findViewById(R.id.tap_one_hotel);
        tvStepSecond = findViewById(R.id.tap_two_hotel);
        tvStepThird = findViewById(R.id.tap_three_hotel);
        llStepFirst = findViewById(R.id.ll_step_first);
        rvHotelDetail = findViewById(R.id.rv_hotel_detail);
        scrollBar = findViewById(R.id.scroll_bar);
        vsStepSecond = findViewById(R.id.vs_step_second);
        vsStepThird = findViewById(R.id.vs_step_third);
        mTvBookTip = findViewById(R.id.tv_book_tip);
        mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rvHotelDetail.setLayoutManager(mLayoutManager);
        mRoomRatePlanBeans = new ArrayList<>();
        mHotelDetailAdapter = new HotelDetailAdapter(R.layout.item_room_detail, mRoomRatePlanBeans);

        mHotelDetailAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                mRoomRatePlanBean = mRoomRatePlanBeans.get(position);
                if (mRoomRatePlanBean.isHasRoom()) {
                    initSecondView();
                } else {
                    showToast(R.string.no_room_left);
                }
            }
        });

        rvHotelDetail.setAdapter(mHotelDetailAdapter);
        scrollBar.setRecyclerView(rvHotelDetail);
        rvHotelDetail.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
//                //当前屏幕停止滚动
//                // 滑动状态停止并且剩余少于两个item时，自动加载下一页
//                if (newState == RecyclerView.SCROLL_STATE_IDLE
//                        && lastVisibleItem + 2 >= mHotelDetailAdapter.getItemCount() && hasMore) {
//                    pageNo++;
//                    mHotelRoomVM.fetchRooms(mHotelBean.getHotelId(), mCheckIn, mCheckOut, pageNo);
//                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                //获取加载的最后一个可见视图在适配器的位置。
//                lastVisibleItem = mLayoutManager.findLastVisibleItemPosition();
                Log.d("onScrolled", "dx: " + dx + " dy: " + dy + " lastVisibleItem:" + lastVisibleItem);
            }
        });
//        mHotelDetailAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
//            @Override
//            public void onLoadMoreRequested() {
//                if (hasMore){
//                    mProgressBar.setVisibility(View.VISIBLE);
//                    pageNo++;
//                    mHotelRoomVM.fetchRooms(mHotelBean.getHotelId(), mCheckIn, mCheckOut, pageNo);
//                }
//            }
//        }, rvHotelDetail);

    }

    @SuppressLint("StringFormatMatches")
    private void initSecondView() {
        try {
            mVsSecond = vsStepSecond.inflate();

            tvHotelName2 = mVsSecond.findViewById(R.id.tv_hotel_name);
            tvRoomType = mVsSecond.findViewById(R.id.tv_room_type);
            tvBookDate2 = mVsSecond.findViewById(R.id.tv_book_date);
            tvCall = mVsSecond.findViewById(R.id.tv_call);
            tvRoomCount = mVsSecond.findViewById(R.id.tv_room_count);
            btnBook = mVsSecond.findViewById(R.id.btn_book);

            //预订房间
            btnBook.setOnClickListener(new XMAutoTrackerEventOnClickListener() {
                @Override
                public ItemEvent returnPositionEventMsg(View view) {
                    return null;
                }

                @Override
                public void onClick(View v) {
                    //如果入住人和手机号都不为空 就可以下单预订房间
                    if (!ListUtils.isEmpty(mNameList) && !StringUtil.isEmpty(bookPhone)) {
                        bookRoom();
                    } else {
                        showToast("请填写预订房间数和联系方式");
                    }

                }
            });
            //填写住房人信息
            tvRoomCount.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(BookHotelActivity.this, BookUserMsgActivity.class);
                    intent.putExtra(NAME_LIST, mNameList);
                    intent.putExtra(ROOM_COUNT_MAX, mRoomRatePlanBean.getRoomCount());
                    startActivityForResult(intent, REQUEST_CODE);
                }
            });
            //填写联系方式
            tvCall.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showPhoneDialog();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            llStepFirst.setVisibility(View.GONE);
            mVsSecond.setVisibility(View.VISIBLE);
            tvStepFirst.setTextColor(Color.WHITE);
            tvStepSecond.setTextColor(Color.parseColor("#00ffff"));
            tvStepThird.setTextColor(Color.WHITE);

            tvHotelName2.setText(String.format(getString(R.string.hotel_name), mHotelName));
            tvRoomType.setText(String.format(getString(R.string.room_type), mRoomRatePlanBean.getRoom().getRoomName()));
            tvBookDate2.setText(String.format(getString(R.string.check_date), mCheckIn, mCheckOut));
            btnBook.setText(String.format(getString(R.string.sure_book_room), mRoomRatePlanBean.getTotalPrice()));
            tvRoomCount.setText(String.format(getString(R.string.room_count), 0));
            tvCall.setText(String.format(getString(R.string.book_call), bookPhone));

        }
    }

    private void showPhoneDialog() {
        if (mEditMsgDialog == null) {
            mEditMsgDialog = new EditMsgDialog(this);
            //注：设置inputtype后 监听无效
            //mEditMsgDialog.getEditText().setInputType(EditorInfo.TYPE_CLASS_PHONE /*| EditorInfo.TYPE_CLASS_NUMBER*/);
//            mEditMsgDialog.setOnRoomEditListener(new EditMsgDialog.OnRoomEditListener() {
//                @Override
//                public void editInputStr(String str) {
//                    if (StringUtil.isMobileNO(str)) {
//                        bookPhone = str;
//                        tvCall.setText(String.format(getString(R.string.book_call), bookPhone));
//                        mEditMsgDialog.dismiss();
//                    } else {
//                        showToast("请输入正确号码");
//                    }
//                }
//            });
        }
        mEditMsgDialog.getEditText().setText(bookPhone);
        mEditMsgDialog.getEditText().setSelection(bookPhone.length());
        mEditMsgDialog.show();

    }


    @SuppressLint("StringFormatMatches")
    private void initThirdView() {
        llStepFirst.setVisibility(View.GONE);
        mVsSecond.setVisibility(View.GONE);
        try {
            mVsThrid = vsStepThird.inflate();
            tvRoomName = mVsThrid.findViewById(R.id.tv_room_name);
            tvRoomMsg = mVsThrid.findViewById(R.id.tv_room_msg);
            tvHotelName = mVsThrid.findViewById(R.id.tv_hotel_name);
            tvBookCount = mVsThrid.findViewById(R.id.tv_book_count);
            tvBookDate = mVsThrid.findViewById(R.id.tv_book_date);
            tvBookCancel = mVsThrid.findViewById(R.id.tv_book_cancel);
            tvBookCall = mVsThrid.findViewById(R.id.tv_book_call);
            ivPay = mVsThrid.findViewById(R.id.iv_pay);
            btnPay = mVsThrid.findViewById(R.id.btn_pay);
            btnCall = mVsThrid.findViewById(R.id.btn_call);
            btnGuide = mVsThrid.findViewById(R.id.btn_guide);
            btnCancel = mVsThrid.findViewById(R.id.btn_cancel);
            mBottomView = mVsThrid.findViewById(R.id.ll_bottom);
            mTvTotalPrice = mVsThrid.findViewById(R.id.tv_price);

            btnPay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    fetchPayResult();
                }
            });

            btnCall.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mHotelBean != null) {
                        LauncherBlueToothPhoneManager.getInstance().callPhone(mHotelBean.getReserve2());
                    }

                }
            });

            btnGuide.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //TODO:跳到导航
                    showToast("导航");
                }
            });

            btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    cancelOrder();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mVsThrid.setVisibility(View.VISIBLE);
            tvStepFirst.setTextColor(Color.WHITE);
            tvStepSecond.setTextColor(Color.WHITE);
            tvStepThird.setTextColor(Color.parseColor("#00ffff"));
            tvBookCount.setText(String.format(getString(R.string.book_room_count), mNameList.size()));
            tvHotelName.setText(String.format(getString(R.string.hotel_name), mHotelName));
            tvRoomName.setText(mRoomRatePlanBean.getRoom().getRoomName());
            tvRoomMsg.setText(mRoomRatePlanBean.getRoomMsg());
            tvBookDate.setText(String.format(getString(R.string.check_date), mCheckIn, mCheckOut));
            tvBookCancel.setText(String.format(getString(R.string.scan_code_cancel), mRoomRatePlanBean.getPriceAndStatuList().get(0).getLastCancelTime()));
            tvBookCall.setText(String.format(getString(R.string.book_call), bookPhone));
            mTvTotalPrice.setText(String.format(getString(R.string.scan_code_price), StringUtil.keep2Decimal(Float.parseFloat(mRoomRatePlanBean.getTotalPrice()) * mNameList.size())));
            if (mBookOrderResult != null) {
                ImageLoader.with(BookHotelActivity.this).load(mBookOrderResult.getQrCode()).error(R.drawable.ic_cover_hotel).into(ivPay);
            }
        }
    }


    private void initData() {
        mNameList = new ArrayList<>();

        Intent intent = getIntent();
        mHotelBean = intent.getParcelableExtra(HOTEL_BEAN);
        mCheckIn = intent.getStringExtra(CHECK_IN);
        mCheckOut = intent.getStringExtra(CHECK_OUT);
        mHotelName = mHotelBean.getHotelName();
        mHotelRoomVM = ViewModelProviders.of(this).get(HotelRoomVM.class);

        initVMObservers();

        //查询房间
//        mHotelRoomVM.fetchRooms(mHotelBean.getHotelId(), mCheckIn, mCheckOut, pageNo);
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
                        if (!ListUtils.isEmpty(data) && data.size() >= PAGE_SIZE) {
                            hasMore = true;
                        } else {
                            hasMore = false;
                            showToast(R.string.no_more_data);
                        }
                        mRoomRatePlanBeans.addAll(data);
                        mHotelDetailAdapter.setNewData(mRoomRatePlanBeans);
                    }

                });
            }
        });
        //取消订单结果
//        mHotelRoomVM.getCancelResult().observe(this, new Observer<XmResource<OrdersPerateBean>>() {
//            @Override
//            public void onChanged(@Nullable XmResource<OrdersPerateBean> ordersPerateBeanXmResource) {
//                if (ordersPerateBeanXmResource == null) {
//                    return;
//                }
//                ordersPerateBeanXmResource.handle(new OnCallback<OrdersPerateBean>() {
//                    @Override
//                    public void onSuccess(OrdersPerateBean data) {
//                        showToast(R.string.cancel_order_success);
//                        mTvBookTip.setText(R.string.already_cancel_order);
//                        mTvBookTip.setVisibility(View.VISIBLE);
//                        tvStepFirst.setVisibility(View.GONE);
//                        tvStepSecond.setVisibility(View.GONE);
//                        tvStepThird.setVisibility(View.GONE);
//                        btnCancel.setVisibility(View.GONE);
//                    }
//                });
//            }
//        });
        //获取订单数据,是否支付成功
        mHotelRoomVM.getOrdersData().observe(this, new Observer<XmResource<OrdersBean>>() {
            @Override
            public void onChanged(@Nullable XmResource<OrdersBean> listXmResource) {
                if (listXmResource == null) {
                    return;
                }
                listXmResource.handle(new OnCallback<OrdersBean>() {
                    @Override
                    public void onSuccess(OrdersBean data) {
                       /* orderStatusId
                        0	已取消
                        1	待确认
                        2	待支付
                        3	已完成
                        4	已删除*/
                        if (PAY_SUCCESS_CODE == data.getOrderStatusId()) {
                            mOrdersBean = data;
                            btnPay.setVisibility(View.GONE);
                            mBottomView.setVisibility(View.VISIBLE);
                            mTvTotalPrice.setVisibility(View.GONE);
                            mTvBookTip.setText(R.string.book_success);
                            mTvBookTip.setVisibility(View.VISIBLE);
                            tvStepFirst.setVisibility(View.GONE);
                            tvStepSecond.setVisibility(View.GONE);
                            tvStepThird.setVisibility(View.GONE);
                            ivPay.setVisibility(View.GONE);
                            tvBookCall.setVisibility(View.VISIBLE);
                            XmTracker.getInstance().uploadEvent(-1,
                                    TrackerCountType.BOOKHOTEL.getType());
                        } else {
                            showToast(R.string.rescan_to_pay);
                        }
                    }

                });

            }
        });
        //下订单结果
        mHotelRoomVM.getBookResult().observe(this, new Observer<XmResource<BookOrderResult>>() {
            @Override
            public void onChanged(@Nullable XmResource<BookOrderResult> stringXmResource) {
                if (stringXmResource == null) {
                    return;
                }
                stringXmResource.handle(new OnCallback<BookOrderResult>() {
                    @Override
                    public void onSuccess(BookOrderResult data) {
                        showToast(getString(R.string.order_generate));
                        mBookOrderResult = data;
                        initThirdView();
                    }

                });
            }
        });
    }

    private void cancelOrder() {
//        if (mOrdersBean != null) {
//            mHotelRoomVM.cancelOrder(Integer.valueOf(mOrdersBean.getOrderId()), mOrdersBean.getOrderStatusId());
//        }

    }

    /**
     * 查询订单支付结果
     */
    private void fetchPayResult() {
        if (mBookOrderResult != null) {
            mHotelRoomVM.fetchOrder(mBookOrderResult.getId());
        }
    }

    private void bookRoom() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < mNameList.size(); i++) {
            if (mNameList.size() - 1 == i) {
                sb.append(mNameList.get(i));
            } else {
                sb.append(mNameList.get(i)).append(",");
            }
        }

//        mHotelRoomVM.bookRoom(mHotelBean.getHotelId(), mHotelName, mRoomRatePlanBean.getRoom().getRoomId(),
//                mRoomRatePlanBean.getRatePlan().getRatePlanId(), mCheckIn, mCheckOut,
//                Float.parseFloat(mRoomRatePlanBean.getTotalPrice()) * mNameList.size() + "",
//                mNameList.size(), sb.toString(), bookPhone);
    }


    public static void startBookHotel(Context context, HotelBean hotelBean, String checkIn, String checkOut) {
        Intent intent = new Intent(context, BookHotelActivity.class);
        intent.putExtra(HOTEL_BEAN, hotelBean);
        intent.putExtra(CHECK_IN, checkIn);
        intent.putExtra(CHECK_OUT, checkOut);
        context.startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CODE) {
                mNameList = data.getStringArrayListExtra(NAME_LIST);
                tvRoomCount.setText(String.format(getString(R.string.room_count), mNameList.size()));
                btnBook.setText(getString((R.string.sure_book_room), Float.parseFloat(mRoomRatePlanBean.getTotalPrice()) * (mNameList.size() == 0 ? 1 : mNameList.size()) + ""));
            }
        }

    }
}

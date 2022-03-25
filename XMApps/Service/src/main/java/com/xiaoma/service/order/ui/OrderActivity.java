package com.xiaoma.service.order.ui;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xiaoma.autotracker.XmAutoTracker;
import com.xiaoma.component.base.BaseActivity;
import com.xiaoma.guide.listener.GuideCallBack;
import com.xiaoma.guide.utils.GuideConstants;
import com.xiaoma.guide.utils.GuideDataHelper;
import com.xiaoma.guide.utils.NewGuide;
import com.xiaoma.login.LoginManager;
import com.xiaoma.login.UserManager;
import com.xiaoma.mapadapter.manager.LocationManager;
import com.xiaoma.mapadapter.model.LatLng;
import com.xiaoma.mapadapter.model.LocationInfo;
import com.xiaoma.mapadapter.utils.MapUtil;
import com.xiaoma.model.User;
import com.xiaoma.model.XmResource;
import com.xiaoma.model.annotation.NormalOnClick;
import com.xiaoma.model.annotation.PageDescComponent;
import com.xiaoma.model.annotation.ResId;
import com.xiaoma.service.R;
import com.xiaoma.service.common.constant.EventBusTags;
import com.xiaoma.service.common.constant.EventConstants;
import com.xiaoma.service.common.manager.CarDataManager;
import com.xiaoma.service.common.manager.CarServiceNotificationHelper;
import com.xiaoma.service.common.model.BatchInfo;
import com.xiaoma.service.order.model.ProgramBean;
import com.xiaoma.service.order.model.ShopBean;
import com.xiaoma.service.order.vm.OrderVM;
import com.xiaoma.ui.dialog.XmDialog;
import com.xiaoma.ui.toast.XMToast;
import com.xiaoma.utils.NetworkUtils;
import com.xiaoma.utils.StringUtil;

import org.simple.eventbus.EventBus;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Thomas on 2018/11/13 0013
 * 保养/维修订单预约
 */
@PageDescComponent(EventConstants.PageDescribe.orderActivityPagePathDesc)
public class OrderActivity extends BaseActivity implements View.OnClickListener, LocationManager.ILocationChangedListener {
    private final String TAG = OrderActivity.class.getSimpleName();
    public static final int CHOOSE_SHOP_REQUEST_CODE = 100;
    public static final int CHOOSE_TIME_REQUEST_CODE = 102;
    public static final int CHOOSE_PROGRAM_REQUEST_CODE = 103;

    public static final String RESULT_DATE = "result_date";
    public static final String RESULT_TIME = "result_time";
    public static final String RESULT_WEEK = "result_week";

    public static final String RESULT_PROGRAM = "result_program";

    public static final String POSITION = "position";
    public static final String EXTRA_ARRIVE_DATE = "extra_arrive_date";
    public static final String EXTRA_POSITION = "extra_position";

    private TextView mArriveDateTv;
    private TextView mNameTv;
    private TextView mPhoneTv;
    private TextView mShopTv;
    private TextView mProgramTv;
    private Button btnSubmit;
    private LinearLayout storeLayout, timeLayout, phoneLayout;

    private String mArriveDate;
    private String mArriveTime;
    private String mArriveWeek;

    private String mTotalTime;
    private int mPosition = -1;
    private ShopBean mShopBean;
    private OrderVM mOrderVM;
    private List<ProgramBean> mSelectProgramList = new ArrayList<>();
    private double distance;
    private NewGuide newGuide;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        initView();
        showGuideWindow();
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocationManager.getInstance().setLocationChangedListener(this);
    }

    private void initView() {
        storeLayout = findViewById(R.id.ll_store);
        timeLayout = findViewById(R.id.ll_time);
        phoneLayout = findViewById(R.id.ll_phone);
        mArriveDateTv = findViewById(R.id.arrive_date);
        btnSubmit = findViewById(R.id.submit_order);
        mNameTv = findViewById(R.id.tv_name);
        mPhoneTv = findViewById(R.id.tv_phone);
        mShopTv = findViewById(R.id.shop_tv);
        mProgramTv = findViewById(R.id.program_tv);

        User user = UserManager.getInstance().getUser(LoginManager.getInstance().getLoginUserId());
        if (user != null) {
            mNameTv.setText(StringUtil.isNotEmpty(user.getName()) ? user.getName() : "");
            mPhoneTv.setText(StringUtil.isNotEmpty(user.getPhone()) ? user.getPhone() : "");
        } else {
            mNameTv.setText("");
            mPhoneTv.setText("");
        }

        mOrderVM = ViewModelProviders.of(this).get(OrderVM.class);
        mOrderVM.commitOrderDates().observe(this, new Observer<XmResource<String>>() {
            @Override
            public void onChanged(@Nullable XmResource<String> xmResource) {
                if (xmResource == null) {
                    return;
                }
                xmResource.handle(new OnCallback<String>() {
                    @Override
                    public void onSuccess(String data) {
                        CarServiceNotificationHelper.getInstance().handOrderNotification(OrderActivity.this, mArriveDate, mArriveTime, getString(R.string.car_service_order), getString(R.string.car_service_order_tip));
                        XMToast.toastSuccess(OrderActivity.this, R.string.commit_success, false);
                        finish();
                    }

                    @Override
                    public void onError(int code, String message) {
                        if (StringUtil.isEmpty(message)) {
                            XMToast.toastException(OrderActivity.this, R.string.commit_failed, false);

                        } else {
                            XMToast.toastException(OrderActivity.this, message, false);
                        }
                    }
                });
            }
        });
        storeLayout.setOnClickListener(this);
        timeLayout.setOnClickListener(this);
        phoneLayout.setOnClickListener(this);
        mProgramTv.setOnClickListener(this);
        btnSubmit.setOnClickListener(this);
        btnSubmit.setEnabled(false);
    }

    /**
     * 选择服务门店
     */
    public void chooseShop() {
        Intent intent = new Intent(this, Choose4sShopActivity.class);
        if (mShopBean != null) {
            intent.putExtra(Choose4sShopActivity.INTENT_SHOP, mShopBean);
            intent.putExtra(EXTRA_POSITION, mPosition);
        }
        startActivityForResult(intent, CHOOSE_SHOP_REQUEST_CODE);
    }

    /**
     * 选择到店时间
     */
    public void chooseTime() {
        Intent intent = new Intent(this, ChooseTimeDialog.class);
        if (!StringUtil.isEmpty(mTotalTime)) {
            intent.putExtra(EXTRA_ARRIVE_DATE, mTotalTime);
            intent.putExtra(EXTRA_POSITION, mPosition);
        }
        startActivityForResult(intent, CHOOSE_TIME_REQUEST_CODE);
    }

    /**
     * 选择项目
     */
    public void chooseProgram() {
        Intent intent = new Intent(this, ChooseProgramDialog.class);
        if (!mSelectProgramList.isEmpty()) {
            intent.putExtra(RESULT_PROGRAM, (Serializable) mSelectProgramList);
        }
        startActivityForResult(intent, CHOOSE_PROGRAM_REQUEST_CODE);
    }

    /**
     * 提交预约订单
     */
    public void submitOrder() {
        if (mSelectProgramList.isEmpty() || mShopBean == null || mArriveDate.isEmpty() || phoneTvIsEmpty()) {
            XMToast.toastException(this, R.string.fill_in_true_information, false);
            return;
        }

        if (!NetworkUtils.isConnected(this)) {
            XMToast.toastException(this, R.string.network_error, false);
            return;
        }

        StringBuilder id = new StringBuilder();
        for (int i = 0; i < mSelectProgramList.size(); i++) {
            if (i < mSelectProgramList.size() - 1) {
                id.append(mSelectProgramList.get(i).getId() + ",");
            } else {
                id.append(mSelectProgramList.get(i).getId());
            }
        }

        mOrderVM.commitOrder(id.toString(), CarDataManager.getInstance().getVinInfo(), mShopBean.getVPROVINCE(), mShopBean.getVCITY(),
                mShopBean.getVDEALER(), mArriveDate, mArriveTime, String.valueOf(mNameTv.getText()), String.valueOf(mPhoneTv.getText()));

        BatchInfo batchInfo = new BatchInfo();
        batchInfo.setH(mArriveDate);
        batchInfo.setI(mArriveTime);
        batchInfo.setJ(id.toString());
        batchInfo.setK(distance + "KM");
        XmAutoTracker.getInstance().onEvent(EventConstants.NormalClick.submitOrder, batchInfo.toJson(), TAG, EventConstants.PageDescribe.orderActivityPagePathDesc);

    }

    private boolean phoneTvIsEmpty() {
        if (getString(R.string.phone).equals(mPhoneTv.getText().toString())) {
            return true;
        }

        return String.valueOf(mPhoneTv.getText()).isEmpty();
    }


    /**
     * 修改电话
     */
    private void showAlterDialog() {
        View view = View.inflate(this, R.layout.dialog_alter_user_info, null);
        final EditText phoneEt = view.findViewById(R.id.phone_et);
        TextView sureBtn = view.findViewById(R.id.btn_sure);
        TextView cancelBtn = view.findViewById(R.id.btn_cancel);
        final XmDialog builder = new XmDialog.Builder(this)
                .setView(view)
                .setWidth(500)
                .setHeight(260)
                .create();
        sureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            @NormalOnClick({EventConstants.NormalClick.alterPhoneSure})
            public void onClick(View v) {
                if (StringUtil.isEmpty(phoneEt.getText().toString())) {
                    XMToast.toastException(OrderActivity.this, getString(R.string.phone_number_null), false);
                    return;
                }
                if (StringUtil.isEmpty(mArriveDate) || mSelectProgramList.isEmpty() || mShopBean == null) {
                    btnSubmit.setEnabled(false);

                } else {
                    btnSubmit.setEnabled(true);
                }
                mPhoneTv.setText(phoneEt.getText().toString());
                builder.dismiss();
            }
        });
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            @NormalOnClick({EventConstants.NormalClick.alterPhoneCancel})
            public void onClick(View v) {
                builder.dismiss();
            }
        });
        if (!StringUtil.isEmpty(mPhoneTv.getText().toString()) && !getString(R.string.phone).equals(mPhoneTv.getText().toString())) {
            phoneEt.setText(mPhoneTv.getText());
            phoneEt.setSelection(mPhoneTv.getText().length());
        }
        builder.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null) {
            return;
        }
        switch (requestCode) {
            //选择门店
            case CHOOSE_SHOP_REQUEST_CODE:
                mShopBean = (ShopBean) data.getSerializableExtra(Choose4sShopActivity.INTENT_SHOP);
                mPosition = data.getIntExtra(POSITION, -1);
                distance = mShopBean.getLocationDistance();
                mShopTv.setText(formatShopName(distance, mShopBean.getVDEALERNAME()));
                if (StringUtil.isEmpty(mArriveDate) || mSelectProgramList.isEmpty() || phoneTvIsEmpty()) {
                    btnSubmit.setEnabled(false);
                } else {
                    btnSubmit.setEnabled(true);
                }
                break;

            //选择到店时间
            case CHOOSE_TIME_REQUEST_CODE:
                mArriveDate = data.getStringExtra(RESULT_DATE);
                mArriveTime = data.getStringExtra(RESULT_TIME);
                mArriveWeek = data.getStringExtra(RESULT_WEEK);
                mTotalTime = mArriveDate + "\u3000" + mArriveTime;
                mPosition = data.getIntExtra(POSITION, -1);
                mArriveDateTv.setText(getString(R.string.arrive_time_total, mArriveDate, mArriveWeek, mArriveTime));
                if (mShopBean == null || mSelectProgramList.isEmpty() || phoneTvIsEmpty()) {
                    btnSubmit.setEnabled(false);

                } else {
                    btnSubmit.setEnabled(true);
                }
                break;

            //选择项目
            case CHOOSE_PROGRAM_REQUEST_CODE:
                mSelectProgramList = (List<ProgramBean>) data.getSerializableExtra(RESULT_PROGRAM);
                setSelectProgram(mSelectProgramList);
                if (StringUtil.isEmpty(mArriveDate) || mShopBean == null || phoneTvIsEmpty()) {
                    btnSubmit.setEnabled(false);

                } else {
                    btnSubmit.setEnabled(true);
                }
                break;
        }
    }

    private String formatShopName(double distance, String vdealername) {
        String str = "";
        String distanceStr = new DecimalFormat("0.00").format(distance);
        if (vdealername.length() > 4) {
            if (distance == 0) {
                str = str + vdealername.substring(0, 4) + "...\u3000\u3000" + getString(R.string.location_ing);
            } else {
                str = str + vdealername.substring(0, 4) + "...\u3000\u3000" + getString(R.string.shop_address_distance, distanceStr);
            }
        } else {
            str = vdealername + "\u3000\u3000" + distanceStr;
        }

        return str;
    }

    /**
     * 设置选择项目text
     *
     * @param list
     */
    public void setSelectProgram(List<ProgramBean> list) {
        mSelectProgramList = list;
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < mSelectProgramList.size(); i++) {
            if (i < mSelectProgramList.size() - 1) {
                builder.append(mSelectProgramList.get(i).getName() + "、");
            } else {
                builder.append(mSelectProgramList.get(i).getName());
            }
        }
        mProgramTv.setText(builder.toString());
    }

    @Override
    @NormalOnClick({EventConstants.NormalClick.launchPhone, EventConstants.NormalClick.launchProgram,
            EventConstants.NormalClick.launchShop, EventConstants.NormalClick.launchDate, EventConstants.NormalClick.submitOrder})
    @ResId({R.id.ll_phone, R.id.program_tv, R.id.ll_store, R.id.ll_time, R.id.submit_order})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_phone:
                //showAlterDialog();
                break;
            case R.id.program_tv:
                chooseProgram();
                break;
            case R.id.ll_store:
                chooseShop();
                break;
            case R.id.ll_time:
                chooseTime();
                break;
            case R.id.submit_order:
                submitOrder();
                break;
        }

    }


    @Override
    public void onLocationChange(LocationInfo locationInfo) {
        if (mShopBean != null) {
            distance = MapUtil.calculateLineDistance(new LatLng(locationInfo.getLatitude(), locationInfo.getLongitude()),
                    new LatLng(mShopBean.getPATHLAT(), mShopBean.getPATHLNG())) / 1000;
            mShopTv.setText(formatShopName(distance, mShopBean.getVDEALERNAME()));
        }

    }

    private void showGuideWindow() {
        if (!GuideDataHelper.shouldShowGuide(this, GuideConstants.SERVICE_SHOWED, GuideConstants.SERVICE_GUIDE_FIRST, false))
            return;
//        final View view = ((ViewGroup) ((ViewGroup) getNaviBar().getChildAt(0)).getChildAt(0)).getChildAt(0);
        final View view = getNaviBar().getBackView();
        if (view == null) return;
        view.post(new Runnable() {
            @Override
            public void run() {
                Rect viewRect = NewGuide.getViewRect(view);
                Rect targetRect = new Rect(viewRect.left, viewRect.top + (viewRect.height() / 2 - 60), viewRect.right, viewRect.top + (viewRect.height() / 2 + 60));
                newGuide = NewGuide.with(OrderActivity.this)
                        .setLebal(GuideConstants.SERVICE_SHOWED)
                        .setTargetView(view)
                        .setGuideLayoutId(R.layout.guide_view_order)
                        .setNeedHande(true)
                        .setNeedShake(true)
                        .needMoveUpALittle(true)
                        .setViewWaveIdOne(R.id.iv_wave_one)
                        .setViewWaveIdTwo(R.id.iv_wave_two)
                        .setViewWaveIdThree(R.id.iv_wave_three)
                        .setViewHandId(R.id.iv_gesture)
                        .setViewSkipId(R.id.tv_guide_skip)
                        .setHighLightRect(targetRect)
                        .setTargetViewTranslationX(0.02f)
                        .setCallBack(new GuideCallBack() {
                            @Override
                            public void onHighLightClicked() {
                                dismissGuideWindow();
                                EventBus.getDefault().post("", EventBusTags.SHOW_LAST_GUIDE);
                                onBackPressed();
                            }
                        })
                        .build();
                newGuide.showGuide();
            }
        });
    }

    private void dismissGuideWindow() {
        if (newGuide != null) {
            newGuide.dismissGuideWindow();
            newGuide = null;
        }
    }
}

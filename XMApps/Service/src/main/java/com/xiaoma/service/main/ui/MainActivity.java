package com.xiaoma.service.main.ui;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xiaoma.autotracker.XmAutoTracker;
import com.xiaoma.component.base.BaseActivity;
import com.xiaoma.guide.utils.GuideConstants;
import com.xiaoma.guide.utils.GuideDataHelper;
import com.xiaoma.guide.utils.NewGuide;
import com.xiaoma.login.LoginManager;
import com.xiaoma.login.business.ui.ChooseUserActivity;
import com.xiaoma.model.XmResource;
import com.xiaoma.model.annotation.NormalOnClick;
import com.xiaoma.model.annotation.PageDescComponent;
import com.xiaoma.model.annotation.ResId;
import com.xiaoma.service.BuildConfig;
import com.xiaoma.service.R;
import com.xiaoma.service.common.constant.EventBusTags;
import com.xiaoma.service.common.constant.EventConstants;
import com.xiaoma.service.common.constant.ServiceConstants;
import com.xiaoma.service.common.manager.CarDataManager;
import com.xiaoma.service.common.manager.IBCallManager;
import com.xiaoma.service.common.model.BatchInfo;
import com.xiaoma.service.common.service.TboxCallWindowService;
import com.xiaoma.service.main.model.HotLineBean;
import com.xiaoma.service.main.model.MaintenancePeriodBean;
import com.xiaoma.service.main.vm.MainVM;
import com.xiaoma.service.order.ui.OrderActivity;
import com.xiaoma.service.order.ui.OrderListActivity;
import com.xiaoma.service.plan.ui.MaintenancePlanActivity;
import com.xiaoma.ui.dialog.ConfirmDialog;
import com.xiaoma.ui.dialog.XmDialog;
import com.xiaoma.ui.toast.XMToast;
import com.xiaoma.update.manager.AppUpdateCheck;
import com.xiaoma.utils.NetworkUtils;
import com.xiaoma.utils.StringUtil;
import com.xiaoma.utils.apptool.AppObserver;
import com.xiaoma.utils.log.KLog;
import com.xiaoma.utils.logintype.callback.OnBlockCallback;
import com.xiaoma.utils.logintype.constant.LoginCfgConstant;
import com.xiaoma.utils.logintype.manager.LoginType;
import com.xiaoma.utils.logintype.manager.LoginTypeManager;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import static com.xiaoma.guide.utils.NewGuide.RIGHT_AND_TOP;

/**
 * Created by Thomas on 2018/11/13 0013
 * service index
 */

@PageDescComponent(EventConstants.PageDescribe.mainActivityPagePathDesc)
public class MainActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "MainActivity";
    private static final int REQ_CODE_LOGIN = 1;
    private static final String INTENT_DATA = "intent_data";
    private TextView tvKiloDetail, tvMaintainTitle, tvMaintainTips, tvCheckMore, tvTimeDetail;
    private ConstraintLayout clKilometre, clTime, clRescueLine, clServiceLine, clPlan, mLayoutOrder;
    private ImageView divide;
    private Button btnMaintain;
    private MainVM mainVM;
    private HotLineBean hotLineBean;
    private int NEED_MAINTENANCE_STATUS = 0;
    private double mDriveDistance = CarDataManager.getInstance().getDriveDistance();
    //todo test
    private File file;
    private static final String DISTANCE_TXT = "distance.txt";
    private LinearLayout mServiceMainLayout;
    private String maintainKm = "";
    private String maintainTime = "";
    private NewGuide newGuide;
    private int ICALL = 1, BCALL = 2;
    private boolean canshow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getWindow().setBackgroundDrawableResource(R.drawable.bg_common);
        setContentView(R.layout.activity_main);
        if (!LoginManager.getInstance().isUserLogin()) {
            ChooseUserActivity.start(this, true);
            finish();
            return;
        }
        initView();
        initData();
        requestFloatWindowPermission();
        EventBus.getDefault().register(this);
        guideJustifyNetWork();
//        initCarNotificationService();
    }

    /*private void initCarNotificationService() {
        ThreadDispatcher.getDispatcher().postOnMainDelayed(new Runnable() {
            @Override
            public void run() {
                MainActivity.this.startService(new Intent(MainActivity.this, CarNotificationService.class));
            }
        }, 3000);
    }*/

    private void initData() {
        initFile();
        mainVM = ViewModelProviders.of(this).get(MainVM.class);
        mainVM.getmUploadDriveDistance().observe(this, new Observer<XmResource<String>>() {
            @Override
            public void onChanged(@Nullable XmResource<String> resource) {
                if (resource == null) {
                    return;
                }
                resource.handle(new OnCallback<String>() {
                    @Override
                    public void onSuccess(String data) {
                        mainVM.fetchPeriodStatus(CarDataManager.getInstance().getVinInfo());
                        KLog.d(TAG, "Upload Distance onSuccess");
                    }

                    @Override
                    public void onError(int code, String message) {
                        super.onError(code, message);
                        KLog.d(TAG, "Upload Distance onError " + message);
                        showNoNetView();
                    }
                });
            }
        });
        mainVM.getPeriodData().observe(this, new Observer<XmResource<MaintenancePeriodBean>>() {
            @Override
            public void onChanged(@Nullable XmResource<MaintenancePeriodBean> resource) {
                if (resource == null) {
                    return;
                }
                resource.handle(new OnCallback<MaintenancePeriodBean>() {
                    @Override
                    public void onSuccess(MaintenancePeriodBean data) {
                        settingPeriodData(data);
                    }

                    @Override
                    public void onError(int code, String message) {
                        KLog.d(TAG, "Upload PeriodData onError " + message);
                        showNoNetView();
                    }
                });
            }
        });
        mainVM.getHotLineData().observe(this, new Observer<XmResource<HotLineBean>>() {
            @Override
            public void onChanged(@Nullable XmResource<HotLineBean> resource) {
                if (resource == null) {
                    return;
                }
                resource.handle(new OnCallback<HotLineBean>() {
                    @Override
                    public void onSuccess(HotLineBean data) {
                        hotLineBean = data;
                    }

                    @Override
                    public void onError(int code, String message) {
                        KLog.d(TAG, "Upload HotLineData onError " + message);
                        showNoNetView();
                    }
                });
            }
        });

        mainVM.uploadDriveDistance(mDriveDistance);

        mainVM.fetchHotLineNumber();
    }

    /**
     * 车机里程测试数据
     */
    private void initFile() {
        //todo 暂且测试里程功能
        if (!BuildConfig.DEBUG) {
            return;
        }
        // 在SD卡目录下创建文件
        file = new File(Environment.getExternalStorageDirectory(), DISTANCE_TXT);
        if (!file.exists()) {
            try {
                file.createNewFile();
                Log.d(TAG, "createNewFile...");

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Log.d(TAG, "file.exist...");
        }
        // 读取SD卡文件里面的内容
        try {
            FileReader fr = new FileReader(Environment.getExternalStorageDirectory() + "/" + DISTANCE_TXT);
            Log.d(TAG, "file path:" + Environment.getExternalStorageDirectory() + "/" + DISTANCE_TXT);
            BufferedReader r = new BufferedReader(fr);
            String result = r.readLine();
            Log.d(TAG, "result:" + result);
            //todo 获取车机里程数
            try {
                mDriveDistance = Double.parseDouble(result);
            } catch (Exception e) {
                mDriveDistance = 0;
            }
            Log.d(TAG, "mDriveDistance:" + mDriveDistance);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void noNetworkOnRetry() {
        super.noNetworkOnRetry();
        mainVM.uploadDriveDistance(mDriveDistance);

        mainVM.fetchHotLineNumber();
    }

    @Override
    protected void onResume() {
        super.onResume();
        AppUpdateCheck.getInstance().checkAppUpdate(getPackageName(), getApplication());
    }

    private void settingPeriodData(MaintenancePeriodBean data) {
        mServiceMainLayout.setVisibility(View.VISIBLE);
        if (data.getRemainingKM() == NEED_MAINTENANCE_STATUS || data.getRemainingTime() == NEED_MAINTENANCE_STATUS) {
            //发现有n项保养项
            tvMaintainTitle.setVisibility(View.GONE);
            clKilometre.setVisibility(View.GONE);
            clTime.setVisibility(View.GONE);
            divide.setVisibility(View.GONE);
            tvMaintainTips.setVisibility(View.VISIBLE);
            tvMaintainTips.setText(String.format(getString(R.string.maintain_count_text), data.getUpkeepSize()));
            tvCheckMore.setVisibility(View.VISIBLE);
            btnMaintain.setVisibility(View.VISIBLE);
            showGuideWindow();
            return;
        } else {
            //距离保养还有
            tvMaintainTitle.setText(R.string.maintain_distance);
            tvMaintainTitle.setVisibility(View.VISIBLE);
            clKilometre.setVisibility(View.VISIBLE);
            clTime.setVisibility(View.VISIBLE);
            divide.setVisibility(View.VISIBLE);
            btnMaintain.setVisibility(View.VISIBLE);
            tvMaintainTips.setVisibility(View.GONE);
            tvCheckMore.setVisibility(View.GONE);
            maintainKm = String.valueOf(data.getRemainingKM()) + data.getKMUnit();
            maintainTime = String.valueOf(data.getRemainingTime()) + data.getTimeUnit();
        }
        //保养即将到期
        if (data.getRemainingKM() <= ServiceConstants.DISTANCE_CAUTION_STATUS) {
            tvMaintainTitle.setText(R.string.maintain_time_nearly);
            tvKiloDetail.setTextColor(getResources().getColor(R.color.red));
        }
        //保养即将到期
        if (getString(R.string.day).equals(data.getTimeUnit()) && data.getRemainingTime() <= ServiceConstants.TIME_CAUTION_STATUS) {
            tvMaintainTitle.setText(R.string.maintain_time_nearly);
            tvTimeDetail.setTextColor(getResources().getColor(R.color.red));
        }
        tvKiloDetail.setText(maintainKm);
        tvTimeDetail.setText(maintainTime);
        showGuideWindow();
    }

    private void initView() {
        mServiceMainLayout = findViewById(R.id.service_main_layout);
        tvKiloDetail = findViewById(R.id.tv_kilometre_detail);
        tvTimeDetail = findViewById(R.id.tv_time_detail);
        tvMaintainTitle = findViewById(R.id.tv_maintain_title);
        tvMaintainTips = findViewById(R.id.tv_maintain_tips);
        tvCheckMore = findViewById(R.id.tv_check_more);
        clKilometre = findViewById(R.id.layout_kilometre);
        clTime = findViewById(R.id.layout_time);
        divide = findViewById(R.id.divide);
        mLayoutOrder = findViewById(R.id.layout_order);
        clRescueLine = findViewById(R.id.layout_rescue_line);
        clServiceLine = findViewById(R.id.layout_service_line);
        clPlan = findViewById(R.id.layout_plan);
        btnMaintain = findViewById(R.id.btn_maintain);
        btnMaintain.setOnClickListener(this);
        clRescueLine.setOnClickListener(this);
        clServiceLine.setOnClickListener(this);
        tvCheckMore.setOnClickListener(this);
        clPlan.setOnClickListener(this);
        mLayoutOrder.setOnClickListener(this);

        //todo 暂时模拟里程数方便测试阶段验证
        TextView tvUpdateDistance = findViewById(R.id.update_distance);
        tvUpdateDistance.setOnClickListener(this);
        tvUpdateDistance.setVisibility(BuildConfig.DEBUG ? View.VISIBLE : View.GONE);
    }

    private void requestFloatWindowPermission() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(MainActivity.this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, 10);
            } else {
                canshow = true;
            }
        } else {
            canshow = true;
        }

    }


    @Override
    @NormalOnClick({EventConstants.NormalClick.goMaintain, EventConstants.NormalClick.optionsDetail, EventConstants.NormalClick.rescueLine,
            EventConstants.NormalClick.serviceLine, EventConstants.NormalClick.carPlan, EventConstants.NormalClick.carOrder,})
    @ResId({R.id.btn_maintain, R.id.tv_check_more, R.id.layout_rescue_line, R.id.layout_service_line, R.id.layout_plan, R.id.layout_order})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_maintain:
                if (!canUse(LoginCfgConstant.SERVICE_INIT_ORDER)) return;
                startActivity(OrderActivity.class);
                BatchInfo batchInfo = new BatchInfo();
                batchInfo.setH(maintainKm);
                batchInfo.setJ(maintainTime);
                XmAutoTracker.getInstance().onEvent(EventConstants.NormalClick.goMaintain, batchInfo.toJson(), TAG, EventConstants.PageDescribe
                        .mainActivityPagePathDesc);
                dismissGuideWindow();
                break;
            case R.id.tv_check_more:
                if (!canUse(LoginCfgConstant.SERVICE_CHECK_MORE)) return;
                startActivity(MaintainListActivity.class);
                break;
            case R.id.layout_rescue_line:
//                if (hotLineBean == null) {
//                    XMToast.toastException(this, getResources().getString(R.string.get_phone_failed), false);
//                    return;
//                }
                showDialog(getString(R.string.rescue_line), hotLineBean.getBCALL(), BCALL);
                break;
            case R.id.layout_service_line:
//                if (hotLineBean == null) {
//                    XMToast.toastException(this, getResources().getString(R.string.get_phone_failed), false);
//                    return;
//                }
                showDialog(getString(R.string.service_line), hotLineBean.getICALL(), ICALL);
                break;
            case R.id.layout_plan:
                if (!canUse(LoginCfgConstant.SERVICE_CAR_MAINTENANCE_PLAN)) return;
                startActivity(MaintenancePlanActivity.class);
                dismissGuideWindow();
                break;
            case R.id.layout_order:
                if (!canUse(LoginCfgConstant.SERVICE_CHECK_ORDER)) return;
                startActivity(OrderListActivity.class);
                break;
            //todo 暂且测试里程数功能
            case R.id.update_distance:
                showDialog();
                break;
        }
    }

    private boolean canUse(String condition) {
        if (TextUtils.isEmpty(condition)) return true;
        return LoginTypeManager.getInstance().judgeUse(condition,
                new OnBlockCallback() {
                    @Override
                    public boolean onShowToast(LoginType loginType) {
                        XMToast.showToast(MainActivity.this, loginType.getPrompt(MainActivity.this));
                        return true;
                    }
                });
    }


    private void showDialog() {
        //todo 暂且测试里程数功能
        View view = View.inflate(this, R.layout.test_dialog_update_distance, null);
        final EditText edDistance = view.findViewById(R.id.edit_distance);
        edDistance.setText(mDriveDistance + "");
        Button btCommit = view.findViewById(R.id.commit);
        final XmDialog builder = new XmDialog.Builder(this)
                .setView(view)
                .setWidth(500)
                .setHeight(260)
                .create();
        btCommit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String distanceStr = edDistance.getText().toString();
                if (StringUtil.isEmpty(distanceStr)) {
                    XMToast.showToast(getBaseContext(), "not empty please input");
                    return;
                }
                try {
                    mDriveDistance = Double.parseDouble(distanceStr);

                } catch (Exception e) {
                    XMToast.showToast(getBaseContext(), "please input number");
                    return;
                }
                // 在SD卡目录下的文件，写入内容
                try {
                    FileWriter fw = new FileWriter(file);
                    fw.write(distanceStr);
                    fw.close();
                    XMToast.showToast(getBaseContext(), "update distance success,please restart app");
                    builder.dismiss();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        builder.show();
    }

    private void showDialog(String title, final String message, final int callType) {
        final ConfirmDialog dialog = new ConfirmDialog(this);
        dialog.setContent(title)
                .setPositiveButton(getString(R.string.dial), new View.OnClickListener() {
                    @Override
                    @NormalOnClick({EventConstants.NormalClick.BCALLSure})
                    public void onClick(View v) {
                        //todo  待接口拨打蓝牙电话  phone = message

                        if (CarDataManager.getInstance().getIsBluetoothCall()) {
                            XMToast.showToast(MainActivity.this, R.string.please_hang_up_the_current_call);
                            dialog.dismiss();
                            return;
                        }

                        if (IBCallManager.getInstance().isIBCall()) {
                            XMToast.showToast(MainActivity.this, R.string.please_hang_up_the_current_call);
                            dialog.dismiss();
                            return;
                        }

                        stopService(new Intent(MainActivity.this, TboxCallWindowService.class));
                        if (callType == ICALL) {

                            int code = CarDataManager.getInstance().operateICall();
                            if (canshow && code == 0) {
                                Intent intent = new Intent(MainActivity.this, TboxCallWindowService.class);
                                intent.putExtra("call_type", callType);
                                startService(intent);
                            } else {
                                showToast(R.string.call_faliue);
                            }
                        } else if (callType == BCALL) {
                            int code = CarDataManager.getInstance().operateBcall();
                            if (canshow && code == 0) {
                                Intent intent = new Intent(MainActivity.this, TboxCallWindowService.class);
                                intent.putExtra("call_type", callType);
                                startService(intent);
                            } else {
                                showToast(R.string.call_faliue);
                            }
                        }
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(getString(R.string.dialog_cancel), new View.OnClickListener() {
                    @Override
                    @NormalOnClick({EventConstants.NormalClick.BCALLCancel})
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
        if (mainVM != null) {
            mainVM.uploadDriveDistance(mDriveDistance);
        }
        AppObserver.getInstance().closeAllActivitiesAndExit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        KLog.i(TAG, StringUtil.format("onActivityResult -> [ requestCode: %d, resultCode: %d, data: %s ]", requestCode, resultCode, data));
        if (REQ_CODE_LOGIN == requestCode) {
            if (RESULT_OK == resultCode) {
                KLog.i(TAG, StringUtil.format("onActivityResult -> Login Success, do Hx login...", requestCode, resultCode, data));
                initView();
                initData();
            } else {
                KLog.i(TAG, StringUtil.format("onActivityResult -> Login cancel, finish !", requestCode, resultCode, data));
                finish();
            }
        }

        if (requestCode == 10) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (Settings.canDrawOverlays(this)) {
                    // SYSTEM_ALERT_WINDOW permission not granted...
                    canshow = true;
                }
            }
        }
    }

    private void showGuideWindow() {
        if (!GuideDataHelper.shouldShowGuide(this, GuideConstants.SERVICE_SHOWED, GuideConstants.SERVICE_GUIDE_FIRST, true))
            return;
        View targetView = findViewById(R.id.icon_plan);
        newGuide = NewGuide.with(MainActivity.this)
                .setLebal(GuideConstants.SERVICE_SHOWED)
                .setTargetView(targetView)
                .setTargetRect(NewGuide.getViewRect(targetView))
                .setGuideLayoutId(R.layout.guide_view_main_two)
                .setNeedHande(true)
                .setNeedShake(true)
                .setViewWaveIdOne(R.id.iv_wave_one)
                .setViewWaveIdTwo(R.id.iv_wave_two)
                .setViewWaveIdThree(R.id.iv_wave_three)
                .setViewHandId(R.id.iv_gesture)
                .setViewSkipId(R.id.tv_guide_skip)
                .setTargetViewTranslationX(0.02f)
                .build();
        newGuide.showGuide();
    }

    @Subscriber(tag = EventBusTags.SHOW_SECOND_GUIDE)
    public void showSecondGuideWindow(String s) {
        if (!GuideDataHelper.shouldShowGuide(this, GuideConstants.SERVICE_SHOWED, GuideConstants.SERVICE_GUIDE_FIRST, false))
            return;
        btnMaintain.post(new Runnable() {
            @Override
            public void run() {
                Rect viewRect = NewGuide.getViewRect(btnMaintain);
                Rect targetRect = new Rect(viewRect.left, viewRect.top - 10, viewRect.right, viewRect.bottom - 10);
                newGuide = NewGuide.with(MainActivity.this)
                        .setLebal(GuideConstants.SERVICE_SHOWED)
                        .setTargetView(btnMaintain)
                        .setTargetRect(targetRect)
                        .setGuideLayoutId(R.layout.guide_view_main_one)
                        .setNeedHande(true)
                        .setNeedShake(true)
                        .setViewWaveIdOne(R.id.iv_wave_one)
                        .setViewWaveIdTwo(R.id.iv_wave_two)
                        .setViewWaveIdThree(R.id.iv_wave_three)
                        .setViewHandId(R.id.iv_gesture)
                        .setHandLocation(RIGHT_AND_TOP)
                        .setViewSkipId(R.id.tv_guide_skip)
                        .build();
                newGuide.showGuide();
                GuideDataHelper.setFirstGuideFalse(GuideConstants.SERVICE_SHOWED);
            }
        });
    }

    @Subscriber(tag = EventBusTags.SHOW_LAST_GUIDE)
    public void showLastGuide(String s) {
        if (!GuideDataHelper.shouldShowGuide(this, GuideConstants.SERVICE_SHOWED, GuideConstants.SERVICE_GUIDE_FIRST, false))
            return;
        NewGuide.with(this)
                .setLebal(GuideConstants.SERVICE_SHOWED)
                .build()
                .showLastGuide();
    }

    private void dismissGuideWindow() {
        if (newGuide != null) {
            newGuide.dismissGuideWindow();
            newGuide = null;
        }
    }

    private void guideJustifyNetWork() {
        if (!NetworkUtils.isConnected(this) && GuideDataHelper.shouldShowGuide(this, GuideConstants.SERVICE_SHOWED, GuideConstants.SERVICE_GUIDE_FIRST, true)) {
            NewGuide.with(this)
                    .setLebal(GuideConstants.SERVICE_SHOWED)
                    .build()
                    .showGuideFinishWindowDueToNetError();
        }
    }

    @Override
    public void onBackPressed() {
        if (IBCallManager.getInstance().isIBCall()) {
            moveTaskToBack(true);
            return;
        }
        finish();
    }
}

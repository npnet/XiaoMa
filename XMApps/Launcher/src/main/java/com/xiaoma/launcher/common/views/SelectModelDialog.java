package com.xiaoma.launcher.common.views;


/*  @project_name：  XMAgateOS
 *  @package_name：  com.xiaoma.launcher.common.views
 *  @file_name:      SelectModelDialog
 *  @author:         Rookie
 *  @create_time:    2019/3/27 10:48
 *  @description：   TODO             */

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.xiaoma.launcher.R;
import com.xiaoma.launcher.common.constant.EventConstants;
import com.xiaoma.launcher.common.constant.LauncherConstants;
import com.xiaoma.launcher.common.manager.UploadMqttDataManager;
import com.xiaoma.launcher.common.model.SelectModeBean;
import com.xiaoma.launcher.schedule.utils.DateUtil;
import com.xiaoma.mapadapter.manager.LocationManager;
import com.xiaoma.model.annotation.NormalOnClick;
import com.xiaoma.model.annotation.ResId;
import com.xiaoma.thread.ThreadDispatcher;
import com.xiaoma.utils.log.KLog;
import com.xiaoma.utils.tputils.TPUtils;

import org.simple.eventbus.EventBus;

import java.util.Calendar;

public class SelectModelDialog extends BaseRecommendDialog implements View.OnClickListener {


    private LinearLayout llLife;
    private LinearLayout llWork;
    private LinearLayout llTravel;
    private LinearLayout llQuiet;
    private int model = LauncherConstants.LIVE_MODEL;


    public SelectModelDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    public int getContentViewId() {
        return R.layout.view_dialog_model;
    }

    @Override
    public void initView() {
        llLife = findViewById(R.id.ll_life);
        llWork = findViewById(R.id.ll_work);
        llTravel = findViewById(R.id.ll_travel);
        llQuiet = findViewById(R.id.ll_quiet);
        llLife.setOnClickListener(this);
        llWork.setOnClickListener(this);
        llTravel.setOnClickListener(this);
        llQuiet.setOnClickListener(this);
        //获取默认模式
        model = TPUtils.get(mContext, LauncherConstants.CAR_MODEL, LauncherConstants.LIVE_MODEL);
        if (model == LauncherConstants.QUIET_MODEL) {
            //如果上次保存的是静静模式，就不需要赋值判断
            initModelView();
            return;
        }
        int day = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
        //周一~周五，默认为工作模式；周六~周日，默认为休闲模式
        if (day == Calendar.SUNDAY || day == Calendar.SATURDAY) {
            initModeByDay(LauncherConstants.CAR_MODEL_REST, LauncherConstants.LIVE_MODEL);
        } else {
            initModeByDay(LauncherConstants.CAR_MODEL_NORMAL, LauncherConstants.WORK_MODEL);
        }
        initModelView();
        //保存下当前的模式
        TPUtils.put(mContext, LauncherConstants.CAR_MODEL, model);
    }

    private void initModeByDay(String carModel, int liveModel) {
        SelectModeBean selectModeBean = TPUtils.getObject(mContext, carModel, SelectModeBean.class);
        if (selectModeBean == null) {
            model = liveModel;
        } else {
            //因为calendar 一周是从周日到周六，所以不能判断周六周日是否是同一周
            if (LauncherConstants.CAR_MODEL_NORMAL.equals(carModel)) {
                if (DateUtil.isSameWeekWithToday(selectModeBean.getCalendar(), Calendar.getInstance())) {
                    model = selectModeBean.getModel();
                } else {
                    model = liveModel;
                }
            } else {
                if (DateUtil.getTimeDistance(selectModeBean.getCalendar().getTime(), Calendar.getInstance().getTime()) <= 1) {
                    model = selectModeBean.getModel();
                } else {
                    model = liveModel;
                }
            }
        }
    }

    @Override
    protected void initWindow() {
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.gravity = Gravity.CENTER;
        lp.width = 665;
        lp.height = 375;
        getWindow().setAttributes(lp);
    }

    private void initModelView() {
        llLife.setSelected(model == LauncherConstants.LIVE_MODEL);
        llWork.setSelected(model == LauncherConstants.WORK_MODEL);
        llTravel.setSelected(model == LauncherConstants.TRAVEL_MODEL);
        llQuiet.setSelected(model == LauncherConstants.QUIET_MODEL);
    }

    @Override
    @NormalOnClick({EventConstants.NormalClick.SELECT_MODE_SURE, EventConstants.NormalClick.SELECT_MODE_CANCEL, EventConstants.NormalClick.SELECT_MODE_LIFE, EventConstants.NormalClick.SELECT_MODE_WORK, EventConstants.NormalClick.SELECT_MODE_TRAVEL, EventConstants.NormalClick.SELECT_MODE_QUIET})
//按钮对应的名称
    @ResId({R.id.ll_life, R.id.ll_work, R.id.ll_travel, R.id.ll_quiet})//按钮对应的R文件id
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_life:
                setModelAndView(LauncherConstants.LIVE_MODEL);
                break;
            case R.id.ll_work:
                setModelAndView(LauncherConstants.WORK_MODEL);
                break;
            case R.id.ll_travel:
                setModelAndView(LauncherConstants.TRAVEL_MODEL);
                break;
            case R.id.ll_quiet:
                setModelAndView(LauncherConstants.QUIET_MODEL);
                break;
            default:
                break;
        }
    }

    private void setModelAndView(int selModel) {
        model = selModel;
        initModelView();
        if (model != LauncherConstants.QUIET_MODEL) {
            int day = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
            if (day == Calendar.SUNDAY || day == Calendar.SATURDAY) {
                TPUtils.putObject(mContext, LauncherConstants.CAR_MODEL_REST, new SelectModeBean(Calendar.getInstance(), model));
                //设置了星期六或星期天模式后，更新下个周一为工作模式
                TPUtils.putObject(mContext, LauncherConstants.CAR_MODEL_NORMAL, new SelectModeBean(Calendar.getInstance(), LauncherConstants.WORK_MODEL));
            } else {
                TPUtils.putObject(mContext, LauncherConstants.CAR_MODEL_NORMAL, new SelectModeBean(Calendar.getInstance(), model));
                //设置了周一到周五后，更新下个周六为生活模式
                TPUtils.putObject(mContext, LauncherConstants.CAR_MODEL_REST, new SelectModeBean(Calendar.getInstance(), LauncherConstants.LIVE_MODEL));
            }
        }
        TPUtils.put(mContext, LauncherConstants.CAR_MODEL, model);
        //刷新fragment tv显示
        EventBus.getDefault().post(model,LauncherConstants.REFRESH_RECOM);
        cancelDismissTimer();
        ThreadDispatcher.getDispatcher().postOnMainDelayed(new Runnable() {
            @Override
            public void run() {
                dismiss();
            }
        }, 200);
    }

    @Override
    public void show() {
        super.show();
        startDismissTimer();
    }

    @Override
    public void dismiss() {
        super.dismiss();
        KLog.d("MqttManager uploadOnLineData");
        UploadMqttDataManager.getInstance().uploadOnLineData(LocationManager.getInstance().getCurrentLocation());
    }


}


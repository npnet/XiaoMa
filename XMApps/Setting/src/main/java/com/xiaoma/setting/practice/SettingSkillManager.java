package com.xiaoma.setting.practice;


/*  @project_name：  XMAgateOS
 *  @package_name：  com.xiaoma.setting.practice
 *  @file_name:      SettingSkillManager
 *  @author:         Rookie
 *  @create_time:    2019/6/20 20:05
 *  @description：   TODO             */

import android.content.Context;

import com.xiaoma.carlib.XmCarFactory;
import com.xiaoma.carlib.constant.SDKConstants;
import com.xiaoma.carlib.manager.XmCarCabinManager;
import com.xiaoma.carlib.manager.XmCarHvacManager;
import com.xiaoma.carlib.manager.XmCarVendorExtensionManager;
import com.xiaoma.model.pratice.CarSettingBean;
import com.xiaoma.model.pratice.CarWindowSettingBean;
import com.xiaoma.model.pratice.VrPracticeConstants;
import com.xiaoma.thread.ThreadDispatcher;
import com.xiaoma.utils.GsonHelper;
import com.xiaoma.utils.log.KLog;
import com.xiaoma.vr.skill.SkillManager;
import com.xiaoma.vr.skill.client.SkillCallback;
import com.xiaoma.vr.skill.client.SkillDispatcher;
import com.xiaoma.vr.skill.client.SkillHandler;
import com.xiaoma.vr.skill.logic.ExecResult;
import com.xiaoma.vr.skill.model.Skill;

public class SettingSkillManager {
    public static final String TAG = SettingSkillManager.class.getSimpleName();

    private SettingSkillManager() {

    }

    public static SettingSkillManager getInstance() {
        return SettingSkillManagerHolder.instance;
    }

    public void init(final Context context) {
        SkillManager.getInstance().init(context);
        SkillDispatcher.getInstance().addSkillHandler(new SkillHandler() {
            @Override
            public boolean onSkill(String command, Skill skill, SkillCallback callback) {

                ExecResult execResult = new ExecResult("", "");
                if (VrPracticeConstants.SKILL_VOLUME.equals(skill.getSkillDesc())) {
                    //TODO:调节音量
                    String value = skill.getExtra();
                    excuteMediaVolume(value);
                    execResult.setResult(VrPracticeConstants.SKILL_SUCCESS);
                    callback.onExec(execResult);
                    return true;
                } else if (VrPracticeConstants.SKILL_SETTINGS.equals(skill.getSkillDesc())) {
                    //TODO:车控设置
                    CarSettingBean carSettingBean = GsonHelper.fromJson(skill.getExtra(), CarSettingBean.class);
                    excuteCarSettingBean(carSettingBean);
                    execResult.setResult(VrPracticeConstants.SKILL_SUCCESS);
                    callback.onExec(execResult);
                    return true;
                }

                return false;
            }
        });
    }

    private void excuteMediaVolume(String value) {
        int volume = Integer.parseInt(value);
        XmCarFactory.getCarAudioManager().setStreamVolume(SDKConstants.MEDIA_VOLUME, volume);
        XmCarFactory.getCarAudioManager().setStreamVolume(SDKConstants.BT_MEDIA_VOLUME, volume);
        KLog.d(TAG, "媒体音量: " + volume);
    }

    private void excuteCarSettingBean(final CarSettingBean carSettingBean) {
        //为了防止空调没打开先去对空调当前状态进行判断
        turnOnAirConditioner();
        ThreadDispatcher.getDispatcher().postDelayed(new Runnable() {
            @Override
            public void run() {
                float temperature = carSettingBean.getTemperature();
                XmCarHvacManager.getInstance().setAcTemp(caseSetTemp(temperature)); //设置空调温度
            }
        }, 200);

        CarWindowSettingBean carWindowSettingBean = carSettingBean.getCarWindowSettingBean();
        if (carWindowSettingBean != null) {
            int mainSeatWindowLevel = carWindowSettingBean.getMainSeatWindowLevel();
            int secondSeatWindowLevel = carWindowSettingBean.getSecondSeatWindowLevel();
            XmCarCabinManager.getInstance().setLeftWindowLock(carWindowSettingBean.isMainSeatWindow() ? caseSetWindowPosition(mainSeatWindowLevel) : 0);
            XmCarCabinManager.getInstance().setRightWindowLock(carWindowSettingBean.isSecondSeatWindow() ? caseSetWindowPosition(secondSeatWindowLevel) : 0);
            XmCarCabinManager.getInstance().setBackLeftWindowLock(carWindowSettingBean.isBackLeftWindow() ? 100 : 0);
            XmCarCabinManager.getInstance().setBackRightWindowLock(carWindowSettingBean.isBackRightWindow() ? 100 : 0);
            int leftFront = carWindowSettingBean.isMainSeatWindow() ? caseSetWindowPosition(mainSeatWindowLevel) : 0;
            int rightFront = carWindowSettingBean.isSecondSeatWindow() ? caseSetWindowPosition(secondSeatWindowLevel) : 0;
            int leftBack = carWindowSettingBean.isBackRightWindow() ? 100 : 0;
            int rightBack = carWindowSettingBean.isBackRightWindow() ? 100 : 0;
            KLog.d(TAG, "车窗情况,左前: " + leftFront + ", 右前: " + rightFront + ", 左后: " + leftBack + ", 右后: " + rightBack);
        }

        boolean backMirrorHeat = carSettingBean.isBackMirrorHeat();
        XmCarHvacManager.getInstance().setMirrorDefroster(backMirrorHeat);
        KLog.d(TAG, "后视镜加热: " + backMirrorHeat);

        boolean backWindowHeat = carSettingBean.isBackWindowHeat();
        XmCarHvacManager.getInstance().setWindowRearHeat(backWindowHeat);
        KLog.d(TAG, "后风窗加热: " + backWindowHeat);

        boolean openWindow = carSettingBean.isOpenWindow();
        XmCarFactory.getCarCabinManager().setTopWindowPos(openWindow ? 100 : 0);
        KLog.d(TAG, "天窗: " + openWindow);

        boolean seatHeat = carSettingBean.isSeatHeat();
        boolean mainSeatHeat = carSettingBean.isMainSeatHeat();
        boolean secondSeatHeat = carSettingBean.isSecondSeatHeat();
        XmCarHvacManager.getInstance().setLeftSeatTemp(mainSeatHeat ? SDKConstants.VALUE.SpeechOnOff2_ON_REQ : SDKConstants.VALUE.SpeechOnOff2_OFF_REQ);
        XmCarHvacManager.getInstance().setRightSeatTemp(secondSeatHeat ? SDKConstants.VALUE.SpeechOnOff2_ON_REQ : SDKConstants.VALUE.SpeechOnOff2_OFF_REQ);
        KLog.d(TAG, "座椅加热, 左座椅: " + mainSeatHeat + ", 右座椅: " + secondSeatHeat);

        boolean openSunShade = carSettingBean.isOpenSunShade();
        int openWindowLevel = carSettingBean.getOpenWindowLevel();
        XmCarFactory.getCarCabinManager().setUmbrellaPos(openSunShade ? caseSetWindowPosition(openWindowLevel) : 0);
        KLog.d(TAG, "遮阳帘开关: " + openSunShade + ", 遮阳帘开幅: " + openWindowLevel);

        boolean openSceneLed = carSettingBean.isOpenSceneLed();
        XmCarVendorExtensionManager.getInstance().setAmbientLightSwitch(openSceneLed ? SDKConstants.VALUE.ATMOSPHERE_LIGHT_ON_REQ : SDKConstants.VALUE.ATMOSPHERE_LIGHT_OFF_REQ);
        KLog.d(TAG, "氛围灯: " + openSceneLed);

        boolean openReadLed = carSettingBean.isOpenReadLed();
        XmCarFactory.getCarVendorExtensionManager().setInteriorLightSwitch(openReadLed);
        KLog.d(TAG, "阅读灯: " + openReadLed);
    }

    private int caseSetWindowPosition(int mainSeatWindowLevel) {
        int windowPosition = -1;
        switch (mainSeatWindowLevel) {
            case 0:
                windowPosition = 100;
                break;
            case 1:
                windowPosition = 50;
                break;
            case 2:
                windowPosition = 33;
                break;
            case 3:
                windowPosition = 66;
                break;
            default:
                windowPosition = 0;
                break;
        }
        return windowPosition;
    }

    private int caseSetTemp(double temp) {
        return (int) (temp - 15) * 2;
    }

    private void turnOnAirConditioner() {
        if (XmCarFactory.getCarHvacManager().getHvacPowerOn() != SDKConstants.VALUE.SpeechOnOff2_ON_REQ) {
            XmCarFactory.getCarHvacManager().setHvacPowerOn(SDKConstants.VALUE.SpeechOnOff2_ON_REQ);
        }
    }

    private static class SettingSkillManagerHolder {
        static final SettingSkillManager instance = new SettingSkillManager();
    }

}

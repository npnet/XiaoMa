package com.xiaoma.carlib.manager;

import android.graphics.Point;

import java.util.List;

/**
 * @author: iSun
 * @date: 2018/12/21 0021
 */
public interface IVendorExtension {

    //后视镜自动折叠
//     boolean getRearviewMirror();

//     void setRearviewMirror(boolean value);


    //设置和获取离车模式智慧灯光开关状态
    //设置和获取离车模式智慧灯光延迟关闭时间
    //The value must be BcmState.GoHome#OFF or BcmState.GoHome#M1 or BcmState.GoHome#M2 or BcmState.GoHome#M3
    int getModeGoHome();

    void setModeGoHome(int value);


    //打开和关闭氛围灯
    int getAmbientLightSwitch();

    void setAmbientLightSwitch(int value);

    //设置和获取氛围灯亮度
    int getAmbientLightBrightness();

    void setAmbientLightBrightness(int value);

    //设置和情景随动颜色
    int getAmbientLightColor();

    void setAmbientLightColor(int value);

    //变道闪烁
    void setLaneChangeFlicker(int value);

    int getLaneChangeFlicker();


    //打开和关闭照地灯
    int getWelcomeLampSwitch();

    void setWelcomeLampSwitch(int value);

    //迎宾灯时间
    int getWelcomeLampTime();

    void setWelcomeLampTime(int value);


    //设置和获取座椅迎宾退让
    boolean getWelcomeSeat();

    void setWelcomeSeat(boolean value);

    //获取前防碰撞预警/紧急制动状态开关
    //打开或者关闭前防碰撞预警/紧急制动开关
    //The value must be IfcState.Fcw#OFF or IfcState.Fcw#ACTIVE or IfcState.Fcw#STANDBY or IfcState.Fcw#ACTIVE_FCW or IfcState.Fcw#ACTIVE_AEB
    int getFcwAebSwitch();

    void setFcwAebSwitch(int value);

    //获取前防碰撞预警/紧急制动灵敏度，包括高中低三种状态
    //The value must be IfcState.FcwSensitivity#LOW or IfcState.FcwSensitivity#NORMAL or IfcState.FcwSensitivity#HIGH
    int getFcwSensitivity();

    void setFcwSensitivity(int value);

    void setMarkMirrorLeft(int id);

    void setMarkMirrorRight(int id);

    //获取后排安全带未系提醒开关状态
    //打开或者关闭后排安全带未系提醒
//     int getRearBeltWorningSwitch();

//     void setRearBeltWorningSwitch(int value);


    //设置和获取LDW车道偏移警示灵敏度
    //The value must be IfcState.LdwSensitivity#NORMAL or IfcState.LdwSensitivity#HIGH or IfcState.LdwSensitivity#LOW
    int getLdwSensitivity();


    void setLdwSensitivity(int value);


    //设置和获取LKA 道路保持状态
    //The value must be IfcState.Lka#OFF or IfcState.Lka#STANDBY or IfcState.Lka#ACTIVE The value TBD.
    int getLKA();

    void setLKA(int value);

    //设置和获取ISA 交通标志识别状态
    //The value must be IfcState.Isa#OFF or IfcState.Isa#STANDBY or IfcState.Isa#ACTIVE
    int getISA();

    void setISA(int value);

    //设置和获取IHC 智能远光状态
    //The value must be IfcState.Ihc#OFF or IfcState.Ihc#STANDBY or IfcState.Ihc#ACTIVE
    int getIHC();

    void setIHC(int value);

    //设置和获取DAW驾驶员注意力提醒
    // The value must be IfcState.Daw#OFF or IfcState.Daw#STANDBY or IfcState.Daw#ACTIVE The value TBD.
    int getDAW();


    void setDAW(int value);


    //设置和获取怠速起停主开关
    //The value must be IfcState.Stt#OFF or IfcState.Stt#STANDBY or IfcState.Stt#ACTIVE
    int getSTT();

    void setSTT(int value);


    //获取离车登车灯光律动接口
    //设置离车登车灯光律动接口
    boolean getWelcomeLightByRhythm();

    void setWelcomeLightByRhythm(boolean value);

    //获取室内灯延时设置
    //设置室内灯延时时间
    int getInteriorLightDelay();

    void setInteriorLightDelay(int value);

    //获取氛围灯音乐律动状态
    boolean getAmbientLightByRhythm();

    //设置氛围灯音乐律动状态
    void setAmbientLightByRhythm(boolean value);

    //后备箱自动开启
    boolean getAutomaticTrunk();

    void setAutomaticTrunk(boolean value);

    //胎压复位
    void setResetTiretPressure(int value);

    //自动夹紧
    int getEPB();

    void setEPB(int value);

    //锁车自动关窗
    void setSelfClosingWindow(boolean value);

    boolean getSelfClosingWindow();

    /**
     * 设置音效模式
     *
     * @param mode
     */
    void setSoundEffectsMode(int mode);

    /**
     * 获取音效模式
     *
     * @return
     */
    int getSoundEffectsMode();

    /**
     * 设置自定义音效
     *
     * @param arr
     */
    void setCustomSoundEffects(Integer[] arr);

    /**
     * 获取当前音效设置
     *
     * @return
     */
    List<Integer> getCurrentSoundEffects(int mode);

    /**
     * 设置Arkamys3D音效（开、关）
     */
    void setArkamys3D(int value);

    /**
     * 获取Arkamys3D音效（开、关）
     */
    int getArkamys3D();

    /**
     * 设置声场模式
     *
     * @param soundFieldMode
     */
    void setSoundFieldMode(int soundFieldMode);

    void setSoundEffectPositionAtAnyPoint(int x, int y);

    /**
     * 获取声场模式
     *
     * @return
     */
    int getSoundFieldMode();

    /**
     * 获取最佳听音位
     *
     * @return
     */
    Point getSoundEffectPositionAtAnyPoint();

    /**
     * 设置开关机音效（开、关）
     *
     * @param opened
     */
    void setOnOffMusic(boolean opened);

    /**
     * 获取开关机音效（开、关）
     *
     * @return
     */
    boolean getOnOffMusic();

    /**
     * 设置车辆提示音的级别（一级或是二级）
     *
     * @param level
     */
    void setCarInfoSound(int level);

    /**
     * 获取车辆提示音的级别（一级或是二级）
     *
     * @return
     */
    int getCarInfoSound();

    /**
     * 设置车速音量补偿的级别
     *
     * @param volume
     */
    void setCarSpeedVolumeCompensate(int volume);

    /**
     * 获取车速音量补偿的级别
     *
     * @return
     */
    int getCarSpeedVolumeCompensate();

    /**
     * 设置泊车媒体音量的级别（静音、弱化、正常）
     *
     * @param volume
     */
    void setParkMediaVolume(int volume);

    /**
     * 获取泊车媒体音量的级别（静音、弱化、正常）
     *
     * @return
     */
    int getParkMediaVolume();

    /*其他设置相关*/
    //获取当前显示模式
    int getDisplayMode();

    //设置当前显示模式
    void setDisplayMode(int mode);

    //获取当前屏幕的亮度值-白天
    int getDayDisplayLevel();

    //获取当前屏幕的亮度值-夜晚
    int getNightDisplayLevel();

    //设置屏幕的亮度值-白天
    void setDayDisplayLevel(int value);

    //获取当前屏幕的亮度值-自动
    int getAutoDisplayLevel();

    //设置屏幕的亮度值-自动
    void setAutoDisplayLevel(int value);


    //设置屏幕的亮度值-白天
    void setNightDisplayLevel(int value);

    int getDisplayLevel();

    void setDisplayLevel(int value);


    //获取按键亮度
    int getKeyBoardLevel();

    //设置按键亮度
    void setKeyBoardLevel(int value);

    //获取屏保状态
    boolean getBanVideoStatus();

    //设置屏保状态
    void setBanVideoStatus(boolean value);

    //获取当前主题
    int getTheme();

    //设置当前主题
    boolean setTheme(int value);

    //获取屏幕状态
    boolean getScreenStatus();

    //关闭屏幕
    void closeScreen();

    //点亮屏幕
    void turnOnScreen();

    // 随速闭锁
    void setSpeedAutoLock(boolean value);

    boolean getSpeedAutoLock();

    //离车自动落锁
    void setLeaveAutoLock(boolean value);

    boolean getLeaveAutoLock();

    //遥控解锁模式 REMOTE_CONTROL_UNLOCK_MODE
//     void setRemoteControlUnlockMode(int value);

//     int getRemoteControlUnlockMode();

    //情景随动开关 MUSIC_SCENE_FOLLOW
    void setMusicSceneFollow(boolean value);

    boolean getMusicSceneFollow();

    //发动机引擎状态
    int getEngineState();

    //获取车机的vin码
    String getVinInfo();

    //获取小灯状态
    int getIllStatus();

    int getLanguage();

    void setLanguage(int languageType);

    void setTime(long time);

    //打开/关闭室内灯
    void setInteriorLightSwitch(boolean state);

    int getInteriorLightSwitch();


    //获取降噪模式
    int getSrMode();

    //设置降噪模式
    void setSrMode(int value);

    public void setInteractModeReq(int mode);

    void setInteractMode(int mode);

    //获取平均油耗
    int getFuelConsumption();

    //获取续航里程
    int getOdometerResidual();

    void setFarAutoLock(boolean value);

    int getFarAutoLock();

    void setApproachAutoUnlock(boolean value);

    int getApproachAutoUnlock();

    //获取疲劳检测开关
    int getTiredState();

    //设置疲劳检测开关
    void setTiredState(int value);

    //获取注意力分散检测开关
    int getDistractionState();

    //设置注意力分散检测开关
    void setDistractionState(int value);

    //获取不良行为检测开关
    int getBadDriverState();

    //设置不良行为检测开关
    void setBadDriverState(int value);

    //人脸识别可用状态
    int getRecognizeAvailable();

    //设置人脸识别可用
    public void setRecognizeAvailable(int value);

    //获取人脸识别状态
    int getRecognizeState();

    //开启或取消
    void setRecognize(int value);

    //主动调用开始人脸录入
    void startFaceRecord(int userid);

    //取消人脸录入
    void cancelFaceRecord();

    //删除人脸识别记录
    void delFaceRecord(int userid);

    //获取(蓝牙)钥匙ID
    int getCarKey();

    //获取车速
    int getCarSpeed();

    int getTestValue(int canstant);

    void addValueChangeListener(XmCarVendorExtensionManager.ValueChangeListener valueChangeListener);

    List<Integer> getRobVersion();

    int getRobAction();

    void setRobAction(int actionId);

    /**
     * 减少全息影像亮度
     *
     * @return
     */
    void setRobBrightnessIncrease();


    /**
     * 增加全息影像亮度
     */
    void setRobBrightnessDecrease();


    /**
     * 获取全息亮度
     *
     * @return
     */
    int getRobBrightness();

    void setRobClothMode(int clothId);

    void setRobCharacterMode(int roleId);

    void setHoloId(int holoId);

    void setRobSwitcher(int value);

    void setRobFiceDir(int value);

    //开启/关闭网络同步时间开关
    void setSynchronizeTimeSwitch(boolean value);

    public void setWindowFortRearHeat(boolean state);

    Integer[] getConfigInfo();

    //设置虚拟低音炮开关状态
    void setSubwoofer(boolean open);

    //获取虚拟低音炮开关状态
    boolean getSubwoofer();

    /**
     * 打开360环视
     */
    void onAvsSwitch();

    void closeAvs();

    /**
     * 判断360环视是否正在前台
     *
     * @return
     */
    boolean getCameraStatus();

    int getSensitivityLevel();

    void setSensivityLevel(int level);

    /**
     * 获取总里程
     * int，取得的值*0.1，单位km
     *
     * @return
     */
    int getOdometer();

    /**
     * 设置外后视镜自动折叠
     */
    void setRearviewMirror(int value);


    int getRearViewMirrorEnable();

    void setSimpleMenuDisplay(int value);

    void setNaviDisplay(int value);

    Integer[] getShowTime();
}

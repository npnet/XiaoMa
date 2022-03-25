package com.xiaoma.assistant.scenarios;

import android.content.Context;
import android.text.TextUtils;

import com.mapbar.xiaoma.manager.XmMapNaviManager;
import com.xiaoma.assistant.R;
import com.xiaoma.assistant.callback.WrapperSynthesizerListener;
import com.xiaoma.assistant.constants.AssistantConstants;
import com.xiaoma.assistant.manager.api.CarControlApiManager;
import com.xiaoma.assistant.model.parser.LxParseResult;
import com.xiaoma.assistant.model.parser.ParserLocationParam;
import com.xiaoma.assistant.utils.AssistantUtils;
import com.xiaoma.assistant.utils.Constants;
import com.xiaoma.carlib.XmCarFactory;
import com.xiaoma.carlib.constant.SDKConstants;
import com.xiaoma.carlib.manager.XmCarHvacManager;
import com.xiaoma.carlib.manager.XmCarVendorExtensionManager;
import com.xiaoma.center.logic.CenterConstants;
import com.xiaoma.ui.toast.XMToast;
import com.xiaoma.utils.StringUtil;
import com.xiaoma.utils.log.KLog;
import com.xiaoma.utils.logintype.callback.OnBlockCallback;
import com.xiaoma.utils.logintype.manager.LoginType;
import com.xiaoma.utils.logintype.manager.LoginTypeManager;
import com.xiaoma.vr.model.SeoptType;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author: iSun
 * @date: 2019/2/15 0015
 * 设备控制
 */
class IatCarControlScenario extends IatScenario {
    private static final String TAG = IatCarControlScenario.class.getSimpleName();
    private static final int SKY_WINDOW_ON = 100;
    private static final int SKY_WINDOW_OFF = 0;
    private static final int WINDOW_ON = 100;
    private static final int WINDOW_OFF = 0;
    public static final int SUNSHADE_ON = 100;
    public static final int SUNSHADE_OFF = 0;
    private static final int LITTLE_LEVEL = 33;//新需求开度变成三分之一
    private static final int SKYLIGHT_PERK_LEVEL = 1;  //1代表天窗翘起


    public IatCarControlScenario(Context context) {
        super(context);
    }

    @Override
    public void init() {

    }

    @Override
    public void onParser(String voiceJson, LxParseResult parseResult, ParserLocationParam location, long session) {
        try {
            JSONObject jsonObject = new JSONObject(parseResult.getSlots());//direction,mode
            if (jsonObject.has("name")) {
                boolean isOpen = !"CLOSE".equals(parseResult.getOperation());
                String level = "";
                if (jsonObject.has("nameValue")) {
                    level = jsonObject.getString("nameValue");
                }
                controlCarDevices(isOpen, jsonObject.getString("name"), level, jsonObject, parseResult.getText());
            } else if (jsonObject.has("mode")) {
                boolean isOpen = "OPEN".equals(parseResult.getOperation());
                String level = "";
                if (jsonObject.has("nameValue")) {
                    level = jsonObject.getString("nameValue");
                }
                controlCarDevices(isOpen, jsonObject.getString("mode"), level, jsonObject, parseResult.getText());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void controlCarDevices(boolean isOpen, String name, String level, JSONObject jsonObject, String text) throws JSONException {
        if (TextUtils.isEmpty(name)) {
            return;
        }
        String speakText = "";
        if (name.contains("车窗")) {//车窗操作需要根据当前车速来决定,车速超过80,不允许操作车窗;
            if (!shouldOperateWindow()) {
                speakText = getString(R.string.high_speed_window_not_operated);
                assistantManager.speakContent(speakText);
                return;
            }
        }
        if (!LoginTypeManager.getInstance().canUse(name, new OnBlockCallback() {
            @Override
            public void handle(LoginType loginType) {
                XMToast.showToast(context, LoginTypeManager.getPrompt(context));
            }
        })) return;
        switch (name) {
            case Constants.ParseKey.SKYLIGHT:
//                CarControlApiManager.getInstance().skyWindowControl(isOpen);
                if (!TextUtils.isEmpty(level)) {
                    setRobAction(AssistantConstants.RobActionKey.CAR_CONTROL_OPEN);
                    switch (level) {
                        case "二分之一":
                            XmCarFactory.getCarCabinManager().setTopWindowPos(50);
                            break;
                        case "三分之一":
                            XmCarFactory.getCarCabinManager().setTopWindowPos(33);
                            break;
                        case "三分之二":
                            XmCarFactory.getCarCabinManager().setTopWindowPos(66);
                            break;
                        case "LITTLE":
                            // 天窗翘起
                            XmCarFactory.getCarCabinManager().setTopWindowPos(1);
                            break;
                    }
                    speakText = getString(R.string.ok);
                } else {
                    setRobAction(48);
                    XmCarFactory.getCarCabinManager().setTopWindowPos(isOpen ? SKY_WINDOW_ON : SKY_WINDOW_OFF);
                    if (StringUtil.isNotEmpty(text)) {
                        if (text.contains(Constants.ParseKey.SKYLIGHT_START)) {
                            speakText = isOpen ? getString(R.string.watch_star_tts) : getString(R.string.watch_close_tts);
                        } else {
                            speakText = getString(R.string.result_ok);
                        }

                    } else {
                        speakText = getString(R.string.result_ok);
                    }

                }
                break;
            case Constants.ParseKey.ALL_LOCK:
                setRobAction(isOpen ? AssistantConstants.RobActionKey.CAR_CONTROL_OPEN : AssistantConstants.RobActionKey.CAR_CONTROL_CLOSE);
                if (XmCarVendorExtensionManager.getInstance().getCarSpeed() > 0) {
                    speakText = getString(R.string.can_not_unlock);
                } else {
                    XmCarFactory.getCarCabinManager().setAllDoorLock(isOpen ? SDKConstants.SpeechDoorLock_ALL_DOOR_UNLK_REQ : SDKConstants.SpeechDoorLock_ALL_DOOR_LK_REQ);
                    speakText = getString(R.string.ok);
                }
                break;
            case Constants.ParseKey.MAIN_LOCK:
            case Constants.ParseKey.LOCK:
                setRobAction(isOpen ? AssistantConstants.RobActionKey.CAR_CONTROL_OPEN : AssistantConstants.RobActionKey.CAR_CONTROL_CLOSE);
                if (XmCarVendorExtensionManager.getInstance().getCarSpeed() > 0) {
                    speakText = getString(R.string.can_not_unlock);
                } else {
                    speakText = getString(R.string.ok);
                    XmCarFactory.getCarCabinManager().setDoorLock(isOpen ? SDKConstants.SpeechDoorLock_DRV_DOOR_UNLK_REQ : SDKConstants.SpeechDoorLock_DRV_DOOR_LK_REQ);
                }
                break;
            case Constants.ParseKey.SMOKING_MODE://我要吸烟
                SeoptType smokingSeoptType = getLocalSeoptType();
                switch (smokingSeoptType) {
                    default:
                    case LEFT:
                        speakText = operateLeftFrontWindow(true, "LITTLE", speakText, text);
                        break;
                    case RIGHT:
                        speakText = operateRightFrontWindow(true, "LITTLE", speakText, text);
                        break;
                    case CLOSE:
                        //打开/关闭所有车窗
                        speakText = openAllWindow(isOpen, level, speakText, text);
                        break;
                }
                break;
            case Constants.ParseKey.WINDOW://我要透透气 、车窗
                SeoptType localSeoptType = getLocalSeoptType();
                //如果是透透气，只有左右驾驶响应
                if (isOpneLittle(level, text)) {
                    switch (localSeoptType) {
                        case LEFT://左
                            speakText = operateLeftFrontWindow(isOpen, level, speakText, text);
                            break;
                        case RIGHT://右
                            speakText = operateRightFrontWindow(isOpen, level, speakText, text);
                            break;
                    }
                }
                //车窗控制
                switch (localSeoptType) {
                    default://默认
                    case LEFT://左
                        speakText = operateLeftFrontWindow(isOpen, level, speakText, text);
                        break;
                    case RIGHT://右
                        speakText = operateRightFrontWindow(isOpen, level, speakText, text);
                        break;
                    case CLOSE:
                        //打开/关闭所有车窗
                        speakText = openAllWindow(isOpen, level, speakText, text);
                        break;
                }
                break;
            case Constants.ParseKey.ALL_WINDOW:
                //                CarControlApiManager.getInstance().carWindowControl(isOpen);
                speakText = openAllWindow(isOpen, level, speakText, text);
                break;
            case Constants.ParseKey.LEFT_FRONT_WINDOW:
                speakText = operateLeftFrontWindow(isOpen, level, speakText, text);
                break;
            case Constants.ParseKey.LEFT_BACK_WINDOW:
                if (XmCarVendorExtensionManager.getInstance().getCarSpeed() > 30) {
                    speakText = getString(R.string.too_fast_to_open_window);
                } else {
                    if (!TextUtils.isEmpty(level)) {
                        level = checkSpeakText(level, text);
                        setRobAction(AssistantConstants.RobActionKey.CAR_CONTROL_OPEN);
                        switch (level) {
                            case "二分之一":
                                speakText = setBackLeftWindowLock(50, "二分之一");
                                break;
                            case "三分之一":
                                speakText = setBackLeftWindowLock(33, "三分之一");
                                break;
                            case "三分之二":
                                speakText = setBackLeftWindowLock(66, "三分之二");
                                break;
                            case "LITTLE":
                                XmCarFactory.getCarCabinManager().setBackLeftWindowLock(LITTLE_LEVEL);
                                speakText = getString(R.string.left_back_window_opend);
                                break;
                        }
                    } else {
                        setRobAction(isOpen ? AssistantConstants.RobActionKey.CAR_CONTROL_OPEN : AssistantConstants.RobActionKey.CAR_CONTROL_CLOSE);
                        XmCarFactory.getCarCabinManager().setBackLeftWindowLock(isOpen ? WINDOW_ON : WINDOW_OFF);
                        speakText = getString(isOpen ? R.string.left_back_window_open : R.string.left_back_window_close);
                    }
                }
                break;
            case Constants.ParseKey.RIGHT_FRONT_WINDOW:
                speakText = operateRightFrontWindow(isOpen, level, speakText, text);
                break;
            case Constants.ParseKey.RIGHT_BACK_WINDOW:
                if (XmCarVendorExtensionManager.getInstance().getCarSpeed() > 30) {
                    speakText = getString(R.string.too_fast_to_open_window);
                } else {
                    if (!TextUtils.isEmpty(level)) {
                        level = checkSpeakText(level, text);
                        setRobAction(AssistantConstants.RobActionKey.CAR_CONTROL_OPEN);
                        switch (level) {
                            case "二分之一":
                                speakText = getBackRightWindowLock(50, "二分之一");
                                break;
                            case "三分之一":
                                speakText = getBackRightWindowLock(33, "三分之一");
                                break;
                            case "三分之二":
                                speakText = getBackRightWindowLock(66, "三分之二");
                                break;
                            case "LITTLE":
                                XmCarFactory.getCarCabinManager().setBackRightWindowLock(LITTLE_LEVEL);
                                speakText = getString(R.string.right_back_window_opend);
                                break;
                        }
                    } else {
                        setRobAction(isOpen ? AssistantConstants.RobActionKey.CAR_CONTROL_OPEN : AssistantConstants.RobActionKey.CAR_CONTROL_CLOSE);
                        XmCarFactory.getCarCabinManager().setBackRightWindowLock(isOpen ? WINDOW_ON : WINDOW_OFF);
                        speakText = getString(isOpen ? R.string.right_back_window_open : R.string.right_back_window_close);
                    }
                }
                break;
            case Constants.ParseKey.SUNSHADE:
                //                CarControlApiManager.getInstance().sunShadeControl(isOpen);
                if (!TextUtils.isEmpty(level)) {
                    setRobAction(AssistantConstants.RobActionKey.CAR_CONTROL_OPEN);
                    switch (level) {
                        case "二分之一":
                            XmCarFactory.getCarCabinManager().setUmbrellaPos(50);
                            break;
                        case "三分之一":
                            XmCarFactory.getCarCabinManager().setUmbrellaPos(33);
                            break;
                        case "三分之二":
                            XmCarFactory.getCarCabinManager().setUmbrellaPos(66);
                            break;
                        case "LITTLE":
                            XmCarFactory.getCarCabinManager().setUmbrellaPos(LITTLE_LEVEL);
                            break;
                    }
                } else {
                    setRobAction(isOpen ? AssistantConstants.RobActionKey.CAR_CONTROL_OPEN : AssistantConstants.RobActionKey.CAR_CONTROL_CLOSE);
                    if (!isOpen) {
                        XmCarFactory.getCarCabinManager().setTopWindowPos(SKY_WINDOW_OFF);
                    }
                    XmCarFactory.getCarCabinManager().setUmbrellaPos(isOpen ? SUNSHADE_ON : SUNSHADE_OFF);
                }
                speakText = getString(R.string.ok);
                break;
            case Constants.ParseKey.AMBIENT_LIGHT:
                if (!TextUtils.isEmpty(jsonObject.optString("color"))) {
                    setRobAction(AssistantConstants.RobActionKey.CAR_CONTROL_OPEN);
                    String color = jsonObject.getString("color");
                    XmCarFactory.getCarVendorExtensionManager().setAmbientLightColor(transColor2Scene(color));
                    speakText = String.format(getString(R.string.switch_color), color);
                } else {
                    setRobAction(isOpen ? AssistantConstants.RobActionKey.CAR_CONTROL_OPEN : AssistantConstants.RobActionKey.CAR_CONTROL_CLOSE);
                    XmCarFactory.getCarVendorExtensionManager().setAmbientLightSwitch(isOpen ? SDKConstants.VALUE.ATMOSPHERE_LIGHT_ON_REQ : SDKConstants.VALUE.ATMOSPHERE_LIGHT_OFF_REQ);
                    speakText = getString(isOpen ? R.string.open_ambient_light : R.string.close_ambient_light);
                }

                break;
            case Constants.ParseKey.INSIDE_LIGHT://室内灯
            case Constants.ParseKey.CEILING_LIGHT://顶棚灯
            case Constants.ParseKey.READ_LIGHT://阅读灯
                //                CarControlApiManager.getInstance().trunkControl(isOpen);
                setRobAction(isOpen ? AssistantConstants.RobActionKey.CAR_CONTROL_OPEN : AssistantConstants.RobActionKey.CAR_CONTROL_CLOSE);
                XmCarFactory.getCarVendorExtensionManager().setInteriorLightSwitch(isOpen);
                speakText = getString(R.string.ok);
                break;
            case Constants.ParseKey.TRUNK:
            case Constants.ParseKey.BACKDOOR:
                //                CarControlApiManager.getInstance().trunkControl(isOpen);
                setRobAction(isOpen ? AssistantConstants.RobActionKey.CAR_CONTROL_OPEN : AssistantConstants.RobActionKey.CAR_CONTROL_CLOSE);
                if (XmCarVendorExtensionManager.getInstance().getCarSpeed() > 0) {
                    speakText = getString(R.string.can_not_unlock);
                } else {
                    XmCarFactory.getCarCabinManager().setBackDoorLock(isOpen ? SDKConstants.VALUE.SpeechOnOff2_ON_REQ : SDKConstants.VALUE.SpeechOnOff2_OFF_REQ);
                    speakText = getString(R.string.ok);
                }
                break;
            case Constants.ParseKey.LOW_BEAM_LIGHTS:
                break;
            case Constants.ParseKey.HIGH_BEAM:
                break;
            case Constants.ParseKey.REVERSE_IMAGE:
                // TODO 接口参数待确认
                //                CarControlApiManager.getInstance().reverseImageControl(isOpen);
                break;
            case Constants.ParseKey.WIPER:
            case Constants.ParseKey.FRONT_WIPER:
                //                CarControlApiManager.getInstance().wiperControl(isOpen);
                setRobAction(AssistantConstants.RobActionKey.CAR_CONTROL_OPEN);
                if (jsonObject.has("mode")) {
                    XmCarFactory.getCarCabinManager().frontWash(SDKConstants.SpeechOnOff_1_ON_REQ);
                } else {
                    XmCarFactory.getCarCabinManager().setWipe(isOpen ? SDKConstants.SpeechOnOff_1_ON_REQ : SDKConstants.SpeechOnOff_1_INACTIVE_REQ);
                }
                speakText = getString(R.string.ok);
                break;
            case Constants.ParseKey.FOG_LIGHT:
                break;
            case Constants.ParseKey.GALLERY_LIGHT:
                //                CarControlApiManager.getInstance().galleryLightControl(isOpen);
                speakText = getString(R.string.all_right);
                break;
            case Constants.ParseKey.WARNING_LIGHT:
                break;
            case Constants.ParseKey.AUTOMATIC_PARKING:
                break;
            case Constants.ParseKey.CRUISE:
                break;
            case Constants.ParseKey.REARVIEW_MIRROR_HEATING:
                //后视镜加热
                // XmCarFactory.getCarHvacManager().setMirrorDefroster(isOpen);
                setRobAction(isOpen ? AssistantConstants.RobActionKey.CAR_CONTROL_OPEN : AssistantConstants.RobActionKey.CAR_CONTROL_CLOSE);
                if (isOpen) {
                    speakText = getString(R.string.rearview_mirror_heating_must_manual);
                } else {
                    speakText = getString(R.string.rearview_close_heating_must_manual);
                }

                break;
            case Constants.ParseKey.PANORAMIC_360:
                String path = null;//方向
                if (jsonObject.has("direction")) {//方向调节
                    try {
                        path = jsonObject.getString("direction");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    controlPortrait(path, isOpen);
                } else if (jsonObject.has("mode")) {
                    String mode = jsonObject.getString("mode");
                    if ("2d".equals(mode)) {
                        // 切换为2D导航视图
                        setRobAction(AssistantConstants.RobActionKey.ROB_ACTION_NAVI);
                        XmMapNaviManager.getInstance().setNaviShowState(1);
                    } else if ("3d".equals(mode)) {
                        // 切换为3D导航视图
                        setRobAction(AssistantConstants.RobActionKey.ROB_ACTION_NAVI);
                        XmMapNaviManager.getInstance().setNaviShowState(2);
                    } else if ("雷达".equals(mode) && isOpen) {//打开

                    } else if ("雷达".equals(mode) && !isOpen) {//关闭

                    } else if ("360".equals(mode)) {
                        //                        CarControlApiManager.getInstance().panoramic360Control(isOpen);
                        setRobAction(31);
                        if (isOpen) {
                            if (XmCarVendorExtensionManager.getInstance().getCarSpeed() > 30) {
                                speakText = getString(R.string.too_fast_to_check_overall_view);
                            } else {
                                speakContent(getString(R.string.ok), new WrapperSynthesizerListener() {
                                    @Override
                                    public void onCompleted() {
                                        closeVoicePopup();
                                        XmCarVendorExtensionManager.getInstance().onAvsSwitch();
                                    }
                                });
                                return;
                            }
                        }
                    }
                } else {
                    //TODO 切换导航视图

                }
                break;
            case Constants.ParseKey.SEAT:
                if (jsonObject.has("mode")) {
                    if ("座椅通风".equals(jsonObject.getString("mode"))) {
                        speakText = getString(R.string.incorrect_function_tts);
                        break;
                    }
                }

                //打开/关闭座椅加热
                SeoptType seoptType = getLocalSeoptType();
                //定向识别关闭 -> 开启/关闭主副座椅加热
                if (seoptType == SeoptType.CLOSE) {
                    setRobAction(isOpen ? AssistantConstants.RobActionKey.CAR_CONTROL_OPEN : AssistantConstants.RobActionKey.CAR_CONTROL_CLOSE);
                    XmCarHvacManager.getInstance().setLeftSeatTemp(isOpen ? SDKConstants.VALUE.SpeechOnOff2_ON_REQ : SDKConstants.VALUE.SpeechOnOff2_OFF_REQ);
                    XmCarHvacManager.getInstance().setRightSeatTemp(isOpen ? SDKConstants.VALUE.SpeechOnOff2_ON_REQ : SDKConstants.VALUE.SpeechOnOff2_OFF_REQ);
                    speakText = isOpen ? context.getString(R.string.turn_on_main_seat_heat) : context.getString(R.string.turn_off_main_seat_heat);
                } else if (seoptType == SeoptType.RIGHT) {
                    setRobAction(isOpen ? AssistantConstants.RobActionKey.CAR_CONTROL_OPEN : AssistantConstants.RobActionKey.CAR_CONTROL_CLOSE);
                    XmCarHvacManager.getInstance().setRightSeatTemp(isOpen ? SDKConstants.VALUE.SpeechOnOff2_ON_REQ : SDKConstants.VALUE.SpeechOnOff2_OFF_REQ);
                    speakText = isOpen ? context.getString(R.string.turn_on_front_seat_heat) : context.getString(R.string.turn_off_front_seat_heat);
                } else {
                    setRobAction(isOpen ? AssistantConstants.RobActionKey.CAR_CONTROL_OPEN : AssistantConstants.RobActionKey.CAR_CONTROL_CLOSE);
                    XmCarHvacManager.getInstance().setLeftSeatTemp(isOpen ? SDKConstants.VALUE.SpeechOnOff2_ON_REQ : SDKConstants.VALUE.SpeechOnOff2_OFF_REQ);
                    speakText = isOpen ? context.getString(R.string.turn_on_main_seat_heat) : context.getString(R.string.turn_off_main_seat_heat);
                }
                break;
            case Constants.ParseKey.MAIN_SEAT:
                //打开/关闭驾驶员座椅加热
                setRobAction(isOpen ? AssistantConstants.RobActionKey.CAR_CONTROL_OPEN : AssistantConstants.RobActionKey.CAR_CONTROL_CLOSE);
                XmCarHvacManager.getInstance().setLeftSeatTemp(isOpen ? SDKConstants.VALUE.SpeechOnOff2_ON_REQ : SDKConstants.VALUE.SpeechOnOff2_OFF_REQ);
                speakText = isOpen ? context.getString(R.string.turn_on_main_seat_heat) : context.getString(R.string.turn_off_main_seat_heat);
                break;
            case Constants.ParseKey.FRONT_SEAT:
                //打开/关闭副驾驶座椅加热
                setRobAction(isOpen ? AssistantConstants.RobActionKey.CAR_CONTROL_OPEN : AssistantConstants.RobActionKey.CAR_CONTROL_CLOSE);
                XmCarHvacManager.getInstance().setRightSeatTemp(isOpen ? SDKConstants.VALUE.SpeechOnOff2_ON_REQ : SDKConstants.VALUE.SpeechOnOff2_OFF_REQ);
                speakText = isOpen ? context.getString(R.string.turn_on_front_seat_heat) : context.getString(R.string.turn_off_front_seat_heat);
                break;
            case Constants.ParseKey.SKYLIGHT_PERK:
                //天窗翘起
                setRobAction(35);
                speakText = getString(R.string.ok);
//                setRobAction(AssistantConstants.RobActionKey.CAR_CONTROL_OPEN);
                XmCarFactory.getCarCabinManager().setTopWindowPos(SKYLIGHT_PERK_LEVEL);
                break;
            case Constants.ParseKey.OPEN_FRONT_WINDOW:
                //天窗翘起
                int speakRandom = AssistantUtils.getUnStandWord();
                String unStandSpeak = getString(AssistantUtils.mUnStands_Speak[speakRandom]);
                String unStand = getString(AssistantUtils.mUnStands[speakRandom]);
                speakAndFeedListening(unStandSpeak, unStand);
                return;
            default:
        }
        if (TextUtils.isEmpty(speakText)) {
            closeVoicePopup();
        } else {
            closeAfterSpeak(speakText);
        }
    }


    /**
     * 打开所有车窗
     */
    private String openAllWindow(boolean isOpen, String level, String speakText, String text) {
        if (XmCarVendorExtensionManager.getInstance().getCarSpeed() > 30) {
            speakText = getString(R.string.too_fast_to_open_window);
        } else {
            if (!TextUtils.isEmpty(level)) {
                level = checkSpeakText(level, text);
                setRobAction(AssistantConstants.RobActionKey.CAR_CONTROL_OPEN);
                switch (level) {
                    case "二分之一":
                        speakText = setAllWindowLock(50, "二分之一");
                        break;
                    case "三分之一":
                        speakText = setAllWindowLock(33, "三分之一");
                        break;
                    case "三分之二":
                        speakText = setAllWindowLock(66, "三分之二");
                        break;
                    case "LITTLE":
                        XmCarFactory.getCarCabinManager().setAllWindowLock(LITTLE_LEVEL);
                        speakText = getString(R.string.opend_all_window);
                        break;
                }

            } else {
                setRobAction(isOpen ? AssistantConstants.RobActionKey.CAR_CONTROL_OPEN : AssistantConstants.RobActionKey.CAR_CONTROL_CLOSE);
                XmCarFactory.getCarCabinManager().setAllWindowLock(isOpen ? WINDOW_ON : WINDOW_OFF);
                speakText = isOpen ? getString(R.string.opening_the_window) : getString(R.string.closeing_the_window);
            }
        }
        return speakText;
    }

    /**
     * 用于应对 指令中包含"透个气"和"透透气"时
     * Level 是三分之一，导致tts播报不正确
     */
    private String checkSpeakText(String level, String text) {
        if (!"LITTLE".equals(level) && (text.contains("透个气") || text.contains("透透气"))) {
            level = "LITTLE";
        }
        return level;
    }

    /**
     * 用于应对 指令中包含"透个气"和"透透气"时
     * Level 是三分之一，导致tts播报不正确
     */
    private boolean isOpneLittle(String level, String text) {
        if (!"LITTLE".equals(level) && (text.contains("透个气") || text.contains("透透气"))) {
            return true;
        }
        return false;
    }


    /**
     * 操作右前车窗
     *
     * @return
     */
    private String operateRightFrontWindow(boolean isOpen, String level, String speakText, String text) {
        if (XmCarVendorExtensionManager.getInstance().getCarSpeed() > 30) {
            speakText = getString(R.string.too_fast_to_open_window);
        } else {
            if (!TextUtils.isEmpty(level)) {
                level = checkSpeakText(level, text);
                setRobAction(AssistantConstants.RobActionKey.CAR_CONTROL_OPEN);
                switch (level) {
                    case "二分之一":
                        speakText = setRightWindowLock(50, "二分之一");
                        break;
                    case "三分之一":
                        speakText = setRightWindowLock(33, "三分之一");
                        break;
                    case "三分之二":
                        speakText = setRightWindowLock(66, "三分之二");
                        break;
                    case "LITTLE":
                        XmCarFactory.getCarCabinManager().setRightWindowLock(LITTLE_LEVEL);
                        speakText = getString(R.string.right_front_window_opend);
                        break;
                }
            } else {
                setRobAction(isOpen ? AssistantConstants.RobActionKey.CAR_CONTROL_OPEN : AssistantConstants.RobActionKey.CAR_CONTROL_CLOSE);
                XmCarFactory.getCarCabinManager().setRightWindowLock(isOpen ? WINDOW_ON : WINDOW_OFF);
                speakText = getString(isOpen ? R.string.right_front_window_open : R.string.right_front_window_close);
            }
        }
        return speakText;
    }


    /**
     * 操作左前车窗
     */
    private String operateLeftFrontWindow(boolean isOpen, String level, String speakText, String text) {
        if (XmCarVendorExtensionManager.getInstance().getCarSpeed() > 30) {
            speakText = getString(R.string.too_fast_to_open_window);
        } else {
            if (!TextUtils.isEmpty(level)) {
                level = checkSpeakText(level, text);
                setRobAction(AssistantConstants.RobActionKey.CAR_CONTROL_OPEN);
                switch (level) {
                    case "二分之一":
                        speakText = setLeftWindowLock(50, "二分之一");
                        break;
                    case "三分之一":
                        speakText = setLeftWindowLock(33, "三分之一");
                        break;
                    case "三分之二":
                        speakText = setLeftWindowLock(66, "三分之二");
                        break;
                    case "LITTLE":
                        XmCarFactory.getCarCabinManager().setLeftWindowLock(LITTLE_LEVEL);
                        speakText = getString(R.string.left_front_window_opend);
                        break;
                }
            } else {
                setRobAction(isOpen ? AssistantConstants.RobActionKey.CAR_CONTROL_OPEN : AssistantConstants.RobActionKey.CAR_CONTROL_CLOSE);
                XmCarFactory.getCarCabinManager().setLeftWindowLock(isOpen ? WINDOW_ON : WINDOW_OFF);
                speakText = getString(isOpen ? R.string.left_front_window_open : R.string.left_front_window_close);
            }
        }
        return speakText;
    }

    private String setAllWindowLock(int targetPos, String levelText) {
        setRobAction(targetPos == WINDOW_OFF ? AssistantConstants.RobActionKey.CAR_CONTROL_CLOSE : AssistantConstants.RobActionKey.CAR_CONTROL_OPEN);
        String speakText;
        int leftWindowLock = XmCarFactory.getCarCabinManager().getLeftWindowLock();
        int rightWindowLock = XmCarFactory.getCarCabinManager().getRightWindowLock();
        int backLeftWindowLock = XmCarFactory.getCarCabinManager().getBackLeftWindowLock();
        int backRightWindowLock = XmCarFactory.getCarCabinManager().getBackRightWindowLock();
        if (leftWindowLock == targetPos && rightWindowLock == targetPos
                && backLeftWindowLock == targetPos && backRightWindowLock == targetPos) {
            speakText = StringUtil.format(context.getString(R.string.already_this_level), levelText);
        } else {
            XmCarFactory.getCarCabinManager().setAllWindowLock(targetPos);
            speakText = StringUtil.format(context.getString(R.string.set_level), levelText);
        }
        return speakText;
    }

    private String setLeftWindowLock(int targetPos, String levelText) {
        setRobAction(targetPos == WINDOW_OFF ? AssistantConstants.RobActionKey.CAR_CONTROL_CLOSE : AssistantConstants.RobActionKey.CAR_CONTROL_OPEN);
        String speakText;
        int pos = XmCarFactory.getCarCabinManager().getLeftWindowLock();
        if (pos == targetPos) {
            speakText = StringUtil.format(context.getString(R.string.already_this_level), levelText);
        } else {
            XmCarFactory.getCarCabinManager().setLeftWindowLock(targetPos);
            speakText = StringUtil.format(context.getString(R.string.set_level), levelText);
        }
        return speakText;
    }

    private String setRightWindowLock(int targetPos, String levelText) {
        setRobAction(targetPos == WINDOW_OFF ? AssistantConstants.RobActionKey.CAR_CONTROL_CLOSE : AssistantConstants.RobActionKey.CAR_CONTROL_OPEN);
        String speakText;
        int pos = XmCarFactory.getCarCabinManager().getRightWindowLock();
        if (pos == targetPos) {
            speakText = StringUtil.format(context.getString(R.string.already_this_level), levelText);
        } else {
            XmCarFactory.getCarCabinManager().setRightWindowLock(targetPos);
            speakText = StringUtil.format(context.getString(R.string.set_level), levelText);
        }
        return speakText;
    }

    private String setBackLeftWindowLock(int targetPos, String levelText) {
        setRobAction(targetPos == WINDOW_OFF ? AssistantConstants.RobActionKey.CAR_CONTROL_CLOSE : AssistantConstants.RobActionKey.CAR_CONTROL_OPEN);
        String speakText;
        int pos = XmCarFactory.getCarCabinManager().getBackLeftWindowLock();
        if (pos == targetPos) {
            speakText = StringUtil.format(context.getString(R.string.already_this_level), levelText);
        } else {
            XmCarFactory.getCarCabinManager().setBackLeftWindowLock(targetPos);
            speakText = StringUtil.format(context.getString(R.string.set_level), levelText);
        }
        return speakText;
    }

    private String getBackRightWindowLock(int targetPos, String levelText) {
        setRobAction(targetPos == WINDOW_OFF ? AssistantConstants.RobActionKey.CAR_CONTROL_CLOSE : AssistantConstants.RobActionKey.CAR_CONTROL_OPEN);
        String speakText;
        int pos = XmCarFactory.getCarCabinManager().getBackRightWindowLock();
        if (pos == targetPos) {
            speakText = StringUtil.format(context.getString(R.string.already_this_level), levelText);
        } else {
            XmCarFactory.getCarCabinManager().setBackRightWindowLock(targetPos);
            speakText = StringUtil.format(context.getString(R.string.set_level), levelText);
        }
        return speakText;
    }

    private int transColor2Scene(String color) {
        int scene = 0;
        switch (color) {
            case "红色":
                scene = 1;
                break;
            case "粉色":
                scene = 2;
                break;
            case "紫色":
                scene = 3;
                break;
            case "蓝色":
                scene = 5;
                break;
            case "青色":
                scene = 7;
                break;
            case "绿色":
                scene = 8;
                break;
            case "黄色":
                scene = 9;
                break;
            case "橙色":
                scene = 10;
                break;
            case "棕色":
                scene = 11;
                break;
            case "白色":
                scene = 12;

        }
        return scene;
    }

    private void controlPortrait(String path, boolean isOpen) {
        // TODO: 2019/2/15 0015 360全景影像控制
        if (TextUtils.isEmpty(path)) {//开关

        } else {//方向控制
            switch (path) {
                case Constants.ParseKey.RIGHT_BACK:
                    CarControlApiManager.getInstance().switchCameraControl(CenterConstants.SwitchCamera.RIGHT_BACK);
                    break;
                case Constants.ParseKey.RIGHT:
                    CarControlApiManager.getInstance().switchCameraControl(CenterConstants.SwitchCamera.RIGHT);
                    break;
                case Constants.ParseKey.RIGHT_FRONT:
                    CarControlApiManager.getInstance().switchCameraControl(CenterConstants.SwitchCamera.RIGHT_FRONT);
                    break;
                case Constants.ParseKey.BEFORE:
                    CarControlApiManager.getInstance().switchCameraControl(CenterConstants.SwitchCamera.FRONT);
                    break;
                case Constants.ParseKey.LEFT_FRONT:
                    CarControlApiManager.getInstance().switchCameraControl(CenterConstants.SwitchCamera.LEFT_FRONT);
                    break;
                case Constants.ParseKey.LEFT:
                    CarControlApiManager.getInstance().switchCameraControl(CenterConstants.SwitchCamera.LEFT);
                    break;
                case Constants.ParseKey.LEFT_REAR:
                    CarControlApiManager.getInstance().switchCameraControl(CenterConstants.SwitchCamera.LEFT_BACK);
                    break;
                case Constants.ParseKey.REAR:
                    CarControlApiManager.getInstance().switchCameraControl(CenterConstants.SwitchCamera.BACK);
                    break;
                default:
                    break;
            }
        }

    }

    @Override
    public void onChoose(String voiceText) {

    }

    @Override
    public boolean isIntercept() {
        return false;
    }

    @Override
    public void onEnd() {

    }

    private boolean shouldOperateWindow() {
        int speed = XmCarFactory.getCarVendorExtensionManager().getCarSpeed();
        KLog.d("hzx", "当前车速: " + speed);
        return speed < 80;
    }
}

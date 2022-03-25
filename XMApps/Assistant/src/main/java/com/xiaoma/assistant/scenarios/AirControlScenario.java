package com.xiaoma.assistant.scenarios;

import android.content.Context;
import android.text.TextUtils;

import com.xiaoma.assistant.R;
import com.xiaoma.assistant.constants.AssistantConstants;
import com.xiaoma.assistant.manager.api.AirConditionerApiManager;
import com.xiaoma.assistant.model.parser.AirBean;
import com.xiaoma.assistant.model.parser.LxParseResult;
import com.xiaoma.assistant.model.parser.ParserLocationParam;
import com.xiaoma.assistant.utils.Constants;
import com.xiaoma.carlib.XmCarFactory;
import com.xiaoma.carlib.constant.SDKConstants;
import com.xiaoma.center.logic.CenterConstants;
import com.xiaoma.utils.GsonHelper;
import com.xiaoma.utils.StringUtil;
import com.xiaoma.utils.log.KLog;
import com.xiaoma.vr.model.SeoptType;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author: iSun
 * @date: 2019/2/13 0013
 * 空调控制
 */
class AirControlScenario extends IatScenario {
    private static final String TAG = AirControlScenario.class.getSimpleName();
    private static final int MAX_AC_TEMP = 32;
    private static final int MIN_AC_TEMP = 18;
    private static final int TEMP_CHANGE_RATE = 2;

    private static final int MAX_WIND_SPEED = 7;
    private static final int MIN_WIND_SPEED = 1;
    private static final int WIND_CHANGE_RATE = 1;
    private static final int MEDIUM_WIND_SPEED = 4;
    private static final int HIGH_WIND_SPEED = 6;
    private static final int LOW_WIND_SPEED = 2;

    public AirControlScenario(Context context, LxParseResult parserResult) {
        super(context);
    }

    @Override
    public void init() {

    }

    @Override
    public void onParser(String voiceJson, LxParseResult parseResult, ParserLocationParam location, long session) {
        this.parseResult = parseResult;
        try {
            if (isSpecialValue(parseResult)) {
                parserMaxOrMin(parseResult);
            } else {//温度与风速调节
                parserSpecific(parseResult);
            }
        } catch (Exception e) {
            e.printStackTrace();
            speakContent(context.getString(R.string.control_air_error));
        }

    }

    private boolean isSpecialValue(LxParseResult parseResult) {
        boolean result = false;
        try {
            JSONObject fanSpeedObj = new JSONObject(parseResult.getSlots());
            JSONObject temperatureObj = new JSONObject(parseResult.getSlots());
            String fanSpeed = null;
            String temperature = null;
            if (fanSpeedObj.has("fanSpeed")) {
                fanSpeed = fanSpeedObj.getString("fanSpeed");
            }
            if (temperatureObj.has("temperature")) {
                temperature = fanSpeedObj.getString("temperature");
            }
            if ("MIN".equals(fanSpeed) || "MIN".equals(temperature) || "MAX".equals(fanSpeed) || "MAX".equals(temperature)
                    || Constants.ParseKey.HIGH_WIND.equals(fanSpeed) || Constants.ParseKey.MEDIUM_WIND.equals(fanSpeed) || Constants.ParseKey.LOW_WIND.equals(fanSpeed)) {
                result = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    // TODO: 2019/2/14 0014 空调控制接口接入
    private void parserSpecific(LxParseResult parseResult) {
        boolean isOpen = !"CLOSE".equals(parseResult.getOperation());
        AirBean bean = GsonHelper.fromJson(parseResult.getSlots(), AirBean.class);
        String speakContent = " ";
        if (bean == null) {
            try {
                JSONObject jsonObject = new JSONObject(parseResult.getSlots());
                if (jsonObject.has("temperature")) {
                    String temperature = jsonObject.getString("temperature");
                    int acTemp = getAcTemp();
                    double realTemp = caseGetRealTemp(acTemp);
                    switch (temperature) {
                        case "PLUS":
                        case "PLUS_LITTLE":
                            speakContent = adjustTemperature(true);
                            break;
                        case "MINUS":
                        case "MINUS_LITTLE":
                            speakContent = adjustTemperature(false);
                            break;
                        case "PLUS_MORE":
                            if (realTemp + 3 <= MAX_AC_TEMP) {
                                realTemp = realTemp + 3;
                                speakContent = String.format(getString(R.string.temp_adjust), realTemp + "");
                                setAcTemp(caseSetTemp(realTemp));
                            } else {
                                if (realTemp >= MAX_AC_TEMP) {
                                    speakContent = getString(R.string.ac_in_max_temp);
                                } else {
                                    realTemp = MAX_AC_TEMP;
                                    speakContent = getString(R.string.temp_in_max);
                                    setAcTemp(caseSetTemp(realTemp));
                                }
                            }
                            break;
                        case "MINUS_MORE":
                            if (realTemp - 3 >= MIN_AC_TEMP) {
                                realTemp = realTemp - 3;
                                speakContent = String.format(getString(R.string.temp_adjust), realTemp + "");
                                setAcTemp(caseSetTemp(realTemp));
                            } else {
                                if (realTemp <= MIN_AC_TEMP) {
                                    speakContent = getString(R.string.ac_in_min_temp);
                                } else {
                                    realTemp = MIN_AC_TEMP;
                                    speakContent = getString(R.string.temp_in_min);
                                    setAcTemp(caseSetTemp(realTemp));
                                }
                            }
                            break;
                    }
                } else if (jsonObject.has("fanSpeed")) {
                    String fanSpeed = jsonObject.getString("fanSpeed");
                    switch (fanSpeed) {
                        case "PLUS":
                            speakContent = adjustWindSpeed(true);
                            break;
                        case "MINUS":
                            speakContent = adjustWindSpeed(false);
                            break;
                    }
                }
                closeAfterSpeak(speakContent);
                return;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if (isOpenAir(bean)) {
            //打开空调
//            AirConditionerApiManager.getInstance().isTurnOnAirConditioner(true);
            setRobAction(AssistantConstants.RobActionKey.AIR_OPEN_RELATED_SETTING);
            XmCarFactory.getCarHvacManager().setHvacPowerOn(SDKConstants.VALUE.SpeechOnOff2_ON_REQ);
            speakContent = getString(R.string.ac_enable);
            closeAfterSpeak(speakContent);
            return;
        } else if (isColoseAir(bean)) {
            //关闭空调
            setRobAction(AssistantConstants.RobActionKey.AIR_OPEN_RELATED_SETTING);
            XmCarFactory.getCarHvacManager().setHvacPowerOn(SDKConstants.VALUE.SpeechOnOff2_OFF_REQ);
            speakContent = getString(R.string.ac_disable);
            closeAfterSpeak(speakContent);
            return;
        } else if (isTemperatureControl(bean)) {
            //温度
            int temp = 0;
            if ("ZERO".equals(bean.temperature.ref)) {
                //具体调节
                temp = Float.valueOf(bean.temperature.offset).intValue();
//                AirConditionerApiManager.getInstance().changeTemperature(CenterConstants.ChangeAcTempOperation.SPECIFIC_NUM, temp);
                if (temp > MAX_AC_TEMP || temp < MIN_AC_TEMP) {
                    assistantManager.speakContent(String.format(getString(R.string.ac_temp_range), MIN_AC_TEMP, MAX_AC_TEMP));
                    return;
                } else {
                    setAcTemp(caseSetTemp(temp));
                    speakContent = String.format(getString(R.string.change_ac_temp), temp);
                    closeAfterSpeak(speakContent);
                    return;
                }
            } else if ("CUR".equals(bean.temperature.ref)) {
                //降低或升高
                boolean isUp = "+".equals(bean.temperature.direct);
//                temp = Float.valueOf(bean.temperature.offset);
                speakContent = adjustTemperature(isUp);
            }
        } else if (isFanSpeedControl(bean)) {
            int speed;
            if ("ZERO".equals(bean.fanSpeed.ref)) {
                //具体调节
                speed = Integer.valueOf(bean.fanSpeed.offset);
//                AirConditionerApiManager.getInstance().changeAcWindSpeedModel(CenterConstants.ChangeAcWindSpeed.SPECIFIC_SPEED, temp);
                if (speed > MAX_WIND_SPEED || speed < MIN_WIND_SPEED) {
                    speakContent = String.format(getString(R.string.wind_speed_range), MIN_WIND_SPEED, MAX_WIND_SPEED);
                } else {
                    turnOnAirConditioner();
                    XmCarFactory.getCarHvacManager().setFanSpeedSetpoint(caseSpeed(speed));
                    speakContent = String.format(getString(R.string.change_fan_speed), speed);
                    closeAfterSpeak(speakContent);
                    return;
                }
            } else if ("CUR".equals(bean.fanSpeed.ref)) {
                //降低或升高
                boolean isUp = "+".equals(bean.fanSpeed.direct);
//                temp = Integer.valueOf(bean.fanSpeed.offset);
                speakContent = adjustWindSpeed(isUp);
                closeAfterSpeak(speakContent);
                return;
            }
        } else if (bean != null && !TextUtils.isEmpty(bean.airflowDirection)) {
            // isOpen
            if (Constants.ParseKey.FACE.equals(bean.airflowDirection) || Constants.ParseKey.HEAD.equals(bean.airflowDirection)) {
                //吹面模式
//                AirConditionerApiManager.getInstance().changeAcWindDirectionModel(CenterConstants.ChangeAcWindDirectionModelOperation.FACE_MODEL);
                setRobAction(AssistantConstants.RobActionKey.AIR_CONTROL_RELATED_SETTING);
                turnOnAirConditioner();
                XmCarFactory.getCarHvacManager().setFanDirectionAvailable(SDKConstants.FACE_MODEL);
                speakContent = getString(R.string.switch_to_face_mode);
                closeAfterSpeak(speakContent);
                return;
            } else if (Constants.ParseKey.FOOT.equals(bean.airflowDirection)) {
                if (!TextUtils.isEmpty(bean.mode)) {
                    //除霜吹脚模式
//                    AirConditionerApiManager.getInstance().changeAcWindDirectionModel(CenterConstants.ChangeAcWindDirectionModelOperation.DEFROST_FOOT);
                    setRobAction(AssistantConstants.RobActionKey.AIR_CONTROL_RELATED_SETTING);
                    turnOnAirConditioner();
                    XmCarFactory.getCarHvacManager().setFanDirectionAvailable(SDKConstants.FOOT_DEF_MODEL);
                    speakContent = getString(R.string.switch_to_defog_foot_mode);
                    closeAfterSpeak(speakContent);
                    return;
                } else {
                    //吹脚模式
//                    AirConditionerApiManager.getInstance().changeAcWindDirectionModel(CenterConstants.ChangeAcWindDirectionModelOperation.FOOT_MODEL);
                    setRobAction(AssistantConstants.RobActionKey.AIR_CONTROL_RELATED_SETTING);
                    turnOnAirConditioner();
                    XmCarFactory.getCarHvacManager().setFanDirectionAvailable(SDKConstants.FOOT_MODEL);
                    speakContent = getString(R.string.switch_to_foot_mode);
                    closeAfterSpeak(speakContent);
                    return;
                }
            } else if (Constants.ParseKey.FACE_FOOT.equals(bean.airflowDirection)) {
                //吹面吹脚模式
//                AirConditionerApiManager.getInstance().changeAcWindDirectionModel(CenterConstants.ChangeAcWindDirectionModelOperation.FACE_FOOT_MODEL);
                setRobAction(AssistantConstants.RobActionKey.AIR_CONTROL_RELATED_SETTING);
                turnOnAirConditioner();
                XmCarFactory.getCarHvacManager().setFanDirectionAvailable(SDKConstants.FACE_FOOT_MODEL);//参数不确定
                speakContent = getString(R.string.switch_to_face_foot_mode);
                closeAfterSpeak(speakContent);
                return;
            }
        } else if (bean != null && !TextUtils.isEmpty(bean.mode)) {
            // isOpen
            speakContent = AirModeControl(isOpen, bean);
            switch (bean.mode) {
                case Constants.ParseKey.REFRIGERATION:
                case Constants.ParseKey.INNER_LOOP:
                case Constants.ParseKey.OUTSIDE_LOOP:
                case Constants.ParseKey.COMPRESSOR:
                case Constants.ParseKey.FRONT_DEFROG:
                case Constants.ParseKey.BACK_DEFROG:
                case Constants.ParseKey.DEFOG:
                case Constants.ParseKey.DEMIST:
                    closeAfterSpeak(speakContent);
                    return;
            }
        }
        closeAfterSpeak(speakContent);
    }

    private void turnOnAirConditioner() {
        if (XmCarFactory.getCarHvacManager().getHvacPowerOn() != SDKConstants.VALUE.SpeechOnOff2_ON_REQ) {
            XmCarFactory.getCarHvacManager().setHvacPowerOn(SDKConstants.VALUE.SpeechOnOff2_ON_REQ);
        }
    }

    private int caseSpeed(int speed) {
        int speedLevel = 0;
        switch (speed) {
            case 1:
                speedLevel = SDKConstants.VALUE.FAN_SPEED_LEVEL_1;
                break;
            case 2:
                speedLevel = SDKConstants.VALUE.FAN_SPEED_LEVEL_2;
                break;
            case 3:
                speedLevel = SDKConstants.VALUE.FAN_SPEED_LEVEL_3;
                break;
            case 4:
                speedLevel = SDKConstants.VALUE.FAN_SPEED_LEVEL_4;
                break;
            case 5:
                speedLevel = SDKConstants.VALUE.FAN_SPEED_LEVEL_5;
                break;
            case 6:
                speedLevel = SDKConstants.VALUE.FAN_SPEED_LEVEL_6;
                break;
            case 7:
                speedLevel = SDKConstants.VALUE.FAN_SPEED_LEVEL_7;
                break;
        }
        return speedLevel;
    }

    private String adjustWindSpeed(boolean isUp) {
        int curFanSpeed = XmCarFactory.getCarHvacManager().getFanSpeedSetpoint();
        String speakContent;
        KLog.d("hzx", "获取的空调风速: " + curFanSpeed);
        if (isUp) {
//                    AirConditionerApiManager.getInstance().changeAcWindSpeedModel(CenterConstants.ChangeAcWindSpeed.INCREASE_SPEED, temp);
            if (curFanSpeed >= MAX_WIND_SPEED) {
                speakContent = getString(R.string.already_max_wind_speed);
            } else {
                turnOnAirConditioner();
                XmCarFactory.getCarHvacManager().setFanSpeedSetpoint(caseSpeed(curFanSpeed + WIND_CHANGE_RATE));
                speakContent = String.format(getString(R.string.change_air_conditioning_speed), curFanSpeed + WIND_CHANGE_RATE);
            }
        } else {
            if (curFanSpeed <= MIN_WIND_SPEED) {
                speakContent = getString(R.string.already_min_wind_speed);
            } else {
                turnOnAirConditioner();
                XmCarFactory.getCarHvacManager().setFanSpeedSetpoint(caseSpeed(curFanSpeed - WIND_CHANGE_RATE));
                speakContent = String.format(getString(R.string.change_air_conditioning_speed), curFanSpeed - WIND_CHANGE_RATE);
            }
        }
        return speakContent;
    }


    private int getAcTemp() {
        SeoptType localSeoptType = getLocalSeoptType();
        if (localSeoptType == SeoptType.LEFT || localSeoptType == SeoptType.CLOSE) {
            return XmCarFactory.getCarHvacManager().getLeftAcTemp();
        } else {
            return XmCarFactory.getCarHvacManager().getRightAcTemp();
        }
    }

    private void setAcTemp(int temp) {
        turnOnAirConditioner();
        setRobAction(AssistantConstants.RobActionKey.AIR_TEMPERATURE_RELATED_SETTING);
        SeoptType localSeoptType = getLocalSeoptType();
        if (localSeoptType == SeoptType.LEFT) {
            KLog.i("filOut| "+"[setAcTemp]->LEFT");
            XmCarFactory.getCarHvacManager().setLeftAcTemp(temp);
        } else if (localSeoptType == SeoptType.RIGHT) {
            KLog.i("filOut| "+"[setAcTemp]->RIGHT");
            XmCarFactory.getCarHvacManager().setRightAcTemp(temp);
        } else if (localSeoptType == SeoptType.CLOSE) {
            KLog.i("filOut| "+"[setAcTemp]->CLOSE");
            XmCarFactory.getCarHvacManager().setAcTemp(temp);
        }
    }

    private String adjustTemperature(boolean isUp) {
        setRobAction(AssistantConstants.RobActionKey.AIR_TEMPERATURE_RELATED_SETTING);
        int temp = getAcTemp();
        int curTemp = (int) caseGetRealTemp(temp);
        KLog.d("hzx", "获取的空调温度: " + curTemp);
        String speakContent;
        if (isUp) {
//                    AirConditionerApiManager.getInstance().changeTemperature(CenterConstants.ChangeAcTempOperation.TURN_UP, temp);
            if (curTemp >= MAX_AC_TEMP) {
                speakContent = getString(R.string.ac_in_max_temp);
            } else if (curTemp <= MAX_AC_TEMP - TEMP_CHANGE_RATE) {
                setAcTemp(caseSetTemp(curTemp + TEMP_CHANGE_RATE));
                speakContent = StringUtil.format(getString(R.string.temp_adjust), curTemp + TEMP_CHANGE_RATE);
            } else {
                setAcTemp(caseSetTemp(MAX_AC_TEMP));
                speakContent = getString(R.string.temp_in_max);
            }
        } else {
            if (curTemp <= MIN_AC_TEMP) {
                speakContent = getString(R.string.ac_in_min_temp);
            } else if (curTemp >= MIN_AC_TEMP + TEMP_CHANGE_RATE) {
                setAcTemp(caseSetTemp(curTemp - TEMP_CHANGE_RATE));
                speakContent = StringUtil.format(getString(R.string.temp_adjust), curTemp - TEMP_CHANGE_RATE);
            } else {
                setAcTemp(caseSetTemp(MIN_AC_TEMP));
                speakContent = getString(R.string.temp_in_min);
            }

        }
        return speakContent;
    }

    private double caseGetRealTemp(int temp) {
        return (temp / 2) + 15;
    }

    /**
     * @param isOpen
     * @param bean
     */
    // TODO: 2019/2/14 0014 空调控制接口接入
    private String AirModeControl(boolean isOpen, AirBean bean) {
        String key = bean.mode;
        String speakContent = " ";
        switch (key) {
            case Constants.ParseKey.AUTO:
//                AirConditionerApiManager.getInstance().changeAcModel(CenterConstants.ChangeAcModelOperation.AUTO, isOpen);
                setRobAction(AssistantConstants.RobActionKey.AIR_CONDITIONING_AUTOMATIC_MODE);
                turnOnAirConditioner();
                XmCarFactory.getCarHvacManager().setAutomaticMode(isOpen ? SDKConstants.VALUE.SpeechOnOff2_ON_REQ : SDKConstants.VALUE.SpeechOnOff2_OFF_REQ);
                speakContent = isOpen ? getString(R.string.ac_auto_on) : getString(R.string.ac_auto_off);
                break;
            case Constants.ParseKey.MANUAL:
//                AirConditionerApiManager.getInstance().changeAcModel(CenterConstants.ChangeAcModelOperation.MANUAL, isOpen);
                setRobAction(AssistantConstants.RobActionKey.AIR_CONDITIONING_AUTOMATIC_MODE);
                XmCarFactory.getCarHvacManager().setAutomaticMode(isOpen ? SDKConstants.VALUE.SpeechOnOff2_ON_REQ : SDKConstants.VALUE.SpeechOnOff2_OFF_REQ);
                speakContent = getString(isOpen ? R.string.manual_mode_open : R.string.manual_mode_close);
                break;
            case Constants.ParseKey.REFRIGERATION:
//                AirConditionerApiManager.getInstance().changeAcModel(CenterConstants.ChangeAcModelOperation.REFRIGERATION, isOpen);
                setRobAction(isOpen ? AssistantConstants.RobActionKey.AIR_OPEN_RELATED_SETTING : AssistantConstants.RobActionKey.AIR_CLOSE_RELATED_SETTING);
                turnOnAirConditioner();
                XmCarFactory.getCarHvacManager().setAcON(isOpen ? SDKConstants.VALUE.SpeechOnOff2_ON_REQ : SDKConstants.VALUE.SpeechOnOff2_OFF_REQ);
                speakContent = isOpen ? getString(R.string.ac_on) : getString(R.string.ac_off);
                if (isOpen) {
                    speakContent = getString(R.string.ac_on);
                } else {
                    speakContent = getString(R.string.ac_off);
                }
                break;
            case Constants.ParseKey.DEHUMIDIFICATION:
                // TODO 接口未提供
                AirConditionerApiManager.getInstance().changeAcModel(CenterConstants.ChangeAcModelOperation.DEHUMIDIFICATION, isOpen);
                break;
            case Constants.ParseKey.HEATING:
//                AirConditionerApiManager.getInstance().changeAcModel(CenterConstants.ChangeAcModelOperation.HEAT, isOpen);
                XmCarFactory.getCarHvacManager().setAcON(isOpen ? SDKConstants.VALUE.SpeechOnOff2_OFF_REQ : SDKConstants.VALUE.SpeechOnOff2_ON_REQ);
                speakContent = getString(isOpen ? R.string.heat_mode_open : R.string.heat_mode_close);
                break;
            case Constants.ParseKey.AIR_SUPPLY:
                // TODO 接口未提供
                AirConditionerApiManager.getInstance().changeAcModel(CenterConstants.ChangeAcModelOperation.WIND, isOpen);
                break;
            case Constants.ParseKey.INNER_LOOP:
//                AirConditionerApiManager.getInstance().changeAcModel(CenterConstants.ChangeAcModelOperation.INNER_CYCLE, isOpen);
                if ("SET".equals(parseResult.getOperation())) {
                    setRobAction(AssistantConstants.RobActionKey.AIR_CONTROL_RELATED_SETTING);
                    turnOnAirConditioner();
                    XmCarFactory.getCarHvacManager().setAirRecirculationOn(SDKConstants.VALUE.SpeechFrsRec_FRS_REQ);
                    speakContent = getString(R.string.inner_loop);
                } else if ("CLOSE".equals(parseResult.getOperation())) {
                    setRobAction(AssistantConstants.RobActionKey.AIR_CONTROL_RELATED_SETTING);
                    turnOnAirConditioner();
                    XmCarFactory.getCarHvacManager().setAirRecirculationOn(SDKConstants.VALUE.SpeechFrsRec_REC_REQ);
                    speakContent = getString(R.string.outside_loop);
                }
                break;
            case Constants.ParseKey.OUTSIDE_LOOP:
//                AirConditionerApiManager.getInstance().changeAcModel(CenterConstants.ChangeAcModelOperation.OUT_CYCLE, isOpen);
                if ("SET".equals(parseResult.getOperation())) {
                    setRobAction(AssistantConstants.RobActionKey.AIR_CONTROL_RELATED_SETTING);
                    turnOnAirConditioner();
                    XmCarFactory.getCarHvacManager().setAirRecirculationOn(SDKConstants.VALUE.SpeechFrsRec_REC_REQ);
                    speakContent = getString(R.string.outside_loop);
                } else if ("CLOSE".equals(parseResult.getOperation())) {
                    setRobAction(AssistantConstants.RobActionKey.AIR_CONTROL_RELATED_SETTING);
                    turnOnAirConditioner();
                    XmCarFactory.getCarHvacManager().setAirRecirculationOn(SDKConstants.VALUE.SpeechFrsRec_FRS_REQ);
                    speakContent = getString(R.string.inner_loop);
                }
                break;
            case Constants.ParseKey.REAR_WINDOW_HEATING:
            case Constants.ParseKey.DEFOGGING:
            case Constants.ParseKey.BACK_DEFROG:
//                AirConditionerApiManager.getInstance().changeAcModel(CenterConstants.ChangeAcModelOperation.BACK_WINDOW_HEAT, isOpen);
                setRobAction(isOpen ? AssistantConstants.RobActionKey.AIR_OPEN_RELATED_SETTING : AssistantConstants.RobActionKey.AIR_CLOSE_RELATED_SETTING);
                XmCarFactory.getCarHvacManager().setWindowRearHeat(isOpen);
                speakContent = isOpen ? context.getString(R.string.opened_heat) : context.getString(R.string.closed_heat);
                break;
            case Constants.ParseKey.FRONT_WINDOW:
                // TODO 接口未提供
                AirConditionerApiManager.getInstance().changeAcModel(CenterConstants.ChangeAcModelOperation.TO_FRONT_WINDOW, isOpen);
                break;
            case Constants.ParseKey.AIR_OUTLET:
                // TODO 接口未提供
                AirConditionerApiManager.getInstance().changeAcModel(CenterConstants.ChangeAcModelOperation.AIR_OUTLET, isOpen);
                break;
            case Constants.ParseKey.FRONT_DEFROG:
                // TODO 接口未提供
                setRobAction(AssistantConstants.RobActionKey.AIR_CONTROL_RELATED_SETTING);
                speakContent = getString(R.string.switch_to_defog);
                turnOnAirConditioner();
                XmCarFactory.getCarVendorExtensionManager().setWindowFortRearHeat(isOpen);
//                AirConditionerApiManager.getInstance().changeDefogModel(CenterConstants.ChangeDefogModel.FRONT_DEFOG, isOpen);
//                speakContent = getString(R.string.switch_to_defog);
                break;
            case Constants.ParseKey.DEFOG:
            case Constants.ParseKey.DEMIST:
//                AirConditionerApiManager.getInstance().changeDefogModel(CenterConstants.ChangeDefogModel.DEFOG, isOpen);
                // TODO 接口未提供
                setRobAction(AssistantConstants.RobActionKey.AIR_CONTROL_RELATED_SETTING);
                speakContent = isOpen?getString(R.string.switch_to_defog):getString(R.string.close_defog);
                turnOnAirConditioner();
                XmCarFactory.getCarVendorExtensionManager().setWindowFortRearHeat(isOpen);
                XmCarFactory.getCarHvacManager().setWindowRearHeat(isOpen);
//                XmCarFactory.getCarHvacManager().setFanDirectionAvailable(SDKConstants.DEF_MODE);
                break;
            case Constants.ParseKey.COMPRESSOR:
                if ("SET".equals(parseResult.getOperation())) {
                    setRobAction(AssistantConstants.RobActionKey.AIR_OPEN_RELATED_SETTING);
                    turnOnAirConditioner();
                    XmCarFactory.getCarHvacManager().setAcON(SDKConstants.VALUE.SpeechOnOff2_ON_REQ);
                    speakContent = getString(R.string.ac_on);
                } else if ("CLOSE".equals(parseResult.getOperation())) {
                    setRobAction(AssistantConstants.RobActionKey.AIR_CLOSE_RELATED_SETTING);
                    turnOnAirConditioner();
                    XmCarFactory.getCarHvacManager().setAcON(SDKConstants.VALUE.SpeechOnOff2_OFF_REQ);
                    speakContent = getString(R.string.ac_off);
                }
                break;
            default:
        }
        return speakContent;
    }

    // TODO: 2019/2/14 0014 空调控制接口接入
    private void parserMaxOrMin(LxParseResult parseResult) throws JSONException {
        JSONObject jsonObject = new JSONObject(parseResult.getSlots());
        String speakContent = " ";
        if (jsonObject.has("temperature")) {//温度
            String tempObj = (String) jsonObject.get("temperature");
            setRobAction(AssistantConstants.RobActionKey.AIR_TEMPERATURE_RELATED_SETTING);
            if ("MAX".equals(tempObj)) {
//                AirConditionerApiManager.getInstance().changeTemperature(CenterConstants.ChangeAcTempOperation.MAX, MAX_AC_TEMP);
                setAcTemp(caseSetTemp(MAX_AC_TEMP));
                speakContent = getString(R.string.temp_in_max);
            } else if ("MIN".equals(tempObj)) {
//                AirConditionerApiManager.getInstance().changeTemperature(CenterConstants.ChangeAcTempOperation.MIN, MIN_AC_TEMP);
                setAcTemp(caseSetTemp(MIN_AC_TEMP));
                speakContent = getString(R.string.temp_in_min);
            }
        }
        if (jsonObject.has("fanSpeed")) {
            String fanObj = (String) jsonObject.get("fanSpeed");
            int windSpeed = 0;
            setRobAction(AssistantConstants.RobActionKey.AIR_WIND_SPEED_RELATED_SETTING);
            if ("MAX".equals(fanObj)) {
//                AirConditionerApiManager.getInstance().changeAcWindSpeedModel(CenterConstants.ChangeAcWindSpeed.MAX_SPEED, 8);
                windSpeed = caseSpeed(MAX_WIND_SPEED);
                speakContent = getString(R.string.fan_speed_change_to_max);
            } else if ("MIN".equals(fanObj)) {
//                AirConditionerApiManager.getInstance().changeAcWindSpeedModel(CenterConstants.ChangeAcWindSpeed.MIN_SPEED, 1);
                windSpeed = caseSpeed(MIN_WIND_SPEED);
                speakContent = getString(R.string.fan_speed_change_to_min);
            } else if (Constants.ParseKey.MEDIUM_WIND.equals(fanObj)) {
//                AirConditionerApiManager.getInstance().changeAcWindSpeedModel(CenterConstants.ChangeAcWindSpeed.MEDIUM_SPEED, 4);
                windSpeed = caseSpeed(MEDIUM_WIND_SPEED);
                speakContent = getString(R.string.fan_speed_change_to_medium);
            } else if (Constants.ParseKey.HIGH_WIND.equals(fanObj)) {
//                AirConditionerApiManager.getInstance().changeAcWindSpeedModel(CenterConstants.ChangeAcWindSpeed.HIGH_SPEED, 6);
                windSpeed = caseSpeed(HIGH_WIND_SPEED);
                speakContent = getString(R.string.fan_speed_change_to_high);
            } else if (Constants.ParseKey.LOW_WIND.equals(fanObj)) {
//                AirConditionerApiManager.getInstance().changeAcWindSpeedModel(CenterConstants.ChangeAcWindSpeed.LOW_SPEED, 2);
                windSpeed = caseSpeed(LOW_WIND_SPEED);
                speakContent = getString(R.string.fan_speed_change_to_low);
            }
            if (windSpeed != 0) {
                turnOnAirConditioner();
                XmCarFactory.getCarHvacManager().setFanSpeedSetpoint(windSpeed);
            }
        }
        closeAfterSpeak(speakContent);
    }

    private int caseSetTemp(double temp) {
        return (int) (temp - 15) * 2;
    }

    private boolean isFanSpeedControl(AirBean bean) {
        boolean result = false;
        if (bean != null && bean.fanSpeed != null) {
            result = true;
        }
        return result;
    }

    private boolean isTemperatureControl(AirBean bean) {
        boolean result = false;
        if (bean != null && bean.temperature != null) {
            result = true;
        }
        return result;
    }

    private boolean isOpenAir(AirBean bean) {
        return bean != null && "OPEN".equals(bean.insType);
    }

    private boolean isColoseAir(AirBean bean) {
        return bean != null && "CLOSE".equals(bean.insType);
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
}

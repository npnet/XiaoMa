package com.xiaoma.assistant.scenarios;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.xiaoma.assistant.R;
import com.xiaoma.assistant.callback.WrapperSynthesizerListener;
import com.xiaoma.assistant.constants.AssistantConstants;
import com.xiaoma.assistant.manager.AssistantManager;
import com.xiaoma.assistant.manager.RequestManager;
import com.xiaoma.assistant.manager.api.AudioManager;
import com.xiaoma.assistant.manager.api.SettingApiManager;
import com.xiaoma.assistant.model.hologram.HoloManInfo;
import com.xiaoma.assistant.model.hologram.HologramDress;
import com.xiaoma.assistant.model.parser.LxParseResult;
import com.xiaoma.assistant.model.parser.ParserLocationParam;
import com.xiaoma.assistant.utils.Constants;
import com.xiaoma.assistant.utils.HolopramHelper;
import com.xiaoma.carlib.XmCarFactory;
import com.xiaoma.carlib.constant.SDKConstants;
import com.xiaoma.carlib.manager.XmCarCabinManager;
import com.xiaoma.carlib.manager.XmCarConfigManager;
import com.xiaoma.carlib.manager.XmCarSensorManager;
import com.xiaoma.carlib.manager.XmCarVendorExtensionManager;
import com.xiaoma.carlib.store.HologramRepo;
import com.xiaoma.center.logic.CenterConstants;
import com.xiaoma.component.nodejump.NodeConst;
import com.xiaoma.component.nodejump.NodeUtils;
import com.xiaoma.config.ConfigConstants;
import com.xiaoma.network.callback.StringCallback;
import com.xiaoma.network.model.Response;
import com.xiaoma.player.AudioConstants;
import com.xiaoma.skin.constant.SkinConstants;
import com.xiaoma.skin.manager.XmSkinManager;
import com.xiaoma.thread.ThreadDispatcher;
import com.xiaoma.ui.toast.XMToast;
import com.xiaoma.utils.GsonHelper;
import com.xiaoma.utils.LaunchUtils;
import com.xiaoma.utils.ListUtils;
import com.xiaoma.utils.StringUtil;
import com.xiaoma.utils.log.KLog;
import com.xiaoma.utils.logintype.callback.OnBlockCallback;
import com.xiaoma.utils.logintype.manager.LoginType;
import com.xiaoma.utils.logintype.manager.LoginTypeManager;
import com.xiaoma.utils.screentool.ScreenControlUtil;
import com.xiaoma.vrfactory.tts.XmTtsManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author: iSun
 * @date: 2019/2/20 0020
 * 指令场景
 */
public class IatInstructionScenario extends IatScenario {
    private static final String TAG = IatInstructionScenario.class.getSimpleName();
    private static final int BRIGHTNESS_CHANGE_RATE = 2;
    //  TODO 待确认屏幕最大亮度
    private static final int MAX_BRIGHTNESS = 10;
    private static final int MIN_BRIGHTNESS = 1;
    private static final int VOLUME_CHANGE_RATE = 1;
    //  TODO 待确认最大音量
    private static final int MAX_MEDIA_VOLUME = 40;
    private static final int MAX_CALL_VOLUME = 40;
    private static final int MAX_TTS_VOLUME = 8;
    private static final int MAX_BLUETOOTH_VOLUME = 40;
    private static final int MAX_KEYBOARD_BRIGHTNESS_LEVEL = 10;
    private static final int MIN_KEYBOARD_BRIGHTNESS_LEVEL = 0;
    public Context mContext;
    private List<Theme> themeList;
    private int[] finishWord = new int[]{R.string.ok, R.string.here, R.string.now_take_photo};
    private String[] changeClothes = new String[]{getString(R.string.clothes_speak_one), getString(R.string.clothes_speak_two), getString(R.string.clothes_speak_three), getString(R.string.clothes_speak_four), getString(R.string.clothes_speak_five), getString(R.string.clothes_speak_six), getString(R.string.clothes_speak_seven)};
    private String[] danceSpeak = new String[]{getString(R.string.dance_speak_one), getString(R.string.dance_speak_two), getString(R.string.dance_speak_three)};
    private static final String LAST_VOLUME = "last_volume";
    private SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
    private int mVolumeChangeRate = 0;

    public IatInstructionScenario(Context context) {
        super(context);
        mContext = context;
    }

    @Override
    public void init() {
        themeList = new ArrayList<>();
        themeList.add(new Theme(SkinConstants.THEME_ZHIHUI, "智慧光圈"));
        themeList.add(new Theme(SkinConstants.THEME_QINGSHE, "轻奢"));
        themeList.add(new Theme(SkinConstants.THEME_DAOMENG, "盗梦空间"));
    }

    @Override
    public void onParser(String voiceJson, LxParseResult parseResult, ParserLocationParam location, long session) {
        try {
            JSONObject slots = new JSONObject(parseResult.getSlots());
            if (slots.has("insType")) {
                String insType = slots.getString("insType");
                if (!LoginTypeManager.getInstance().canUse(insType, new OnBlockCallback() {
                    @Override
                    public void handle(LoginType loginType) {
                        XMToast.showToast(context, LoginTypeManager.getPrompt(context));
                    }
                })) return;
                instructionDispatcher(parseResult, insType);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void instructionDispatcher(LxParseResult parseResult, String insType) throws JSONException {
        JSONObject jsonObject = new JSONObject(parseResult.getSlots());
        String tag = null;//全局或目标音源 radio|
        if (jsonObject.has("tag")) {
            tag = jsonObject.getString("tag");
        }
        int displayLevel = XmCarVendorExtensionManager.getInstance().getDisplayLevel();
        String speakText = "";
        switch (insType) {
            case "CHANGE"://服装选择，语言切换及发音人切换
                parseChange(parseResult);
                return;
            case "SET_LANGUAGE"://系统语言设置
                setSystemLanguage(parseResult);
                setRobAction(AssistantConstants.RobActionKey.SWITCH_SYSTEM_LANGUAGE);
                return;
            case "PERFORM"://技能表演
                skillPerformance(parseResult);
                return;
            case "OPEN_VOICE"://打开语音设置
                OpenVoiceSetting();
                return;
            case "CLOSE_VOICE"://关闭语音设置
                closeVoiceSetting();
                speakText = getString(R.string.close_assistant_setting);
                break;
            case "OPEN_TACHOGRAPH"://打开行车记录仪
                break;
            case "CLOSE_TACHOGRAPH"://关闭行车记录仪
                break;
            case "TAKE_PHOTO"://行车记录仪拍照
                setRobAction(14);
                if (XmCarConfigManager.hasJourneyRecord()) {
                    speakContent(getString(getFinishWord()), new WrapperSynthesizerListener() {
                        @Override
                        public void onCompleted() {
                            closeVoicePopup();
                            Bundle bundle = new Bundle();
                            bundle.putString(CenterConstants.DATE, CenterConstants.ASSISTANT_MARK);
                            LaunchUtils.launchAppOnlyNewTask(context, CenterConstants.LAUNCHER,
                                    "com.xiaoma.launcher.mark.ui.activity.MarkMainActivity",
                                    bundle);
                        }
                    });
                } else {
                    speakMultiToneListening(getString(R.string.no_device_speak), getString(R.string.no_device));
                }
                return;
            case "TAKE_VIDEO"://行车记录仪摄像
                break;
            case "MODE_DAY"://屏幕白天模式
                SettingApiManager.getInstance().changeDisplayMode(Constants.SystemSetting.DISPLAY_MODEL_DAYLIGHT);
                speakText = context.getString(R.string.set_mode_day);
                XmCarVendorExtensionManager.getInstance().setDisplayMode(SDKConstants.DISPLAYSCREENMODE_DAY);
                setRobAction(AssistantConstants.RobActionKey.ROB_ACTION_VOLUME_SETTING);
                break;
            case "MODE_NIGHT"://屏幕黑夜模式
                SettingApiManager.getInstance().changeDisplayMode(Constants.SystemSetting.DISPLAY_MODEL_NIGHT);
                speakText = context.getString(R.string.set_mode_night);
                XmCarVendorExtensionManager.getInstance().setDisplayMode(SDKConstants.DISPLAYSCREENMODE_NIGHT);
                setRobAction(AssistantConstants.RobActionKey.ROB_ACTION_VOLUME_SETTING);
                break;
            case "MODE_AUTO"://屏幕自动模式
                SettingApiManager.getInstance().changeDisplayMode(Constants.SystemSetting.DISPLAY_MODEL_CHANGE);
                speakText = context.getString(R.string.set_mode_auto);
                XmCarVendorExtensionManager.getInstance().setDisplayMode(SDKConstants.DISPLAYSCREENMODE_AUTO);
                setRobAction(AssistantConstants.RobActionKey.ROB_ACTION_VOLUME_SETTING);
                break;
            case "MODE_CHANGE":
//                SettingApiManager.getInstance().changeDisplayMode(Constants.SystemSetting.DISPLAY_MODEL_CHANGE);
                int curDisplayModel = XmCarVendorExtensionManager.getInstance().getDisplayMode();
                XmCarVendorExtensionManager.getInstance().setDisplayMode(curDisplayModel == Constants.SystemSetting.DISPLAY_MODEL_DAYLIGHT ? Constants.SystemSetting.DISPLAY_MODEL_NIGHT : Constants.SystemSetting.DISPLAY_MODEL_DAYLIGHT);
                break;
            case "HOMEPAGE"://返回主界面
                speakText = okHomeAnswers[new Random().nextInt(2)];
                returnToWindow();
                break;
            case "BRIGHTNESS_MINUS"://降低屏幕亮度
//                SettingApiManager.getInstance().changeBrightness(Constants.SystemSetting.BRIGHTNESS_DOWN);
                setRobAction(AssistantConstants.RobActionKey.ROB_ACTION_VOLUME_SETTING);
                if (displayLevel > 2) {
                    XmCarVendorExtensionManager.getInstance().setDisplayLevel(displayLevel - BRIGHTNESS_CHANGE_RATE);
                    speakText = getString(R.string.brightness_down);
                } else if (displayLevel == 2) {
                    XmCarVendorExtensionManager.getInstance().setDisplayLevel(1);
                    speakText = getString(R.string.brightness_min);
                } else if (displayLevel == 1) {
                    speakText = getString(R.string.already_min_brightness);
                }
                break;
            case "BRIGHTNESS_PLUS"://调亮
                setRobAction(AssistantConstants.RobActionKey.ROB_ACTION_VOLUME_SETTING);
//                SettingApiManager.getInstance().changeBrightness(Constants.SystemSetting.BRIGHTNESS_UP);
                if (displayLevel < MAX_BRIGHTNESS - 1) {
                    XmCarVendorExtensionManager.getInstance().setDisplayLevel(displayLevel + BRIGHTNESS_CHANGE_RATE);
                    speakText = getString(R.string.brightness_up);
                } else if (displayLevel == MAX_BRIGHTNESS - 1) {
                    XmCarVendorExtensionManager.getInstance().setDisplayLevel(MAX_BRIGHTNESS);
                    speakText = getString(R.string.brightness_max);
                } else if (displayLevel == MAX_BRIGHTNESS) {
                    speakText = getString(R.string.already_max_brightness);

                }
                break;
            case "OPEN_SCREEN"://点亮屏：
//                SettingApiManager.getInstance().changeBrightness(Constants.SystemSetting.BRIGHTNESS_ON);
//                XmCarVendorExtensionManager.getInstance().turnOnScreen();
                setRobAction(36);
                ScreenControlUtil.sendTurnOnScreenBroadCast(context);
                speakText = getString(R.string.open_screen);
                break;
            case "CLOSE_SCREEN"://关闭屏：
//                SettingApiManager.getInstance().changeBrightness(Constants.SystemSetting.BRIGHTNESS_OFF);
//                XmCarVendorExtensionManager.getInstance().closeScreen();
                setRobAction(36);
                ScreenControlUtil.sendTurnOffScreenBroadCast(context);
                speakText = getString(R.string.close_screen_call_when_need);
                break;
            case "BRIGHTNESS_MAX"://屏幕最亮
                setRobAction(AssistantConstants.RobActionKey.ROB_ACTION_VOLUME_SETTING);
                XmCarVendorExtensionManager.getInstance().setDisplayLevel(MAX_BRIGHTNESS);
                speakText = getString(R.string.brightness_max);
                break;
            case "BRIGHTNESS_MIN":
                setRobAction(AssistantConstants.RobActionKey.ROB_ACTION_VOLUME_SETTING);
                XmCarVendorExtensionManager.getInstance().setDisplayLevel(MIN_BRIGHTNESS);
                speakText = getString(R.string.brightness_min);
                break;
            case "VOLUME_MINUS"://声音小点
                speakText = dispatchVolume(Constants.SystemSetting.VOLUME_DOWN, -1, tag);
                setRobAction(AssistantConstants.RobActionKey.ROB_ACTION_VOLUME_SETTING);
                break;
            case "VOLUME_PLUS"://声音大点
                speakText = dispatchVolume(Constants.SystemSetting.VOLUME_UP, -1, tag);
                setRobAction(AssistantConstants.RobActionKey.ROB_ACTION_VOLUME_SETTING);
                break;
            case "VOLUME_MUTE"://静音：
                setRobAction(AssistantConstants.RobActionKey.ROB_ACTION_VOLUME_SETTING);
                seMute(true);
                speakText = "";
                break;
            case "VOLUME_UNMUTE"://取消静音：
                setRobAction(AssistantConstants.RobActionKey.ROB_ACTION_VOLUME_SETTING);
                seMute(false);
                speakText = "";
                break;
            case "VOLUME_MAX"://音量最大
                speakText = dispatchVolume(Constants.SystemSetting.VOLUME_MAX, -1, tag);
                break;
            case "VOLUME_MIN"://音量最小
                speakText = dispatchVolume(Constants.SystemSetting.VOLUME_MIN, -1, tag);
                break;
            case "VOLUME_ADJUST"://具体音量
                int series = -1;
                if (jsonObject.has("series")) {
                    series = jsonObject.getInt("series");
                }
                if (series < 0 || series > 40) {
                    speakText = context.getString(R.string.valid_volume);
                    break;
                }
                setRobAction(AssistantConstants.RobActionKey.ROB_ACTION_VOLUME_SETTING);
                speakText = dispatchVolume(Constants.SystemSetting.VOLUME_SPECIFIC_NUM, series, tag);
                if (series == 0) {//series == 0，相当于静音，不应该有tts播报
                    closeVoicePopup();
                    return;
                }
                break;
            case "MODE_SSF"://声场模式设置成 标准
                XmCarVendorExtensionManager.getInstance().setSoundFieldMode(SDKConstants.VALUE.STANDARD);
                setRobAction(AssistantConstants.RobActionKey.ROB_ACTION_VOLUME_SETTING);
                speakText = context.getString(R.string.switch_to_standard_mode);
                break;
            case "MODE_OHSF"://声场模式设置成 歌剧院
                XmCarVendorExtensionManager.getInstance().setSoundFieldMode(SDKConstants.VALUE.CINEMA);
                setRobAction(AssistantConstants.RobActionKey.ROB_ACTION_VOLUME_SETTING);
                speakText = context.getString(R.string.switch_to_opera_house_mode);
                break;
            case "MODE_CHSF"://声场模式设置成 音乐厅
                XmCarVendorExtensionManager.getInstance().setSoundFieldMode(SDKConstants.VALUE.ODEUM);
                setRobAction(AssistantConstants.RobActionKey.ROB_ACTION_VOLUME_SETTING);
                speakText = context.getString(R.string.switch_to_odeum_mode);
                break;
            case "MODE_SSE"://音效模式设置成 标准
                XmCarVendorExtensionManager.getInstance().setArkamys3D(SDKConstants.VALUE.ARKAMYS_3D_OFF);
                XmCarVendorExtensionManager.getInstance().setSoundEffectsMode(SDKConstants.VALUE.SOUND_EFFECTS_STANDARD);
                setRobAction(AssistantConstants.RobActionKey.ROB_ACTION_VOLUME_SETTING);
                speakText = context.getString(R.string.switch_to_standard_sound_effect_mode);
                break;
            case "MODE_PSE"://音效模式设置成 流行
                XmCarVendorExtensionManager.getInstance().setArkamys3D(SDKConstants.VALUE.ARKAMYS_3D_OFF);
                XmCarVendorExtensionManager.getInstance().setSoundEffectsMode(SDKConstants.VALUE.SOUND_EFFECTS_POP);
                setRobAction(AssistantConstants.RobActionKey.ROB_ACTION_VOLUME_SETTING);
                speakText = context.getString(R.string.switch_to_pop_sound_effect_mode);
                break;
            case "MODE_CLSE"://音效模式设置成 古典
                XmCarVendorExtensionManager.getInstance().setArkamys3D(SDKConstants.VALUE.ARKAMYS_3D_OFF);
                XmCarVendorExtensionManager.getInstance().setSoundEffectsMode(SDKConstants.VALUE.SOUND_EFFECTS_CLASSIC);
                setRobAction(AssistantConstants.RobActionKey.ROB_ACTION_VOLUME_SETTING);
                speakText = context.getString(R.string.switch_to_classic_sound_effect_mode);
                break;
            case "MODE_JSE"://音效模式设置成 爵士
                XmCarVendorExtensionManager.getInstance().setArkamys3D(SDKConstants.VALUE.ARKAMYS_3D_OFF);
                XmCarVendorExtensionManager.getInstance().setSoundEffectsMode(SDKConstants.VALUE.SOUND_EFFECTS_JAZZ);
                setRobAction(AssistantConstants.RobActionKey.ROB_ACTION_VOLUME_SETTING);
                speakText = context.getString(R.string.switch_to_jazz_sound_effect_mode);
                break;
            case "MODE_CUSE"://音效模式设置成 自定义
                XmCarVendorExtensionManager.getInstance().setArkamys3D(SDKConstants.VALUE.ARKAMYS_3D_OFF);
                XmCarVendorExtensionManager.getInstance().setSoundEffectsMode(SDKConstants.VALUE.SOUND_EFFECTS_USER);
                setRobAction(AssistantConstants.RobActionKey.ROB_ACTION_VOLUME_SETTING);
                speakText = context.getString(R.string.switch_to_customize_sound_effect_mode);
                break;
            case "OPEN_STSE"://开启Arkamys3D音效
                XmCarVendorExtensionManager.getInstance().setArkamys3D(SDKConstants.VALUE.ARKAMYS_3D_ALL_ON);
                setRobAction(AssistantConstants.RobActionKey.ROB_ACTION_VOLUME_SETTING);
                speakText = context.getString(R.string.opend_3d_sound_effect);
                break;
            case "CLOSE_STSE"://关闭Arkamys3D音效
                XmCarVendorExtensionManager.getInstance().setArkamys3D(SDKConstants.VALUE.ARKAMYS_3D_OFF);
                setRobAction(AssistantConstants.RobActionKey.ROB_ACTION_VOLUME_SETTING);
                speakText = context.getString(R.string.closed_3d_sound_effect);
                break;
            case "KEYPADLIGHT_PLUS"://调高按键亮度
            {
                int keyBoardLevel = XmCarVendorExtensionManager.getInstance().getKeyBoardLevel();
                if (keyBoardLevel < MAX_BRIGHTNESS - 1) {
                    XmCarVendorExtensionManager.getInstance().setKeyBoardLevel(keyBoardLevel + 2);
                    addContentCloseWindow(context.getString(R.string.raise__keypad_light));
                } else if (keyBoardLevel == MAX_BRIGHTNESS - 1) {
                    XmCarVendorExtensionManager.getInstance().setKeyBoardLevel(MAX_KEYBOARD_BRIGHTNESS_LEVEL);
                    addContentCloseWindow(context.getString(R.string.max_keypad_light));
                } else {
                    addContentCloseWindow(context.getString(R.string.already_max_keypad_light));
                }
                setRobAction(AssistantConstants.RobActionKey.ROB_ACTION_VOLUME_SETTING);
                return;
            }
            case "KEYPADLIGHT_MINUS"://调低按键亮度
                int keyBoardLevel = XmCarVendorExtensionManager.getInstance().getKeyBoardLevel();
                if (keyBoardLevel < 1) {
                    addContentCloseWindow(context.getString(R.string.already_min_keypad_light));
                } else if (keyBoardLevel == 1) {
                    XmCarVendorExtensionManager.getInstance().setKeyBoardLevel(MIN_KEYBOARD_BRIGHTNESS_LEVEL);
                    addContentCloseWindow(context.getString(R.string.min_keypad_light));
                } else {
                    XmCarVendorExtensionManager.getInstance().setKeyBoardLevel(keyBoardLevel - 2);
                    addContentCloseWindow(context.getString(R.string.lower_keypad_light));
                }
                setRobAction(AssistantConstants.RobActionKey.ROB_ACTION_VOLUME_SETTING);
                return;
            case "VITS_MAX"://车辆信息提示音设置为“大”
                XmCarVendorExtensionManager.getInstance().setCarInfoSound(SDKConstants.VALUE.INFORMATION_TONE_LEVER_LARGER);
                setRobAction(AssistantConstants.RobActionKey.ROB_ACTION_CARMESSAGE_VOLUME_SETTING);
                speakText = context.getString(R.string.information_tone_lever_larger);
                break;
            case "VITS_MIN"://车辆信息提示音设置为“小”
                XmCarVendorExtensionManager.getInstance().setCarInfoSound(SDKConstants.VALUE.INFORMATION_TONE_LEVER_NORMAL);
                setRobAction(AssistantConstants.RobActionKey.ROB_ACTION_CARMESSAGE_VOLUME_SETTING);
                speakText = context.getString(R.string.information_tone_lever_normal);
                break;
            case "OPEN_COLWARNING"://开启前碰撞预警
                setRobAction(35);
                XmCarVendorExtensionManager.getInstance().setFcwAebSwitch(SDKConstants.VALUE.FCW_ON_REQ);
                speakText = context.getString(R.string.open_colwarning);
                setRobAction(AssistantConstants.RobActionKey.ROB_ACTION_OPEN_COLWARNING);
                break;
            case "CLOSE_COLWARNING"://关闭前碰撞预警
                setRobAction(36);
                XmCarVendorExtensionManager.getInstance().setFcwAebSwitch(SDKConstants.VALUE.FCW_OFF_REQ);
                speakText = context.getString(R.string.close_colwarning);
                setRobAction(AssistantConstants.RobActionKey.ROB_ACTION_CLOSE_COLWARNING);
                break;
            case "CHANGE_THEME"://切换主题
                if (!XmCarSensorManager.getInstance().isConditionMeet()) {
                    speakText = mContext.getString(R.string.switch_skin_error);
                } else {
                    setRobAction(29);
//                    int theme = XmCarVendorExtensionManager.getInstance().getTheme();
                    int theme = getCurIndex();
                    int index = 0;
                    for (int i = 0; i < themeList.size(); i++) {
                        if (themeList.get(i).id == theme) {
                            index = i;
                        }
                    }
                    index = (index == themeList.size() - 1) ? 0 : index + 1;
                    XmCarVendorExtensionManager.getInstance().setTheme(themeList.get(index).id);
                    setSkin(index);
                    speakText = StringUtil.format(context.getString(R.string.set_theme), themeList.get(index).name);
                    setRobAction(AssistantConstants.RobActionKey.ROB_ACTION_SYSTEM_SETTINGS);
                }
                break;
            case "SET_THEME"://设置指定主题
                if (!XmCarSensorManager.getInstance().isConditionMeet()) {
                    speakText = mContext.getString(R.string.switch_skin_error);
                } else {
                    setRobAction(29);
                    if (jsonObject.has("category")) {
                        String category = jsonObject.getString("category");
                        switch (category) {
                            case "智慧":
                                XmSkinManager.getInstance().restoreDefault(mContext);
                                XmCarVendorExtensionManager.getInstance().setTheme(SkinConstants.THEME_ZHIHUI);
                                speakText = context.getString(R.string.set_theme_humanity);
                                setRobAction(AssistantConstants.RobActionKey.ROB_ACTION_SYSTEM_SETTINGS);
                                break;
                            case "轻奢":
                                XmSkinManager.getInstance().loadSkinByName(mContext, "skinId", SkinConstants.SKIN_NAME_QINGSHE);
                                XmCarVendorExtensionManager.getInstance().setTheme(SkinConstants.THEME_QINGSHE);
                                speakText = context.getString(R.string.set_theme_luxury);
                                setRobAction(AssistantConstants.RobActionKey.ROB_ACTION_SYSTEM_SETTINGS);
                                break;
                            case "盗梦":
                                XmSkinManager.getInstance().loadSkinByName(mContext, "skinId", SkinConstants.SKIN_NAME_DAOMENG);
                                XmCarVendorExtensionManager.getInstance().setTheme(SkinConstants.THEME_DAOMENG);
                                speakText = context.getString(R.string.set_theme_inception);
                                setRobAction(AssistantConstants.RobActionKey.ROB_ACTION_SYSTEM_SETTINGS);
                                break;
                        }
                    }
                }
                break;
            case "OPEN_NETSYN"://开启网络同步时间开关
                setRobAction(35);
                XmCarVendorExtensionManager.getInstance().setSynchronizeTimeSwitch(true);
                speakText = context.getString(R.string.turn_on_sychronize_time_switch);
                setRobAction(AssistantConstants.RobActionKey.ROB_ACTION_OPEN_NETSYN);
                break;
            case "CLOSE_NETSYN"://关闭网络同步时间开关
                setRobAction(36);
                XmCarVendorExtensionManager.getInstance().setSynchronizeTimeSwitch(false);
                speakText = context.getString(R.string.turn_off_sychronize_time_switch);
                setRobAction(AssistantConstants.RobActionKey.ROB_ACTION_CLOSE_NETSYN);
                break;
            case "OPEN_RMAFOLDING"://开启后视镜自动折叠
                setRobAction(35);
                XmCarCabinManager.getInstance().setRearviewMirror(true);
                speakText = context.getString(R.string.open_rmafolding);
                setRobAction(AssistantConstants.RobActionKey.ROB_ACTION_OPEN_RMAFOLDING);
                break;
            case "CLOSE_RMAFOLDING"://关闭后视镜自动折叠
                setRobAction(36);
                XmCarCabinManager.getInstance().setRearviewMirror(false);
                speakText = context.getString(R.string.close_rmafolding);
                setRobAction(AssistantConstants.RobActionKey.ROB_ACTION_CLOSE_RMAFOLDING);
                break;
            case "OPEN_BRLBW"://打开后排安全带未系提醒开关
                setRobAction(35);
                XmCarCabinManager.getInstance().setRearBeltWorningSwitch(SDKConstants.VALUE.REAR_BELT_ON_REQ);
                speakText = context.getString(R.string.open_rear_belt_worning);
                setRobAction(AssistantConstants.RobActionKey.ROB_ACTION_OPEN_BRLBW);
                break;
            case "CLOSE_BRLBW"://关闭后排安全带未系提醒开关
                setRobAction(36);
                XmCarCabinManager.getInstance().setRearBeltWorningSwitch(SDKConstants.VALUE.REAR_BELT_OFF);
                speakText = context.getString(R.string.close__rear_belt_worning);
                setRobAction(AssistantConstants.RobActionKey.ROB_ACTION_CLOSE_BRLBW);
                break;
            case "CANCEL"://取消
            case "CLOSE"://取消
            case "SLEEP"://取消
                closeAssistant();
                return;
            default:
        }
        if (TextUtils.isEmpty(speakText)) {
            closeVoicePopup();
        } else {
            closeAfterSpeak(speakText);
        }
    }

    public void closeAssistant() {
        XmTtsManager.getInstance().stopSpeaking();
        addFeedbackAndSpeak(mContext.getString(R.string.see_you), new WrapperSynthesizerListener() {
            @Override
            public void onCompleted() {
                AssistantManager.getInstance().closeAssistant();
            }
        });

    }

    private void setSkin(int index) {
        if (index == 0) {
            XmSkinManager.getInstance().restoreDefault(mContext);
        } else if (index == 1) {
            XmSkinManager.getInstance().loadSkinByName(mContext, "skinId", SkinConstants.SKIN_NAME_QINGSHE);
        } else if (index == 2) {
            XmSkinManager.getInstance().loadSkinByName(mContext, "skinId", SkinConstants.SKIN_NAME_DAOMENG);
        }
    }

    private int getCurIndex() {
        int index = 0;
        String curSkinName = XmSkinManager.getInstance().getCurSkinName();
        if (TextUtils.isEmpty(curSkinName) || SkinConstants.SKIN_NAME_RENWEN.equalsIgnoreCase(curSkinName)) {
            index = 0;
        } else if (SkinConstants.SKIN_NAME_QINGSHE.equalsIgnoreCase(curSkinName)) {
            index = 1;
        } else if (SkinConstants.SKIN_NAME_DAOMENG.equalsIgnoreCase(curSkinName)) {
            index = 2;
        }
        return index;
    }

    private void setSystemLanguage(LxParseResult parseResult) throws JSONException {
        JSONObject jsonObject = new JSONObject(parseResult.getSlots());
        String speakContent = getString(R.string.language_set_failure);
        if (jsonObject.has("category")) {
            String dance = jsonObject.getString("category");
            if ("English".equals(dance)) {
                setLanguageAfterSpeak(R.string.already_set_en, SDKConstants.LANGUAGE_EN);
                return;
            } else if ("Chinese".equals(dance)) {
                setLanguageAfterSpeak(R.string.already_set_cn, SDKConstants.LANGUAGE_CH);
                return;
            }
        }
        speakContent(speakContent);
    }

    private void setLanguageAfterSpeak(int resId, int languageId) {
        String speakContent = getString(resId);
        ThreadDispatcher.getDispatcher().postDelayed(new Runnable() {
            @Override
            public void run() {
                XmCarFactory.getCarVendorExtensionManager().setLanguage(languageId);
            }
        }, 1300);
        addFeedbackAndSpeak(speakContent, new WrapperSynthesizerListener() {
            @Override
            public void onCompleted() {
                AssistantManager.getInstance().closeAssistant();
            }

            @Override
            public void onError(int code) {
                AssistantManager.getInstance().closeAssistant();
            }
        });
    }

    int[] intDance = new int[]{5, 6, 7};

    private void skillPerformance(LxParseResult parseResult) throws JSONException {
        JSONObject jsonObject = new JSONObject(parseResult.getSlots());
        if (XmCarConfigManager.hasHologram()) {

            if (jsonObject.has("dance")) {
                String dance = jsonObject.getString("dance");
                if ("random".equals(dance)) {
                    setRobAction(intDance[new Random().nextInt(intDance.length)]);
                    closeAfterSpeak(getString(R.string.dance_speak));
                }
            }
            if (jsonObject.has("program")) {
                String dance = jsonObject.getString("program");
                if ("random".equals(dance)) {
                    setRobAction(intDance[new Random().nextInt(intDance.length)]);
                    closeAfterSpeak(getString(R.string.dance_speak));
                }
            }
            if (jsonObject.has("exclude#program")) {
                String exclude = jsonObject.getString("exclude#program");
                if ("CURRENT".equals(exclude)) {
                    setRobAction(intDance[new Random().nextInt(intDance.length)]);
                    closeAfterSpeak(getString(R.string.dance_speak));
                }
            }

        } else {
            closeAfterSpeak(danceSpeak[new Random().nextInt(danceSpeak.length)]);
        }
    }

    public void setRobAction(int action) {
        XmCarFactory.getCarVendorExtensionManager().setRobAction(action);
    }

    public String dispatchVolume(int volumeOperation, int volumeValue, String tag) {
        if (volumeOperation == Constants.SystemSetting.VOLUME_MUTE || volumeOperation == Constants.SystemSetting.VOLUME_MUTE_CANCELED) {
            int mediaVolume;
            //int voiceVolume;
            //int phoneVolume;
            int bluetoothVolume;
            if (volumeOperation == Constants.SystemSetting.VOLUME_MUTE) {
                mediaVolume = XmCarFactory.getCarAudioManager().getStreamVolume(SDKConstants.MEDIA_VOLUME);
                // voiceVolume = XmCarFactory.getCarAudioManager().getStreamVolume(SDKConstants.TTS_VOLUME);
                // phoneVolume = XmCarFactory.getCarAudioManager().getStreamVolume(SDKConstants.PHONE_VOLUME);
                bluetoothVolume = XmCarFactory.getCarAudioManager().getStreamVolume(SDKConstants.BT_MEDIA_VOLUME);
                SharedPreferences.Editor edit = sp.edit();
                edit.putInt(VolumeType.MEDIA.value, mediaVolume);
                // edit.putInt(VolumeType.VOICE.value, voiceVolume);
                // edit.putInt(VolumeType.PHONE.value, phoneVolume);
                edit.putInt(VolumeType.BLUETOOTHMEDIA.value, bluetoothVolume).apply();
                mediaVolume = 0;
                // voiceVolume = 0;
                //phoneVolume = 0;
                bluetoothVolume = 0;
            } else {
                mediaVolume = sp.getInt(VolumeType.MEDIA.value, 1);
                if (mediaVolume == 0) mediaVolume = 1;
                //voiceVolume = sp.getInt(VolumeType.VOICE.value, 1);
                //  if (voiceVolume == 0) voiceVolume = 1;
                //phoneVolume = sp.getInt(VolumeType.PHONE.value, 1);
                //if (phoneVolume == 0) phoneVolume = 1;
                bluetoothVolume = sp.getInt(VolumeType.BLUETOOTHMEDIA.value, 1);
                if (bluetoothVolume == 0) bluetoothVolume = 1;
            }

            // XmCarFactory.getCarAudioManager().setStreamVolume(SDKConstants.PHONE_VOLUME, phoneVolume);
            XmCarFactory.getCarAudioManager().setStreamVolume(SDKConstants.MEDIA_VOLUME, mediaVolume);
            XmCarFactory.getCarAudioManager().setStreamVolume(SDKConstants.BT_MEDIA_VOLUME, bluetoothVolume);
            //XmCarFactory.getCarAudioManager().setStreamVolume(SDKConstants.MEDIA_VOLUME, voiceVolume);
         /*   if (assistantManager.isShowing()) {
                assistantManager.startListening();
            }*/
            return getString(R.string.ok);
        }
        int streamType = -1;
        int max_volume = 40;
        if (!TextUtils.isEmpty(tag)) {
            switch (tag) {
                case "media":
                    streamType = SDKConstants.MEDIA_VOLUME;
                    max_volume = MAX_MEDIA_VOLUME;
                    mVolumeChangeRate = 2;
                    break;
                case "voice":
                    streamType = SDKConstants.TTS_VOLUME;
                    max_volume = MAX_TTS_VOLUME;
                    mVolumeChangeRate = 1;
                    break;
                case "telephone":
                    streamType = SDKConstants.PHONE_VOLUME;
                    max_volume = MAX_CALL_VOLUME;
                    mVolumeChangeRate = 1;
                    break;
                case "bluetooth":
                    streamType = SDKConstants.BT_MEDIA_VOLUME;
                    max_volume = MAX_BLUETOOTH_VOLUME;
                    mVolumeChangeRate = 2;
                    break;
                case "bluetoothMusic":
                    streamType = SDKConstants.BT_MEDIA_VOLUME;
                    max_volume = MAX_BLUETOOTH_VOLUME;
                    mVolumeChangeRate = 2;
                    break;
            }
        }
        if (streamType == -1) {
            mVolumeChangeRate = 2;
            String textMedia = setVolume(volumeOperation, volumeValue, tag, SDKConstants.MEDIA_VOLUME, MAX_MEDIA_VOLUME);
            String textBT = setVolume(volumeOperation, volumeValue, tag, SDKConstants.BT_MEDIA_VOLUME, MAX_BLUETOOTH_VOLUME);
            if (AudioManager.getInstance().isBluetoothMusicPlaying()) {
                return textBT;
            } else {
                return textMedia;
            }


        } else {
            return setVolume(volumeOperation, volumeValue, tag, streamType, max_volume);
        }
    }

    private String setVolume(int volumeOperation, int volumeValue, String tag, int streamType, int max_volume) {
        if (volumeOperation == Constants.SystemSetting.VOLUME_SPECIFIC_NUM) {
            XmCarFactory.getCarAudioManager().setStreamVolume(streamType, volumeValue);
            return getString(R.string.ok);
        }
        int curStreamVolume = XmCarFactory.getCarAudioManager().getStreamVolume(streamType);
        KLog.d("hzx", "当前声音渠道: " + tag + ", 当前音量: " + curStreamVolume);
        if (volumeOperation == Constants.SystemSetting.VOLUME_UP) {
            if (curStreamVolume < max_volume - mVolumeChangeRate) {
                XmCarFactory.getCarAudioManager().setStreamVolume(streamType, curStreamVolume + mVolumeChangeRate);
                return getString(R.string.volume_up);
            } else if (curStreamVolume == max_volume - 1) {
                XmCarFactory.getCarAudioManager().setStreamVolume(streamType, max_volume);
                return getString(R.string.volume_up);
            } else if (curStreamVolume == max_volume) {
                return getString(R.string.volume_already_max);
            }
        } else if (volumeOperation == Constants.SystemSetting.VOLUME_DOWN) {
            if (curStreamVolume >= 2) {
                XmCarFactory.getCarAudioManager().setStreamVolume(streamType, curStreamVolume - mVolumeChangeRate > 1 ? curStreamVolume - mVolumeChangeRate : 1);
                return getString(R.string.volume_down);
            } else {
                return getString(R.string.volume_already_min);
            }
        }
        /*if (assistantManager.isShowing()) {
            assistantManager.startListening();
        }*/
        return null;
    }

    public String getStreamText(int streamID) {
        switch (streamID) {
            case SDKConstants.TTS_VOLUME:
                return "voice";
            case SDKConstants.PHONE_VOLUME:
                return "telephone";
            case SDKConstants.BT_MEDIA_VOLUME:
                return "bluetooth";
            case SDKConstants.MEDIA_VOLUME:
            default:
                return "media";
        }
    }

    private void OpenVoiceSetting() {
        // TODO 打开应用指定页面
//        OpenAppUtils.openApp(context, Constants.SystemSetting.PACKAGE_NAME_SETTING,
//                context.getString(R.string.app_not_install));
//        chooseSpecificPageOfSetting(Constants.SystemSetting.FragmentPage.ASSISTANT_ACTIVITY);
        boolean isSuccess = NodeUtils.jumpTo(context, Constants.SystemSetting.PACKAGE_NAME_SETTING,
                "com.xiaoma.setting.main.ui.MainActivity",
                NodeConst.Setting.ASSISTANT_ACTIVITY + "/" + NodeConst.Setting.ASSISTANT_FRAGMENT);
        if (isSuccess) {
            assistantManager.closeAssistant();
        } else {
            assistantManager.speakContent(getString(R.string.setting_uninstall));
        }
    }

    private void closeVoiceSetting() {
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Constants.CLOSE_APP + Constants.SystemSetting.PACKAGE_NAME_SETTING);
//            intent.putExtra(Constants.PACKAGE_NAME, packageName);
        context.sendBroadcast(intent);
    }


    private void addContentCloseWindow(String speakContent) {
        addFeedbackAndSpeak(speakContent, new WrapperSynthesizerListener() {
            @Override
            public void onCompleted() {
                AssistantManager.getInstance().closeAssistant();
            }
        });
    }


    private void chooseSpecificPageOfSetting(int page) {
        SettingApiManager.getInstance().chooseSpecificPage(page);
    }

    private String[] okHomeAnswers = {"好嘞", "嗯"};

    public void returnToWindow() {
        Intent intent = new Intent();
        intent.setAction(ConfigConstants.NAVIBARWINDOW_CLOSE_ACTION);
        context.sendBroadcast(intent);
        Intent home = new Intent(Intent.ACTION_MAIN);
        home.addCategory(Intent.CATEGORY_HOME);
        home.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(home);
        setRobAction(AssistantConstants.RobActionKey.ROB_ACTION_CLOSE_APP);
    }

    private void parseChange(LxParseResult parseResult) throws JSONException {
        JSONObject jsonObject = new JSONObject(parseResult.getSlots());
        if (XmCarConfigManager.hasHologram()) {
            //服装选择
            if (jsonObject.has("clothes")) {
                String clothes = jsonObject.getString("clothes");
                if ("random".equals(clothes)) {
                    switchCloth();
                    return;
                }
            }
            //我不喜欢这套衣服
            if (jsonObject.has("exclude#clothes")) {
                String exclude = jsonObject.getString("exclude#clothes");
                if ("CURRENT".equals(exclude)) {
                    switchCloth();
                    return;
                }
            }
        } else {
            speakMultiToneListening(getString(R.string.not_clother_speak), getString(R.string.not_clother));
        }
        if (jsonObject.has("speaker")) {
            String speaker = jsonObject.getString("speaker");
            if ("random".equals(speaker)) {
                //随机发音人
            } else {

            }
        }
    }

    private void switchCloth() {
        fetchClothIdFromLocal();//
    }

    private void fetchClothIdFromNet() {
        //获取holoId
        int usingRoleId = HologramRepo.getUsingHoloId(context);
        if (usingRoleId != HologramRepo.DEFAULT_HOLO_ID) {
            fetchClothes(usingRoleId);
        } else {
            useCloth(HologramRepo.DEFAULT_CLOTH_ID);
        }
    }

    private void fetchClothIdFromLocal() {
        int usingClothId = HologramRepo.getUsingClothId(context, HologramRepo.getUsingRoleId(context));
        int[] clothIds = HolopramHelper.getInstance().getDatas().get(HologramRepo.getUsingRoleId(context));
        if (clothIds == null) {
            useCloth(HologramRepo.DEFAULT_CLOTH_ID);
            return;
        }
        int index = -1;
        int clothId;
        for (int i = 0; i < clothIds.length; i++) {
            if (usingClothId == clothIds[i]) {
                index = i;
                break;
            }
        }

        //切换到下一个服饰
        if (index == -1) {
            clothId = HologramRepo.DEFAULT_CLOTH_ID;
        } else if (index + 1 >= clothIds.length) {
            index = 0;
            clothId = clothIds[index];
        } else {
            index++;
            clothId = clothIds[index];
        }
        useCloth(clothId);
    }

    private void fetchClothes(int holoId) {
        RequestManager.newSingleton().requestHoloManInfo(holoId, new StringCallback() {
            @Override
            public void onSuccess(Response<String> response) {
                String content = response.body();
                if (TextUtils.isEmpty(content)) {
                    useCloth(HologramRepo.DEFAULT_CLOTH_ID);
                    return;
                }
                parseClothes(content, holoId);
            }

            @Override
            public void onError(Response<String> response) {
                useCloth(HologramRepo.DEFAULT_CLOTH_ID);
            }
        });
    }

    private void parseClothes(String content, int holoId) {
        KLog.i("filOut| " + "[parseClothes]->开始解释文本");
        try {
            JSONObject jsonObject = new JSONObject(content);
            String data = jsonObject.getString("data");
            String resultCode = jsonObject.getString("resultCode");
            if (!resultCode.equals("1")) {
                useCloth(HologramRepo.DEFAULT_CLOTH_ID);
                return;
            }
            HoloManInfo holoManInfo = GsonHelper.fromJson(data, HoloManInfo.class);
            if (holoManInfo == null) {
                useCloth(HologramRepo.DEFAULT_CLOTH_ID);
                return;
            }
            List<HologramDress> holo_clothes = holoManInfo.getChildItems().getHolo_clothes();
            if (ListUtils.isEmpty(holo_clothes)) {
                useCloth(HologramRepo.DEFAULT_CLOTH_ID);
                return;
            }
            // 获取当前服饰id
            int index = -1;
            int clothId;
            String usingClothId = HologramRepo.getUsingClothId(context, HologramRepo.getUsingRoleId(context)) + "";
            for (int i = 0; i < holo_clothes.size(); i++) {
                if (holo_clothes.get(i).getCode().equals(usingClothId)) {
                    index = i;
                }
            }
            //切换到下一个服饰
            if (index == -1) {
                clothId = HologramRepo.DEFAULT_CLOTH_ID;
            } else if (index + 1 >= holo_clothes.size()) {
                index = 0;
                clothId = Integer.parseInt(holo_clothes.get(index).getCode());
            } else {
                index++;
                clothId = Integer.parseInt(holo_clothes.get(index).getCode());
            }
            useCloth(clothId);
            KLog.i("filOut| " + "[parseClothes]->使用服饰 index " + index);
        } catch (Exception e) {
            useCloth(HologramRepo.DEFAULT_CLOTH_ID);
        }
    }

    private void useCloth(int clothId) {
        KLog.i("filOut| " + "[useCloth]->服饰id " + clothId + "  角色roleId " + HologramRepo.getUsingRoleId(context));
        setRobAction(AssistantConstants.RobActionKey.ROB_ACTION_CHANGE_CLOTHER);
        XmCarVendorExtensionManager.getInstance().setRobClothMode(clothId);
        HolopramHelper.getInstance().updateClothChange(context, HologramRepo.getUsingRoleId(context), clothId);
        closeAfterSpeak(changeClothes[new Random().nextInt(changeClothes.length)]);
    }


    private int getFinishWord() {
        Random random = new Random();
        int index = random.nextInt(finishWord.length);
        return finishWord[index];
    }

    public void seMute(boolean isMute) {
        XmCarFactory.getCarAudioManager().setCarMasterMute(isMute);
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

    enum VolumeType {
        MEDIA("media"),
        PHONE("phone"),
        BLUETOOTHMEDIA("bluetooth_media"),
        VOICE("voice");

        private String value;

        VolumeType(String value) {
            this.value = value;
        }

    }

    class Theme {
        public int id;
        public String name;

        public Theme(int id, String name) {
            this.id = id;
            this.name = name;
        }
    }
}

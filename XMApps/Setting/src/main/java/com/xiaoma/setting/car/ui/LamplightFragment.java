package com.xiaoma.setting.car.ui;

import android.annotation.DrawableRes;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.SeekBar;

import com.xiaoma.carlib.constant.SDKConstants;
import com.xiaoma.carlib.manager.XmCarConfigManager;
import com.xiaoma.component.base.BaseFragment;
import com.xiaoma.model.annotation.PageDescComponent;
import com.xiaoma.setting.R;
import com.xiaoma.setting.car.vm.LamplightVM;
import com.xiaoma.setting.common.constant.EventConstants;
import com.xiaoma.setting.common.constant.SettingConstants;
import com.xiaoma.setting.common.utils.AnimUtils;
import com.xiaoma.setting.common.views.MultyRadioGroup;
import com.xiaoma.setting.common.views.RadioButtonLayout;
import com.xiaoma.setting.common.views.SettingItemView;
import com.xiaoma.setting.common.views.SwitchAnimation;
import com.xiaoma.setting.common.views.TextTackSeekBar;
import com.xiaoma.utils.log.KLog;

@PageDescComponent(EventConstants.PageDescribe.lamplightSettingFragmentPagePathDesc)
public class LamplightFragment extends BaseFragment implements SeekBar.OnSeekBarChangeListener, SettingItemView.StateListener, SwitchAnimation.SwitchListener, LamplightVM.OnValueChange {
    private static final String TAG = LamplightFragment.class.getSimpleName();
    private static final String CAR_CONTROL = "car_control";
//    private final String wjw = "Wjw";
    private TextTackSeekBar skbAmbientLight;
    private LamplightVM lamplightVM;
    private SettingItemView sivInDoorLight;
    private SettingItemView sivChangeLaneFlash;

    private SwitchAnimation leaveLight;
    private SwitchAnimation welcomeLight;
    private SwitchAnimation goHome;
    private SwitchAnimation ambientLight;
    private SwitchAnimation sceneLight;
    private SwitchAnimation ihc;
    private RadioButtonLayout radiogroupGoHome;

    private MultyRadioGroup colorSelect;

    private View ambientLightContent;
    private AnimUtils animUtils;
    private MultyRadioGroup.OnCheckedChangeListener onCheckedChangeListener;
    private RadioButtonLayout.CustomOnCheckedChangeListener customOnCheckedChangeListener;
    private int currentSetting = SettingConstants.LAMPLIGHT_GO_HOME_MODE;
    private ImageView mIvSettingPic;
    private int[] goHomeImagesArr = {R.drawable.lamplight_go_home_close, R.drawable.lamplight_go_home_open_10, R.drawable.lamplight_go_home_open_20, R.drawable.lamplight_go_home_open_30};
    private int[] whiteLightImagesArr = {R.drawable.lamplight_atmosphere_white_one, R.drawable.lamplight_atmosphere_white_two, R.drawable.lamplight_atmosphere_white_three,
            R.drawable.lamplight_atmosphere_white_four, R.drawable.lamplight_atmosphere_white_five, R.drawable.lamplight_atmosphere_white_six, R.drawable.lamplight_atmosphere_white_seven,
            R.drawable.lamplight_atmosphere_white_eight, R.drawable.lamplight_atmosphere_white_nine, R.drawable.lamplight_atmosphere_white_ten};
    private int[] teaseLightImagesArr = {R.drawable.lamplight_atmosphere_tease_one, R.drawable.lamplight_atmosphere_tease_two, R.drawable.lamplight_atmosphere_tease_three,
            R.drawable.lamplight_atmosphere_tease_four, R.drawable.lamplight_atmosphere_tease_five, R.drawable.lamplight_atmosphere_tease_six, R.drawable.lamplight_atmosphere_tease_seven,
            R.drawable.lamplight_atmosphere_tease_eight, R.drawable.lamplight_atmosphere_tease_nine, R.drawable.lamplight_atmosphere_tease_ten};
    private int[] flirtLightImagesArr = {R.drawable.lamplight_atmosphere_flirt_one, R.drawable.lamplight_atmosphere_flirt_two, R.drawable.lamplight_atmosphere_flirt_three,
            R.drawable.lamplight_atmosphere_flirt_four, R.drawable.lamplight_atmosphere_flirt_five, R.drawable.lamplight_atmosphere_flirt_six, R.drawable.lamplight_atmosphere_flirt_seven,
            R.drawable.lamplight_atmosphere_flirt_eight, R.drawable.lamplight_atmosphere_flirt_nine, R.drawable.lamplight_atmosphere_flirt_ten};
    private int[] relaxLightImagesArr = {R.drawable.lamplight_atmosphere_relax_one, R.drawable.lamplight_atmosphere_relax_two, R.drawable.lamplight_atmosphere_relax_three,
            R.drawable.lamplight_atmosphere_relax_four, R.drawable.lamplight_atmosphere_relax_five, R.drawable.lamplight_atmosphere_relax_six, R.drawable.lamplight_atmosphere_relax_seven,
            R.drawable.lamplight_atmosphere_relax_eight, R.drawable.lamplight_atmosphere_relax_nine, R.drawable.lamplight_atmosphere_relax_ten};
    private int[] quietLightImagesArr = {R.drawable.lamplight_atmosphere_quiet_one, R.drawable.lamplight_atmosphere_quiet_two, R.drawable.lamplight_atmosphere_quiet_three,
            R.drawable.lamplight_atmosphere_quiet_four, R.drawable.lamplight_atmosphere_quiet_five, R.drawable.lamplight_atmosphere_quiet_six, R.drawable.lamplight_atmosphere_quiet_seven,
            R.drawable.lamplight_atmosphere_quiet_eight, R.drawable.lamplight_atmosphere_quiet_nine, R.drawable.lamplight_atmosphere_quiet_ten};
    private int[] wellLightImagesArr = {R.drawable.lamplight_atmosphere_well_one, R.drawable.lamplight_atmosphere_well_two, R.drawable.lamplight_atmosphere_well_three,
            R.drawable.lamplight_atmosphere_well_four, R.drawable.lamplight_atmosphere_well_five, R.drawable.lamplight_atmosphere_well_six, R.drawable.lamplight_atmosphere_well_seven,
            R.drawable.lamplight_atmosphere_well_eight, R.drawable.lamplight_atmosphere_well_nine, R.drawable.lamplight_atmosphere_well_ten};
    private int[] peacefulLightImagesArr = {R.drawable.lamplight_atmosphere_peaceful_one, R.drawable.lamplight_atmosphere_peaceful_two, R.drawable.lamplight_atmosphere_peaceful_three,
            R.drawable.lamplight_atmosphere_peaceful_four, R.drawable.lamplight_atmosphere_peaceful_five, R.drawable.lamplight_atmosphere_peaceful_six, R.drawable.lamplight_atmosphere_peaceful_seven,
            R.drawable.lamplight_atmosphere_peaceful_eight, R.drawable.lamplight_atmosphere_peaceful_nine, R.drawable.lamplight_atmosphere_peaceful_ten};
    private int[] coldLightImagesArr = {R.drawable.lamplight_atmosphere_cold_one, R.drawable.lamplight_atmosphere_cold_two, R.drawable.lamplight_atmosphere_cold_three,
            R.drawable.lamplight_atmosphere_cold_four, R.drawable.lamplight_atmosphere_cold_five, R.drawable.lamplight_atmosphere_cold_six, R.drawable.lamplight_atmosphere_cold_seven,
            R.drawable.lamplight_atmosphere_cold_eight, R.drawable.lamplight_atmosphere_cold_nine, R.drawable.lamplight_atmosphere_cold_ten};
    private int[] zealLightImagesArr = {R.drawable.lamplight_atmosphere_zeal_one, R.drawable.lamplight_atmosphere_zeal_two, R.drawable.lamplight_atmosphere_zeal_three,
            R.drawable.lamplight_atmosphere_zeal_four, R.drawable.lamplight_atmosphere_zeal_five, R.drawable.lamplight_atmosphere_zeal_six, R.drawable.lamplight_atmosphere_zeal_seven,
            R.drawable.lamplight_atmosphere_zeal_eight, R.drawable.lamplight_atmosphere_zeal_nine, R.drawable.lamplight_atmosphere_zeal_ten};
    private int[] warmLightImagesArr = {R.drawable.lamplight_atmosphere_warm_one, R.drawable.lamplight_atmosphere_warm_two, R.drawable.lamplight_atmosphere_warm_three,
            R.drawable.lamplight_atmosphere_warm_four, R.drawable.lamplight_atmosphere_warm_five, R.drawable.lamplight_atmosphere_warm_six, R.drawable.lamplight_atmosphere_warm_seven,
            R.drawable.lamplight_atmosphere_warm_eight, R.drawable.lamplight_atmosphere_warm_nine, R.drawable.lamplight_atmosphere_warm_ten};
    private int[] livelyLightImagesArr = {R.drawable.lamplight_atmosphere_lively_one, R.drawable.lamplight_atmosphere_lively_two, R.drawable.lamplight_atmosphere_lively_three,
            R.drawable.lamplight_atmosphere_lively_four, R.drawable.lamplight_atmosphere_lively_five, R.drawable.lamplight_atmosphere_lively_six, R.drawable.lamplight_atmosphere_lively_seven,
            R.drawable.lamplight_atmosphere_lively_eight, R.drawable.lamplight_atmosphere_lively_nine, R.drawable.lamplight_atmosphere_lively_ten};
    private int[] noisyLightImagesArr = {R.drawable.lamplight_atmosphere_noisy_one, R.drawable.lamplight_atmosphere_noisy_two, R.drawable.lamplight_atmosphere_noisy_three,
            R.drawable.lamplight_atmosphere_noisy_four, R.drawable.lamplight_atmosphere_noisy_five, R.drawable.lamplight_atmosphere_noisy_six, R.drawable.lamplight_atmosphere_noisy_seven,
            R.drawable.lamplight_atmosphere_noisy_eight, R.drawable.lamplight_atmosphere_noisy_nine, R.drawable.lamplight_atmosphere_noisy_ten};
    private boolean isHiddenChanged = false; // ???????????????onHiddenChanged
    /*-------????????????-------*/
//    private Handler handler;
    /*-------????????????-------*/


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_car_lamplight, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        bindView(view);
        lamplightVM.setOnValueChange(this);
    }

    private void bindView(View view) {
        ambientLightContent = view.findViewById(R.id.ll_ambient_light_content);
        ambientLight = view.findViewById(R.id.ambient_light);
        sceneLight = view.findViewById(R.id.scene_light);
        radiogroupGoHome = view.findViewById(R.id.go_home_radio_button);
        radiogroupGoHome.setDelayUIChange(true);
        sivChangeLaneFlash = view.findViewById(R.id.siv_change_lane_flashing);
        welcomeLight = view.findViewById(R.id.welcome_light);
        if (!XmCarConfigManager.hasCourtesyLamp()) {
            welcomeLight.setVisibility(View.GONE);
        }
        sivInDoorLight = view.findViewById(R.id.indoor_light);
        sivInDoorLight.setDelayItemSelector(true);
        leaveLight = view.findViewById(R.id.leave_light);
        goHome = view.findViewById(R.id.go_home_switch);
        skbAmbientLight = view.findViewById(R.id.skb_ambient_light);
        if (!XmCarConfigManager.hasMusicMoodLighting()) {
            ambientLight.setVisibility(View.GONE);
        }
        skbAmbientLight.setMinValue(1);
        skbAmbientLight.setRangeDisplayOnLeftAndRight(false);
        colorSelect = view.findViewById(R.id.color_select);
        colorSelect.setDelayItemSelector(true);
        ihc = view.findViewById(R.id.ihc);
        if (XmCarConfigManager.hasSmartHeadlight() || XmCarConfigManager.hasSqureLight()) {
            ihc.setVisibility(View.VISIBLE);
        } else {
            ihc.setVisibility(View.GONE);
        }
        mIvSettingPic = view.findViewById(R.id.iv_setting_pic);
        onCheckedChangeListener = new MultyRadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(MultyRadioGroup group, int checkedId) {
                onSelect(group.getId(), checkedId - 1);
            }
        };
        customOnCheckedChangeListener = new RadioButtonLayout.CustomOnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int index) {
                onSelect(group.getId(), index - 1);
            }
        };
        colorSelect.setOnCheckedChangeListener(onCheckedChangeListener);
        radiogroupGoHome.setCustomOnCheckedChangeListener(customOnCheckedChangeListener);

        initData();
        skbAmbientLight.setOnSeekBarChangeListener(this);
        skbAmbientLight.setMinValue(1);
        sivChangeLaneFlash.setListener(this);
        sivInDoorLight.setListener(this);


        ihc.setListener(this);
        sceneLight.setListener(this);
        ambientLight.setListener(this);
        welcomeLight.setListener(this);
        leaveLight.setListener(this);
        goHome.setListener(this);
        ambientLight.setListener(this);

        sceneLight.setNeedReverse(false);
    }

    private void initData() {
        lamplightVM = ViewModelProviders.of(this).get(LamplightVM.class).initData();
        if (getVM().getGoHome() >= 0) {
            goHome.check(true);
            radiogroupGoHome.setVisibility(View.VISIBLE);
            radiogroupGoHome.checkIndex(getVM().getGoHome());
        }
        leaveLight.check(getVM().getLeaveLight());
        sivInDoorLight.setCheck(getVM().getIndoorLight());
        welcomeLight.check(getVM().getWelcomeLight());
        sivChangeLaneFlash.setCheck(getVM().getLaneChangeFlicker());
        ihc.check(getVM().getIHC());
        ambientLight.check(getVM().getAmbientLightSwitch());
        ambientLightContent.setVisibility(getVM().getAmbientLightSwitch() ? View.VISIBLE : View.GONE);
        skbAmbientLight.setProgress(getVM().getAmbientLightBrightness());
//        if (!getVM().getSceneLightSwitch()) { //????????????????????????????????????????????????
//            colorSelect.setVisibility(View.VISIBLE);
        colorSelect.check(getVM().getAmbientLightColor());
//        }
        sceneLight.check(getVM().getSceneLightSwitch());
        if (!isHiddenChanged)
            mIvSettingPic.setImageResource(R.drawable.car_setting_default); // ?????????
    }


    private LamplightVM getVM() {
        if (lamplightVM != null) {
            return lamplightVM;
        } else {
            return lamplightVM = ViewModelProviders.of(this).get(LamplightVM.class).initData();
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        KLog.e(TAG, "onProgressChanged : " + progress);
        switch (seekBar.getId()) {
            case R.id.skb_ambient_light://???????????????
                getVM().setAmbientLightBrightness(progress);
                break;
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onSelect(int viewId, int index) {
        KLog.e(TAG, "onSelect : " + viewId);
        switch (viewId) {
            case R.id.go_home_radio_button://????????????
                getVM().setGoHome(index);
                break;
            case R.id.indoor_light://???????????????
                getVM().setIndoorLight(index);
                break;
            case R.id.siv_change_lane_flashing://????????????
                getVM().setLaneChangeFlicker(index);
                break;
            case R.id.color_select://??????????????????????????????
                getVM().setAmbientLightColor(index);
                break;
        }

    }


    private AnimUtils getAnimUtils() {
        if (animUtils != null) {
            return animUtils;
        }
        animUtils = AnimUtils.newInstance(getContext(), ambientLightContent, ambientLight, getHeight(ambientLightContent));
        return animUtils;
    }

    private int getHeight(View view) {
        int width = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int height = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        view.measure(width, height);
        return view.getMeasuredHeight();
    }


    @Override
    public void onSwitch(int viewId, boolean state) {
        KLog.e(TAG, "onSwitch : " + viewId);
        switch (viewId) {
            case R.id.go_home_switch://????????????
                getVM().setGoHome(state);
//                setValueByTime(goHome, state);
                break;
            case R.id.leave_light://????????????
                getVM().setLeaveLight(state);
                break;
            case R.id.welcome_light://?????????
                getVM().setWelcomeLight(state);
                break;
            case R.id.ihc://ihc????????????
                getVM().setIHC(state);
                break;
            case R.id.ambient_light://???????????????
                getVM().setAmbientLightSwitch(state);
                if (state) {
                    skbAmbientLight.setProgress(getVM().getAmbientLightBrightness());
//                    if (!getVM().getSceneLightSwitch()) { //????????????????????????????????????????????????
//                        colorSelect.setVisibility(View.VISIBLE);
//                        colorSelect.check(getVM().getAmbientLightColor());
//                    }
                }
                break;
            case R.id.scene_light://??????????????????
                getVM().setSceneLightSwitch(state);
//                if (!state) { //?????????????????????????????????????????????
//                    colorSelect.check(getVM().getAmbientLightColor());
//                }
                break;
        }
    }

    /*--------????????????----------*/
    /*private void setValueByTime(final SwitchAnimation goHome, final boolean state) {
        if (handler == null){
            handler = new Handler();
        }
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                goHome.check(state);
            }
        }, 2000);
    }*/

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            isHiddenChanged = true;
            initData();
        }
    }

    @Override
    public void onChange(int id, Object value) {
        if (id == SDKConstants.MODE_GO_HOME) {
            boolean b = (int) value >= SDKConstants.VALUE.GO_HOME_M1;
            KLog.d(CAR_CONTROL, "????????????: " + b);
            int i = getVM().caseGoHomeToIndex((Integer) value);
            if (i == getVM().caseGoHomeFinalStatusToIndex(goHome.isCheck(), radiogroupGoHome.getCheckedIndex())) {
                return;
            }
            goHome.check((int) value >= SDKConstants.VALUE.GO_HOME_M1);
            if ((int) value >= SDKConstants.VALUE.GO_HOME_M1) {
                radiogroupGoHome.setCustomOnCheckedChangeListener(null);
                radiogroupGoHome.checkIndex(i);
                KLog.d(CAR_CONTROL, "??????????????????: " + i);
                radiogroupGoHome.setCustomOnCheckedChangeListener(customOnCheckedChangeListener);
            }
            currentSetting = SettingConstants.LAMPLIGHT_GO_HOME_MODE;
            switchSettingPic(i + 1);
        } else if (id == SDKConstants.INTERIOR_LIGHT_DELAY) {
//            int result = (int) value;
//            sivInDoorLight.setListener(null);
//            sivInDoorLight.setCheck(result >= SDKConstants.VALUE.INTERIOR_LIGHT_DELAY_10S && result <= SDKConstants.VALUE.INTERIOR_LIGHT_DELAY_30S ? result : 0);
//            int i = result >= SDKConstants.VALUE.INTERIOR_LIGHT_DELAY_10S && result <= SDKConstants.VALUE.INTERIOR_LIGHT_DELAY_30S ? result : 0;
//            if (i == sivInDoorLight.getIndex() + 1)
//                return;
//            KLog.d(CAR_CONTROL, "???????????????: " + i);
//            sivInDoorLight.setListener(this);
//            currentSetting = SettingConstants.LAMPLIGHT_INDOOR_LIGHT_DELAY_SETTING;
//            switchSettingPic(i);
        } else if (id == SDKConstants.IHC) {
            boolean b = getVM().caseIhcToState((Integer) value);
            if (b == ihc.isCheck())
                return;
            ihc.check(b);
            KLog.d(CAR_CONTROL, "????????????: " + b);
            currentSetting = SettingConstants.LAMPLIGHT_SMART_HEAD_LIGHT;
            switchSettingPic(b);
        } else if (id == SDKConstants.AMBIENT_LIGHT_SWITCH) {
            boolean b = getVM().caseAmbientLightSwitchToState((Integer) value);
            if (b == ambientLight.isCheck())
                return;
            ambientLight.check(b);
            KLog.d(CAR_CONTROL, "???????????????: " + b);
            currentSetting = SettingConstants.LAMPLIGHT_ATMOSPHERE_LIGHT;
            switchSettingPic(b);
        } else if (id == SDKConstants.AMBIENT_LIGHT_BRIGHTNESS) {
            if ((Integer) value == skbAmbientLight.getProgress())
                return;
            skbAmbientLight.setOnSeekBarChangeListener(null);
            KLog.d(CAR_CONTROL, "???????????????: " + value);
            skbAmbientLight.setProgress((Integer) value);
            skbAmbientLight.setOnSeekBarChangeListener(this);
            currentSetting = SettingConstants.LAMPLIGHT_ATMOSPHERE_LIGHT;
            switchSettingPic((Integer) value);
        } else if (id == SDKConstants.MUSIC_SCENE_FOLLOW) {
            boolean b = (int) value == SDKConstants.VALUE.MUSIC_SCENE_FOLLOW;
            if (b == sceneLight.isCheck())
                return;
            sceneLight.check(b);
            KLog.d(CAR_CONTROL, "??????????????????: " + b);
            currentSetting = SettingConstants.LAMPLIGHT_SCENE_LIGHT;
            switchSettingPic(b);
        } else if (id == SDKConstants.MUSIC_SCENE_FOLLOW_COLOR) {
            int i = getVM().caseAmbientLightColorToIndex((Integer) value);
            if (i == colorSelect.getCheckedRadioButtonId())
                return;
            colorSelect.setOnCheckedChangeListener(null);
            colorSelect.check(i);
            colorSelect.setOnCheckedChangeListener(onCheckedChangeListener);
            KLog.d(CAR_CONTROL, "????????????????????????: " + i);
            currentSetting = SettingConstants.LAMPLIGHT_SCENE_LIGHT;
            switchSettingPic(i);
        } else if (id == SDKConstants.WELCOME_LAMP_SWITCH) {
            boolean b = getVM().caseWelcomeLightToSwitch((Integer) value);
            if (b == welcomeLight.isCheck())
                return;
            welcomeLight.check(b);
            KLog.d(CAR_CONTROL, "???????????????: " + b);
            currentSetting = SettingConstants.LAMPLIGHT_WELCOME_LIGHT_SETTING;
            switchSettingPic(b);
        }
    }

    private void switchSettingPic(int index) {
//        KLog.e(wjw, "switchSettingPic");
        int lightColor;
        switch (currentSetting) {
            case SettingConstants.LAMPLIGHT_GO_HOME_MODE: // ????????????
//                KLog.e(wjw, "????????????");
//                KLog.e(wjw, "flag :" + index);
                if (index < 0 || index >= goHomeImagesArr.length) return;
//                KLog.e(wjw, "set ImageResourse");
                mIvSettingPic.setImageResource(goHomeImagesArr[index]);
                break;
            case SettingConstants.LAMPLIGHT_INDOOR_LIGHT_DELAY_SETTING: // ?????????????????????
//                KLog.e(wjw, "?????????????????????");
//                KLog.e(wjw, "flag :" + index);
                mIvSettingPic.setImageResource(R.drawable.car_setting_default);
                break;
            case SettingConstants.LAMPLIGHT_ATMOSPHERE_LIGHT: // ?????????
//                KLog.e(wjw, "?????????");
//                KLog.e(wjw, "flag :" + index);
                lightColor = getVM().getAmbientLightColor(); // ???????????????
//                KLog.e(wjw, "lightColor :" + lightColor);
                mIvSettingPic.setImageResource(getCurrentAtmospherePicByColor(lightColor));
                break;
            case SettingConstants.LAMPLIGHT_SCENE_LIGHT:
//                KLog.e(wjw, "??????????????????");
//                KLog.e(wjw, "flag :" + index);
                lightColor = getVM().getAmbientLightColor(); // ???????????????
//                KLog.e(wjw, "lightColor :" + lightColor);
                mIvSettingPic.setImageResource(getCurrentAtmospherePicByColor(lightColor));
                break;
        }
    }

    private void switchSettingPic(boolean open) {
//        KLog.e(wjw, "switchSettingPic");
        int lightColor;
        switch (currentSetting) {
            case SettingConstants.LAMPLIGHT_WELCOME_LIGHT_SETTING:
//                KLog.e(wjw, "???????????????");
//                KLog.e(wjw, "flag :" + open);
                mIvSettingPic.setImageResource(open ? R.drawable.lamplight_wellcome_light_open : R.drawable.lamplight_wellcome_light_close);
                break;
            case SettingConstants.LAMPLIGHT_SMART_HEAD_LIGHT:
//                KLog.e(wjw, "????????????");
//                KLog.e(wjw, "flag :" + open);
                mIvSettingPic.setImageResource(open ? R.drawable.lamplight_smart_head_light_open : R.drawable.lamplight_smart_head_light_close);
                break;
            case SettingConstants.LAMPLIGHT_ATMOSPHERE_LIGHT: // ????????????????????? ??????????????????
//                KLog.e(wjw, "?????????");
//                KLog.e(wjw, "flag :" + open);
                lightColor = getVM().getAmbientLightColor(); // ???????????????
                mIvSettingPic.setImageResource(open ? getCurrentAtmospherePicByColor(lightColor) : R.drawable.lamplight_atmosphere_close);
                break;
            case SettingConstants.LAMPLIGHT_GO_HOME_MODE:
//                KLog.e(wjw, "????????????");
//                KLog.e(wjw, "flag :" + open);
                int index = getVM().getGoHome();
                if (open && (index < 0 || index >= goHomeImagesArr.length)) return;
                mIvSettingPic.setImageResource(open ? goHomeImagesArr[index] : R.drawable.lamplight_go_home_close);
                break;
            case SettingConstants.LAMPLIGHT_SCENE_LIGHT: // ????????????
//                KLog.e(wjw, "??????????????????");
//                KLog.e(wjw, "flag :" + open);
                lightColor = getVM().getAmbientLightColor(); // ???????????????
//                KLog.e(wjw, "lightColor :" + lightColor);
                mIvSettingPic.setImageResource(getCurrentAtmospherePicByColor(lightColor));
                break;
        }
    }

    /**
     * @param color ???????????????
     * @return ??????????????????????????????????????????
     */
    private @DrawableRes
    int getCurrentAtmospherePicByColor(int color) {
        int drawableRes = 0;
        int brightness = getLightBrightness();
        switch (color) {
            case 1: // ??????
                drawableRes = zealLightImagesArr[brightness];
                break;
            case 2: // ??????
                drawableRes = teaseLightImagesArr[brightness];
                break;
            case 3: // ??????
                drawableRes = flirtLightImagesArr[brightness];
                break;
            case 4: // ??????
                drawableRes = quietLightImagesArr[brightness];
                break;
            case 5: // ??????
                drawableRes = wellLightImagesArr[brightness];
                break;
            case 6: // ??????
                drawableRes = peacefulLightImagesArr[brightness];
                break;
            case 7: // ??????
                drawableRes = coldLightImagesArr[brightness];
                break;
            case 8: // ??????
                drawableRes = relaxLightImagesArr[brightness];
                break;
            case 9: // ??????
                drawableRes = warmLightImagesArr[brightness];
                break;
            case 10: // ??????
                drawableRes = livelyLightImagesArr[brightness];
                break;
            case 11: // ??????
                drawableRes = noisyLightImagesArr[brightness];
                break;
            case 12: // ???
                drawableRes = whiteLightImagesArr[brightness];
                break;
        }
//        KLog.e(wjw, "color = " + color);
        return drawableRes;
    }

    /**
     * ????????????????????????????????????
     *
     * @return
     */
    private int getLightBrightness() {
        int brightness = getVM().getAmbientLightBrightness();//???????????? 0-10
//        if (brightness >= whiteLightImagesArr.length - 1) {
//            brightness = whiteLightImagesArr.length - 1;
//        } else {
//            brightness++;
//        }
        int min = skbAmbientLight.getMin();
        if (min == 0) { // 0-10
            if (brightness == 10) {
                brightness--;
            }
        } else { // 1 - 10
            brightness--;
        }
        return brightness;
    }
}

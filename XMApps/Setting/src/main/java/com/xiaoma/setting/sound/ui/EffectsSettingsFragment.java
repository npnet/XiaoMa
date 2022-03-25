package com.xiaoma.setting.sound.ui;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioGroup;

import com.xiaoma.autotracker.XmAutoTracker;
import com.xiaoma.carlib.callback.onGetIntArrayResultListener;
import com.xiaoma.carlib.constant.SDKConstants;
import com.xiaoma.carlib.manager.XmCarVendorExtensionManager;
import com.xiaoma.component.base.BaseFragment;
import com.xiaoma.model.annotation.PageDescComponent;
import com.xiaoma.setting.R;
import com.xiaoma.setting.common.constant.EventConstants;
import com.xiaoma.setting.common.views.RadioButtonLayout;
import com.xiaoma.setting.common.views.SettingSwitch;
import com.xiaoma.setting.common.views.TickMarkSeekBar;
import com.xiaoma.setting.sound.vm.EffectsSettingsVM;
import com.xiaoma.utils.ListUtils;
import com.xiaoma.utils.log.KLog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by qiuboxiang on 2018/10/11 17:53
 * description:
 */
@PageDescComponent(EventConstants.PageDescribe.effectsSettingFragmentPagePathDesc)
public class EffectsSettingsFragment extends BaseFragment implements TickMarkSeekBar.OnProgressChangedListener, RadioButtonLayout.CustomOnCheckedChangeListener, onGetIntArrayResultListener/*, EffectsSettingsVM.OnChangeEvent*/ {

    public static final String TAG = "EffectsSettingsFragment";
    private final int INDEX_CUSTOM_SOUND_EFFECTS = 4;
    private final int MAX_DECIBEL = 14;
    private final int MIN_DECIBEL = 0;
    private final int SEEKBAR_TICK_MARK_COUNT = MAX_DECIBEL + 1;
    private List<TickMarkSeekBar> mSeekBarList;
    private EffectsSettingsVM mEffectsSettingsVM;
    private LinearLayout mLlSettings;
    private RadioButtonLayout mEffectsModeRadioGorup;
    private LinearLayout mImageLayout;
    private LinearLayout mEffectsModeLayout;
    private SoundFieldLayout mSoundFieldLayout;
    //    private SettingSwitch mArkamys3DSwitch;
    private RadioButtonLayout mArkamys3dRadioGorup;
    private SettingSwitch mSubwooferSwitch;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_effects_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mEffectsSettingsVM = ViewModelProviders.of(this).get(EffectsSettingsVM.class);
        bindView(view);
        registerObserver();
        XmCarVendorExtensionManager.getInstance().addEffectsListener(this);
//        mEffectsSettingsVM.setOnChangeEvent(this);
    }

    private void registerObserver() {
        registerObserver(mEffectsSettingsVM.getStandardEffect(), 0);
        registerObserver(mEffectsSettingsVM.getPopEffect(), 1);
        registerObserver(mEffectsSettingsVM.getClassicEffect(), 2);
        registerObserver(mEffectsSettingsVM.getJazzEffect(), 3);
        registerObserver(mEffectsSettingsVM.getCustomEffect(), 4);
    }

    void registerObserver(MutableLiveData<List<Integer>> liveData, final int index) {
        liveData.observe(this, new Observer<List<Integer>>() {
            @Override
            public void onChanged(@Nullable List<Integer> list) {
                if (!ListUtils.isEmpty(list) && mEffectsModeRadioGorup.getCheckedIndex() == index) {
                    initSeekBar(list);
                }
            }
        });
    }

    private void bindView(View view) {
        mSeekBarList = new ArrayList<>();
        mSeekBarList.add((TickMarkSeekBar) view.findViewById(R.id.seekBar_50Hz));
        mSeekBarList.add((TickMarkSeekBar) view.findViewById(R.id.seekBar_200Hz));
        mSeekBarList.add((TickMarkSeekBar) view.findViewById(R.id.seekBar_800Hz));
        mSeekBarList.add((TickMarkSeekBar) view.findViewById(R.id.seekBar_3200Hz));
        mSeekBarList.add((TickMarkSeekBar) view.findViewById(R.id.seekBar_10000Hz));

        for (int i = 0; i < mSeekBarList.size(); i++) {
            TickMarkSeekBar seekBar = mSeekBarList.get(i);
            seekBar.setTickMarkCount(SEEKBAR_TICK_MARK_COUNT);
            seekBar.setOnProgressChangedListener(this);
            if (i == 0) {
                seekBar.setTopTickMarkText(String.format(getString(R.string.decibel_value), MAX_DECIBEL));
                seekBar.setBottomTickMarkText(String.format(getString(R.string.decibel_value), MIN_DECIBEL));
            }
        }

        mSoundFieldLayout = view.findViewById(R.id.layout_sound_field);
        mSoundFieldLayout.setOnPointChangedListener(new SoundFieldLayout.OnPositionChangedListener() {
            @Override
            public void onPositionChanged(int viewId, Point point, String position) {
                if (point != null) {
                    Log.d(TAG, "onPositionChanged: " + point.x + " " + point.y);
                    mEffectsSettingsVM.setSoundEffectPositionAtAnyPoint(point);
                }

            }
        });
        Point point = mEffectsSettingsVM.getSoundEffectPositionAtAnyPoint().getValue();
        mSoundFieldLayout.setCheckedPoint(point);

        mLlSettings = view.findViewById(R.id.ll_settings);

        mEffectsModeRadioGorup = view.findViewById(R.id.sound_effects_mode_radioGroup);
        int selectedModeIndex = mEffectsSettingsVM.getSoundEffectsMode().getValue();
        mEffectsModeRadioGorup.setCustomOnCheckedChangeListener(this);
        mEffectsModeRadioGorup.checkIndex(selectedModeIndex);
        initSeekBar(selectedModeIndex);

        mImageLayout = view.findViewById(R.id.image_layout);
        mEffectsModeLayout = view.findViewById(R.id.layout_sound_effects_mode);

   /*     mArkamys3DSwitch = view.findViewById(R.id.switch_Arkamys_3D);
        mArkamys3DSwitch.setListener(new SettingSwitch.StateListener() {
            @Override
            public void onCheck(int viewId, boolean isChecked) {
                mEffectsSettingsVM.setArkamys3D(isChecked);
                showImageLayout(isChecked);
                String status = isChecked ? getString(R.string.open) : getString(R.string.close);
                XmAutoTracker.getInstance().onEvent(EventConstants.NormalClick.SoundEffetsSetting, status,
                        "EffectsSettingsFragment", EventConstants.PageDescribe.effectsSettingFragmentPagePathDesc);
            }
        });
        mArkamys3DSwitch.setChecked(mEffectsSettingsVM.getArkamys3D().getValue());*/

        mSubwooferSwitch = view.findViewById(R.id.switch_subwoofer);
        mSubwooferSwitch.setListener(new SettingSwitch.StateListener() {
            @Override
            public void onCheck(int viewId, boolean isChecked) {
                mEffectsSettingsVM.setSubwoofer(isChecked);
                String status = isChecked ? getString(R.string.open) : getString(R.string.close);
                XmAutoTracker.getInstance().onEvent(EventConstants.NormalClick.SubwooferSetting, status,
                        "EffectsSettingsFragment", EventConstants.PageDescribe.effectsSettingFragmentPagePathDesc);
            }
        });
        mSubwooferSwitch.setChecked(mEffectsSettingsVM.getSubwoofer().getValue());

        mArkamys3dRadioGorup = view.findViewById(R.id.radioGroup_Arkamys_3D);
        mArkamys3dRadioGorup.setCustomOnCheckedChangeListener(this);
        int arkamys3DStatus = mEffectsSettingsVM.getArkamys3D().getValue();
        mArkamys3dRadioGorup.checkIndex(arkamys3DStatus);
        showImageLayout(arkamys3DStatus == 0 || arkamys3DStatus == 1);
    }

    private void initSeekBar(int index) {
        List<Integer> list = null;
        switch (index) {
            case EffectsSettingsVM.SOUND_EFFECTS_INDEX_0:
                list = mEffectsSettingsVM.getStandardEffect().getValue();
                break;
            case EffectsSettingsVM.SOUND_EFFECTS_INDEX_1:
                list = mEffectsSettingsVM.getPopEffect().getValue();
                break;
            case EffectsSettingsVM.SOUND_EFFECTS_INDEX_2:
                list = mEffectsSettingsVM.getClassicEffect().getValue();
                break;
            case EffectsSettingsVM.SOUND_EFFECTS_INDEX_3:
                list = mEffectsSettingsVM.getJazzEffect().getValue();
                break;
            case EffectsSettingsVM.SOUND_EFFECTS_INDEX_4:
                list = mEffectsSettingsVM.getCustomEffect().getValue();
                break;
        }

        initSeekBar(list);
    }

    private void initSeekBar(List<Integer> list) {
        if (ListUtils.isEmpty(list)) {
            return;
        }
        List<Integer> integerList = new ArrayList<>(list);
        integerList.remove(0);
        for (int i = 0; i < mSeekBarList.size(); i++) {
            TickMarkSeekBar seekBar = mSeekBarList.get(i);
            if (i < integerList.size()) {
                seekBar.setCursorIndex(integerList.get(i));
            }
        }
    }

    @Override
    public void onProgressChanged(int viewId, int progress, float rate, int cursorIndex) {
        KLog.e(TAG, "onProgressChanged " + cursorIndex);
        Integer[] integers = new Integer[6];
        integers[0] = SDKConstants.VALUE.SOUND_EFFECTS_USER;
        for (int i = 0; i < mSeekBarList.size(); i++) {
            TickMarkSeekBar seekBar = mSeekBarList.get(i);
            integers[i + 1] = seekBar.getCursorIndex();
        }
        String event = "";
        switch (viewId) {
            case R.id.seekBar_50Hz:
                mEffectsSettingsVM.setCustomSoundEffects(EffectsSettingsVM.SOUND_EFFECTS_INDEX_0, cursorIndex, integers);
                event = EventConstants.slideEvent.soundEffect50Setting;
                break;
            case R.id.seekBar_200Hz:
                mEffectsSettingsVM.setCustomSoundEffects(EffectsSettingsVM.SOUND_EFFECTS_INDEX_1, cursorIndex, integers);
                event = EventConstants.slideEvent.soundEffect200Setting;
                break;
            case R.id.seekBar_800Hz:
                mEffectsSettingsVM.setCustomSoundEffects(EffectsSettingsVM.SOUND_EFFECTS_INDEX_2, cursorIndex, integers);
                event = EventConstants.slideEvent.soundEffect800Setting;
                break;
            case R.id.seekBar_3200Hz:
                mEffectsSettingsVM.setCustomSoundEffects(EffectsSettingsVM.SOUND_EFFECTS_INDEX_3, cursorIndex, integers);
                event = EventConstants.slideEvent.soundEffect3200Setting;
                break;
            case R.id.seekBar_10000Hz:
                mEffectsSettingsVM.setCustomSoundEffects(EffectsSettingsVM.SOUND_EFFECTS_INDEX_4, cursorIndex, integers);
                event = EventConstants.slideEvent.soundEffect10000Setting;
                break;
        }
        if (mEffectsModeRadioGorup.getCheckedRadioButtonId() != INDEX_CUSTOM_SOUND_EFFECTS) {
            mEffectsModeRadioGorup.checkIndex(INDEX_CUSTOM_SOUND_EFFECTS);
        }
        XmAutoTracker.getInstance().onEvent(EventConstants.NormalClick.SoundEffetsSetting, event,
                "EffectsSettingsFragment", EventConstants.PageDescribe.effectsSettingFragmentPagePathDesc);
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedIndex) {
        switch (group.getId()) {
            case R.id.sound_effects_mode_radioGroup:
                mEffectsSettingsVM.setSoundEffectsMode(checkedIndex);
//                initSeekBar(checkedIndex);
                break;
            case R.id.radioGroup_Arkamys_3D:
                mEffectsSettingsVM.setArkamys3D(checkedIndex);
                showImageLayout(checkedIndex == 0 || checkedIndex == 1);
                String status = "";
                switch (checkedIndex) {
                    case 0:
                        status = getString(R.string.all_passenger);
                        break;
                    case 1:
                        status = getString(R.string.driver);
                        break;
                    case 2:
                        status = getString(R.string.close);
                        break;
                }
                XmAutoTracker.getInstance().onEvent(EventConstants.NormalClick.SoundEffetsSetting, status,
                        "EffectsSettingsFragment", EventConstants.PageDescribe.effectsSettingFragmentPagePathDesc);
                break;
        }
    }

    private void showImageLayout(boolean isArkamys3dOpen) {
        mLlSettings.setVisibility(isArkamys3dOpen ? View.GONE : View.VISIBLE);
        mSubwooferSwitch.setVisibility(isArkamys3dOpen ? View.VISIBLE : View.GONE);
        mEffectsModeLayout.setVisibility(isArkamys3dOpen ? View.GONE : View.VISIBLE);
        mImageLayout.setVisibility(isArkamys3dOpen ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        XmCarVendorExtensionManager.getInstance().removeEffectsListener(this);
    }

    @Override
    public void onSoundEffectsGetResult(Integer[] result) {
        Log.d("QBX", "onSoundEffectsGetResult: ");
        List<Integer> list = Arrays.asList(result);
        if (result.length != 0) {
            int selectIndex = result[0];
            mEffectsModeRadioGorup.checkIndex(selectIndex);
        }
        initSeekBar(list);

        String log = "";
        for (int i = 0; i < list.size(); i++) {
            log += "index: " + i + ", value: " + list.get(i);
        }
        Log.d("sound_return", "index: " + mEffectsModeRadioGorup.getCheckedIndex() + ", value: " + log);
    }

    @Override
    public void onArkamys3dEffectsGetResult(int value) {
        mArkamys3dRadioGorup.checkIndex(mEffectsSettingsVM.caseArkamys3dValueToIndex(value));
    }

    @Override
    public void onSubwooferGetValue(boolean value) {
        mSubwooferSwitch.setChecked(value);
    }

    @Override
    public void onSoundEffectPositionGetResult(Integer[] result) {
        int x = result[1];
        int y = result[0];
        Point point = mEffectsSettingsVM.getSoundEffectPositionAtAnyPoint().getValue();
        if (point != null && x == point.x && y == point.y) {
            return;
        }
        mSoundFieldLayout.setCheckedPoint(new Point(x, y));
    }

    /*@Override
    public void onChange(int id, Object value) {
        if (id == SDKConstants.ARKAMYS_3D) {
            mArkamys3DSwitch.setChecked((int) value == SDKConstants.VALUE.ARKAMYS_3D_ALL_ON);
        } else if (id == SDKConstants.SOUND_EFFECTS) {
            Integer[] result = (Integer[]) value;
            if (result.length >= 1) {
                mEffectsModeRadioGorup.checkIndex(mEffectsSettingsVM.caseSoundEffectsModeToIndex(result[0]));
            }
        } else if (id == SDKConstants.FADER_BALANCE) {
            Integer[] result = (Integer[]) value;
            if (result.length >= 2) {
                int x = result[1];
                int y = result[0];
                mSoundFieldLayout.setCheckedPoint(new Point(x, y));
            }
        }
    }*/
}

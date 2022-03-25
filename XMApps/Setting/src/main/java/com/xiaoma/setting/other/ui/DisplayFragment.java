package com.xiaoma.setting.other.ui;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import com.xiaoma.autotracker.XmAutoTracker;
import com.xiaoma.carlib.constant.SDKConstants;
import com.xiaoma.carlib.manager.XmCarConfigManager;
import com.xiaoma.carlib.manager.XmCarVendorExtensionManager;
import com.xiaoma.component.base.BaseFragment;
import com.xiaoma.model.annotation.PageDescComponent;
import com.xiaoma.setting.R;
import com.xiaoma.setting.common.constant.EventConstants;
import com.xiaoma.setting.common.views.SettingItemView;
import com.xiaoma.setting.common.views.TextTackSeekBar;
import com.xiaoma.setting.other.model.DisplaySettingVM;
import com.xiaoma.utils.log.KLog;

/**
 * Created by Administrator on 2018/10/30 0030.
 */

@PageDescComponent(EventConstants.PageDescribe.displaySettingFragmentPagePathDesc)
public class DisplayFragment extends BaseFragment implements SeekBar.OnSeekBarChangeListener, DisplaySettingVM.ValueChangeEvent {

    private SettingItemView mSivDisplayMode;
    private TextTackSeekBar mKeyBoardSeekBar, mDisplayBrightnessSeekbar;
    private SettingItemView mSivBanVideo;
    private DisplaySettingVM mDisplaySettingVM;
    private boolean isOnAutoDisplay = false;
    private static final String CAR_CONTROL = "car_control";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_display, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mDisplaySettingVM = ViewModelProviders.of(this).get(DisplaySettingVM.class);
        bindView(view);
        mDisplaySettingVM.setOnValueChangeEvent(this);
    }

    private void bindView(View view) {
        mSivDisplayMode = view.findViewById(R.id.siv_display_mode);
        mDisplayBrightnessSeekbar = ((TextTackSeekBar) view.findViewById(R.id.display_brightness_seekbar));
        mDisplayBrightnessSeekbar.setMinValue(1);
        mSivDisplayMode.setListener(new SettingItemView.StateListener() {
            @Override
            public void onSelect(int viewId, int index) {
                setDisplayModeSelect(index);
            }
        });
        initDisplayModeIndex(mDisplaySettingVM.getDisplayMode().getValue());
        mDisplayBrightnessSeekbar.setOnSeekBarChangeListener(this);
        mKeyBoardSeekBar = ((TextTackSeekBar) view.findViewById(R.id.key_brightness_seekbar));
        mKeyBoardSeekBar.setMinValue(1);
        mKeyBoardSeekBar.setOnSeekBarChangeListener(this);
        mKeyBoardSeekBar.setProgress(mDisplaySettingVM.getKeyBoardLevel().getValue());
        mSivBanVideo = view.findViewById(R.id.siv_ban_video);
        boolean ban = mDisplaySettingVM.getBanVideoStatus().getValue();
        if (ban == SDKConstants.VALUE.UN_BAN_VIDEO) {
            mSivBanVideo.setCheck(1);
        } else {
            mSivBanVideo.setCheck(0);
        }
        mSivBanVideo.setListener(new SettingItemView.StateListener() {
            @Override
            public void onSelect(int viewId, int index) {
                if (index == 0) {
                    mDisplaySettingVM.setBanVideoStatus(SDKConstants.VALUE.BAN_VIDEO);
                } else {
                    mDisplaySettingVM.setBanVideoStatus(SDKConstants.VALUE.UN_BAN_VIDEO);
                }
                String status = index == 0 ? "禁止" : "允许";
                XmAutoTracker.getInstance().onEvent(EventConstants.NormalClick.watchVideoSetting, status,
                        "DisplayFragment", EventConstants.PageDescribe.displaySettingFragmentPagePathDesc);
            }
        });
        view.findViewById(R.id.rb_subtract).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                XmCarVendorExtensionManager.getInstance().setRobBrightnessDecrease();
            }
        });
        view.findViewById(R.id.rb_add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                XmCarVendorExtensionManager.getInstance().setRobBrightnessIncrease();
            }
        });

        View light_3d = view.findViewById(R.id.light_3d_parent);
        if (XmCarConfigManager.hasHologram()) {
            light_3d.setVisibility(View.VISIBLE);
        } else {
            light_3d.setVisibility(View.GONE);
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        String event = "";
        switch (seekBar.getId()) {
            case R.id.display_brightness_seekbar:
                mDisplaySettingVM.setDisplayLevel(progress);
                KLog.d("ljb", "onProgressChanged拿到的亮度:" + mDisplaySettingVM.getDayDisplayLevel().getValue());
                event = EventConstants.slideEvent.screenBrightnessSetting;
                break;
            case R.id.key_brightness_seekbar:
                mDisplaySettingVM.setKeyBoardLevel(progress);
                event = EventConstants.slideEvent.keyBrightnessSetting;
                break;
        }
        XmAutoTracker.getInstance().onEvent(event, String.valueOf(progress), "DisplayFragment",
                EventConstants.PageDescribe.displaySettingFragmentPagePathDesc);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    private void setDisplayModeSelect(int index) {
        int progressValue = 0;
        switch (index) {
            case 0:
                mDisplaySettingVM.setDisplayMode(SDKConstants.VALUE.DISPLAY_AUTO);
                progressValue = mDisplaySettingVM.getAutoDisplayLevel().getValue();
                isOnAutoDisplay = true;
                break;
            case 1:
                mDisplaySettingVM.setDisplayMode(SDKConstants.VALUE.DISPLAY_DAY);
                progressValue = mDisplaySettingVM.getDayDisplayLevel().getValue();
                KLog.d("ljb", "白天拿到的亮度:" + progressValue);
                isOnAutoDisplay = false;
                break;
            case 2:
                mDisplaySettingVM.setDisplayMode(SDKConstants.VALUE.DISPLAY_NIGHT);
                progressValue = mDisplaySettingVM.getNightDisplayLevel().getValue();
                KLog.d("ljb", "黑夜拿到的亮度:" + progressValue);
                isOnAutoDisplay = false;
                break;
        }
        if (mDisplayBrightnessSeekbar != null) {
            mDisplayBrightnessSeekbar.setOnSeekBarChangeListener(null);
            mDisplayBrightnessSeekbar.setProgress(progressValue);
            mDisplayBrightnessSeekbar.setOnSeekBarChangeListener(DisplayFragment.this);
        }
    }

    private synchronized void initDisplayModeIndex(int value) {
        int progressValue = 1;
        if (value == SDKConstants.DISPLAYSCREENMODE_AUTO) {
            mSivDisplayMode.setCheck(0);
            progressValue = mDisplaySettingVM.getAutoDisplayLevel().getValue();
            isOnAutoDisplay = true;
        } else if (value == SDKConstants.DISPLAYSCREENMODE_DAY) {
            mSivDisplayMode.setCheck(1);
            progressValue = mDisplaySettingVM.getDayDisplayLevel().getValue();
            isOnAutoDisplay = false;
        } else if (value == SDKConstants.DISPLAYSCREENMODE_NIGHT) {
            mSivDisplayMode.setCheck(2);
            progressValue = mDisplaySettingVM.getNightDisplayLevel().getValue();
            isOnAutoDisplay = false;
        }
        if (mDisplayBrightnessSeekbar != null) {
            mDisplayBrightnessSeekbar.setProgress(progressValue);
        }
    }

    @Override
    public void onChange(int id, Object value) {

        if (id == SDKConstants.ID_DISPLAY_MODE) {
            mSivDisplayMode.setCheck((int) value);
        }
        if (id == SDKConstants.ID_KEYBOARD_LEVEL) {
            mKeyBoardSeekBar.setOnSeekBarChangeListener(null);
            mKeyBoardSeekBar.setProgress((int) value);
            mKeyBoardSeekBar.setOnSeekBarChangeListener(DisplayFragment.this);
        }

        if (id == SDKConstants.ID_DAY_DISPLAY_LEVEL || id == SDKConstants.ID_NIGHT_DISPLAY_LEVEL) {
            mDisplayBrightnessSeekbar.setOnSeekBarChangeListener(null);
            mDisplayBrightnessSeekbar.setProgress((int) value);
            KLog.d(CAR_CONTROL, "白天或黑夜屏幕亮度: " + value);
            mDisplayBrightnessSeekbar.setOnSeekBarChangeListener(DisplayFragment.this);
        } else if (id == SDKConstants.ID_ILL_STATUS) { //小灯状态更改，主动去获取自动模式下的亮度。
            if (isOnAutoDisplay) {
                mDisplayBrightnessSeekbar.setOnSeekBarChangeListener(null);
                mDisplayBrightnessSeekbar.setProgress(mDisplaySettingVM.getAutoDisplayLevel().getValue());
                KLog.d(CAR_CONTROL, "自动模式下屏幕亮度: " + mDisplaySettingVM.getAutoDisplayLevel().getValue());
                mDisplayBrightnessSeekbar.setOnSeekBarChangeListener(DisplayFragment.this);
            }
        }
    }
}

package com.xiaoma.club.personal.ui;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.xiaoma.autotracker.XmAutoTracker;
import com.xiaoma.club.R;
import com.xiaoma.club.common.model.ClubEventConstants;
import com.xiaoma.club.common.ui.SlideInFragment;
import com.xiaoma.club.common.util.ClubSettings;
import com.xiaoma.model.annotation.PageDescComponent;

/**
 * Author: loren
 * Date: 2018/10/13 0017
 */
@PageDescComponent(ClubEventConstants.PageDescribe.settingDrawerFragment)
public class SettingDrawerFragment extends SlideInFragment implements View.OnClickListener {
    public final String TAG = getClass().getSimpleName();
    private Switch chatPushSwitch;
    private Switch autoPlaySwitch;

    @Override
    protected int getSlideViewId() {
        return R.id.setting_parent;
    }

    @Override
    protected View onCreateWrapView(View childView) {
        return LayoutInflater.from(childView.getContext()).inflate(R.layout.fmt_setting_drawer, (ViewGroup) childView, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
    }

    private void initView(View view) {
        view.findViewById(R.id.setting_outside).setOnClickListener(this);
        chatPushSwitch = view.findViewById(R.id.setting_chat_push_switch);
        autoPlaySwitch = view.findViewById(R.id.setting_auto_play_switch);
        boolean chatPushOpen = ClubSettings.isChatPushOpen();
        boolean autoPlayOpen = ClubSettings.isAutoPlayOpen();
        Log.i(TAG, String.format("initView: chatPushOpen: %s, autoPlayOpen: %s", chatPushOpen, autoPlayOpen));
        chatPushSwitch.setChecked(chatPushOpen);
        chatPushSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.i(TAG, String.format("initView: ChatPushSwitch: isChecked: %s", isChecked));
                ClubSettings.setChatPushOpen(isChecked);
                String isOpened = isChecked ? getString(R.string.open_status) : getString(R.string.close_status);
                XmAutoTracker.getInstance().onEvent(ClubEventConstants.NormalClick.chatPushSwicth, isOpened,
                        "SettingDrawerFragment", ClubEventConstants.PageDescribe.settingDrawerFragment);
            }
        });
        autoPlaySwitch.setChecked(autoPlayOpen);
        autoPlaySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.i(TAG, String.format("initView: AutoPlaySwitch: isChecked: %s", isChecked));
                ClubSettings.setAutoPlayOpen(isChecked);
                String isOpened = isChecked ? getString(R.string.open_status) : getString(R.string.close_status);
                XmAutoTracker.getInstance().onEvent(ClubEventConstants.NormalClick.autoPlaySwicth, isOpened,
                        "SettingDrawerFragment", ClubEventConstants.PageDescribe.settingDrawerFragment);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.setting_outside:
                if (getActivity() != null) {
                    getActivity().onBackPressed();
                }
                break;
        }
    }
}

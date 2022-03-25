package com.xiaoma.shop.business.ui.sound;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xiaoma.component.base.BaseFragment;
import com.xiaoma.shop.R;
import com.xiaoma.shop.business.model.EventMsg;
import com.xiaoma.shop.business.model.PayInfo;
import com.xiaoma.shop.business.ui.ShopBaseFragment;
import com.xiaoma.shop.business.ui.theme.PersonalityThemeFragment;
import com.xiaoma.shop.business.ui.theme.VoiceToneFragment;
import com.xiaoma.shop.common.callback.OnPayFromPersonalCallback;
import com.xiaoma.shop.common.constant.PaySourceType;
import com.xiaoma.shop.common.constant.VehicleSoundType;
import com.xiaoma.utils.FragmentUtils;
import com.xiaoma.utils.NetworkUtils;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;

/**
 * @Author: LiangJingBo.
 * @Date: 2019/05/29
 * @Describe:
 */

public class VehicleSoundFragment extends ShopBaseFragment implements View.OnClickListener, OnPayFromPersonalCallback {

    private TextView mTvAudioSound;
    private TextView mTvInstrumentSound;
    private Fragment mFragments[] = {AudioSoundFragment.newInstance(), InstrumentSoundFragment.newInstance()};

    private String mCurrentTag = VehicleSoundType.ProductType.AUDIO_SOUND;
    private boolean isFirstLoad = true;// 是否是第一次加载

    public static VehicleSoundFragment newInstance() {
        return new VehicleSoundFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_vehicle_sound, container, false);
        return super.onCreateWrapView(view);//包裹，需要show no network view
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        initEvent();
        checkNet();
        EventBus.getDefault().registerSticky(this);
        EventBus.getDefault().register(this);
    }

    @Subscriber
    public void getAction(EventMsg eventMsg) {
        if (eventMsg == null || TextUtils.isEmpty(eventMsg.getAction())) return;
        switch (eventMsg.getAction()) {
            case EventMsg.ACTION.ACTION_VEHICLE:
                switchFragment(0);
                break;
            case EventMsg.ACTION.ACTION_INSTRUMENT:
                switchFragment(1);
                break;
        }
    }

    @Override
    public void onDestroyView() {
        EventBus.getDefault().unregister(this);
        super.onDestroyView();
    }

    private void checkNet() {
        if (NetworkUtils.isConnected(getActivity())) {
            if (isFirstLoad) {
                isFirstLoad = false;
                switchFragment(0);
            } else {
                EventBus.getDefault().post(mCurrentTag, mCurrentTag);
            }
//            showContentView();
        } else {
            showNoNetView();
        }
    }


    public void parentShowNoNetView(String tag) {
        mCurrentTag = tag;
        showNoNetView();
    }

    public void parentShowContentView() {
        showContentView();
    }

    @Override
    protected void noNetworkOnRetry() {
        checkNet();
    }

    private void initView(View view) {
        mTvAudioSound = view.findViewById(R.id.tv_audio_sound);
        mTvInstrumentSound = view.findViewById(R.id.tv_instrument_sound);
    }

    private void initEvent() {
        mTvAudioSound.setOnClickListener(this);
        mTvInstrumentSound.setOnClickListener(this);
    }

    private void switchFragment(int index) {
        Fragment childFragment = mFragments[index];
        if (childFragment.isAdded()) {
            FragmentUtils.showHide(mFragments[index], mFragments);//切换fragment
        } else {
            FragmentUtils.hide(getChildFragmentManager());
            FragmentUtils.add(getChildFragmentManager(), childFragment, R.id.fl_contain);
        }
        // TextView 样式变化
        restoreStyle();
        switch (index) {
            case 0:
                mTvAudioSound.setTextAppearance(R.style.text_view_light_white);
                break;
            case 1:
                mTvInstrumentSound.setTextAppearance(R.style.text_view_light_white);
                break;
        }

    }


    private void restoreStyle() {
        mTvAudioSound.setTextAppearance(R.style.text_view_normal);
        mTvInstrumentSound.setTextAppearance(R.style.text_view_normal);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_audio_sound:
                switchFragment(0);
                break;
            case R.id.tv_instrument_sound:
                switchFragment(1);
                break;

        }
    }

    @Override
    public void handlePay(PayInfo payInfo, boolean immediateExecute) {
        //判断哪个界面的
        if (PaySourceType.VEHICLE.equals(payInfo.getProductType())) {
            AudioSoundFragment audioSoundFragment = (AudioSoundFragment) mFragments[0];
            if (audioSoundFragment instanceof OnPayFromPersonalCallback) {
                EventBus.getDefault().postSticky(new EventMsg(EventMsg.ACTION.ACTION_VEHICLE));
                EventBus.getDefault().post(new EventMsg(EventMsg.ACTION.ACTION_VEHICLE));
                ((OnPayFromPersonalCallback) audioSoundFragment).handlePay(payInfo, false);
            }
        } else {
            InstrumentSoundFragment instrumentSoundFragment = (InstrumentSoundFragment) mFragments[1];
            if (instrumentSoundFragment instanceof OnPayFromPersonalCallback) {
                EventBus.getDefault().postSticky(new EventMsg(EventMsg.ACTION.ACTION_INSTRUMENT));
                EventBus.getDefault().post(new EventMsg(EventMsg.ACTION.ACTION_INSTRUMENT));
                ((OnPayFromPersonalCallback) instrumentSoundFragment).handlePay(payInfo, false);
            }
        }
    }
}

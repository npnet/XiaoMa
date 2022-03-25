package com.xiaoma.shop.business.ui.bought;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xiaoma.shop.R;
import com.xiaoma.shop.business.adapter.bought.BoughtVehicleSoundAdapter;
import com.xiaoma.shop.business.model.MineBought;
import com.xiaoma.shop.business.model.VehicleSoundEntity;
import com.xiaoma.shop.business.ui.bought.callback.OneKeyCleanCacheCallback;
import com.xiaoma.shop.common.constant.CacheBindStatus;
import com.xiaoma.utils.FragmentUtils;

/**
 * @Author: LiangJingBo.
 * @Date: 2019/05/29
 * @Describe:
 */

public class BoughtVehicleSoundFragment extends AbsBoughtFragment<VehicleSoundEntity.SoundEffectListBean, BoughtVehicleSoundAdapter>
        implements View.OnClickListener, OneKeyCleanCacheCallback {


    private Fragment mFragments[] = {BoughtAudioSoundFragment.newInstance(), BoughtInstrumentSoundFragment.newInstance()};

    public static AbsBoughtFragment newInstance() {
        return new BoughtVehicleSoundFragment();
    }

    @Override
    protected String getItemDownloadUrl(VehicleSoundEntity.SoundEffectListBean bean) {
        return null;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bought_vehicle_sound, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        switchFragment(0);
        if (getOwnParentFragment() != null) {
            getOwnParentFragment().changeTheTvStyle(0);
        }
    }

    @Override
    protected BoughtVehicleSoundAdapter createAdapter() {
        return null;
    }

    @Override
    protected int requestResourceType() {
        return 0;
    }

    @Override
    protected int cacheBindStatus() {
        return CacheBindStatus.NONE;
    }

    @Override
    protected void obtainActuallyData(boolean more, MineBought data) {

    }


    public void switchFragment(int index) {
        Fragment childFragment = mFragments[index];
        if (childFragment.isAdded()) {
            FragmentUtils.showHide(mFragments[index], mFragments);//切换fragment
        } else {
            FragmentUtils.hide(getChildFragmentManager());
            FragmentUtils.add(getChildFragmentManager(), childFragment, R.id.fl_contain);
        }
    }

    public BoughtMainFragment getOwnParentFragment() {
        if (getParentFragment() != null && getParentFragment() instanceof BoughtMainFragment) {
            return (BoughtMainFragment) getParentFragment();
        }
        return null;
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
    public void initAction(int status, int type) {
    }

    @Override
    public void selectedCacheSize(String cacheText) {

    }

    @Override
    public void startClean() {

    }

    @Override
    public void completeClean() {

    }

    @Override
    public void downloadCompleteUpdateCacheSize() {

    }

    @Override
    public void refreshCacheSize() {

    }
}

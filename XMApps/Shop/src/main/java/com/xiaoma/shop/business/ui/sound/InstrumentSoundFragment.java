package com.xiaoma.shop.business.ui.sound;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.fsl.android.uniqueota.UniqueOtaConstants;
import com.xiaoma.shop.common.constant.ResourceType;
import com.xiaoma.shop.common.constant.UpdateResouceType;
import com.xiaoma.shop.common.constant.VehicleSoundType;
import com.xiaoma.shop.common.util.VehicleSoundUtils;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;
import org.simple.eventbus.ThreadMode;

/**
 * @Author: LiangJingBo.
 * @Date: 2019/05/29
 * @Describe: 仪表音效
 */

public class InstrumentSoundFragment extends AbsChildVehicleSoundFragment {
    public static final String NOTIFY_ALL = InstrumentSoundFragment.class.getSimpleName() + "_notify_all";

    public static Fragment newInstance() {
        return new InstrumentSoundFragment();
    }


    @Override
    protected String getProductType() {
        return VehicleSoundType.ProductType.INSTRUMENT_SOUND;
    }

    @Override
    protected int requestResourceType() {
        return ResourceType.INSTRUMENT_SOUND;
    }


    @Override
    protected int requestUpdateResourceType() {
        if (VehicleSoundUtils.isPro()) {//高配
            return UpdateResouceType.ICH;
        } else {
            return UpdateResouceType.ICL;
        }
    }

    @Override
    protected int getEcu() {
        return VehicleSoundUtils.isPro()
                ? UniqueOtaConstants.EcuId.IC_H
                : UniqueOtaConstants.EcuId.IC_L;
    }


    @Subscriber(mode = ThreadMode.MAIN, tag = VehicleSoundType.ProductType.INSTRUMENT_SOUND)
    public void getMessage(String msg) {
        if (NOTIFY_ALL.equals(msg)) {
            mAdapter.notifyDataSetChanged();
        } else {
            fetchVehicleSounds(getProductType(), mSortRule, mCurType);
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}

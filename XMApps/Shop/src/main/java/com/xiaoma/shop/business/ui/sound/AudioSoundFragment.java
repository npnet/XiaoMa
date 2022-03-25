package com.xiaoma.shop.business.ui.sound;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.fsl.android.uniqueota.UniqueOtaConstants;
import com.xiaoma.shop.business.model.PayInfo;
import com.xiaoma.shop.business.pay.PaySuccessResultCallback;
import com.xiaoma.shop.common.constant.ResourceType;
import com.xiaoma.shop.common.constant.UpdateResouceType;
import com.xiaoma.shop.common.constant.VehicleSoundType;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;
import org.simple.eventbus.ThreadMode;

/**
 * @Author: LiangJingBo.
 * @Date: 2019/05/29
 * @Describe: 音响音效
 */

public class AudioSoundFragment extends AbsChildVehicleSoundFragment {
    public static final String NOTIFY_ALL = AudioSoundFragment.class.getSimpleName() + "_notify_all";

    public static Fragment newInstance() {
        return new AudioSoundFragment();
    }

    @Override
    protected String getProductType() {
        return VehicleSoundType.ProductType.AUDIO_SOUND;
    }



    @Override
    protected int requestResourceType() {
        return ResourceType.VEHICLE_SOUND;
    }

    // 模拟使用效果，真正的实现在父类
//    @Override
//    protected void updateVehicleSound(final int pos) {
//        UpdateOtaUtils.pushTheFileToDst(
//                mAdapter.getData().get(pos).getFilePath(),
//                requestResourceType(),
//                requestUpdateResourceType(),
//                new UpdateOtaUtils.OnHandleListener() {
//                    @Override
//                    public void onSucceed() {
//                        Log.i("_TAG", "onSucceed(AudioSoundFragment.java:49): use the source");
//                        showUseSuccess();
//                        mAdapter.useSourceAtPosition(pos);
//                    }
//
//                    @Override
//                    public void onFailure() {
//                        showUpdateError();
//                    }
//                });
//    }

    @Override
    protected int requestUpdateResourceType() {
        return UpdateResouceType.HU;
    }

    @Override
    protected int getEcu() {
        return UniqueOtaConstants.EcuId.HU;
    }


    @Subscriber(mode = ThreadMode.MAIN, tag = VehicleSoundType.ProductType.AUDIO_SOUND)
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

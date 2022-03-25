package com.xiaoma.shop.business.ui.bought;

import com.fsl.android.uniqueota.UniqueOtaConstants;
import com.xiaoma.shop.business.model.MineBought;
import com.xiaoma.shop.business.model.VehicleSoundEntity;
import com.xiaoma.shop.business.ui.sound.InstrumentSoundFragment;
import com.xiaoma.shop.common.constant.ResourceType;
import com.xiaoma.shop.common.constant.UpdateResouceType;
import com.xiaoma.shop.common.constant.VehicleSoundType;
import com.xiaoma.shop.common.util.VehicleSoundUtils;

import java.util.List;

/**
 * Created by Gillben
 * date: 2019/3/5 0005
 * <p>
 * 已购买仪表音效
 */
public class BoughtInstrumentSoundFragment extends AbsBoughtVehicleSoundFragment {

    public static BoughtInstrumentSoundFragment newInstance() {
        return new BoughtInstrumentSoundFragment();
    }

    @Override
    protected int requestResourceType() {
        return ResourceType.INSTRUMENT_SOUND;
    }


    @Override
    protected int getEcu() {
        return VehicleSoundUtils.isPro()
                ? UniqueOtaConstants.EcuId.IC_H
                : UniqueOtaConstants.EcuId.IC_L;
    }

    @Override
    protected String getKeyTag() {
        return BoughtInstrumentSoundFragment.class.getSimpleName();
    }

    @Override
    protected List<VehicleSoundEntity.SoundEffectListBean> getData(MineBought data) {
        return data.getInstrumentList();
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
    protected String getProductType() {
        return VehicleSoundType.ProductType.INSTRUMENT_SOUND;
    }

    @Override
    protected Object getEvent() {
        return InstrumentSoundFragment.NOTIFY_ALL;
    }
}

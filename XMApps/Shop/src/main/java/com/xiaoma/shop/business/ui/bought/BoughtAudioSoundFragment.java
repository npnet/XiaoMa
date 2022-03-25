package com.xiaoma.shop.business.ui.bought;

import com.fsl.android.uniqueota.UniqueOtaConstants;
import com.xiaoma.shop.business.model.MineBought;
import com.xiaoma.shop.business.model.VehicleSoundEntity;
import com.xiaoma.shop.business.ui.sound.AudioSoundFragment;
import com.xiaoma.shop.common.constant.ResourceType;
import com.xiaoma.shop.common.constant.UpdateResouceType;
import com.xiaoma.shop.common.constant.VehicleSoundType;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Gillben
 * date: 2019/3/5 0005
 * <p>
 * 已购买音响音效
 */
public class BoughtAudioSoundFragment extends AbsBoughtVehicleSoundFragment {

    public static BoughtAudioSoundFragment newInstance() {
        return new BoughtAudioSoundFragment();
    }

    @Override
    protected int requestResourceType() {
        return ResourceType.VEHICLE_SOUND;
    }


    @Override
    protected int getEcu() {
        return UniqueOtaConstants.EcuId.HU;
    }

    @Override
    protected String getKeyTag() {
        return BoughtAudioSoundFragment.class.getSimpleName();
    }

    @Override
    protected List<VehicleSoundEntity.SoundEffectListBean> getData(MineBought data) {
        return data.getVehicleSounds();
    }

    private void changeTheData(List<VehicleSoundEntity.SoundEffectListBean> dataList) {
        List<VehicleSoundEntity.SoundEffectListBean> listBeans = new ArrayList<>();
        for (int i = 0; i < dataList.size(); i++) {
            VehicleSoundEntity.SoundEffectListBean bean = dataList.get(i);
            bean.setBuy(true);
            if (bean.getThemeName().equals("音效1")) {
                bean.setDownloadNum(1000000);
                bean.setFilePath("http://dldir1.qq.com/weixin/android/weixin704android1420.apk");
                bean.setSize(101607014);
                bean.setDiscountPrice(0);
                bean.setDiscountScorePrice(0);
                bean.setAuditionPath("http://ydown.smzy.com/yinpin/2018-11/smzy_2018111509.mp3");
            } else if (bean.getThemeName().equals("音效2")) {
                bean.setDownloadNum(1000000);
                bean.setFilePath("http://qd.myapp.com/myapp/qqteam/AndroidQQ/mobileqq_android.apk");
                bean.setSize(71276191);
                bean.setDiscountPrice(0);
                bean.setDiscountScorePrice(0);
                bean.setAuditionPath("http://ydown.smzy.com/yinpin/2018-11/smzy_2018111509.mp3");
            } else if (bean.getThemeName().equals("音效3")) {
                bean.setDownloadNum(1000000);
                bean.setFilePath("http://sqdd.myapp.com/myapp/qqteam/tim/down/tim.apk");
                bean.setSize(54417598);
                bean.setDiscountPrice(0);
                bean.setDiscountScorePrice(0);
                bean.setAuditionPath("http://ydown.smzy.com/yinpin/2018-11/smzy_2018111509.mp3");
            } else {
                bean.setFilePath(bean.getFilePath() + i);
                listBeans.add(bean);
            }
        }
        dataList.addAll(listBeans);
        dataList.addAll(listBeans);
        dataList.addAll(listBeans);
        dataList.addAll(listBeans);
        dataList.addAll(listBeans);
        dataList.addAll(listBeans);
        dataList.addAll(listBeans);
        dataList.addAll(listBeans);
        dataList.addAll(listBeans);
        dataList.addAll(listBeans);
        dataList.addAll(listBeans);
        dataList.addAll(listBeans);
        dataList.addAll(listBeans);
        dataList.addAll(listBeans);
        dataList.addAll(listBeans);
        dataList.addAll(listBeans);
        dataList.addAll(listBeans);
        dataList.addAll(listBeans);
        dataList.addAll(listBeans);
        dataList.addAll(listBeans);
    }

    @Override
    protected int requestUpdateResourceType() {
        return UpdateResouceType.HU;
    }

    @Override
    protected String getProductType() {
        return VehicleSoundType.ProductType.AUDIO_SOUND;
    }

    @Override
    protected Object getEvent() {
        return AudioSoundFragment.NOTIFY_ALL;
    }
}

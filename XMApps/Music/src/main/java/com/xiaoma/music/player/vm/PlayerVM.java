package com.xiaoma.music.player.vm;

import android.app.Application;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.xiaoma.component.base.BaseViewModel;
import com.xiaoma.model.XmResource;
import com.xiaoma.music.BTMusicFactory;
import com.xiaoma.music.OnlineMusicFactory;
import com.xiaoma.music.UsbMusicFactory;
import com.xiaoma.music.kuwo.listener.OnChargeQualityListener;
import com.xiaoma.music.kuwo.model.XMMusic;
import com.xiaoma.music.model.BTMusic;
import com.xiaoma.music.model.UsbMusic;
import com.xiaoma.music.player.model.MusicQualityModel;
import com.xiaoma.utils.log.KLog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @author zs
 * @date 2018/10/11 0011.
 */
public class PlayerVM extends BaseViewModel {

    private static final String TAG = "PlayerVM";
    private MutableLiveData<XMMusic> onlineMusic;
    private static MutableLiveData<UsbMusic> usbMusic;
    private MutableLiveData<BTMusic> btMusic;
    private MutableLiveData<Integer> btStatus;
    private MutableLiveData<XmResource<List<MusicQualityModel>>> qualityModels;
    private MutableLiveData<MusicQualityModel> currentQualityModels;

    public PlayerVM(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<XMMusic> getOnlineMusic() {
        if (onlineMusic == null) {
            onlineMusic = new MutableLiveData<>();
        }
        return onlineMusic;
    }

    public MutableLiveData<UsbMusic> getUsbMusic() {
        if (usbMusic == null) {
            usbMusic = new MutableLiveData<>();
        }
        return usbMusic;
    }

    public MutableLiveData<BTMusic> getBTMusic() {
        if (btMusic == null) {
            btMusic = new MutableLiveData<>();
        }
        return btMusic;
    }

    public MutableLiveData<Integer> getBtStatus() {
        if (btStatus == null) {
            btStatus = new MutableLiveData<>();
        }
        return btStatus;
    }

    public MutableLiveData<XmResource<List<MusicQualityModel>>> getQualityModels() {
        if (qualityModels == null) {
            qualityModels = new MutableLiveData<>();
        }
        return qualityModels;
    }

    public MutableLiveData<MusicQualityModel> getCurrentQualityModels() {
        if (currentQualityModels == null) {
            currentQualityModels = new MutableLiveData<>();
        }
        return currentQualityModels;
    }

    public void setOnlineMusic(XMMusic xmMusic) {
        getOnlineMusic().postValue(xmMusic);
    }

    public void fetchOnlineMusic() {
        XMMusic nowPlayingMusic = OnlineMusicFactory.getKWPlayer().getNowPlayingMusic();
        if (nowPlayingMusic == null || nowPlayingMusic.getSDKBean() == null) {
            return;
        }
        getOnlineMusic().postValue(nowPlayingMusic);
    }

    public void fetchCurrentQuality(int quality) {
        //电台类节目获取最高音质为-1，故电台类节目音质选择功能置灰不可用
        int maxQuality = OnlineMusicFactory.getKWPlayer().getNowPlayMusicBestQuality();
        OnlineMusicFactory.getKWPlayer().chargeNowPlayMusic(quality, new OnChargeQualityListener() {
            @Override
            public void onSuccess(MusicQualityModel model) {
                if (model != null) {
                    model.setRadio(maxQuality == -1);
                    getCurrentQualityModels().postValue(model);
                }
            }

            @Override
            public void onFailed(String msg) {

            }
        });
    }

    public void fetchMusicQualityModel() {
        getQualityModels().setValue(XmResource.loading());
        //获取当前播放音乐的最佳音质
        int maxQuality = OnlineMusicFactory.getKWPlayer().getNowPlayMusicBestQuality();
        if (maxQuality == -1) {
            return;
        }
        List<MusicQualityModel> models = new ArrayList<>();
        for (int i = maxQuality; i > 0; i--) {
            OnlineMusicFactory.getKWPlayer().chargeNowPlayMusic(i, new OnChargeQualityListener() {
                @Override
                public void onSuccess(MusicQualityModel model) {
                    KLog.d(TAG, "chargeNowPlayMusic onSuccess : " + model);
                    if (model != null) {
                        models.add(model);
                        Collections.sort(models, new Comparator<MusicQualityModel>() {
                            @Override
                            public int compare(MusicQualityModel o1, MusicQualityModel o2) {
                                return Integer.compare(o1.getQuality(), o2.getQuality());
                            }
                        });
                        getQualityModels().postValue(XmResource.response(models));
                    }
                }

                @Override
                public void onFailed(String msg) {
                    getQualityModels().postValue(XmResource.failure(""));
                }
            });
        }
    }

    public void setUsbMusic(UsbMusic usbMusic) {
        getUsbMusic().postValue(usbMusic);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        onlineMusic = null;
        usbMusic = null;
    }

    public void fetchUsbMusic() {
        final UsbMusic usbMusic = UsbMusicFactory.getUsbPlayerProxy().getCurrUsbMusic();
        if (usbMusic != null) {
            setUsbMusic(usbMusic);
        }
    }

    public void fetchBtMusic() {
        final BTMusic btMusic = BTMusicFactory.getBTMusicControl().getCurrBTMusic();
        if (btMusic != null) {
            getBTMusic().postValue(btMusic);
        }
    }

    public void fetchBtStatus() {
        // TODO:蓝牙播放状态
        /*int btStatus = XmMusicManager.getInstance().getBtAudio().getBtStatus();
        getBtStatus().postValue(btStatus);*/
    }
}

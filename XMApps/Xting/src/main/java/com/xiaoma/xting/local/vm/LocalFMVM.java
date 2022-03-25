package com.xiaoma.xting.local.vm;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.xiaoma.mapadapter.manager.LocationManager;
import com.xiaoma.mapadapter.model.LocationInfo;
import com.xiaoma.utils.CollectionUtil;
import com.xiaoma.utils.GsonHelper;
import com.xiaoma.xting.common.LocalPlayList;
import com.xiaoma.xting.common.XtingConstants;
import com.xiaoma.xting.common.XtingUtils;
import com.xiaoma.xting.launcher.LocalFMOperateManager;
import com.xiaoma.xting.local.model.AMChannelBean;
import com.xiaoma.xting.local.model.BaseChannelBean;
import com.xiaoma.xting.local.model.FMChannelBean;
import com.xiaoma.xting.player.model.AcrRadioBean;
import com.xiaoma.xting.sdk.AcrFactory;
import com.xiaoma.xting.sdk.listener.OnRadioResultListener;

import java.util.List;

/**
 * @author KY
 * @date 2018/10/11
 */
public class LocalFMVM extends AndroidViewModel {

    private MutableLiveData<List<FMChannelBean>> mFMChannels;
    private MutableLiveData<List<AMChannelBean>> mAMChannels;
    private String mLat;
    private String mLon;
    private LocalPlayList.ChannelChangeListener mChangeListener;

    public LocalFMVM(@NonNull Application application) {
        super(application);
        LocalPlayList.getInstance().addListener(mChangeListener = new LocalPlayList.ChannelChangeListener() {
            @Override
            public void onFMChange(List<FMChannelBean> fmChannelBeans) {
                getFMChannels().setValue(fmChannelBeans);
            }

            @Override
            public void onAMChange(List<AMChannelBean> amChannelBeans) {
                getAMChannels().setValue(amChannelBeans);
            }
        });
    }

    public void getLocation() {
        LocationInfo location = LocationManager.getInstance().getRealCurrentLocation();
        if (location != null) {
            mLat = String.valueOf(location.getLatitude());
            mLon = String.valueOf(location.getLongitude());
        }
    }

    public boolean saveFMChannel(FMChannelBean fmChannel) {
        FMChannelBean saveBean = XtingUtils.getDBManager(null).queryById(fmChannel.getChannelValue(), FMChannelBean.class);
        if (saveBean != null) {
            getChannelInfo(fmChannel, XtingConstants.FMAM.TYPE_FM, fmChannel.getChannelValue() / 100);
            return false;
        }
        saveChannel(fmChannel, XtingConstants.FMAM.TYPE_FM);
        getChannelInfo(fmChannel, XtingConstants.FMAM.TYPE_FM, fmChannel.getChannelValue() / 100);
        return true;
    }

    public boolean saveAMChannel(AMChannelBean amChannel) {
        AMChannelBean saveBean = XtingUtils.getDBManager(null).queryById(amChannel.getChannelValue(), AMChannelBean.class);
        if (saveBean != null) {
            getChannelInfo(amChannel, XtingConstants.FMAM.TYPE_AM, amChannel.getChannelValue());
            return false;
        }
        saveChannel(amChannel, XtingConstants.FMAM.TYPE_AM);
        getChannelInfo(amChannel, XtingConstants.FMAM.TYPE_AM, amChannel.getChannelValue());
        return true;
    }

    private void getChannelInfo(final BaseChannelBean channelBean, final int radioType, int frequency) {
        if (XtingConstants.LOCAL_FM_TO_XMLY_TOGGLE &&
                !TextUtils.isEmpty(mLat) && !TextUtils.isEmpty(mLon)) {
            AcrFactory.getInstance().getSDK().requestRadioMetadata(radioType, mLat, mLon, frequency, new OnRadioResultListener() {
                @Override
                public void onRadioResult(String json) {
                    AcrRadioBean mRadioItem = GsonHelper.fromJson(json, AcrRadioBean.class);
                    if (mRadioItem != null
                            && mRadioItem.status.code == 0
                            && !CollectionUtil.isListEmpty(mRadioItem.data.list)
                            && mRadioItem.data.list.get(0) != null) {
                        channelBean.setChannelCover(mRadioItem.data.list.get(0).logo);
                        if (mRadioItem.data.list.get(0).external_ids != null
                                && !TextUtils.isEmpty(mRadioItem.data.list.get(0).external_ids.ximalaya)) {
                            channelBean.setXmlyId(mRadioItem.data.list.get(0).external_ids.ximalaya);
                        }
                        if (!TextUtils.isEmpty(mRadioItem.data.list.get(0).name)) {
                            channelBean.setChannelName(mRadioItem.data.list.get(0).name);
                        }
                    }
                    saveChannel(channelBean, radioType);
                }
            });
        }
    }

    private void saveChannel(BaseChannelBean channelBean, int radioType) {
        LocalFMOperateManager.newSingleton().saveChannel(channelBean, radioType);
    }

    public void fetchFMChannels() {
        List<FMChannelBean> fmChannels = XtingUtils.getDBManager(null).queryAll(FMChannelBean.class);
        LocalPlayList.getInstance().setFmChannelBeans(fmChannels);
        getFMChannels().setValue(fmChannels);
    }

    public void fetchAMChannels() {
        List<AMChannelBean> amChannels = XtingUtils.getDBManager(null).queryAll(AMChannelBean.class);
        LocalPlayList.getInstance().setAmChannelBeans(amChannels);
        getAMChannels().setValue(amChannels);
    }

    public MutableLiveData<List<FMChannelBean>> getFMChannels() {
        if (mFMChannels == null)
            mFMChannels = new MutableLiveData<>();
        return mFMChannels;
    }

    public MutableLiveData<List<AMChannelBean>> getAMChannels() {
        if (mAMChannels == null)
            mAMChannels = new MutableLiveData<>();
        return mAMChannels;
    }

    public void saveNewFMChannels(List<FMChannelBean> fmChannel) {
        XtingUtils.getDBManager(null).deleteAll(FMChannelBean.class);
        XtingUtils.getDBManager(null).saveAll(fmChannel);
    }

    public void saveNewAMChannels(List<AMChannelBean> amChannels) {
        XtingUtils.getDBManager(null).deleteAll(AMChannelBean.class);
        XtingUtils.getDBManager(null).saveAll(amChannels);
    }

    public void deleteAMChannels(AMChannelBean data) {
        LocalPlayList.getInstance().deleteAM(data, false);
    }

    public void deleteFMChannels(FMChannelBean data) {
        LocalPlayList.getInstance().deleteFM(data, false);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        AcrFactory.getInstance().getSDK().release();
        LocalPlayList.getInstance().removeListener(mChangeListener);
    }
}

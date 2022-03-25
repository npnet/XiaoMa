package com.xiaoma.xting.player.vm;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.xiaoma.xting.common.XtingUtils;
import com.xiaoma.xting.common.playerSource.info.BeanConverter;
import com.xiaoma.xting.common.playerSource.info.model.SubscribeInfo;
import com.xiaoma.xting.local.model.AMChannelBean;
import com.xiaoma.xting.local.model.BaseChannelBean;
import com.xiaoma.xting.local.model.FMChannelBean;
import com.xiaoma.xting.sdk.LocalFMFactory;
import com.xiaoma.xting.sdk.model.BandType;

import java.util.ArrayList;
import java.util.List;

/**
 * <des>
 *
 * @author wutao
 * @date 2018/12/5
 */
public class LocalPlayUpVM extends AndroidViewModel {

    private MutableLiveData<List<BaseChannelBean>> mChannelLists;
    private MutableLiveData<List<BaseChannelBean>> mDbRadio;

    public LocalPlayUpVM(@NonNull Application application) {
        super(application);
    }

    public void getSwitch() {
        getSwitchList().setValue(fetchChannels());
    }

    public MutableLiveData<List<BaseChannelBean>> getSwitchList() {
        if (mChannelLists == null) {
            mChannelLists = new MutableLiveData<>();
        }
        return mChannelLists;
    }

    private List<BaseChannelBean> fetchChannels() {
        List<FMChannelBean> fmChannels;
        List<AMChannelBean> amChannels;
        List<BaseChannelBean> list = new ArrayList<>();
        if (LocalFMFactory.getSDK().getCurrentBand() == BandType.FM) {
            fmChannels = XtingUtils.getDBManager(null).queryAll(FMChannelBean.class);
            list.addAll(fmChannels);
        } else {
            amChannels = XtingUtils.getDBManager(null).queryAll(AMChannelBean.class);
            list.addAll(amChannels);
        }
        return list;
    }

    public void getLocalStoreFM() {
        getDbRadioLiveDate().setValue(getDbRadio());
    }

    public MutableLiveData<List<BaseChannelBean>> getDbRadioLiveDate() {
        if (mDbRadio == null) {
            mDbRadio = new MutableLiveData<>();
        }
        return mDbRadio;
    }

    private List<BaseChannelBean> getDbRadio() {
        BandType currentBand = LocalFMFactory.getSDK().getCurrentBand();
        List<SubscribeInfo> subscribeInfoList;
        if (currentBand == BandType.FM) {
            subscribeInfoList = XtingUtils.getSubscribeDao().selectAllFM();
        } else {
            subscribeInfoList = XtingUtils.getSubscribeDao().selectAllAM();
        }

        return BeanConverter.toChannelList(subscribeInfoList);
    }

}

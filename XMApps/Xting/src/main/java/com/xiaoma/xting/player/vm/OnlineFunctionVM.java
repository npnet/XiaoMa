package com.xiaoma.xting.player.vm;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.xiaoma.model.XmResource;
import com.xiaoma.xting.common.XtingConstants;
import com.xiaoma.xting.common.XtingUtils;
import com.xiaoma.xting.common.playerSource.PlayerSourceFacade;
import com.xiaoma.xting.common.playerSource.contract.PlayerSourceType;
import com.xiaoma.xting.common.playerSource.info.model.SubscribeInfo;
import com.xiaoma.xting.sdk.OnlineFMFactory;
import com.xiaoma.xting.sdk.bean.XMDataCallback;
import com.xiaoma.xting.sdk.bean.XMRelativeAlbums;

import java.util.List;

/**
 * <des>
 *
 * @author YangGang
 * @date 2018/11/6
 */
public class OnlineFunctionVM extends AndroidViewModel {

    private MutableLiveData<XmResource<XMRelativeAlbums>> mRelativeAlbumsLiveData;

    public OnlineFunctionVM(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<XmResource<XMRelativeAlbums>> getRelativeAlbumsLiveData() {
        if (mRelativeAlbumsLiveData == null) {
            mRelativeAlbumsLiveData = new MutableLiveData<>();
        }
        return mRelativeAlbumsLiveData;
    }

    public void fetchSimilar(long searchId) {
        PlayerSourceFacade.newSingleton().setSourceType(PlayerSourceType.HIMALAYAN);
        getRelativeAlbumsLiveData().postValue(XmResource.loading());
        OnlineFMFactory.getInstance().getSDK().getRelativeAlbums(searchId, new XMDataCallback<XMRelativeAlbums>() {
            @Override
            public void onSuccess(@Nullable XMRelativeAlbums data) {
                if (data != null) {
                    getRelativeAlbumsLiveData().setValue(XmResource.success(data));
                } else {
                    getRelativeAlbumsLiveData().setValue(XmResource.failure(XtingConstants.EMPTY_DATA));
                }
            }

            @Override
            public void onError(int code, String msg) {
                getRelativeAlbumsLiveData().setValue(XmResource.error(code, msg));
            }
        });
    }


    private MutableLiveData<List<SubscribeInfo>> mSubscribeListLiveData;

    public void fetchSubscribes() {
        setSubscribeListLiveData(XtingUtils.getSubscribeDao().selectAllRadio());
    }

    public MutableLiveData<List<SubscribeInfo>> getSubscribeListLiveData() {
        if (mSubscribeListLiveData == null) {
            mSubscribeListLiveData = new MutableLiveData<>();
        }
        return mSubscribeListLiveData;
    }

    private void setSubscribeListLiveData(List<SubscribeInfo> value) {
        getSubscribeListLiveData().setValue(value);
    }
}

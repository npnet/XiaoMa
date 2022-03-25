package com.xiaoma.xting.online.vm;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.xiaoma.model.XmResource;
import com.xiaoma.ui.toast.XMToast;
import com.xiaoma.xting.R;
import com.xiaoma.xting.common.playerSource.PlayerSourceFacade;
import com.xiaoma.xting.common.playerSource.contract.PlayerAction;
import com.xiaoma.xting.common.playerSource.contract.PlayerSourceSubType;
import com.xiaoma.xting.common.playerSource.contract.PlayerSourceType;
import com.xiaoma.xting.common.playerSource.contract.PlayerStatus;
import com.xiaoma.xting.common.playerSource.info.BeanConverter;
import com.xiaoma.xting.common.playerSource.info.impl.PlayerInfoImpl;
import com.xiaoma.xting.common.playerSource.info.model.PlayerInfo;
import com.xiaoma.xting.common.playerSource.loadmore.IFetchListener;
import com.xiaoma.xting.sdk.bean.XMRadio;

/**
 * <des>
 *
 * @author YangGang
 * @date 2019/5/29
 */
public class PlayWithPlayerInfoVM extends AndroidViewModel {
    private MutableLiveData<XmResource<Object>> mState;

    public PlayWithPlayerInfoVM(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<XmResource<Object>> getState() {
        if (mState == null) {
            mState = new MutableLiveData<>();
        }
        return mState;
    }

    private void setState(XmResource<Object> state) {
        getState().setValue(state);
    }

    public void play(XMRadio xmRadio) {
        PlayerSourceFacade.newSingleton().setSourceType(PlayerSourceType.HIMALAYAN);
        if (xmRadio.getScheduleID() <= 0) {
            PlayerSourceFacade.newSingleton().getPlayerControl().playWithModel(xmRadio);
        } else {
            PlayerInfo playerInfo = BeanConverter.toPlayerInfo(xmRadio);
            playerInfo.setSourceSubType(PlayerSourceSubType.SCHEDULE); // 表示调用Shedule 进行播放
            PlayerSourceFacade.newSingleton().getPlayerFetch().fetch(playerInfo, new IFetchListener() {
                @Override
                public void onLoading() {
                    PlayerInfoImpl.newSingleton().onPlayerStatusChanged(PlayerStatus.LOADING);
                }

                @Override
                public void onSuccess(Object o) {
                    PlayerSourceFacade.newSingleton().getPlayerControl().play();
                }

                @Override
                public void onFail() {
                    PlayerInfoImpl.newSingleton().onPlayerStatusChanged(PlayerStatus.ERROR);
                }

                @Override
                public void onError(int code, String msg) {
                    XMToast.showToast(getApplication(), R.string.net_work_error);
                    PlayerInfoImpl.newSingleton().onPlayerStatusChanged(PlayerStatus.ERROR);
                }
            });
        }
    }

    public void play(PlayerInfo playerInfo) {
        int type = playerInfo.getType();

        if (type == PlayerSourceType.RADIO_XM) {
            type = PlayerSourceType.HIMALAYAN;
            playerInfo.setType(type);
        }

        playerInfo.setPreShowF(true);
        PlayerInfoImpl.newSingleton().onPlayerInfoChanged(playerInfo);

        if (PlayerSourceFacade.newSingleton().getSourceType() != PlayerSourceType.DEFAULT
                && !PlayerSourceFacade.newSingleton().setSourceType(type)) {
            PlayerSourceFacade.newSingleton().getPlayerControl().switchPlayerAlbum(null);
        }
        playerInfo.setAction(PlayerAction.PLAY_LIST);
        PlayerSourceFacade.newSingleton().getPlayerFetch().fetch(playerInfo, new IFetchListener() {
            @Override
            public void onLoading() {
                PlayerInfoImpl.newSingleton().onPlayerStatusChanged(PlayerStatus.PAUSE);
            }

            @Override
            public void onSuccess(Object o) {
                setState(XmResource.success(o));
            }

            @Override
            public void onFail() {
                setState(XmResource.failure(""));
                XMToast.showToast(getApplication(), R.string.state_fail_by_loading_data);
                PlayerInfoImpl.newSingleton().onPlayerStatusChanged(PlayerStatus.ERROR);
            }

            @Override
            public void onError(int code, String msg) {
                setState(XmResource.error(code, msg));
                XMToast.showToast(getApplication(), R.string.net_work_error);
                PlayerInfoImpl.newSingleton().onPlayerStatusChanged(PlayerStatus.ERROR);
            }
        });
    }
}

package com.xiaoma.xting.common.playerSource;

import android.util.Log;
import android.util.SparseArray;

import com.xiaoma.component.AppHolder;
import com.xiaoma.config.bean.SourceType;
import com.xiaoma.config.utils.SourceUtils;
import com.xiaoma.ui.toast.XMToast;
import com.xiaoma.vr.iat.RemoteIatManager;
import com.xiaoma.vr.model.AppType;
import com.xiaoma.xting.R;
import com.xiaoma.xting.common.playerSource.contract.PlayerOperate;
import com.xiaoma.xting.common.playerSource.contract.PlayerPlayMode;
import com.xiaoma.xting.common.playerSource.contract.PlayerSourceType;
import com.xiaoma.xting.common.playerSource.control.AudioFocusManager;
import com.xiaoma.xting.common.playerSource.control.IPlayerControl;
import com.xiaoma.xting.common.playerSource.control.controlImpl.HimalayanControl;
import com.xiaoma.xting.common.playerSource.control.controlImpl.KoalaControl;
import com.xiaoma.xting.common.playerSource.control.controlImpl.YQRadioControl;
import com.xiaoma.xting.common.playerSource.info.callback.HimalayanStatusListenerImpl;
import com.xiaoma.xting.common.playerSource.info.impl.PlayerInfoImpl;
import com.xiaoma.xting.common.playerSource.info.sharedPref.SharedPrefUtils;
import com.xiaoma.xting.common.playerSource.loadmore.IPlayerFetch;
import com.xiaoma.xting.common.playerSource.loadmore.impl.HimalayanPlayerFetch;
import com.xiaoma.xting.common.playerSource.loadmore.impl.KoalaPlayerFetch;
import com.xiaoma.xting.common.playerSource.loadmore.impl.XMServerFetch;
import com.xiaoma.xting.common.playerSource.loadmore.impl.YQRadioPlayerFetch;
import com.xiaoma.xting.common.playerSource.time.SystemLivingTimeReceiver;
import com.xiaoma.xting.launcher.XtingAudioClient;
import com.xiaoma.xting.sdk.OnlineFMPlayerFactory;

/**
 * <des>
 *
 * @author YangGang
 * @date 2019/5/28
 */
public class PlayerSourceFacade {

    private @PlayerSourceType
    int mSourceType;
    private int mPlayMode;
    private SparseArray<IPlayerControl> mControlCache;
    private SparseArray<IPlayerFetch> mFetch;

    private PlayerSourceFacade() {
        mPlayMode = -1;
        mSourceType = PlayerSourceType.DEFAULT;
        mControlCache = new SparseArray<>(3);
        mFetch = new SparseArray<>(3);
    }

    public static PlayerSourceFacade newSingleton() {
        return Holder.sINSTANCE;
    }

    public IPlayerControl getPlayerControl() {
        return getPlayerControl(mSourceType);
    }

    public IPlayerControl getPlayerControl(@PlayerSourceType int type) {
        if (type == PlayerSourceType.DEFAULT) {
            return null;
        }
        IPlayerControl control = mControlCache.get(type);
        if (control == null) {
            switch (type) {
                case PlayerSourceType.HIMALAYAN:
                case PlayerSourceType.RADIO_XM:
                    control = new HimalayanControl();
                    break;
                case PlayerSourceType.KOALA:
                    control = new KoalaControl();
                    break;
                case PlayerSourceType.RADIO_YQ:
                    control = new YQRadioControl();
                    break;
            }

            mControlCache.put(type, control);
        }
        return control;
    }

    public IPlayerFetch getPlayerFetch() {
        checkTypeValidation();
        IPlayerFetch fetch = mFetch.get(mSourceType);
        if (fetch == null) {
            switch (mSourceType) {
                case PlayerSourceType.HIMALAYAN:
                    fetch = new HimalayanPlayerFetch();
                    break;
                case PlayerSourceType.KOALA:
                    fetch = new KoalaPlayerFetch();
                    break;
                case PlayerSourceType.RADIO_YQ:
                    fetch = new YQRadioPlayerFetch();
                    break;
                case PlayerSourceType.RADIO_XM:
                    fetch = new XMServerFetch();
                    break;
            }
            mFetch.put(mSourceType, fetch);
        }
        return fetch;
    }

    @PlayerSourceType
    public int getSourceType() {
        return mSourceType;
    }

    /**
     * 设置 数据源
     *
     * @param type 电台类型 {@link PlayerSourceType}
     */
    public boolean setSourceType(@PlayerSourceType int type) {
        Log.d("YG", "{setSourceType}-[cur / new] : " + mSourceType + " / " + type);
        if (mSourceType != type) {
            //语音要求 ： 如果没有音源信息就不传递，有变化就传递，本地 AppType.RADIO,网络 AppType.INTERNET_RADIO
            if (type == PlayerSourceType.RADIO_YQ) {
                XMToast.showToast(AppHolder.getInstance().getAppContext(), R.string.now_is_fm_play);
            } else {
                if (mSourceType == PlayerSourceType.RADIO_YQ && type != PlayerSourceType.DEFAULT) {
                    XMToast.showToast(AppHolder.getInstance().getAppContext(), R.string.now_is_online_play);
                }
            }
            if (mSourceType != PlayerSourceType.DEFAULT) {
                IPlayerControl lastPlayerControl = getPlayerControl();
                if (lastPlayerControl != null) {
                    if (type == PlayerSourceType.RADIO_YQ) {
                        lastPlayerControl.pause();
                    } else {
                        lastPlayerControl.switchPlayerSource(type);
                    }
                    if (type != PlayerSourceType.DEFAULT) {
                        SourceUtils.setSourceStatus(SourceType.NET_FM, false);
                        RemoteIatManager.getInstance().uploadPlayState(false, AppType.INTERNET_RADIO);
                    }
                }
                if (type != PlayerSourceType.RADIO_XM
                        && type != PlayerSourceType.KOALA) {
                    XtingAudioClient.newSingleton(AppHolder.getInstance().getAppContext()).restoreLauncherCategoryId();
                }

            }
            mSourceType = type;
            if (type != PlayerSourceType.HIMALAYAN) {
                SystemLivingTimeReceiver.newSingleton()
                        .unRegisterReceiver(AppHolder.getInstance().getAppContext(), HimalayanStatusListenerImpl.newSingleton());
                //桌面点击播放 ，不进行记忆，每次从头开始播放
                if (type == PlayerSourceType.RADIO_XM) {
                    OnlineFMPlayerFactory.getPlayer().clearMemory(false);
                }
            } else {
                OnlineFMPlayerFactory.getPlayer().clearMemory(true);
            }
            IPlayerControl nowPlayerControl = getPlayerControl();
            if (nowPlayerControl != null) {
                nowPlayerControl.init(AppHolder.getInstance().getAppContext());
                nowPlayerControl.setPlayMode(getPlayMode());
            }
            AudioFocusManager.getInstance().setPlayerControl(nowPlayerControl);
            return true;
        }

        return false;
    }

    public void switchPlayerType() {
        if (mSourceType == PlayerSourceType.RADIO_YQ) {
            PlayerSourceFacade.newSingleton().setSourceType(PlayerSourceType.DEFAULT);
            PlayerInfoImpl.newSingleton().onPlayerInfoChanged(null);
        }
    }

    public int getPlayMode() {
        if (mPlayMode == -1) {
            mPlayMode = SharedPrefUtils.getPlayMode(AppHolder.getInstance().getAppContext());
            if (mPlayMode == -1) {
                setPlayMode(PlayerPlayMode.SEQUENTIAL);
            }
        }
        return mPlayMode;
    }

    @PlayerOperate
    public int setPlayMode(int playMode) {
        if (mPlayMode != playMode) {
            mPlayMode = playMode;
            SharedPrefUtils.cachePlayMode(AppHolder.getInstance().getAppContext(), playMode);
            return getPlayerControl().setPlayMode(playMode);
        }
        return PlayerOperate.DEFAULT;
    }

    /**
     * 用于避免弱网时候切换之后 音源播放混乱的问题
     *
     * @param sourceType 请求校验的sourceType
     * @return
     */
    public boolean checkType(@PlayerSourceType int sourceType) {
        Log.d("YG", "{checkType}-[cur / check] : " + mSourceType + " / " + sourceType);
        return mSourceType == sourceType;
    }

    private void checkTypeValidation() {
        if (mSourceType == PlayerSourceType.DEFAULT) {
            throw new NullPointerException("You should call XtingManger#setSourceType(int) first!");
        }
    }

    interface Holder {
        PlayerSourceFacade sINSTANCE = new PlayerSourceFacade();
    }
}

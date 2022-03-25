package com.xiaoma.xting.common.playerSource.loadmore.impl;

import android.support.annotation.Nullable;
import android.util.Log;

import com.xiaoma.xting.common.playerSource.PlayerSourceFacade;
import com.xiaoma.xting.common.playerSource.contract.PlayerAction;
import com.xiaoma.xting.common.playerSource.contract.PlayerSourceType;
import com.xiaoma.xting.common.playerSource.info.BeanConverter;
import com.xiaoma.xting.common.playerSource.info.impl.PlayerInfoImpl;
import com.xiaoma.xting.common.playerSource.loadmore.IFetchListener;
import com.xiaoma.xting.common.playerSource.loadmore.IPlayerFetch;
import com.xiaoma.xting.sdk.OnlineFM;
import com.xiaoma.xting.sdk.OnlineFMFactory;
import com.xiaoma.xting.sdk.OnlineFMPlayerFactory;
import com.xiaoma.xting.sdk.bean.AbsXMDataCallback;
import com.xiaoma.xting.sdk.bean.XMRadio;
import com.xiaoma.xting.sdk.bean.XMRadioListById;
import com.xiaoma.xting.sdk.bean.XMSchedule;
import com.xiaoma.xting.sdk.bean.XMScheduleList;
import com.xiaoma.xting.sdk.bean.XMTrack;
import com.xiaoma.xting.sdk.utils.HimalayanPlayerUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * <des>
 *
 * @author YangGang
 * @date 2019/6/5
 */
public class YQRadioPlayerFetch extends IPlayerFetch {

    private static final String TAG = YQRadioPlayerFetch.class.getSimpleName();
    private final OnlineFM mRequest;

    public YQRadioPlayerFetch() {
        mRequest = OnlineFMFactory.getInstance().getSDK();
    }

    @Override
    protected void fetch(final IFetchListener listener) {
        mRequest.getTodaySchedules(mPlayerInfo.getAlbumId(), new AbsXMDataCallback<XMScheduleList>() {
            @Override
            public void onSuccess(@Nullable XMScheduleList data) {
                if (!PlayerSourceFacade.newSingleton().checkType(PlayerSourceType.RADIO_YQ)) {
                    Log.d("YG", "{Type}-[Change] : " + TAG);
                    return;
                }
                if (data != null) {
                    List<XMSchedule> xmSchedules = data.getmScheduleList();
                    if (xmSchedules != null && xmSchedules.size() > 0) {
                        if (mPlayerInfo.getAction() == PlayerAction.SET_LIST) {
                            List<XMTrack> xmTracks = new ArrayList<>(xmSchedules.size());
                            for (XMSchedule xmSchedule : xmSchedules) {
                                xmTracks.add(HimalayanPlayerUtils.schedule2Track(xmSchedule));
                            }
                            OnlineFMPlayerFactory.getPlayer().setPlayList(xmTracks, HimalayanPlayerUtils.checkIndexWithTime(xmSchedules));
                        } else {
                            OnlineFMPlayerFactory.getPlayer().playSchedule(xmSchedules, -1);
                        }
                        dispatchSuccess(xmSchedules, listener);
                        return;
                    }
                }
                handleFail(listener);
            }

            @Override
            public void onError(int code, String msg) {
                if (!PlayerSourceFacade.newSingleton().checkType(PlayerSourceType.RADIO_YQ)) {
                    Log.d("YG", "{Type}-[Change] : " + TAG);
                    return;
                }
                dispatchError(code, msg, listener);
            }
        });
    }

    private void handleFail(final IFetchListener listener) {
        mRequest.getRadioById(mPlayerInfo.getAlbumId(), new AbsXMDataCallback<XMRadioListById>() {
            @Override
            public void onSuccess(long albumId, @Nullable XMRadioListById data) {
                if (data != null) {
                    List<XMRadio> radioList = data.getRadios();
                    if (radioList != null && radioList.size() > 0) {
                        XMRadio xmRadio = radioList.get(0);
                        if (mPlayerInfo.getAction() == PlayerAction.SET_LIST) {
                            OnlineFMPlayerFactory.getPlayer().setRadio(xmRadio);
                        } else {
                            PlayerSourceFacade.newSingleton().setSourceType(PlayerSourceType.HIMALAYAN);
                            PlayerInfoImpl.newSingleton().onPlayerInfoChanged(BeanConverter.toPlayerInfo(xmRadio));
                            OnlineFMPlayerFactory.getPlayer().playRadio(xmRadio);
                        }
                        dispatchSuccess(xmRadio, listener);
                        return;
                    }
                }

                dispatchFail(listener);
            }

            @Override
            public void onError(int code, String msg) {
                dispatchError(code, msg, listener);
            }
        });
    }
}

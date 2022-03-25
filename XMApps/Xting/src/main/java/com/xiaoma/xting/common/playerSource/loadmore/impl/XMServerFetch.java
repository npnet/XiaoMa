package com.xiaoma.xting.common.playerSource.loadmore.impl;

import android.app.Application;
import android.util.Log;

import com.xiaoma.component.AppHolder;
import com.xiaoma.model.ResultCallback;
import com.xiaoma.model.XMResult;
import com.xiaoma.ui.toast.XMToast;
import com.xiaoma.xting.R;
import com.xiaoma.xting.common.RequestManager;
import com.xiaoma.xting.common.playerSource.PlayerSourceFacade;
import com.xiaoma.xting.common.playerSource.contract.PlayerAction;
import com.xiaoma.xting.common.playerSource.contract.PlayerSourceType;
import com.xiaoma.xting.common.playerSource.loadmore.IFetchListener;
import com.xiaoma.xting.common.playerSource.loadmore.IPlayerFetch;
import com.xiaoma.xting.launcher.model.LauncherAlbum;
import com.xiaoma.xting.sdk.OnlineFMPlayerFactory;
import com.xiaoma.xting.sdk.bean.XMTrack;
import com.xiaoma.xting.sdk.utils.HimalayanPlayerUtils;

import java.util.ArrayList;
import java.util.Random;

/**
 * <des>
 *
 * @author YangGang
 * @date 2019/5/27
 */
public class XMServerFetch extends IPlayerFetch {

    private static final String TAG = XMServerFetch.class.getSimpleName();

    @Override
    protected void fetch(IFetchListener listener) {
//        int page = mPlayerInfo.getPage();
//        if (page < 0) {
//            page = 0;
//        }
        Log.d("LPL", "{fetch}-[mPlayerInfo] : " + mPlayerInfo);
        RequestManager.requestAudioList(String.valueOf(mPlayerInfo.getCategoryId()), 0, new ResultCallback<XMResult<LauncherAlbum>>() {
            @Override
            public void onSuccess(XMResult<LauncherAlbum> result) {
                if (!PlayerSourceFacade.newSingleton().checkType(PlayerSourceType.RADIO_XM)) {
                    Log.d(TAG, "{Type}-[Change] : ");
                    return;
                }
                if (result != null) {
                    LauncherAlbum data = result.getData();
                    if (data != null) {
//                    setPageExtremes(0, data.getTotalPages(), data.getTotal());
//                    setPageBound(data.getCurrentPage());
                        ArrayList<XMTrack> trackList = data.getTrackList();
                        if (trackList != null && !trackList.isEmpty()) {
                            int index = 0;
                            if (mPlayerInfo.getPage() >= 0) {
                                index = HimalayanPlayerUtils.checkIndexWithId(trackList, mPlayerInfo.getProgramId());
                                if (index < 0) {
                                    Log.d(TAG, "{onSuccess}-[Add PlayerInfo] : " + index + " - " + mPlayerInfo);
                                    XMTrack xmTrack = HimalayanPlayerUtils.toXMTrack(mPlayerInfo);
                                    trackList.add(0, xmTrack);
                                    index = 0;
                                }
                            }
                            setPageExtremes(0, data.getCurrentPage(), trackList.size());
                            setPageBound(data.getCurrentPage());

                            if (mPlayerInfo.getAction() == PlayerAction.PLAY_LIST) {
                                int random = new Random().nextInt(trackList.size());
                                OnlineFMPlayerFactory.getPlayer().playList(trackList, random);
                            } else {
                                OnlineFMPlayerFactory.getPlayer().setPlayList(trackList, index);
                            }
                            dispatchSuccess(trackList, listener);
                            return;
                        }

                    }
                }

                dispatchFail(listener);
            }

            @Override
            public void onFailure(int code, String msg) {
                if (!PlayerSourceFacade.newSingleton().checkType(PlayerSourceType.RADIO_XM)) {
                    Log.d(TAG, "{Type}-[Change] : ");
                    return;
                }
                Application context = AppHolder.getInstance().getAppContext();
                XMToast.showToast(context, context.getString(R.string.net_work_error));
                dispatchError(code, msg, listener);
            }
        });

    }
}

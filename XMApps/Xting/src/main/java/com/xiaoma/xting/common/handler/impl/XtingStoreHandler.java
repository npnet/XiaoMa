package com.xiaoma.xting.common.handler.impl;

import android.os.Bundle;

import com.xiaoma.center.logic.remote.ClientCallback;
import com.xiaoma.player.AudioConstants;
import com.xiaoma.player.AudioInfo;
import com.xiaoma.utils.ListUtils;
import com.xiaoma.xting.common.XtingConstants;
import com.xiaoma.xting.common.XtingUtils;
import com.xiaoma.xting.common.handler.AbsActionHandler;
import com.xiaoma.xting.common.playerSource.contract.PlayerSourceSubType;
import com.xiaoma.xting.common.playerSource.contract.PlayerSourceType;
import com.xiaoma.xting.common.playerSource.info.model.SubscribeInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * <des>
 *
 * @author YangGang
 * @date 2019/3/15
 */
public class XtingStoreHandler extends AbsActionHandler {

    public XtingStoreHandler(ClientCallback callback) {
        super(callback);
    }

    @Override
    public void handleRequestAction(Bundle bundle) {
        List<SubscribeInfo> subscribeList = XtingUtils.getSubscribeDao().selectAll();
        ArrayList<AudioInfo> collectLists = new ArrayList<>();

        if (!ListUtils.isEmpty(subscribeList)) {
            AudioInfo bean;
//            XtingAudioClient.newSingleton(AppHolder.getInstance().getAppContext()).restoreLauncherCategoryId();
            for (SubscribeInfo subscribeInfo : subscribeList) {
                bean = new AudioInfo();
                bean.setAlbumId(subscribeInfo.getAlbumId());
                bean.setCover(subscribeInfo.getCoverUrl());
                bean.setTitle(subscribeInfo.getAlbumName());
                bean.setSubTitle(subscribeInfo.getProgramName());
                int sourceType = subscribeInfo.getSourceType();
                if (sourceType == PlayerSourceType.HIMALAYAN) {
                    int sourceSubType = subscribeInfo.getSourceSubType();
                    if (sourceSubType == PlayerSourceSubType.TRACK) {
                        bean.setAudioType(AudioConstants.AudioTypes.XTING_NET_FM);
                    } else {
                        bean.setAudioType(AudioConstants.AudioTypes.XTING_NET_RADIO);
                    }
                } else if (sourceType == PlayerSourceType.KOALA) {
                    bean.setAudioType(AudioConstants.AudioTypes.XTING_KOALA_ALBUM);
                } else if (sourceType == PlayerSourceType.RADIO_YQ) {
                    bean.setTitle(getRadioTitle(bean.getAlbumId()));
                    bean.setAudioType(AudioConstants.AudioTypes.XTING_LOCAL_FM);
                } else if (sourceType == PlayerSourceType.RADIO_XM) {
                    bean.setAudioType(AudioConstants.AudioTypes.XTING_NET_FM);
                }

                collectLists.add(bean);
            }
        }

        share(collectLists);

    }

    private String getRadioTitle(long albumId) {
        if (albumId >= XtingConstants.FMAM.getFMStart()
                && albumId <= XtingConstants.FMAM.getFMEnd()) {
            return XtingUtils.getFMTitle((int) albumId);
        } else {
            return XtingUtils.getAMTitle((int) albumId);
        }
    }

    private void share(ArrayList<AudioInfo> list) {
        Bundle callbackData = new Bundle();
        callbackData.putString(AudioConstants.BundleKey.ACTION, AudioConstants.BundleKey.AUDIO_LIST);
        callbackData.putInt(AudioConstants.BundleKey.AUDIO_RESPONSE_CODE, AudioConstants.AudioResponseCode.SUCCESS);
        if (list != null && !list.isEmpty()) {
            callbackData.putParcelableArrayList(AudioConstants.BundleKey.AUDIO_LIST, list);
        }
        dispatchRequestCallback(callbackData);
    }
}

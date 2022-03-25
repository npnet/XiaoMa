package com.xiaoma.xting.common.handler.impl;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.xiaoma.center.logic.CenterConstants;
import com.xiaoma.center.logic.remote.ClientCallback;
import com.xiaoma.player.AudioConstants;
import com.xiaoma.utils.ListUtils;
import com.xiaoma.vr.tts.EventTtsManager;
import com.xiaoma.vr.tts.OnTtsListener;
import com.xiaoma.xting.assistant.AssistantRepository;
import com.xiaoma.xting.common.XtingConstants;
import com.xiaoma.xting.common.handler.AbsActionHandler;
import com.xiaoma.xting.common.playerSource.PlayerSourceFacade;
import com.xiaoma.xting.common.playerSource.contract.PlayerSourceType;
import com.xiaoma.xting.launcher.ToLauncherBeanConvert;
import com.xiaoma.xting.sdk.OnlineFMFactory;
import com.xiaoma.xting.sdk.OnlineFMPlayerFactory;
import com.xiaoma.xting.sdk.bean.XMDataCallback;
import com.xiaoma.xting.sdk.bean.XMRadio;
import com.xiaoma.xting.sdk.bean.XMRadioList;
import com.xiaoma.xting.sdk.bean.XMSearchAll;

import java.util.List;
import java.util.Random;

/**
 * <des>
 *
 * @author YangGang
 * @date 2019/3/26
 */
public class SearchRadiosHandler extends AbsActionHandler {

    private static final String TAG = "SearchRadiosHandler";

    private static String [] localTTs = {"好嘞","没问题","喏"};

    public SearchRadiosHandler(ClientCallback callback) {
        super(callback);
    }

    @Override
    public void handleRequestAction(final Bundle bundle) {
        String name = bundle.getString("Key");
        Log.d(TAG + "_KEY", "[ handleRequestAction ] * bundle = " + bundle.toString() +
                "\n at " + Log.getStackTraceString(new Throwable()));
        if (TextUtils.isEmpty(name)) {
            shareBoolean(false);
        } else {
            PlayerSourceFacade.newSingleton().setSourceType(PlayerSourceType.HIMALAYAN);
            OnlineFMFactory.getInstance().getSDK().getSearchAll(name, 1, XtingConstants.PAGE_COUNT, new XMDataCallback<XMSearchAll>() {
                @Override
                public void onSuccess(@Nullable XMSearchAll data) {
                    if (data != null) {
                        XMRadioList radioList = data.getRadioList();
                        if (radioList != null && !ListUtils.isEmpty(radioList.getRadios())) {
                            List<XMRadio> radios = radioList.getRadios();
                            AssistantRepository.newSingleton().updateRadios(radios);
                            shareRadios(radios);
                            if (bundle.getBoolean("play")) {
                                XMRadio xmRadio = radios.get(0);
                                if (bundle.getBoolean(CenterConstants.XtingThirdBundleKey.RADIO_LOCAL_TTS, false)) {
                                    EventTtsManager.getInstance().startSpeaking(getFinishWord(), new OnTtsListener() {
                                        @Override
                                        public void onCompleted() {
                                            OnlineFMPlayerFactory.getPlayer().playRadio(xmRadio);
                                        }

                                        @Override
                                        public void onBegin() {

                                        }

                                        @Override
                                        public void onError(int code) {
                                        }
                                    });
                                } else {
                                    OnlineFMPlayerFactory.getPlayer().playRadio(xmRadio);
                                }
                            }
                            return;
                        }
                    }

                    onError(-1, "");
                }

                @Override
                public void onError(int code, String msg) {
                    shareBoolean(false);
                }
            });
        }
    }

    /**
     * 主要用于语音助手的反馈
     *
     * @param operateOk
     */
    private void shareBoolean(boolean operateOk) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(CenterConstants.XtingThirdBundleKey.RESULT, operateOk);
        dispatchRequestCallback(bundle);
    }

    private void shareRadios(List<XMRadio> list) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(CenterConstants.XtingThirdBundleKey.RESULT, true);
        bundle.putParcelableArrayList(AudioConstants.BundleKey.AUDIO_LIST, ToLauncherBeanConvert.radioList2AudioInfoList(list));

        dispatchRequestCallback(bundle);
    }

    private String getFinishWord() {
        Random random = new Random();
        int index = random.nextInt(localTTs.length);
        return localTTs[index];
    }

}

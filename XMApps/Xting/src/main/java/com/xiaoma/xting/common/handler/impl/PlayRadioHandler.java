package com.xiaoma.xting.common.handler.impl;

import android.os.Bundle;

import com.xiaoma.center.logic.CenterConstants;
import com.xiaoma.center.logic.remote.ClientCallback;
import com.xiaoma.player.AudioConstants;
import com.xiaoma.utils.ListUtils;
import com.xiaoma.xting.assistant.AssistantRepository;
import com.xiaoma.xting.common.handler.AbsActionHandler;
import com.xiaoma.xting.sdk.XmlyPlayer;
import com.xiaoma.xting.sdk.bean.XMRadio;

import java.util.List;

/**
 * <des>
 *
 * @author YangGang
 * @date 2019/3/26
 */
public class PlayRadioHandler extends AbsActionHandler {

    public PlayRadioHandler(ClientCallback callback) {
        super(callback);
    }

    @Override
    public void handleRequestAction(Bundle data) {
        //默认从第一首开始播
        List<XMRadio> radios = AssistantRepository.newSingleton().getRadios();
        if (!ListUtils.isEmpty(radios)) {
            int radioIndex = data.getInt(CenterConstants.XtingThirdBundleKey.RADIO_INDEX, -1);
            long radioId = data.getLong(CenterConstants.XtingThirdBundleKey.RADIO_ID, -1);
            XMRadio xmRadio = radios.get(radioIndex);
            if (xmRadio.getDataId() == radioId) {
                XmlyPlayer.getInstance().playRadio(xmRadio);
                share(true);
            } else {
                for (XMRadio radio : radios) {
                    if (radio.getDataId() == radioId) {
                        XmlyPlayer.getInstance().playRadio(radio);
                        share(true);
                        return;
                    }
                }

            }

        }

        share(false);
    }

    private void share(boolean operateOK) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(AudioConstants.BundleKey.RESULT, operateOK);

        dispatchRequestCallback(bundle);
    }
}

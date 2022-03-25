package com.xiaoma.music.export.HandleKwActionFactory;

import android.content.Context;
import android.os.Bundle;

import com.xiaoma.center.logic.remote.ClientCallback;
import com.xiaoma.music.common.audiosource.AudioSource;
import com.xiaoma.music.common.audiosource.AudioSourceManager;
import com.xiaoma.music.export.client.AssistantClient;
import com.xiaoma.player.AbsAudioClient;
import com.xiaoma.player.AudioConstants;
import com.xiaoma.utils.tputils.TPUtils;

/**
 * Created by ZYao.
 * Date ：2019/3/12 0012
 */
public class AudioSourceRequestHandler extends BaseRequestInterceptHandler {

    public AudioSourceRequestHandler(Context mContext, ClientCallback clientCallback) {
        super(mContext, clientCallback);
    }

    @Override
    public void handler(AbsAudioClient client, Bundle data) {

    }

    @Override
    public void handlerAssistant(AssistantClient client, int action, Bundle data) {
        int currentAudioType;
        @AudioSource int currAudioSource = AudioSourceManager.getInstance().getCurrAudioSource();
        switch (currAudioSource) {
            case AudioSource.BLUETOOTH_MUSIC:
                currentAudioType = AudioConstants.AudioTypes.MUSIC_LOCAL_BT;
                break;
            case AudioSource.NONE:
                @AudioSource int saveSource = TPUtils.get(mContext, AudioSourceManager.KEY_AUDIO_SOURCE, AudioSource.NONE);
                switch (saveSource) {
                    case AudioSource.BLUETOOTH_MUSIC:
                        currentAudioType = AudioConstants.AudioTypes.MUSIC_LOCAL_BT;
                        break;
                    case AudioSource.NONE:
                        currentAudioType = AudioConstants.AudioTypes.NONE;
                        break;
                    case AudioSource.ONLINE_MUSIC:
                        currentAudioType = AudioConstants.AudioTypes.MUSIC_ONLINE_KUWO;
                        break;
                    case AudioSource.USB_MUSIC:
                        currentAudioType = AudioConstants.AudioTypes.MUSIC_LOCAL_USB;
                        break;
                    default:
                        currentAudioType = AudioConstants.AudioTypes.NONE;
                        break;
                }
                break;
            case AudioSource.ONLINE_MUSIC:
                currentAudioType = AudioConstants.AudioTypes.MUSIC_ONLINE_KUWO;
                break;
            case AudioSource.USB_MUSIC:
                currentAudioType = AudioConstants.AudioTypes.MUSIC_LOCAL_USB;
                break;
            default:
                currentAudioType = AudioConstants.AudioTypes.NONE;
                break;
        }

        if (clientCallback != null) {
            Bundle callbackData = new Bundle();
            callbackData.putInt(AudioConstants.BundleKey.ACTION, action);
            callbackData.putInt(AudioConstants.BundleKey.RESULT, currentAudioType);
            clientCallback.setData(callbackData);
            clientCallback.callback();
        }
    }

    @Override
    public void searchInKw(AssistantClient client, int action, Bundle data) {

    }

    @Override
    public void searchInUsb(AssistantClient client, int action, Bundle data) {

    }
}

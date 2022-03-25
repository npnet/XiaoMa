package com.xiaoma.music.export.HandleKwActionFactory;

import android.content.Context;
import android.os.Bundle;

import com.xiaoma.center.logic.remote.ClientCallback;
import com.xiaoma.music.OnlineMusicFactory;
import com.xiaoma.music.export.client.AssistantClient;
import com.xiaoma.music.kuwo.impl.IKuwoConstant;
import com.xiaoma.music.kuwo.listener.OnAudioFetchListener;
import com.xiaoma.music.kuwo.model.XMMusic;
import com.xiaoma.player.AbsAudioClient;
import com.xiaoma.player.AudioConstants;

import cn.kuwo.base.bean.Music;

/**
 * Created by ZYao.
 * Date ：2019/3/12 0012
 */
public class FetchImageRequestHandler extends BaseRequestInterceptHandler {


    public FetchImageRequestHandler(Context mContext, ClientCallback clientCallback) {
        super(mContext, clientCallback);
    }

    @Override
    public void handler(AbsAudioClient client, Bundle data) {
        long id = data.getLong(AudioConstants.BundleKey.KW_MUSIC_ID, -1);
        OnlineMusicFactory.getKWAudioFetch().fetchMusicById(id, new OnAudioFetchListener<XMMusic>() {
            @Override
            public void onFetchSuccess(XMMusic xmMusic) {
                if (XMMusic.isEmpty(xmMusic)) {
                    shareKwImageUrl("", AudioConstants.AudioResponseCode.ERROR, clientCallback);
                    return;
                }
                fetchImage(xmMusic);
            }

            @Override
            public void onFetchFailed(String msg) {
                Music music = new Music();
                music.rid = id;
                fetchImage(new XMMusic(music));
            }
        });
    }

    @Override
    public void searchInKw(AssistantClient client, int action, Bundle data) {

    }

    @Override
    public void searchInUsb(AssistantClient client, int action, Bundle data) {

    }

    private void fetchImage(XMMusic music) {
        OnlineMusicFactory.getKWAudioFetch().fetchImage(music, new OnAudioFetchListener<String>() {
            @Override
            public void onFetchSuccess(String s) {
                shareKwImageUrl(s, AudioConstants.AudioResponseCode.SUCCESS, clientCallback);
            }

            @Override
            public void onFetchFailed(String msg) {
                shareKwImageUrl(msg, AudioConstants.AudioResponseCode.ERROR, clientCallback);
            }
        }, IKuwoConstant.IImageSize.SIZE_240);
    }

    public void shareKwImageUrl(String url, int code, ClientCallback clientCallback) {
        if (clientCallback != null) {
            Bundle callbackData = new Bundle();
            callbackData.putString(AudioConstants.BundleKey.ACTION, AudioConstants.BundleKey.KW_IMAGE_URL);
            callbackData.putString(AudioConstants.BundleKey.KW_IMAGE_URL, url);
            callbackData.putInt(AudioConstants.BundleKey.AUDIO_RESPONSE_CODE, code);
            clientCallback.setData(callbackData);
            clientCallback.callback();
        }
    }

}

package com.xiaoma.music.export.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.google.gson.reflect.TypeToken;
import com.xiaoma.model.XMResult;
import com.xiaoma.music.OnlineMusicFactory;
import com.xiaoma.music.R;
import com.xiaoma.music.common.manager.RequestManager;
import com.xiaoma.music.export.manager.AudioShareManager;
import com.xiaoma.music.export.model.SearchInfo;
import com.xiaoma.music.kuwo.listener.OnAudioFetchListener;
import com.xiaoma.music.kuwo.listener.PlayAfterSuccessFetchListener;
import com.xiaoma.music.kuwo.model.XMMusic;
import com.xiaoma.network.callback.StringCallback;
import com.xiaoma.network.model.Response;
import com.xiaoma.utils.GsonHelper;
import com.xiaoma.utils.ListUtils;
import com.xiaoma.utils.NetworkUtils;
import com.xiaoma.utils.log.KLog;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ZYao.
 * Date ：2019/4/18 0018
 */
public class ExportMusicReceiver extends BroadcastReceiver {
    public static final int KUWO_ANTIFATIGUE_TYPE = 3;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null) {
            return;
        }
        if (!NetworkUtils.isConnected(context)) {
            playLocalMusic(context);
            return;
        }
        String action = intent.getAction();
        if (action.equals(AudioShareManager.PLAY_MUSIC_ACTION)) {
            RequestManager.getInstance().searchMusicByKeyWord(KUWO_ANTIFATIGUE_TYPE, null, 0, 30, new StringCallback() {
                @Override
                public void onSuccess(Response<String> response) {
                    KLog.d("MrMine", "onSuccess: " + response.body());
                    XMResult<List<SearchInfo>> result = GsonHelper.fromJson(response.body(), new TypeToken<XMResult<List<SearchInfo>>>() {
                    }.getType());
                    if (result == null || !result.isSuccess()) {
                        playLocalMusic(context);
                        return;
                    }
                    List<SearchInfo> searchInfoList = result.getData();
                    if (ListUtils.isEmpty(searchInfoList)) {
                        playLocalMusic(context);
                        return;
                    }
                    KLog.d("MrMine", "onSuccess: " + searchInfoList.size());
                    List<Long> ids = new ArrayList<>();
                    for (SearchInfo searchInfo : searchInfoList) {
                        ids.add((long) searchInfo.getKwSongId());
                    }
                    OnlineMusicFactory.getKWAudioFetch().fetchMusicByIds(ids, new PlayAfterSuccessFetchListener<List<XMMusic>>() {
                        @Override
                        public void onFetchSuccess(List<XMMusic> musicList) {
                            OnlineMusicFactory.getKWPlayer().play(musicList, 0);
                            AudioShareManager.getInstance().shareKwAudioDataSourceChanged();
                        }

                        @Override
                        public void onFetchFailed(String msg) {
                            playLocalMusic(context);
                        }
                    });
                }
            });
        }
    }

    public void playLocalMusic(Context context) {
        Map<String, String> localMusic = new HashMap<>();
        String[] stringArray = context.getResources().getStringArray(R.array.local_music);
        for (String string : stringArray) {
            localMusic.put(string, getAssetsCacheFile(context, string + ".mp3"));
        }
        OnlineMusicFactory.getKWPlayer().playLocalMusic(localMusic);
    }

    public String getAssetsCacheFile(Context context, String fileName) {
        File cacheFile = new File(context.getCacheDir(), fileName);
        try {
            InputStream inputStream = context.getAssets().open(fileName);
            try {
                FileOutputStream outputStream = new FileOutputStream(cacheFile);
                try {
                    byte[] buf = new byte[1024];
                    int len;
                    while ((len = inputStream.read(buf)) > 0) {
                        outputStream.write(buf, 0, len);
                    }
                } finally {
                    outputStream.close();
                }
            } finally {
                inputStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return cacheFile.getAbsolutePath();
    }

}

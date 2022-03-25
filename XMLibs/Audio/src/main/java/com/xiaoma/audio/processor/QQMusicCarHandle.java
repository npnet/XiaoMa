package com.xiaoma.audio.processor;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import com.xiaoma.audio.listener.IMusicHandle;
import com.xiaoma.audio.listener.IMusicSearchListener;
import com.xiaoma.audio.listener.IMusicStatusListener;
import com.xiaoma.audio.model.QQMusicData;
import com.xiaoma.audio.model.MusicPlayMode;
import com.xiaoma.utils.GsonHelper;
import java.net.URLEncoder;

class QQMusicCarHandle implements IMusicHandle<IMusicStatusListener<QQMusicData>, IMusicSearchListener<QQMusicData>> {

    private static final String INTENT_NAME = "com.tencent.qqmusiccar.EXTRA_COMMAND_DATA";
    private static final String INTENT_FILTER_NAME = "com.tencent.qqmusiccar.action.PLAY_COMMAND_SEND_FOR_THIRD";
    private Context context;
    private IMusicStatusListener<QQMusicData> mMusicStatusListener;
    private final BroadcastReceiver qqMusicReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String data = intent.getStringExtra(INTENT_NAME);
            QQMusicData qqMusicData = GsonHelper.fromJson(data, QQMusicData.class);
            if (qqMusicData != null && qqMusicData.isPlayModule()) {
                handlePlayModule(qqMusicData);
            }
        }

        private void handlePlayModule(QQMusicData qqMusicData) {
            if (qqMusicData.command != null && qqMusicData.command.isUpdateStatus()) {
                handleUpdateStatus(qqMusicData);
            }
        }

        private void handleUpdateStatus(QQMusicData qqMusicData) {
            if (mMusicStatusListener == null) {
                return;
            }
            if (qqMusicData.command.isBuffering()) {
                mMusicStatusListener.onBuffering(qqMusicData);
            } else if (qqMusicData.command.isPlaying()) {
                mMusicStatusListener.onPlaying(qqMusicData);
            } else if (qqMusicData.command.isPause()) {
                mMusicStatusListener.onPause(qqMusicData);
            } else if (qqMusicData.command.isStop()) {
                mMusicStatusListener.onStop(qqMusicData);
            }
        }
    };

    QQMusicCarHandle(Context context) {
        this.context = context;
    }

    @Override
    public void init() {
        context.registerReceiver(qqMusicReceiver, new IntentFilter(INTENT_FILTER_NAME));
    }

    @Override
    public void startApp() {
        if (mMusicStatusListener != null) {
            mMusicStatusListener.onInit();
        }
        Intent intent = new Intent();
        String uri = "qqmusiccar://?action=0&pull_from=12121";
        intent.setData(Uri.parse(uri));
        context.sendBroadcast(intent);
    }

    @Override
    public void exitApp() {
        Intent intent = new Intent();
        String uri = "qqmusiccar://?action=1&pull_from=12121";
        intent.setData(Uri.parse(uri));
        context.sendBroadcast(intent);
    }

    @Override
    public void startWithKeyword(String keyword) {
        Intent intent = new Intent();
        String uri = "qqmusiccar://?action=8&pull_from=12121&mb=true";
        uri = uri + "&search_key=" + getEncodedKeyword(keyword);
        intent.setData(Uri.parse(uri));
        context.sendBroadcast(intent);
    }

    @Override
    public void setPlayMode(MusicPlayMode musicPlayMode) {
        //...
    }

    @Override
    public void playNext() {
        Intent intent = new Intent();
        String uri = "qqmusiccar://?action=20&pull_from=12121&m0=3";
        intent.setData(Uri.parse(uri));
        context.sendBroadcast(intent);
    }

    @Override
    public void playPre() {
        Intent intent = new Intent();
        String uri = "qqmusiccar://?action=20&pull_from=12121&m0=2";
        intent.setData(Uri.parse(uri));
        context.sendBroadcast(intent);
    }

    @Override
    public void pause() {
        Intent intent = new Intent();
        String uri = "qqmusiccar://?action=20&pull_from=12121&m0=1";
        intent.setData(Uri.parse(uri));
        context.sendBroadcast(intent);
    }

    @Override
    public void play() {
        Intent intent = new Intent();
        String uri = "qqmusiccar://?action=20&pull_from=12121&m0=0";
        intent.setData(Uri.parse(uri));
        context.sendBroadcast(intent);
    }

    @Override
    public void release() {
        removeListener(null);
        context.unregisterReceiver(qqMusicReceiver);
    }

    @Override
    public void setPlayModeRandom() {
        //...
    }

    @Override
    public void subscribeProgram() {
        //...
    }

    @Override
    public void unSubscribeProgram() {
        //...
    }

    @Override
    public void registerListener(IMusicStatusListener<QQMusicData> listener) {
        if (mMusicStatusListener != null) {
            mMusicStatusListener = listener;
        }
    }

    @Override
    public void removeListener(IMusicStatusListener<QQMusicData> listener) {
        mMusicStatusListener = null;
    }

    @Override
    public void searchMusicInfo(String singer, String song, String album, IMusicSearchListener<QQMusicData> listener) {
        //...
    }

    private String getEncodedKeyword(String keyword) {
        try {
            return URLEncoder.encode(keyword, "utf-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return keyword;
    }


}

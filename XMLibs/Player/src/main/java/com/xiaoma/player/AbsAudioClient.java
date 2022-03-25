package com.xiaoma.player;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.xiaoma.center.logic.local.Center;
import com.xiaoma.center.logic.remote.Client;
import com.xiaoma.center.logic.remote.ClientCallback;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kaka
 * on 19-1-22 上午10:43
 * <p>
 * desc: #a
 * </p>
 */
public abstract class AbsAudioClient extends Client {
    private static final String TAG = AbsAudioClient.class.getSimpleName() + "_LOG";

    protected Context context;
    private List<ClientCallback> mConnectCallbackLists;

    public AbsAudioClient(Context context, @AudioConstants.AudioType int audioType) {
        super(context, audioType);
        this.context = context;
        mConnectCallbackLists = new ArrayList<>();
    }

    @Override
    protected void onReceive(int action, Bundle data) {
        switch (action) {
            case AudioConstants.Action.Option.PAUSE:
                onPause();
                break;
            case AudioConstants.Action.Option.PLAY:
                onPlay(data, null);
                break;
            case AudioConstants.Action.Option.NEXT:
                onNext(null);
                break;
            case AudioConstants.Action.Option.PREVIOUS:
                onPrevious(null);
                break;
            case AudioConstants.Action.Option.FAVORITE:
                onFavorite();
                break;
            case AudioConstants.Action.Option.SWITCH_PLAY_MODE:
                onPlayModeChange();
                break;
        }
    }

    @Override
    protected void onRequest(int action, Bundle data, ClientCallback callback) {
        if (action == AudioConstants.Action.SEARCH) {
            onSearchRequest(data, callback);
        } else if (action == AudioConstants.Action.Option.NEXT) {
            onNext(callback);
        } else if (action == AudioConstants.Action.Option.PREVIOUS) {
            onPrevious(callback);
        } else if (action == AudioConstants.Action.PLAY) {
            onPlay(data, callback);
        } else if (action == AudioConstants.Action.Option.PAUSE) {
            onPause(callback);
        } else if (action == AudioConstants.Action.Option.PLAYER_STATUS) {
            isPlaying(callback);
        } else if (action == AudioConstants.Action.Option.SEEK) {
            seek(data, callback);
        } else if (action == AudioConstants.Action.Option.SWITCH_PLAY_MODE) {
            swithPlayMode(data, callback);
        } else if (action == AudioConstants.Action.Option.CURRENT_PLAY_MODE) {
            getCurrentPlayMode(callback);
        } else {
            onOtherRequest(action, data, callback);
        }
    }

    @Override
    protected void onConnect(int action, Bundle data, ClientCallback callback) {
        Log.d("AbsAudioClient", "onConnect,action=" + action);
        if (action == AudioConstants.Action.PLAYER_CONNECT) {
            Log.d("AbsAudioClient", "mConnectCallbackLists,contains=" + mConnectCallbackLists.contains(callback));
            if (!mConnectCallbackLists.contains(callback)) {
                Log.d("AbsAudioClient", "mConnectCallbackLists,add callback");
                mConnectCallbackLists.add(callback);
            }
            onConnect();
        }
    }

    protected void onPlayModeChange() {
    }

    protected abstract void onFavorite();

    protected abstract void onPrevious(ClientCallback callback);

    protected abstract void onNext(ClientCallback callback);

    protected abstract void onPlay(Bundle data, ClientCallback callback);

    protected abstract void onPause();

    protected abstract void onSearchRequest(Bundle bundle, ClientCallback callback);

    protected abstract void onConnect();

    protected void isPlaying(ClientCallback callback) {

    }

    protected void onPause(ClientCallback callback) {

    }

    protected void swithPlayMode(Bundle data, ClientCallback callback) {

    }

    protected void getCurrentPlayMode(ClientCallback callback) {

    }

    /**
     * 设置 / 获取 到指定秒
     *
     * @param data
     * @param callback
     */
    protected void seek(Bundle data, ClientCallback callback) {

    }

    /**
     * 音频应用共享音频信息到Launcher
     *
     * @param audioInfo 音频信息
     */
    public boolean shareAudioInfo(AudioInfo audioInfo) {
        Log.d(TAG, "[AudioApp] shareAudioInfo "
                + "\n * audioApp: " + this.getClass().getName()
                + "\n * connectCallbackListSize: " + mConnectCallbackLists.size()
                + "\n * audio info: " + audioInfo);
        if (mConnectCallbackLists.size() > 0) {
            Bundle callbackData = new Bundle();
            callbackData.putString(
                    AudioConstants.BundleKey.ACTION,
                    AudioConstants.BundleKey.AUDIO_INFO);
            callbackData.putParcelable(
                    AudioConstants.BundleKey.AUDIO_INFO,
                    audioInfo);
            dispatchWithConnect(callbackData);
            return true;
        }
        return false;
    }

    /**
     * 音频应用共享音频状态到Launcher
     *
     * @param audioState 音频状态
     */
    public void shareAudioState(@AudioConstants.AudioState int audioState, @AudioConstants.AudioType int audioType) {
        Log.d(TAG, "[AudioApp] shareAudioState "
                + "\n * audioApp: " + this.getClass().getName()
                + "\n * connectCallbackListSize: " + mConnectCallbackLists.size()
                + "\n * audio info: " + audioState);
        if (mConnectCallbackLists.size() > 0) {
            Bundle callbackData = new Bundle();
            callbackData.putString(
                    AudioConstants.BundleKey.ACTION,
                    AudioConstants.BundleKey.AUDIO_STATE);
            callbackData.putInt(
                    AudioConstants.BundleKey.AUDIO_STATE,
                    audioState);
            callbackData.putInt(
                    AudioConstants.BundleKey.AUDIO_TYPE,
                    audioType);
            dispatchWithConnect(callbackData);
        }
    }

    public void shareInitMusicInfo() {
        Log.d(TAG, "[AudioApp] shareInitMusicInfo "
                + "\n * audioApp: " + this.getClass().getName()
                + "\n * connectCallbackListSize: " + mConnectCallbackLists.size());
    }

    /**
     * 共享usb,蓝牙连接状态
     *
     * @param connectState
     */
    public void shareConnectState(@AudioConstants.ConnectState int connectState) {
        Log.d(TAG, "[AudioApp] shareConnectState "
                + "\n * audioApp: " + this.getClass().getName()
                + "\n * connectCallbackListSize: " + mConnectCallbackLists.size());
        if (mConnectCallbackLists.size() > 0) {
            Bundle callbackData = new Bundle();
            callbackData.putString(
                    AudioConstants.BundleKey.ACTION,
                    AudioConstants.BundleKey.CONNECT_STATE);
            callbackData.putInt(
                    AudioConstants.BundleKey.CONNECT_STATE,
                    connectState);
            dispatchWithConnect(callbackData);
        }
    }

    /**
     * 共享音频进度到服务中心
     *
     * @param progressInfo 音频进度
     */
    public void shareAudioProgress(ProgressInfo progressInfo) {
        Log.d(TAG, "[AudioApp] shareAudioProgress "
                + "\n * audioApp: " + this.getClass().getName()
                + "\n * connectCallbackListSize: " + mConnectCallbackLists.size()
                + "\n * progressInfo: " + progressInfo);
        if (mConnectCallbackLists.size() > 0) {
            Bundle callbackData = new Bundle();
            callbackData.setClassLoader(ProgressInfo.class.getClassLoader());
            callbackData.putString(
                    AudioConstants.BundleKey.ACTION,
                    AudioConstants.BundleKey.AUDIO_PROGRESS);
            callbackData.putParcelable(
                    AudioConstants.BundleKey.AUDIO_PROGRESS,
                    progressInfo);
            dispatchWithConnect(callbackData);
        }
    }

    /**
     * 共享音频是否为已收藏状态到服务中心
     *
     * @param isFavorite 是否已收藏
     */
    public void shareAudioFavorite(boolean isFavorite) {
        Log.d(TAG, "[AudioApp] shareAudioFavorite "
                + "\n * audioApp: " + this.getClass().getName()
                + "\n * connectCallbackListSize: " + mConnectCallbackLists.size()
                + "\n * isFavorite: " + isFavorite);
        if (mConnectCallbackLists.size() > 0) {
            Bundle callbackData = new Bundle();
            callbackData.putString(
                    AudioConstants.BundleKey.ACTION,
                    AudioConstants.BundleKey.AUDIO_FAVORITE);
            callbackData.putBoolean(
                    AudioConstants.BundleKey.AUDIO_FAVORITE,
                    isFavorite);
            dispatchWithConnect(callbackData);
        }
    }

    public void shareAudioMode(@AudioConstants.AudioPlayMode int playMode) {
        Log.d(TAG, "[AudioApp] shareAudioMode "
                + "\n * audioApp: " + this.getClass().getName()
                + "\n * connectCallbackListSize: " + mConnectCallbackLists.size()
                + "\n * playMode: " + playMode);
        if (mConnectCallbackLists.size() > 0) {
            Bundle callbackData = new Bundle();
            callbackData.putString(
                    AudioConstants.BundleKey.ACTION,
                    AudioConstants.BundleKey.AUDIO_PLAYMODE);
            callbackData.putInt(
                    AudioConstants.BundleKey.AUDIO_PLAYMODE,
                    playMode);
            dispatchWithConnect(callbackData);
        }
    }

    public void shareAudioDataSourceChanged(@AudioConstants.OnlineInfoSource int dataSource) {
        Log.d(TAG, "[AudioApp] shareAudioDataSourceChanged "
                + "\n * audioApp: " + this.getClass().getName()
                + "\n * connectCallbackListSize: " + mConnectCallbackLists.size()
                + "\n * dataSource: " + dataSource);
        if (mConnectCallbackLists.size() > 0) {
            Bundle callbackData = new Bundle();
            callbackData.putString(
                    AudioConstants.BundleKey.ACTION,
                    AudioConstants.BundleKey.AUDIO_DATA_SOURCE);
            callbackData.putInt(
                    AudioConstants.BundleKey.AUDIO_DATA_SOURCE,
                    dataSource);
            dispatchWithConnect(callbackData);
        }
    }

    public void shareAudioList(@AudioConstants.AudioResponseCode int code, ClientCallback callback) {
        shareAudioList(
                code,
                null,
                null,
                -1,
                callback);
    }

    public void shareHaveHistory(boolean haveHistory, ClientCallback callback) {
        if (callback != null) {
            Bundle callbackData = new Bundle();
            callbackData.putString(
                    AudioConstants.BundleKey.ACTION,
                    AudioConstants.BundleKey.HAVE_HISTORY);
            callbackData.putBoolean(AudioConstants.BundleKey.HAVE_HISTORY, haveHistory);
            callback.setData(callbackData);
            callback.callback();
        }
    }

    public void shareAudioList(int code, ArrayList<AudioInfo> audioInfoList, int[] pageInfo, int playIndex, ClientCallback callback) {
        Log.d(TAG, "[AudioApp] shareAudioList "
                + "\n * audioApp: " + this.getClass().getName()
                + "\n * connectCallbackListSize: " + mConnectCallbackLists.size()
                + "\n * code: " + code
                + "\n * audioInfoList: " + audioInfoList
                + "\n * pageInfo: " + pageInfo
                + "\n * playIndex: " + playIndex
                + "\n * callback: " + callback);

        if (callback != null) {
            Bundle callbackData = new Bundle();
            callbackData.putString(
                    AudioConstants.BundleKey.ACTION,
                    AudioConstants.BundleKey.AUDIO_LIST);
            callbackData.putInt(
                    AudioConstants.BundleKey.AUDIO_RESPONSE_CODE,
                    code);
            callbackData.putInt(
                    AudioConstants.BundleKey.AUDIO_PLAYING_INDEX,
                    playIndex);
            if (audioInfoList != null && !audioInfoList.isEmpty()) {
                callbackData.putParcelableArrayList(
                        AudioConstants.BundleKey.AUDIO_LIST,
                        audioInfoList);
            }
            if (pageInfo != null) {
                callbackData.putIntArray(
                        AudioConstants.BundleKey.PAGE_INFO,
                        pageInfo);
            }
            callback.setData(callbackData);
            callback.callback();
        }
    }

    public void shareAudioControlCode(@AudioConstants.AudioControlCode int code, ClientCallback clientCallback) {
        Log.d(TAG, "[AudioApp] shareAudioControlCode "
                + "\n * audioApp: " + this.getClass().getName()
                + "\n * connectCallbackListSize: " + mConnectCallbackLists.size()
                + "\n * code: " + code
                + "\n * clientCallback: " + clientCallback);
        if (clientCallback != null) {
            Bundle callbackData = new Bundle();
            callbackData.putString(
                    AudioConstants.BundleKey.ACTION,
                    AudioConstants.BundleKey.AUDIO_CONTROL_CODE);
            callbackData.putInt(
                    AudioConstants.BundleKey.AUDIO_CONTROL_CODE,
                    code);
            clientCallback.setData(callbackData);
            clientCallback.callback();
        }
    }

    public void shareAudioTypeToLauncher(@AudioConstants.AudioType int audioType) {
        Log.d(TAG, "[AudioApp] shareAudioTypeToLauncher "
                + "\n * audioApp: " + this.getClass().getName()
                + "\n * connectCallbackListSize: " + mConnectCallbackLists.size()
                + "\n * audioType: " + audioType);
        if (mConnectCallbackLists.size() > 0) {
            Bundle callbackData = new Bundle();
            callbackData.putString(
                    AudioConstants.BundleKey.ACTION,
                    AudioConstants.BundleKey.AUDIO_TYPE);
            callbackData.putInt(
                    AudioConstants.BundleKey.AUDIO_TYPE,
                    audioType);
            dispatchWithConnect(callbackData);
        }
    }


    public void shareAudioResult(boolean result, ClientCallback clientCallback) {
        if (clientCallback != null) {
            Bundle callbackData = new Bundle();
            callbackData.putString(AudioConstants.BundleKey.ACTION, AudioConstants.BundleKey.RESULT);
            callbackData.putBoolean(AudioConstants.BundleKey.RESULT, result);
            if (result) {
                callbackData.putInt(
                        AudioConstants.BundleKey.AUDIO_RESPONSE_CODE,
                        AudioConstants.AudioResponseCode.SUCCESS);
            } else {
                callbackData.putInt(
                        AudioConstants.BundleKey.AUDIO_RESPONSE_CODE,
                        AudioConstants.AudioResponseCode.ERROR);
            }
            clientCallback.setData(callbackData);
            clientCallback.callback();
        }
    }

    private void dispatchWithConnect(Bundle bundle) {
        for (ClientCallback clientCallback : mConnectCallbackLists) {
            clientCallback.setData(bundle);
            clientCallback.callback();
        }
    }

    protected void onOtherRequest(int action, Bundle data, ClientCallback callback) {
        // empty
    }

    public void registerToServerCenter() {
        if (!Center.getInstance().isConnected()) {
            Center.getInstance().init(context);
        }
        Center.getInstance().runAfterConnected(new Runnable() {
            @Override
            public void run() {
                Center.getInstance().register(AbsAudioClient.this);
            }
        });
    }

}

package com.xiaoma.dualscreen.manager;

import android.content.Context;
import android.os.Bundle;

import com.xiaoma.autotracker.XmTracker;
import com.xiaoma.autotracker.model.TrackerCountType;
import com.xiaoma.center.IClientCallback;
import com.xiaoma.center.logic.CenterConstants;
import com.xiaoma.center.logic.local.Linker;
import com.xiaoma.center.logic.model.Request;
import com.xiaoma.center.logic.model.RequestHead;
import com.xiaoma.center.logic.model.Response;
import com.xiaoma.center.logic.model.SourceInfo;
import com.xiaoma.dualscreen.R;
import com.xiaoma.dualscreen.listener.DualScreenConnectListener;
import com.xiaoma.dualscreen.listener.DuralScreenPlayListener;
import com.xiaoma.dualscreen.listener.IBTConnectState;
import com.xiaoma.dualscreen.listener.PhoneListener;
import com.xiaoma.player.AudioCategoryBean;
import com.xiaoma.player.AudioConstants;
import com.xiaoma.player.AudioInfo;
import com.xiaoma.player.ProgressInfo;
import com.xiaoma.ui.toast.XMToast;
import com.xiaoma.utils.log.KLog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lailai.
 * Date: 2019/1/25
 */
public class PlayerConnectHelper {

    private static PlayerConnectHelper instance;
    private List<DuralScreenPlayListener> mPlayListener = new ArrayList<>();
    private List<DualScreenConnectListener> mConnectListener = new ArrayList<>();
    private SourceInfo mLocalSourceInfo;
    private Context mContext;
    //当前播放状态
    private int mPlayState;
    public static boolean isRecognize = false;
    public boolean isRecommand = false;
    private PhoneListener phoneListener;
    //音频历史数据
    private Map<Integer, AudioInfo> historyAudioMap = new HashMap<>();

    public static PlayerConnectHelper getInstance() {
        if (instance == null) {
            synchronized (PlayerConnectHelper.class) {
                if (instance == null) {
                    instance = new PlayerConnectHelper();
                }
            }
        }

        return instance;
    }

    public void playerConnect(Context context, final SourceInfo source) {
        KLog.e("playerConnect,source=" + source.getPort());
        this.mContext = context;
        mLocalSourceInfo = new SourceInfo(context.getPackageName(), CenterConstants.DUALSCREEN_PORT);
        Request connectRequest = new Request(mLocalSourceInfo, new RequestHead(source, AudioConstants.Action.PLAYER_CONNECT), null);
        int connectFlag = Linker.getInstance().connect(connectRequest, new IClientCallback.Stub() {
            @Override
            public void callback(Response response) {
                KLog.e("playerConnect callback,source=" + source.getPort());
                Bundle extra = response.getExtra();
                extra.setClassLoader(ProgressInfo.class.getClassLoader());
                String action = extra.getString(AudioConstants.BundleKey.ACTION);
                if (AudioConstants.BundleKey.AUDIO_PROGRESS.equals(action)) {
                    ProgressInfo progressInfo = extra.getParcelable(action);
                    if (progressInfo != null) {
                        if (!isPlayingState()) {
                            mPlayState = AudioConstants.AudioStatus.PLAYING;
                            for (DuralScreenPlayListener launcherPlayListener : mPlayListener) {
                                launcherPlayListener.onPlayState(mPlayState);
                            }
                        }
                        for (DuralScreenPlayListener launcherPlayListener : mPlayListener) {
                            KLog.d("callback: " + progressInfo.toString());
                            launcherPlayListener.onProgress(progressInfo);
                        }
                    }
                    // 进度大于95%代表基本听完这首歌，上报
                    if (progressInfo.getPercent() >= 0.95) {
                        if (isRecognize) {
                            XmTracker.getInstance().uploadEvent(-1, TrackerCountType.MUSICRECOGNIZE.getType());
                            isRecognize = false;
                        }
                        if (isRecommand) {
                            XmTracker.getInstance().uploadEvent(-1, TrackerCountType.RECOMMANDMUSIC.getType());
                            isRecommand = false;
                        }
                    }

                } else if (AudioConstants.BundleKey.AUDIO_STATE.equals(action)) {
                    int mCurrentPlayState = extra.getInt(action);
                    mPlayState = mCurrentPlayState;
                    KLog.d("callback: state:" + mCurrentPlayState);
                    for (DuralScreenPlayListener launcherPlayListener : mPlayListener) {
                        launcherPlayListener.onPlayState(mCurrentPlayState);
                    }

                } else if (AudioConstants.BundleKey.AUDIO_INFO.equals(action)) {
                    AudioInfo mAudioInfo = extra.getParcelable(AudioConstants.BundleKey.AUDIO_INFO);
                    if (mAudioInfo != null) {
                        if (mAudioInfo.isHistory()) {
                            historyAudioMap.put(mAudioInfo.getAudioType(), mAudioInfo);
                        }
                        KLog.d("callback: " + mAudioInfo.toString());
                        for (DuralScreenPlayListener launcherPlayListener : mPlayListener) {
                            launcherPlayListener.onAudioInfo(mAudioInfo);
                        }
                    }

                } else if (AudioConstants.BundleKey.AUDIO_FAVORITE.equals(action)) {
                    boolean audioFavorite = extra.getBoolean(action);
                    KLog.d("callback: favorite: " + audioFavorite);
                    for (DuralScreenPlayListener launcherPlayListener : mPlayListener) {
                        launcherPlayListener.onAudioFavorite(audioFavorite);
                    }

                } else if (AudioConstants.BundleKey.AUDIO_PLAYMODE.equals(action)) {
                    int mCurrentPlayMode = extra.getInt(action);
                    KLog.d("callback: playMode: " + mCurrentPlayMode);

                } else if (AudioConstants.BundleKey.AUDIO_DATA_SOURCE.equals(action)) {
                    int dataSource = extra.getInt(action);
                    KLog.d("callback: dataSource: " + dataSource);
                    for (DuralScreenPlayListener launcherPlayListener : mPlayListener) {
                        launcherPlayListener.onDataSource(dataSource);
                    }

                } else if (AudioConstants.BundleKey.CONNECT_STATE.equals(action)) {
                    //连接状态
                    int connectState = extra.getInt(action);
                    KLog.d("callback: connectState: " + connectState);
                    for (DualScreenConnectListener connectListener : mConnectListener) {
                        connectListener.connectState(connectState);
                    }

                } else {
                    KLog.d("callback: Error type : " + action);
                }
            }
        });
        KLog.e("Linker playerConnect connectFlag:" + connectFlag + ",source=" + source.getPort());
    }

    public void setPlayListener(DuralScreenPlayListener listener) {
        if (!mPlayListener.contains(listener)) {
            mPlayListener.add(listener);
        }
    }

    public void removePlayListener(DuralScreenPlayListener listener){
        if(listener != null){
            mPlayListener.remove(listener);
        }
    }

    public void setConnectListener(DualScreenConnectListener listener) {
        if (!mConnectListener.contains(listener)) {
            mConnectListener.add(listener);
        }
    }

    public void removeConnectListener(DualScreenConnectListener listener){
        if(listener != null){
            mConnectListener.remove(listener);
        }
    }

    public void setPhoneListener(PhoneListener phoneListener) {
        this.phoneListener = phoneListener;
    }

    /**
     * 上下曲切换音频
     *
     * @param option
     * @param audioType
     */
    public void preNextAudio(int option, int audioType) {
        if (phoneListener != null && phoneListener.getPhoneState() != 0) {
            return;
        }
        isRecognize = false;
        Bundle bundle = new Bundle();
        bundle.putInt(AudioConstants.BundleKey.ACTION, option);
        Request request = new Request(mLocalSourceInfo, new RequestHead(getSourceInfoByAudioType(audioType), option), bundle);
        Linker.getInstance().request(request, new IClientCallback.Stub() {
            @Override
            public void callback(Response response) {
                Bundle extra = response.getExtra();
                int code = extra.getInt(AudioConstants.BundleKey.AUDIO_CONTROL_CODE);
                switch (code) {
                    case AudioConstants.AudioControlCode.TOP:
                        XMToast.showToast(mContext, mContext.getString(R.string.audio_first));
                        break;

                    case AudioConstants.AudioControlCode.BOTTOM:
                        XMToast.showToast(mContext, mContext.getString(R.string.audio_last));
                        break;
                }
            }
        });
    }

    /**
     * 暂停音频
     *
     * @param audioType
     */
    public void pauseAudio(int audioType) {
        Request request = new Request(mLocalSourceInfo, new RequestHead(getSourceInfoByAudioType(audioType),
                AudioConstants.Action.Option.PAUSE), null);
        Linker.getInstance().send(request);
    }


    public void playXtingHistory(int audioType, int action) {
        if (phoneListener != null && phoneListener.getPhoneState() != 0) {
            return;
        }
        isRecognize = false;
        Bundle playTypeBundle = new Bundle();
        AudioCategoryBean categoryBean = new AudioCategoryBean();
        categoryBean.setAction(AudioConstants.PlayAction.HISTORY);

        playTypeBundle.putParcelable(AudioConstants.BundleKey.EXTRA, categoryBean);
        playTypeBundle.putInt(AudioConstants.BundleKey.ACTION, action);

        Request request = new Request(mLocalSourceInfo, new RequestHead(getSourceInfoByAudioType(audioType),
                AudioConstants.Action.PLAY), playTypeBundle);
        Linker.getInstance().request(request, new IClientCallback.Stub() {
            @Override
            public void callback(Response response) {
                Bundle extra = response.getExtra();
                int code = extra.getInt(AudioConstants.BundleKey.AUDIO_CONTROL_CODE);
                switch (code) {
                    case AudioConstants.AudioControlCode.TOP:
                        XMToast.showToast(mContext, mContext.getString(R.string.audio_first));
                        break;

                    case AudioConstants.AudioControlCode.BOTTOM:
                        XMToast.showToast(mContext, mContext.getString(R.string.audio_last));
                        break;
                }
            }
        });
    }

    public void playXtingHistoryBySubType(int audioType, int action, int subType) {
        if (phoneListener != null && phoneListener.getPhoneState() != 0) {
            return;
        }
        isRecognize = false;
        Bundle playTypeBundle = new Bundle();
        AudioCategoryBean categoryBean = new AudioCategoryBean();
        categoryBean.setAction(AudioConstants.PlayAction.HISTORY);

        playTypeBundle.putParcelable(AudioConstants.BundleKey.EXTRA, categoryBean);
        playTypeBundle.putInt(AudioConstants.BundleKey.ACTION, action);
        playTypeBundle.putInt(AudioConstants.BundleKey.XiangTingType, subType);

        Request request = new Request(mLocalSourceInfo, new RequestHead(getSourceInfoByAudioType(audioType),
                AudioConstants.Action.PLAY), playTypeBundle);
        Linker.getInstance().request(request, new IClientCallback.Stub() {
            @Override
            public void callback(Response response) {
                Bundle extra = response.getExtra();
                int code = extra.getInt(AudioConstants.BundleKey.AUDIO_CONTROL_CODE);
                switch (code) {
                    case AudioConstants.AudioControlCode.TOP:
                        XMToast.showToast(mContext, mContext.getString(R.string.audio_first));
                        break;

                    case AudioConstants.AudioControlCode.BOTTOM:
                        XMToast.showToast(mContext, mContext.getString(R.string.audio_last));
                        break;
                }
            }
        });
    }


    public void playAudioBySubType(int audioType, int action, String subAction, int subType) {
        playAudio(audioType, action, 0, 0, subAction, subType);
    }


    public void playAudio(int audioType, int action, int categoryId, int index, String subAction, int subType) {
        if (phoneListener != null && phoneListener.getPhoneState() != 0) {
            return;
        }
        Bundle playTypeBundle = new Bundle();
        AudioCategoryBean categoryBean = new AudioCategoryBean();
        categoryBean.setAction(action);
        categoryBean.setCategoryId(categoryId);
        categoryBean.setIndex(index);
        playTypeBundle.putParcelable(AudioConstants.BundleKey.EXTRA, categoryBean);
        playTypeBundle.putInt(subAction, subType);
        Request request = new Request(mLocalSourceInfo, new RequestHead(getSourceInfoByAudioType(audioType),
                AudioConstants.Action.Option.PLAY), playTypeBundle);
        Linker.getInstance().send(request);
        isRecognize = false;
    }


    public void fetchBTConnectState(final IBTConnectState callBack) {
        Request searchRequest = new Request(mLocalSourceInfo, new RequestHead(PlayerConnectHelper.getInstance().getSourceInfoByAudioType(AudioConstants.AudioTypes.MUSIC_LOCAL_BT),
                AudioConstants.Action.BT_CONNECT_STATE), null);
        Linker.getInstance().request(searchRequest, new IClientCallback.Stub() {
            @Override
            public void callback(Response response) {
                Bundle extra = response.getExtra();
                extra.setClassLoader(AudioInfo.class.getClassLoader());
                boolean connect = extra.getBoolean(AudioConstants.BundleKey.RESULT, false);
                callBack.isConnect(connect);
            }
        });
    }


    /**
     * 根据音源类型获取SourceInfo
     *
     * @param audioType 音源
     * @return
     */
    public SourceInfo getSourceInfoByAudioType(int audioType) {
        //如果是在线广播类型也认为是在线电台类型
        if (audioType == AudioConstants.AudioTypes.XTING_LOCAL_FM
                || audioType == AudioConstants.AudioTypes.XTING_NET_RADIO
                || audioType == AudioConstants.AudioTypes.XTING_KOALA_ALBUM
                || audioType == AudioConstants.AudioTypes.XTING_LOCAL_AM
                || audioType == AudioConstants.AudioTypes.XTING_NET_FM) {
            audioType = AudioConstants.AudioTypes.XTING;
        }

        return PlayerAudioManager.getInstance().getSourceInfo(audioType);
    }

    /**
     * 当前是否是播放状态
     *
     * @return
     */
    public boolean isPlayingState() {
        return mPlayState == AudioConstants.AudioStatus.PLAYING;
    }

}

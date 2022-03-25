package com.xiaoma.launcher.player.manager;

import android.content.Context;
import android.os.Bundle;
import android.os.RemoteException;

import com.xiaoma.autotracker.XmTracker;
import com.xiaoma.autotracker.model.TrackerCountType;
import com.xiaoma.center.IClientCallback;
import com.xiaoma.center.logic.ErrorCode;
import com.xiaoma.center.logic.local.Linker;
import com.xiaoma.center.logic.model.Request;
import com.xiaoma.center.logic.model.RequestHead;
import com.xiaoma.center.logic.model.Response;
import com.xiaoma.center.logic.model.SourceInfo;
import com.xiaoma.component.AppHolder;
import com.xiaoma.guide.utils.GuideConstants;
import com.xiaoma.guide.utils.GuideDataHelper;
import com.xiaoma.guide.utils.NewGuide;
import com.xiaoma.launcher.R;
import com.xiaoma.launcher.common.constant.LauncherConstants;
import com.xiaoma.launcher.player.callback.ImageResultCallBack;
import com.xiaoma.launcher.player.callback.LauncherPlayListener;
import com.xiaoma.launcher.player.ui.AudioMainFragment;
import com.xiaoma.player.AudioCategoryBean;
import com.xiaoma.player.AudioConstants;
import com.xiaoma.player.AudioInfo;
import com.xiaoma.player.PlayerConstants;
import com.xiaoma.player.ProgressInfo;
import com.xiaoma.thread.ThreadDispatcher;
import com.xiaoma.ui.toast.XMToast;
import com.xiaoma.utils.log.KLog;
import com.xiaoma.utils.tputils.TPUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhushi.
 * Date: 2019/1/25
 */
public class PlayerConnectHelper {
    private static final String TAG = "PlayerConnectHelper";
    public static final String LAST_AUDIO_INFO = "last_audio_info";
    private static PlayerConnectHelper instance;
    private List<LauncherPlayListener> mPlayListener = new ArrayList<>();
    private SourceInfo mLocalSourceInfo;
    private Context mContext;
    //当前播放状态
    private int mPlayState;
    public static boolean isRecognize = false;
    public boolean isRecommand = false;

    private AudioInfo mAudioInfo;
    private ProgressInfo mProgressInfo;
    private int mAudioType = AudioConstants.AudioTypes.NONE;
    private boolean firstProgressInfo = true;

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

    public int playerConnect(Context context, final SourceInfo source) {
        this.mContext = context;
        mLocalSourceInfo = new SourceInfo(context.getPackageName(), LauncherConstants.LAUNCHER_PORT);
        Request connectRequest = new Request(mLocalSourceInfo, new RequestHead(source, AudioConstants.Action.PLAYER_CONNECT), null);
        int connectCode = Linker.getInstance().connect(connectRequest, clientCallback);
        KLog.d("connect code :" + connectCode + " ,connectRequest:" + connectRequest);
        if (connectCode == ErrorCode.CODE_SUCCESS
                && source.getPort() == AudioConstants.AudioTypes.MUSIC_LOCAL_BT) {
            ThreadDispatcher.getDispatcher().postDelayed(new Runnable() {
                @Override
                public void run() {
                    fetchBTConnectState();
                }
            }, 2000);
            if (onAudioTypeChangedListener != null) {
                onAudioTypeChangedListener.onClientConnect();
            }
        }
        return connectCode;
    }

    private IClientCallback.Stub clientCallback = new IClientCallback.Stub() {
        @Override
        public void callback(Response response) {
            Bundle extra = response.getExtra();
            extra.setClassLoader(ProgressInfo.class.getClassLoader());
            String action = extra.getString(AudioConstants.BundleKey.ACTION);
            if (AudioConstants.BundleKey.AUDIO_PROGRESS.equals(action)) {
                mProgressInfo = extra.getParcelable(action);
                if (mProgressInfo != null) {
                    if (mAudioType != mProgressInfo.getAudioType()) {
                        KLog.d("XM_LOG_" + "progress info: " + "diff type");
                        return;
                    }
                    for (LauncherPlayListener launcherPlayListener : mPlayListener) {
                        KLog.d("callback: progressInfo:" + mProgressInfo.toString());
                        launcherPlayListener.onProgress(mProgressInfo);
                    }
                    if (firstProgressInfo) {
                        //通知状态为播放中
                        for (LauncherPlayListener launcherPlayListener : mPlayListener) {
                            launcherPlayListener.onPlayState(AudioConstants.AudioStatus.PLAYING);
                        }
                        firstProgressInfo = false;
                    }
                    // 进度大于95%代表基本听完这首歌，上报
                    if (mProgressInfo.getPercent() >= 0.95) {
                        if (isRecognize) {
                            XmTracker.getInstance().uploadEvent(-1, TrackerCountType.MUSICRECOGNIZE.getType());
                            isRecognize = false;
                        }
                        if (isRecommand) {
                            XmTracker.getInstance().uploadEvent(-1, TrackerCountType.RECOMMANDMUSIC.getType());
                            isRecommand = false;
                        }
                    }
                }

            } else if (AudioConstants.BundleKey.AUDIO_STATE.equals(action)) {
                int mCurrentPlayState = extra.getInt(action);
                int audioType = extra.getInt(AudioConstants.BundleKey.AUDIO_TYPE);
                int currentDataSource = getCurrentDataSource();
                KLog.d("callback: playState:" + mCurrentPlayState + "  ,audioType: " + audioType + " ,currentDataSource: " + currentDataSource);
                if (currentDataSource != audioType) {
                    return;
                }
                mPlayState = mCurrentPlayState;
                //关闭本地FM时
                if (mPlayState == AudioConstants.AudioStatus.EXIT) {
                    //清空音频数据
                    TPUtils.putObject(mContext, AudioMainFragment.LAST_AUDIO_INFO, null);
                }
                KLog.d("callback: playState:" + mCurrentPlayState);
                for (LauncherPlayListener launcherPlayListener : mPlayListener) {
                    launcherPlayListener.onPlayState(mCurrentPlayState);
                }

            } else if (AudioConstants.BundleKey.AUDIO_INFO.equals(action)) {
                mAudioInfo = extra.getParcelable(AudioConstants.BundleKey.AUDIO_INFO);
                if (mAudioInfo != null) {
                    if (mAudioInfo.isHistory()) {
                        return;
                    }
                    KLog.d("XM_LOG_" + "mAudioType: " + mAudioType + "，mAudioInfo.getAudioType(): " + mAudioInfo.getAudioType());
                    if (mAudioType != mAudioInfo.getAudioType()) {
                        return;
                    }
                    firstProgressInfo = true;
                    putLauncherCategoryId(mAudioInfo);
                    TPUtils.putObject(mContext, AudioMainFragment.LAST_AUDIO_INFO, mAudioInfo);
                    for (LauncherPlayListener launcherPlayListener : mPlayListener) {
                        launcherPlayListener.onAudioInfo(mAudioInfo);
                    }
                }

            } else if (AudioConstants.BundleKey.AUDIO_FAVORITE.equals(action)) {
                boolean audioFavorite = extra.getBoolean(action);
                KLog.d("callback: favorite: " + audioFavorite);
                for (LauncherPlayListener launcherPlayListener : mPlayListener) {
                    launcherPlayListener.onAudioFavorite(audioFavorite);
                }

            } else if (AudioConstants.BundleKey.AUDIO_DATA_SOURCE.equals(action)) {
                int dataSource = extra.getInt(action);
                KLog.d("callback: dataSource: " + dataSource);
                //保存最后一次音频来源
                TPUtils.put(mContext, AudioMainFragment.LAST_SOURCE, dataSource);
                for (LauncherPlayListener launcherPlayListener : mPlayListener) {
                    launcherPlayListener.onDataSource(dataSource);
                }
            } else if (AudioConstants.BundleKey.AUDIO_TYPE.equals(action)) {
                mAudioType = extra.getInt(action);
                if (onAudioTypeChangedListener != null) {
                    onAudioTypeChangedListener.onAudioTypeChange(mAudioType);
                }
            } else if (AudioConstants.BundleKey.CONNECT_STATE.equals(action)) {
                int connectState = extra.getInt(action);
                if (connectState == AudioConstants.ConnectStatus.USB_SCAN_FINISH_AUTO) {
                    // TODO: 2019/8/13 0013
                    playUsbHistory();
                }
            }
        }
    };

    public interface OnAudioTypeChangedListener {
        void onAudioTypeChange(int audioType);

        void onClientConnect();
    }

    public OnAudioTypeChangedListener onAudioTypeChangedListener;

    public void setOnAudioTypeChangedListener(OnAudioTypeChangedListener onAudioTypeChangedListener) {
        this.onAudioTypeChangedListener = onAudioTypeChangedListener;
    }

    public void playUsbHistory() {
        mAudioInfo = TPUtils.getObject(AppHolder.getInstance().getAppContext(), LAST_AUDIO_INFO, AudioInfo.class);
        if (mAudioInfo.getAudioType() != AudioConstants.AudioTypes.MUSIC_LOCAL_USB) {
            return;
        }
        if (isInNewGuideMode()) {
            return;
        }
        Request searchRequest = new Request(mLocalSourceInfo, new RequestHead(PlayerConnectHelper.getInstance().getSourceInfoByAudioType(AudioConstants.AudioTypes.MUSIC_LOCAL_USB),
                AudioConstants.Action.LAUNCHER_USB_HISTORY), null);
        Linker.getInstance().request(searchRequest, new IClientCallback.Stub() {
            @Override
            public void callback(Response response) {
                Bundle extra = response.getExtra();
                extra.setClassLoader(AudioInfo.class.getClassLoader());
                boolean connect = extra.getBoolean(AudioConstants.BundleKey.RESULT, false);
                if (!connect) {
//                    XMToast.showToast(mContext,mContext.getString(R.string.usb_audio_not_play));

                }
            }
        });
    }

    private boolean isInNewGuideMode() {
        if (GuideDataHelper.shouldShowGuide(GuideConstants.LAUNCHER_SHOWED, true)) {
            return true;
        }
        if (NewGuide.isGuideShowNow()) {
            return true;
        }
        return false;
    }


    private void putLauncherCategoryId(AudioInfo audioInfo) {
        if ((audioInfo.getAudioType() == AudioConstants.AudioTypes.XTING_LOCAL_FM
                || (audioInfo.getAudioType() == AudioConstants.AudioTypes.XTING_KOALA_ALBUM && audioInfo.getLauncherCategoryId() > 0)
                || audioInfo.getAudioType() == AudioConstants.AudioTypes.MUSIC_LOCAL_BT
                || audioInfo.getAudioType() == AudioConstants.AudioTypes.MUSIC_LOCAL_USB)) {
            int categoryId = -1;
            switch (audioInfo.getAudioType()) {
                case AudioConstants.AudioTypes.XTING_LOCAL_FM:
                    categoryId = AudioMainFragment.LOCAL_FM_ID;
                    break;

                case AudioConstants.AudioTypes.XTING_KOALA_ALBUM:
                    categoryId = AudioMainFragment.BENTEN_FM_ID;
                    break;

                case AudioConstants.AudioTypes.MUSIC_LOCAL_BT:
                    categoryId = AudioMainFragment.BT_ITEM_ID;
                    break;

                case AudioConstants.AudioTypes.MUSIC_LOCAL_USB:
                    categoryId = AudioMainFragment.USB_ITEM_ID;
                    break;
            }
            TPUtils.put(mContext, AudioMainFragment.CATEGORY_ID, categoryId);
        }
    }

    private int getCurrentDataSource() {
        int currentDataSource = 0;
        switch (mAudioType) {
            case AudioConstants.AudioTypes.MUSIC_ONLINE_KUWO:
            case AudioConstants.AudioTypes.MUSIC_LOCAL_USB:
            case AudioConstants.AudioTypes.MUSIC_LOCAL_BT:
            case AudioConstants.AudioTypes.MUSIC_KUWO_RADIO:
                currentDataSource = mAudioType;
                break;
            case AudioConstants.AudioTypes.XTING_NET_RADIO:
            case AudioConstants.AudioTypes.XTING_NET_FM:
            case AudioConstants.AudioTypes.XTING_LOCAL_FM:
            case AudioConstants.AudioTypes.XTING_KOALA_ALBUM:
            case AudioConstants.AudioTypes.XTING_LOCAL_AM:
            case AudioConstants.AudioTypes.XTING:
                currentDataSource = AudioConstants.AudioTypes.XTING;
                break;
        }
        return currentDataSource;
    }

    /**
     * 当播放收藏列表数据时,通知播放器收藏状态为已收藏
     */
    public void notifyFavorite() {
        for (LauncherPlayListener launcherPlayListener : mPlayListener) {
            launcherPlayListener.onAudioFavorite(true);
        }
    }

    public void setPlayListener(LauncherPlayListener listener) {
        if (!mPlayListener.contains(listener)) {
            mPlayListener.add(listener);
        }
    }

    public void removePlayListener(LauncherPlayListener listener) {
        mPlayListener.remove(listener);
    }

    /**
     * 上下曲切换音频
     *
     * @param option
     * @param audioType
     */
    public void preNextAudio(int option, int audioType) {
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

    public void seek(long second, int audioType) {
        Bundle bundle = new Bundle();
        bundle.putLong("seconds", mProgressInfo.getTotal() * second / 100);
        bundle.putInt("type", 1);
        Request request = new Request(mLocalSourceInfo, new RequestHead(getSourceInfoByAudioType(audioType),
                AudioConstants.Action.Option.SEEK), bundle);
        Linker.getInstance().request(request, new IClientCallback.Stub() {
            @Override
            public void callback(Response response) throws RemoteException {

            }
        });
    }

    /**
     * 取消搜台
     */
    public void cancelScan() {
        Request request = new Request(mLocalSourceInfo,
                new RequestHead(getSourceInfoByAudioType(PlayerConstants.AudioTypes.XTING_LOCAL_FM),
                        AudioConstants.Action.CANCEL_SCAN), null);
        Linker.getInstance().request(request, new IClientCallback.Stub() {
            @Override
            public void callback(Response response) throws RemoteException {

            }
        });
    }

    /**
     * 收藏音频
     */
    public void favoriteAudio(int audioType) {
        Request request = new Request(mLocalSourceInfo, new RequestHead(getSourceInfoByAudioType(audioType),
                AudioConstants.Action.Option.FAVORITE), null);
        Linker.getInstance().send(request);
    }

    /**
     * 播放器播放音频
     *
     * @param audioType
     */
    public void playAudio(int audioType, int action) {
        playAudio(audioType, action, 0, 0);
    }

    /**
     * 列表播放指定音频
     *
     * @param audioType
     */
    public void playAudio(int audioType, int action, int categoryId, int index) {
        Bundle playTypeBundle = new Bundle();
        AudioCategoryBean categoryBean = new AudioCategoryBean();
        categoryBean.setAction(action);
        categoryBean.setCategoryId(categoryId);
        categoryBean.setIndex(index);
        playTypeBundle.putParcelable(AudioConstants.BundleKey.EXTRA, categoryBean);
        playTypeBundle.putBoolean(AudioConstants.BundleKey.EXTRA_2, audioType == AudioConstants.AudioTypes.XTING_LOCAL_FM);
        Request request = new Request(mLocalSourceInfo, new RequestHead(getSourceInfoByAudioType(audioType),
                AudioConstants.Action.Option.PLAY), playTypeBundle);
        Linker.getInstance().send(request);
        isRecognize = false;
    }

    /**
     * 桌面听歌识曲播放指定ID音频
     *
     * @param categoryId
     */
    public void playAudio(int categoryId) {
        Bundle playTypeBundle = new Bundle();
        AudioCategoryBean categoryBean = new AudioCategoryBean();
        categoryBean.setAction(AudioConstants.PlayAction.PLAY_REC_MUSIC);
        categoryBean.setCategoryId(categoryId);
        playTypeBundle.putParcelable(AudioConstants.BundleKey.EXTRA, categoryBean);
        Request request = new Request(mLocalSourceInfo, new RequestHead(getSourceInfoByAudioType(AudioConstants.AudioTypes.MUSIC_ONLINE_KUWO),
                AudioConstants.Action.Option.PLAY), playTypeBundle);
        Linker.getInstance().send(request);
    }

    /**
     * 桌面听歌识曲播放指定Name音频
     */
    public void playNameAudio(String singerName, String musicName) {
        Bundle playTypeBundle = new Bundle();
        playTypeBundle.putInt(AudioConstants.BundleKey.SEARCH_ACTION, AudioConstants.SearchAction.SEARCH_MUSIC_BY_NAME);
        playTypeBundle.putString(AudioConstants.BundleKey.SINGER, singerName);
        playTypeBundle.putString(AudioConstants.BundleKey.SONG, musicName);
        Request request = new Request(mLocalSourceInfo, new RequestHead(getSourceInfoByAudioType(AudioConstants.AudioTypes.MUSIC_ONLINE_KUWO),
                AudioConstants.Action.SEARCH), playTypeBundle);
        Linker.getInstance().request(request, new IClientCallback.Stub() {
            @Override
            public void callback(Response response) throws RemoteException {

            }
        });
    }

    /**
     * 列表播放USB音频
     *
     * @param audioType
     */
    public void playUsbAudio(int audioType, int action, String usbPath, int index) {
        Bundle playTypeBundle = new Bundle();
        AudioCategoryBean categoryBean = new AudioCategoryBean();
        categoryBean.setAction(action);
        categoryBean.setUsbPath(usbPath);
        categoryBean.setIndex(index);
        playTypeBundle.putParcelable(AudioConstants.BundleKey.EXTRA, categoryBean);
        Request request = new Request(mLocalSourceInfo, new RequestHead(getSourceInfoByAudioType(audioType),
                AudioConstants.Action.Option.PLAY), playTypeBundle);
        Linker.getInstance().send(request);
    }

    public void fetchKwImage(AudioInfo audioInfo, final ImageResultCallBack callBack) {
        Bundle searchPurpose = new Bundle();
        KLog.d("kw image" + " fetchKwImage");
        searchPurpose.putInt(AudioConstants.BundleKey.SEARCH_ACTION, AudioConstants.SearchAction.FETCH_IMAGE);
        searchPurpose.putLong(AudioConstants.BundleKey.KW_MUSIC_ID, audioInfo.getUniqueId());
        Request searchRequest = new Request(mLocalSourceInfo, new RequestHead(getSourceInfoByAudioType(AudioConstants.AudioTypes.MUSIC_ONLINE_KUWO),
                AudioConstants.Action.SEARCH), searchPurpose);
        Linker.getInstance().request(searchRequest, new IClientCallback.Stub() {
            @Override
            public void callback(Response response) {
                Bundle extra = response.getExtra();
                KLog.d("kw image" + " fetchKwImage  Response");
                extra.setClassLoader(AudioInfo.class.getClassLoader());
                String url = extra.getString(AudioConstants.BundleKey.KW_IMAGE_URL);
                callBack.onFetchImageSuccess(url);
            }
        });
    }

    /**
     * 根据桌面分类id播放
     *
     * @param categoryId
     */
    public void playAudioCategory(int audioType, final int categoryId, final AudioMainFragment.SwitchResultCallBack callBack) {
        Bundle searchPurpose = new Bundle();
        searchPurpose.putInt(AudioConstants.BundleKey.SEARCH_ACTION, AudioConstants.SearchAction.SEARCH_RESULT);
        searchPurpose.putInt(AudioConstants.BundleKey.SEARCH_CATEGORY_ID, categoryId);
        searchPurpose.putBoolean(AudioConstants.BundleKey.EXTRA, categoryId == AudioMainFragment.LOCAL_FM_ID);//是否是本地FM
        searchPurpose.putBoolean(AudioConstants.BundleKey.EXTRA_2, categoryId == AudioMainFragment.BENTEN_FM_ID);//是否是奔腾电台
        Request searchRequest = new Request(mLocalSourceInfo, new RequestHead(getSourceInfoByAudioType(audioType),
                AudioConstants.Action.SEARCH), searchPurpose);
        Linker.getInstance().request(searchRequest, new IClientCallback.Stub() {
            @Override
            public void callback(Response response) {
                Bundle extra = response.getExtra();
                extra.setClassLoader(AudioInfo.class.getClassLoader());
                int responseCode = extra.getInt(AudioConstants.BundleKey.AUDIO_RESPONSE_CODE);
                KLog.d("responseCode:" + responseCode);
                callBack.onSwitchCategoryResult(responseCode, categoryId);
            }
        });
    }

    /**
     * 判断蓝牙状态是否连接
     */
    public void fetchBTConnectState() {
        Request searchRequest = new Request(mLocalSourceInfo, new RequestHead(getSourceInfoByAudioType(
                AudioConstants.AudioTypes.MUSIC_LOCAL_BT),
                AudioConstants.Action.BT_CONNECT_STATE), null);
        Linker.getInstance().request(searchRequest, new IClientCallback.Stub() {
            @Override
            public void callback(Response response) {
                Bundle extra = response.getExtra();
                extra.setClassLoader(AudioInfo.class.getClassLoader());
                boolean connect = extra.getBoolean(AudioConstants.BundleKey.RESULT, false);
                KLog.d("fetchBTConnectState connect:" + connect);
                BluetoothReceiver.BluetoothStateCallback callback = BluetoothReceiver.getBluetoothStateCallback();
                if (connect) {
                    callback.onBTSinkConnected();

                } else {
                    callback.onBTDisconnected();
                }
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
                || audioType == AudioConstants.AudioTypes.XTING_NET_FM
                || audioType == AudioConstants.AudioTypes.XTING_LOCAL_AM
                || audioType == AudioConstants.AudioTypes.XTING
        ) {
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

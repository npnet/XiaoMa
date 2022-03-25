package com.xiaoma.dualscreen.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.xiaoma.carlib.manager.XmCarVendorExtensionManager;
import com.xiaoma.dualscreen.R;
import com.xiaoma.dualscreen.listener.DualScreenConnectListener;
import com.xiaoma.dualscreen.listener.DuralScreenPlayListener;
import com.xiaoma.dualscreen.listener.IBTConnectState;
import com.xiaoma.dualscreen.manager.MediaViewManager;
import com.xiaoma.dualscreen.manager.PlayerConnectHelper;
import com.xiaoma.image.ImageLoader;
import com.xiaoma.player.AudioConstants;
import com.xiaoma.player.AudioInfo;
import com.xiaoma.player.ProgressInfo;
import com.xiaoma.thread.ThreadDispatcher;
import com.xiaoma.ui.view.MarqueeTextView;
import com.xiaoma.utils.FileUtils;
import com.xiaoma.utils.NetworkUtils;
import com.xiaoma.utils.log.KLog;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.xiaoma.player.AudioConstants.AudioTypes.MUSIC_KUWO_RADIO;
import static com.xiaoma.player.AudioConstants.AudioTypes.MUSIC_LOCAL_BT;
import static com.xiaoma.player.AudioConstants.AudioTypes.MUSIC_LOCAL_USB;
import static com.xiaoma.player.AudioConstants.AudioTypes.MUSIC_ONLINE_KUWO;
import static com.xiaoma.player.AudioConstants.AudioTypes.XTING;
import static com.xiaoma.player.AudioConstants.AudioTypes.XTING_KOALA_ALBUM;
import static com.xiaoma.player.AudioConstants.AudioTypes.XTING_LOCAL_AM;
import static com.xiaoma.player.AudioConstants.AudioTypes.XTING_LOCAL_FM;
import static com.xiaoma.player.AudioConstants.AudioTypes.XTING_NET_FM;
import static com.xiaoma.player.AudioConstants.AudioTypes.XTING_NET_RADIO;
import static com.xiaoma.player.AudioConstants.BundleKey.MusicType;
import static com.xiaoma.player.AudioConstants.BundleKey.XiangTingType;

/**
 * @author: iSun
 * @date: 2019/3/7 0007
 */
public class MediaView extends BaseView implements DualScreenConnectListener, DuralScreenPlayListener{
    private Context mContext;
    private LinearLayout mMusicTypeLayout;
    private LinearLayout mMusicDescLayout;
    private RelativeLayout mNoUsbLayout, mNoBlueLayout, mNoInternetLayout, mUsbBreakLayout, mBlueBreakLayout, mNoMusicContainer, desCoverRl;
    private TextView noBlueTv,noUSBTv,noInternetTv,usbBreakTv,blueBreakTv,noMusicTv;
    private ImageView imageCover;
    private MarqueeTextView tvTitle;
    private final String DEFAULT_TAG = "default";
    private TextView[] mTvMediaTypes;
    //usb连接状态
    private static int usbConnectState;
    //蓝牙连接状态
    private static boolean btMusicConnected = false;
    //当前音频信息
    private static AudioInfo mAudioInfo;
    //音频播放状态
    private static int mPlayState;
    private PlayerConnectHelper connectHelper;
    private OnMediaListener mOnMediaListener;
    private MediaViewManager mMediaViewManager = null;
    private ResetAudioInfoRunnable resetAudioInfoRunnable;
    private int mSelectedTypeTextColor = R.color.simple_tv_color_yellow;
    private int mNormalTypeTextColor = R.color.white;
    private ImageView mIvNoUsb, mIvNoBlue, mIvNoInternet, mIvUsbBreak, mIvBlueBreak, mIvNoMusic;
    private ProgressBar mProgressBar;
    private ImageView mMediaTypeBg;
    private int defaultCoverId = R.drawable.iv_default_cover;
    private boolean isSetDefault = false;

    public MediaView(Context context) {
        super(context);
        this.mContext = context;
        init();
    }

    public MediaView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        init();
    }

    public MediaView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        init();
    }

    public MediaView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.mContext = context;
        init();
    }

    @Override
    public void onRefresh() {

    }

    @Override
    public int contentViewId() {
        return R.layout.view_media;
    }

    @Override
    public void onViewCreated() {

    }

    private void init() {
        mMusicTypeLayout = findViewById(R.id.ll_music_type);
        mMusicDescLayout = findViewById(R.id.ll_music_desc);

        imageCover = findViewById(R.id.iv_cover);
        tvTitle = findViewById(R.id.tv_title);
        mProgressBar = findViewById(R.id.pb_music);

        TextView mTvUsb = findViewById(R.id.tv_usb);
        TextView mTvBluetooth = findViewById(R.id.tv_bluetooth);
        TextView mTvOnlineMusic = findViewById(R.id.tv_online_music);
        TextView mTvFM = findViewById(R.id.tv_fm);
        TextView mTvAM = findViewById(R.id.tv_am);
        TextView mTvOnlineRadio = findViewById(R.id.tv_online_radio);

        mTvMediaTypes = new TextView[6];
        mTvMediaTypes[0] = mTvUsb;
        mTvMediaTypes[1] = mTvBluetooth;
        mTvMediaTypes[2] = mTvOnlineMusic;
        mTvMediaTypes[3] = mTvFM;
        mTvMediaTypes[4] = mTvAM;
        mTvMediaTypes[5] = mTvOnlineRadio;

        mMediaTypeBg = findViewById(R.id.bg_low_wisdom);

        mNoUsbLayout = findViewById(R.id.noUsbContainer);
        mNoBlueLayout = findViewById(R.id.noBlueContainer);
        mNoInternetLayout = findViewById(R.id.noInternetContainer);
        mUsbBreakLayout = findViewById(R.id.usbBreakContainer);
        mBlueBreakLayout = findViewById(R.id.blueBreakContainer);
        mNoMusicContainer = findViewById(R.id.noMusicContainer);
        desCoverRl = findViewById(R.id.des_cover_rl);
        mIvNoUsb = findViewById(R.id.iv_no_usb);
        mIvNoBlue = findViewById(R.id.iv_no_blue);
        mIvNoInternet = findViewById(R.id.iv_no_internet);
        mIvUsbBreak = findViewById(R.id.iv_usb_break);
        mIvBlueBreak = findViewById(R.id.iv_blue_break);
        mIvNoMusic = findViewById(R.id.iv_no_music);

        noBlueTv = findViewById(R.id.tv_no_blue);
        noUSBTv = findViewById(R.id.tv_no_usb);
        noInternetTv = findViewById(R.id.tv_no_internet);
        usbBreakTv = findViewById(R.id.tv_usb_break);
        blueBreakTv = findViewById(R.id.tv_blue_break);
        noMusicTv = findViewById(R.id.tv_no_music);



        initAudioListener();
        mMediaViewManager = MediaViewManager.getInstance();

    }

    @Override
    public void onDestory() {
        super.onDestory();
        connectHelper.removeConnectListener(this);
        connectHelper.removePlayListener(this);
        this.mOnMediaListener = null;
    }


    public void initMenuLevel() {
        mMediaViewManager.setMainStep();
        XmCarVendorExtensionManager.getInstance().setMediaMenuLevel(MediaViewManager.IViewStep.MEDIA_MAIN);
        showMediaView(mMusicTypeLayout);
    }

    public void refreshDetail() {
       if(skipNextStep()){
           initMusicDesc(false);
       }
    }

    /**
     * init 音频相关监听
     */
    private void initAudioListener() {
        connectHelper = PlayerConnectHelper.getInstance();
        //音频监听
        connectHelper.setPlayListener(this);
        //usb 与 蓝牙连接监听
        connectHelper.setConnectListener(this);
    }

    public void setSelectedMusicUp() {
        KLog.e("setSelectedMusicUp");
        if (mMediaViewManager.isDetail()) {
            //想听播放历史处理
            if (mAudioInfo != null) {
                if (mAudioInfo.isHistory() && (mAudioInfo.getAudioType() ==
                        AudioConstants.AudioTypes.XTING_NET_FM || mAudioInfo.getAudioType() == AudioConstants.AudioTypes.XTING_NET_RADIO)) {
                    connectHelper.playXtingHistory(mAudioInfo.getAudioType(), AudioConstants.Action.Option.PREVIOUS);
                } else {
                    connectHelper.preNextAudio(AudioConstants.Action.Option.PREVIOUS, mAudioInfo.getAudioType());
                }
            }
        } else {
            mMediaViewManager.pre();
            notifyChangeMusic();
        }
    }

    public void setSelectedMusicDown() {
        KLog.e("setSelectedMusicDown");
        if (mMediaViewManager.isDetail()) {
            if (mAudioInfo != null) {
                //想听播放历史处理
                if (mAudioInfo.isHistory() && (mAudioInfo.getAudioType() ==
                        AudioConstants.AudioTypes.XTING_NET_FM || mAudioInfo.getAudioType() == AudioConstants.AudioTypes.XTING_NET_RADIO)) {
                    connectHelper.playXtingHistory(mAudioInfo.getAudioType(), AudioConstants.Action.Option.NEXT);
                } else {
                    connectHelper.preNextAudio(AudioConstants.Action.Option.NEXT, mAudioInfo.getAudioType());
                }
            }
        } else {
            mMediaViewManager.next();
            notifyChangeMusic();
        }
    }

    public void setMediaViewOK(boolean justRefreashUI) {
        KLog.e("setMediaViewOK");
        //网络异常
        if (!NetworkUtils.isConnected(getContext()) && !mMediaViewManager.isLocal()) {
            mMediaViewManager.setDetailStep();
            XmCarVendorExtensionManager.getInstance().setMediaMenuLevel(MediaViewManager.IViewStep.MEDIA_DETAIL);
            showMediaView(mNoInternetLayout);
            return;
        }
        int currentSelectIndex = mMediaViewManager.getCurrentSelectIndex();
        int mSelectedAudioType = covertIndexToType(currentSelectIndex);
        int mCurrentAudioType = getCurrentAudioType();
        initMusicDesc(false);
        if (mMediaViewManager.isMain() && isPlaying() && mSelectedAudioType == mCurrentAudioType) {   //如果是一级菜单且选择的音源是正在播放的
            skipNextStep();
            return;
        }
        skipNextStep();
        int audioType = 0;
        int subType = 0;
        KLog.e("setMediaViewOK, selectedMusic=" + mSelectedAudioType);
        if (!isPlaying() || mSelectedAudioType != mCurrentAudioType) {
            if(mSelectedAudioType != mCurrentAudioType && !justRefreashUI){
                pauseAudio();
                setDefaultIcon(true);
            }
            switch (currentSelectIndex) {
                case MediaViewManager.IAudioType.USB_MUSIC:  //USB音乐
                    if(usbConnectState == AudioConstants.ConnectStatus.USB_SCAN_FINISH_WITH_NO_MUSIC){
                        showMediaView(mNoMusicContainer);
                        return;
                    }else if (usbConnectState != AudioConstants.ConnectStatus.USB_SCAN_FINISH) {
                        showMediaView(mNoUsbLayout);
                        return;
                    }
                    audioType = MUSIC_LOCAL_USB;
                    subType = AudioConstants.MusicType.USB;
                    break;
                case MediaViewManager.IAudioType.BT_MUSIC:  //蓝牙音乐
                    //蓝牙未连接
                    if (!btMusicConnected) {
                        showBTView();
                        return;
                    }
                    audioType = MUSIC_LOCAL_BT;
                    subType = AudioConstants.MusicType.BLUE;
                    break;
                case MediaViewManager.IAudioType.ONLINE_MUSIC:  //在线音乐
                    if (connectHelper.getSourceInfoByAudioType(MUSIC_ONLINE_KUWO) == null) {
                        showMediaView(mNoMusicContainer);
                        return;
                    }
                    audioType = MUSIC_ONLINE_KUWO;
                    subType = AudioConstants.MusicType.ONLINE;
                    break;
                case MediaViewManager.IAudioType.LOCAL_FM:   //FM电台
                    if (connectHelper.getSourceInfoByAudioType(XTING_NET_FM) == null) {
                        showMediaView(mNoMusicContainer);
                        return;
                    }
                    audioType = AudioConstants.XiangTingType.FM;
                    subType = AudioConstants.XiangTingType.FM;
                    break;
                case MediaViewManager.IAudioType.LOCAL_AM:  //AM电台
                    if (connectHelper.getSourceInfoByAudioType(XTING_NET_FM) == null) {
                        showMediaView(mNoMusicContainer);
                        return;
                    }
                    audioType = AudioConstants.XiangTingType.AM;
                    subType = AudioConstants.XiangTingType.AM;
                    break;
                case MediaViewManager.IAudioType.ONLINE_XTING:  //在线电台
                    if (connectHelper.getSourceInfoByAudioType(XTING_NET_RADIO) == null) {
                        showMediaView(mNoMusicContainer);
                        return;
                    }
                    audioType = AudioConstants.XiangTingType.ONLINE;
                    subType = AudioConstants.XiangTingType.ONLINE;
                    break;
            }
            if(justRefreashUI) return;
            if (audioType == AudioConstants.XiangTingType.FM
                    || audioType == AudioConstants.XiangTingType.AM
                    || audioType == AudioConstants.XiangTingType.ONLINE) {
                audioType = XTING;
            }
            //想听播放历史处理
            if (mAudioInfo != null && mAudioInfo.isHistory() &&
                    (mAudioInfo.getAudioType() == AudioConstants.AudioTypes.XTING_NET_FM
                            || mAudioInfo.getAudioType() == AudioConstants.AudioTypes.XTING_NET_RADIO)) {
                playXtingHistory(audioType, subType);
            } else {
                if (audioType == XTING) {
                    playXting(audioType, subType);
                } else {
                    connectHelper.playAudioBySubType(audioType, AudioConstants.PlayAction.DEFAULT, MusicType, subType);
                }
            }
        } else {
            if(justRefreashUI) return;
            //暂停播放
            if (mMediaViewManager.isDetail()) {
                pauseAudio();
            }
        }
    }

    private void playXtingHistory(int audioType, int subType) {
        final int finalAudioType = audioType;
        final int finalSubType = subType;
        ThreadDispatcher.getDispatcher().postDelayed(new Runnable() {
            @Override
            public void run() {
                connectHelper.playXtingHistoryBySubType(finalAudioType, AudioConstants.Action.Option.PLAY, finalSubType);
            }
        }, 800);
    }

    private void playXting(int audioType, int subType) {
        final int finalAudioType1 = audioType;
        final int finalSubType1 = subType;
        ThreadDispatcher.getDispatcher().postDelayed(new Runnable() {
            @Override
            public void run() {
                connectHelper.playAudioBySubType(finalAudioType1, AudioConstants.PlayAction.DEFAULT, XiangTingType, finalSubType1);
            }
        }, 800);
    }

    private void showBTView() {
        connectHelper.fetchBTConnectState(new IBTConnectState() {
            @Override
            public void isConnect(boolean isConnected) {
                if (!isConnected) {
                    btMusicConnected = false;
                    showMediaView(mNoBlueLayout);
                } else {
                    btMusicConnected = true;
                    connectHelper.playAudioBySubType(MUSIC_LOCAL_BT, AudioConstants.PlayAction.DEFAULT, MusicType, AudioConstants.MusicType.BLUE);
                    showMediaView(mMusicDescLayout);
                }
            }
        });
    }

    private int getCurrentAudioType() {
        int mCurrentAudioType = -1;
        if (mAudioInfo != null) {
            int audioType = mAudioInfo.getAudioType();
            switch (audioType) {
                case XTING_NET_FM:
                case XTING_NET_RADIO:
                case XTING_KOALA_ALBUM:
                    mCurrentAudioType = AudioConstants.XiangTingType.ONLINE;
                    break;
                case XTING_LOCAL_FM:
                    mCurrentAudioType = AudioConstants.XiangTingType.FM;
                    break;
                case XTING_LOCAL_AM:
                    mCurrentAudioType = AudioConstants.XiangTingType.AM;
                    break;
                case MUSIC_ONLINE_KUWO:
                case MUSIC_KUWO_RADIO:
                    mCurrentAudioType = MUSIC_ONLINE_KUWO;
                    break;
                case MUSIC_LOCAL_USB:
                    mCurrentAudioType = MUSIC_LOCAL_USB;
                    break;
                case MUSIC_LOCAL_BT:
                    mCurrentAudioType = MUSIC_LOCAL_BT;
                    break;
                default:
                    mCurrentAudioType = -1;
                    break;
            }
        }
        return mCurrentAudioType;
    }

    public boolean skipNextStep() {
//        if (mMediaViewManager.isDetail()) {
//            return false;
//        }
        mMediaViewManager.setDetailStep();
        XmCarVendorExtensionManager.getInstance().setMediaMenuLevel(MediaViewManager.IViewStep.MEDIA_DETAIL);
        showMediaView(mMusicDescLayout);
        return true;
    }

    private void setDefaultIcon(boolean resProcess) {
        tvTitle.setText(getContext().getString(R.string.music_info_loading));
        imageCover.setImageResource(defaultCoverId);
        if(resProcess) mProgressBar.setProgress(0);
        isSetDefault = true;
    }

    private void pauseAudio() {
        if (mAudioInfo != null) {
            if (isPlaying()) {
                connectHelper.pauseAudio(mAudioInfo.getAudioType());
            }
        }
    }

    public void setMediaViewReturn() {
        KLog.e("setMediaViewReturn");
        if (mMediaViewManager.isMain()) {
            return;
        }
        showMediaView(mMusicTypeLayout);
        notifyChangeMusic();
        setDefaultIcon(false);
        //发送菜单等级
        mMediaViewManager.setMainStep();
        XmCarVendorExtensionManager.getInstance().setMediaMenuLevel(MediaViewManager.IViewStep.MEDIA_MAIN);
    }

    private void notifyChangeMusic() {
        final int currentSelectIndex = mMediaViewManager.getCurrentSelectIndex();
        for (int i = 0; i < mTvMediaTypes.length; i++) {
            if (i == currentSelectIndex) {
                mTvMediaTypes[i].setTextColor(getResources().getColor(mSelectedTypeTextColor));
                mTvMediaTypes[i].setTextSize(TypedValue.COMPLEX_UNIT_PX, 40);
            } else {
                mTvMediaTypes[i].setTextColor(getResources().getColor(mNormalTypeTextColor));
                mTvMediaTypes[i].setTextSize(TypedValue.COMPLEX_UNIT_PX, 32);
            }
        }
    }

    @Override
    public void connectState(final int connect) {
        KLog.e("media connectState=" + connect);
        ThreadDispatcher.getDispatcher().postOnMain(new Runnable() {
            @Override
            public void run() {
                switch (connect) {
                    case AudioConstants.ConnectStatus.BLUETOOTH_CONNECTED:
                        showBTMedia();
                        break;
                    case AudioConstants.ConnectStatus.BLUETOOTH_DISCONNECTED:
                        handPlayingBtBreak();
                        break;

                    //蓝牙已打开并且已经连接音乐
                    case AudioConstants.ConnectStatus.BLUETOOTH_SINK_CONNECTED:
                        btMusicConnected = true;
                        showBTMedia();
                        break;
                    //已关闭蓝牙音乐
                    case AudioConstants.ConnectStatus.BLUETOOTH_SINK_DISCONNECTED:
                        btMusicConnected = false;
                        handPlayingBtBreak();
                        break;

                    case AudioConstants.ConnectStatus.USB_SCAN_FINISH:
                        usbConnectState = connect;
                        showUsb();
                        break;
                    case AudioConstants.ConnectStatus.USB_SCAN_FINISH_WITH_NO_MUSIC:
                        usbConnectState = connect;
                        showNoUsbMusic();
                        break;
                    case AudioConstants.ConnectStatus.USB_REMOVE:
                    case AudioConstants.ConnectStatus.USB_NOT_MOUNTED:
                    case AudioConstants.ConnectStatus.USB_UNSUPPORT:
                        //刷新usb栏目状态
                        usbConnectState = connect;
                        handPlayingUsbRemove();
                        break;
                }
            }

            private void showBTMedia() {
                if (mMediaViewManager.isDetail()
                        && mMediaViewManager.getCurrentSelectIndex() == MediaViewManager.IAudioType.BT_MUSIC) {
                    showMediaView(mMusicDescLayout);
                }
            }

            private void showUsb() {
                if (mMediaViewManager.isDetail()
                        && mMediaViewManager.getCurrentSelectIndex() == MediaViewManager.IAudioType.USB_MUSIC) {
                    showMediaView(mMusicDescLayout);
                }
            }

            private void showNoUsbMusic() {
                if (mMediaViewManager.isDetail()
                        && mMediaViewManager.getCurrentSelectIndex() == MediaViewManager.IAudioType.USB_MUSIC) {
                    showMediaView(mNoMusicContainer);
                }
            }
        });
    }

    @Override
    public void onProgress(final ProgressInfo progressInfo) {
        KLog.e("onProgress progressInfo:" + progressInfo);
        // TODO: 2019/7/16 0016 刷新ui
        ThreadDispatcher.getDispatcher().postOnMain(new Runnable() {
            @Override
            public void run() {
                if (checkNum(String.valueOf(progressInfo.getPercent()))) {
                    int progress = (int) (progressInfo.getPercent() * 100);
                    mProgressBar.setProgress(progress);
                    if (!mProgressBar.isShown() && !isBTFMAMMedia()) {
                        mProgressBar.setVisibility(View.VISIBLE);
                    }
                } else {
                    mProgressBar.setProgress(0);
                }
            }
        });
    }

    @Override
    public void onAudioInfo(final AudioInfo audioInfo) {
        KLog.e("onAudioInfo audioInfo:" + audioInfo);
        //酷我音乐和蓝牙音乐有异步问题，所以必需在播放状态才进行切换,防止来回跳动
        if (audioInfo.getPlayState() != AudioConstants.AudioStatus.PLAYING && audioInfo.getPlayState() != AudioConstants.AudioStatus.LOADING
                && (audioInfo.getAudioType() == MUSIC_LOCAL_BT
                || audioInfo.getAudioType() == MUSIC_ONLINE_KUWO
                || audioInfo.getAudioType() == MUSIC_KUWO_RADIO)) {
            return;
        }
        boolean hasChanged = false;
        if(mAudioInfo != null && audioInfo != null &&
                (audioInfo.getAudioType() != mAudioInfo.getAudioType()
                        || mAudioInfo.getUniqueId() != audioInfo.getUniqueId())){
            hasChanged = true;
        }
        mAudioInfo = audioInfo;
        if (resetAudioInfoRunnable != null) {
            ThreadDispatcher.getDispatcher().removeOnMain(resetAudioInfoRunnable);
        }
        ThreadDispatcher.getDispatcher().postOnMainDelayed(resetAudioInfoRunnable = new ResetAudioInfoRunnable(audioInfo, hasChanged), 100);
    }


    private void syncAudioType(AudioInfo audioInfo) {
        int mSelectedAudioType = -1;
        int audioType = audioInfo.getAudioType();
        switch (audioType) {
            case XTING_NET_FM:
            case XTING_NET_RADIO:
            case XTING_KOALA_ALBUM:
                mSelectedAudioType = MediaViewManager.IAudioType.ONLINE_XTING;
                break;
            case XTING_LOCAL_FM:
                mSelectedAudioType = MediaViewManager.IAudioType.LOCAL_FM;
                break;
            case XTING_LOCAL_AM:
                mSelectedAudioType = MediaViewManager.IAudioType.LOCAL_AM;
                break;
            case MUSIC_ONLINE_KUWO:
            case MUSIC_KUWO_RADIO:
                mSelectedAudioType = MediaViewManager.IAudioType.ONLINE_MUSIC;
                break;
            case MUSIC_LOCAL_USB:
                mSelectedAudioType = MediaViewManager.IAudioType.USB_MUSIC;
                break;
            case MUSIC_LOCAL_BT:
                mSelectedAudioType = MediaViewManager.IAudioType.BT_MUSIC;
                break;
        }
        MediaViewManager.getInstance().setCurrentSelectIndex(mSelectedAudioType);
    }

    @Override
    public void onPlayState(final int playState) {
        KLog.e("onAudioInfo onPlayState:" + playState);
        if (playState != mPlayState) {
            if(playState == AudioConstants.AudioStatus.ERROR && mMediaViewManager.isDetail() && mMusicDescLayout.isShown()) {
                ThreadDispatcher.getDispatcher().postOnMain(new Runnable() {
                    @Override
                    public void run() {
                        showMediaView(mNoInternetLayout);
                    }
                });
            }else if (mPlayState == AudioConstants.AudioStatus.ERROR && !mMusicDescLayout.isShown() && mMediaViewManager.isDetail()){
                ThreadDispatcher.getDispatcher().postOnMain(new Runnable() {
                    @Override
                    public void run() {
                        showMediaView(mMusicDescLayout);
                    }
                });
            }
            mPlayState = playState;
            if (mAudioInfo != null) {
                mOnMediaListener.onConferMediaSimpleText(mAudioInfo.getTitle());
            }
        }
    }

    public boolean isPlaying() {
        return mPlayState == AudioConstants.AudioStatus.PLAYING
                || mPlayState == AudioConstants.AudioStatus.LOADING;
    }

    @Override
    public void onDataSource(int dataSource) {

    }

    @Override
    public void onAudioFavorite(boolean favorite) {

    }

    /**
     * 当前播放usb音乐时拔出
     */
    private void handPlayingUsbRemove() {
        KLog.e("handPlayingUsbRemove");
        if(mMediaViewManager.getCurrentSelectIndex() == MediaViewManager.IAudioType.USB_MUSIC){
            setAudioInfoNull();
            if(mMediaViewManager.isDetail() ){
                setMediaViewReturn();
                setDefaultIcon(true);
            }
        }
    }

    /**
     * 当前播放蓝牙音乐时断开
     */
    private void handPlayingBtBreak() {
        KLog.e("handPlayingBtBreak");
        if(mMediaViewManager.getCurrentSelectIndex() == MediaViewManager.IAudioType.BT_MUSIC){
            setAudioInfoNull();
            if(mMediaViewManager.isDetail()){
                showMediaView(mBlueBreakLayout);
                setDefaultIcon(true);
            }
        }
    }

    /**
     * 断开连接后音频信息置空,重置栏目状态
     */
    private void setAudioInfoNull() {
        mAudioInfo = null;
        //todo
        if (mOnMediaListener != null) {
            mOnMediaListener.onConferMediaSimpleText("");
        }
    }

    private void initMusicDesc(boolean resetInfo) {
        if (mAudioInfo == null) {
            return;
        }
        KLog.e("initMusicDesc," + mAudioInfo.toString());
        String title = mAudioInfo.getTitle();
        if(mAudioInfo.getAudioType() == XTING_NET_FM
                || mAudioInfo.getAudioType() == XTING_LOCAL_FM
                || mAudioInfo.getAudioType() == XTING_NET_RADIO
                || mAudioInfo.getAudioType() == XTING_KOALA_ALBUM
                || mAudioInfo.getAudioType() == XTING_LOCAL_AM
                || mAudioInfo.getAudioType() == XTING){
            if(!TextUtils.isEmpty(mAudioInfo.getSubTitle())){
                title = mAudioInfo.getSubTitle();
            }
        }else if(TextUtils.isEmpty(mAudioInfo.getTitle())){
            title = mAudioInfo.getSubTitle();
        }

        if (TextUtils.isEmpty(title)) {
            title = getContext().getString(R.string.unknow_song);
        }
        if(!tvTitle.getText().toString().equals(title)){
            tvTitle.setText(title);
        }
        if (isBTFMAMMedia()) {
            mProgressBar.setVisibility(GONE);
        } else {
            mProgressBar.setVisibility(VISIBLE);
        }
        if(resetInfo) mProgressBar.setProgress(0);
        if(getCurrentAudioType() == AudioConstants.AudioTypes.MUSIC_LOCAL_USB){
            Bitmap bitmap = FileUtils.getMP3AlbumArt(getContext().getApplicationContext(),mAudioInfo.getUsbMusicPath());
            ImageLoader.with(mContext)
                    .asBitmap()
                    .load(bitmap == null?"":bitmap)
                    .placeholder(defaultCoverId)
                    .into(imageCover);
        }else if(isBTFMAMMedia()){
            imageCover.setImageResource(defaultCoverId);
            isSetDefault = true;
        }else{
            isSetDefault = false;
            ImageLoader.with(mContext)
                    .asBitmap()
                    .load(mAudioInfo.getCover() == null?"":mAudioInfo.getCover())
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            if(isSetDefault) return;
                            if(resource == null){
                                imageCover.setImageResource(defaultCoverId);
                            }else{
                                imageCover.setImageBitmap(resource);
                            }
                        }
                    });
        }
    }

    public void setMediaListener(OnMediaListener onMediaListener) {
        this.mOnMediaListener = onMediaListener;
        if (mOnMediaListener != null && mAudioInfo != null) {
            mOnMediaListener.onConferMediaSimpleText(mAudioInfo.getTitle());
        }
    }

    private int covertIndexToType(int selected) {
        int audioType = -1;
        switch (selected) {
            case 0:  //USB音乐
                audioType = MUSIC_LOCAL_USB;
                break;
            case 1:  //蓝牙音乐
                audioType = MUSIC_LOCAL_BT;
                break;
            case 2:  //在线音乐
                audioType = MUSIC_ONLINE_KUWO;
                break;
            case 3:   //FM电台
                audioType = AudioConstants.XiangTingType.FM;
                break;
            case 4:  //AM电台
                audioType = AudioConstants.XiangTingType.AM;
                break;
            case 5:  //在线电台
                audioType = AudioConstants.XiangTingType.ONLINE;
                break;
        }
        return audioType;
    }

    public boolean isMusicTypeLayout() {
        KLog.e("mMusicTypeLayout.isShown():" + mMusicTypeLayout.isShown());
        return mMusicTypeLayout.isShown();
    }

    //skin
    public void changeSkin(boolean isHigh, int skinType) {
        if (isHigh) {
            switch (skinType) {

                case 1:
                    defaultCoverId = R.drawable.iv_default_cover;
                    mSelectedTypeTextColor = R.color.simple_tv_color_blue;
                    mNormalTypeTextColor = R.color.white;
                    mIvNoUsb.setImageResource(R.drawable.icon_no_usb_high_luxury);
                    mIvNoBlue.setImageResource(R.drawable.icon_no_blue_high_luxury);
                    mIvNoInternet.setImageResource(R.drawable.icon_no_internet_high_luxury);
                    mIvUsbBreak.setImageResource(R.drawable.icon_no_usb_high_luxury);
                    mIvBlueBreak.setImageResource(R.drawable.icon_no_blue_high_luxury);
                    mIvNoMusic.setImageResource(R.drawable.icon_no_contact_high_luxury);
                    mMediaTypeBg.setBackgroundColor(Color.TRANSPARENT);
                    mMusicTypeLayout.setPadding(0, 25, 0, 0);
                    mMusicDescLayout.setPadding(0, 10, 0, 0);
                    mProgressBar.setProgressDrawable(getResources().getDrawable(R.drawable.layer_list_progressbar_bar2));
                    resetLayout(317,11, 35, 35, -1);
                    resetNoMusicView(380, 356, 35, 45);
                    resetNoBlueView(380, 356,  35, 45);
                    resetNoUSBView(419, 356,  35, 45);
                    resetNoInternetView(380, 356,  35, 45);
                    resetUSBBreakView(419, 356,  35, 45);
                    resetBlueBreakView(380, 356,  35, 45);
                    break;
                case 2:
                    defaultCoverId = R.drawable.iv_default_cover_dream;
                    mSelectedTypeTextColor = R.color.simple_tv_color_yellow;
                    mNormalTypeTextColor = R.color.white;
                    mIvNoUsb.setImageResource(R.drawable.icon_no_usb_high_dream);
                    mIvNoBlue.setImageResource(R.drawable.icon_no_blue_high_dream);
                    mIvNoInternet.setImageResource(R.drawable.icon_no_internet_high_dream);
                    mIvUsbBreak.setImageResource(R.drawable.icon_no_usb_high_dream);
                    mIvBlueBreak.setImageResource(R.drawable.icon_no_blue_high_dream);
                    mIvNoMusic.setImageResource(R.drawable.icon_no_contact_high_dream);
                    mMediaTypeBg.setBackgroundColor(Color.TRANSPARENT);
                    mMusicTypeLayout.setPadding(0, 25, 0, 0);
                    mProgressBar.setProgressDrawable(getResources().getDrawable(R.drawable.layer_list_progressbar_bar3));
                    resetLayout(327,31, 45, 35, -1);
                    resetNoMusicView(380, 356, 55, 65);
                    resetNoBlueView(380, 356,  55, 65);
                    resetNoUSBView(419, 356,  55, 65);
                    resetNoInternetView(380, 356,  55, 65);
                    resetUSBBreakView(419, 356,  55, 65);
                    resetBlueBreakView(380, 356,  55, 65);
                    break;
                case 0:default:
                    defaultCoverId = R.drawable.iv_default_cover;
                    mSelectedTypeTextColor = R.color.simple_tv_color_yellow;
                    mNormalTypeTextColor = R.color.white;
                    mIvNoUsb.setImageResource(R.drawable.icon_no_usb_high_wisdom);
                    mIvNoBlue.setImageResource(R.drawable.icon_no_blue_high_wisdom);
                    mIvNoInternet.setImageResource(R.drawable.icon_no_internet_high_wisdom);
                    mIvUsbBreak.setImageResource(R.drawable.icon_no_usb_high_wisdom);
                    mIvBlueBreak.setImageResource(R.drawable.icon_no_blue_high_wisdom);
                    mIvNoMusic.setImageResource(R.drawable.icon_no_contact_high_wisdom);
                    mMediaTypeBg.setBackgroundColor(Color.TRANSPARENT);
                    mMusicTypeLayout.setPadding(0, 25, 0, 0);
                    mProgressBar.setProgressDrawable(getResources().getDrawable(R.drawable.layer_list_progressbar_bar1));
                    resetLayout(327,31, 40, 50, -1);
                    resetNoMusicView(380, 356, 40, 50);
                    resetNoBlueView(380, 356,  40, 50);
                    resetNoUSBView(419, 356,  40, 50);
                    resetNoInternetView(380, 356,  40, 50);
                    resetUSBBreakView(419, 356,  40, 50);
                    resetBlueBreakView(380, 356,  40, 50);
                    break;
            }
        } else {
            switch (skinType) {
                case 1:
                    defaultCoverId = R.drawable.iv_default_cover;
                    mSelectedTypeTextColor = R.color.simple_tv_color_blue;
                    mNormalTypeTextColor = R.color.white;
                    mIvNoUsb.setImageResource(R.drawable.icon_no_usb_low_luxury);
                    mIvNoBlue.setImageResource(R.drawable.icon_no_blue_low_luxury);
                    mIvNoInternet.setImageResource(R.drawable.icon_no_internet_low_luxury);
                    mIvUsbBreak.setImageResource(R.drawable.icon_no_usb_low_luxury);
                    mIvBlueBreak.setImageResource(R.drawable.icon_no_blue_low_luxury);
                    mIvNoMusic.setImageResource(R.drawable.icon_no_contact_low_luxury);
                    mMediaTypeBg.setBackgroundColor(Color.TRANSPARENT);
                    mMusicTypeLayout.setPadding(0, 25, 0, 0);
                    mMusicDescLayout.setPadding(0, 20, 0, 0);
                    mProgressBar.setProgressDrawable(getResources().getDrawable(R.drawable.layer_list_progressbar_bar2));
                    resetLayout(317,11, 20, 20, 200);
                    resetNoMusicView(174, 172, 155, 85);
                    resetNoBlueView(174, 172, 155, 85);
                    resetNoUSBView(174, 183, 155, 85);
                    resetNoInternetView(174, 172, 155, 85);
                    resetUSBBreakView(174, 183, 155, 85);
                    resetBlueBreakView(174, 172, 155, 85);
                    break;
                case 2:
                    defaultCoverId = R.drawable.iv_default_cover_dream;
                    mSelectedTypeTextColor = R.color.simple_tv_color_yellow;
                    mNormalTypeTextColor = R.color.white;
                    mIvNoUsb.setImageResource(R.drawable.icon_no_usb_low_dream);
                    mIvNoBlue.setImageResource(R.drawable.icon_no_blue_low_dream);
                    mIvNoInternet.setImageResource(R.drawable.icon_no_internet_low_dream);
                    mIvUsbBreak.setImageResource(R.drawable.icon_no_usb_low_dream);
                    mIvBlueBreak.setImageResource(R.drawable.icon_no_blue_low_dream);
                    mIvNoMusic.setImageResource(R.drawable.icon_no_contact_low_dream);
                    mMediaTypeBg.setBackgroundColor(Color.TRANSPARENT);
                    mMusicTypeLayout.setPadding(0, 85, 0, 0);
                    mMusicDescLayout.setPadding(0, 70, 0, 0);
                    mProgressBar.setProgressDrawable(getResources().getDrawable(R.drawable.layer_list_progressbar_bar6));
                    resetLayout(327,31, 50, 40, 280);
                    resetNoMusicView(380, 356, 75, 90);
                    resetNoBlueView(380, 356, 75, 90);
                    resetNoUSBView(419, 356, 75, 90);
                    resetNoInternetView(380, 356, 75, 90);
                    resetUSBBreakView(419, 356, 75, 90);
                    resetBlueBreakView(380, 356, 75, 90);
                    break;
                case 0:default:
                    defaultCoverId = R.drawable.iv_default_cover;
                    mSelectedTypeTextColor = R.color.simple_tv_color_yellow;
                    mNormalTypeTextColor = R.color.white;
                    mIvNoUsb.setImageResource(R.drawable.icon_no_usb_high_wisdom);
                    mIvNoBlue.setImageResource(R.drawable.icon_no_blue_high_wisdom);
                    mIvNoInternet.setImageResource(R.drawable.icon_no_internet_high_wisdom);
                    mIvUsbBreak.setImageResource(R.drawable.icon_no_usb_high_wisdom);
                    mIvBlueBreak.setImageResource(R.drawable.icon_no_blue_high_wisdom);
                    mIvNoMusic.setImageResource(R.drawable.icon_no_contact_high_wisdom);
                    mMediaTypeBg.setBackgroundResource(R.drawable.bg_low_wisdom);
                    mMusicTypeLayout.setPadding(0, 85, 0, 0);
                    mMusicDescLayout.setPadding(0, 60, 0, 0);
                    mProgressBar.setProgressDrawable(getResources().getDrawable(R.drawable.layer_list_progressbar_bar4));
                    resetLayout(327,31, 50, 50, 280);
                    resetNoMusicView(380, 356, 135, 145);
                    resetNoBlueView(380, 356,135, 145);
                    resetNoUSBView(419, 356, 135, 145);
                    resetNoInternetView(380, 356, 135, 160);
                    resetUSBBreakView(419, 356, 135, 145);
                    resetBlueBreakView(380, 356, 135, 145);
                    break;
            }
        }
        notifyChangeMusic();
    }

    private void resetNoMusicView(int width, int height, int ivMarginTop, int txtMarginTop){
        RelativeLayout.LayoutParams linearParams = (RelativeLayout.LayoutParams) mIvNoMusic.getLayoutParams();
        linearParams.width = width;
        linearParams.height = height;
        linearParams.topMargin = ivMarginTop;
        mIvNoMusic.setLayoutParams(linearParams);

        RelativeLayout.LayoutParams desLinearParams = (RelativeLayout.LayoutParams) noMusicTv.getLayoutParams();
        desLinearParams.topMargin = txtMarginTop;
        noMusicTv.setLayoutParams(desLinearParams);
    }

    private void resetNoBlueView(int width, int height, int ivMarginTop, int txtMarginTop){
        RelativeLayout.LayoutParams linearParams = (RelativeLayout.LayoutParams) mIvNoBlue.getLayoutParams();
        linearParams.width = width;
        linearParams.height = height;
        linearParams.topMargin = ivMarginTop;
        mIvNoBlue.setLayoutParams(linearParams);

        RelativeLayout.LayoutParams desLinearParams = (RelativeLayout.LayoutParams) noBlueTv.getLayoutParams();
        desLinearParams.topMargin = txtMarginTop;
        noBlueTv.setLayoutParams(desLinearParams);
    }

    private void resetNoUSBView(int width, int height, int ivMarginTop, int txtMarginTop){
        RelativeLayout.LayoutParams linearParams = (RelativeLayout.LayoutParams) mIvNoUsb.getLayoutParams();
        linearParams.width = width;
        linearParams.height = height;
        linearParams.topMargin = ivMarginTop;
        mIvNoUsb.setLayoutParams(linearParams);

        RelativeLayout.LayoutParams desLinearParams = (RelativeLayout.LayoutParams) noUSBTv.getLayoutParams();
        desLinearParams.topMargin = txtMarginTop;
        noUSBTv.setLayoutParams(desLinearParams);
    }

    private void resetNoInternetView(int width, int height, int ivMarginTop, int txtMarginTop){
        RelativeLayout.LayoutParams linearParams = (RelativeLayout.LayoutParams) mIvNoInternet.getLayoutParams();
        linearParams.width = width;
        linearParams.height = height;
        linearParams.topMargin = ivMarginTop;
        mIvNoInternet.setLayoutParams(linearParams);

        RelativeLayout.LayoutParams desLinearParams = (RelativeLayout.LayoutParams) noInternetTv.getLayoutParams();
        desLinearParams.topMargin = txtMarginTop;
        noInternetTv.setLayoutParams(desLinearParams);

    }

    private void resetUSBBreakView(int width, int height, int ivMarginTop, int txtMarginTop){
        RelativeLayout.LayoutParams linearParams = (RelativeLayout.LayoutParams) mIvUsbBreak.getLayoutParams();
        linearParams.width = width;
        linearParams.height = height;
        linearParams.topMargin = ivMarginTop;
        mIvUsbBreak.setLayoutParams(linearParams);

        RelativeLayout.LayoutParams desLinearParams = (RelativeLayout.LayoutParams) usbBreakTv.getLayoutParams();
        desLinearParams.topMargin = txtMarginTop;
        usbBreakTv.setLayoutParams(desLinearParams);
    }

    private void resetBlueBreakView(int width, int height, int ivMarginTop, int txtMarginTop){
        RelativeLayout.LayoutParams linearParams = (RelativeLayout.LayoutParams) mIvBlueBreak.getLayoutParams();
        linearParams.width = width;
        linearParams.height = height;
        linearParams.topMargin = ivMarginTop;
        mIvBlueBreak.setLayoutParams(linearParams);

        RelativeLayout.LayoutParams desLinearParams = (RelativeLayout.LayoutParams) blueBreakTv.getLayoutParams();
        desLinearParams.topMargin = txtMarginTop;
        blueBreakTv.setLayoutParams(desLinearParams);
    }

    private void resetLayout(int width, int height, int topMargin, int desTopMargin, int titleWidth) {
        LinearLayout.LayoutParams linearParams = (LinearLayout.LayoutParams) mProgressBar.getLayoutParams();
        linearParams.width = width;
        linearParams.height = height;
        if(topMargin > 0) linearParams.topMargin = topMargin;
        mProgressBar.setLayoutParams(linearParams);

        LinearLayout.LayoutParams desLinearParams = (LinearLayout.LayoutParams) desCoverRl.getLayoutParams();
        if(desTopMargin > 0) desLinearParams.topMargin = desTopMargin;
        desCoverRl.setLayoutParams(desLinearParams);

        if(titleWidth > 0){
            LinearLayout.LayoutParams titleLinearParams = (LinearLayout.LayoutParams) tvTitle.getLayoutParams();
            titleLinearParams.width = titleWidth;
            tvTitle.setLayoutParams(titleLinearParams);
        }
    }

    private void showMediaView(View view) {
        mMusicTypeLayout.setVisibility(GONE);
        mMusicDescLayout.setVisibility(GONE);
        mNoUsbLayout.setVisibility(GONE);
        mNoBlueLayout.setVisibility(GONE);
        mNoInternetLayout.setVisibility(GONE);
        mUsbBreakLayout.setVisibility(GONE);
        mBlueBreakLayout.setVisibility(GONE);
        mNoMusicContainer.setVisibility(GONE);
        view.setVisibility(VISIBLE);
        if (mMediaViewManager.isLocalRadio()) {
            mProgressBar.setVisibility(GONE);
        } else {
            mProgressBar.setVisibility(VISIBLE);
        }
        if (view == mMusicTypeLayout) {
            mMediaTypeBg.setVisibility(VISIBLE);
        } else {
            mMediaTypeBg.setVisibility(GONE);
        }
    }

    private boolean checkNum(String number) {
        String rex = "^[0-9]\\d*(\\.\\d+)?$";
        Pattern p = Pattern.compile(rex);
        Matcher m = p.matcher(number);
        return m.find();
    }

    public interface OnMediaListener {
        void onConferMediaSimpleText(String text);
    }

    public class ResetAudioInfoRunnable implements Runnable {
        public final AudioInfo audioInfo;
        private boolean hasChangedInfo;
        ResetAudioInfoRunnable(AudioInfo audioInfo, boolean hasChangedInfo) {
            this.audioInfo = audioInfo;
            this.hasChangedInfo = hasChangedInfo;
        }

        @Override
        public void run() {
            if (mMediaViewManager.isDetail()) {
                //处理蓝牙音乐暂停时回调数据有延迟
                if (audioInfo.getPlayState() != AudioConstants.AudioStatus.PLAYING && audioInfo.getPlayState() != AudioConstants.AudioStatus.LOADING
                        && (audioInfo.getAudioType() == MUSIC_LOCAL_BT
                        || audioInfo.getAudioType() == MUSIC_ONLINE_KUWO
                        || audioInfo.getAudioType() == MUSIC_KUWO_RADIO)) {
                    return;
                }
                showMediaView(mMusicDescLayout);
                syncAudioType(audioInfo);
                initMusicDesc(hasChangedInfo);
            }
            if (mMediaViewManager.isMain()) {
                //酷我音乐和蓝牙音乐有异步问题，所以必需在播放状态才进行切换,防止来回跳动
                if (audioInfo.getAudioType() == MUSIC_LOCAL_BT
                        || audioInfo.getAudioType() == MUSIC_ONLINE_KUWO
                        || audioInfo.getAudioType() == MUSIC_KUWO_RADIO) {
                    if (audioInfo.getPlayState() == AudioConstants.AudioStatus.PLAYING
                            || audioInfo.getPlayState() == AudioConstants.AudioStatus.LOADING) {
                        syncAudioType(audioInfo);
                        notifyChangeMusic();
                    }
                } else {
                    syncAudioType(audioInfo);
                    notifyChangeMusic();
                }
            }
            if (mOnMediaListener != null) {
                mOnMediaListener.onConferMediaSimpleText(audioInfo.getTitle());
            }
        }

    }

    private boolean isBTFMAMMedia(){
        if(mAudioInfo != null && (mAudioInfo.getAudioType() == XTING_LOCAL_FM
                || mAudioInfo.getAudioType() == MUSIC_LOCAL_BT
                || mAudioInfo.getAudioType() == XTING_LOCAL_AM))
            return true;
        return false;
    }

    public int currentLevel(){
        if(mMediaViewManager == null) return -1;
        return mMediaViewManager.getCurrentStep();
    }

}

package com.xiaoma.music.common.manager;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.support.annotation.RequiresApi;

import com.xiaoma.carlib.wheelcontrol.OnWheelKeyListener;
import com.xiaoma.carlib.wheelcontrol.WheelKeyEvent;
import com.xiaoma.carlib.wheelcontrol.XmWheelManager;
import com.xiaoma.component.dispatch.SimulateWheelDispatch;
import com.xiaoma.config.ConfigManager;
import com.xiaoma.music.BTMusicFactory;
import com.xiaoma.music.OnlineMusicFactory;
import com.xiaoma.music.R;
import com.xiaoma.music.UsbMusicFactory;
import com.xiaoma.music.common.audiosource.AudioSource;
import com.xiaoma.music.common.audiosource.AudioSourceManager;
import com.xiaoma.music.common.constant.MusicConstants;
import com.xiaoma.music.common.util.MusicFastPlayController;
import com.xiaoma.music.kuwo.model.XMMusicList;
import com.xiaoma.music.manager.BTControlManager;
import com.xiaoma.thread.ThreadDispatcher;
import com.xiaoma.ui.toast.XMToast;
import com.xiaoma.utils.log.KLog;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import cn.kuwo.mod.playcontrol.PlayMode;

/**
 * @author LKF
 * @date 2018-12-24
 * 焦点管理器辅助类,对Audio焦点事件进行相应处理,同时对AudioSource进行处理
 */
public class AudioFocusHelper {
    private static final String TAG = AudioFocusHelper.class.getSimpleName();
    private static boolean IS_OVER_ANDROID_O = Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O;
    private static final int AUDIO_FOCUS_GAIN_FLAG = AudioManager.AUDIOFOCUS_GAIN;
    private static final int AUDIO_STREAM_TYPE = AudioManager.STREAM_MUSIC;
    private static final float DUCK_VOLUME_SCALE = 0.25f;
    private final int[] KEYCODE = new int[]{WheelKeyEvent.KEYCODE_WHEEL_SEEK_SUB, WheelKeyEvent.KEYCODE_WHEEL_SEEK_ADD};
    private final Context mContext;
    private OnWheelKeyListener carLibListener = new OnWheelKeyListener.Stub() {
        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public boolean onKeyEvent(int keyAction, int keyCode) {
            List<Integer> keyCodeList = Arrays.stream(KEYCODE).boxed().collect(Collectors.toList());
            if (keyAction == WheelKeyEvent.ACTION_CLICK) {
                if (keyCodeList.contains(keyCode)) {
                    switch (keyCode) {
                        case WheelKeyEvent.KEYCODE_WHEEL_SEEK_ADD:
                            //方控上一首
                            previous();
                            break;
                        case WheelKeyEvent.KEYCODE_WHEEL_SEEK_SUB:
                            //方控下一首
                            next();
                            break;
                    }
                }
                return true;
            } else if (keyAction == WheelKeyEvent.ACTION_LONG_PRESS) {
                if (AudioSourceManager.getInstance().getCurrAudioSource() == AudioSource.BLUETOOTH_MUSIC) {
                    //蓝牙无快进/快退
                    return false;
                }

                if (keyCodeList.contains(keyCode)) {
                    KLog.d("WheelEventTag", "长按触发快进快退");
                    switch (keyCode) {
                        case WheelKeyEvent.KEYCODE_WHEEL_SEEK_ADD:
                            MusicFastPlayController.getInstance().starter(false);
                            break;
                        case WheelKeyEvent.KEYCODE_WHEEL_SEEK_SUB:
                            MusicFastPlayController.getInstance().starter(true);
                            break;
                    }
                }
                return true;
            } else if (keyAction == WheelKeyEvent.ACTION_RELEASE) {
                ThreadDispatcher.getDispatcher().postOnMain(()
                        -> MusicFastPlayController.getInstance().closeStarter());
                return true;
            }
            return false;
        }

        @Override
        public String getPackageName() throws RemoteException {
            return mContext.getPackageName();
        }
    };

    private AudioManager mAudioManager;

    private AudioManager.OnAudioFocusChangeListener mFocusChangeListener;
    private AudioFocusRequest mAudioFocusRequest;

    private  AudioManager.OnAudioFocusChangeListener mUsbFocusChangeListener;
    private AudioFocusRequest mUsbAudioFocusRequest;

    private BtAudioFocusChangeListener mBtFocusChangeListener;
    private AudioFocusRequest mBtAudioFocusRequest;

    private boolean mHasAudioFocus;
    private PlayerProxy mPlayerProxy;

    private boolean isAudioFocusLossTransient;

    public boolean hasAudioFocus() {
        return mHasAudioFocus;
    }

    public boolean requestAudioFocus(int audioSource) {
        if (mFocusChangeListener == null) {
            mFocusChangeListener = new MusicAudioFocusChangeListener();
        }
        int rlt;
        if (IS_OVER_ANDROID_O) {
            if (mAudioFocusRequest == null) {
                Bundle bundle = new Bundle();
                bundle.putInt(MusicConstants.AUDIO_FOCUS_SOURCE, audioSource);
                AudioAttributes attr = new AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setLegacyStreamType(AUDIO_STREAM_TYPE)
                        .addBundle(bundle)
                        .build();
                mAudioFocusRequest = new AudioFocusRequest.Builder(AUDIO_FOCUS_GAIN_FLAG)
                        .setAudioAttributes(attr)
                        .setAcceptsDelayedFocusGain(false)
                        .setOnAudioFocusChangeListener(mFocusChangeListener)
                        .build();
            }
            rlt = mAudioManager.requestAudioFocus(mAudioFocusRequest);
        } else {
            rlt = mAudioManager.requestAudioFocus(mFocusChangeListener, AUDIO_STREAM_TYPE, AUDIO_FOCUS_GAIN_FLAG);
        }
        mHasAudioFocus = AudioManager.AUDIOFOCUS_REQUEST_GRANTED == rlt;
        if (mHasAudioFocus) {
            registerCarLib();
        }
        KLog.i(TAG, String.format("requestAudioFocus() rlt: %s", rlt));
        return mHasAudioFocus;
    }

    public boolean requestUsbAudioFocus(int audioSource) {
        if (mUsbFocusChangeListener == null) {
            mUsbFocusChangeListener = new UsbAudioFocusChangeListener();
        }
        int rlt;
        if (IS_OVER_ANDROID_O) {
            if (mUsbAudioFocusRequest == null) {
                Bundle bundle = new Bundle();
                bundle.putInt(MusicConstants.AUDIO_FOCUS_SOURCE, audioSource);
                AudioAttributes attr = new AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setLegacyStreamType(AUDIO_STREAM_TYPE)
                        .addBundle(bundle)
                        .build();
                mUsbAudioFocusRequest = new AudioFocusRequest.Builder(AUDIO_FOCUS_GAIN_FLAG)
                        .setAudioAttributes(attr)
                        .setAcceptsDelayedFocusGain(false)
                        .setOnAudioFocusChangeListener(mUsbFocusChangeListener)
                        .build();
            }
            rlt = mAudioManager.requestAudioFocus(mUsbAudioFocusRequest);
        } else {
            rlt = mAudioManager.requestAudioFocus(mUsbFocusChangeListener, AUDIO_STREAM_TYPE, AUDIO_FOCUS_GAIN_FLAG);
        }
        mHasAudioFocus = AudioManager.AUDIOFOCUS_REQUEST_GRANTED == rlt;
        if (mHasAudioFocus) {
            registerCarLib();
        }
        KLog.i(TAG, String.format("requestAudioFocus() rlt: %s", rlt));
        return mHasAudioFocus;
    }

    public boolean requestAudioFocus() {
        if (mFocusChangeListener == null) {
            mFocusChangeListener = new MusicAudioFocusChangeListener();
        }
        int rlt;
        if (IS_OVER_ANDROID_O) {
            if (mAudioFocusRequest == null) {
                AudioAttributes attr = new AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setLegacyStreamType(AUDIO_STREAM_TYPE)
                        .build();
                mAudioFocusRequest = new AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN_TRANSIENT)
                        .setAudioAttributes(attr)
                        .setAcceptsDelayedFocusGain(false)
                        .setOnAudioFocusChangeListener(mFocusChangeListener)
                        .build();
            }
            rlt = mAudioManager.requestAudioFocus(mAudioFocusRequest);
        } else {
            rlt = mAudioManager.requestAudioFocus(mFocusChangeListener, AUDIO_STREAM_TYPE, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
        }
        mHasAudioFocus = AudioManager.AUDIOFOCUS_REQUEST_GRANTED == rlt;
        if (mPlayerProxy != null) {
            try {
                mPlayerProxy.setVolumeScale(1f);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        KLog.i(TAG, String.format("requestAudioFocus() rlt: %s", rlt));
        return mHasAudioFocus;
    }

    public boolean requestBluetoothMusicAudioFocus(int audioSource) {
        if (mBtFocusChangeListener == null) {
            mBtFocusChangeListener = new BtAudioFocusChangeListener();
        }
        int rlt;
        if (IS_OVER_ANDROID_O) {
            if (mBtAudioFocusRequest == null) {
                Bundle bundle = new Bundle();
                bundle.putInt(MusicConstants.AUDIO_FOCUS_SOURCE, audioSource);
                AudioAttributes attr = new AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setLegacyStreamType(AudioManager.STREAM_BLUETOOTH_MUSIC)
                        .addBundle(bundle)
                        .build();
                mBtAudioFocusRequest = new AudioFocusRequest.Builder(AUDIO_FOCUS_GAIN_FLAG)
                        .setAudioAttributes(attr)
                        .setAcceptsDelayedFocusGain(false)
                        .setOnAudioFocusChangeListener(mBtFocusChangeListener)
                        .build();
            }
            rlt = mAudioManager.requestAudioFocus(mBtAudioFocusRequest);
        } else {
            rlt = mAudioManager.requestAudioFocus(mBtFocusChangeListener, AudioManager.STREAM_BLUETOOTH_MUSIC, AUDIO_FOCUS_GAIN_FLAG);
        }
        mHasAudioFocus = AudioManager.AUDIOFOCUS_REQUEST_GRANTED == rlt;
        if (mHasAudioFocus) {
            BTControlManager.getInstance().startRender();
            registerCarLib();
            if (mPlayerProxy != null) {
                try {
                    mPlayerProxy.setVolumeScale(1f);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        KLog.i(TAG, String.format("requestAudioFocus() rlt: %s", rlt));
        return mHasAudioFocus;
    }

    public void abandonAudioFocus() {
        int rlt = Integer.MIN_VALUE;
        try {
            if (IS_OVER_ANDROID_O) {
                if (mAudioFocusRequest != null) {
                    rlt = mAudioManager.abandonAudioFocusRequest(mAudioFocusRequest);
                }
            } else {
                if (mFocusChangeListener != null) {
                    rlt = mAudioManager.abandonAudioFocus(mFocusChangeListener);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mAudioFocusRequest = null;
            mFocusChangeListener = null;
        }
        mHasAudioFocus = false;
        KLog.i(TAG, String.format("abandonAudioFocus() rlt: %s", rlt));
    }

    public void abandonUsbAudioFocus() {
        int rlt = Integer.MIN_VALUE;
        try {
            if (IS_OVER_ANDROID_O) {
                if (mUsbAudioFocusRequest != null) {
                    rlt = mAudioManager.abandonAudioFocusRequest(mUsbAudioFocusRequest);
                }
            } else {
                if (mUsbFocusChangeListener != null) {
                    rlt = mAudioManager.abandonAudioFocus(mUsbFocusChangeListener);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mUsbAudioFocusRequest = null;
            mUsbFocusChangeListener = null;
        }
        mHasAudioFocus = false;
        KLog.i(TAG, String.format("abandonAudioFocus() rlt: %s", rlt));
    }

    public void abandonBtAudioFocus() {
        int rlt = Integer.MIN_VALUE;
        try {
            if (IS_OVER_ANDROID_O) {
                if (mBtAudioFocusRequest != null) {
                    rlt = mAudioManager.abandonAudioFocusRequest(mBtAudioFocusRequest);
                }
            } else {
                if (mBtFocusChangeListener != null) {
                    rlt = mAudioManager.abandonAudioFocus(mBtFocusChangeListener);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mBtAudioFocusRequest = null;
            mBtFocusChangeListener = null;
            BTControlManager.getInstance().pauseRender();
        }
        mHasAudioFocus = false;
        KLog.i(TAG, String.format("abandonBtAudioFocus() rlt: %s", rlt));
    }

    private class MusicAudioFocusChangeListener implements AudioManager.OnAudioFocusChangeListener {

        @Override
        public void onAudioFocusChange(int focusChange) {
            KLog.i(TAG, String.format("onAudioFocusChange( focusChange: %s )", focusChange));
            switch (focusChange) {
                case AudioManager.AUDIOFOCUS_GAIN:
                    mHasAudioFocus = true;
                    // 重新得到焦点
                    if (mPlayerProxy != null) {
                        mPlayerProxy.setVolumeScale(1f);
                        if (!mPlayerProxy.isPlaying()) {
                            boolean success = requestAudioFocus(AudioSource.ONLINE_MUSIC);
                            if (success) {
                                mPlayerProxy.continuePlayWithoutFocus();
                            }
                        }
                        registerCarLib();
                    }
                    break;
                case AudioManager.AUDIOFOCUS_LOSS:
                    mHasAudioFocus = false;
                    if (mPlayerProxy != null) {
                        mPlayerProxy.pauseWithoutFocus();
                        release();
                    }
                    abandonAudioFocus();// 永久失去焦点,直接释放焦点
                    break;
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                    mHasAudioFocus = false;
                    // 短暂失去焦点
                    if (mPlayerProxy != null) {
                        mPlayerProxy.pauseWithoutFocus();
                        release();
                    }
                    break;
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                    mHasAudioFocus = false;
                    // 以低音量方式进行
                    if (mPlayerProxy != null) {
//                        mPlayerProxy.setVolumeScale(DUCK_VOLUME_SCALE);
                        release();
                    }
                    break;
            }
        }
    }

    private class UsbAudioFocusChangeListener implements AudioManager.OnAudioFocusChangeListener {

        @Override
        public void onAudioFocusChange(int focusChange) {
            KLog.i(TAG, String.format("onAudioFocusChange( focusChange: %s )", focusChange));
            switch (focusChange) {
                case AudioManager.AUDIOFOCUS_GAIN:
                    mHasAudioFocus = true;
                    // 重新得到焦点
                    if (mPlayerProxy != null) {
                        mPlayerProxy.setVolumeScale(1f);
                        if (!mPlayerProxy.isPlaying()) {
                            boolean success = requestUsbAudioFocus(AudioSource.USB_MUSIC);
                            if (success) {
                                mPlayerProxy.continuePlayWithoutFocus();
                            }
                        }
                        registerCarLib();
                    }
                    break;
                case AudioManager.AUDIOFOCUS_LOSS:
                    mHasAudioFocus = false;
                    if (mPlayerProxy != null) {
                        mPlayerProxy.pauseWithoutFocus();
                        release();
                    }
                    abandonUsbAudioFocus();// 永久失去焦点,直接释放焦点
                    break;
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                    mHasAudioFocus = false;
                    // 短暂失去焦点
                    if (mPlayerProxy != null) {
                        mPlayerProxy.pauseWithoutFocus();
                        release();
                    }
                    break;
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                    mHasAudioFocus = false;
                    // 以低音量方式进行
                    if (mPlayerProxy != null) {
//                        mPlayerProxy.setVolumeScale(DUCK_VOLUME_SCALE);
                        release();
                    }
                    break;
            }
        }
    }

    private void registerCarLib() {
        XmWheelManager.getInstance().register(carLibListener, KEYCODE);
    }

    public void release() {
        XmWheelManager.getInstance().unregister(carLibListener);
    }

    private void previous() {
        switch (AudioSourceManager.getInstance().getCurrAudioSource()) {
            case AudioSource.ONLINE_MUSIC:
                int playMode = OnlineMusicFactory.getKWPlayer().getPlayMode();
                int index = OnlineMusicFactory.getKWPlayer().getNowPlayMusicIndex();
                if (PlayMode.MODE_ALL_ORDER == playMode && index == 0) {
                    XMToast.showToast(mContext, mContext.getString(R.string.already_first));
                    return;
                }
                OnlineMusicFactory.getKWPlayer().playPre();
                XMToast.showToast(mContext, mContext.getString(R.string.have_to_pre));
                break;
            case AudioSource.USB_MUSIC:
                boolean playPre = UsbMusicFactory.getUsbPlayerListProxy().playPre();
                if (playPre) {
                    XMToast.showToast(mContext, mContext.getString(R.string.have_to_pre));
                }
                break;
            case AudioSource.BLUETOOTH_MUSIC:
                BTMusicFactory.getBTMusicControl().preMusic();
                XMToast.showToast(mContext, mContext.getString(R.string.have_to_pre));
                break;
        }

    }

    private void next() {
        switch (AudioSourceManager.getInstance().getCurrAudioSource()) {
            case AudioSource.ONLINE_MUSIC:
                int playMode = OnlineMusicFactory.getKWPlayer().getPlayMode();
                int index = OnlineMusicFactory.getKWPlayer().getNowPlayMusicIndex();
                XMMusicList nowPlayingList = OnlineMusicFactory.getKWPlayer().getNowPlayingList();
                if (nowPlayingList == null) {
                    return;
                }
                int size = nowPlayingList.toList().size();
                if (PlayMode.MODE_ALL_ORDER == playMode && index == size - 1) {
                    XMToast.showToast(mContext, mContext.getString(R.string.already_last));
                    return;
                }
                OnlineMusicFactory.getKWPlayer().playNext();
                XMToast.showToast(mContext, mContext.getString(R.string.have_to_next));
                break;
            case AudioSource.USB_MUSIC:
                final boolean playNext = UsbMusicFactory.getUsbPlayerListProxy().playNext();
                if (playNext) {
                    XMToast.showToast(mContext, mContext.getString(R.string.have_to_next));
                }
                break;
            case AudioSource.BLUETOOTH_MUSIC:
                BTMusicFactory.getBTMusicControl().nextMusic();
                XMToast.showToast(mContext, mContext.getString(R.string.have_to_next));
                break;
        }

    }

    private void switchPlayPause() {
        switch (AudioSourceManager.getInstance().getCurrAudioSource()) {
            case AudioSource.ONLINE_MUSIC:
                OnlineMusicFactory.getKWPlayer().togglePauseAndPlay();
                break;
            case AudioSource.USB_MUSIC:
                UsbMusicFactory.getUsbPlayerProxy().switchPlayPause();
                break;
            case AudioSource.BLUETOOTH_MUSIC:
                BTMusicFactory.getBTMusicControl().switchPlay();
                break;
        }
    }

    private class BtAudioFocusChangeListener implements AudioManager.OnAudioFocusChangeListener {

        @Override
        public void onAudioFocusChange(int focusChange) {
            KLog.i(TAG, String.format("onAudioFocusChange( focusChange: %s )", focusChange));
            switch (focusChange) {
                case AudioManager.AUDIOFOCUS_GAIN:
                    mHasAudioFocus = true;
                    if (mPlayerProxy != null) {
                        mPlayerProxy.setVolumeScale(1f);
                        boolean success = requestBluetoothMusicAudioFocus(AudioSource.BLUETOOTH_MUSIC);
                        if (success) {
                            mPlayerProxy.continuePlayWithoutFocus();
                        }
                        registerCarLib();
                        BTControlManager.getInstance().startRender();
                    }
                    break;
                case AudioManager.AUDIOFOCUS_LOSS:
                    mHasAudioFocus = false;
                    if (mPlayerProxy != null) {
                        mPlayerProxy.pauseWithoutFocus();
                        release();
                        BTControlManager.getInstance().pauseRender();
                    }
                    abandonBtAudioFocus();// 永久失去焦点,直接释放焦点
                    break;
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                    mHasAudioFocus = false;
                    // 短暂失去焦点
                    if (mPlayerProxy != null) {
                        mPlayerProxy.pauseWithoutFocus();
                        release();
                        BTControlManager.getInstance().pauseRender();
                    }
                    break;
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                    mHasAudioFocus = false;
                    // 以低音量方式进行
                    if (mPlayerProxy != null) {
//                        mPlayerProxy.setVolumeScale(DUCK_VOLUME_SCALE);
                        release();
//                        BTControlManager.getInstance().pauseRender();
                    }
                    break;
            }
        }
    }

    public interface PlayerProxy {
        boolean isPlaying();

        /**
         * 继续播放,但是不要申请焦点
         */
        void continuePlayWithoutFocus();

        /**
         * 暂停播放,但是不要放弃焦点
         */
        void pauseWithoutFocus();

        /**
         * 设置当前音量的百分比
         */
        void setVolumeScale(float scale);
    }

    public AudioFocusHelper(Context context, PlayerProxy playerProxy) {
        mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        mPlayerProxy = playerProxy;
        mContext = context;
        initSimulateWheel();
    }

    private int curKey = -1;
    private Handler handler;
    private Runnable nextLongPressTask = () -> {
        try {
            carLibListener.onKeyEvent(WheelKeyEvent.ACTION_LONG_PRESS, WheelKeyEvent.KEYCODE_WHEEL_SEEK_SUB);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    };
    private Runnable previousLongPressTask = () -> {
        try {
            carLibListener.onKeyEvent(WheelKeyEvent.ACTION_LONG_PRESS, WheelKeyEvent.KEYCODE_WHEEL_SEEK_ADD);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    };
    private SimulateWheelDispatch.OnWheelEvent event = new SimulateWheelDispatch.OnWheelEvent() {
        @Override
        public boolean onNextDown() {
            try {
                synchronized (AudioFocusHelper.class) {
                    if (curKey != -1) {
                        return false;
                    }
                    curKey = WheelKeyEvent.KEYCODE_WHEEL_SEEK_SUB;
                    getHandler().postDelayed(nextLongPressTask, 1500);
                    carLibListener.onKeyEvent(WheelKeyEvent.ACTION_PRESS, WheelKeyEvent.KEYCODE_WHEEL_SEEK_SUB);
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            return false;
        }

        @Override
        public boolean onNextUp() {
            try {
                if (curKey != WheelKeyEvent.KEYCODE_WHEEL_SEEK_SUB) {
                    return false;
                }
                curKey = -1;
                getHandler().removeCallbacks(nextLongPressTask);
                carLibListener.onKeyEvent(WheelKeyEvent.ACTION_RELEASE, WheelKeyEvent.KEYCODE_WHEEL_SEEK_SUB);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            return false;
        }

        @Override
        public boolean onPreviousDown() {
            try {
                if (curKey != -1) {
                    return false;
                }
                curKey = WheelKeyEvent.KEYCODE_WHEEL_SEEK_ADD;
                getHandler().postDelayed(previousLongPressTask, 1500);
                carLibListener.onKeyEvent(WheelKeyEvent.ACTION_PRESS, WheelKeyEvent.KEYCODE_WHEEL_SEEK_ADD);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            return false;
        }

        @Override
        public boolean onPreviousUp() {
            try {
                if (curKey != WheelKeyEvent.KEYCODE_WHEEL_SEEK_ADD) {
                    return false;
                }
                curKey = -1;
                getHandler().removeCallbacks(previousLongPressTask);
                carLibListener.onKeyEvent(WheelKeyEvent.ACTION_RELEASE, WheelKeyEvent.KEYCODE_WHEEL_SEEK_ADD);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            return false;
        }
    };

    private Handler getHandler() {
        return handler == null ? handler = new Handler() : handler;
    }

    private void initSimulateWheel() {
        if (!ConfigManager.ApkConfig.isDebug()) {
            return;
        }
        if (ConfigManager.ApkConfig.isCarPlatform()) {
            return;
        }
        SimulateWheelDispatch.getInstance().setEventListener(event); // 音乐
    }

    public boolean isAudioFocusLossTransient() {
        return true;
    }

}
package com.xiaoma.player;

import android.content.Context;
import android.os.RemoteException;
import android.util.Log;

import com.xiaoma.player.PlayerConstants.AudioOption;
import com.xiaoma.player.PlayerConstants.AudioState;
import com.xiaoma.player.PlayerConstants.AudioType;

import java.util.HashSet;

/**
 * @author KY
 * @date 2018/11/8
 */
public class PlayerManagerHelper {
    private static final String TAG = PlayerManagerHelper.class.getSimpleName() + "_TAG";
    private static PlayerManagerHelper instance;
    private AudioInfo mCurrentAudioInfo;
    private ProgressInfo mCurrentProgressInfo;
    private IAudio.Stub mAudioStub;
    private boolean isPlayerPrepared;
    private boolean hasFocus;
    private boolean mFavorited;
    @AudioState
    private int mCurrentAudioStatus = PlayerConstants.AudioStatus.STOPPED;
    private AudioStatusChangeListener mAudioStatusChangeListener;
    private HashSet<OnControlListener> mOnControlListeners = new HashSet<>();

    public interface OnControlListener {
        /**
         * launcher 触发的控制回调
         *
         * @param audioType 音频类型
         * @param option    控制动作
         * @see PlayerConstants.AudioTypes
         */
        void onControl(@AudioType int audioType, @AudioOption int option);
    }

    public static PlayerManagerHelper getInstance() {
        if (instance == null) {
            synchronized (PlayerManagerHelper.class) {
                if (instance == null) {
                    instance = new PlayerManagerHelper();
                }
            }
        }
        return instance;
    }

    public void setNewProgressInfo(ProgressInfo progressInfo) {
        try {
            mCurrentProgressInfo = progressInfo;
            if (!hasFocus && isPlayerPrepared
                    && mCurrentAudioStatus == PlayerConstants.AudioStatus.PLAYING) {
                PlayerManager.getInstance().setAudio(mAudioStub);
            }
            if (hasFocus && mAudioStatusChangeListener != null
                    && mAudioStatusChangeListener.asBinder().isBinderAlive()) {
                mAudioStatusChangeListener.onAudioProgressChanged(progressInfo);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setNewAudioInfo(AudioInfo audioInfo) {
        try {
            mCurrentAudioInfo = audioInfo;

            if (!hasFocus && isPlayerPrepared
                    && mCurrentAudioStatus == PlayerConstants.AudioStatus.PLAYING) {
                PlayerManager.getInstance().setAudio(mAudioStub);
            }
            if (hasFocus && mAudioStatusChangeListener != null
                    && mAudioStatusChangeListener.asBinder().isBinderAlive()) {
                mAudioStatusChangeListener.onAudioInfoChanged(audioInfo);
            }
        } catch (RemoteException e) {
            Log.e(TAG, "set new audio info exception occurred");
            e.printStackTrace();
        }
    }

    public void setNewAudioStatus(@AudioState int status) {
        try {
            mCurrentAudioStatus = status;
            if (!hasFocus && isPlayerPrepared
                    && status == PlayerConstants.AudioStatus.PLAYING) {
                PlayerManager.getInstance().setAudio(mAudioStub);
            }
            if (hasFocus && mAudioStatusChangeListener != null
                    && mAudioStatusChangeListener.asBinder().isBinderAlive()) {
                mAudioStatusChangeListener.onAudioStatusChanged(status);
            }
        } catch (RemoteException e) {
            Log.e(TAG, "set new audio status exception occurred");
            e.printStackTrace();
        }
    }

    private PlayerManagerHelper() {
        mAudioStub = new IAudio.Stub() {
            @Override
            public void control(int option) {
                for (OnControlListener mOnControlListener : mOnControlListeners) {
                    if (mOnControlListener != null) {
                        int audioType = mCurrentAudioInfo == null ? PlayerConstants.AudioTypes.NONE : mCurrentAudioInfo.getAudioType();
                        mOnControlListener.onControl(audioType, option);
                    }
                }
            }

            @Override
            public int getAudioStatus() {
                return mCurrentAudioStatus;
            }

            @Override
            public AudioInfo getAudioInfo() {
                return mCurrentAudioInfo == null ? new AudioInfo() : mCurrentAudioInfo;
            }

            @Override
            public ProgressInfo getProgressInfo() {
                return mCurrentProgressInfo != null ? mCurrentProgressInfo : new ProgressInfo();
            }

            @Override
            public void onAudioUpdate(IAudio cur) {
                hasFocus = false;
            }

            @Override
            public boolean getFavoriteStatus() throws RemoteException {
                return mFavorited;
            }

            @Override
            public void listenAudioStatus(AudioStatusChangeListener listener) {
                mAudioStatusChangeListener = listener;
                hasFocus = true;
            }
        };
    }

    public void addOnControlListener(OnControlListener onControlListener) {
        if (mOnControlListeners != null
                && onControlListener != null) {
            mOnControlListeners.add(onControlListener);
        }
    }

    public void removeOnControlListener(OnControlListener onControlListener) {
        if (mOnControlListeners != null
                && onControlListener != null) {
            mOnControlListeners.remove(onControlListener);
        }
    }

    public void init(Context context) {
        PlayerManager.getInstance().init(context);
        PlayerManager.getInstance().addOnPlayerServiceListener(new PlayerManager.OnPlayerPreparedListener() {
            @Override
            public void onPlayerPrepared() {
                isPlayerPrepared = true;
                if (mCurrentAudioStatus == PlayerConstants.AudioStatus.PLAYING) {
                    PlayerManager.getInstance().setAudio(mAudioStub);
                }
            }

            @Override
            public void onPlayerDead() {
                isPlayerPrepared = false;
                mAudioStatusChangeListener = null;
            }
        });
    }

    public void setNewAudioFavoriteStatus(boolean favorited) {
        try {
            mFavorited = favorited;
            if (!hasFocus && isPlayerPrepared
                    && mCurrentAudioStatus == PlayerConstants.AudioStatus.PLAYING) {
                PlayerManager.getInstance().setAudio(mAudioStub);
            }
            if (hasFocus && mAudioStatusChangeListener != null
                    && mAudioStatusChangeListener.asBinder().isBinderAlive()) {
                mAudioStatusChangeListener.onAudioFavoritStatusChanged(favorited);
            }
        } catch (RemoteException e) {
            Log.e(TAG, "set new audio status exception occurred");
            e.printStackTrace();
        }
    }
}

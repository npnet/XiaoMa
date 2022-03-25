package com.xiaoma.music.player.ui;

import android.app.Activity;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.xiaoma.component.base.VisibleFragment;
import com.xiaoma.component.nodejump.NodeConst;
import com.xiaoma.model.annotation.NormalOnClick;
import com.xiaoma.model.annotation.PageDescComponent;
import com.xiaoma.model.annotation.ResId;
import com.xiaoma.music.R;
import com.xiaoma.music.UsbMusicFactory;
import com.xiaoma.music.callback.OnUsbMusicChangedListener;
import com.xiaoma.music.common.audiosource.AudioSource;
import com.xiaoma.music.common.audiosource.AudioSourceListener;
import com.xiaoma.music.common.audiosource.AudioSourceManager;
import com.xiaoma.music.common.constant.EventConstants;
import com.xiaoma.music.common.model.PlayStatus;
import com.xiaoma.music.common.util.MusicFastPlayController;
import com.xiaoma.music.manager.IUsbMusic;
import com.xiaoma.music.manager.IUsbMusicList;
import com.xiaoma.music.manager.PlayerListManager;
import com.xiaoma.music.manager.SortUsbMusicManager;
import com.xiaoma.music.manager.UsbPlayerProxy;
import com.xiaoma.music.model.UsbMusic;
import com.xiaoma.music.player.model.PlayMode;
import com.xiaoma.music.player.model.UsbSort;
import com.xiaoma.music.player.view.UsbContentView;
import com.xiaoma.music.player.view.player.PlayerView;
import com.xiaoma.music.player.view.player.UsbPlayListWindow;
import com.xiaoma.music.player.vm.PlayerVM;
import com.xiaoma.music.player.vm.UsbPlayListVM;
import com.xiaoma.thread.ThreadDispatcher;
import com.xiaoma.ui.toast.XMToast;
import com.xiaoma.utils.receiver.UsbDetector;
import com.xiaoma.utils.tputils.TPUtils;
import com.xiaoma.vr.dispatch.annotation.Command;

import java.util.List;

/**
 * @author zs
 * @date 2018/11/23 0023.
 */
@PageDescComponent(EventConstants.PageDescribe.usbPlayFragment)
public class UsbPlayFragment extends VisibleFragment implements View.OnClickListener,
        PlayerView.OnOperationListener, PlayerListManager.IUsbPlayModeChangedListener,
        MusicFastPlayController.FastPlayProgressObserver {

    private UsbPlayListWindow mPlayListWindow;
    private ImageView mPlayModeIv;
    private PlayerView mPlayerView;
    private UsbContentView mContentView;
    private PlayerVM mPlayerVM;
    private final IUsbMusic mUsbMusic = UsbMusicFactory.getUsbPlayerProxy();
    private final IUsbMusicList mUsbMusicList = UsbMusicFactory.getUsbPlayerListProxy();

    public static UsbPlayFragment newInstance() {
        return new UsbPlayFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_play_usb, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bindView(view);
        initView();
        initData();
    }

    private void initData() {
        mPlayerVM = ViewModelProviders.of(this).get(PlayerVM.class);
        mPlayerVM.getUsbMusic().observe(this, new Observer<UsbMusic>() {
            @Override
            public void onChanged(@Nullable UsbMusic usbMusic) {
                if (usbMusic != null) {
                    mContentView.refreshView(usbMusic);
                }
            }
        });
        mPlayerVM.fetchUsbMusic();
        UsbMusicFactory.getUsbPlayerListProxy().addStateChangeListen(this);
        int mode = UsbMusicFactory.getUsbPlayerListProxy().getPlayMode();
        switch (mode) {
            case PlayMode.LIST_ORDER:
                mPlayModeIv.setImageResource(R.drawable.icon_order_selector);
                break;
            case PlayMode.LIST_LOOP:
                mPlayModeIv.setImageResource(R.drawable.icon_list_loop_selector);
                break;
            case PlayMode.SINGLE_LOOP:
                mPlayModeIv.setImageResource(R.drawable.icon_single_loop_selector);
                break;
            case PlayMode.RANDOM:
                mPlayModeIv.setImageResource(R.drawable.icon_random_selector);
                break;
            default:
                break;
        }
        AudioSourceManager.getInstance().addAudioSourceListener(new AudioSourceListener() {
            @Override
            public void onAudioSourceSwitch(int preAudioSource, int currAudioSource) {
                ThreadDispatcher.getDispatcher().runOnMain(new Runnable() {
                    @Override
                    public void run() {
                        if (currAudioSource != AudioSource.USB_MUSIC) {
                            try {
                                mPlayListWindow.dismiss();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });

            }
        });
    }

    private void bindView(View view) {
        mPlayModeIv = view.findViewById(R.id.activity_player_play_mode);
        mPlayerView = view.findViewById(R.id.fragment_player_view);
        mContentView = view.findViewById(R.id.usb_content_view);
        view.findViewById(R.id.activity_player_playlist).setOnClickListener(this);
        mPlayModeIv.setOnClickListener(this);
        mPlayerView.setOnOperationListener(this);
        mPlayListWindow = new UsbPlayListWindow(getActivity());
        UsbMusicFactory.getUsbPlayerProxy().addMusicChangeListener(usbPlayerListener);
//        UsbMusicFactory.getUsbPlayerListProxy().addUsbPlayTipsListener(this);
        UsbDetector.getInstance().addUsbDetectListener(usbDetectListener);
    }

    @Override
    public void onVisibleChange(boolean realVisible) {
        super.onVisibleChange(realVisible);
        if (realVisible) {
            MusicFastPlayController.getInstance().setFastPlayObserver(this);
        } else {
            MusicFastPlayController.getInstance().removeFastPlayObserver();
        }
    }

    @Override
    public boolean handleJump(String nextNode) {
        super.handleJump(nextNode);
        switch (nextNode) {
            case NodeConst.MUSIC.OPEN_PLAY_LIST:
                ThreadDispatcher.getDispatcher().postOnMainDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (!mPlayListWindow.isShowing()) {
                            getView().findViewById(R.id.activity_player_playlist).performClick();
                        }
                    }
                }, 100);
                return true;
            default:
                return false;
        }
    }

    @Override
    public String getThisNode() {
        return NodeConst.MUSIC.PLAYER_FRAGMENT;
    }

    @Command("关闭播放列表")
    public void closePlayerList() {
        ThreadDispatcher.getDispatcher().runOnMain(new Runnable() {
            @Override
            public void run() {
                if (mPlayListWindow != null && mPlayListWindow.isShowing()) {
                    mPlayListWindow.display(false);
                }
            }
        });
    }

    @Command("打开播放列表")
    public void openPlayerList() {
        ThreadDispatcher.getDispatcher().postOnMainDelayed(new Runnable() {
            @Override
            public void run() {
                if (!mPlayListWindow.isShowing()) {
                    getView().findViewById(R.id.activity_player_playlist).performClick();
                }
            }
        }, 100);
    }


    private void initView() {
        UsbMusic usbMusic = UsbMusicFactory.getUsbPlayerProxy().getCurrUsbMusic();
        mPlayerView.setUsbImageCover(usbMusic);
        initPlayStatus();
        List<UsbMusic> usbMusicList = UsbMusicFactory.getUsbPlayerListProxy().getUsbMusicList();
        SortUsbMusicManager.getInstance().setDefaultMusicList(usbMusicList);
    }

    private void initPlayStatus() {
        int playStatus = UsbPlayerProxy.getInstance().getPlayStatus();
        if (PlayStatus.PLAYING == playStatus) {
            mPlayerView.updatePlayStatus(PlayStatus.PLAYING);
            mContentView.updatePlayStatus(PlayStatus.PLAYING);
        } else {
            mPlayerView.updatePlayStatus(PlayStatus.PAUSE);
            mContentView.updatePlayStatus(PlayStatus.PAUSE);
        }
    }

    @Override
    public void musicOperate() {
        mUsbMusic.switchPlay(!mUsbMusic.isPlaying());
    }

    @Override
    public void nextMusic() {
        mUsbMusicList.playNext();
    }

    @Override
    public void preMusic() {
        mUsbMusicList.playPre();
    }

    @Override
    public void scrollUp() {
    }

    @NormalOnClick({EventConstants.NormalClick.playlist, EventConstants.NormalClick.switchMode})
    @ResId({R.id.activity_player_playlist, R.id.activity_player_play_mode})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.activity_player_playlist:
                fetchPlayList();
                break;
            case R.id.activity_player_play_mode:
                switchPlayMode();
                break;
            default:
                break;
        }
    }

    private void fetchPlayList() {
        UsbPlayListVM usbPlayListVM = ViewModelProviders.of(this).get(UsbPlayListVM.class);
        usbPlayListVM.getUsbPlayList().observe(this, new Observer<List<UsbMusic>>() {
            @Override
            public void onChanged(@Nullable List<UsbMusic> usbMusics) {
                mPlayListWindow.setData(usbMusics);
            }
        });
        usbPlayListVM.fetchUsbPlaylist();
        mPlayListWindow.display(true);
    }

    private void switchPlayMode() {
        int mode = UsbMusicFactory.getUsbPlayerListProxy().getPlayMode();
        switch (mode) {
            case PlayMode.LIST_ORDER:
                UsbMusicFactory.getUsbPlayerListProxy().setPlayMode(PlayMode.LIST_LOOP);
                mPlayModeIv.setImageResource(R.drawable.icon_list_loop_selector);
                XMToast.showToast(mContext, mContext.getResources().getString(R.string.play_mode_list_loop));
                break;
            case PlayMode.LIST_LOOP:
                UsbMusicFactory.getUsbPlayerListProxy().setPlayMode(PlayMode.SINGLE_LOOP);
                mPlayModeIv.setImageResource(R.drawable.icon_single_loop_selector);
                XMToast.showToast(mContext, mContext.getResources().getString(R.string.play_mode_single_loop));
                break;
            case PlayMode.SINGLE_LOOP:
                UsbMusicFactory.getUsbPlayerListProxy().setPlayMode(PlayMode.RANDOM);
                mPlayModeIv.setImageResource(R.drawable.icon_random_selector);
                XMToast.showToast(mContext, mContext.getResources().getString(R.string.play_mode_random));
                break;
            case PlayMode.RANDOM:
                UsbMusicFactory.getUsbPlayerListProxy().setPlayMode(PlayMode.LIST_ORDER);
                mPlayModeIv.setImageResource(R.drawable.icon_order_selector);
                XMToast.showToast(mContext, mContext.getResources().getString(R.string.play_mode_list_order));
                break;
            default:
                break;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        UsbMusicFactory.getUsbPlayerListProxy().removeStateChangeListen(this);
//        UsbMusicFactory.getUsbPlayerListProxy().removeUsbPlayTipsListener(this);
        UsbPlayerProxy.getInstance().removeMusicChangeListener(usbPlayerListener);
        UsbDetector.getInstance().removeUsbDetectListener(usbDetectListener);
        SortUsbMusicManager.getInstance().clear();
    }

    private UsbDetector.UsbDetectListener usbDetectListener = new UsbDetector.UsbDetectListener() {
        @Override
        public void noUsbMounted() {
            onUsbNotConn();
        }

        @Override
        public void inserted() {

        }

        @Override
        public void mounted(List<String> mountPaths) {
            TPUtils.put(mContext, UsbPlayListWindow.TP_PLAY_SORT, UsbSort.DEFAULT);
        }

        @Override
        public void mountError() {
            onUsbNotConn();
        }

        @Override
        public void removed() {
            onUsbNotConn();
        }

        private void onUsbNotConn() {
            Activity act = getActivity();
            if (act != null) {
                act.finish();
            }
        }
    };
    private OnUsbMusicChangedListener usbPlayerListener = new OnUsbMusicChangedListener() {
        @Override
        public void onBuffering(UsbMusic music) {
            ThreadDispatcher.getDispatcher().runOnMain(new Runnable() {
                @Override
                public void run() {
                    mPlayerView.setUsbImageCover(music);
                    mPlayerView.updatePlayStatus(PlayStatus.BUFFERING);
//                    mContentView.updatePlayStatus(PlayStatus.BUFFERING);
//                    mPlayerVM.setUsbMusic(music);
                }
            });
        }

        @Override
        public void onPlay(UsbMusic music) {
            ThreadDispatcher.getDispatcher().runOnMain(new Runnable() {
                @Override
                public void run() {
                    mPlayerView.setUsbImageCover(music);
                    mPlayerView.updatePlayStatus(PlayStatus.PLAYING);
                    mContentView.updatePlayStatus(PlayStatus.PLAYING);
                    mPlayerVM.setUsbMusic(music);
                }
            });
        }

        @Override
        public void onPause() {
            ThreadDispatcher.getDispatcher().runOnMain(new Runnable() {
                @Override
                public void run() {
                    mPlayerView.updatePlayStatus(PlayStatus.PAUSE);
                    mContentView.updatePlayStatus(PlayStatus.PAUSE);
                }
            });
        }

        @Override
        public void onProgressChange(final long progressInMs, long totalInMs) {
            ThreadDispatcher.getDispatcher().runOnMain(new Runnable() {
                @Override
                public void run() {
                    if (mPlayListWindow != null) {
                        mPlayListWindow.updateProgress(progressInMs, totalInMs);
                    }
                    mContentView.updateProgress(progressInMs);
                }
            });
        }

        @Override
        public void onPlayFailed(int errorCode) {
            ThreadDispatcher.getDispatcher().runOnMain(new Runnable() {
                @Override
                public void run() {
                    mPlayerView.updatePlayStatus(PlayStatus.PAUSE);
                    mContentView.updatePlayStatus(PlayStatus.PAUSE);
                    UsbMusic currUsbMusic = UsbMusicFactory.getUsbPlayerProxy().getCurrUsbMusic();
                    mPlayerView.setUsbImageCover(currUsbMusic);
                    mPlayerVM.setUsbMusic(currUsbMusic);
                    XMToast.toastException(mContext, R.string.file_loss_play_error);
                }
            });
        }

        @Override
        public void onPlayStop() {

        }

        @Override
        public void onCompletion() {
            ThreadDispatcher.getDispatcher().runOnMain(new Runnable() {
                @Override
                public void run() {
                    mPlayerView.updatePlayStatus(PlayStatus.PAUSE);
                    mContentView.updatePlayStatus(PlayStatus.PAUSE);
                }
            });
        }
    };

    @Override
    public void onPlayModeChanged(int mode) {
        switch (mode) {
            case PlayMode.LIST_ORDER:
                mPlayModeIv.setImageResource(R.drawable.icon_order_selector);
                break;
            case PlayMode.LIST_LOOP:
                mPlayModeIv.setImageResource(R.drawable.icon_list_loop_selector);
                break;
            case PlayMode.SINGLE_LOOP:
                mPlayModeIv.setImageResource(R.drawable.icon_single_loop_selector);
                break;
            case PlayMode.RANDOM:
                mPlayModeIv.setImageResource(R.drawable.icon_random_selector);
                break;
            default:
                break;
        }
    }

    @Override
    public void updatePlayProgress(int newTime) {
        if (mContentView != null) {
            mContentView.updateProgress(newTime);
        }
    }
}

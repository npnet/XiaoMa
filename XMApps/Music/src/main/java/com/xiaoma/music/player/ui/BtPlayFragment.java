package com.xiaoma.music.player.ui;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xiaoma.component.base.VisibleFragment;
import com.xiaoma.model.annotation.PageDescComponent;
import com.xiaoma.music.BTMusicFactory;
import com.xiaoma.music.R;
import com.xiaoma.music.callback.OnBTConnectStateChangeListener;
import com.xiaoma.music.callback.OnBTMusicChangeListener;
import com.xiaoma.music.common.constant.EventConstants;
import com.xiaoma.music.common.model.PlayStatus;
import com.xiaoma.music.manager.BTControlManager;
import com.xiaoma.music.model.BTMusic;
import com.xiaoma.music.player.view.BtContentView;
import com.xiaoma.music.player.view.player.PlayerView;
import com.xiaoma.music.player.vm.PlayerVM;

/**
 * @author zs
 * @date 2018/11/23 0023.
 */
@PageDescComponent(EventConstants.PageDescribe.btPlayFragment)
public class BtPlayFragment extends VisibleFragment implements PlayerView.OnOperationListener {

    private PlayerView playerView;
    private BtContentView contentView;

    public static BtPlayFragment newInstance() {
        return new BtPlayFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_play_bt, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        initData();
    }

    private void initData() {
        BTControlManager.getInstance().addBtMusicInfoChangeListener(btListener);
        BTControlManager.getInstance().addBtStateChangeListener(btListener);

        PlayerVM playerVM = ViewModelProviders.of(this).get(PlayerVM.class);
        playerVM.getBTMusic().observe(this, new Observer<BTMusic>() {
            @Override
            public void onChanged(@Nullable BTMusic btMusic) {
                if (btMusic != null) {
                    contentView.refreshView(btMusic);
                    playerView.setBTImageCover(btMusic);
                }
            }
        });
        playerVM.getBtStatus().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer btStatus) {
                if (btStatus != null)
                    playerView.updatePlayStatus(btStatus);
            }
        });
        playerVM.fetchBtMusic();
        playerVM.fetchBtStatus();
    }

    private void initView(View view) {
        playerView = view.findViewById(R.id.fragment_bt_player_view);
        contentView = view.findViewById(R.id.play_content_bt_iv);
        playerView.setOnOperationListener(this);
        initPlayStatus();
    }

    private void initPlayStatus() {
        int playStatus = BTMusicFactory.getBTMusicControl().getBtPlayStatus();
        if (PlayStatus.PLAYING == playStatus) {
            playerView.updatePlayStatus(PlayStatus.PLAYING);
            contentView.updatePlayStatus(PlayStatus.PLAYING);
        } else {
            playerView.updatePlayStatus(PlayStatus.PAUSE);
            contentView.updatePlayStatus(PlayStatus.PAUSE);
        }
    }

    @Override
    public void musicOperate() {
        BTControlManager.getInstance().switchPlay();
    }

    @Override
    public void nextMusic() {
        BTControlManager.getInstance().nextMusic();
    }

    @Override
    public void preMusic() {
        BTControlManager.getInstance().preMusic();
    }

    @Override
    public void scrollUp() {
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        BTControlManager.getInstance().removeBtMusicInfoChangeListener(btListener);
        BTControlManager.getInstance().removeBtStateChangeListener(btListener);
    }

    private BTListener btListener = new BTListener();

    private class BTListener implements OnBTMusicChangeListener, OnBTConnectStateChangeListener {
        @Override
        public void currentBtMusic(BTMusic music) {
            if (music != null) {
                contentView.refreshView(music);
                playerView.setBTImageCover(music);
            }
        }

        @Override
        public void onPlay() {
            playerView.updatePlayStatus(PlayStatus.PLAYING);
            contentView.updatePlayStatus(PlayStatus.PLAYING);
        }

        @Override
        public void onPause() {
            playerView.updatePlayStatus(PlayStatus.PAUSE);
            contentView.updatePlayStatus(PlayStatus.PAUSE);
        }

        @Override
        public void onProgressChange(long progressInMs, long totalInMs) {
            contentView.updateProgress(progressInMs);
        }

        @Override
        public void onPlayFailed(int errorCode) {
            playerView.updatePlayStatus(PlayStatus.STOP);
            contentView.updatePlayStatus(PlayStatus.STOP);
        }

        @Override
        public void onPlayStop() {
            playerView.updatePlayStatus(PlayStatus.STOP);
            contentView.updatePlayStatus(PlayStatus.STOP);
        }

        @Override
        public void onBTConnect() {

        }

        @Override
        public void onBTSinkConnected() {

        }

        @Override
        public void onBTDisconnect() {
            if (getActivity() != null)
                getActivity().finish();
        }

        @Override
        public void onBTSinkDisconnected() {
            if (getActivity() != null)
                getActivity().finish();
        }
    }
}

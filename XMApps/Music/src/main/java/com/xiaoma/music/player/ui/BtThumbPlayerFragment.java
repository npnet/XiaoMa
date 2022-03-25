package com.xiaoma.music.player.ui;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import com.xiaoma.model.annotation.PageDescComponent;
import com.xiaoma.music.BTMusicFactory;
import com.xiaoma.music.R;
import com.xiaoma.music.callback.OnBTConnectStateChangeListener;
import com.xiaoma.music.callback.OnBTMusicChangeListener;
import com.xiaoma.music.common.constant.EventConstants;
import com.xiaoma.music.common.model.PlayStatus;
import com.xiaoma.music.manager.BTControlManager;
import com.xiaoma.music.manager.IBTMusic;
import com.xiaoma.music.model.BTMusic;
import com.xiaoma.music.player.vm.BtThumbPlayerVM;

/**
 * Created by LKF on 2018-12-21 0021.
 */
@PageDescComponent(EventConstants.PageDescribe.btThumbPlayerFragment)
public class BtThumbPlayerFragment extends AbstractThumbPlayerFragment {
    private final IBTMusic mPlayer = BTMusicFactory.getBTMusicControl();
    private PlayerCallback mPlayerCallback;
    private BtThumbPlayerVM mVM;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mPlayerCallback = new PlayerCallback();
        mPlayer.addBtMusicInfoChangeListener(mPlayerCallback);
        getThumbPlayerVM().getBtMusic().setValue(mPlayer.getCurrBTMusic());
        getThumbPlayerVM().getPlayingStatus().setValue(mPlayer.isPlaying() ? PlayStatus.PLAYING : PlayStatus.PAUSE);
        BTMusicFactory.getBTMusicControl().addBtStateChangeListener(listener);
        hideCharge();
    }

    private OnBTConnectStateChangeListener listener = new OnBTConnectStateChangeListener() {
        @Override
        public void onBTConnect() {

        }

        @Override
        public void onBTDisconnect() {

        }

        @Override
        public void onBTSinkConnected() {

        }

        @Override
        public void onBTSinkDisconnected() {
            mVM.getPlayingTitle().setValue(getString(R.string.super_music));
            mVM.getPlayingPicBitmap().setValue(null);
        }
    };

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mPlayer.removeBtMusicInfoChangeListener(mPlayerCallback);
        BTMusicFactory.getBTMusicControl().removeBtStateChangeListener(listener);
    }

    @Override
    protected void skipToPlayer() {
        PlayerActivity.launch(mContext);
    }

    @Override
    protected void switchPlay() {
        if (BTControlManager.getInstance().getCurrBTMusic() != null) {
            String title = BTControlManager.getInstance().getCurrBTMusic().getTitle();
            if (title != null && title.equals(mContext.getString(R.string.have_no_song))) {
                showToastException(R.string.play_btmusic_failed_empty);
            }
        }
        BTMusicFactory.getBTMusicControl().switchPlay();
    }

    @Override
    protected BtThumbPlayerVM getThumbPlayerVM() {
        if (mVM == null) {
            mVM = ViewModelProviders.of(this).get(BtThumbPlayerVM.class);
            mVM.getBtMusic().observe(this, new Observer<BTMusic>() {
                @Override
                public void onChanged(@Nullable BTMusic music) {
                    if (music == null)
                        return;
                    mVM.getPlayingTitle().setValue(music.getTitle());
//                    mVM.getPlayingPicBitmap().setValue(music.getBitmap());
                }
            });
        }
        return mVM;
    }

    private class PlayerCallback implements OnBTMusicChangeListener {
        @Override
        public void currentBtMusic(BTMusic btMusic) {
            getThumbPlayerVM().getBtMusic().postValue(btMusic);
        }

        @Override
        public void onPlay() {
            getThumbPlayerVM().getPlayingStatus().postValue(PlayStatus.PLAYING);
        }

        @Override
        public void onPause() {
            getThumbPlayerVM().getPlayingStatus().postValue(PlayStatus.PAUSE);
        }

        @Override
        public void onProgressChange(long progressInMs, long totalInMs) {

        }

        @Override
        public void onPlayFailed(int errorCode) {
            getThumbPlayerVM().getPlayingStatus().postValue(PlayStatus.FAILED);
        }

        @Override
        public void onPlayStop() {
            getThumbPlayerVM().getPlayingStatus().postValue(PlayStatus.STOP);
        }
    }
}

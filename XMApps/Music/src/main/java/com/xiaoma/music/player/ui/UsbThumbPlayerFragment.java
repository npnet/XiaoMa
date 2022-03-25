package com.xiaoma.music.player.ui;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;

import com.xiaoma.model.annotation.PageDescComponent;
import com.xiaoma.music.R;
import com.xiaoma.music.UsbMusicFactory;
import com.xiaoma.music.callback.OnUsbMusicChangedListener;
import com.xiaoma.music.common.constant.EventBusTags;
import com.xiaoma.music.common.constant.EventConstants;
import com.xiaoma.music.common.model.DelayUpdateThumbEvent;
import com.xiaoma.music.common.model.PlayStatus;
import com.xiaoma.music.manager.IUsbMusic;
import com.xiaoma.music.manager.IUsbPlayTipsListener;
import com.xiaoma.music.model.UsbMusic;
import com.xiaoma.music.player.vm.UsbThumbPlayerVM;
import com.xiaoma.ui.toast.XMToast;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;

/**
 * Created by LKF on 2018-12-21 0021.
 */
@PageDescComponent(EventConstants.PageDescribe.usbThumbPlayerFragment)
public class UsbThumbPlayerFragment extends AbstractThumbPlayerFragment implements IUsbPlayTipsListener {
    private final IUsbMusic mPlayer = UsbMusicFactory.getUsbPlayerProxy();
    private OnUsbMusicChangedListener mUsbMusicChangedListener;
    private UsbThumbPlayerVM mVM;

    @Override
    protected void skipToPlayer() {
        PlayerActivity.launch(mContext);
    }

    @Override
    protected void switchPlay() {
        mPlayer.switchPlay(!mPlayer.isPlaying());
    }

    @Override
    protected UsbThumbPlayerVM getThumbPlayerVM() {
        if (mVM == null) {
            mVM = ViewModelProviders.of(this).get(UsbThumbPlayerVM.class);
            mVM.getUsbMusic().observe(this, new Observer<UsbMusic>() {
                @Override
                public void onChanged(@Nullable UsbMusic music) {
                    if (music == null)
                        return;
                    String title = music.getName();
                    if (TextUtils.isEmpty(title)) {
                        title = music.getDisplayName();
                    }
                    mVM.getPlayingTitle().setValue(title);
                    mVM.getPlayingPicModel().setValue(music);
                }
            });
        }
        return mVM;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mPlayer.addMusicChangeListener(mUsbMusicChangedListener = new PlayerCallback());
        getThumbPlayerVM().getPlayingStatus().setValue(mPlayer.getPlayStatus());
        getThumbPlayerVM().getUsbMusic().setValue(mPlayer.getCurrUsbMusic());
        hideCharge();
        UsbMusicFactory.getUsbPlayerListProxy().addUsbPlayTipsListener(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mPlayer.removeMusicChangeListener(mUsbMusicChangedListener);
        UsbMusicFactory.getUsbPlayerListProxy().removeUsbPlayTipsListener(this);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }


    @Subscriber(tag = EventBusTags.MOUTED_USB_DELAY_THUMB)
    public void delayUpdateThumbStatus(DelayUpdateThumbEvent delayUpdateThumbEvent) {
        getThumbPlayerVM().getPlayingStatus().postValue(PlayStatus.PAUSE);
    }

    private class PlayerCallback implements OnUsbMusicChangedListener {
        @Override
        public void onBuffering(UsbMusic usbMusic) {
            getThumbPlayerVM().getUsbMusic().postValue(usbMusic);
            getThumbPlayerVM().getPlayingStatus().postValue(PlayStatus.BUFFERING);
        }

        @Override
        public void onPlay(UsbMusic usbMusic) {
            getThumbPlayerVM().getUsbMusic().postValue(usbMusic);
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
            XMToast.toastException(mContext, R.string.file_loss_play_error);
        }

        @Override
        public void onPlayStop() {
            getThumbPlayerVM().getPlayingStatus().postValue(PlayStatus.STOP);
        }

        @Override
        public void onCompletion() {
            getThumbPlayerVM().getPlayingStatus().postValue(PlayStatus.STOP);
        }
    }

    @Override
    public void onFirstTips() {
        XMToast.showToast(mContext, mContext.getString(R.string.already_first));
    }

    @Override
    public void onLastTips() {
        XMToast.showToast(mContext, mContext.getString(R.string.already_last));
    }
}

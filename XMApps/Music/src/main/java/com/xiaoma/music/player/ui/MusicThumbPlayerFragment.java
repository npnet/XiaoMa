package com.xiaoma.music.player.ui;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;

import com.xiaoma.component.nodejump.NodeConst;
import com.xiaoma.guide.utils.GuideConstants;
import com.xiaoma.guide.utils.GuideDataHelper;
import com.xiaoma.model.annotation.PageDescComponent;
import com.xiaoma.music.OnlineMusicFactory;
import com.xiaoma.music.R;
import com.xiaoma.music.common.constant.EventBusTags;
import com.xiaoma.music.common.constant.EventConstants;
import com.xiaoma.music.common.model.PlayStatus;
import com.xiaoma.music.export.manager.AudioShareManager;
import com.xiaoma.music.kuwo.image.KwImage;
import com.xiaoma.music.kuwo.impl.IKuwoConstant;
import com.xiaoma.music.kuwo.listener.OnMusicChargeListener;
import com.xiaoma.music.kuwo.listener.OnPlayControlListener;
import com.xiaoma.music.kuwo.model.XMMusic;
import com.xiaoma.music.kuwo.observer.KuwoPlayControlObserver;
import com.xiaoma.music.kuwo.proxy.XMKWPlayerOperateProxy;
import com.xiaoma.music.player.vm.MusicThumbPlayerVM;

import org.simple.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import cn.kuwo.base.bean.Music;

/**
 * Created by LKF on 2018-12-21 0021.
 */
@PageDescComponent(EventConstants.PageDescribe.onlineThumbPlayerFragment)
public class MusicThumbPlayerFragment extends AbstractThumbPlayerFragment {
    private static final String TAG = MusicThumbPlayerFragment.class.getSimpleName();
    private PlayerCallback mPlayerCallback;
    private MusicThumbPlayerVM mVM;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        OnlineMusicFactory.getKWMessage().addPlayStateListener(mPlayerCallback = new PlayerCallback());
        XMKWPlayerOperateProxy player = OnlineMusicFactory.getKWPlayer();

        Music music = null;
        XMMusic xmMusic = player.getNowPlayingMusic();
        if (xmMusic != null) {
            music = xmMusic.getSDKBean();
        }
        getThumbPlayerVM().getMusic().setValue(music);

        getThumbPlayerVM().getPlayingStatus().setValue(player.getStatus());
        OnlineMusicFactory.getKWLogin().fetchVipInfo();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        OnlineMusicFactory.getKWMessage().removePlayStateListener(mPlayerCallback);
    }

    @Override
    protected void skipToPlayer() {
        PlayerActivity.launch(mContext);
        if (GuideDataHelper.shouldShowGuide(getContext(), GuideConstants.MUSIC_SHOWED, GuideConstants.MUSIC_GUIDE_FIRST, false)) {
            EventBus.getDefault().post("", EventBusTags.MINI_PLAYER_CLICKED);
        }
    }

    @Override
    protected void switchPlay() {
        XMKWPlayerOperateProxy player = OnlineMusicFactory.getKWPlayer();
        if (player.isPlaying()) {
            player.pause();
        } else {
            player.continuePlay();
        }
    }

    @Override
    protected MusicThumbPlayerVM getThumbPlayerVM() {
        if (mVM == null) {
            mVM = ViewModelProviders.of(this).get(MusicThumbPlayerVM.class);
            mVM.getMusic().observe(this, new Observer<Music>() {
                @Override
                public void onChanged(@Nullable Music music) {
                    if (music == null)
                        return;
                    try {
                        mVM.getPlayingTitle().setValue(music.name.replace("&nbsp;", " "));
                        mVM.getPlayingPicModel().setValue(new KwImage(music));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    AudioShareManager.getInstance().shareInitMusicInfo();
                }
            });
        }
        return mVM;
    }

    @Override
    public boolean handleJump(String nextNode) {
        super.handleJump(nextNode);
        switch (nextNode) {
            case NodeConst.MUSIC.PLAYER_ACTIVITY:
                getView().findViewById(R.id.fragment_thumb_tv_music_info).performClick();
                return true;
            default:
                return false;
        }
    }

    @Override
    public String getThisNode() {
        return NodeConst.MUSIC.MUSIC_THUMB_FRAGMENT;
    }

    private class PlayerCallback implements OnPlayControlListener {
        private void updateStatus(@PlayStatus int status) {
            getThumbPlayerVM().getPlayingStatus().postValue(status);
        }

        private void updateMusic(XMMusic music) {
            if (music != null && music.getSDKBean() != null) {
                getThumbPlayerVM().getMusic().postValue(music.getSDKBean());
            }
        }

        private void updateAll(XMMusic music, @PlayStatus int status) {
            updateMusic(music);
            updateStatus(status);
        }

        private String getDumpInfo(XMMusic music) {
            return music != null ? music.getName() : "null";
        }

        @Override
        public void onPreStart(XMMusic music) {
            Log.d(TAG, String.format("onPreStart( music: %s )", getDumpInfo(music)));
            updateAll(music, PlayStatus.BUFFERING);
            EventBus.getDefault().post(music, EventBusTags.RECENTER_PLAY);
        }

        @Override
        public void onReadyPlay(XMMusic music) {
            Log.d(TAG, String.format("onReadyPlay( music: %s )", getDumpInfo(music)));
//            updateAll(music, PlayStatus.BUFFERING);
            updateMusic(music);
            hideCharge();
            chargeMusic(music);
        }

        @Override
        public void onBufferStart() {
            Log.d(TAG, "onBufferStart()");
        }

        @Override
        public void onBufferFinish() {
            Log.d(TAG, "onBufferFinish()");
        }

        @Override
        public void onPlay(XMMusic music) {
            Log.d(TAG, String.format("onPlay( music: %s )", getDumpInfo(music)));
            updateAll(music, PlayStatus.PLAYING);
        }

        @Override
        public void onSeekSuccess(int position) {
            Log.d(TAG, String.format("onSeekSuccess( position: %s )", position));
        }

        @Override
        public void onPause() {
            Log.d(TAG, "onPause()");
            updateStatus(PlayStatus.PAUSE);
        }

        @Override
        public void onProgressChange(long progressInMs, long totalInMs) {
            //Log.d(TAG, String.format("onProgressChange( progressInMs: %s, totalInMs: %s )", progressInMs, totalInMs));
        }

        @Override
        public void onPlayFailed(@KuwoPlayControlObserver.ErrorCode int errorCode, String errorMsg) {
            Log.d(TAG, String.format("onPlayFailed( errorCode: %s, errorMsg: %s )", errorCode, errorMsg));
            updateStatus(PlayStatus.FAILED);
            if (errorCode == KuwoPlayControlObserver.ErrorCode.NEED_ALBUM ||
                    errorCode == KuwoPlayControlObserver.ErrorCode.NEED_SING_SONG ||
                    errorCode == KuwoPlayControlObserver.ErrorCode.NO_COPY_RIGHT) {
                showToastException(R.string.play_failed_next);
            }
        }

        @Override
        public void onPlayStop() {
            Log.d(TAG, "onPlayStop()");
            updateStatus(PlayStatus.STOP);
        }

        @Override
        public void onPlayModeChanged(int playMode) {
            Log.d(TAG, String.format("onPlayModeChanged( playMode: %s )", playMode));
        }

        @Override
        public void onCurrentPlayListChanged() {

        }
    }

    private void chargeMusic(XMMusic music) {
        List<XMMusic> musics = new ArrayList<>();
        musics.add(music);
        OnlineMusicFactory.getKWPlayer().chargeMusics(musics, new OnMusicChargeListener() {
            @Override
            public void onChargeSuccess(List<XMMusic> musics, List<Integer> chargeResults) {
                if (chargeResults != null && !chargeResults.isEmpty()) {
                    checkCharge(chargeResults.get(0));
                }
            }

            @Override
            public void onChargeFailed(String msg) {

            }
        });
    }

    private void checkCharge(int type) {
        switch (type) {
            case IKuwoConstant.XMMusicChargeType.NEED_VIP:
            case IKuwoConstant.XMMusicChargeType.NEED_VIP_SONG:
            case IKuwoConstant.XMMusicChargeType.NEED_VIP_ALBUM:
                //需要vip
                showCharge();
                //非vip用户显示开通气泡
                EventBus.getDefault().post(!OnlineMusicFactory.getKWLogin().isCarVipUser(), EventBusTags.MAIN_SHOW_BUY_VIP_VIEW);
                break;
            case IKuwoConstant.XMMusicChargeType.NEED_ALBUM:
            case IKuwoConstant.XMMusicChargeType.NEED_SONG:
                //需要购买单曲或专辑
//                showCharge(getString(R.string.need_buy));
//                break;
            case IKuwoConstant.XMMusicChargeType.NO_COPYRIGHT:
                //没有版权
//                showCharge("权");
//                break;
            default:
                hideCharge();
                break;
        }
    }
}

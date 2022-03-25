package com.xiaoma.launcher.travel.film.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.xiaoma.autotracker.listener.XMAutoTrackerEventOnClickListener;
import com.xiaoma.carlib.manager.XmCarEventDispatcher;
import com.xiaoma.center.logic.CenterConstants;
import com.xiaoma.component.AppHolder;
import com.xiaoma.component.base.BaseActivity;
import com.xiaoma.launcher.R;
import com.xiaoma.launcher.common.constant.EventConstants;
import com.xiaoma.launcher.common.constant.LauncherConstants;
import com.xiaoma.launcher.travel.film.ijkplayer.INiceControllerVideoListener;
import com.xiaoma.launcher.travel.film.ijkplayer.NiceVideoPlayer;
import com.xiaoma.launcher.travel.film.ijkplayer.NiceVideoPlayerManager;
import com.xiaoma.launcher.travel.film.ijkplayer.TxVideoPlayerController;
import com.xiaoma.launcher.travel.film.utils.PreviewFilmLimitCarEvent;
import com.xiaoma.model.ItemEvent;
import com.xiaoma.model.annotation.BusinessOnClick;
import com.xiaoma.model.annotation.PageDescComponent;
import com.xiaoma.trip.movie.response.FilmsBean;
import com.xiaoma.ui.dialog.ConfirmDialog;
import com.xiaoma.ui.toast.XMToast;
import com.xiaoma.utils.StringUtil;
import com.xiaoma.utils.log.KLog;

@PageDescComponent(EventConstants.PageDescribe.FilmTrailerActivityPagePathDesc)
public class FilmTrailerActivity extends BaseActivity implements INiceControllerVideoListener, PreviewFilmLimitCarEvent.LimitCallback {

    private NiceVideoPlayer mVideoPlayer;
    private TxVideoPlayerController mPlayerController;
    private FilmsBean mFilmsBean;
    private boolean notWarnPrompt;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getNaviBar().hideNavi();
        setContentView(R.layout.activity_look_trailer);
        XmCarEventDispatcher.getInstance().registerEvent(PreviewFilmLimitCarEvent.getInstance());
        PreviewFilmLimitCarEvent.getInstance().setLimitCallback(this);
        Intent intent = getIntent();
        if (intent != null) {
            boolean allowPreview = intent.getBooleanExtra(LauncherConstants.ActionExtras.ALLOW_PREVIEW_TRAILER, false);
            if (allowPreview) {
                notWarnPrompt = true;
            }
        }

        bindView();
        initView();
        initData();
//        registerVrReceiver();
    }

    private void initData() {
        mFilmsBean = (FilmsBean) getIntent().getSerializableExtra(LauncherConstants.ActionExtras.FILMS_BEAN);
        if (mFilmsBean != null) {
            setVideo(mFilmsBean);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        KLog.d("wzw", "onResume");
    }

    private void initView() {
        mPlayerController = new TxVideoPlayerController(this);
        mPlayerController.setNiceClickPreviosAndNextListener(this);
        mVideoPlayer.setController(mPlayerController);
    }

    private void bindView() {
        mVideoPlayer = findViewById(R.id.trailer_video_player);
    }

    @Override
    public void onClickPrevious() {

    }

    @Override
    public void onClickNext() {

    }

    @Override
    public void onClickClose() {
        finish();
    }

    public void setVideo(FilmsBean video) {
        if (video.getFilmDetailForm() != null) {
            if (StringUtil.isNotEmpty(video.getFilmDetailForm().getTrailersUrl())) {

                mVideoPlayer.setUrl(video.getFilmDetailForm().getTrailersUrl());
                mPlayerController.setTitle(String.format(getString(R.string.trailer), video.getTitle()));
                mVideoPlayer.start();
            } else {
                XMToast.showToast(FilmTrailerActivity.this, R.string.not_find_film);
            }
        }
    }

    private void registerVrReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(CenterConstants.SHOW_VOICE_ASSISTANT_DIALOG);
        filter.addAction(CenterConstants.DISMISS_VOICE_ASSISTANT_DIALOG);
        registerReceiver(vrReceiver, filter);
    }

    private BroadcastReceiver vrReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(CenterConstants.SHOW_VOICE_ASSISTANT_DIALOG)) {
                //语音助手弹窗起来
                mVideoPlayer.pause();
            } else if (action.equals(CenterConstants.DISMISS_VOICE_ASSISTANT_DIALOG)) {
                //语音助手弹窗关闭
                mVideoPlayer.restart();
            }
        }
    };

    @Override
    protected void onPause() {
        super.onPause();
        KLog.d("wzw", "onPause");
        if (mVideoPlayer!=null) {
            mVideoPlayer.pause();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        XmCarEventDispatcher.getInstance().unregisterEvent(PreviewFilmLimitCarEvent.getInstance());
        notWarnPrompt = false;
        if (mVideoPlayer!=null){
            mVideoPlayer.stop();
        }

        NiceVideoPlayerManager.instance().releaseNiceVideoPlayer();
    }


    @Override
    public void limitPreview() {
        finish();
        XMToast.showToast(AppHolder.getInstance().getAppContext(), R.string.driving_forbid_watch_video);
    }

    @Override
    public void warnPreview() {
        if (!notWarnPrompt) {
            showWarnPrompt();
        }
    }


    private void showWarnPrompt() {
        if (mVideoPlayer != null) {
            mVideoPlayer.pause();
        }

        final ConfirmDialog warnDialog = new ConfirmDialog(this, false);
        warnDialog.setContent(getString(R.string.driving_watch_video_message) + "\n" + getString(R.string.driving_watch_video_tip))
                .setPositiveButton(getString(R.string.driving_watch_video_replay), new XMAutoTrackerEventOnClickListener() {
                    @Override
                    public ItemEvent returnPositionEventMsg(View view) {
                        return new ItemEvent(EventConstants.NormalClick.CONTINUE_PLAY, null);
                    }

                    @Override
                    @BusinessOnClick
                    public void onClick(View v) {
                        //todo 继续播放
                        warnDialog.dismiss();
                        if (mVideoPlayer != null) {
                            mVideoPlayer.start(mVideoPlayer.getCurrentPosition());
                        }
                    }
                })
                .setNegativeButton(getString(R.string.driving_watch_video_stop_close), new XMAutoTrackerEventOnClickListener() {
                    @Override
                    public ItemEvent returnPositionEventMsg(View view) {
                        return new ItemEvent(EventConstants.NormalClick.STOP_CLOSE, null);
                    }

                    @Override
                    @BusinessOnClick
                    public void onClick(View v) {
                        //todo 停止播放 并关闭当前播放页面
                        warnDialog.dismiss();
                        notWarnPrompt = false;
                        FilmTrailerActivity.this.finish();
                    }
                })
                .show();
        notWarnPrompt = true;
    }
}

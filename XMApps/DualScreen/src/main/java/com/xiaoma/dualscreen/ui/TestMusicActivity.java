//package com.xiaoma.dualscreen.ui;
//
//import android.os.Bundle;
//import android.view.View;
//import android.widget.Button;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//import com.bumptech.glide.Glide;
//import com.xiaoma.center.logic.local.Center;
//import com.xiaoma.component.base.BaseActivity;
//import com.xiaoma.dualscreen.R;
//import com.xiaoma.dualscreen.listener.DuralScreenPlayListener;
//import com.xiaoma.dualscreen.manager.PlayerAudioManager;
//import com.xiaoma.dualscreen.manager.PlayerConnectHelper;
//import com.xiaoma.dualscreen.model.MediaModel;
//import com.xiaoma.player.AudioConstants;
//import com.xiaoma.player.AudioInfo;
//import com.xiaoma.player.ProgressInfo;
//import com.xiaoma.thread.ThreadDispatcher;
//import com.xiaoma.utils.log.KLog;
//
//public class TestMusicActivity extends BaseActivity implements DuralScreenPlayListener, View.OnClickListener {
//
//    private PlayerConnectHelper connectHelper;
//
//    private ImageView imageCover;
//
//    private TextView tvTitle, tvContent;
//
//    private AudioInfo mCurrentAudioInfo;
//
//    private int mPlayState;
//
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_test_music);
//
//        imageCover = findViewById(R.id.iv_cover);
//        tvTitle = findViewById(R.id.tv_title);
//        tvContent = findViewById(R.id.tv_content);
//
//        initCenter();
//
//        connectHelper = PlayerConnectHelper.getInstance();
//        connectHelper.setPlayListener(this);
//
//        findViewById(R.id.btn_pre).setOnClickListener(this);
//        findViewById(R.id.btn_play).setOnClickListener(this);
//        findViewById(R.id.btn_pause).setOnClickListener(this);
//        findViewById(R.id.btn_next).setOnClickListener(this);
//    }
//
//    @Override
//    public void onProgress(ProgressInfo progressInfo) {
//
//    }
//
//    @Override
//    public void onAudioInfo(final AudioInfo audioInfo) {
//        KLog.d("ljb", "onAudioInfo audioInfo:" + audioInfo);
//        mCurrentAudioInfo = audioInfo;
//        ThreadDispatcher.getDispatcher().postOnMain(new Runnable() {
//            @Override
//            public void run() {
//                MediaModel mediaModel = new MediaModel();
//                mediaModel.setName(audioInfo.getTitle());
//                mediaModel.setSinger(audioInfo.getSubTitle());
//                mediaModel.setIcon(audioInfo.getCover());
//                initView(mediaModel);
//            }
//        });
//    }
//
//    @Override
//    public void onPlayState(final int playState) {
//        KLog.d("ljb", "onAudioInfo onPlayState:" + playState);
//        ThreadDispatcher.getDispatcher().postOnMain(new Runnable() {
//            @Override
//            public void run() {
//                mPlayState = playState;
//            }
//        });
//    }
//
//    @Override
//    public void onDataSource(int dataSource) {
//
//    }
//
//    @Override
//    public void onAudioFavorite(boolean favorite) {
//
//    }
//
//    private void initView(MediaModel mediaModel) {
//        Glide.with(this).load(mediaModel.getIcon()).into(imageCover);
//        tvTitle.setText(mediaModel.getName());
//        tvContent.setText(mediaModel.getSinger());
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        connectHelper.removePlayListener(this);
//    }
//
//    private void initCenter() {
//        KLog.d("ljb", "initCenter : " + Center.getInstance().isConnected());
//        if (!Center.getInstance().isConnected()) {
//            Center.getInstance().init(getApplication());
//        }
//        Center.getInstance().runAfterConnected(new Runnable() {
//            @Override
//            public void run() {
//                PlayerAudioManager.getInstance().connectSourceInfos(getApplication());
//                PlayerAudioManager.getInstance().addStateCallBack(getApplication());
//            }
//        });
//    }
//
//    @Override
//    public void onClick(View v) {
//        KLog.d("ljb", "onClick mCurrentAudioInfo=" + mCurrentAudioInfo);
//        switch (v.getId()) {
//            case R.id.btn_pre:
//                if (mCurrentAudioInfo != null) {
//                    //想听播放历史处理
//                    if (mCurrentAudioInfo != null && mCurrentAudioInfo.isHistory() && (mCurrentAudioInfo.getAudioType() ==
//                            AudioConstants.AudioTypes.XTING_NET_FM || mCurrentAudioInfo.getAudioType() == AudioConstants.AudioTypes.XTING_NET_RADIO)) {
//                        connectHelper.playXtingHistory(mCurrentAudioInfo.getAudioType(), AudioConstants.Action.Option.PREVIOUS);
//
//                    } else {
//                        connectHelper.preNextAudio(AudioConstants.Action.Option.PREVIOUS, mCurrentAudioInfo.getAudioType());
//                    }
//                }
//                break;
//            case R.id.btn_play:
//                if (mCurrentAudioInfo != null) {
//                    if (mPlayState == AudioConstants.AudioStatus.PLAYING) {
//                        connectHelper.pauseAudio(mCurrentAudioInfo.getAudioType());
//
//                    } else {
//                        //想听播放历史处理
//                        if (mCurrentAudioInfo != null && mCurrentAudioInfo.isHistory() && (mCurrentAudioInfo.getAudioType() ==
//                                AudioConstants.AudioTypes.XTING_NET_FM || mCurrentAudioInfo.getAudioType() == AudioConstants.AudioTypes.XTING_NET_RADIO)) {
//                            connectHelper.playXtingHistory(mCurrentAudioInfo.getAudioType(), AudioConstants.Action.Option.PLAY);
//
//                        } else {
//                            connectHelper.playAudio(mCurrentAudioInfo.getAudioType(), AudioConstants.PlayAction.DEFAULT);
//                        }
//                    }
//                }
//                break;
//            case R.id.btn_pause:
//                if (mPlayState == AudioConstants.AudioStatus.PLAYING) {
//                    connectHelper.pauseAudio(mCurrentAudioInfo.getAudioType());
//
//                } else {
//                    //想听播放历史处理
//                    if (mCurrentAudioInfo != null && mCurrentAudioInfo.isHistory() && (mCurrentAudioInfo.getAudioType() ==
//                            AudioConstants.AudioTypes.XTING_NET_FM || mCurrentAudioInfo.getAudioType() == AudioConstants.AudioTypes.XTING_NET_RADIO)) {
//                        connectHelper.playXtingHistory(mCurrentAudioInfo.getAudioType(), AudioConstants.Action.Option.PLAY);
//
//                    } else {
//                        connectHelper.playAudio(mCurrentAudioInfo.getAudioType(), AudioConstants.PlayAction.DEFAULT);
//                    }
//                }
//                break;
//            case R.id.btn_next:
//                if (mCurrentAudioInfo != null) {
//                    //想听播放历史处理
//                    if (mCurrentAudioInfo != null && mCurrentAudioInfo.isHistory() && (mCurrentAudioInfo.getAudioType() ==
//                            AudioConstants.AudioTypes.XTING_NET_FM || mCurrentAudioInfo.getAudioType() == AudioConstants.AudioTypes.XTING_NET_RADIO)) {
//                        connectHelper.playXtingHistory(mCurrentAudioInfo.getAudioType(), AudioConstants.Action.Option.NEXT);
//
//                    } else {
//                        connectHelper.preNextAudio(AudioConstants.Action.Option.NEXT, mCurrentAudioInfo.getAudioType());
//                    }
//                }
//                break;
//        }
//    }
//
//}

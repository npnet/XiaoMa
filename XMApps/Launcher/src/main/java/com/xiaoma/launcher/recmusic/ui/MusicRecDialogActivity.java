package com.xiaoma.launcher.recmusic.ui;

import android.annotation.SuppressLint;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.acrcloud.rec.ACRCloudResult;
import com.acrcloud.rec.IACRCloudListener;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.xiaoma.autotracker.listener.XMAutoTrackerEventOnClickListener;
import com.xiaoma.carlib.manager.CarProvider;
import com.xiaoma.carlib.manager.XmMicManager;
import com.xiaoma.center.logic.CenterConstants;
import com.xiaoma.component.base.BaseActivity;
import com.xiaoma.config.ConfigManager;
import com.xiaoma.db.DBManager;
import com.xiaoma.image.ImageLoader;
import com.xiaoma.launcher.R;
import com.xiaoma.launcher.common.constant.EventConstants;
import com.xiaoma.launcher.player.manager.PlayerConnectHelper;
import com.xiaoma.launcher.recmusic.adapter.MusicRecAdapter;
import com.xiaoma.launcher.recmusic.model.MusicRecord;
import com.xiaoma.launcher.recmusic.vm.MusicRecVM;
import com.xiaoma.model.ItemEvent;
import com.xiaoma.model.annotation.BusinessOnClick;
import com.xiaoma.model.annotation.NormalOnClick;
import com.xiaoma.model.annotation.PageDescComponent;
import com.xiaoma.musicrec.manager.MusicRecManager;
import com.xiaoma.musicrec.model.Music;
import com.xiaoma.thread.ThreadDispatcher;
import com.xiaoma.ui.StateControl.StateView;
import com.xiaoma.ui.dialog.ConfirmDialog;
import com.xiaoma.ui.toast.XMToast;
import com.xiaoma.ui.view.XmDividerDecoration;
import com.xiaoma.ui.view.XmScrollBar;
import com.xiaoma.utils.ListUtils;
import com.xiaoma.utils.NetworkUtils;
import com.xiaoma.utils.StringUtil;
import com.xiaoma.utils.log.KLog;
import com.xiaoma.vr.tts.EventTtsManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author taojin
 * @date 2019/1/11
 */
@PageDescComponent(EventConstants.PageDescribe.MusicRecDialogActivityPagePathDesc)
public class MusicRecDialogActivity extends BaseActivity implements IACRCloudListener {

    private static final String MUSIC_PACKAGE = "com.xiaoma.music";
    private ImageView ivMusicClose;
    private LinearLayout tvMusicHistory;
    private RelativeLayout ivMusicLayout;
    private TextView tvMusicIat;
    private ImageView ivMusciSong;
    private ImageView historyImg;
    private TextView tvMusicSongName;
    private TextView tvMusicSingerName;
    private RecyclerView rvMusicRec;
    private MusicRecVM musicRecVM;
    private TextView tvErrorTip;
    private XmScrollBar xmScrollBar;
    private RelativeLayout rlMusicBottom;
    //默认进入就识别
    private boolean isRecognition = true;
    private StateView musicStateView;
    private StateView musicRvStateView;
    private MusicRecAdapter musicRecAdapter;
    private List<MusicRecord> musicRecordList;
    private TextView tvMusicRecClear;
    private PlayerConnectHelper connectHelper;
    private AnimationDrawable animationDrawable;
    private ImageView mIvMusicType;
    private RotateAnimation mRotate;
    private View mNoNetworkView;
    private TextView mMusicTvRetry;
    private MusicRecord mMusicRecord;
    private ImageView mRecAnimImg;
    private ImageView mStopBackImg;
    private String TAG = "MusicRecDialogActivity";
    private Music mMusic;
    private IntentFilter mFilter;
    Runnable startRecdelay = new Runnable() {
        @Override
        public void run() {
            //让语音助手释放焦点
            boolean mic = setMic(true);
            if (mic) {
                setProvider(true);
                MusicRecManager.getInstance().handleRecognize();
            } else {
                stopRec();
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getNaviBar().setVisibility(View.GONE);
        setContentView(R.layout.activity_music_rec_dialog);
        statusBarDividerGone();
        getRootLayout().setBackgroundResource(R.color.color_transparent);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        getWindow().setGravity(Gravity.START);
        CarProvider.getInstance().init(this);
        bindView();
        initListener();
        initRvAndAdapter();
        initData();
    }

    private void bindView() {
        ivMusicClose = findViewById(R.id.iv_music_rec_close);
        tvMusicHistory = findViewById(R.id.tv_music_rec_history);
        historyImg = findViewById(R.id.music_rec_history_img);
        ivMusicLayout = findViewById(R.id.iv_music_rec_layout);
        mIvMusicType = findViewById(R.id.iv_music_type);
        mRecAnimImg = findViewById(R.id.rec_anim_img);
        mStopBackImg = findViewById(R.id.stop_back_img);
        tvMusicIat = findViewById(R.id.tv_music_rec_iat);
        ivMusciSong = findViewById(R.id.iv_music_rec_song);
        tvMusicSongName = findViewById(R.id.tv_music_rec_song_name);
        tvMusicSingerName = findViewById(R.id.tv_music_rec_singer_name);
        rvMusicRec = findViewById(R.id.rv_music_rec);
        tvErrorTip = findViewById(R.id.tv_error_tip);
        rlMusicBottom = findViewById(R.id.rl_music_rev_bottom);
        musicStateView = findViewById(R.id.music_rec_sv);
        musicRvStateView = findViewById(R.id.music_rec_rv_sv);
        tvMusicRecClear = findViewById(R.id.tv_music_rev_clear);
        xmScrollBar = findViewById(com.xiaoma.launcher.R.id.scroll_bar);
        musicStateView.setLoadingView(R.layout.music_loding_view);
        musicRvStateView.setEmptyView(R.layout.music_history_empty);
        mNoNetworkView = musicRvStateView.getNoNetworkView();
        mMusicTvRetry = mNoNetworkView.findViewById(R.id.tv_retry);
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(60, 0, 0, 0);
        mNoNetworkView.setLayoutParams(lp);
        mNoNetworkView.setBackgroundResource(R.drawable.rec_bg_frame_left);
    }

    private void initListener() {
        animationDrawable = (AnimationDrawable) mRecAnimImg.getBackground();
        ivMusicLayout.setOnClickListener(new XMAutoTrackerEventOnClickListener() {
            @Override
            public ItemEvent returnPositionEventMsg(View view) {
                String type = "";
                if (NetworkUtils.isConnected(MusicRecDialogActivity.this)) {
                    if (isRecognition) {
                        type = EventConstants.NormalClick.MUSIC_REC_START;
                    } else {
                        type = EventConstants.NormalClick.MUSIC_REC_STOP;
                    }
                } else {
                    type = EventConstants.NormalClick.MUSIC_REC_RETRY;
                }
                return new ItemEvent(EventConstants.NormalClick.MUSIC_REC_TYPE, type);
            }

            @Override
            @BusinessOnClick
            public void onClick(View v) {
                EventTtsManager.getInstance().stopSpeaking();
                if (startRecdelay != null) {
                    ThreadDispatcher.getDispatcher().remove(startRecdelay);
                }
                if (NetworkUtils.isConnected(MusicRecDialogActivity.this)) {
                    if (isRecognition) {
                        stopRec();
                    } else {
                        startRec();
                    }
                } else {
                    showLoading();
                }
            }
        });
        tvMusicHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            @NormalOnClick(EventConstants.NormalClick.MUSIC_REC_HISTORY)
            public void onClick(View v) {
                if (musicRvStateView.getVisibility() == View.INVISIBLE) {
                    historyImg.setSelected(true);
                    musicRvStateView.setVisibility(View.VISIBLE);
                    if (NetworkUtils.isConnected(MusicRecDialogActivity.this)) {
                        musicRecVM.fetchMusicRecordData();
                    } else {
                        musicRvStateView.showNoNetwork();
                    }
                } else if (musicRvStateView.getVisibility() == View.VISIBLE) {
                    historyImg.setSelected(false);
                    musicRvStateView.setVisibility(View.INVISIBLE);
                }
            }
        });
        tvMusicRecClear.setOnClickListener(new View.OnClickListener() {
            @Override
            @NormalOnClick(EventConstants.NormalClick.MUSIC_REC_CANCEL_HISTORY)
            public void onClick(View v) {
                showCanclHistoryDialog();
            }
        });
        ivMusicClose.setOnClickListener(new View.OnClickListener() {
            @Override
            @NormalOnClick(EventConstants.NormalClick.MUSIC_REC_CLOSE)
            public void onClick(View v) {
                if (startRecdelay != null) {
                    ThreadDispatcher.getDispatcher().remove(startRecdelay);
                }
                stopRec();
                moveTaskToBack(true);
            }
        });
        rlMusicBottom.setOnClickListener(new View.OnClickListener() {
            @Override
            @NormalOnClick(EventConstants.NormalClick.MUSIC_REC_SUCESS)
            public void onClick(View v) {
                if (mMusicRecord != null) {
                    startMusicApp();
                    //TODO 用歌手和歌曲播放
                    connectHelper.playNameAudio(mMusicRecord.getSingerName(), mMusicRecord.getName());
                }

            }
        });
        if (mMusicTvRetry != null) {
            mMusicTvRetry.setOnClickListener(new View.OnClickListener() {
                @Override
                @NormalOnClick(EventConstants.NormalClick.MUSIC_REC_HISTORY_NETWORK_RETRY)
                public void onClick(View v) {
                    historyRetry();
                }
            });
        }
    }

    private void initData() {
        connectHelper = PlayerConnectHelper.getInstance();
        musicRecVM = ViewModelProviders.of(this).get(MusicRecVM.class);
        musicRecVM.getMusicData().observe(this, new Observer<ACRCloudResult>() {

            @Override
            public void onChanged(@Nullable ACRCloudResult acrCloudResult) {
                mMusic = MusicRecManager.getInstance().handlerMusicResult(acrCloudResult);
                if (mMusic != null) {
                    List<Music.ArtistsBean> artists = mMusic.getArtists();
                    StringBuilder stringBuilder = new StringBuilder();
                    for (int i = 0; i < artists.size(); i++) {
                        if (i < artists.size() - 1) {
                            stringBuilder.append(artists.get(i).getName() + "&");
                        } else {
                            stringBuilder.append(artists.get(i).getName());
                        }
                    }
                    //musicRecVM.saveMusicData(mMusic.getTitle(), stringBuilder.toString());
                    resourceNotHaveMusic();
                    animationDrawable.stop();
                    setMic(false);
                    setProvider(false);
                    mRecAnimImg.setVisibility(View.GONE);
                    mStopBackImg.setVisibility(View.VISIBLE);
                    mIvMusicType.setImageResource(R.drawable.icon_rec_music);
                } else {
                    setNotDataView();
                }
            }
        });

        musicRecVM.getMusicRecordData().observe(this, new Observer<List<MusicRecord>>() {
            @Override
            public void onChanged(@Nullable List<MusicRecord> musicRecords) {
                if (!ListUtils.isEmpty(musicRecords)) {
                    musicRvStateView.showContent();
                    musicRecordList.clear();
                    Collections.reverse(musicRecords);
                    musicRecordList.addAll(musicRecords);
                    musicRecAdapter.notifyDataSetChanged();
                } else {
                    musicRvStateView.showEmpty();
                }

            }
        });

    }

    private void resourceNotHaveMusic() {
        if (mMusic != null) {
            mMusicRecord = new MusicRecord();
            List<Music.ArtistsBean> artists = mMusic.getArtists();
            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i < artists.size(); i++) {
                if (i < artists.size() - 1) {
                    stringBuilder.append(artists.get(i).getName() + "&");
                } else {
                    stringBuilder.append(artists.get(i).getName());
                }
            }

            rlMusicBottom.setVisibility(View.VISIBLE);
            tvErrorTip.setVisibility(View.INVISIBLE);
            if (StringUtil.isNotEmpty(mMusic.getTitle())) {
                tvMusicSongName.setText(mMusic.getTitle());
            } else {
                tvMusicSongName.setText(getString(com.xiaoma.launcher.R.string.song_name_failed));
            }
            if (StringUtil.isNotEmpty(stringBuilder.toString())) {
                tvMusicSingerName.setText(stringBuilder.toString());
            } else {
                tvMusicSingerName.setText(getString(com.xiaoma.launcher.R.string.singer_name_failed));
            }
            tvMusicIat.setText(getString(R.string.click_re_recognition));
            if (mMusic.getAlbum() != null) {
                mMusicRecord.setSingerCoverUrl(mMusic.getAlbum().getImage());
                ImageLoader.with(MusicRecDialogActivity.this)
                        .load(mMusic.getAlbum().getImage())
                        .placeholder(R.drawable.muscirec_default_cover)
                        .transform(new RoundedCorners(10))
                        .into(ivMusciSong);
            }

            mMusicRecord.setName(mMusic.getTitle());
            mMusicRecord.setSingerName(stringBuilder.toString());
            mMusicRecord.setSaveType(false);
            mMusicRecord.setRecId(mMusic.getAcrid());
            saveRecMusic(mMusicRecord);
            EventTtsManager.getInstance().
                    startSpeaking(StringUtil.format(MusicRecDialogActivity.this.getString(R.string.successful_recognition),
                            stringBuilder.toString(),
                            mMusic.getTitle()));
            PlayerConnectHelper.isRecognize = true;
            musicRecVM.fetchMusicRecordData();
            return;
        }
        setNotDataView();
    }

    private void saveRecMusic(MusicRecord musicRecord) {
        List<MusicRecord> musicRecords = null;
        try {
            musicRecords = DBManager.getInstance().getDBManager().queryAll(MusicRecord.class);
            for (MusicRecord item : musicRecords) {
                if (item.equals(musicRecord)) {
                    return;
                }
            }
            DBManager.getInstance().getDBManager().insert(musicRecord);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        EventTtsManager.getInstance().init(MusicRecDialogActivity.this);
        statusBarDividerGone();
        if (mFilter == null) {
            registerReceiver();
        }
        MusicRecManager.getInstance().init(this, this);
        musicRecVM.fetchMusicRecordData();
        startRec();
    }

    private void setNotDataView() {
        EventTtsManager.getInstance().startSpeaking(MusicRecDialogActivity.this.getString(R.string.failure_recognition));
        animationDrawable.stop();
        setMic(false);
        setProvider(false);
        mRecAnimImg.setVisibility(View.GONE);
        mStopBackImg.setVisibility(View.VISIBLE);
        mIvMusicType.setImageResource(R.drawable.icon_rec_music);
        rlMusicBottom.setVisibility(View.INVISIBLE);
        tvErrorTip.setVisibility(View.VISIBLE);
        tvMusicIat.setText(getString(R.string.no_recognition_song));
        tvErrorTip.setText(getString(R.string.recognition_success));
    }

    private void initRvAndAdapter() {
        rvMusicRec.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        XmDividerDecoration decor = new XmDividerDecoration(this, DividerItemDecoration.HORIZONTAL);
        int horizontal = 60;
        int extra = 40;
        decor.setRect(0, 0, horizontal, 0);
        decor.setExtraMargin(extra);
        rvMusicRec.addItemDecoration(decor);

        musicRecordList = new ArrayList<>();
        musicRecAdapter = new MusicRecAdapter(R.layout.item_music_rec_rv, musicRecordList);
        rvMusicRec.setAdapter(musicRecAdapter);
        xmScrollBar.setRecyclerView(rvMusicRec);
        musicRecAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if (musicRecordList.get(position) != null) {
                    startMusicApp();
                   /* if (musicRecordList.get(position).isSaveType()) {
                        connectHelper.playAudio(ConvertUtils.stringToInt(musicRecordList.get(position).getSongId()));
                    } else {
                        //TODO 用歌手和歌曲播放
                        connectHelper.playNameAudio(musicRecordList.get(position).getSingerName(), musicRecordList.get(position).getName());
                    }*/
                    //TODO 用歌手和歌曲播放
                    connectHelper.playNameAudio(musicRecordList.get(position).getSingerName(), musicRecordList.get(position).getName());
                }
            }
        });
    }

    @Override
    @SuppressLint("MissingPermission")
    protected void noNetworkOnRetry() {
        musicStateView.showContent();
        if (NetworkUtils.isConnected(this)) {
            startRec();
        } else {
            notNoNetworkView();
        }
    }

    /**
     * 历史记录断网重新加载
     */
    private void historyRetry() {
        if (NetworkUtils.isConnected(this)) {
            musicRvStateView.showContent();
            musicRecVM.fetchMusicRecordData();
        } else {
            musicRvStateView.showNoNetwork();
        }
    }


    /**
     * 开始识别
     */
    @SuppressLint("MissingPermission")
    public void startRec() {
        if (mMusic != null) {
            mMusic = null;
        }
        if (NetworkUtils.isConnected(this)) {
            isRecognition = true;
            animationDrawable.start();
            mRecAnimImg.setVisibility(View.VISIBLE);
            mStopBackImg.setVisibility(View.GONE);
            mIvMusicType.setImageResource(R.drawable.icon_rec_voice);
            tvMusicIat.setText(getString(R.string.recognition_music));
            rlMusicBottom.setVisibility(View.INVISIBLE);
            tvErrorTip.setVisibility(View.VISIBLE);
            tvErrorTip.setText(getString(R.string.click_recognition_stop));
            ThreadDispatcher.getDispatcher().postDelayed(startRecdelay, 1000);
        } else {
            notNoNetworkView();
        }
    }

    /**
     * 释放语音助手的焦点或者恢复其焦点
     *
     * @param toggle
     */
    private boolean setMic(boolean toggle) {
        if (toggle) {
            return XmMicManager.getInstance().requestMicFocus(onMusicFocus, XmMicManager.MIC_LEVEL_APP, XmMicManager.FLAG_NONE);
        } else {
            return XmMicManager.getInstance().abandonMicFocus(onMusicFocus);
        }
    }

    private XmMicManager.OnMicFocusChangeListener onMusicFocus = new XmMicManager.OnMicFocusChangeListener() {
        @Override
        public void onMicFocusChange(int focusChange) {
            switch (focusChange) {
                case XmMicManager.MICFOCUS_GAIN:
                    Log.d(TAG, "MusicRec MICFOCUS_GAIN");
                    break;
                case XmMicManager.MICFOCUS_LOSS:
                    stopRec();
                    break;
            }
        }
    };

    /**
     * 无网络视图
     */
    public void notNoNetworkView() {
        animationDrawable.stop();
        setMic(false);
        setProvider(false);
        mRecAnimImg.setVisibility(View.GONE);
        mStopBackImg.setVisibility(View.VISIBLE);
        mIvMusicType.setImageResource(R.drawable.icon_rec_refresh);
        tvMusicIat.setText(getString(R.string.no_network_retry));
        tvErrorTip.setText(getString(R.string.no_network_prompt));
    }

    /**
     * 停止识别
     */
    public void stopRec() {
        isRecognition = false;
        //让语音助手恢复焦点
        animationDrawable.stop();
        setProvider(false);
        mRecAnimImg.setVisibility(View.GONE);
        mStopBackImg.setVisibility(View.VISIBLE);
        mIvMusicType.setImageResource(R.drawable.icon_rec_music);
        MusicRecManager.getInstance().stopRecognize();
        tvMusicIat.setText(getString(R.string.click_recognition_music));
        rlMusicBottom.setVisibility(View.INVISIBLE);
        tvErrorTip.setVisibility(View.VISIBLE);
        tvErrorTip.setText(getString(R.string.recognition_stop));
        setMic(false);
    }

    @Override
    public void onBackPressed() {
        stopRec();
        moveTaskToBack(true);
    }

    @Override
    public void onResult(ACRCloudResult acrCloudResult) {
        if (isRecognition) {
            isRecognition = false;
            musicRecVM.setMusicData(acrCloudResult);
        }
    }

    @Override
    public void onVolumeChanged(double v) {

    }

    /**
     * 加载监听
     */
    private void showLoading() {
        musicStateView.showLoading();
        View loadingView = musicStateView.getLoadingView();
        ImageView loadingImg = loadingView.findViewById(R.id.iv_music_loading);
        mRotate = new RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        LinearInterpolator lin = new LinearInterpolator();
        mRotate.setInterpolator(lin);
        mRotate.setDuration(2000);//设置动画持续周期
        mRotate.setRepeatCount(-1);//设置重复次数
        mRotate.setFillAfter(true);//动画执行完后是否停留在执行完的状态
        mRotate.setStartOffset(10);//执行前的等待时间
        loadingImg.setAnimation(mRotate);
        mRotate.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                mRotate.cancel();
                noNetworkOnRetry();
            }
        });
    }


    /**
     * 清除确认dialog
     */
    private void showCanclHistoryDialog() {
        ConfirmDialog dialog = new ConfirmDialog(this);
        dialog.setContent(getString(com.xiaoma.launcher.R.string.colse_over_muscirec_date))
                .setPositiveButton(getString(com.xiaoma.launcher.R.string.btn_sure), new View.OnClickListener() {
                    @Override
                    @NormalOnClick(EventConstants.NormalClick.MUSIC_REC_CANCEL_HISTORY_SURE)
                    public void onClick(View v) {
                        ThreadDispatcher.getDispatcher().postOnMain(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    DBManager.getInstance().getDBManager().deleteAll(MusicRecord.class);
                                    musicRvStateView.showEmpty();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(getString(com.xiaoma.launcher.R.string.cancel), new View.OnClickListener() {
                    @Override
                    @NormalOnClick(EventConstants.NormalClick.MUSIC_REC_CANCEL_HISTORY_CANCEL)
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                })
                .show();

    }

    private void startMusicApp() {
        Intent intent = getPackageManager().getLaunchIntentForPackage(MUSIC_PACKAGE);
        if (intent != null) {
            startActivity(intent);
        } else {
            XMToast.showToast(this, this.getString(R.string.music_no_install));
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (startRecdelay != null) {
            ThreadDispatcher.getDispatcher().remove(startRecdelay);
        }
        EventTtsManager.getInstance().stopSpeaking();
        stopRec();
    }

    @Override
    protected void onStop() {
        super.onStop();
        CarProvider.getInstance().stopCar();
        if (mRotate != null) {
            mRotate.cancel();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void setProvider(boolean source) {
        if (!ConfigManager.ApkConfig.isCarPlatform()) {

        } else {
            //切换录制源
            CarProvider.getInstance().setBooleanProperty(
                    CarProvider.ID_SONG_RECOGNITION,
                    0,
                    source
            );
            //true 为 内录
            // false 为 麦克风
            //获取
            boolean curSource = CarProvider.getInstance()
                    .getBooleanProperty(CarProvider.ID_SONG_RECOGNITION);

            KLog.d("youthyjj Recording source setting result++++++" + curSource);
        }
    }

    private void registerReceiver() {
        mFilter = new IntentFilter();
        mFilter.addAction(CenterConstants.INCOMING_CALL);
        mFilter.addAction(CenterConstants.IN_A_CALL);
        this.registerReceiver(receiver, mFilter);
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action == null) {
                return;
            }
            switch (action) {
                case CenterConstants.INCOMING_CALL:
                case CenterConstants.IN_A_CALL:
                    stopRec();
                    moveTaskToBack(true);
                    break;
            }
        }
    };

}
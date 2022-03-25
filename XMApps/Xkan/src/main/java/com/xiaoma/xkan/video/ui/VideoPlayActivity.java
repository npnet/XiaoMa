//package com.xiaoma.xkan.video.ui;
//
//import android.arch.lifecycle.Observer;
//import android.arch.lifecycle.ViewModelProviders;
//import android.content.BroadcastReceiver;
//import android.content.Context;
//import android.content.Intent;
//import android.content.IntentFilter;
//import android.net.Uri;
//import android.os.Bundle;
//import android.support.annotation.Nullable;
//import android.view.View;
//import android.view.WindowManager;
//
//import com.xiaoma.autotracker.listener.XMAutoTrackerEventOnClickListener;
//import com.xiaoma.component.AppHolder;
//import com.xiaoma.component.base.BaseActivity;
//import com.xiaoma.image.ImageLoader;
//import com.xiaoma.model.ItemEvent;
//import com.xiaoma.model.XmResource;
//import com.xiaoma.model.annotation.BusinessOnClick;
//import com.xiaoma.model.annotation.PageDescComponent;
//import com.xiaoma.ui.dialog.ConfirmDialog;
//import com.xiaoma.ui.toast.XMToast;
//import com.xiaoma.utils.ByteUtils;
//import com.xiaoma.utils.FileUtils;
//import com.xiaoma.utils.ListUtils;
//import com.xiaoma.utils.log.KLog;
//import com.xiaoma.xkan.R;
//import com.xiaoma.xkan.common.constant.EventConstants;
//import com.xiaoma.xkan.common.constant.XkanConstants;
//import com.xiaoma.xkan.common.manager.XkanCarEvent;
//import com.xiaoma.xkan.common.model.UsbMediaInfo;
//import com.xiaoma.xkan.ijkplayer.INiceControllerVideoListener;
//import com.xiaoma.xkan.ijkplayer.NiceVideoPlayer;
//import com.xiaoma.xkan.ijkplayer.NiceVideoPlayerManager;
//import com.xiaoma.xkan.ijkplayer.TxVideoPlayerController;
//import com.xiaoma.xkan.video.vm.XmVideoVm;
//
//import org.simple.eventbus.EventBus;
//import org.simple.eventbus.Subscriber;
//
//import java.io.File;
//import java.util.List;
//
///**
// * 视频播放页面
// * Created by zhushi.
// * Date: 2018/11/12
// */
//@PageDescComponent(EventConstants.PageDescribe.VIDEOPLAYACTIVITYPAGEPATHDESC)
//public class VideoPlayActivity extends BaseActivity implements INiceControllerVideoListener, XkanCarEvent.CallBack {
//
//    private static final String TAG = "VideoPlayActivity";
//    private NiceVideoPlayer mVideoPlayer;
//    private static final float DEFAULT_SPEED = 0.5f;
//    //视频索引
//    private int mVideoIndex;
//    private String mVideoType;
//    private TxVideoPlayerController mPlayerController;
//    private boolean isShowVideoDialog = false;
//
//    private boolean isVideoPlay;
//
//    private List<UsbMediaInfo> currVideoList;
//
//    @Override
//    protected void onCreate(@Nullable Bundle savedInstanceState) {
//        //隐藏状态栏
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        super.onCreate(savedInstanceState);
//        getNaviBar().hideNavi();
//        setContentView(R.layout.activity_video_play);
//        EventBus.getDefault().register(this);
//        bindView();
//        initView();
//        initData();
////        registerAssistantReceiver();
//    }
//
//    private BroadcastReceiver mAssistantReceiver = new BroadcastReceiver() {
//
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            String action = intent.getAction();
//            if (XkanConstants.SHOW_VOICE_ASSISTANT_DIALOG.equals(action)) {
//                //唤起语音助手时,记录视频播放状态
//                isVideoPlay = mVideoPlayer.isPlaying();
//                //如果语音助手唤起时,视频正在播放,就暂停
//                if (mVideoPlayer.isPlaying()) {
//                    mVideoPlayer.pause();
//                }
//
//            } else if (XkanConstants.DISMISS_VOICE_ASSISTANT_DIALOG.equals(action)) {
//                //语音助手dialog消失
//                if (!isVideoPlay) {
//                    //如果语音助手唤起之前,已经是暂停状态,就不用操作
//                    return;
//                }
//                if (mVideoPlayer.isPaused()) {
//                    mVideoPlayer.restart();
//                }
//            }
//        }
//    };
//
//    private void registerAssistantReceiver() {
//        IntentFilter filter = new IntentFilter();
//        //语音助手dialog唤起
//        filter.addAction(XkanConstants.SHOW_VOICE_ASSISTANT_DIALOG);
//        //语音助手dialog消失
//        filter.addAction(XkanConstants.DISMISS_VOICE_ASSISTANT_DIALOG);
//        registerReceiver(mAssistantReceiver, filter);
//    }
//
//    private void bindView() {
//        mVideoPlayer = findViewById(R.id.nice_video_player);
//    }
//
//    private void initView() {
//        mPlayerController = new TxVideoPlayerController(this);
//        mPlayerController.setNiceClickPreviosAndNextListener(this);
//        mVideoPlayer.setController(mPlayerController);
//        XkanCarEvent.getInstance().setmCallBack(this);
//    }
//
//    private void initData() {
//        mVideoIndex = getIntent().getIntExtra(XkanConstants.VIDEO_INDEX, -1);
//        mVideoType = getIntent().getStringExtra(XkanConstants.FROM_TYPE);
//        XmVideoVm videoVm = ViewModelProviders.of(this).get(XmVideoVm.class);
//        videoVm.getmVideos().observe(this, new Observer<XmResource<List<UsbMediaInfo>>>() {
//            @Override
//            public void onChanged(@Nullable XmResource<List<UsbMediaInfo>> listXmResource) {
//                listXmResource.handle(new OnCallback<List<UsbMediaInfo>>() {
//                    @Override
//                    public void onSuccess(List<UsbMediaInfo> data) {
//                        currVideoList = data;
//                        if (ListUtils.isEmpty(data)) {
//                            return;
//                        }
//
//                        try {
//                            setVideo(data.get(mVideoIndex));
//                        } catch (Exception e) {
//                            KLog.e(TAG, e.getMessage());
//                        } catch (Throwable t) {
//                            KLog.e(TAG, t.getMessage());
//                        }
//                    }
//                });
//            }
//        });
//        videoVm.fetchmVideos(mVideoType);
//    }
//
//    @Override
//    protected void onStop() {
//        super.onStop();
//    }
//
//    private void setVideo(UsbMediaInfo mediaInfo) {
//        mVideoPlayer.setUrl(mediaInfo.getPath());
//
//        mPlayerController.setTitle(FileUtils.getFileNameNoEx(mediaInfo.getMediaName()));
//        mPlayerController.setSize(ByteUtils.getFileSize(mediaInfo.getSize()));
//
//        mPlayerController.setSpeed(DEFAULT_SPEED);
//        ImageLoader.with(this)
//                .load(Uri.fromFile(new File(mediaInfo.getPath())))
//                .error(R.drawable.icon_video_default)
//                .placeholder(R.drawable.icon_video_default)
//                .into(mPlayerController.imageView());
//        mVideoPlayer.start();
//
//    }
//
//    @Override
//    public void onClickPrevious() {
//        if (ListUtils.isEmpty(currVideoList)) {
//            return;
//        }
//        mVideoIndex--;
//        if (mVideoIndex < 0) {
//            mVideoIndex = currVideoList.size() - 1;
//        }
//        mVideoPlayer.releasePlayer();
//        try {
//            setVideo(currVideoList.get(mVideoIndex));
//        } catch (Exception e) {
//            KLog.e(TAG, e.getMessage());
//        } catch (Throwable t) {
//            KLog.e(TAG, t.getMessage());
//        }
//    }
//
//    @Override
//    public void onClickNext() {
//        if (ListUtils.isEmpty(currVideoList)) {
//            return;
//        }
//        mVideoIndex++;
//        if (mVideoIndex >= currVideoList.size()) {
//            mVideoIndex = 0;
//        }
//        mVideoPlayer.releasePlayer();
//
//        try {
//            setVideo(currVideoList.get(mVideoIndex));
//        } catch (Exception e) {
//            KLog.e(TAG, e.getMessage());
//        } catch (Throwable t) {
//            KLog.e(TAG, t.getMessage());
//        }
//    }
//
//    @Override
//    public void onClickClose() {
//        postEvent();
//        finish();
//    }
//
//    /**
//     * 视频播放完成,播放下一个视频
//     */
//    @Override
//    public void onFinish() {
//        if (ListUtils.isEmpty(currVideoList)) {
//            postEvent();
//            finish();
//        } else {
//            mVideoIndex++;
//            if (mVideoIndex >= currVideoList.size()) {
//                //如果是最后一个视频，就结束播放
//                finish();
//            } else {
//                mVideoPlayer.releasePlayer();
//                try {
//                    setVideo(currVideoList.get(mVideoIndex));
//                } catch (Exception e) {
//                    KLog.e(TAG, e.getMessage());
//                } catch (Throwable t) {
//                    KLog.e(TAG, t.getMessage());
//                }
//            }
//        }
//    }
//
//    public void postEvent() {
//        if (XkanConstants.FROM_ALL.equals(mVideoType)) {
//            EventBus.getDefault().post(mVideoIndex, XkanConstants.XKAN_MAIN_VIDEO_POS);
//        } else {
//            EventBus.getDefault().post(mVideoIndex, XkanConstants.XKAN_VIDEO_POS);
//        }
//    }
//
//    /**
//     * 收到USB移除通知释放player关闭页面
//     *
//     * @param event
//     */
//
//    @Subscriber(tag = XkanConstants.RELEASE_MEDIAINFO)
//    public void releaseMediaAndFinishActivity(String event) {
//        NiceVideoPlayerManager.instance().releaseNiceVideoPlayer();
//        finish();
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        NiceVideoPlayerManager.instance().pauseNiceVideoPlayer();
//    }
//
//    @Override
//    protected void onDestroy() {
//        NiceVideoPlayerManager.instance().releaseNiceVideoPlayer();
//        EventBus.getDefault().unregister(this);
//        XkanCarEvent.getInstance().setmCallBack(null);
////        unregisterReceiver(mAssistantReceiver);
//        super.onDestroy();
//    }
//
//    @Override
//    public void onBackPressed() {
//        postEvent();
//        super.onBackPressed();
//    }
//
//    @Override
//    public void onSpeedOutZero() {
//        finish();
//        KLog.d(TAG, "onSpeedOutZero");
//        XMToast.showToast(AppHolder.getInstance().getAppContext(), R.string.driving_forbid_watch_video);
//    }
//
//    @Override
//    public void onSpeedOutTen() {
//        KLog.d(TAG, "isShowVideoDialog" + isShowVideoDialog);
//        if (!isShowVideoDialog) {
//            KLog.d(TAG, "onSpeedOutTen");
//            showVideoDialog();
//        }
//    }
//
//
//    private void showVideoDialog() {
//        NiceVideoPlayerManager.instance().pauseNiceVideoPlayer();
//        final ConfirmDialog dialog = new ConfirmDialog(this);
//        dialog.setContent(getString(R.string.driving_watch_video_message) + "\n" + getString(R.string.driving_watch_video_tip))
//                .setPositiveButton(getString(R.string.driving_watch_video_replay), new XMAutoTrackerEventOnClickListener() {
//                    @Override
//                    public ItemEvent returnPositionEventMsg(View view) {
//                        return new ItemEvent(EventConstants.NormalClick.CONTINUE_PLAY, null);
//                    }
//
//                    @Override
//                    @BusinessOnClick
//                    public void onClick(View v) {
//                        dialog.dismiss();
//                        NiceVideoPlayerManager.instance().resumeNiceVideoPlayer();
//                        isShowVideoDialog = false;
//
//                    }
//                })
//                .setNegativeButton(getString(R.string.driving_watch_video_stop_close), new XMAutoTrackerEventOnClickListener() {
//                    @Override
//                    public ItemEvent returnPositionEventMsg(View view) {
//                        return new ItemEvent(EventConstants.NormalClick.STOP_CLOSE, null);
//                    }
//
//                    @Override
//                    @BusinessOnClick
//                    public void onClick(View v) {
//                        dialog.dismiss();
//                        isShowVideoDialog = false;
//                        finish();
//                    }
//                })
//                .show();
//        isShowVideoDialog = true;
//    }
//}

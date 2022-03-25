package com.xiaoma.instructiondistribute.xkan.video.ui;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.xiaoma.component.base.BaseActivity;
import com.xiaoma.image.ImageLoader;
import com.xiaoma.instructiondistribute.BuildConfig;
import com.xiaoma.instructiondistribute.R;
import com.xiaoma.instructiondistribute.utils.DistributeConstants;
import com.xiaoma.instructiondistribute.xkan.common.constant.XkanConstants;
import com.xiaoma.instructiondistribute.xkan.common.listener.UsbConnectStateListener;
import com.xiaoma.instructiondistribute.xkan.common.listener.UsbMediaSearchListener;
import com.xiaoma.instructiondistribute.xkan.common.manager.UsbMediaDataManager;
import com.xiaoma.instructiondistribute.xkan.common.model.UsbMediaInfo;
import com.xiaoma.instructiondistribute.xkan.common.model.UsbStatus;
import com.xiaoma.instructiondistribute.xkan.ijkplayer.INiceControllerVideoListener;
import com.xiaoma.instructiondistribute.xkan.ijkplayer.NiceVideoPlayer;
import com.xiaoma.instructiondistribute.xkan.ijkplayer.NiceVideoPlayerManager;
import com.xiaoma.instructiondistribute.xkan.ijkplayer.TxVideoPlayerController;
import com.xiaoma.instructiondistribute.xkan.video.PlayerStatusChangeCallback;
import com.xiaoma.instructiondistribute.xkan.video.vm.XmVideoVm;
import com.xiaoma.model.XmResource;
import com.xiaoma.ui.toast.XMToast;
import com.xiaoma.utils.ByteUtils;
import com.xiaoma.utils.FileUtils;
import com.xiaoma.utils.ListUtils;
import com.xiaoma.utils.StringUtil;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;

import java.io.File;
import java.util.List;

/**
 * 视频播放页面
 * Created by zhushi.
 * Date: 2018/11/12
 */
public class VideoPlayActivity extends BaseActivity implements INiceControllerVideoListener, PlayerStatusChangeCallback, UsbConnectStateListener {

    private static final String TAG = "VideoPlayActivity";
    private static final float DEFAULT_SPEED = 0.5f;
    private NiceVideoPlayer mVideoPlayer;
    /**
     * 视频索引
     */
    private int mVideoIndex = -1;
    private String mVideoType;
    private TxVideoPlayerController mPlayerController;
    private XmVideoVm mVideoVm;


    private List<UsbMediaInfo> currVideoList;
    private boolean triggered; // 避免播放状态改变导致多次调用eventbus发送结果

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        //隐藏状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        getNaviBar().hideNavi();
        setContentView(R.layout.activity_video_play);
        EventBus.getDefault().register(this);
        bindView();
        initView();
        initData();
        UsbMediaDataManager.getInstance().syncUsbConnectState(VideoPlayActivity.this, VideoPlayActivity.this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        eolVideoOperation(intent);
    }

    private void eolVideoOperation(Intent intent) {
        Bundle extras = intent.getExtras();
        if (extras == null) return;
        int action = extras.getInt("action");
        int status = extras.getInt("status");
        Bundle result = new Bundle();
        switch (action) {
            case DistributeConstants.ACTION_SET_USB_VIDEO_PAUSE_PLAY:
                handleUsbVideoPauseOrPlay(status, result);
                break;
            case DistributeConstants.ACTION_GET_USB_VIDEO_PAUSE_PLAY:
                handleUsbVideoStatus(result);
                break;
            case DistributeConstants.ACTION_SET_USB_VIDEO_PREVIOUS_NEXT:
                handleUsbVideoNextOrPrevious(status, result);
                break;
        }
    }

    public static final String TAG_OPEARTE_USB_VIDEO_NEXT_PRE = "usb_video_next_pre";
    private boolean mOpearteOkF = false;

    private void handleUsbVideoNextOrPrevious(int status, Bundle result) {
        XMToast.showToast(VideoPlayActivity.this, "Play Next Or Pre " + status);
        mOpearteOkF = false;
        switch (status) {
            case 1: // next
                onClickNext();
                break;
            case 2: // previous
                onClickPrevious();
                break;
        }

        if (mOpearteOkF) {
            EventBus.getDefault().post(true, TAG_OPEARTE_USB_VIDEO_NEXT_PRE);
        }
    }

    private void handleUsbVideoStatus(Bundle result) {
        boolean playing = mVideoPlayer.isPlaying();
        result.putInt("status", playing ? 1 : 2);
        result.putInt("rw", 2); // 2:read
        EventBus.getDefault().post(result, "usb_video_play_pause");
    }

    private void handleUsbVideoPauseOrPlay(int status, Bundle result) {
        XMToast.showToast(VideoPlayActivity.this, "Handle Play Or Pause : " + status + " , isPlaying " + isUSBVideoReadyPlaying());
        switch (status) {
            case 1: // play 会自动播放 错误监听 然后传输操作结果
//                mVideoPlayer.getUrl()
                if (!isUSBVideoReadyPlaying()) {
                    mVideoPlayer.restart();
                }
                break;
            case 2: // pause
                pauseUsbVideo();
                break;
        }
        result.putInt("status", status);
        result.putInt("rw", 1); // 2:write
        EventBus.getDefault().post(result, "usb_video_play_pause");
    }

    private void pauseUsbVideo() {
        if (mVideoPlayer != null && (mVideoPlayer.isPlaying() || mVideoPlayer.isBufferingPlaying())) {
            mVideoPlayer.pause();
        }
    }

    private boolean isUSBVideoReadyPlaying() {
        return mVideoPlayer != null && (mVideoPlayer.isPlaying() || mVideoPlayer.isBufferingPlaying());
    }

    private void bindView() {
        mVideoPlayer = findViewById(R.id.nice_video_player);
        LinearLayout testLayout = findViewById(R.id.ll_test);
        testLayout.setVisibility(BuildConfig.DEBUG ? View.VISIBLE : View.GONE);
    }

    private void initView() {
        mPlayerController = new TxVideoPlayerController(this);
        mPlayerController.setNiceClickPreviosAndNextListener(this);
        mVideoPlayer.setController(mPlayerController);
        mPlayerController.setPlayerStatusChangeListener(this);
    }

    private void initData() {
        mVideoIndex = getIntent().getIntExtra(XkanConstants.VIDEO_INDEX, -1);
        mVideoType = getIntent().getStringExtra(XkanConstants.FROM_TYPE);
        if (StringUtil.isEmpty(mVideoType)) {
            mVideoType = XkanConstants.FROM_VIDEO;
        }
        mVideoVm = ViewModelProviders.of(this).get(XmVideoVm.class);
        mVideoVm.getmVideos().observe(this, new Observer<XmResource<List<UsbMediaInfo>>>() {
            @Override
            public void onChanged(@Nullable XmResource<List<UsbMediaInfo>> listXmResource) {
                listXmResource.handle(new OnCallback<List<UsbMediaInfo>>() {
                    @Override
                    public void onSuccess(List<UsbMediaInfo> data) {
                        currVideoList = data;
                        if (ListUtils.isEmpty(data)) {
                            XMToast.showToast(VideoPlayActivity.this, "USB视频列表为空");
                            return;
                        }
                        if (mVideoIndex < 0) {
                            mVideoIndex = 0;
                        }
                        XMToast.showToast(VideoPlayActivity.this, "Play Video at Index " + mVideoIndex);
                        setVideo(data.get(mVideoIndex));

                        Bundle result = new Bundle();
                        result.putInt("status", 1);
                        result.putInt("rw", 1); // 2:write
                        EventBus.getDefault().post(result, "usb_video_play_pause");
                    }
                });
            }
        });
        mVideoVm.fetchmVideos(mVideoType);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    private void setVideo(UsbMediaInfo mediaInfo) {
        mVideoPlayer.setUrl(mediaInfo.getPath());

        mPlayerController.setTitle(FileUtils.getFileNameNoEx(mediaInfo.getMediaName()));
        mPlayerController.setSize(ByteUtils.getFileSize(mediaInfo.getSize()));

        mPlayerController.setSpeed(DEFAULT_SPEED);
        ImageLoader.with(this)
                .load(Uri.fromFile(new File(mediaInfo.getPath())))
                .error(R.drawable.icon_video_default)
                .placeholder(R.drawable.icon_video_default)
                .into(mPlayerController.imageView());
        mVideoPlayer.start();
    }

    @Override
    public void onClickPrevious() {
        if (ListUtils.isEmpty(currVideoList)) {
            XMToast.showToast(this, "USB播放列表为空");
            return;
        }
        mVideoIndex--;
        if (mVideoIndex < 0) {
            mVideoIndex = currVideoList.size() - 1;
        }
        mVideoPlayer.releasePlayer();
        setVideo(currVideoList.get(mVideoIndex));

        mOpearteOkF = true;
    }

    @Override
    public void onClickNext() {
        if (ListUtils.isEmpty(currVideoList)) {
            XMToast.showToast(this, "USB播放列表为空");
            return;
        }
        mVideoIndex++;
        Log.d(TAG, "onClickNext: " + mVideoIndex);
        if (mVideoIndex >= currVideoList.size()) {
            mVideoIndex = 0;
        }
        mVideoPlayer.releasePlayer();
        setVideo(currVideoList.get(mVideoIndex));
        mOpearteOkF = true;
    }

    @Override
    public void onClickClose() {
        postEvent();
        finish();
    }

    /**
     * 视频播放完成,播放下一个视频
     */
    @Override
    public void onFinish() {
        if (ListUtils.isEmpty(currVideoList)) {
            postEvent();
            finish();
        } else {
            mVideoIndex++;
            if (mVideoIndex >= currVideoList.size()) {
                mVideoIndex = 0;
            }
            mVideoPlayer.releasePlayer();
            setVideo(currVideoList.get(mVideoIndex));
        }
    }

    public void postEvent() {
        if (XkanConstants.FROM_ALL.equals(mVideoType)) {
            EventBus.getDefault().post(mVideoIndex, XkanConstants.XKAN_MAIN_VIDEO_POS);
        } else {
            EventBus.getDefault().post(mVideoIndex, XkanConstants.XKAN_VIDEO_POS);
        }
    }

    /**
     * 收到USB移除通知释放player关闭页面
     *
     * @param event
     */

    @Subscriber(tag = XkanConstants.RELEASE_MEDIAINFO)
    public void releaseMediaAndFinishActivity(String event) {
        NiceVideoPlayerManager.instance().releaseNiceVideoPlayer();
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        NiceVideoPlayerManager.instance().pauseNiceVideoPlayer();
    }

    @Override
    protected void onDestroy() {
        UsbMediaDataManager.getInstance().removeConnectListener();
        NiceVideoPlayerManager.instance().releaseNiceVideoPlayer();
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        postEvent();
        super.onBackPressed();
    }

    @Override
    public void onPlayerStatusChange(int state) {
//        Bundle result = new Bundle();
//        switch (state) {
//            case NiceVideoPlayer.STATE_PLAYING: // 播放中 控制只执行一次
//                if (triggered) return;
//                triggered = true;
//                new Handler().postDelayed(() -> {
//
//                }, 5000);
//                break;
//        }
    }

    public void play(View view) {
        startVideo(DistributeConstants.ACTION_SET_USB_VIDEO_PAUSE_PLAY, 1);

    }

    public void pause(View view) {
        startVideo(DistributeConstants.ACTION_SET_USB_VIDEO_PAUSE_PLAY, 2);
    }

    public void next(View view) {
        startVideo(DistributeConstants.ACTION_SET_USB_VIDEO_PREVIOUS_NEXT, 1);
    }

    public void pre(View view) {
        startVideo(DistributeConstants.ACTION_SET_USB_VIDEO_PREVIOUS_NEXT, 2);
    }

    private void startVideo(int action, int status) {
        Intent intent = new Intent(this, VideoPlayActivity.class);
        intent.putExtra("action", action);
        intent.putExtra("status", status);
        startActivity(intent);
    }

    @Override
    public void onConnection(UsbStatus status, List<String> mountPaths) {
        if (status == UsbStatus.MOUNTED) {
            showProgressDialog("U盘扫描中");
            UsbMediaDataManager.getInstance().fetchUsbMediaData(mountPaths.get(0), new UsbMediaSearchListener() {
                @Override
                public void onUsbMediaSearchFinished() {
                    //默认进入列表模式
                    dismissProgress();
                    mVideoVm.fetchmVideos(mVideoType);
                }
            });
        } else {
            finish();
        }
    }
}

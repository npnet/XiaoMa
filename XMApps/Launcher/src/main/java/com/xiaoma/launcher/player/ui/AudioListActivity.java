package com.xiaoma.launcher.player.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.discretescrollview.DSVOrientation;
import com.discretescrollview.DiscreteScrollView;
import com.discretescrollview.transform.ScaleTransformer;
import com.xiaoma.ad.utils.GsonUtil;
import com.xiaoma.autotracker.XmAutoTracker;
import com.xiaoma.center.IClientCallback;
import com.xiaoma.center.logic.local.Linker;
import com.xiaoma.center.logic.model.Request;
import com.xiaoma.center.logic.model.RequestHead;
import com.xiaoma.center.logic.model.Response;
import com.xiaoma.center.logic.model.SourceInfo;
import com.xiaoma.component.base.BaseActivity;
import com.xiaoma.launcher.R;
import com.xiaoma.launcher.common.constant.EventConstants;
import com.xiaoma.launcher.common.constant.LauncherConstants;
import com.xiaoma.launcher.player.adapter.AudioListAdapter;
import com.xiaoma.launcher.player.callback.LauncherPlayListener;
import com.xiaoma.launcher.player.manager.PlayerConnectHelper;
import com.xiaoma.launcher.player.model.AudioMusicMarkInfo;
import com.xiaoma.model.annotation.NormalOnClick;
import com.xiaoma.model.annotation.PageDescComponent;
import com.xiaoma.player.AudioConstants;
import com.xiaoma.player.AudioInfo;
import com.xiaoma.player.ProgressInfo;
import com.xiaoma.thread.ThreadDispatcher;
import com.xiaoma.utils.receiver.UsbDetector;

import java.util.ArrayList;
import java.util.List;

/**
 * 音频分类列表页面
 * Created by zhushi.
 * Date: 2019/1/15
 */
@PageDescComponent(EventConstants.PageDescribe.AudioListActivityPagePathDesc)
public class AudioListActivity extends BaseActivity implements DiscreteScrollView.OnItemChangedListener,
        DiscreteScrollView.ScrollStateChangeListener<RecyclerView.ViewHolder>, LauncherPlayListener,
        UsbDetector.UsbDetectListener {

    private static final int CURRENT_PAGE = -1;

    private List<AudioInfo> mList = new ArrayList<>();
    private DiscreteScrollView mPlayScrollView;
    private AudioListAdapter mAudioListAdapter;
    private ImageView mIvBack;
    private TextView mTvTitle;
    private TextView mAudioTitle;
    private TextView mAudioSubTitle;
    private View emptyView;

    //当前页数
    private int mCurrentPage = 0;
    //原始页数
    private int mOrginPage = 0;
    //总页数
    private int mTotalPage;
    //是否向上加载
    private boolean isUp;

    private PlayerConnectHelper connectHelper;
    //当前音频
    private AudioInfo currentAudioInfo;
    //从播放器传过来的音频
    private AudioInfo audioInfoFromLauncher;
    private boolean isInit = true;
    private int mPosition;
    //列表是否滑动
    private boolean isScroll;
    private boolean isEmptyList = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_audio_list);
        statusBarDividerGone();
        initView();
        setTitle();
        initData();
    }

    private void initView() {
        audioInfoFromLauncher = getIntent().getParcelableExtra(AudioMainFragment.AUDIO_INFO);
        if (audioInfoFromLauncher == null) {
            audioInfoFromLauncher = new AudioInfo();
        }

        mIvBack = findViewById(R.id.iv_back);
        mIvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            @NormalOnClick(EventConstants.NormalClick.AUDIO_LIST_CLOSE)
            public void onClick(View v) {
                finish();
            }
        });
        mTvTitle = findViewById(R.id.tv_title);
        mPlayScrollView = findViewById(R.id.play_list);
        mAudioTitle = findViewById(R.id.audio_title);
        mAudioSubTitle = findViewById(R.id.audio_sub_title);
        emptyView = findViewById(R.id.empty_view);

        mPlayScrollView.addScrollStateChangeListener(this);
        mPlayScrollView.addOnItemChangedListener(this);

        mAudioListAdapter = new AudioListAdapter();
        mPlayScrollView.setOrientation(DSVOrientation.HORIZONTAL);
        mPlayScrollView.setSlideOnFling(true);
        mPlayScrollView.setAdapter(mAudioListAdapter);
        //列表项缩小倍率
        mPlayScrollView.setItemTransformer(new ScaleTransformer.Builder().setMinScale(0.8f).build());

        mAudioListAdapter.setEnableLoadMore(true);
        mAudioListAdapter.setUpFetchEnable(true);
//        mAudioListAdapter.setLoadMoreView(new TravelLoadMoreView());
        //右滑加载
        mAudioListAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                if (mOrginPage + 1 >= mTotalPage) {
                    notifyLoadState(LauncherConstants.END);

                } else {
                    isUp = false;
                    mOrginPage++;
                    ThreadDispatcher.getDispatcher().post(new Runnable() {
                        @Override
                        public void run() {
                            searchAudioList(audioInfoFromLauncher.getAudioType(), mOrginPage);
                        }
                    });
                }
            }
        }, mPlayScrollView);
        //左滑加载
        mAudioListAdapter.setUpFetchListener(new BaseQuickAdapter.UpFetchListener() {
            @Override
            public void onUpFetch() {
                if (mCurrentPage > 0) {
                    isUp = true;
                    mCurrentPage--;
                    ThreadDispatcher.getDispatcher().post(new Runnable() {
                        @Override
                        public void run() {
                            searchAudioList(audioInfoFromLauncher.getAudioType(), mCurrentPage);
                        }
                    });

                } else {
                    mAudioListAdapter.setUpFetching(false);
                    mAudioListAdapter.setUpFetchEnable(false);
                    mPlayScrollView.setNestedScrollingEnabled(true);
                }
            }
        });
        //列表点击
        mAudioListAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                mPosition = position;
                AudioInfo clickItem = mList.get(position);
                //如果当前项和点击项是同一个
                if (currentAudioInfo == clickItem) {
                    AudioMusicMarkInfo audioMusicMarkInfo = new AudioMusicMarkInfo();
                    audioMusicMarkInfo.id = mList.get(position).getUniqueId() + "";
                    audioMusicMarkInfo.value = mList.get(position).getAudioType() + "";
                    audioMusicMarkInfo.h = "";
                    audioMusicMarkInfo.i = "";
                    if (currentAudioInfo.getPlayState() == AudioConstants.AudioStatus.PLAYING) {
                        XmAutoTracker.getInstance().onEvent(EventConstants.NormalClick.AUDIO_STOP_LISTITEM,
                                GsonUtil.toJson(audioMusicMarkInfo),
                                EventConstants.PagePath.AudioListActivityPath,
                                EventConstants.PageDescribe.AudioListActivityPagePathDesc);
                        //暂停
                        connectHelper.pauseAudio(currentAudioInfo.getAudioType());

                    } else {
                        XmAutoTracker.getInstance().onEvent(EventConstants.NormalClick.AUDIO_PLAY_LISTITEM,
                                GsonUtil.toJson(audioMusicMarkInfo),
                                EventConstants.PagePath.AudioListActivityPath,
                                EventConstants.PageDescribe.AudioListActivityPagePathDesc);
                        //播放
                        clickPlayAudio(currentAudioInfo, position);
                    }

                } else {
                    //滑动到position位置
                    mPlayScrollView.smoothScrollToPosition(position);
                }
            }
        });

        connectHelper = PlayerConnectHelper.getInstance();
        connectHelper.setPlayListener(this);
        //usb音频添加usb连接监听
        if (audioInfoFromLauncher.getAudioType() == AudioConstants.AudioTypes.MUSIC_LOCAL_USB) {
            UsbDetector.getInstance().addUsbDetectListener(this);
        }
    }

    private void setTitle() {
        //设置标题
        if (getString(R.string.favorite_music).equals(audioInfoFromLauncher.getTitle())) {
            mTvTitle.setText(getString(R.string.favorite_music));

        } else if (audioInfoFromLauncher.getAudioType() == AudioConstants.AudioTypes.MUSIC_ONLINE_KUWO &&
                audioInfoFromLauncher.getLauncherCategoryId() == 0) {
            mTvTitle.setText(getString(R.string.audio_music_list));

        } else {
            mTvTitle.setText(audioInfoFromLauncher.getTitle());
        }
    }

    private void initData() {
        if (getString(R.string.favorite_music).equals(audioInfoFromLauncher.getTitle())) {
            ThreadDispatcher.getDispatcher().post(new Runnable() {
                @Override
                public void run() {
                    //获取音乐收藏数据
                    searchMusicFavoriteList(AudioConstants.AudioTypes.MUSIC_ONLINE_KUWO);
                }
            });

        } else {
            ThreadDispatcher.getDispatcher().post(new Runnable() {
                @Override
                public void run() {
                    //获取列表数据
                    searchAudioList(audioInfoFromLauncher.getAudioType(), CURRENT_PAGE);
                }
            });
        }
    }

    private void searchMusicFavoriteList(int audioType) {
        showProgressDialog(R.string.base_loading);
        SourceInfo local = new SourceInfo(getPackageName(), LauncherConstants.LAUNCHER_PORT);
        Bundle searchPurpose = new Bundle();
        searchPurpose.putInt(AudioConstants.BundleKey.SEARCH_ACTION, AudioConstants.SearchAction.FAVORITE);
        Request searchRequest = new Request(local, new RequestHead(PlayerConnectHelper.getInstance().getSourceInfoByAudioType(audioType),
                AudioConstants.Action.SEARCH), searchPurpose);

        Linker.getInstance().request(searchRequest, new IClientCallback.Stub() {
            @Override
            public void callback(final Response response) {
                ThreadDispatcher.getDispatcher().postOnMain(new Runnable() {
                    @Override
                    public void run() {
                        dismissProgress();
                        handResponse(response, CURRENT_PAGE);
                    }
                });
            }
        });
    }

    /**
     * 获取列表数据
     *
     * @param audioType
     * @param page
     */
    public void searchAudioList(final int audioType, final int page) {
        showProgressDialog(R.string.base_loading);
        SourceInfo local = new SourceInfo(getPackageName(), LauncherConstants.LAUNCHER_PORT);
        Bundle searchPurpose = new Bundle();
        if (page == CURRENT_PAGE) {
            searchPurpose.putInt(AudioConstants.BundleKey.SEARCH_ACTION, AudioConstants.SearchAction.CURRENT);

        } else {
            searchPurpose.putInt(AudioConstants.BundleKey.SEARCH_ACTION, AudioConstants.SearchAction.PAGE_LIST);
            searchPurpose.putInt(AudioConstants.BundleKey.CURRENT_PAGE, page);
        }
        Request searchRequest = new Request(local, new RequestHead(connectHelper.getSourceInfoByAudioType(audioType),
                AudioConstants.Action.SEARCH), searchPurpose);

        Linker.getInstance().request(searchRequest, new IClientCallback.Stub() {
            @Override
            public void callback(final Response response) {
                ThreadDispatcher.getDispatcher().postOnMain(new Runnable() {
                    @Override
                    public void run() {
                        dismissProgress();
                        handResponse(response, page);
                    }
                });
            }
        });
    }

    /**
     * 解析Response
     *
     * @param response
     * @param page
     */
    private void handResponse(Response response, int page) {
        Bundle extra = response.getExtra();
        extra.setClassLoader(AudioInfo.class.getClassLoader());

        int responseCode = extra.getInt(AudioConstants.BundleKey.AUDIO_RESPONSE_CODE);
        if (responseCode == AudioConstants.AudioResponseCode.SUCCESS) {
            //列表数据
            final List<AudioInfo> audioInfoList = extra.getParcelableArrayList(AudioConstants.BundleKey.AUDIO_LIST);
            //列表空处理
            if (audioInfoList == null || audioInfoList.size() == 0) {
                if (isEmptyList && page == CURRENT_PAGE) {
                    emptyView.setVisibility(View.VISIBLE);
                }
                return;
            }
            isEmptyList = false;
            emptyView.setVisibility(View.GONE);
            //分页信息
            int[] mPageInfo = extra.getIntArray(AudioConstants.BundleKey.PAGE_INFO);
            if (mPageInfo != null) {
                //当前页
                mCurrentPage = mPageInfo[0];
                //总页数
                mTotalPage = mPageInfo[1];
            }

            if (isUp) {
                //向上加载时数据添加到头
                mList.addAll(0, audioInfoList);
                mAudioListAdapter.addData(0, audioInfoList);

            } else {
                mList.addAll(audioInfoList);
                mAudioListAdapter.addData(audioInfoList);
            }
            //获取当前音频在列表的位置,并同步播放状态
            int playIndex = getIndexAndSynPlayState(mList);

            //初次请求的数据是第几页
            if (page == CURRENT_PAGE) {
                mOrginPage = mCurrentPage;
            }
            //列表滑动到当前播放项
            if (isInit) {
                mPlayScrollView.scrollToPosition(playIndex);
            }
            notifyLoadState(LauncherConstants.COMPLETE);

        } else {
            notifyLoadState(LauncherConstants.FAILED);
        }
    }

    /**
     * 获取当前音频在列表的位置,并同步播放状态
     *
     * @param audioInfoList
     * @return 当前音频在列表的位置
     */
    private int getIndexAndSynPlayState(List<AudioInfo> audioInfoList) {
        int playIndex = 0;
        if (audioInfoFromLauncher == null) {
            return playIndex;
        }

        if (audioInfoFromLauncher.getAudioType() == AudioConstants.AudioTypes.MUSIC_LOCAL_USB) {
            //usb比较usbPath
            for (int i = 0; i < audioInfoList.size(); i++) {
                AudioInfo audioInfo = audioInfoList.get(i);
                //如果usbPath相同
                if (audioInfo != null && audioInfo.getUsbMusicPath().equals(audioInfoFromLauncher.getUsbMusicPath())) {
                    playIndex = i;
                    audioInfo.setPlayState(audioInfoFromLauncher.getPlayState());
                    break;
                }
            }

        } else {
            for (int i = 0; i < audioInfoList.size(); i++) {
                AudioInfo audioInfo = audioInfoList.get(i);
                //如果节目id相同
                if (audioInfo != null && audioInfo.getUniqueId() == audioInfoFromLauncher.getUniqueId()) {
                    playIndex = i;
                    audioInfo.setPlayState(audioInfoFromLauncher.getPlayState());
                    break;
                }
            }
        }

        return playIndex;
    }

    private void notifyLoadState(final int state) {
        if (LauncherConstants.COMPLETE == state) {
            mAudioListAdapter.loadMoreComplete();

        } else if (LauncherConstants.END == state) {
            mAudioListAdapter.loadMoreEnd();

        } else if (LauncherConstants.FAILED == state) {
            mAudioListAdapter.loadMoreFail();
        }
        mPlayScrollView.setNestedScrollingEnabled(true);
    }

    @Override
    public void onPlayState(final int playState) {
        ThreadDispatcher.getDispatcher().postOnMain(new Runnable() {
            @Override
            public void run() {
                //设置当前音频播放状态
                if (currentAudioInfo != null) {
                    currentAudioInfo.setPlayState(playState);
                }
                mAudioListAdapter.notifyItemChanged(mPosition);
            }
        });
    }

    @Override
    public void onCurrentItemChanged(@Nullable RecyclerView.ViewHolder viewHolder, int adapterPosition) {
        if (adapterPosition < mList.size()) {
            AudioMusicMarkInfo audioMusicMarkInfo = new AudioMusicMarkInfo();
            audioMusicMarkInfo.id = mList.get(adapterPosition).getUniqueId() + "";
            audioMusicMarkInfo.value = mList.get(adapterPosition).getAudioType() + "";
            audioMusicMarkInfo.h = "";
            XmAutoTracker.getInstance().onEvent(EventConstants.NormalClick.LISTITEM,
                    GsonUtil.toJson(audioMusicMarkInfo),
                    EventConstants.PagePath.AudioListActivityPath,
                    EventConstants.PageDescribe.AudioListActivityPagePathDesc);
            mPosition = adapterPosition;
            for (int i = 0; i < mList.size(); i++) {
                mList.get(i).setCenterItem(false);
            }
            onItemChanged(mList.get(adapterPosition), adapterPosition);
        }
    }

    /**
     * item change
     *
     * @param audioInfo
     * @param position
     */
    private void onItemChanged(AudioInfo audioInfo, int position) {
        if (audioInfo == null) {
            return;
        }
        audioInfo.setCenterItem(true);

        mAudioTitle.setText(audioInfo.getTitle());
        mAudioSubTitle.setText(audioInfo.getSubTitle());

        if (!mPlayScrollView.isComputingLayout()) {
            mAudioListAdapter.notifyDataSetChanged();
        }
        //处理初次进页面就播放的问题
        if (isInit || currentAudioInfo == audioInfo) {
            currentAudioInfo = audioInfo;
            return;
        }
        currentAudioInfo = audioInfo;

        //滑动播放
        scrollPlayAudio(currentAudioInfo, position);
    }

    /**
     * 点击item项播放
     *
     * @param audioInfo
     * @param position
     */
    private void clickPlayAudio(AudioInfo audioInfo, int position) {
        connectHelper.removePlayListener(this);

        if (audioInfo.getAudioType() == AudioConstants.AudioTypes.MUSIC_LOCAL_USB) {
            //usb音频
            connectHelper.playUsbAudio(audioInfo.getAudioType(), AudioConstants.PlayAction.PLAY_LIST_AT_INDEX,
                    audioInfo.getUsbMusicPath(), position);

        } else if (audioInfo.getAudioType() == AudioConstants.AudioTypes.XTING_LOCAL_FM) {
            //本地FM
            connectHelper.playAudio(audioInfo.getAudioType(), AudioConstants.PlayAction.PLAY_LIST_AT_INDEX,
                    (int) audioInfo.getUniqueId(), position);

        } else {
            if (getString(R.string.favorite_music).equals(audioInfoFromLauncher.getTitle())) {
                //音乐收藏
                if (audioInfo.isCenterItem() && audioInfoFromLauncher.getUniqueId() == audioInfo.getUniqueId()) {
                    connectHelper.playAudio(audioInfo.getAudioType(), AudioConstants.PlayAction.DEFAULT);

                } else {
                    connectHelper.playAudio(audioInfo.getAudioType(), AudioConstants.PlayAction.PLAY_KW_FAVORITE_LIST,
                            (int) audioInfo.getUniqueId(), position);
                }
                audioInfoFromLauncher = audioInfo;

            } else {
                //在线音乐、电台
                if (audioInfo.isCenterItem()) {
                    connectHelper.playAudio(audioInfo.getAudioType(), AudioConstants.PlayAction.DEFAULT);

                } else {
                    connectHelper.playAudio(audioInfo.getAudioType(), AudioConstants.PlayAction.PLAY_LIST_AT_INDEX,
                            (int) audioInfo.getUniqueId(), position);
                }
            }
        }
        connectHelper.setPlayListener(this);
    }

    /**
     * 滑动播放当前音频
     */
    private void scrollPlayAudio(AudioInfo audioInfo, int position) {
        connectHelper.removePlayListener(this);

        if (audioInfo.getAudioType() == AudioConstants.AudioTypes.MUSIC_LOCAL_USB) {
            //usb音频
            connectHelper.playUsbAudio(audioInfo.getAudioType(), AudioConstants.PlayAction.PLAY_LIST_AT_INDEX,
                    audioInfo.getUsbMusicPath(), position);

        } else if (audioInfo.getAudioType() == AudioConstants.AudioTypes.XTING_LOCAL_FM) {
            //本地FM
            connectHelper.playAudio(audioInfo.getAudioType(), AudioConstants.PlayAction.PLAY_LIST_AT_INDEX,
                    (int) audioInfo.getUniqueId(), position);

        } else {
            if (getString(R.string.favorite_music).equals(audioInfoFromLauncher.getTitle())) {
                connectHelper.playAudio(audioInfo.getAudioType(), AudioConstants.PlayAction.PLAY_KW_FAVORITE_LIST,
                        (int) audioInfo.getUniqueId(), position);

            } else {
                connectHelper.playAudio(audioInfo.getAudioType(), AudioConstants.PlayAction.PLAY_LIST_AT_INDEX,
                        (int) audioInfo.getUniqueId(), position);
            }
        }

        connectHelper.setPlayListener(this);
    }

    @Override
    protected boolean isAppNeedShowNaviBar() {
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        connectHelper.removePlayListener(this);
        if (audioInfoFromLauncher.getAudioType() == AudioConstants.AudioTypes.MUSIC_LOCAL_USB) {
            UsbDetector.getInstance().removeUsbDetectListener(this);
        }
    }

    @Override
    public void onScrollStart(@NonNull RecyclerView.ViewHolder currentItemHolder, int adapterPosition) {
    }

    @Override
    public void onScrollEnd(@NonNull RecyclerView.ViewHolder currentItemHolder, int adapterPosition) {
        isScroll = false;
        if (adapterPosition == mList.size() && mList.size() > 0) {
            mPlayScrollView.smoothScrollToPosition(adapterPosition - 1);
        }
    }

    @Override
    public void onScroll(float scrollPosition, int currentPosition, int newPosition, @Nullable RecyclerView.ViewHolder
            currentHolder, @Nullable RecyclerView.ViewHolder newCurrent) {
        isInit = false;
        isScroll = true;
    }

    @Override
    public void onAudioInfo(final AudioInfo audioInfo) {
        ThreadDispatcher.getDispatcher().postOnMain(new Runnable() {
            @Override
            public void run() {
                if (!isScroll && audioInfo.getAudioType() != AudioConstants.AudioTypes.XTING_LOCAL_FM) {
                    syncAudioInfo(audioInfo);
                }

                if (audioInfo.getAudioType() == AudioConstants.AudioTypes.MUSIC_LOCAL_BT) {
                    onPlayState(AudioConstants.AudioStatus.PAUSING);
                }

                //当前在USB列表页面，播放非USB音乐时，刷新列表状态
                if (audioInfoFromLauncher.getTitle() != null &&
                        audioInfoFromLauncher.getTitle().contains("USB") &&
                        audioInfo.getAudioType() != AudioConstants.AudioTypes.MUSIC_LOCAL_USB) {
                    onPlayState(AudioConstants.AudioStatus.PAUSING);
                }
            }
        });
    }

    /**
     * 同步自动切换的音频数据
     *
     * @param audioInfo
     */
    private void syncAudioInfo(AudioInfo audioInfo) {
        for (int i = 0; i < mList.size(); i++) {
            if (audioInfo.getAudioType() == AudioConstants.AudioTypes.MUSIC_LOCAL_USB) {
                if (mList.get(i).getUsbMusicPath().equals(audioInfo.getUsbMusicPath())) {
                    currentAudioInfo = mList.get(i);
                    mPlayScrollView.smoothScrollToPosition(i);
                    break;
                }

            } else {
                if (mList.get(i).getUniqueId() == audioInfo.getUniqueId()) {
                    currentAudioInfo = mList.get(i);
                    mPlayScrollView.smoothScrollToPosition(i);
                    break;
                }
            }
        }
    }

    @Override
    public void onDataSource(int dataSource) {
    }

    @Override
    public void onAudioFavorite(boolean favorite) {
    }

    @Override
    public void onProgress(ProgressInfo progressInfo) {
    }

    //-----------------------USB连接状态回调--------------------------------

    /**
     * USB拔出时finish页面
     */
    @Override
    public void removed() {
        finish();
    }

    @Override
    public void noUsbMounted() {
    }

    @Override
    public void inserted() {
    }

    @Override
    public void mounted(List<String> mountPaths) {
    }

    @Override
    public void mountError() {
    }
    //-----------------------USB连接状态回调--------------------------------
}

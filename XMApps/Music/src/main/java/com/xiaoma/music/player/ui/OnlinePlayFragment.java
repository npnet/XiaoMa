package com.xiaoma.music.player.ui;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.xiaoma.autotracker.XmAutoTracker;
import com.xiaoma.center.logic.CenterConstants;
import com.xiaoma.component.base.BaseFragment;
import com.xiaoma.component.base.VisibleFragment;
import com.xiaoma.component.nodejump.NodeConst;
import com.xiaoma.component.nodejump.NodeUtils;
import com.xiaoma.model.XmResource;
import com.xiaoma.model.annotation.NormalOnClick;
import com.xiaoma.model.annotation.PageDescComponent;
import com.xiaoma.model.annotation.ResId;
import com.xiaoma.music.OnlineMusicFactory;
import com.xiaoma.music.R;
import com.xiaoma.music.common.audiosource.AudioSource;
import com.xiaoma.music.common.audiosource.AudioSourceListener;
import com.xiaoma.music.common.audiosource.AudioSourceManager;
import com.xiaoma.music.common.constant.EventBusTags;
import com.xiaoma.music.common.constant.EventConstants;
import com.xiaoma.music.common.manager.MusicDbManager;
import com.xiaoma.music.common.manager.UploadPlayManager;
import com.xiaoma.music.common.model.PlayStatus;
import com.xiaoma.music.common.util.MusicFastPlayController;
import com.xiaoma.music.export.manager.AudioShareManager;
import com.xiaoma.music.kuwo.listener.OnMusicChargeListener;
import com.xiaoma.music.kuwo.listener.OnPlayControlListener;
import com.xiaoma.music.kuwo.model.XMListType;
import com.xiaoma.music.kuwo.model.XMMusic;
import com.xiaoma.music.kuwo.model.XMMusicList;
import com.xiaoma.music.kuwo.observer.KuwoPlayControlObserver;
import com.xiaoma.music.kuwo.proxy.XMKWPlayerOperateProxy;
import com.xiaoma.music.player.adapter.QualityListAdapter;
import com.xiaoma.music.player.model.CollectEventBean;
import com.xiaoma.music.player.model.MusicQualityModel;
import com.xiaoma.music.player.view.OnlineContentView;
import com.xiaoma.music.player.view.player.OnlinePlayListWindow;
import com.xiaoma.music.player.view.player.PlayerView;
import com.xiaoma.music.player.vm.OnlinePlayListVM;
import com.xiaoma.music.player.vm.PlayerVM;
import com.xiaoma.thread.ThreadDispatcher;
import com.xiaoma.ui.toast.XMToast;
import com.xiaoma.utils.FragmentUtils;
import com.xiaoma.vr.dispatch.annotation.Command;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;

import java.util.List;

import cn.kuwo.mod.playcontrol.PlayMode;

/**
 * @author zs
 * @date 2018/11/23 0023.
 */
@PageDescComponent(EventConstants.PageDescribe.onlinePlayFragment)
public class OnlinePlayFragment extends VisibleFragment
        implements View.OnClickListener, PlayerView.OnOperationListener, MusicFastPlayController.FastPlayProgressObserver {

    private OnlinePlayListWindow mPlayListWindow;
    private MusicQualityWindow qualityWindow;
    private ImageView mCollectionIv;
    private PlayerView mPlayerView;
    private OnlineContentView mContentView;
    private LyricFragment mLyricFragment;
    private boolean isLyric;
    private ImageView playListBtn;

    private PlayerVM mPlayerVM;

    public static OnlinePlayFragment newInstance() {
        return new OnlinePlayFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_play_online, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bindView(view);
        initView();
        initData();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onVisibleChange(boolean realVisible) {
        super.onVisibleChange(realVisible);
        if (realVisible) {
            MusicFastPlayController.getInstance().setFastPlayObserver(this);
        } else {
            MusicFastPlayController.getInstance().removeFastPlayObserver();
        }
    }

    @Command("关闭歌词")
    public void closeLyric() {
        ThreadDispatcher.getDispatcher().runOnMain(new Runnable() {
            @Override
            public void run() {
                if (isLyric) {
                    mContentView.setVisibility(View.VISIBLE);
                    switchFragment(mLyricFragment, false);
                    isLyric = false;
                }
            }
        });
    }

    private void initData() {
        mPlayerVM = ViewModelProviders.of(this).get(PlayerVM.class);
        mPlayerVM.getOnlineMusic().observe(this, new Observer<XMMusic>() {
            @Override
            public void onChanged(@Nullable XMMusic xmMusic) {
                if (xmMusic != null) {
                    mContentView.refreshView(xmMusic);
                }
            }
        });
        mPlayerVM.fetchOnlineMusic();
        mPlayerVM.getCurrentQualityModels().observe(this, new Observer<MusicQualityModel>() {
            @Override
            public void onChanged(@Nullable MusicQualityModel model) {
                if (model != null) {
                    if (model.getQualityText() != 0) {
                        boolean isNeedBuyVip = model.isNeedVip() && !OnlineMusicFactory.getKWLogin().isCarVipUser();
                        mContentView.setQualityViewData(model, isNeedBuyVip);
                        mLyricFragment.setQualityViewData(model, isNeedBuyVip);
                    }
                }
            }
        });
        mPlayerVM.fetchCurrentQuality(OnlineMusicFactory.getKWPlayer().getDownloadWhenPlayQuality());
        mPlayerVM.getQualityModels().observe(this, new Observer<XmResource<List<MusicQualityModel>>>() {
            @Override
            public void onChanged(@Nullable XmResource<List<MusicQualityModel>> listXmResource) {
                if (listXmResource != null) {
                    listXmResource.handle(new OnCallback<List<MusicQualityModel>>() {
                        @Override
                        public void onSuccess(List<MusicQualityModel> data) {
                            if (data != null && !data.isEmpty()) {
                                qualityWindow.refreshQualityList(data);
                            }
                        }

                        @Override
                        public void onLoading() {
                            qualityWindow.setLoading();
                        }

                        @Override
                        public void onFailure(String msg) {
                            qualityWindow.setFailure();
                        }
                    });
                }
            }
        });
        AudioSourceManager.getInstance().addAudioSourceListener(new AudioSourceListener() {
            @Override
            public void onAudioSourceSwitch(int preAudioSource, int currAudioSource) {
                ThreadDispatcher.getDispatcher().runOnMain(new Runnable() {
                    @Override
                    public void run() {
                        if (currAudioSource != AudioSource.ONLINE_MUSIC) {
                            try {
                                mPlayListWindow.dismiss();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });

            }
        });
    }

    private void bindView(View view) {
        mCollectionIv = view.findViewById(R.id.activity_player_collection);
        mPlayerView = view.findViewById(R.id.fragment_player_view);
        mContentView = view.findViewById(R.id.online_content_view);
        mContentView.setVisibility(View.VISIBLE);
        mContentView.setOnQualityClickListener(this);
        mContentView.setOnVipViewClickListener(new OnlineContentView.OnVipViewClickListener() {
            @Override
            public void OnVipViewClick() {
                handleVipClick();
            }
        });
        mLyricFragment = LyricFragment.newInstance();
        mLyricFragment.setOnChildClickListener(new LyricFragment.OnChildClickListener() {
            @Override
            public void onQualityClick() {
                showQualityWindow();
            }

            @Override
            public void OnBuyVipViewClick() {
                handleVipClick();
            }
        });

        mCollectionIv.setOnClickListener(this);
        playListBtn = view.findViewById(R.id.activity_player_playlist);
        playListBtn.setOnClickListener(this);
        view.findViewById(R.id.activity_player_lyric).setOnClickListener(this);
        mPlayerView.setOnOperationListener(this);
        OnlineMusicFactory.getKWMessage().addPlayStateListener(kwListener);
    }

    private void handleVipClick() {
        //跳转到会员中心节点
        NodeUtils.jumpTo(mContext, CenterConstants.MUSIC, "com.xiaoma.music.MainActivity", NodeConst.MUSIC.MAIN_ACTIVITY
                + "/" + NodeConst.MUSIC.MAIN_FRAGMENT
                + "/" + NodeConst.MUSIC.MINE_FRAGMENT
                + "/" + NodeConst.MUSIC.OPEN_VIP_CENTER);
    }

    private void initView() {
        mPlayListWindow = new OnlinePlayListWindow(getActivity());
        qualityWindow = new MusicQualityWindow(getActivity());
        qualityWindow.setOnItemChildClickListener(new QualityListAdapter.OnItemChildClickListener() {
            @Override
            public void onQualityClick(MusicQualityModel model) {
                if (model.isNeedVip()) {
                    //需要vip
                    if (!OnlineMusicFactory.getKWLogin().isCarVipUser()) {
                        //当前用户不是vip或者未登录，跳转到会员中心页面
                        handleVipClick();
                    } else {
                        //当前用户是vip,切换音质
                        boolean isSuccess = OnlineMusicFactory.getKWPlayer().setDownloadWhenPlayQuality(model.getQuality());
//                        MusicQualityManager.getInstance().setCustomQuality(model.getQuality());
                        qualityWindow.show(false);
                        mPlayerVM.fetchCurrentQuality(OnlineMusicFactory.getKWPlayer().getDownloadWhenPlayQuality());
                    }
                } else if (model.isNeedBuy()) {
                    // TODO:loren 2019/7/3 0003 待产品确认付费音质交互
                    //需要付费
                    if (!OnlineMusicFactory.getKWLogin().isUserLogon()) {
//                        showToast("需要付费，用户未登录");
                    } else {
//                        showToast("显示付费二维码");
                    }
                } else {
                    boolean isSuccess = OnlineMusicFactory.getKWPlayer().setDownloadWhenPlayQuality(model.getQuality());
//                    MusicQualityManager.getInstance().setCustomQuality(model.getQuality());
                    qualityWindow.show(false);
                    mPlayerVM.fetchCurrentQuality(OnlineMusicFactory.getKWPlayer().getDownloadWhenPlayQuality());
                }
            }

            @Override
            public void onBuyVipClick() {
                handleVipClick();
            }
        });
        XMMusic music = OnlineMusicFactory.getKWPlayer().getNowPlayingMusic();
        mPlayerView.setKwImageCover(music);
        initPlayStatus();
        initCollectStatus(music);
    }

    @Override
    public void onResume() {
        super.onResume();
        checkBtnEnable();
        OnlineMusicFactory.getKWLogin().fetchVipInfo();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            checkBtnEnable();
        }
    }

    private void checkBtnEnable() {
        XMMusicList nowPlayingList = OnlineMusicFactory.getKWPlayer().getNowPlayingList();
        if (nowPlayingList != null) {
            if (nowPlayingList.getType() == XMListType.LIST_RADIO) {
                playListBtn.setEnabled(false);
            } else {
                playListBtn.setEnabled(true);
            }
        }
    }

    @Override
    public boolean handleJump(String nextNode) {
        super.handleJump(nextNode);
        switch (nextNode) {
            case NodeConst.MUSIC.OPEN_PLAY_LIST:
                ThreadDispatcher.getDispatcher().postOnMainDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (!mPlayListWindow.isShowing()) {
                            getView().findViewById(R.id.activity_player_playlist).performClick();
                        }
                    }
                }, 100);
                return true;
            case NodeConst.MUSIC.OPEN_PLAY_LYRIC:
                ThreadDispatcher.getDispatcher().postOnMain(new Runnable() {
                    @Override
                    public void run() {
                        if (!isLyric) {
                            mContentView.setVisibility(View.GONE);
                            switchFragment(mLyricFragment, true);
                            isLyric = true;
                        }
                    }
                });
                return true;
            default:
                return false;
        }
    }

    @Override
    public String getThisNode() {
        return NodeConst.MUSIC.PLAYER_FRAGMENT;
    }

    @Command("关闭播放列表")
    public void closePlayerList() {
        ThreadDispatcher.getDispatcher().runOnMain(new Runnable() {
            @Override
            public void run() {
                if (mPlayListWindow != null && mPlayListWindow.isShowing()) {
                    mPlayListWindow.display(false);
                }
            }
        });
    }

    @Command("打开播放列表")
    public void openPlayerList() {
        ThreadDispatcher.getDispatcher().postOnMainDelayed(new Runnable() {
            @Override
            public void run() {
                if (!mPlayListWindow.isShowing()) {
                    getView().findViewById(R.id.activity_player_playlist).performClick();
                }
            }
        }, 100);
    }

    private void initPlayStatus() {
        final int status = OnlineMusicFactory.getKWPlayer().getStatus();
        switch (status) {
            case PlayStatus.BUFFERING:
                mPlayerView.updatePlayStatus(PlayStatus.BUFFERING);
                mContentView.updatePlayStatus(PlayStatus.BUFFERING);
                break;
            case PlayStatus.PLAYING:
                mPlayerView.updatePlayStatus(PlayStatus.PLAYING);
                mContentView.updatePlayStatus(PlayStatus.PLAYING);
                break;
            case PlayStatus.PAUSE:
            case PlayStatus.STOP:
            case PlayStatus.FAILED:
                mPlayerView.updatePlayStatus(PlayStatus.PAUSE);
                mContentView.updatePlayStatus(PlayStatus.PAUSE);
                break;
            default:
                mPlayerView.updatePlayStatus(PlayStatus.PAUSE);
                mContentView.updatePlayStatus(PlayStatus.PAUSE);
                break;
        }
    }

    private void initCollectStatus(XMMusic music) {
        if (music == null) {
            return;
        }
        XMMusic collection = MusicDbManager.getInstance().queryCollectionMusicById(music.getRid());
        if (collection != null) {
            mCollectionIv.setImageResource(R.drawable.icon_collect_selector);
        } else {
            mCollectionIv.setImageResource(R.drawable.icon_uncollect_selector);
        }
    }

    @Override
    public void musicOperate() {
        XMKWPlayerOperateProxy player = OnlineMusicFactory.getKWPlayer();
        if (player.isPlaying()) {
            player.pause();
        } else {
            player.continuePlay();
        }
    }

    @Override
    public void nextMusic() {
        int playMode = OnlineMusicFactory.getKWPlayer().getPlayMode();
        int index = OnlineMusicFactory.getKWPlayer().getNowPlayMusicIndex();
        XMMusicList nowPlayingList = OnlineMusicFactory.getKWPlayer().getNowPlayingList();
        if (nowPlayingList == null) {
            return;
        }
        int size = nowPlayingList.toList().size();
        if (PlayMode.MODE_ALL_ORDER == playMode && index == size - 1) {
            XMToast.showToast(getActivity(), getString(R.string.already_last));
            return;
        }
        OnlineMusicFactory.getKWPlayer().playNext();
    }

    @Override
    public void preMusic() {
        int playMode = OnlineMusicFactory.getKWPlayer().getPlayMode();
        int index = OnlineMusicFactory.getKWPlayer().getNowPlayMusicIndex();
        if (PlayMode.MODE_ALL_ORDER == playMode && index == 0) {
            XMToast.showToast(getActivity(), getString(R.string.already_first));
            return;
        }
        OnlineMusicFactory.getKWPlayer().playPre();
    }

    @Override
    public void scrollUp() {
        PlayerActivity parentActivity = getParentActivity();
        if (parentActivity != null) {
            parentActivity.jumpToAlbumSwitchFragment(AlbumSwitchFragment.newInstance(), parentActivity.FRAGMENT_ALBUM_SWITCH_TAG);
        }
        XmAutoTracker.getInstance().onEvent(EventConstants.NormalClick.ACTION_SLIDE_UP,
                "OnlinePlayFragment", EventConstants.PageDescribe.onlinePlayFragment);
    }

    @NormalOnClick({EventConstants.NormalClick.playlist, EventConstants.NormalClick.collection, EventConstants.NormalClick.lyric, EventConstants.NormalClick.quality})
    @ResId({R.id.activity_player_playlist, R.id.activity_player_collection, R.id.activity_player_lyric, R.id.online_music_quality_view})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.activity_player_playlist:
                fetchPlayList();
                break;
            case R.id.activity_player_collection:
                XMMusic nowPlayingMusic = OnlineMusicFactory.getKWPlayer().getNowPlayingMusic();
                postEventToCollection(nowPlayingMusic);
                break;
            case R.id.activity_player_lyric:
                if (!isLyric) {
                    mContentView.setVisibility(View.GONE);
                    switchFragment(mLyricFragment, true);
                } else {
                    switchFragment(mLyricFragment, false);
                    mContentView.setVisibility(View.VISIBLE);
                }
                isLyric = !isLyric;
                break;
            case R.id.quality_view_quality_tv:
                showQualityWindow();
                break;
            default:
                break;
        }
    }

    private void showQualityWindow() {
        qualityWindow.show(true);
        mPlayerVM.fetchMusicQualityModel();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if(mPlayListWindow!=null){
            mPlayListWindow.release();
        }
        OnlineMusicFactory.getKWMessage().removePlayStateListener(kwListener);
    }

    private void fetchPlayList() {
        OnlinePlayListVM playListVM = ViewModelProviders.of(this).get(OnlinePlayListVM.class);
        playListVM.fetchOnlinePlaylist();
        playListVM.getPlayList().observe(this, new Observer<List<XMMusic>>() {
            @Override
            public void onChanged(@Nullable List<XMMusic> playingMusics) {
                mPlayListWindow.setData(playingMusics);
                OnlineMusicFactory.getKWPlayer().chargeMusics(playingMusics, new OnMusicChargeListener() {
                    @Override
                    public void onChargeSuccess(List<XMMusic> musics, List<Integer> chargeResults) {
                        if (chargeResults != null && !chargeResults.isEmpty()) {
                            mPlayListWindow.setChargeTypes(chargeResults);
                        }
                    }

                    @Override
                    public void onChargeFailed(String msg) {

                    }
                });
            }
        });
        mPlayListWindow.display(true);
    }

    private void postEventToCollection(XMMusic nowPlayingMusic) {
        if (nowPlayingMusic == null) {
            return;
        }
        long rid = nowPlayingMusic.getRid();
        XMMusic favoriteMusic = MusicDbManager.getInstance().queryCollectionMusicById(rid);
        CollectEventBean bean = new CollectEventBean();
        bean.setFavorite(favoriteMusic != null);
        bean.setMusic(nowPlayingMusic);
        if (favoriteMusic != null) {
            mCollectionIv.setImageResource(R.drawable.icon_uncollect_selector);
            showToast(mContext.getString(R.string.cancel_favorite));
            MusicDbManager.getInstance().deleteCollectionMusic(favoriteMusic);
            AudioShareManager.getInstance().shareOnlineAudioFavorite(false);
        } else {
            AudioShareManager.getInstance().shareOnlineAudioFavorite(true);
            mCollectionIv.setImageResource(R.drawable.icon_collect_selector);
            XMToast.toastSuccess(mContext, mContext.getString(R.string.favorite_success));
            MusicDbManager.getInstance().saveCollectionMusic(nowPlayingMusic);
            UploadPlayManager.getInstance().uploadCollectMusicCount();
        }
        EventBus.getDefault().post(bean, EventBusTags.MUSIC_COLLECTION);
    }

    @Subscriber(tag = EventBusTags.MUSIC_COLLECTION)
    public void onAddHistoryEvent(CollectEventBean music) {
        if (music != null) {
            if (music.isFavorite()) {
                mCollectionIv.setImageResource(R.drawable.icon_uncollect_selector);
            } else {
                mCollectionIv.setImageResource(R.drawable.icon_collect_selector);
            }
        }
    }

    public void switchFragment(BaseFragment fragment, boolean isShow) {
        if (fragment == null) {
            return;
        }
        if (!isShow) {
            FragmentUtils.hide(fragment);
            return;
        }
        if (!fragment.isAdded()) {
            FragmentUtils.add(getChildFragmentManager(), fragment, R.id.activity_player_content_fl);
        } else {
            FragmentUtils.show(fragment);
        }

    }

    private PlayerActivity getParentActivity() {
        FragmentActivity activity = getActivity();
        if (activity instanceof PlayerActivity) {
            return ((PlayerActivity) activity);
        }
        return null;
    }

    private OnPlayControlListener kwListener = new OnPlayControlListener() {
        @Override
        public void onReadyPlay(XMMusic music) {
            mPlayerView.setKwImageCover(music);
            if (mPlayerVM != null) {
                mPlayerVM.setOnlineMusic(music);
                mPlayerVM.fetchMusicQualityModel();
                mPlayerVM.fetchCurrentQuality(OnlineMusicFactory.getKWPlayer().getDownloadWhenPlayQuality());
            }
            if (mPlayListWindow != null) {
                mPlayListWindow.updateProgress(OnlineMusicFactory.getKWPlayer().getCurrentPos());
            }
        }

        @Override
        public void onBufferStart() {

        }

        @Override
        public void onBufferFinish() {

        }

        @Override
        public void onPreStart(XMMusic music) {
            mPlayerView.updatePlayStatus(PlayStatus.BUFFERING);
            mContentView.updatePlayStatus(PlayStatus.PAUSE);
            mPlayerView.setKwImageCover(music);
            initCollectStatus(music);
            if (mPlayListWindow != null) {
                mPlayListWindow.updateProgress(OnlineMusicFactory.getKWPlayer().getCurrentPos());
            }
            mPlayerVM.setOnlineMusic(music);
        }

        @Override
        public void onPlay(XMMusic music) {
            mPlayerView.updatePlayStatus(PlayStatus.PLAYING);
            mContentView.updatePlayStatus(PlayStatus.PLAYING);
            mPlayerVM.setOnlineMusic(music);
//            mPlayerVM.fetchCurrentQuality(OnlineMusicFactory.getKWPlayer().getDownloadWhenPlayQuality());
        }

        @Override
        public void onSeekSuccess(int position) {

        }

        @Override
        public void onPause() {
            mPlayerView.updatePlayStatus(PlayStatus.PAUSE);
            mContentView.updatePlayStatus(PlayStatus.PAUSE);
        }

        @Override
        public void onProgressChange(final long progressInMs, long totalInMs) {
            ThreadDispatcher.getDispatcher().runOnMain(new Runnable() {
                @Override
                public void run() {
                    if (mPlayListWindow != null) {
                        mPlayListWindow.updateProgress(progressInMs);
                    }
                    mContentView.updateProgress(progressInMs);
                }
            });
        }

        @Override
        public void onPlayFailed(int errorCode, String errorMsg) {
            mPlayerView.updatePlayStatus(PlayStatus.PAUSE);
            mContentView.updatePlayStatus(PlayStatus.PAUSE);
            if (errorCode == KuwoPlayControlObserver.ErrorCode.NEED_ALBUM ||
                    errorCode == KuwoPlayControlObserver.ErrorCode.NEED_SING_SONG ||
                    errorCode == KuwoPlayControlObserver.ErrorCode.NO_COPY_RIGHT) {
                showToastException(R.string.play_failed_next);
            }
        }

        @Override
        public void onPlayStop() {
            mPlayerView.updatePlayStatus(PlayStatus.PAUSE);
            mContentView.updatePlayStatus(PlayStatus.PAUSE);
        }

        @Override
        public void onPlayModeChanged(int playMode) {

        }

        @Override
        public void onCurrentPlayListChanged() {

        }
    };

    public void updatePlaying() {
        if (mPlayListWindow != null) {
            mPlayListWindow.updatePlaying();
        }

    }

    @Override
    public void updatePlayProgress(int newTime) {
        if (mContentView != null) {
            mContentView.updateProgress(newTime);
        }
    }
}

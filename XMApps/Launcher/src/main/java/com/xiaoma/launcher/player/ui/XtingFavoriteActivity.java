package com.xiaoma.launcher.player.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
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
import com.xiaoma.launcher.player.adapter.FavoriteAudioAdapter;
import com.xiaoma.launcher.player.callback.LauncherPlayListener;
import com.xiaoma.launcher.player.manager.PlayerConnectHelper;
import com.xiaoma.launcher.player.model.AudioMusicMarkInfo;
import com.xiaoma.model.annotation.NormalOnClick;
import com.xiaoma.model.annotation.PageDescComponent;
import com.xiaoma.model.annotation.ResId;
import com.xiaoma.player.AudioConstants;
import com.xiaoma.player.AudioInfo;
import com.xiaoma.player.ProgressInfo;
import com.xiaoma.thread.ThreadDispatcher;
import com.xiaoma.ui.view.XmDividerDecoration;
import com.xiaoma.ui.view.XmScrollBar;

import java.util.ArrayList;
import java.util.List;

/**
 * 电台收藏
 * Created by zhushi.
 * Date: 2019/2/18
 */
@PageDescComponent(EventConstants.PageDescribe.XtingFavoriteActivityPagePathDesc)
public class XtingFavoriteActivity extends BaseActivity implements LauncherPlayListener, View.OnClickListener {
    private TextView mTvTitle;
    private RecyclerView mRecyclerView;
    private XmScrollBar xmScrollBar;
    private FavoriteAudioAdapter audioAdapter;
    private List<AudioInfo> audioInfos = new ArrayList<>();
    private AudioInfo clickAudioInfo;
    //从播放器传过来的音频
    private AudioInfo audioInfoFromLauncher;
    private PlayerConnectHelper connectHelper;
    private ProgressInfo mProgressInfo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_xting_favorite_list);
        statusBarDividerGone();
        initView();
        initData();
    }

    private void initView() {
        findViewById(R.id.iv_back).setOnClickListener(this);
        mTvTitle = findViewById(R.id.tv_title);
        mRecyclerView = findViewById(R.id.favorite_list);
        xmScrollBar = findViewById(R.id.scroll_bar);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        XmDividerDecoration decor = new XmDividerDecoration(this, DividerItemDecoration.HORIZONTAL);
        int horizontal = 98;
        int extra = 60;
        decor.setRect(horizontal, 0, horizontal, 0);
        decor.setExtraMargin(extra);
        mRecyclerView.addItemDecoration(decor);
        audioAdapter = new FavoriteAudioAdapter(audioInfos, this);
        mRecyclerView.setAdapter(audioAdapter);
        xmScrollBar.setRecyclerView(mRecyclerView);
    }

    private void initData() {
        connectHelper = PlayerConnectHelper.getInstance();
        connectHelper.setPlayListener(this);
        audioInfoFromLauncher = getIntent().getParcelableExtra(AudioMainFragment.AUDIO_INFO);
        mTvTitle.setText(getString(R.string.favorite_xting));
        searchAudioList(AudioConstants.AudioTypes.XTING_NET_FM);
        audioAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                if (view.getId() == R.id.iv_play_cover || view.getId() == R.id.iv_control) {
                    clickAudioInfo = audioInfos.get(position);
                    AudioMusicMarkInfo audioMusicMarkInfo = new AudioMusicMarkInfo();
                    audioMusicMarkInfo.id = audioInfos.get(position).getUniqueId() + "";
                    audioMusicMarkInfo.value = audioInfos.get(position).getAudioType() + "";
                    if (mProgressInfo != null) {
                        audioMusicMarkInfo.h = mProgressInfo.getCurrent() + "";
                        audioMusicMarkInfo.i = mProgressInfo.getTotal() + "";
                    }
                    if (clickAudioInfo.getPlayState() == AudioConstants.AudioStatus.PLAYING) {
                        XmAutoTracker.getInstance().onEvent(EventConstants.NormalClick.XTING_AUDIO_PLAY_LISTITEM,
                                GsonUtil.toJson(audioMusicMarkInfo),
                                EventConstants.PagePath.XtingFavoriteActivity,
                                EventConstants.PageDescribe.XtingFavoriteActivityPagePathDesc);
                        if (clickAudioInfo.getAudioType() == AudioConstants.AudioTypes.XTING_LOCAL_FM) {
                            return;
                        }
                        //暂停
                        connectHelper.pauseAudio(clickAudioInfo.getAudioType());

                    } else {
                        XmAutoTracker.getInstance().onEvent(EventConstants.NormalClick.XTING_AUDIO_STOP_LISTITEM,
                                GsonUtil.toJson(audioMusicMarkInfo),
                                EventConstants.PagePath.XtingFavoriteActivity,
                                EventConstants.PageDescribe.XtingFavoriteActivityPagePathDesc);
                        //点击为FM时默认会切换成功
                        if (clickAudioInfo.getAudioType() == AudioConstants.AudioTypes.XTING_LOCAL_FM) {
                            onPlayState(AudioConstants.AudioStatus.PLAYING);
                        }
                        //播放
                        connectHelper.playAudio(clickAudioInfo.getAudioType(), AudioConstants.PlayAction.PLAY_ALBUM_AT_INDEX,
                                (int) clickAudioInfo.getAlbumId(), position);
                        //通知音频桌面数据已收藏
                        connectHelper.notifyFavorite();
                    }
                }
            }
        });
    }

    public void searchAudioList(final int audioType) {
        SourceInfo local = new SourceInfo(getPackageName(), LauncherConstants.LAUNCHER_PORT);
        Bundle searchPurpose = new Bundle();
        searchPurpose.putInt(AudioConstants.BundleKey.SEARCH_ACTION, AudioConstants.SearchAction.FAVORITE);
        Request searchRequest = new Request(local, new RequestHead(PlayerConnectHelper.getInstance().getSourceInfoByAudioType(audioType),
                AudioConstants.Action.SEARCH), searchPurpose);
        Linker.getInstance().request(searchRequest, new IClientCallback.Stub() {
            @Override
            public void callback(Response response) {
                Bundle extra = response.getExtra();
                extra.setClassLoader(AudioInfo.class.getClassLoader());
                int responseCode = extra.getInt(AudioConstants.BundleKey.AUDIO_RESPONSE_CODE);
                if (responseCode == AudioConstants.AudioResponseCode.SUCCESS) {
                    List<AudioInfo> audioInfoList = extra.getParcelableArrayList(AudioConstants.BundleKey.AUDIO_LIST);
                    if (audioInfoList == null || audioInfoList.size() == 0) {
                        audioAdapter.setEmptyView(R.layout.state_empty_view, (ViewGroup) mRecyclerView.getParent());
                        return;
                    }
                    int index = 0;
                    //同步播放状态
                    if (audioInfoFromLauncher != null) {
                        for (int i = 0; i < audioInfoList.size(); i++) {
                            AudioInfo audioInfo = audioInfoList.get(i);
                            if (audioInfoFromLauncher.getAudioType() == AudioConstants.AudioTypes.XTING_LOCAL_FM) {
                                if (audioInfo.getAlbumId() == audioInfoFromLauncher.getUniqueId()) {
                                    index = i;
                                    audioInfo.setPlayState(audioInfoFromLauncher.getPlayState());
                                    break;
                                }

                            } else {
                                if (audioInfo.getAlbumId() == audioInfoFromLauncher.getAlbumId()) {
                                    index = i;
                                    audioInfo.setPlayState(audioInfoFromLauncher.getPlayState());
                                    break;
                                }
                            }
                        }
                    }
                    audioInfos.clear();
                    audioInfos.addAll(audioInfoList);
                    audioAdapter.setNewData(audioInfos);
                    mRecyclerView.scrollToPosition(index);
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        connectHelper.removePlayListener(this);
    }

    @Override
    public void onPlayState(final int playState) {
        ThreadDispatcher.getDispatcher().postOnMain(new Runnable() {
            @Override
            public void run() {
                if (clickAudioInfo == null) {
                    return;
                }
                //如果点击的item和播放的item是同一项
                if (clickAudioInfo == audioInfoFromLauncher) {
                    clickAudioInfo.setPlayState(playState);
                    audioAdapter.notifyDataSetChanged();
                    return;
                }
                for (AudioInfo audioInfo : audioInfos) {
                    audioInfo.setPlayState(AudioConstants.AudioStatus.PAUSING);
                }
                if (clickAudioInfo.getAudioType() == AudioConstants.AudioTypes.XTING_LOCAL_FM) {
                    clickAudioInfo.setPlayState(AudioConstants.AudioStatus.PLAYING);

                } else {
                    clickAudioInfo.setPlayState(playState);
                }
                audioAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    @NormalOnClick({EventConstants.NormalClick.XTING_FAVORITE_CLOSE})//按钮对应的名称
    @ResId({R.id.iv_back})//按钮对应的R文件id
    public void onClick(View v) {
        if (v.getId() == R.id.iv_back) {
            finish();
        }
    }

    @Override
    public void onProgress(ProgressInfo progressInfo) {
        mProgressInfo = progressInfo;
    }

    @Override
    public void onAudioInfo(AudioInfo audioInfo) {
        audioInfoFromLauncher = audioInfo;
        if (audioInfo.getAudioType() == AudioConstants.AudioTypes.MUSIC_LOCAL_BT) {
            onPlayState(AudioConstants.AudioStatus.PAUSING);
        }
    }

    @Override
    public void onDataSource(int dataSource) {
    }

    @Override
    public void onAudioFavorite(boolean favorite) {
    }

    @Override
    protected boolean isAppNeedShowNaviBar() {
        return false;
    }
}

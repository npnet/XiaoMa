package com.xiaoma.music.mine.ui;

import android.annotation.SuppressLint;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;

import com.xiaoma.component.base.LazyLoadFragment;
import com.xiaoma.model.XmResource;
import com.xiaoma.model.annotation.PageDescComponent;
import com.xiaoma.music.OnlineMusicFactory;
import com.xiaoma.music.R;
import com.xiaoma.music.common.constant.EventBusTags;
import com.xiaoma.music.common.constant.EventConstants;
import com.xiaoma.music.common.manager.KwPlayInfoManager;
import com.xiaoma.music.common.manager.MusicDbManager;
import com.xiaoma.music.common.model.ShowHideEvent;
import com.xiaoma.music.common.view.RecyclerDividerItem;
import com.xiaoma.music.export.manager.AudioShareManager;
import com.xiaoma.music.kuwo.listener.OnPlayControlListener;
import com.xiaoma.music.kuwo.model.XMMusic;
import com.xiaoma.music.mine.adapter.CollectionAdapter;
import com.xiaoma.music.mine.model.LocalModel;
import com.xiaoma.music.mine.vm.CollectionVM;
import com.xiaoma.music.player.model.CollectEventBean;
import com.xiaoma.music.player.ui.AlbumSwitchFragment;
import com.xiaoma.thread.ThreadDispatcher;
import com.xiaoma.ui.OnRvItemClickListener;
import com.xiaoma.ui.view.XmScrollBar;
import com.xiaoma.utils.BackHandlerHelper;
import com.xiaoma.utils.ListUtils;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ZYao.
 * Date ：2018/10/11 0011
 */
@PageDescComponent(EventConstants.PageDescribe.collectionFragment)
public class CollectionFragment extends LazyLoadFragment implements BackHandlerHelper.FragmentBackHandler, KwPlayInfoManager.AlbumTypeChangedListener {
    private static final String KEY_ENTRY = "key_entry";
    public static final String KEY_ENTRY_PLAY = "player";
    public static final String KEY_ENTRY_MAIN = "main";
    private CollectionAdapter collectorAdapter;
    private CollectionVM collectionVM;
    private XmScrollBar scrollBar;
    private static final String MARGIN_KEY = "margin";
    private String keyEntry;
    private MusicDbManager.OnCollectionStatusChangeListener collectionStatusChangeListener;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        KwPlayInfoManager.getInstance().addAlbumTypeChangedListener(this);
    }


    public static CollectionFragment newInstance(int margin, String key) {
        CollectionFragment fragment = new CollectionFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(MARGIN_KEY, margin);
        bundle.putString(KEY_ENTRY, key);
        fragment.setArguments(bundle);
        return fragment;
    }


    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_music_collector;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void initView(View view) {
        RecyclerView rvMusicCollect = view.findViewById(R.id.rv_music_collect);
        initScrollBar(view, rvMusicCollect);
        rvMusicCollect.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
        int offset = mContext.getResources().getDimensionPixelOffset(R.dimen.size_item_margin);
        int first = mContext.getResources().getDimensionPixelOffset(R.dimen.size_fragment_album_switch_left);
        final Bundle bundle = getArguments();
        if (bundle != null) {
            first = bundle.getInt(MARGIN_KEY, mContext.getResources().getDimensionPixelOffset(R.dimen.size_fragment_album_switch_left));
        }
        RecyclerDividerItem dividerItem = new RecyclerDividerItem(mContext, DividerItemDecoration.HORIZONTAL);
        dividerItem.setRect(0, 0, offset, 0, first);
        rvMusicCollect.addItemDecoration(dividerItem);
        collectorAdapter = new CollectionAdapter(this, new ArrayList<LocalModel>());
        rvMusicCollect.setItemAnimator(null);
        collectorAdapter.setLongClick((!TextUtils.isEmpty(keyEntry)) && keyEntry.equals(KEY_ENTRY_MAIN));
        rvMusicCollect.setAdapter(collectorAdapter);
        scrollBar.setRecyclerView(rvMusicCollect);
        rvMusicCollect.setOnTouchListener(new View.OnTouchListener() {
            boolean isDeleteStatus = false;
            float startX = 0;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (collectorAdapter == null) {
                    return false;
                }
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        isDeleteStatus = collectorAdapter.isDeleteStatus();
                        startX = event.getX();
                        break;
                    case MotionEvent.ACTION_UP:
                        float upX = event.getX();
                        if (isDeleteStatus && Math.abs(upX - startX) < 10) {
                            collectorAdapter.recoverNormal();
                        }
                        isDeleteStatus = false;
                        break;
                }
                return false;
            }
        });
        collectorAdapter.setListener(new OnRvItemClickListener<XMMusic>() {
            @Override
            public void onItemClick(int position, XMMusic music) {
                if (XMMusic.isEmpty(music)) {
                    return;
                }
                if ((!TextUtils.isEmpty(keyEntry)) && !keyEntry.equals(KEY_ENTRY_MAIN)) {
                    XMMusic nowPlayingMusic = OnlineMusicFactory.getKWPlayer().getNowPlayingMusic();
                    if (nowPlayingMusic != null
                            && music.getSDKBean().rid != nowPlayingMusic.getSDKBean().rid) {
                        playMusic(position);
                    }
                    AlbumSwitchFragment parentFragment = (AlbumSwitchFragment) getParentFragment();
                    if (parentFragment != null) {
                        parentFragment.backToOnlinePlayFragment();
                    }
                } else {
                    playMusic(position);
                    collectorAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onItemDelete() {
                if (collectorAdapter != null) {
                    checkEmptyView(collectorAdapter.getData());
                }
            }
        });
        OnlineMusicFactory.getKWMessage().addPlayStateListener(kwListener);
        MusicDbManager.getInstance().addCollectionStatusChangeListener(collectionStatusChangeListener = new MusicDbManager.OnCollectionStatusChangeListener() {
            @Override
            public void onAddCollection() {
                updateCollection();
            }

            @Override
            public void onRemoveCollection() {
                updateCollection();
            }

            private void updateCollection() {
                collectionVM.initCollectorData();
            }
        });
    }

    private void playMusic(int position) {
        List<LocalModel> localModels = collectorAdapter.getData();
        if (!ListUtils.isEmpty(localModels)) {
            List<XMMusic> musicList = wrapperXmMusics(localModels);
            XMMusic xmMusic = musicList.get(position);
            AudioShareManager.getInstance().shareKwAudioDataSourceChanged();
            OnlineMusicFactory.getKWPlayer().play(musicList,position);
            KwPlayInfoManager.getInstance().setCurrentPlayInfo(xmMusic.getRid() + xmMusic.getName(),
                    KwPlayInfoManager.AlbumType.COLLECTION);
        }
    }

    @NonNull
    private List<XMMusic> wrapperXmMusics(List<LocalModel> localModels) {
        List<XMMusic> musicList = new ArrayList<>();
        for (LocalModel localModel : localModels) {
            XMMusic xmMusic = localModel.getXmMusic();
            if (XMMusic.isEmpty(xmMusic)) {
                continue;
            }
            musicList.add(xmMusic);
        }
        return musicList;
    }

    private void initScrollBar(View view, RecyclerView rvMusicCollect) {
        scrollBar = view.findViewById(R.id.scroll_bar);
        Bundle arguments = getArguments();
        if (arguments != null) {
            keyEntry = arguments.getString(KEY_ENTRY);
        }
    }

    @Override
    protected void cancelData() {
        if (collectorAdapter != null) {
            collectorAdapter.recoverNormal();
        }
    }

    @Override
    protected void loadData() {
        super.loadData();
        collectionVM = ViewModelProviders.of(this).get(CollectionVM.class);
        collectionVM.getCollectionMusic().observe(this, new Observer<XmResource<List<LocalModel>>>() {
            @Override
            public void onChanged(@Nullable XmResource<List<LocalModel>> listXmResource) {
                if (listXmResource != null) {
                    listXmResource.handle(new OnCallback<List<LocalModel>>() {
                        @Override
                        public void onSuccess(List<LocalModel> data) {
                            if (data != null) {
                                collectorAdapter.setNewData(data);
                                collectorAdapter.notifyDataSetChanged();
                                checkEmptyView(data);
                            }
                        }
                    });
                }
            }
        });
//        showLoadingView();
        collectionVM.initCollectorData();
    }

    private void checkEmptyView(List<LocalModel> data) {
        if (data.isEmpty()) {
            scrollBar.setVisibility(View.GONE);
            showEmptyView();
        } else {
            if ((!TextUtils.isEmpty(keyEntry)) && keyEntry.equals(KEY_ENTRY_MAIN)) {
                scrollBar.setVisibility(View.VISIBLE);
            } else {
                scrollBar.setVisibility(View.VISIBLE);
            }
            showContentView();
        }
    }


    @Subscriber(tag = EventBusTags.MUSIC_COLLECTION)
    public void onAddHistoryEvent(CollectEventBean music) {
        collectionVM.addMusicToCollection(music);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (collectorAdapter != null) {
            collectorAdapter.notifyDataSetChanged();
        }
    }

    @Subscriber(tag = EventBusTags.SHOW_OR_HIDE_MINE)
    public void onHideEvent(ShowHideEvent event) {
        if (!event.isShowMine()) {
            if (collectorAdapter != null) {
                collectorAdapter.recoverNormal();
            }
        } else {
            if (collectorAdapter != null) {
                collectorAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (collectorAdapter != null) {
            collectorAdapter.recoverNormal();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        OnlineMusicFactory.getKWMessage().removePlayStateListener(kwListener);
        MusicDbManager.getInstance().removeCollectionStatusChangeListener(collectionStatusChangeListener);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        KwPlayInfoManager.getInstance().removeAlbumTypeChangedListener(this);
    }

    @Override
    public boolean onBackPressed() {
        if (collectorAdapter != null && collectorAdapter.isDeleteStatus()) {
            collectorAdapter.recoverNormal();
            return true;
        } else {
            return BackHandlerHelper.handleBackPress(this);
        }
    }

    @Override
    public void onChanged(String currentAlbumId, int currentType) {
        ThreadDispatcher.getDispatcher().postOnMain(new Runnable() {
            @Override
            public void run() {
                if (collectorAdapter != null) {
                    collectorAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        if (collectorAdapter != null) {
            collectorAdapter.notifyDataSetChanged();
        }
        super.setUserVisibleHint(isVisibleToUser);
    }

    private OnPlayControlListener kwListener = new OnPlayControlListener() {
        @Override
        public void onPreStart(XMMusic music) {

        }

        @Override
        public void onReadyPlay(final XMMusic music) {
            Fragment parentFragment = getParentFragment();
            if (parentFragment instanceof MineFragment) {
                KwPlayInfoManager.getInstance().setCurrentHistoryAlbumId(music.getRid() + music.getName());
                collectorAdapter.notifyDataSetChanged();
            }
        }

        @Override
        public void onBufferStart() {

        }

        @Override
        public void onBufferFinish() {

        }

        @Override
        public void onPlay(XMMusic music) {

        }

        @Override
        public void onSeekSuccess(int position) {

        }

        @Override
        public void onPause() {

        }

        @Override
        public void onProgressChange(long progressInMs, long totalInMs) {

        }

        @Override
        public void onPlayStop() {

        }

        @Override
        public void onPlayModeChanged(int playMode) {

        }

        @Override
        public void onCurrentPlayListChanged() {

        }

        @Override
        public void onPlayFailed(int errorCode, String errorMsg) {

        }
    };
}

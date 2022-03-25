package com.xiaoma.music.mine.ui;

import android.annotation.SuppressLint;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.xiaoma.music.kuwo.listener.OnPlayControlListener;
import com.xiaoma.music.kuwo.model.XMMusic;
import com.xiaoma.music.mine.adapter.HistoryAdapter;
import com.xiaoma.music.mine.model.LocalModel;
import com.xiaoma.music.mine.vm.HistoryVM;
import com.xiaoma.thread.ThreadDispatcher;
import com.xiaoma.ui.view.XmScrollBar;
import com.xiaoma.utils.BackHandlerHelper;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ZYao.
 * Date ：2018/10/11 0011
 */
@PageDescComponent(EventConstants.PageDescribe.historyFragment)
public class HistoryFragment extends LazyLoadFragment implements MineFragment.OnMusicHistoryChangeListener,
        MusicDbManager.OnHistoryChangedListener, BackHandlerHelper.FragmentBackHandler, KwPlayInfoManager.AlbumTypeChangedListener {
    private HistoryAdapter historyAdapter;
    private HistoryVM historyVM;
    private XmScrollBar scrollBar;

    public static HistoryFragment newInstance() {
        return new HistoryFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        MusicDbManager.getInstance().addHistoryChangedListener(this);
        KwPlayInfoManager.getInstance().addAlbumTypeChangedListener(this);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_music_history;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void initView(View view) {
        RecyclerView rvHistory = view.findViewById(R.id.rv_music_history);
        scrollBar = view.findViewById(R.id.scroll_bar);
        rvHistory.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
        int offset = mContext.getResources().getDimensionPixelOffset(R.dimen.size_item_margin);
        int first = mContext.getResources().getDimensionPixelOffset(R.dimen.size_item_first_margin);
        RecyclerDividerItem dividerItem = new RecyclerDividerItem(mContext, DividerItemDecoration.HORIZONTAL);
        dividerItem.setRect(0, 0, offset, 0, first);
        rvHistory.addItemDecoration(dividerItem);
        if (getParentFragment() != null) {
            ((MineFragment) getParentFragment()).setMusicHistoryChangeListener(this);
        }
        historyAdapter = new HistoryAdapter(this, new ArrayList<LocalModel>());
        rvHistory.setAdapter(historyAdapter);
        scrollBar.setRecyclerView(rvHistory);
        rvHistory.setOnTouchListener(new View.OnTouchListener() {
            boolean isDeleteStatus = false;
            float startX = 0;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (historyAdapter == null) {
                    return false;
                }
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        isDeleteStatus = historyAdapter.isDeleteStatus();
                        startX = event.getX();
                        break;
                    case MotionEvent.ACTION_UP:
                        float upX = event.getX();
                        if (isDeleteStatus && Math.abs(upX - startX) < 10) {
                            historyAdapter.recoverNormal();
                        }
                        isDeleteStatus = false;
                        break;
                }
                return false;
            }
        });
        OnlineMusicFactory.getKWMessage().addPlayStateListener(kwListener);
    }

    @Override
    protected void cancelData() {
        if (historyAdapter != null) {
            historyAdapter.recoverNormal();
        }
    }

    @Override
    public void onClearAllHistorySuccess() {
        if (historyVM != null) {
            historyVM.postEmptyData();
        }
    }

    @Override
    public void onAddHistory() {

    }

    @Override
    protected void loadData() {
        super.loadData();
        historyVM = ViewModelProviders.of(this).get(HistoryVM.class);
        historyVM.getHistoryMusicList().observe(this, new Observer<XmResource<List<LocalModel>>>() {
            @Override
            public void onChanged(@Nullable XmResource<List<LocalModel>> listXmResource) {
                if (listXmResource != null) {
                    listXmResource.handle(new OnCallback<List<LocalModel>>() {
                        @Override
                        public void onSuccess(List<LocalModel> musicInfos) {
                            if (musicInfos != null) {
                                historyAdapter.setNewData(musicInfos);
                                checkEmptyView(musicInfos);
                            }
                        }

                        @Override
                        public void onError(int code, String message) {
                            super.onError(code, message);
                        }
                    });
                }
            }
        });
//        showLoadingView();
        historyVM.initMusicInfoData();
    }

    private void checkEmptyView(List<LocalModel> musicInfos) {
        if (musicInfos.isEmpty()) {
            scrollBar.setVisibility(View.GONE);
            showEmptyView();
        } else {
            scrollBar.setVisibility(View.VISIBLE);
            showContentView();
        }
    }


    @Subscriber(tag = EventBusTags.RECENTER_PLAY)
    public void onAddHistoryEvent(XMMusic music) {
        if (music != null && historyVM != null) {
            historyVM.addMusicHistory(music);
        }
    }

    @Subscriber(tag = EventBusTags.SHOW_OR_HIDE_MINE)
    public void onHideEvent(ShowHideEvent event) {
        if (!event.isShowMine()) {
            if (historyAdapter != null) {
                historyAdapter.recoverNormal();
            }
        } else {
            if (historyVM != null) {
                historyVM.initMusicInfoData();
            }
        }
    }

    @Override
    public void clearAllHistory() {
        historyVM.clearAllHistory();
        scrollBar.setVisibility(View.GONE);
        showEmptyView();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (historyVM != null) {
            historyVM.initMusicInfoData();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (historyAdapter != null) {
            historyAdapter.recoverNormal();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        MusicDbManager.getInstance().removeHistoryChangedListener(this);
        KwPlayInfoManager.getInstance().removeAlbumTypeChangedListener(this);
    }

    @Override
    public void onChanged(String currentAlbumId, int currentType) {
        ThreadDispatcher.getDispatcher().postOnMain(new Runnable() {
            @Override
            public void run() {
                if (historyAdapter != null) {
                    historyAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    @Override
    public boolean onBackPressed() {
        if (historyAdapter != null && historyAdapter.isDeleteStatus()) {
            historyAdapter.recoverNormal();
            return true;
        } else {
            return BackHandlerHelper.handleBackPress(this);
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        if (historyVM != null) {
            historyVM.initMusicInfoData();
        }
        super.setUserVisibleHint(isVisibleToUser);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        OnlineMusicFactory.getKWMessage().removePlayStateListener(kwListener);
    }

    private OnPlayControlListener kwListener = new OnPlayControlListener() {
        @Override
        public void onPreStart(XMMusic music) {

        }

        @Override
        public void onReadyPlay(XMMusic music) {
            KwPlayInfoManager.getInstance().setCurrentCollectionAlbumId(music.getRid() + music.getName());
            historyAdapter.notifyDataSetChanged();
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

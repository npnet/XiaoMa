package com.xiaoma.music.player.view.player;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.IBinder;
import android.support.annotation.DrawableRes;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.xiaoma.autotracker.listener.XMAutoTrackerEventOnClickListener;
import com.xiaoma.model.ItemEvent;
import com.xiaoma.model.annotation.BusinessOnClick;
import com.xiaoma.model.annotation.NormalOnClick;
import com.xiaoma.model.annotation.ResId;
import com.xiaoma.music.R;
import com.xiaoma.music.UsbMusicFactory;
import com.xiaoma.music.callback.OnUsbMusicChangedListener;
import com.xiaoma.music.common.constant.EventConstants;
import com.xiaoma.music.common.constant.PlayerBroadcast;
import com.xiaoma.music.common.model.PlayStatus;
import com.xiaoma.music.manager.OnUsbPlayListChangedListener;
import com.xiaoma.music.manager.PlayerListManager;
import com.xiaoma.music.manager.SortUsbMusicManager;
import com.xiaoma.music.manager.UsbPlayerProxy;
import com.xiaoma.music.model.UsbMusic;
import com.xiaoma.music.player.adapter.UsbPlayListAdapter;
import com.xiaoma.music.player.model.AnimationState;
import com.xiaoma.music.player.model.PlayMode;
import com.xiaoma.music.player.model.UsbSort;
import com.xiaoma.thread.ThreadDispatcher;
import com.xiaoma.ui.toast.XMToast;
import com.xiaoma.ui.view.XmScrollBar;
import com.xiaoma.utils.PopWindowUtils;
import com.xiaoma.utils.tputils.TPUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zs
 * @date 2018/10/12 0012.
 */
public class UsbPlayListWindow extends PopupWindow implements View.OnClickListener,
        PlayerListManager.IUsbPlayModeChangedListener, OnUsbPlayListChangedListener {

    public static final String TP_PLAY_SORT = "UsbSort";

    private Context mContext;
    private View mView;
    private TextView mPlayModeTv;
    private TextView mListSizeTv;
    private TextView mTvSort;
    private RecyclerView mRecyclerView;
    private UsbPlayListAdapter mAdapter;
    private LinearLayoutManager mLinearLayoutManager;
    private XmScrollBar xmScrollBar;
    @UsbSort
    private int mCurSort;

    private Window mWindow;


    public UsbPlayListWindow(Activity activity) {
        super(activity);
        mContext = activity.getApplication();
        initView(activity);
    }

    private void initView(Activity activity) {
        mView = View.inflate(activity, R.layout.view_usb_popwindow, null);
        setContentView(mView);
        mPlayModeTv = mView.findViewById(R.id.view_pop_play_mode_tv);
        mListSizeTv = mView.findViewById(R.id.view_pop_list_size_tv);
        mTvSort = mView.findViewById(R.id.tv_sort);
        mRecyclerView = mView.findViewById(R.id.view_pop_playlist_rv);
        xmScrollBar = mView.findViewById(R.id.xmScrollBar);
        mView.findViewById(R.id.view_pop_play_mode_back).setOnClickListener(this);
        initListener();
        setWidth(mContext.getResources().getDimensionPixelSize(R.dimen.width_view_play_list_window));
        setHeight(RelativeLayout.LayoutParams.MATCH_PARENT);
        setFocusable(true);
        setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        PopWindowUtils.fitPopupWindowOverStatusBar(this, true);
        mLinearLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mRecyclerView.setItemAnimator(null);
        mAdapter = new UsbPlayListAdapter();
        mRecyclerView.setAdapter(mAdapter);
        xmScrollBar.setRecyclerView(mRecyclerView);
        setAnimationStyle(R.style.popup_window_anim_style);
        initSortManager();
        initPlayMode();
        initSort();

        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                UsbMusic usbMusic = mAdapter.getItem(position);
                UsbMusic currUsbMusic = UsbMusicFactory.getUsbPlayerProxy().getCurrUsbMusic();
                String usbPath = usbMusic.getPath();
                if(usbPath == null){
                    return;
                }
                if(usbPath.equals(currUsbMusic.getPath())&& !UsbMusicFactory.getUsbPlayerProxy().isPlaying()){
                    UsbMusicFactory.getUsbPlayerProxy().switchPlay(true);
                } else if(!usbPath.equals(currUsbMusic.getPath())){
                    if(UsbMusicFactory.getUsbPlayerProxy().play(usbMusic)){
                        mAdapter.setSelectPosition(position);
                    }
                }
            }
         });
        mWindow = activity.getWindow();
        initPlayStatus();
    }

    private void initSortManager() {
        List<UsbMusic> usbMusicList = UsbMusicFactory.getUsbPlayerListProxy().getUsbMusicList();
        SortUsbMusicManager.getInstance().setDefaultMusicList(usbMusicList);
    }

    private void initListener() {
        mPlayModeTv.setOnClickListener(new XMAutoTrackerEventOnClickListener() {
            @Override
            public ItemEvent returnPositionEventMsg(View view) {
                return new ItemEvent(((TextView) view).getText().toString(), "0");
            }

            @BusinessOnClick
            @Override
            public void onClick(View v) {
                switchPlayMode();
            }
        });
        mTvSort.setOnClickListener(new XMAutoTrackerEventOnClickListener() {
            @Override
            public ItemEvent returnPositionEventMsg(View view) {
                return new ItemEvent(((TextView) view).getText().toString(), "0");
            }

            @BusinessOnClick
            @Override
            public void onClick(View v) {
                switchSort();
            }
        });
    }

    private void initPlayStatus() {
        int playStatus = UsbPlayerProxy.getInstance().getPlayStatus();
        if (PlayStatus.PLAYING == playStatus) {
            mAdapter.setState(AnimationState.PLAY);
        } else if (PlayStatus.BUFFERING == playStatus) {
            mAdapter.setState(AnimationState.LOADING);
        } else {
            mAdapter.setState(AnimationState.PAUSE);
        }
    }

    private void initPlayMode() {
        int mCurPlayMode = UsbMusicFactory.getUsbPlayerListProxy().getPlayMode();
        switch (mCurPlayMode) {
            case PlayMode.LIST_ORDER:
                setPlayModeIcon(mPlayModeTv, R.drawable.icon_playlist_order);
                mPlayModeTv.setText(R.string.play_mode_list_order);
                break;
            case PlayMode.LIST_LOOP:
                setPlayModeIcon(mPlayModeTv, R.drawable.icon_playlist_list_loop);
                mPlayModeTv.setText(R.string.play_mode_list_loop);
                break;
            case PlayMode.SINGLE_LOOP:
                setPlayModeIcon(mPlayModeTv, R.drawable.icon_playlist_single);
                mPlayModeTv.setText(R.string.play_mode_single_loop);
                break;
            case PlayMode.RANDOM:
                setPlayModeIcon(mPlayModeTv, R.drawable.icon_playlist_random);
                mPlayModeTv.setText(R.string.play_mode_random);
                break;
            default:
                break;
        }
    }

    private void initSort() {
        mCurSort = TPUtils.get(mContext, TP_PLAY_SORT, UsbSort.DEFAULT);
        switch (mCurSort) {
            case UsbSort.DEFAULT:
                mTvSort.setText(R.string.sort_by_default);
                sortByDefault();
                break;
            case UsbSort.GENRE:
                mTvSort.setText(R.string.sort_by_genre);
                sortByGenre();
                break;
            case UsbSort.ARTIST:
                mTvSort.setText(R.string.sort_by_artist);
                sortByArtist();
                break;
            default:
                break;
        }
    }

    public void setData(List<UsbMusic> data) {
        mAdapter.setNewData(data);
        mListSizeTv.setText(String.format("(%s)", data.size()));
        UsbMusic music = UsbMusicFactory.getUsbPlayerProxy().getCurrUsbMusic();
        int position = data.indexOf(music);
        mAdapter.setSelectPosition(position);
        mRecyclerView.scrollToPosition(position);
    }

    public void updateProgress(long curPos, long duration) {
        mAdapter.updateProgress(curPos, duration);
    }

    @NormalOnClick(EventConstants.NormalClick.playlistBackToPlayer)
    @ResId(R.id.view_pop_play_mode_back)
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.view_pop_play_mode_back:
                display(false);
                break;
            default:
                break;
        }
    }

    private void switchSort() {
        switch (mCurSort) {
            case UsbSort.DEFAULT:
                sortByGenre();
                break;
            case UsbSort.GENRE:
                sortByArtist();
                break;
            case UsbSort.ARTIST:
                sortByDefault();
                break;
            default:
                break;
        }
    }

    private void sortByGenre() {
        mTvSort.setText(R.string.sort_by_genre);
        List<UsbMusic> musicList = SortUsbMusicManager.getInstance().getSortByGenre();
        UsbMusicFactory.getUsbPlayerListProxy().replaceUsbMusicList(musicList);
        mCurSort = UsbSort.GENRE;
    }

    private void sortByArtist() {
        mTvSort.setText(R.string.sort_by_artist);
        List<UsbMusic> musicList = SortUsbMusicManager.getInstance().getSortByArtist();
        UsbMusicFactory.getUsbPlayerListProxy().replaceUsbMusicList(musicList);
        mCurSort = UsbSort.ARTIST;
    }

    private void sortByDefault() {
        ArrayList<UsbMusic> usbMusicList = new ArrayList<>(UsbMusicFactory.getUsbPlayerListProxy().getDefaultUsbMusicList());
        UsbMusicFactory.getUsbPlayerListProxy().replaceUsbMusicList(usbMusicList);
        mTvSort.setText(R.string.sort_by_default);
        mCurSort = UsbSort.DEFAULT;
    }

    public void display(boolean isDisplay) {
        if (isDisplay && !isShowing()) {
            bgAlpha(0.3f);
            refreshPlayingItem(UsbMusicFactory.getUsbPlayerProxy().getCurrUsbMusic());
            showAtLocation(mView, Gravity.NO_GRAVITY, 0, 0);
            initPlayStatus();
            initPlayMode();
            initSort();
            updateProgress(UsbMusicFactory.getUsbPlayerProxy().getCurPosition()
                    , UsbMusicFactory.getUsbPlayerProxy().getDuration());
        } else if (!isDisplay && isShowing()) {
            dismiss();
        }
    }

    @Override
    public void showAtLocation(IBinder token, int gravity, int x, int y) {
        super.showAtLocation(token, gravity, x, y);
        onShow();
    }

    @Override
    public void showAsDropDown(View anchor, int xoff, int yoff, int gravity) {
        super.showAsDropDown(anchor, xoff, yoff, gravity);
        onShow();
    }

    private void onShow() {
        UsbMusicFactory.getUsbPlayerListProxy().addStateChangeListen(this);
        UsbMusicFactory.getUsbPlayerListProxy().addUsbPlayListChangedListener(this);
        UsbMusicFactory.getUsbPlayerProxy().addMusicChangeListener(usbListener);
    }

    @Override
    public void dismiss() {
        super.dismiss();
        bgAlpha(1.0f);
        TPUtils.put(getContentView().getContext(), TP_PLAY_SORT, mCurSort);
        UsbMusicFactory.getUsbPlayerListProxy().removeStateChangeListen(this);
        UsbMusicFactory.getUsbPlayerListProxy().removeUsbPlayListChangedListener(this);
        UsbMusicFactory.getUsbPlayerProxy().removeMusicChangeListener(usbListener);
    }

    private void bgAlpha(float alphaValue) {
        WindowManager.LayoutParams attributes = mWindow.getAttributes();
        attributes.alpha = alphaValue;
        mWindow.setAttributes(attributes);
    }

    private void switchPlayMode() {
        int playMode = UsbMusicFactory.getUsbPlayerListProxy().getPlayMode();
        switch (playMode) {
            case PlayMode.LIST_ORDER:
                setPlayModeIcon(mPlayModeTv, R.drawable.icon_playlist_list_loop);
                mPlayModeTv.setText(R.string.play_mode_list_loop);
                UsbMusicFactory.getUsbPlayerListProxy().setPlayMode(PlayMode.LIST_LOOP);
                XMToast.showToast(mContext, R.string.play_mode_list_loop);
                break;
            case PlayMode.LIST_LOOP:
                setPlayModeIcon(mPlayModeTv, R.drawable.icon_playlist_single);
                mPlayModeTv.setText(R.string.play_mode_single_loop);
                UsbMusicFactory.getUsbPlayerListProxy().setPlayMode(PlayMode.SINGLE_LOOP);
                XMToast.showToast(mContext, R.string.play_mode_single_loop);
                break;
            case PlayMode.SINGLE_LOOP:
                setPlayModeIcon(mPlayModeTv, R.drawable.icon_playlist_random);
                mPlayModeTv.setText(R.string.play_mode_random);
                UsbMusicFactory.getUsbPlayerListProxy().setPlayMode(PlayMode.RANDOM);
                XMToast.showToast(mContext, R.string.play_mode_random);
                break;
            case PlayMode.RANDOM:
                setPlayModeIcon(mPlayModeTv, R.drawable.icon_playlist_order);
                mPlayModeTv.setText(R.string.play_mode_list_order);
                UsbMusicFactory.getUsbPlayerListProxy().setPlayMode(PlayMode.LIST_ORDER);
                XMToast.showToast(mContext, R.string.play_mode_list_order);
                break;
            default:
                break;
        }
    }

    private void setPlayModeIcon(TextView textView, @DrawableRes int resId) {
        Drawable drawable = mContext.getResources().getDrawable(resId, mContext.getTheme());
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        textView.setCompoundDrawables(drawable, null, null, null);
    }

    private OnUsbMusicChangedListener usbListener = new OnUsbMusicChangedListener() {
        @Override
        public void onBuffering(UsbMusic usbMusic) {
            setPlayState(AnimationState.LOADING);
            refreshPlayingItem(usbMusic);
            updateProgress(0L, UsbMusicFactory.getUsbPlayerProxy().getDuration());
        }

        @Override
        public void onPlay(UsbMusic music) {
            setPlayState(AnimationState.PLAY);
            refreshPlayingItem(music);
        }

        @Override
        public void onPause() {
            setPlayState(AnimationState.PAUSE);
        }

        @Override
        public void onProgressChange(long progressInMs, long totalInMs) {

        }

        @Override
        public void onPlayFailed(int errorCode) {
            setPlayState(AnimationState.PAUSE);
        }

        @Override
        public void onPlayStop() {
            setPlayState(AnimationState.PAUSE);
        }

        @Override
        public void onCompletion() {
            setPlayState(AnimationState.PAUSE);
        }

        private void setPlayState(AnimationState state) {
            if (mAdapter != null) {
                mAdapter.setState(state);
            }
        }
    };

    private void refreshPlayingItem(UsbMusic music) {
        ThreadDispatcher.getDispatcher().runOnMain(new Runnable() {
            @Override
            public void run() {
                if (mAdapter != null) {
                    List<UsbMusic> data = mAdapter.getData();
                    final int index = data.indexOf(music);
                    mAdapter.setSelectPosition(index);
                    mRecyclerView.scrollToPosition(index);
                }
            }
        });
    }

    @Override
    public void onPlayModeChanged(int mode) {
        ThreadDispatcher.getDispatcher().postOnMain(new Runnable() {
            @Override
            public void run() {
                changePlayMode(mode);
            }
        });
    }

    private void changePlayMode(int mode) {
        switch (mode) {
            case PlayMode.LIST_ORDER:
                setPlayModeIcon(mPlayModeTv, R.drawable.icon_playlist_order);
                mPlayModeTv.setText(R.string.play_mode_list_order);
                break;
            case PlayMode.LIST_LOOP:
                setPlayModeIcon(mPlayModeTv, R.drawable.icon_playlist_list_loop);
                mPlayModeTv.setText(R.string.play_mode_list_loop);
                break;
            case PlayMode.SINGLE_LOOP:
                setPlayModeIcon(mPlayModeTv, R.drawable.icon_playlist_single);
                mPlayModeTv.setText(R.string.play_mode_single_loop);
                break;
            case PlayMode.RANDOM:
                setPlayModeIcon(mPlayModeTv, R.drawable.icon_playlist_random);
                mPlayModeTv.setText(R.string.play_mode_random);
                break;
            default:
                break;
        }
    }

    @Override
    public void onPlayMusicListChanged(ArrayList<UsbMusic> musicList) {
        setData(musicList);
    }
}

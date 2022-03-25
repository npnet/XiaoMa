package com.xiaoma.music.player.view.player;

import android.annotation.DrawableRes;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.IBinder;
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
import com.xiaoma.music.OnlineMusicFactory;
import com.xiaoma.music.R;
import com.xiaoma.music.common.constant.EventConstants;
import com.xiaoma.music.common.constant.PlayerBroadcast;
import com.xiaoma.music.common.model.PlayStatus;
import com.xiaoma.music.export.manager.AudioShareManager;
import com.xiaoma.music.kuwo.impl.IKuwoConstant;
import com.xiaoma.music.kuwo.listener.OnPlayControlListener;
import com.xiaoma.music.kuwo.model.XMMusic;
import com.xiaoma.music.kuwo.model.XMMusicList;
import com.xiaoma.music.player.adapter.OnlinePlayListAdapter;
import com.xiaoma.music.player.model.AnimationState;
import com.xiaoma.ui.toast.XMToast;
import com.xiaoma.ui.view.XmScrollBar;
import com.xiaoma.utils.PopWindowUtils;

import java.util.List;

import static cn.kuwo.mod.playcontrol.PlayMode.MODE_ALL_CIRCLE;
import static cn.kuwo.mod.playcontrol.PlayMode.MODE_ALL_ORDER;
import static cn.kuwo.mod.playcontrol.PlayMode.MODE_ALL_RANDOM;
import static cn.kuwo.mod.playcontrol.PlayMode.MODE_SINGLE_CIRCLE;

/**
 * @author zs
 * @date 2018/10/12 0012.
 */
public class OnlinePlayListWindow extends PopupWindow implements View.OnClickListener {

    private static final String TAG = "OnlinePlayListWindow";

    private Context mContext;
    private View mView;
    private TextView mPlayModeTv;
    private TextView mListSizeTv;
    private RecyclerView mRecyclerView;
    private OnlinePlayListAdapter mAdapter;
    private LinearLayoutManager mLinearLayoutManager;
    private Window mWindow;
    private XmScrollBar xmScrollBar;
    private IntentFilter mIntentFilter;
    private BroadcastReceiver mReceiver;
    public OnlinePlayListWindow(Activity activity) {
        super(activity);
        mContext = activity.getApplication();
        initView(activity);
    }

    private void initView(Activity activity) {
        mView = View.inflate(activity, R.layout.view_online_pop_window, null);
        setContentView(mView);
        mPlayModeTv = mView.findViewById(R.id.view_pop_play_mode_tv);
        mListSizeTv = mView.findViewById(R.id.view_pop_list_size_tv);
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
        mAdapter = new OnlinePlayListAdapter();
        mRecyclerView.setAdapter(mAdapter);
        xmScrollBar.setRecyclerView(mRecyclerView);
        setAnimationStyle(R.style.popup_window_anim_style);

        initPlayMode();

        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                int index = OnlineMusicFactory.getKWPlayer().getNowPlayMusicIndex();
                int status = OnlineMusicFactory.getKWPlayer().getStatus();
                if (index == position && IKuwoConstant.IAudioStatus.PAUSE == status) {
                    OnlineMusicFactory.getKWPlayer().continuePlay();
                } else if (index != position) {
                    mAdapter.setState(AnimationState.LOADING);
                    XMMusicList nowPlayingList = OnlineMusicFactory.getKWPlayer().getNowPlayingList();
                    if (nowPlayingList != null) {
                        List<XMMusic> xmMusics = nowPlayingList.toList();
                        AudioShareManager.getInstance().shareKwAudioDataSourceChanged();
                        OnlineMusicFactory.getKWPlayer().play(xmMusics, position);
                    }
                }
                mAdapter.setSelectPosition(position);
            }
        });
        mWindow = activity.getWindow();
        initPlayStatus();
        registerPlayModeChanged();
    }
    private void registerPlayModeChanged() {
        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(PlayerBroadcast.Action.ONLINE_PLAY_MODE);
        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                initPlayMode();
            }
        };
        mContext.registerReceiver(mReceiver, mIntentFilter);
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
    }

    private void initPlayStatus() {
        int status = OnlineMusicFactory.getKWPlayer().getStatus();
        if (PlayStatus.PLAYING == status) {
            mAdapter.setState(AnimationState.PLAY);
        } else if (PlayStatus.BUFFERING == status) {
            mAdapter.setState(AnimationState.LOADING);
        } else {
            mAdapter.setState(AnimationState.PAUSE);
        }
    }

    private void initPlayMode() {
        int curPlayMode = OnlineMusicFactory.getKWPlayer().getPlayMode();
        switch (curPlayMode) {
            case MODE_ALL_ORDER:
                setPlayModeIcon(mPlayModeTv, R.drawable.icon_playlist_order);
                mPlayModeTv.setText(R.string.play_mode_list_order);
                break;
            case MODE_ALL_CIRCLE:
                setPlayModeIcon(mPlayModeTv, R.drawable.icon_playlist_list_loop);
                mPlayModeTv.setText(R.string.play_mode_list_loop);
                break;
            case MODE_SINGLE_CIRCLE:
                setPlayModeIcon(mPlayModeTv, R.drawable.icon_playlist_single);
                mPlayModeTv.setText(R.string.play_mode_single_loop);
                break;
            case MODE_ALL_RANDOM:
                setPlayModeIcon(mPlayModeTv, R.drawable.icon_playlist_random);
                mPlayModeTv.setText(R.string.play_mode_random);
                break;
            default:
                break;
        }
    }

    public void setData(List<XMMusic> data) {
        mAdapter.setNewData(data);
        mListSizeTv.setText(String.format("(%s)", data.size()));
        int position = OnlineMusicFactory.getKWPlayer().getNowPlayMusicIndex();
        mAdapter.setSelectPosition(position);
        mRecyclerView.scrollToPosition(position);
    }

    public void setChargeTypes(List<Integer> types) {
        if (mAdapter != null) {
            mAdapter.setChargeType(types);
        }
    }

    public void updateProgress(long curPos) {
        mAdapter.updateProgress(curPos);
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

    public void bgAlpha(float alphaValue) {
        WindowManager.LayoutParams attributes = mWindow.getAttributes();
        attributes.alpha = alphaValue;
        mWindow.setAttributes(attributes);
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
        OnlineMusicFactory.getKWMessage().addPlayStateListener(onlineListener);
    }

    @Override
    public void dismiss() {
        super.dismiss();
        bgAlpha(1.0f);
        OnlineMusicFactory.getKWMessage().removePlayStateListener(onlineListener);
    }

    public void display(boolean isDisplay) {
        if (isDisplay && !isShowing()) {
            bgAlpha(0.3f);
            initPlayStatus();
            int position = mAdapter.getCurPosition();
            if (mAdapter != null && position != -1) {
                refreshPlayingItem(position);
            }
            updateProgress(OnlineMusicFactory.getKWPlayer().getCurrentPos());
            showAtLocation(mView, Gravity.NO_GRAVITY, 0, 0);
        } else if (!isDisplay && isShowing()) {
            dismiss();
        }
    }

    private void switchPlayMode() {
        int curPlayMode = OnlineMusicFactory.getKWPlayer().getPlayMode();
        switch (curPlayMode) {
            case MODE_ALL_ORDER:
                setPlayModeIcon(mPlayModeTv, R.drawable.icon_playlist_list_loop);
                mPlayModeTv.setText(R.string.play_mode_list_loop);
                XMToast.showToast(mContext, R.string.play_mode_list_loop);
                OnlineMusicFactory.getKWPlayer().setPlayMode(MODE_ALL_CIRCLE);
                break;
            case MODE_ALL_CIRCLE:
                setPlayModeIcon(mPlayModeTv, R.drawable.icon_playlist_single);
                mPlayModeTv.setText(R.string.play_mode_single_loop);
                XMToast.showToast(mContext, R.string.play_mode_single_loop);
                OnlineMusicFactory.getKWPlayer().setPlayMode(cn.kuwo.mod.playcontrol.PlayMode.MODE_SINGLE_CIRCLE);
                break;
            case MODE_SINGLE_CIRCLE:
                setPlayModeIcon(mPlayModeTv, R.drawable.icon_playlist_random);
                mPlayModeTv.setText(R.string.play_mode_random);
                XMToast.showToast(mContext, R.string.play_mode_random);
                OnlineMusicFactory.getKWPlayer().setPlayMode(cn.kuwo.mod.playcontrol.PlayMode.MODE_ALL_RANDOM);
                break;
            case MODE_ALL_RANDOM:
                setPlayModeIcon(mPlayModeTv, R.drawable.icon_playlist_order);
                mPlayModeTv.setText(R.string.play_mode_list_order);
                XMToast.showToast(mContext, R.string.play_mode_list_order);
                OnlineMusicFactory.getKWPlayer().setPlayMode(MODE_ALL_ORDER);
                break;
            default:
                break;
        }
    }

    private void setPlayModeIcon(TextView textView, @DrawableRes int resId) {
        Drawable drawable = mContext.getResources().getDrawable(resId);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        textView.setCompoundDrawables(drawable, null, null, null);
    }

    private OnPlayControlListener onlineListener = new OnPlayControlListener() {

        @Override
        public void onReadyPlay(XMMusic music) {
            int position = OnlineMusicFactory.getKWPlayer().getNowPlayMusicIndex();
            refreshPlayingItem(position);
        }

        @Override
        public void onPreStart(XMMusic music) {
            if (mAdapter != null) {
                mAdapter.setState(AnimationState.LOADING);
                XMMusicList nowPlayingList = OnlineMusicFactory.getKWPlayer().getNowPlayingList();
                setData(nowPlayingList.toList());
            }
        }

        @Override
        public void onPlay(XMMusic music) {
            if (mAdapter != null) {
                mAdapter.setState(AnimationState.PLAY);
                XMMusicList nowPlayingList = OnlineMusicFactory.getKWPlayer().getNowPlayingList();
                setData(nowPlayingList.toList());
            }
        }

        @Override
        public void onPlayFailed(int errorCode, String errorMsg) {
            if (mAdapter != null) {
                mAdapter.setState(AnimationState.PAUSE);
            }
        }

        @Override
        public void onPlayStop() {
            if (mAdapter != null) {
                mAdapter.setState(AnimationState.PAUSE);
            }
        }

        @Override
        public void onPause() {
            if (mAdapter != null) {
                mAdapter.setState(AnimationState.PAUSE);
            }
        }

        @Override
        public void onProgressChange(long progressInMs, long totalInMs) {

        }

        @Override
        public void onPlayModeChanged(int playMode) {
            switch (playMode) {
                case MODE_ALL_ORDER:
                    setPlayModeIcon(mPlayModeTv, R.drawable.icon_playlist_order);
                    mPlayModeTv.setText(R.string.play_mode_list_order);
                    break;
                case MODE_ALL_CIRCLE:
                    setPlayModeIcon(mPlayModeTv, R.drawable.icon_playlist_list_loop);
                    mPlayModeTv.setText(R.string.play_mode_list_loop);
                    break;
                case MODE_SINGLE_CIRCLE:
                    setPlayModeIcon(mPlayModeTv, R.drawable.icon_playlist_single);
                    mPlayModeTv.setText(R.string.play_mode_single_loop);
                    break;
                case MODE_ALL_RANDOM:
                    setPlayModeIcon(mPlayModeTv, R.drawable.icon_playlist_random);
                    mPlayModeTv.setText(R.string.play_mode_random);
                    break;
                default:
                    break;
            }
        }
        @Override
        public void onCurrentPlayListChanged() {

        }

        @Override
        public void onBufferStart() {
            if (mAdapter != null) {
                mAdapter.setState(AnimationState.LOADING);
                XMMusicList nowPlayingList = OnlineMusicFactory.getKWPlayer().getNowPlayingList();
                setData(nowPlayingList.toList());
            }
        }

        @Override
        public void onBufferFinish() {

        }

        @Override
        public void onSeekSuccess(int position) {

        }
    };

    private void refreshPlayingItem(int position) {
        if (mAdapter != null) {
            mAdapter.setSelectPosition(position);
        mRecyclerView.scrollToPosition(position);
        }
    }

    public void updatePlaying(){
        if(mAdapter != null){
            mAdapter.updatePlaying();
        }
    }

    public void release() {
        if(mContext==null) return;
        mContext.unregisterReceiver(mReceiver);
    }
}

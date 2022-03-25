package com.xiaoma.xting.player.view;

import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.xiaoma.autotracker.XmAutoTracker;
import com.xiaoma.ui.StateControl.OnRetryClickListener;
import com.xiaoma.ui.StateControl.StateView;
import com.xiaoma.ui.StateControl.Type;
import com.xiaoma.ui.dialog.impl.IOnDialogClickListener;
import com.xiaoma.ui.dialog.impl.XMCompatDialog;
import com.xiaoma.ui.navi.NavigationBar;
import com.xiaoma.ui.view.XmScrollBar;
import com.xiaoma.utils.GsonHelper;
import com.xiaoma.utils.ListUtils;
import com.xiaoma.utils.PopWindowUtils;
import com.xiaoma.xting.R;
import com.xiaoma.xting.common.EventConstants;
import com.xiaoma.xting.common.playerSource.PlayerSourceFacade;
import com.xiaoma.xting.common.playerSource.contract.PlayerAction;
import com.xiaoma.xting.common.playerSource.contract.PlayerLoadMore;
import com.xiaoma.xting.common.playerSource.contract.PlayerSourceSubType;
import com.xiaoma.xting.common.playerSource.contract.PlayerSourceType;
import com.xiaoma.xting.common.playerSource.contract.PlayerStatus;
import com.xiaoma.xting.common.playerSource.info.model.PlayerInfo;
import com.xiaoma.xting.common.playerSource.info.sharedPref.SharedPrefUtils;
import com.xiaoma.xting.common.playerSource.loadmore.IFetchListener;
import com.xiaoma.xting.common.playerSource.loadmore.ILoadMoreListener;
import com.xiaoma.xting.common.playerSource.loadmore.impl.LoadMoreListenerImpl;
import com.xiaoma.xting.common.playerSource.time.ISystemTimeChangeListener;
import com.xiaoma.xting.common.playerSource.time.SystemLivingTimeReceiver;
import com.xiaoma.xting.common.playerSource.utils.PlayerSourceUtils;
import com.xiaoma.xting.common.view.XmDividerDecoration;
import com.xiaoma.xting.common.view.XtingLoadMoreView;
import com.xiaoma.xting.koala.KoalaPlayer;
import com.xiaoma.xting.local.model.BaseChannelBean;
import com.xiaoma.xting.player.adapter.PlayerListAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * <des>
 *
 * @author YangGang
 * @date 2019/5/30
 */
public class PlayerListPopView extends PopupWindow implements PopupWindow.OnDismissListener, View.OnClickListener, OnRetryClickListener, ISystemTimeChangeListener {

    private static final float ALPHA_1 = 1.0f;
    private static final float ALPHA_1_3 = 0.3f;

    private Window mWindow;
    private FragmentActivity mActivity;
    private PlayerListAdapter mAdapter;
    private String[] mPlayModel;

    private TextView mPlayModeTV;
    private StateView mViewState;
    private XmScrollBar mScrollBar;
    private NavigationBar mNaviBar;
    private ImageView mPlayModeIcon;
    private RecyclerView mPlayListRV;

    private @PlayerStatus
    int mPlayerStatus;
    private boolean mHasLoadingF;
    private PlayerInfo mPlayerInfo;
    private LinearLayoutManager mLayoutManger;

    public PlayerListPopView(FragmentActivity activity) {
        this.mActivity = activity;

        preBindView();
        bindView();
        afterBindView();
    }

    public void show(PlayerInfo playerInfo) {
        mPlayerInfo = playerInfo;
        if (!isShowing()) {
            setBgAlpha(ALPHA_1_3);
            showAtLocation(getContentView(), Gravity.START | Gravity.BOTTOM, 0, 0);
            int type = playerInfo.getType();
            if (type == PlayerSourceType.RADIO_YQ
                    || (type == PlayerSourceType.HIMALAYAN
                    && playerInfo.getSourceSubType() == PlayerSourceSubType.RADIO)) {
                setPlayModeEnable(false);
                mPlayModeIcon.setVisibility(View.GONE);
                mPlayModeTV.setText(mActivity.getString(R.string.local_fm_list));
            } else if (type == PlayerSourceType.KOALA && KoalaPlayer.newSingleton().isPGCRadio()) {
                setPlayModeEnable(false);
                mPlayModeIcon.setVisibility(View.GONE);
                mPlayModeTV.setText(mActivity.getString(R.string.guide_play_list));
            } else {
                setPlayModeEnable(true);
                mPlayModeIcon.setVisibility(View.VISIBLE);
                showPlayMode(PlayerSourceFacade.newSingleton().getPlayMode());
            }

            mHasLoadingF = false;
            showPlayList(playerInfo);
        }
    }

    private void loadPlayerList(final PlayerInfo playerInfo) {
        playerInfo.setAction(PlayerAction.SET_LIST);
        PlayerSourceFacade.newSingleton().getPlayerFetch().fetch(playerInfo, new IFetchListener() {
            @Override
            public void onLoading() {
                mViewState.showLoading();
            }

            @Override
            public void onSuccess(Object t) {
                showPlayList(playerInfo);
            }

            @Override
            public void onFail() {
                if (mPlayerInfo.getType() == PlayerSourceType.RADIO_YQ) {
                    ArrayList<PlayerInfo> playerInfoList = new ArrayList<>();
                    playerInfoList.add(mPlayerInfo);
                    mAdapter.setNewData(playerInfoList);
                    mViewState.showContent();
                    mAdapter.notifyPlayerIndex(0);
                } else {
                    mViewState.showEmpty();
                }
            }

            @Override
            public void onError(int code, String msg) {
                if (playerInfo.getType() != PlayerSourceType.RADIO_YQ) {
                    mViewState.showNoNetwork();
                } else {
                    ArrayList<PlayerInfo> playerInfoList = new ArrayList<>();
                    playerInfoList.add(mPlayerInfo);
                    mAdapter.setNewData(playerInfoList);
                    mViewState.showContent();
                    mAdapter.notifyPlayerIndex(0);
                }
            }
        });
    }

    private void showPlayList(PlayerInfo playerInfo) {
        List<PlayerInfo> playerInfoList = PlayerSourceFacade.newSingleton().getPlayerControl().getPlayList();
        if (ListUtils.isEmpty(playerInfoList)) {
            if (!mHasLoadingF) {
                mHasLoadingF = true;
                loadPlayerList(playerInfo);
            } else {
                mViewState.showEmpty();
            }
        } else {
            if (playerInfo.getSourceType() == PlayerSourceType.RADIO_YQ) {
                PlayerInfo playerInfoInPlayer = playerInfoList.get(0);
                int playerInfoType = playerInfo.getType();
                if (playerInfoType != PlayerSourceType.HIMALAYAN
                        || playerInfoInPlayer.getSourceSubType() == PlayerSourceSubType.TRACK) {
                    PlayerSourceFacade.newSingleton().getPlayerControl(playerInfoType).switchPlayerSource(PlayerSourceType.RADIO_YQ);
                    mHasLoadingF = true;
                    loadPlayerList(playerInfo);
                    return;
                }
            }

            mAdapter.setNewData(playerInfoList);
            mViewState.showContent();
            int playIndex = PlayerSourceFacade.newSingleton().getPlayerControl().getPlayIndex();
            mAdapter.notifyPlayerIndex(playIndex);
            if (playIndex < 4) {
                mLayoutManger.scrollToPositionWithOffset(0, 0);
            } else {
                mLayoutManger.scrollToPositionWithOffset(playIndex - 4, 0);
            }
            mAdapter.notifyPlayerStatus(mPlayerStatus);
            loadMore(playerInfo);
        }
    }

    /**
     * 主要用于刷新本地列表
     */
    public void notifyYQPlayList() {
        List<PlayerInfo> playerInfoList = PlayerSourceFacade.newSingleton().getPlayerControl().getPlayList();
        if (ListUtils.isEmpty(playerInfoList)) {
            mViewState.showEmpty();
        } else {
            mAdapter.setNewData(playerInfoList);
            mViewState.showContent();
            int playIndex = PlayerSourceFacade.newSingleton().getPlayerControl().getPlayIndex();
            mAdapter.notifyPlayerIndex(playIndex);
        }
    }

    public void notifyPlayIndex() {
        if (isShowing()) {
            mAdapter.notifyPlayerIndex(PlayerSourceFacade.newSingleton().getPlayerControl().getPlayIndex());
        }
    }

    public void notifyProgress(long progress) {
        if (isShowing()) {
            mAdapter.notifyPlayerProgress(progress);
        }
    }

    public void notifyStatus(@PlayerStatus int status) {
        mPlayerStatus = status;
        if (isShowing()) {
            mAdapter.notifyPlayerStatus(status);
        }
    }

    private ILoadMoreListener mLoadMoreListener = new ILoadMoreListener() {
        @Override
        public void notifyLoadMoreResult(boolean isDownload, int loadMore, List<PlayerInfo> list) {
            if (isDownload) {
                Log.d("Jir", "notifyLoadMoreResult: download ==> " + loadMore);
                if (list != null) {
                    mAdapter.addData(list);
                }
                if (loadMore == PlayerLoadMore.LOAD_COMPLETE) {
                    mAdapter.loadMoreComplete();
                } else if (loadMore == PlayerLoadMore.LOAD_END) {
                    mAdapter.loadMoreEnd(false);
                } else {
                    mAdapter.loadMoreFail();
                }
            } else {
                Log.d("Jir", "notifyLoadMoreResult: upFetch ==> " + loadMore);
                if (list != null) {
                    mAdapter.addData(0, list);
                    mAdapter.notifyPlayerIndex(PlayerSourceFacade.newSingleton().getPlayerControl().getPlayIndex());
                }
                mAdapter.setUpFetching(false);
                if (loadMore == PlayerLoadMore.LOAD_COMPLETE) {
                    mAdapter.loadMoreComplete();
                } else if (loadMore == PlayerLoadMore.LOAD_END) {
                    mAdapter.setUpFetchEnable(false);
                } else {
                    mAdapter.loadMoreFail();
                }
                mAdapter.setEnableLoadMore(true);
            }
            mPlayListRV.setNestedScrollingEnabled(true);
        }
    };

    private void preBindView() {
        mWindow = mActivity.getWindow();
        View rootView = LayoutInflater.from(mActivity).inflate(R.layout.popwindow_play_list, null);
        setContentView(rootView);

        setWidth(mActivity.getResources().getDimensionPixelOffset(R.dimen.width_fm_list_pop));
        setHeight(ConstraintLayout.LayoutParams.MATCH_PARENT);

        this.setFocusable(true);
        this.setTouchable(true);

        mHasLoadingF = false;
        mPlayerStatus = PlayerStatus.PAUSE;
        mPlayModel = mActivity.getResources().getStringArray(R.array.playMode);
    }

    private void bindView() {
        View rootView = getContentView();

        mNaviBar = rootView.findViewById(R.id.naviBar);
        mViewState = rootView.findViewById(R.id.viewState);
        mPlayModeTV = rootView.findViewById(R.id.tvPlayMode);
        mPlayListRV = rootView.findViewById(R.id.rvPlayList);
        mScrollBar = rootView.findViewById(R.id.xmScrollBar);
        mPlayModeIcon = rootView.findViewById(R.id.ivPlayMode);
    }

    private void afterBindView() {
        mNaviBar.showBackNavi();
        PopWindowUtils.fitPopupWindowOverStatusBar(this, true);
        mAdapter = new PlayerListAdapter();
        mLayoutManger = new LinearLayoutManager(mActivity);
        mLayoutManger.setOrientation(LinearLayoutManager.VERTICAL);
        mPlayListRV.setLayoutManager(mLayoutManger);
        mPlayListRV.setAdapter(mAdapter);
        XmDividerDecoration xmDividerDecoration = new XmDividerDecoration(getContentView().getContext(), DividerItemDecoration.VERTICAL);
        xmDividerDecoration.setRect(0, 0, 0, 16);
        mPlayListRV.addItemDecoration(xmDividerDecoration);
        mPlayListRV.setItemAnimator(null);

        mScrollBar.setRecyclerView(mPlayListRV);

        mPlayModeTV.setOnClickListener(this);
        mPlayModeIcon.setOnClickListener(this);

        setOnDismissListener(this);
        mViewState.setOnRetryClickListener(this);

        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                int type = mPlayerInfo.getType();
                if (type == PlayerSourceType.HIMALAYAN || type == PlayerSourceType.KOALA || type == PlayerSourceType.RADIO_XM) {
                    if (PlayerSourceFacade.newSingleton().getPlayerControl().getPlayIndex() == position) {
                        PlayerSourceFacade.newSingleton().getPlayerControl().play();
                    } else {
                        PlayerSourceFacade.newSingleton().getPlayerControl().playWithIndex(position);
                    }
                } else if (type == PlayerSourceType.RADIO_YQ) {
                    final PlayerInfo bean = mAdapter.getItem(position);
                    XMCompatDialog.createMiddleTextDialog()
                            .setMsg(mActivity.getString(R.string.str_radio_to_book, bean.getProgramName(), bean.getExtra1(), bean.getExtra2()))
                            .setLeftClickListener(android.R.string.yes, new IOnDialogClickListener() {
                                @Override
                                public void onDialogClick(View v) {
                                    sendBookBroadcast(bean);
                                }
                            })
                            .setRightClickListener(android.R.string.no, null)
                            .showDialog(mActivity.getSupportFragmentManager());
                }
            }
        });

        setAnimationStyle(R.style.popup_window_anim_style);
    }

    private void setPlayModeEnable(boolean enable) {
        mPlayModeTV.setEnabled(enable);
        mPlayModeIcon.setEnabled(enable);
    }

    private void showPlayMode(int playMode) {
        mPlayModeIcon.setImageLevel(playMode);
        int totalCount = PlayerSourceFacade.newSingleton().getPlayerFetch().getTotalCount();
        if (totalCount == -2) {
            mPlayModeTV.setText(mPlayModel[playMode]);
        } else {
            mPlayModeTV.setText(mActivity.getString(R.string.str_player_mode_and_count,
                    mPlayModel[playMode],
                    totalCount));
        }
    }

    private void setBgAlpha(float alphaValue) {
        WindowManager.LayoutParams attributes = mWindow.getAttributes();
        attributes.alpha = alphaValue;
        mWindow.setAttributes(attributes);
    }

    private void registerListener() {
        if (mPlayerInfo != null
                && (mPlayerInfo.getSourceSubType() == PlayerSourceSubType.SCHEDULE
                || (mPlayerInfo.getSourceType() == PlayerSourceType.RADIO_YQ && mPlayerInfo.getAlbumId() > 0))) {
            SystemLivingTimeReceiver.newSingleton().register(mActivity, this);
        }
    }

    private void loadMore(PlayerInfo info) {
        if (info.getSourceSubType() == PlayerSourceSubType.TRACK
//                || info.getType() == PlayerSourceType.RADIO_XM 这个暂时不支持加载更多
                || info.getType() == PlayerSourceType.KOALA) {
            // showPlayMode(PlayerSourceFacade.newSingleton().getPlayMode());
            LoadMoreListenerImpl.newSingleton().addLoadMoreListener(mLoadMoreListener);
            mAdapter.setEnableLoadMore(true);
            mAdapter.setUpFetchEnable(true);
            mAdapter.setLoadMoreView(new XtingLoadMoreView());
            mAdapter.setStartUpFetchPosition(2);
            mPlayListRV.setNestedScrollingEnabled(true);
            if (info.getSourceSubType() != PlayerSourceSubType.KOALA_PGC_RADIO) {
                mAdapter.setUpFetchListener(new BaseQuickAdapter.UpFetchListener() {
                    @Override
                    public void onUpFetch() {
                        //向上加载
                        if (mPlayListRV.isNestedScrollingEnabled()) {
                            mPlayListRV.setNestedScrollingEnabled(false);
                            mAdapter.setUpFetching(true);
                            PlayerSourceFacade.newSingleton().getPlayerFetch().loadMore(false);
                        }
                    }
                });
            }

            mAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
                @Override
                public void onLoadMoreRequested() {
                    //向下加载
                    if (mPlayListRV.isNestedScrollingEnabled()) {
                        mPlayListRV.setNestedScrollingEnabled(false);
                        PlayerSourceFacade.newSingleton().getPlayerFetch().loadMore(true);
                    }
                }
            }, mPlayListRV);

        } else {
            mAdapter.setEnableLoadMore(false);
            mAdapter.setUpFetchEnable(false);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvPlayMode:
            case R.id.ivPlayMode:
                XmAutoTracker.getInstance().onEvent(EventConstants.NormalClick.ACTION_CHANGE_PLAY_MODE,
                        this.getClass().getName(),
                        EventConstants.PageDescribe.VIEW_NET_POP_LIST);
                showPlayMode(PlayerSourceUtils.switchPlayMode());
                break;
        }
    }

    @Override
    public void onDismiss() {
//        XmAutoTracker.getInstance().onEvent(
//                "--",
//                this.getClass().getName(),
//                EventConstants.NormalClick.ACTION_CLOSE_PLAY_LIST);
        mAdapter.setUpFetchListener(null);
        mAdapter.setOnLoadMoreListener(null, mPlayListRV);
        LoadMoreListenerImpl.newSingleton().removeLoadMoreListener(mLoadMoreListener);
        SystemLivingTimeReceiver.newSingleton().unRegisterReceiver(this);
        setBgAlpha(ALPHA_1);
    }

    private void sendBookBroadcast(PlayerInfo bean) {
        Intent intent = new Intent();
        intent.putExtra("packageName", "com.xiaoma.xting");
        intent.putExtra("className", "com.xiaoma.xting.MainActivity");

        intent.putExtra("startTime", bean.getExtra1());
        intent.putExtra("endTime", bean.getExtra2());

        BaseChannelBean lastYQPlayer = SharedPrefUtils.getLastYQPlayerInfo(true);
        intent.putExtra("info", ((bean.getSourceSubType() == PlayerSourceSubType.YQ_RADIO_FM) ? "0" : "1") + GsonHelper.toJson(lastYQPlayer));

        intent.setAction("xting.local.fm");
        mActivity.sendBroadcast(intent);
    }

    @Override
    public void onRetryClick(View view, Type type) {
        show(mPlayerInfo);
    }

    @Override
    public void onMinuteChanged() {
        if (mPlayerInfo.getSourceType() == PlayerSourceType.RADIO_YQ) {
            notifyPlayIndex();
        }
    }

    @Override
    public void onSystemTimeChanged() {
        Log.d("LPL", "{onSystemTimeChanged}-[] : ");
        showPlayList(mPlayerInfo);
    }

    @Override
    public void onSystemDateChanged() {

    }
}

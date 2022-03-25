package com.xiaoma.xting.mine.ui;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.support.annotation.Nullable;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.xiaoma.autotracker.XmAutoTracker;
import com.xiaoma.component.nodejump.NodeConst;
import com.xiaoma.model.annotation.PageDescComponent;
import com.xiaoma.ui.dialog.impl.IOnDialogClickListener;
import com.xiaoma.ui.dialog.impl.XMCompatDialog;
import com.xiaoma.ui.toast.XMToast;
import com.xiaoma.utils.ListUtils;
import com.xiaoma.utils.NetworkUtils;
import com.xiaoma.xting.R;
import com.xiaoma.xting.common.EventConstants;
import com.xiaoma.xting.common.adapter.EditableAdapter;
import com.xiaoma.xting.common.playerSource.PlayerSourceFacade;
import com.xiaoma.xting.common.playerSource.info.BeanConverter;
import com.xiaoma.xting.common.playerSource.info.impl.PlayerInfoImpl;
import com.xiaoma.xting.common.playerSource.info.model.PlayerInfo;
import com.xiaoma.xting.common.playerSource.info.model.RecordInfo;
import com.xiaoma.xting.launcher.XtingAudioClient;
import com.xiaoma.xting.online.vm.PlayWithPlayerInfoVM;

import java.util.List;

/**
 * @author KY
 * @date 2018/10/11
 */
@PageDescComponent(EventConstants.PageDescribe.FRAGMENT_HISTORY)
public class HistoryFragment extends AbsMineTabFragment<RecordInfo> {

    public static HistoryFragment newInstance() {
        return new HistoryFragment();
    }

    @Override
    protected HistoryAdapter getAdapter() {
        return new HistoryAdapter();
    }

    @Override
    protected void subscriberList() {
        mVM.getRecordsLiveData().observe(this, new Observer<List<RecordInfo>>() {
            @Override
            public void onChanged(@Nullable List<RecordInfo> beans) {
                boolean isEmpty = ListUtils.isEmpty(beans);
                if (checkVisible() && mClearAllVisibleListener != null) {
                    mClearAllVisibleListener.clearAllVisible(!isEmpty);
                }
                if (isEmpty) {
                    showEmptyView();
                } else {
                    showContentView();
                    mAdapter.setNewData(beans);
                }
            }
        });
        final PlayWithPlayerInfoVM playWithPlayerInfoVM = ViewModelProviders.of(this).get(PlayWithPlayerInfoVM.class);
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if (!NetworkUtils.isConnected(getActivity())) {
                    XMToast.showToast(getActivity(), getString(R.string.net_not_connect));
                    return;
                }
                XtingAudioClient.newSingleton(mContext).restoreLauncherCategoryId();
                if (!mAdapter.isOnEdit()) {

                    XtingAudioClient.newSingleton(mContext).restoreLauncherCategoryId();

                    RecordInfo bean = mAdapter.getItem(position);
                    PlayerInfo toPlayerInfo = BeanConverter.toPlayerInfo(bean);
                    PlayerInfo curPlayerInfo = PlayerInfoImpl.newSingleton().getPlayerInfo();

                    if (PlayerSourceFacade.newSingleton().getSourceType() != bean.getType()
                            || !PlayerSourceFacade.newSingleton().getPlayerControl().isCurPlayerInfoAlive()
                            || curPlayerInfo == null
                            || curPlayerInfo.getSourceSubType() != toPlayerInfo.getSourceSubType()) {

                        playWithPlayerInfoVM.play(toPlayerInfo);
                    } else {
                        if (PlayerSourceFacade.newSingleton().getPlayerFetch().isPageInside(toPlayerInfo.getPage())) {
                            List<PlayerInfo> playList = PlayerSourceFacade.newSingleton().getPlayerControl().getPlayList();
                            if (playList != null && playList.size() > 0) {
                                for (int i = 0, n = playList.size(); i < n; i++) {
                                    if (playList.get(i).getProgramId() == toPlayerInfo.getProgramId()) {
                                        mAdapter.startMarquee(view);
                                        PlayerSourceFacade.newSingleton().getPlayerControl().playWithIndex(i);
                                        return;
                                    }
                                }
                            }
                        }
                        playWithPlayerInfoVM.play(toPlayerInfo);
                    }
                }
            }
        });
        mAdapter.setOnEditListener(new EditableAdapter.OnEditListener<RecordInfo>() {
            @Override
            public void onChange(List<RecordInfo> data) {
                if (ListUtils.isEmpty(data)) {
                    showEmptyView();
                }
            }

            @Override
            public void onRemove(RecordInfo data) {
                mVM.delete(data);
                if ((mAdapter != null && mAdapter.getItemCount() == 0)
                        || mVM.isRecordsEmpty()) {
                    showEmptyView();
                    if (mClearAllVisibleListener != null) {
                        mClearAllVisibleListener.clearAllVisible(false);
                    }
                }
            }
        });
        mAdapter.setDraggable(false);
    }

    @Override
    public void clearAllItem() {
        List<RecordInfo> historyBeans = mVM.getRecordsLiveData().getValue();
        if (ListUtils.isEmpty(historyBeans)) {
            return;
        }
        XMCompatDialog.createMiddleTextDialog()
                .setMsg(R.string.confirm_to_clear_history)
                .setLeftClickListener(android.R.string.ok, new IOnDialogClickListener() {
                    @Override
                    public void onDialogClick(View v) {
                        XmAutoTracker.getInstance().onEvent(
                                EventConstants.PageDescribe.FRAGMENT_HISTORY,
                                this.getClass().getName(),
                                EventConstants.NormalClick.ACTION_CONFIRM_CLEAR_HISTORY
                        );
                        mVM.clearAllRecord();
                    }
                })
                .setRightClickListener(android.R.string.cancel, new IOnDialogClickListener() {
                    @Override
                    public void onDialogClick(View v) {
                        XmAutoTracker.getInstance().onEvent(
                                EventConstants.PageDescribe.FRAGMENT_HISTORY,
                                this.getClass().getName(),
                                EventConstants.NormalClick.ACTION_CANCEL_CLEAR_HISTORY
                        );
                    }
                })
                .showDialog(getFragmentManager());
    }

    @Override
    protected void refreshData() {
        mVM.getRecordFromDB();
    }

    private class HistoryAdapter extends EditableAdapter<RecordInfo> {
        HistoryAdapter() {
            super(null);
        }

        @Override
        public void setOnItemClick(View v, int position) {
            if (isOnEdit() && !isLongPress()) {
                setOnEdit(false);
            } else {
                //避免重复点击刷新数据
                RecordInfo item = mAdapter.getItem(position);
                if (PlayerInfoImpl.newSingleton().isThisPlayerInfoPlaying(BeanConverter.toPlayerInfo(item), true)) {
                    refreshMarquee(v);
                    return;
                }
                super.setOnItemClick(v, position);
                refreshMarquee(v);
            }
            updateLongPressd();
        }
    }

    @Override
    public String getThisNode() {
        return NodeConst.Xting.FGT_MY_HISTORY;
    }
}

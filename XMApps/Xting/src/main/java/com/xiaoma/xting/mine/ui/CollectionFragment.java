package com.xiaoma.xting.mine.ui;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.support.annotation.Nullable;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.xiaoma.component.nodejump.NodeConst;
import com.xiaoma.model.annotation.PageDescComponent;
import com.xiaoma.network.callback.SimpleCallback;
import com.xiaoma.thread.ThreadDispatcher;
import com.xiaoma.ui.dialog.impl.IOnDialogClickListener;
import com.xiaoma.ui.dialog.impl.XMCompatDialog;
import com.xiaoma.ui.toast.XMToast;
import com.xiaoma.utils.ListUtils;
import com.xiaoma.utils.NetworkUtils;
import com.xiaoma.xting.R;
import com.xiaoma.xting.common.EventConstants;
import com.xiaoma.xting.common.XtingUtils;
import com.xiaoma.xting.common.adapter.EditableAdapter;
import com.xiaoma.xting.common.playerSource.PlayerSourceFacade;
import com.xiaoma.xting.common.playerSource.contract.PlayerSourceType;
import com.xiaoma.xting.common.playerSource.info.BeanConverter;
import com.xiaoma.xting.common.playerSource.info.IPlayerInfo;
import com.xiaoma.xting.common.playerSource.info.impl.PlayerInfoImpl;
import com.xiaoma.xting.common.playerSource.info.model.PlayerInfo;
import com.xiaoma.xting.common.playerSource.info.model.RecordInfo;
import com.xiaoma.xting.common.playerSource.info.model.SubscribeInfo;
import com.xiaoma.xting.launcher.LocalFMOperateManager;
import com.xiaoma.xting.launcher.XtingAudioClient;
import com.xiaoma.xting.local.model.BaseChannelBean;
import com.xiaoma.xting.online.vm.PlayWithPlayerInfoVM;
import com.xiaoma.xting.sdk.LocalFMFactory;

import java.util.List;

/**
 * @author KY
 * @date 2018/10/12
 */
@PageDescComponent(EventConstants.PageDescribe.FRAGMENT_COLLECT)
public class CollectionFragment extends AbsMineTabFragment<SubscribeInfo> {

    public static CollectionFragment newInstance() {
        return new CollectionFragment();
    }

    @Override
    protected CollectAdapter getAdapter() {
        return new CollectAdapter();
    }

    private Observer<List<SubscribeInfo>> observer = new Observer<List<SubscribeInfo>>() {
        @Override
        public void onChanged(@Nullable List<SubscribeInfo> beans) {
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
    };

    @Override
    public void clearAllItem() {
        List<SubscribeInfo> collectBeans = mVM.getSubscribesLiveData().getValue();
        if (ListUtils.isEmpty(collectBeans)) {
            return;
        }
        XMCompatDialog.createMiddleTextDialog()
                .setMsg(R.string.confirm_to_clear_collect)
                .setLeftClickListener(android.R.string.ok, new IOnDialogClickListener() {
                    @Override
                    public void onDialogClick(View v) {
                        mVM.clearAllSubscribe();
                    }
                })
                .setRightClickListener(android.R.string.cancel, null)
                .showDialog(getFragmentManager());
    }

    @Override
    protected void subscriberList() {
        PlayerInfoImpl.newSingleton().registerPlayerInfoListener(new IPlayerInfo() {
            @Override
            public void onPlayerInfoChanged(PlayerInfo playerInfo) {

            }

            @Override
            public void onPlayerStatusChanged(int status) {

            }

            @Override
            public void onPlayerProgress(long progress, long duration) {

            }

            @Override
            public void onProgramSubscribeChanged(boolean subscribe) {
                if (!mAdapter.isOnEdit()) {
                    refreshData();
                }
            }
        });
        mVM.getSubscribesLiveData().observeForever(observer);
        final PlayWithPlayerInfoVM playerInfoVM = ViewModelProviders.of(this).get(PlayWithPlayerInfoVM.class);
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if (!mAdapter.isOnEdit()) {
                    XtingAudioClient.newSingleton(mContext).restoreLauncherCategoryId();
                    SubscribeInfo bean = mAdapter.getItem(position);
                    if (bean == null) return;
                    if (bean.getType() != PlayerSourceType.RADIO_YQ) {
                        if (!NetworkUtils.isConnected(getActivity())) {
                            XMToast.showToast(getActivity(), getString(R.string.net_not_connect));
                            return;
                        }
                        XtingAudioClient.newSingleton(mContext).restoreLauncherCategoryId();
                        mAdapter.startMarquee(view);

                        PlayerInfo curPlayerInfo = PlayerInfoImpl.newSingleton().getPlayerInfo();
                        if (BeanConverter.toPlayerInfo(bean).equals(curPlayerInfo)) {
                            if (PlayerSourceFacade.newSingleton().getPlayerControl().isCurPlayerInfoAlive()) {
                                PlayerSourceFacade.newSingleton().getPlayerControl().play();
                            } else {
                                playerInfoVM.play(curPlayerInfo);
                            }
                        } else {
                            List<RecordInfo> recordInfos = XtingUtils.getRecordDao().selectProgramByAlbum(bean.getType(), bean.getAlbumId());
                            if (ListUtils.isEmpty(recordInfos)) {
                                playerInfoVM.play(BeanConverter.toPlayerInfo(bean));
                            } else {
                                playerInfoVM.play(BeanConverter.toPlayerInfo(recordInfos.get(0)));
                            }
                        }
                    } else {
                        if (!LocalFMFactory.getSDK().isRadioOpen()) {
                            LocalFMFactory.getSDK().openRadio();
                        }
                        long channelValue = bean.getAlbumId();
                        LocalFMOperateManager.newSingleton().playChannel((int) channelValue);
                        refreshInfo(bean);
                    }
                }

            }
        });
        mAdapter.setOnEditListener(new EditableAdapter.OnEditListener<SubscribeInfo>() {
            @Override
            public void onChange(List<SubscribeInfo> data) {
                if (ListUtils.isEmpty(data)) {
                    showEmptyView();
                }
            }

            @Override
            public void onRemove(SubscribeInfo data) {
                mVM.delete(data);
                if ((mAdapter != null && mAdapter.getItemCount() == 0)
                        || mVM.isSubscribesEmpty()) {
                    showEmptyView();
                    if (mClearAllVisibleListener != null) {
                        mClearAllVisibleListener.clearAllVisible(false);
                    }
                }
                PlayerInfo playerInfo = PlayerInfoImpl.newSingleton().getPlayerInfo();
                if (BeanConverter.toPlayerInfo(data).equals(playerInfo)) {
                    PlayerInfoImpl.newSingleton().onProgramSubscribeChanged(false);
                }
            }
        });
        mAdapter.setDraggable(false);
        mVM.getSubscribeFromDB();
    }

    private void refreshInfo(final SubscribeInfo bean) {
        ThreadDispatcher.getDispatcher().postLowPriority(new Runnable() {
            @Override
            public void run() {
                BaseChannelBean channelBean = XtingUtils.getBaseChannelByValue((int) bean.getAlbumId());
                LocalFMOperateManager.newSingleton().getRadioInfo(channelBean, new SimpleCallback<BaseChannelBean>() {
                    @Override
                    public void onSuccess(BaseChannelBean model) {
                        bean.setImgUrl(model.getCoverUrl());
                        XtingUtils.getSubscribeDao().insert(bean);
                        if (!isVisible()) return;
                        ThreadDispatcher.getDispatcher().postOnMain(new Runnable() {
                            @Override
                            public void run() {
                                refreshData();
                            }
                        });
                    }

                    @Override
                    public void onError(int code, String msg) {

                    }
                });
            }
        });
    }

    @Override
    public void refreshData() {
        mVM.getSubscribeFromDB();
    }

    @Override
    public void onDestroy() {
        mVM.getSubscribesLiveData().removeObserver(observer);
        super.onDestroy();
    }

    @Override
    public String getThisNode() {
        return NodeConst.Xting.FGT_MY_COLLECT;
    }

    private class CollectAdapter extends EditableAdapter<SubscribeInfo> {

        CollectAdapter() {
            super(null);
        }

        @Override
        public void setOnItemClick(View v, int position) {
            if (isOnEdit() && !isLongPress()) {
                setOnEdit(false);
            } else {
                //避免重复点击刷新数据
                if (PlayerInfoImpl.newSingleton().isThisPlayerInfoPlaying(BeanConverter.toPlayerInfo(mAdapter.getItem(position)), false)) {
                    mAdapter.startMarquee(v);
                    return;
                }
                super.setOnItemClick(v, position);
                mAdapter.startMarquee(v);
            }
            updateLongPressd();
        }
    }
}
